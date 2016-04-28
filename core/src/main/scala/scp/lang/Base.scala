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
  
  def moduleObject(fullName: String, tp: TypeRep): Rep
  def methodApp(self: Rep, mtd: DSLSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep
  
  type DSLSymbol
  def loadSymbol(mod: Boolean, typ: String, symName: String): DSLSymbol
  def loadOverloadedSymbol(mod: Boolean, typ: String, symName: String, index: Int): DSLSymbol
  
  
  def repEq(a: Rep, b: Rep): Boolean
  def typEq(a: TypeRep, b: TypeRep): Boolean
  
  //implicit def funType[A: TypeEv, B: TypeEv]: TypeEv[A => B]
  def funType(a: TypeRep, b: TypeRep): TypeRep
  def unitType: TypeRep
  
  trait ConstAPI {
    def unapply[A: ru.TypeTag, S](x: Q[A,S]): Option[A]
  }
  //val Const: ConstAPI // TODO
  val ConstQ: ConstAPI
  
  
  //def transform(r: Rep)(f: PartialFunction[Rep, Rep]): Rep
  def transform(r: Rep)(f: Rep => Rep): Rep
  
  def typ(r: Rep): TypeRep
  
  def extract(xtor: Rep, t: Rep): Option[Extract]
  def spliceExtract(xtor: Rep, t: Args): Option[Extract]
  
  protected def runRep(r: Rep): Any
  
  
  /// EXT
  
  def hole[A: TypeEv](name: String): Rep
  def flatHole[A: TypeEv](name: String): Rep
  
  def typeHole[A](name: String): TypeRep
  
  
}

class BaseDefs { base: Base =>
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Provided Definitions:
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  type SomeRep = Rep
  type SomeQ = Q[_,_]
  
  
  sealed case class Quoted[+Typ, -Scp](rep: Rep) {
    def typ = QuotedType[Typ](trep)
    def trep = base.typ(rep)
    
    type Type <: Typ
    
    import scala.language.experimental.macros
    def subs[T,C](s: (Symbol, Quoted[T,C])): Any = macro Base.subsImpl[T,C]
    
    //abstract class Rewrite[A] {
    //  def apply[S >: Scp](q: Q[A,S]): Q[A,S] // Error:(53, 17) contravariant type Scp occurs in covariant position in type  >: Scp of type S
    //}
    
    def =~= (that: Quoted[_,_]): Boolean = rep =~= that.rep
    
    def run(implicit ev: {} <:< Scp): Typ = runUnsafe
    lazy val runUnsafe: Typ = runRep(rep).asInstanceOf[Typ]
    
    def cast[T >: Typ]: Quoted[T, Scp] = this
    def erase: Quoted[Any, Scp] = this
    
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
  def thunk[A: TypeEv](value: => Rep): Rep = abs[Unit, A]("thunk", (_: Rep) => value)(TypeEv(unitType), TypeEv(typeRepOf[A]))
  
  
  sealed trait ArgList {
    def reps: Seq[Rep]
    def extract(al: ArgList): Option[Extract] = (this, al) match {
      case (a0: Args, a1: Args) => a0 extract a1
      case (ArgsVarargs(a0, va0), ArgsVarargs(a1, va1)) => for {
        a <- a0 extract a1
        va <- va0 extract va1
        m <- merge(a, va)
      } yield m
      case (ArgsVarargSpliced(a0, va0), ArgsVarargSpliced(a1, va1)) => for {
        a <- a0 extract a1
        va <- base.extract(va0, va1)
        m <- merge(a, va)
      } yield m
      case (ArgsVarargSpliced(a0, va0), ArgsVarargs(a1, vas1)) => for { // case dsl"List($xs*)" can extract dsl"List(1,2,3)"
        a <- a0 extract a1
        va <- base.spliceExtract(va0, vas1)
        m <- merge(a, va)
      } yield m
      case _ => None
    }
    override def toString = (this match {
      case Args(as @ _*) => as
      case ArgsVarargs(as, vas) => as.reps ++ vas.reps
      case ArgsVarargSpliced(as, va) => as.reps.map(_.toString) :+ s"$va: _*"
    }) mkString ("(",",",")")
  }
  case class Args(reps: Rep*) extends ArgList {
    def apply(vreps: Rep*) = ArgsVarargs(this, Args(vreps: _*))
    def splice(vrep: Rep) = ArgsVarargSpliced(this, vrep)
    
    def extract(that: Args): Option[Extract] = {
      require(reps.size == that.reps.size)
      val args = (reps zip that.reps) map { case (a,b) => base.extract(a, b) }
      (Some(EmptyExtract) +: args).reduce[Option[Extract]] { // `reduce` on non-empty; cf. `Some(EmptyExtract)`
        case (acc, a) => for (acc <- acc; a <- a; m <- merge(acc, a)) yield m }
    }
  }
  object ArgList {
    def unapplySeq(x: ArgList) = x match {
      case Args(as @ _*) => Some(as)
      case ArgsVarargs(as, vas) => Some(as.reps ++ vas.reps)
      case ArgsVarargSpliced(_, _) => None
    }
  }
  case class ArgsVarargs(args: Args, varargs: Args) extends ArgList {
    val reps = args.reps ++ varargs.reps
  }
  //object Varargs {
  //  def unapplySeq(x: ArgList) = x match {
  //    case ArgsVarargs(as, vas) => Some(as.reps, vas.reps)
  //    case _ => None
  //  }
  //}
  case class ArgsVarargSpliced(args: Args, vararg: Rep) extends ArgList {
    val reps = args.reps :+ vararg
  }
  
  //def open(name: String): Nothing = ???
  
  //def splice[A: Lift](x: A): A = ??? // TODO better error
  def splice[A](x: A): A = ??? // TODO better error // FIXME require Lift..?
  
  //def splice[A,S:Scope](x: Q[A,S]): A = ??? // TODO better error
  def splice[A,S](x: Q[A,S]): A = ??? // TODO better error
  def spliceVararg[A,S](x: Seq[Q[A,S]]): Seq[A] = ??? // TODO better error
  //def spliceVarargs[A,S](xs: Q[A,S]*): Seq[A] = ???
  
  /** Used for construction syntax {{{dsl"Seq(${xs: __*})"}}}, mirror of the extraction (where '_*' does not work) */
  type __* = Seq[_]
  
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
  
  /** Artifact of a term extraction: map from hole name to terms, types and flattened term lists */
  type Extract = (Map[String, Rep], Map[String, TypeRep], Map[String, Seq[Rep]])
  val EmptyExtract: Extract = (Map(), Map(), Map())
  
  
  protected def merge(a: Extract, b: Extract): Option[Extract] = {
    b._1 foreach { case (name, vb) => (a._1 get name) foreach { va => if (!(va =~= vb)) return None } }
    b._3 foreach { case (name, vb) => (a._3 get name) foreach { va =>
      if (va.size != vb.size || !(va zip vb forall {case (vva,vvb) => vva =~= vvb})) return None } }
    val typs = a._2 ++ b._2.map {
      case (name, t) =>
        // Note: We could do better than =:= and infer LUB/GLB, but that would probably mean embedding the variance with the type...
        if (a._2 get name forall (_ =:= t)) name -> t 
        else return None
    }
    val flatVals = a._3 ++ b._3
    val vals = a._1 ++ b._1
    Some(vals, typs, flatVals)
  }
  protected def mergeAll(as: TraversableOnce[Option[Extract]]): Option[Extract] = {
    if (as isEmpty) return Some(EmptyExtract)
    as.reduce[Option[Extract]] { case (acc, a) => for (acc <- acc; a <- a; m <- merge(acc, a)) yield m }
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
          val qr = Quoted(r)
          println("R "+rewrites.head.apply[Any].isDefinedAt(qr))
          //println("A "+rewrites.head.apply[Any](qr))
          rewrites collectFirst {
            case rewrite if rewrite.apply[Any].isDefinedAt(qr) => rewrite.apply[Any](qr).rep
          } getOrElse r
      }
      Quoted(r)
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
  
  
  
  // PRIVATE
  
  def `private checkExtract`(position: String, maps: Extract)(valKeys: String*)(typKeys: String*)(flatValKeys: String*): Extract = {
    val prnt = (s: Traversable[_]) => s mkString ("{", ",", "}")
    //def keySets = s"{ ${valKeys.toSet}; ${typKeys.toSet}; ${flatValKeys.toSet} }" // Scala bug java.lang.VerifyError: Bad type on operand stack
    val keySets = () => s"( ${prnt(valKeys)}; ${prnt(typKeys)}; ${prnt(flatValKeys)} )"
    
    assert(maps._1.keySet == valKeys.toSet, "Extracted value keys "+prnt(maps._1.keySet)+" do not correspond to specified keys "+keySets())//+valKeys.toSet)
    assert(maps._3.keySet == flatValKeys.toSet, "Extracted flattened value keys "+prnt(maps._3.keySet)+" do not correspond to specified keys "+keySets())//+flatValKeys.toSet)
    //assert(maps._2.keySet == typKeys.toSet, "Extracted type keys "+maps._2.keySet+" do not correspond to specified keys "+typKeys)
    val xkeys = maps._2.keySet
    val keys = typKeys.toSet
    assert(xkeys -- keys isEmpty, "Unexpected extracted type keys "+(xkeys -- keys)+", not in specified keys "+keySets())//+keys)
    val noExtr = keys -- xkeys
    val plur = "s" * (noExtr.size - 1)
    if (noExtr nonEmpty) System.err.print( // not 'println' since the position String contains a newLine
      s"""Warning: no type representations were extracted for type hole$plur: ${prnt(noExtr map ("$"+_))}
         |  Perhaps the type hole$plur ${if (plur isEmpty) "is" else "are"} in the position of an unconstrained GADT type parameter where the GADT is matched contravariantly...
         |${position}""".stripMargin)
    ( maps._1, (maps._2 ++ (noExtr map (k => k -> typeHole(s"$k<error>")))), maps._3 ) // probably not safe to return a hole here, but at this point we're screwed anyway...
  }
  
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









