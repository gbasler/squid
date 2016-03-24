package scp.lang

import annotation.StaticAnnotation
import scala.reflect.runtime.{universe => ru}

/** Type class to describe scopes */
//trait Scope[-S]

/*
    Do we really need the type parameters in operations?
    they are no more inferred corectly; cf: dslMethodApp[Nothing,Nothing]
    
    TODO add all necessary requirements in Base so QQ macros go well (Rep extract, subs, etc.)
    
 */
/** Main language trait, encoding second order lambda calculus with records, let-bindings and ADTs */
trait Base extends BaseDefs { base =>
  
  //type Rep[Typ, -Scp]
  //type TypeRep[A]
  type Rep
  type TypeRep
  
  def const[A: TypeEv](value: A): Rep
  def abs[A: TypeEv, B: TypeEv](name: String, fun: Rep => Rep): Rep
  def app[A: TypeEv, B: TypeEv](fun: Rep, arg: Rep): Rep
  
  //def dslMethodApp[A,S](self: Option[SomeRep], mtd: DSLDef, targs: List[SomeTypeRep], args: List[List[SomeRep]], tp: TypeRep[A], run: Any): Rep[A,S]
  def dslMethodApp(self: Option[Rep], mtd: DSLSymbol, targs: List[TypeRep], argss: List[List[Rep]], tp: TypeRep): Rep
  
  //import scala.reflect.{runtime => srr}
  
  ////type DSLSymbol
  //type DSLSymbol = srr.universe.MethodSymbol
  ////def loadSymbol(fullName: String, info: String, module: Boolean): DSLSymbol
  //def loadSymbol(sym: srr.universe.MethodSymbol): DSLSymbol
  type DSLSymbol
  def loadSymbol(typ: ru.Type, symName: String, erasure: String): DSLSymbol
  
  
  def repEq(a: Rep, b: Rep): Boolean
  def typEq(a: TypeRep, b: TypeRep): Boolean
  
  //implicit def funType[A: TypeEv, B: TypeEv]: TypeEv[A => B]
  
  
  trait ConstAPI {
    def unapply[A: ru.TypeTag, S](x: Q[A,S]): Option[A]
  }
  //val Const: ConstAPI // TODO
  val ConstQ: ConstAPI
  
  
  //def transform(r: Rep)(f: PartialFunction[Rep, Rep]): Rep
  def transform(r: Rep)(f: Rep => Rep): Rep
  
  def typ(r: Rep): TypeRep
  
  def extract(xtor: Rep, t: Rep): Option[Extract]
  
  protected def runRep(r: Rep): Any
  
  
  /// EXT
  
  def hole[A: TypeEv](name: String): Rep
  
  def typeHole[A](name: String): TypeRep
  
  
}

class BaseDefs { base: Base =>
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Provided Definitions:
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  type SomeRep = Rep
  type SomeQ = Q[_,_]
  
  
  //sealed case class Quoted[+Typ, -Scp](rep: Rep)(implicit val typ: TypeEv[Typ])//, val scp: Scope[Scp])
  sealed case class Quoted[+Typ: TypeEv, -Scp](rep: Rep) {
    val tpr = typeRepOf[Typ]
    
    type Type <: Typ
    
    import scala.language.experimental.macros
    def subs[T,C](s: (Symbol, Quoted[T,C])): Any = macro Base.subsImpl[T,C]
    
    //abstract class Rewrite[A] {
    //  def apply[S >: Scp](q: Q[A,S]): Q[A,S] // Error:(53, 17) contravariant type Scp occurs in covariant position in type  >: Scp of type S
    //}
    
    def =~= (that: Quoted[_,_]): Boolean = rep =~= that.rep
    
    def run(implicit ev: {} <:< Scp): Typ = runUnsafe
    def runUnsafe: Typ = runRep(rep).asInstanceOf[Typ]
    //def run(p:Int): Typ = { ???; "lol".asInstanceOf[Typ] }
    
    override def toString = s"""dsl"$rep""""
  }
  type Q[+T,-S] = Quoted[T, S] // shortcut
  
  sealed case class QuotedType[+Typ](rep: TypeRep)
  type QT[+T] = QuotedType[T] // shortcut
  
  sealed class QuotedDepType[+Typ, -Scp](rep: TypeRep)/*(implicit val scp: Scope[Scp])*/ extends QuotedType[Typ](rep)
  type QDT[+T,-S] = QuotedDepType[T, S] // shortcut
  
  
  @annotation.implicitNotFound(msg = "Could not find type representation evidence for ${A} (implicit of type TypeEv[${A}])")
  final case class TypeEv[A](rep: TypeRep)
  
  def typeEv[A: TypeEv] = implicitly[TypeEv[A]]
  def typeRepOf[A: TypeEv]: TypeRep = typeEv[A].rep
  
  
  def letin[A: TypeEv, B: TypeEv](name: String, value: Rep, body: Rep => Rep): Rep = app[A,B](abs[A,B](name, body), value)
  def ascribe[A: TypeEv](value: Rep): Rep = value // FIXME don't all IRs need to override it to have sound match checking?
  
  
  //def open(name: String): Nothing = ???
  
  //def splice[A: Lift](x: A): A = ??? // TODO better error
  def splice[A](x: A): A = ??? // TODO better error // FIXME require Lift..?
  
  //def splice[A,S:Scope](x: Q[A,S]): A = ??? // TODO better error
  def splice[A,S](x: Q[A,S]): A = ??? // TODO better error
  //implicit def spliceDeep[A: Lift](x: A): Rep[A] = implicitly[Lift[A]].apply(x)
  //implicit def spliceDeep[A](x: Rep[A]): Rep[A] = x
  implicit def spliceDeep(x: Rep): Rep = x
  
  trait Lift[A] { def apply(x: A): Rep }
  implicit def liftConst[A: TypeEv] = new Lift[A] { def apply(x: A) = const(x) }
  
  
  implicit class RepHelp(private val self: Rep) { // extends AnyVal { // Error: value class may not be a member of another class
    def =~= (that: Rep): Boolean = repEq(self, that)
  }
  implicit class TypeRepHelp(private val self: TypeRep) { // extends AnyVal { // Error: value class may not be a member of another class
    def =:= (that: TypeRep): Boolean = typEq(self, that)
  }
  
  
  
  /// EXT
  
  type Extract = (Map[String, SomeRep], Map[String, TypeRep])
  val EmptyExtract: Extract = Map() -> Map()
  
  
  protected def merge(a: Extract, b: Extract): Option[Extract] = {
    val vals = a._1 ++ b._1.map {
      case (name, v) if a._1 isDefinedAt name =>
        //println(s"Duplicates for $name:\n\t$v\n\t${a._1(name)}")
        if (a._1(name) =~= v) (name -> v)
        else return None
      case (name, v) => (name -> v)
    }
    val typs = a._2 ++ b._2.map {
      case (name, t) if a._2 isDefinedAt name =>
        if (a._2(name) =:= t) (name -> t)
        else return None
      case (name, t) => (name -> t)
    }
    Some(vals, typs)
  }
  
  
  
  
  
  
  //type `type scala.Function1` // TODO possibility not to mix it in
  //def `def scala.Function1.apply::(v1: T1)R`[A: TypeEv, B: TypeEv](self: Rep[A -> B])(arg: Rep[A]) = app(self, arg)
  
  
  
  implicit class QuotedOps[Typ,Scp](self: Q[Typ,Scp]) {
    abstract class Rewrite[A] {
      //def apply[S >: Scp](sub: Q[A,S]): Option[Q[A,S]]
      def apply[S >: Scp]: PartialFunction[Q[A,S], Q[A,S]]
      //def apply(): Q[Typ,Scp] = ???
      
      //private val rewrites = collection.mutable.Buffer[PartialFunction[Q[Typ,_],Q[Typ,_]]]
      //
      //def rewrite[S]
      //
      //lazy val pf = 
      
    }
    def transform(rewrites: Rewrite[_]*): Q[Typ,Scp] = {
      val r = base.transform(self.rep) {
        (r: Rep) =>
          val qr = Quoted(r)(TypeEv(typ(r)))
          println("R "+rewrites.head.apply[Any].isDefinedAt(qr))
          //println("A "+rewrites.head.apply[Any](qr))
          rewrites collectFirst {
            case rewrite if rewrite.apply[Any].isDefinedAt(qr) => rewrite.apply[Any](qr).rep
          } getOrElse r
      }
      Quoted(r)(TypeEv(typ(r)))
    }
    
    //def transform(rewrites: Rewrite[_]*): Q[Typ,Scp] = {
    //  val stream = Stream(rewrites: _*)
    //  transform(self.rep,
    //    (r: Rep) => rewrites collectFirst { case rewrite if rewrite.apply[] })
    //}
  }
  
  
  class Transformer {
    
    abstract class RewriteRulePoly {
      type Ctx
      //type From
      //type To
      //val ev: From =:= To
      type Typ[A]
      def apply[A]: PartialFunction[Q[Typ[A],Ctx], Q[Typ[A],Ctx]]
    }
    
    abstract class RewriteRule[A] {
      type Ctx
      def apply: PartialFunction[Q[A,Ctx], Q[A,Ctx]]
      def rewrite[From,To](f: PartialFunction[Q[From,Ctx], Q[To,Ctx]])(implicit ev: From =:= To) = ???
    }
    
    ////////////////////
    
    abstract class Rewrite[A] {
      def apply[Ctx]: PartialFunction[Q[A,Ctx], Q[A,Ctx]]
    }
    abstract class RewriteForall[A[_]] {
      def apply[Ctx, X: TypeEv]: PartialFunction[Q[A[X],Ctx], Q[A[X],Ctx]]
    }
    abstract class RewriteForallForall[A[_, _]] {
      //import Base.Param
      import reflect.runtime.universe.TypeTag
      //def apply[Ctx, X, Y]: PartialFunction[Q[A[X, Y],Ctx], Q[A[X, Y],Ctx]]
      //def apply[Ctx, X <: Param, Y <: Param]: PartialFunction[Q[A[X, Y],Ctx], Q[A[X, Y],Ctx]]
      def apply[Ctx, X: TypeEv, Y: TypeEv]: PartialFunction[Q[A[X, Y],Ctx], Q[A[X, Y],Ctx]]
    }
    
    /** Using special cases for different variances does not actually seem necessary!
      * Indeed, at the time of writing def apply ..., we'll KNOW what the alias stands for, and the original variances will be used! */
    /*
    abstract class Rewrite_=[A[_]] {
      def apply[Ctx, X]: PartialFunction[Q[A[X],Ctx], Q[A[X],Ctx]]
    }
    abstract class Rewrite_+[A[+_]] {
      def apply[Ctx, X]: PartialFunction[Q[A[X],Ctx], Q[A[X],Ctx]]
    }
    abstract class Rewrite_-[A[-_]] {
      def apply[Ctx, X]: PartialFunction[Q[A[X],Ctx], Q[A[X],Ctx]]
    }
    
    abstract class Rewrite_==[A[_,_]] {
      def apply[Ctx, X, Y]: PartialFunction[Q[A[X, Y],Ctx], Q[A[X, Y],Ctx]]
    }
    abstract class Rewrite_+=[A[+_,_]] {
      def apply[Ctx, X, Y]: PartialFunction[Q[A[X, Y],Ctx], Q[A[X, Y],Ctx]]
    }
    abstract class Rewrite_=+[A[_,+_]] {
      def apply[Ctx, X, Y]: PartialFunction[Q[A[X, Y],Ctx], Q[A[X, Y],Ctx]]
    }
    abstract class Rewrite_++[A[+_,+_]] {
      def apply[Ctx, X, Y]: PartialFunction[Q[A[X, Y],Ctx], Q[A[X, Y],Ctx]]
    }
    // etc...
    */
    
    
    //object rewrite {
    //  //def += [A,S] (f: PartialFunction[Q[A,S], Q[A,S]]) = ???
    //  //def += [A,S] (f: PartialFunction[Q[Int,{}], Q[A,S]]) = ???
    //  def += [A,S] (f: Q[A,S] => Q[A,S]) = ???
    //  //def += (f: Q[Int,{}] => Q[Int,{}]) = ???
    //  //def += (f: ((Q[A,S] => Q[A,S]) forSome {type A; type S})) = ???
    //  //def += (f: Q[_,_] => Q[_,_]) = ???
    //}
    //def rew[T] = new {
    //  def += [A<:T] (f: Q[A,{}] => Q[A,{}]) = ???
    //}
    //abstract class Rewrite {
    //  def apply[A,S](q: Q[A,S]): Q[A,S]
    //}
    
    /*
    abstract class Rewrite[A] {
      def apply[S](q: Q[A,S]): Q[A,S]
    }
    */
  }
  
  import scala.language.experimental.macros
  def iso(qf: Q[_,_] => Q[_,_]): Any = macro Base.isoImpl
  
  def iso2[A,S](qf: Q[A,S] => Q[A,S]): Any = ???
  
  //def iso3[A](qf: (Q[A,s] => Q[A,s]) forSome {type s}): Any = ??? // nope
  
  
  
}

object Base {
  import reflect.macros.whitebox.Context
  def isoImpl(c: Context)(qf: c.Tree) = {
    import c.universe._
    println(qf+" : "+qf.tpe)
    //qf.tpe.foreach{ case tp => println("Sub "+tp) }
    qf.tpe match {
      case TypeRef(_, _, a :: b :: Nil) =>
        println(a,b)
        val et1 = a match {
          //case TypeRef(_, _, t :: s :: Nil) =>
          //  println(t,s)
          case ExistentialType(syms, tp) =>
            println(syms,tp)
            syms.tail.head
        }
        val et2 = b match {
          case ExistentialType(syms, tp) =>
            println(syms,tp)
            syms.head
        }
        println(et1.typeSignature =:= et2.typeSignature)
        ???
    }
    qf
  }
  
  trait HoleType
  
  
  def subsImpl[T: c.WeakTypeTag, C: c.WeakTypeTag](c: Context)(s: c.Tree) = { // TODO use C to check context!!
    import c.universe._
    
    val T = weakTypeOf[T]
    val C = weakTypeOf[C]
    
    print()
    
    //println(c.macroApplication, weakTypeOf[T], weakTypeOf[C])
    val quoted = c.macroApplication match {
      case q"$q.subs[$_,$_]($_)" => q
    }
    println(quoted)
    val (name, term) = s match {
      case q"scala.this.Predef.ArrowAssoc[$_]($name).->[$_]($term)" =>
        (name match {
          case q"scala.Symbol.apply(${Literal(Constant(str: String))})" => str
          case _ => c.abort(c.enclosingPosition, "Nope name") // TODO BE
        }) -> term
    }
    println(name, term)
    //q"""transform($quoted.rep){
    //  case 
    //  case r => r
    //}"""
    //q"val q = $quoted; Quoted[q.Type,$C](q.rep.subs($name -> $term.rep))"
    val (typ,ctx) = quoted.tpe.widen match {
      case TypeRef(_, _, typ :: ctx:: Nil) => typ -> ctx
      // TODO handle annoying types
    }
    q"Quoted[$typ,$C]($quoted.rep.subs($name -> $term.rep))"
    // FIXME context should actually use q.tpe's Ctx
  }
  
  // TODO rm:
  trait ParamType[T] // used in SimpleEmbedding, for type-safe program transfo
  //class opaque extends StaticAnnotation
  trait Param
  
}









