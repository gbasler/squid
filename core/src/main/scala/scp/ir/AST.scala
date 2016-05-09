package scp
package ir

import scala.collection.mutable
import lang._

import scala.reflect.runtime.{universe => ru}
import ScalaTyping.{Contravariant, Covariant, Variance}
import scp.quasi.EmbeddingException
import scp.utils.MacroUtils.StringOps

/**
  * TODO: add a lock on the HashMap...
  * Also, we could share objects that are equal instead of cluttering the table (but that'd require a bidirectional map)
  */
//private // cannot make this private; needs access to `constants` from reflexive compilation
object AST {
  import scala.tools.reflect.ToolBox
  private lazy val toolBox = ru.runtimeMirror(getClass.getClassLoader).mkToolBox() // Q: necessary 'lazy'? (objects are already lazy)
  
  private var curConstantIndex = 0
  //val constants = new mutable.WeakHashMap[Int, Any] // cannot be weak; otherwise we may lose the object before accessing it...
  val constants = new mutable.HashMap[Int, Any]
}

/** Main language trait, encoding second order lambda calculus with records, let-bindings and ADTs
  * TODO: should have a Liftable trait for constants, with the default impl resulting in storage in glbal hash table (when Scala is the backend) */
trait AST extends Base with ScalaTyping { // TODO rm dep to ScalaTyping
  import AST._
  
  
  object Constant extends ConstAPI {
    override def unapply[A: ru.TypeTag, S](x: Q[A, S]): Option[A] = x.rep match {
      case Const(v) if ru.typeOf[A] <:< x.rep.typ.asInstanceOf[ScalaTyping#TypeRep].typ => Some(v.asInstanceOf[A])
      case _ => None
    }
  }
  
  
  
  // TODO move this to some other file
  protected def runRep(r: Rep): Any = {
    val t = toTree(r)
    System.err.println("Compiling tree: "+t)
    toolBox.eval(t)
  }
  protected def toTree(r: Rep): ru.Tree = { // TODO remember (lazy val in Rep)
    import ru._
    
    //println(s"To tree: $r")
    r match {
        
      //case Const(v) => v // we want a tree!
      //case Const(v) => ru.Literal(ru.Constant(v)) // TODOne check correct constant type -- otherwise it raises 'java.lang.Error: bad constant value'
        
      //case Const(v: Int) => q"$v"
      //case Const(v: Double) => q"$v"
      //case Const(v: String) => q"$v"
        
      // Types that can be lifted as literals:
      case Const(_: Int) | Const(_: Long) | Const(_: Float) | Const(_: Double) | Const(_: Byte)
           | Const(_: String) | Const(_: Symbol) | Const(())
           | Const(_: scala.reflect.api.Types#TypeApi) | Const(_: scala.reflect.api.Symbols#SymbolApi) // matching Types#Type is unchecked (erasure)
      => ru.Literal(ru.Constant(r.asInstanceOf[Const[_]].value)) // Note: if bad types passed, raises 'java.lang.Error: bad constant value'
        
      // Common types we provide a lifting for:
      // FIXME: match the type symbol, not the value; for example a Const[Any] with runtime type Any may contain a Seq[_]
      case cst @ Const(vs: Seq[_]) =>
        val tp = cst.typ.asInstanceOf[ScalaTyping#ScalaTypeRep].targs.head
        q"_root_.scala.Seq(..${vs map (v => toTree(Const(v)(TypeEv(tp.asInstanceOf[TypeRep]))))})"
      case cst @ Const(vs: Set[_]) =>
        val tp = cst.typ.asInstanceOf[ScalaTyping#ScalaTypeRep].targs.head
        q"_root_.scala.Predef.Set(..${vs map (v => toTree(Const(v) (TypeEv(tp.asInstanceOf[TypeRep])) ))})"
      case cst @ Const(Some(v)) => 
        val tp = cst.typ.asInstanceOf[ScalaTyping#ScalaTypeRep].targs.head
        q"_root_.scala.Some(${toTree(Const(v) (TypeEv(tp.asInstanceOf[TypeRep])) )})"
      case Const(None) => q"_root_.scala.None"
        
      // Other types of constants are simply stored (forever) in a global hash table:
      case cst @ Const(v) =>
        constants(curConstantIndex) = v
        curConstantIndex += 1
        q"${symbolOf[AST].companion}.constants(${curConstantIndex-1}).asInstanceOf[${cst.typ.asInstanceOf[ScalaTyping#TypeRep].typ}]"
        
      //case Const(v: reflect.ClassTag[_]) => 
      //  println(v.runtimeClass.getName)
      //  q"_root_.scala.reflect.classTag[${v.runtimeClass.getName}]"
        
      //case Const(cl: Class[_]) =>
        //q"${TypeName(cl.getName)}.class" // not valid Scala syntax
        //q"_root_.scala.reflect.classTag[${TypeName(cl.getName)}].runtimeClass" // object classTag is not a member of package reflect
        //q"_root_.scala.reflect.classTag[String].runtimeClass" // object classTag is not a member of package reflect
        
      case Var(n) => q"${TermName(n)}"
      case App(f, a) => q"(${toTree(f)})(${toTree(a)})"
      case as @ Ascribe(v) =>
        //toTree(v)
        q"${toTree(v)}:${as.typ.asInstanceOf[ScalaTyping#TypeRep].typ}"
      case a: Abs =>
        val typ = a.ptyp.asInstanceOf[ScalaTyping#TypeRep].typ // TODO adapt API
        /* // handling thunks:
        if (a.ptyp <:< unitType) q"() => ${toTree(a.body)}" else*/ // probably not safe in general (Unit params could occur in the presence of generics)
        q"(${TermName(a.pname)}: $typ) => ${toTree(a.body)}"
      case dsln @ NewObject(tp) =>
        /** For extremely obscure reasons, I got problems with this line (on commit https://github.com/LPTK/SC-Paradise-Open-Terms-Proto/commit/7c63fba4486ac42c96fe55c754a7f5d636596d61)
          * The toolbox would crash with internal errors.
          * The only way I found to fix it is a hack to reconstruct a type tree from scratch using the owners chain...
          */
        //New(tq"${tp.typ}") // FAILS SOMETIMES -- depending on what happened before...
        
        New(tp.typ match {
          case TypeRef(tpe, sym, args) =>
            def path(s: Symbol): Tree =
              // In some contexts, we get some weird '_root_.<root>.blah' paths
              if (s.owner == NoSymbol || s.name.toString=="<root>") q"_root_" else q"${path(s.owner)}.${s.name.toTermName}"
            tq"${path(sym.owner)}.${sym.name.toTypeName}[..$args]"
          case tpe => tq"$tpe"
        })
        
      case dslm @ ModuleObject(fullName, tp) =>
        
        //q"${reflect.runtime.currentMirror.staticModule(fullName)}"
        // ^ the (capricious) toolbox sometimes wants full paths [1], and this does not generate full paths...
        // [1]: It seems to be when the compiled epxression has macro expansions, like in dsl""" dsl"..." """.run
        
        val path = fullName.splitSane('.').toList
        path.tail.foldLeft(q"${TermName(path.head)}":Tree)((acc,n) => q"$acc.${TermName(n)}")
        
      case dslm @ MethodApp(self, mtd, targs, argss, tp) =>
        val self2 = toTree(self)
        val argss2 = argss map {
          case Args(as @ _*) => as map toTree
          case ArgsVarargs(Args(as @ _*), Args(vas @ _*)) => (as map toTree) ++ (vas map toTree)
          case ArgsVarargSpliced(Args(as @ _*), va) => (as map toTree) :+ q"${toTree(va)}: _*"
        }
        q"$self2 ${mtd.name} ...$argss2"
      case Hole(n) => throw new Exception(s"Trying to build an open term! Variable '$n' is free.")
    }
  }
  
  
  sealed trait Rep { // Q: why not make it a class with typ as param?
    val typ: TypeRep
    
    def quoted = Quoted[Nothing, Nothing](this)
    
    /** Used to replace free variables with bound ones when an unquote captures bound variables */
    def subs(xs: (String, Rep)*): Rep = subs(xs.toMap)
    def subs(xs: Map[String, Rep]): Rep =
      if (xs isEmpty) this else transformPartial(this) { case r @ Hole(n) => xs getOrElse (n, r) }
    
    def extract(t: Rep): Option[Extract] = {
      //println(s"$this << $t")
      
      val r: Option[Extract] = (this, t) match {
        case (_, Ascribe(v)) => // Note: even if 'this' is a Hole, it is needed for term equivalence to ignore ascriptions
          mergeAll(typ extract (t.typ, Covariant), extract(v))
          
        case (Hole(name), _) => // Note: will also extract holes... which is important to asses open term equivalence
          // Note: it is not necessary to replace 'extruded' symbols here, since we use Hole's to represent free variables (see case for Abs)
          typ extract (t.typ, Covariant) flatMap { merge(_, (Map(name -> t), Map(), Map())) }
          
        case (Var(n1), Hole(n2)) if n1 == n2 => // This is needed when we do function matching (see case for Abs); n2 is to be seen as a FV
          Some(EmptyExtract)
          //typ.extract(t.typ, Covariant) // I think the types cannot be wrong here (case for Abs checks parameter types)
          
        case (_, Hole(_)) => None
          
        case (v1: Var, v2: Var) =>
          // Bound variables are never supposed to be matched;
          // if we match a binder, we'll replace the bound variable with a free variable first
          throw new AssertionError("Bound variables are not supposed to be matched.")
          //if (v1.name == v2.name) Some(EmptyExtract) else None // check same type?
          
        case (Const(v1), Const(v2)) => if (v1 == v2) Some(EmptyExtract) else None
          
        case (App(f1,a1), App(f2,a2)) => for (e1 <- f1 extract f2; e2 <- a1 extract a2; m <- merge(e1, e2)) yield m
          
        case (a1: Abs, a2: Abs) =>
          // The body of the matched function is recreated with a *free variable* in place of the parameter, and then matched with the
          // body of the matcher, so what the matcher extracts contains potentially (safely) extruded variables.
          for {
            pt <- a1.ptyp extract (a2.ptyp, Contravariant)
            b <- a1.body.extract(a2.fun(a1.param.toHole)) // 'a1.param.toHole' represents a free variables
            m <- merge(pt, b)
          } yield m
          
        case (ModuleObject(fullName1,tp1), ModuleObject(fullName2,tp2)) if fullName1 == fullName2 =>
          Some(EmptyExtract) // Note: not necessary to test the types: object with identical paths should be identical
          
        case (NewObject(tp1), NewObject(tp2)) => tp1 extract (tp2, Covariant)
          
        case (MethodApp(self1,mtd1,targs1,args1,tp1), MethodApp(self2,mtd2,targs2,args2,tp2))
          if mtd1 == mtd2
          //if {println(s"Comparing ${mtd1.fullName} == ${mtd2.fullName}, ${mtd1 == mtd2}"); mtd1 == mtd2}
        =>
          assert(args1.size == args2.size, s"Inconsistent number of argument lists for method $mtd1: $args1 and $args2")
          assert(targs1.size == targs2.size, s"Inconsistent number of type arguments for method $mtd1: $targs1 and $targs2")
          
          for {
            s <- self1 extract self2
            t <- {
              /** The following used to end with '... extract (b, Variance of p.asType)'.
                * However, method type parameters seem to always be tagged as invariant.
                * This was kind of restrictive. For example, you could not match apply functions like "Seq()" with "Seq[Any]()"
                * We now match method type parameters covariantly, although I'm not sure it is sound. At least the fact we
                * now also match the *returned types* prevents obvious unsoundness sources.
                */
              mergeAll( (targs1 zip targs2 zip mtd1.typeParams) map { case ((a,b),p) => a extract (b, Covariant) } )
            }
            a <- mergeAll( (args1 zip args2) map { case (as,bs) => as extract bs } )
            rt <- tp1 extract (tp2, Covariant)
            m0 <- merge(s, t)
            m1 <- merge(m0, a)
            m2 <- merge(m1, rt)
          } yield m2
        case (or: OtherRep, r) => or extractRep r
        case _ => None
      }
      
      //println(s">> $r")
      r
    }
    
    override def equals(that: Any) = that match { // TODO override hashCode...
      case that: Rep =>
        if (this eq that) true else repEq(this, that)
      case _ => false
    }
  }
  
  trait OtherRep extends Rep { 
    // TODO add missing methods (toTree, ...) + update pattern matches
    
    def extractRep(that: Rep): Option[Extract]
    
    def transform(f: Rep => Rep): Rep
    
  }
  
  def extract(xtor: Rep, t: Rep): Option[Extract] = xtor.extract(t)//(Map())
  def spliceExtract(xtor: Rep, args: Args): Option[Extract] = xtor match {
    case SplicedHole(name) => Some(Map(), Map(), Map(name -> args.reps))
    case h @ Hole(name) => // If we extract ($xs*) using ($xs:_*), we have to build a Seq in the object language and return it
      val rep = methodApp(
        moduleObject("scala.collection.Seq", SimpleTypeRep(ru.typeOf[Seq.type])),
        loadSymbol(true, "scala.collection.Seq", "apply"),
        h.typ.asInstanceOf[ScalaTypeRep].targs.head :: Nil, // TODO check; B/E
        Args()(args.reps: _*) :: Nil, h.typ)
      Some(Map(name -> rep), Map(), Map())
    case _ => throw EmbeddingException(s"Trying to splice-extract with invalid extractor $xtor")
  }
  
  def const[A: TypeEv](value: A): Rep = Const(value)
  //def abs[A: TypeEv, B: TypeEv](name: String, fun: Rep => Rep): Rep = {
  //  val v = Var(name)(typeRepOf[A])
  //  Abs(v, fun(v))
  //}
  def abs[A: TypeEv, B: TypeEv](name: String, fun: Rep => Rep): Rep = Abs(name, typeRepOf[A], fun)
  def app[A: TypeEv, B: TypeEv](fun: Rep, arg: Rep): Rep = App[A,B](fun, arg)
  
  override def ascribe[A: TypeEv](value: Rep): Rep = Ascribe[A](value)
  
  def newObject(tp: TypeRep): Rep = NewObject(tp)
  def moduleObject(fullName: String, tp: TypeRep): Rep = ModuleObject(fullName: String, tp: TypeRep)
  def methodApp(self: Rep, mtd: DSLSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep =
    MethodApp(self, mtd, targs, argss, tp)
  
  def hole[A: TypeEv](name: String) = Hole[A](name)
  //def hole[A: TypeEv](name: String) = Var(name)(typeRepOf[A])
  
  def splicedHole[A: TypeEv](name: String): Rep = SplicedHole(name)
  
  
  /**
    * In xtion, represents an extraction hole
    * In ction, represents a free variable
    */
  case class Hole[+A: TypeEv](name: String) extends Rep {
    val typ = typeEv[A].rep
    override def toString = s"($$$name: $typ)"  // used to be:  s"($$$$$name: $typ)"
  
    //override def equals(that: Any): Boolean = that match {
    //  case HoleExtract(n) => n == name // TODO check type
    //  case _ => false
    //}
  }
  case class SplicedHole[+A: TypeEv](name: String) extends Rep {
    val typ = typeEv[A].rep
    override def toString = s"($$$name: $typ*)"
  }
  
  case class Var(val name: String)(val typ: TypeRep) extends Rep {
    def toHole = Hole(name)(TypeEv(typ))
    override def toString = s"($name: $typ)"
  }
  
  case class Const[A: TypeEv](value: A) extends Rep {
    val typ = typeEv[A].rep
    override def toString = s"$value"
  }
  //case class Abs(param: Var, body: Rep) extends Rep {
  //  def inline(arg: Rep): Rep = ??? //body withSymbol (param -> arg)
  //case class Abs[A:TypeEv,B](name: String, fun: Rep => Rep) extends Rep {
  //  val param = Var(name)
  case class Abs(pname: String, ptyp: TypeRep, fun: Rep => Rep) extends Rep {
    val param = Var(pname)(ptyp)
    val body = fun(param)
    
    //val typ = funType(TypeEv(ptyp), TypeEv(body.typ)).rep
    val typ = funType(ptyp, body.typ)
    
    def inline(arg: Rep): Rep = ??? //body withSymbol (param -> arg)
    override def toString = body match {
      case that: Abs => s"{ $param, ${that.print(false)} }"
      case _ => print(true)
    }
    def print(paren: Boolean) =
    if (paren) s"{ ($param) => $body }" else s"($param) => $body"
  }
  case class App[A,B: TypeEv](fun: Rep, arg: Rep) extends Rep {
    val typ = typeEv[B].rep
    override def toString = s"$fun $arg"
  }
  
  case class Ascribe[A: TypeEv](value: Rep) extends Rep {
    val typ = typeEv[A].rep
    override def toString = s"$value<:$typ" //s"($value: $typ)"
    
    override def extract(t: Rep): Option[Extract] = {
      val r0 = value.extract(t) getOrElse (return None)
      (typ extract (t.typ, ScalaTyping.Covariant)) flatMap (m => merge(r0, m))
    }
    
    //override def equals(that: Any) = value == that
  }
  
  case class NewObject(typ: TypeRep) extends Rep {
    override def toString = s"new $typ"
  }
  case class ModuleObject(fullName: String, typ: TypeRep) extends Rep {
    override def toString = fullName
  }
  case class MethodApp(self: Rep, sym: DSLSymbol, targs: List[TypeRep], argss: List[ArgList], typ: TypeRep) extends Rep {
    lazy val mtd = DSLDef(sym.fullName, sym.info.toString, sym.isStatic)
    override def toString = (if (sym.isConstructor) s"$self" else s"$self.${mtd.shortName}") +
      (targs.mkString("[",",","]")*(targs.size min 1)) + (argss mkString)
  }
  
  
  /**
    * Note: Works well with FVs (represented as holes),
    * since it checks that each extraction extracts exactly a hole with the same name
    */
  def repEq(a: Rep, b: Rep): Boolean = {
    (a extract b, b extract a) match {
      //case (Some((xs,xts)), Some((ys,yts))) => xs.keySet == ys.keySet && xts.keySet == yts.keySet
      case (Some((xs,xts,fxs)), Some((ys,yts,fys))) =>
        // Note: could move these out of the function body:
        val extractsHole: ((String, Rep)) => Boolean = {
          case (k: String, Hole(name)) if k == name => true
          case _ => false
        }
        val extractsTypeHole: ((String, TypeRep)) => Boolean = {
          case (k: String, TypeHoleRep(name)) if k == name => true
          case _ => false
        }
        fxs.isEmpty && fys.isEmpty && // spliced term lists are only supposed to be present in extractor terms, which are not supposed to be compared
        (xs forall extractsHole) && (ys forall extractsHole) && (xts forall extractsTypeHole) && (yts forall extractsTypeHole)
      case _ => false
    }
  }
  
  //def typEq(a: TypeRep, b: TypeRep): Boolean = ???
  
  //implicit def funType[A: TypeEv, B: TypeEv]: TypeEv[A => B] = ???
  
  
  
  def transformPartial(r: Rep)(f: PartialFunction[Rep, Rep]): Rep =
    //transform(r){ case r if f isDefinedAt r => f(r) case r => r}
    transform(r)(r => f applyOrElse (r, identity[Rep]))
  
  def transform(r: Rep)(f: Rep => Rep): Rep = {
    //println(s"Traversing $r")
    val tr = (r: Rep) => transform(r)(f)
    val ret = f(r match {
      //case Abs(p, b) => Abs(p, tr(b))
      case a: Abs => Abs(a.pname, a.ptyp, (x: Rep) => tr(a.fun(x)))
      case a: Ascribe[_] => Ascribe(tr(a.value))(TypeEv(a.typ))
      case ap @ App(fun, a) => App(tr(fun), tr(a))(TypeEv(ap.typ))
      case Hole(_) | SplicedHole(_) | NewObject(_) => r
      case mo @ ModuleObject(fullName, tp) => mo
      case MethodApp(self, mtd, targs, argss, tp) =>
        def trans(args: Args) = Args(args.reps map tr: _*)
        MethodApp(tr(self), mtd, targs, argss map {
          case as: Args => trans(as)
          case ArgsVarargs(as, vas) => ArgsVarargs(trans(as), trans(vas))
          case ArgsVarargSpliced(as, va) => ArgsVarargSpliced(trans(as), va)
        }, tp)
      //case s: Symbol => s
      case v: Var => v
      case Const(_) => r
      case or: OtherRep => or.transform(f)
    })
    //println(s"Traversing $r, getting $ret")
    //println(s"=> $ret")
    ret
  }
  
  def typ(r: Rep): TypeRep = r.typ
  
  
  
}















