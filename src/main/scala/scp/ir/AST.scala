package scp
package ir

import scala.collection.mutable
import lang._
import utils._

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
  * TODO: should have a Liftable trait for constants, with the default impl resulting in storage in glbal hash table (when Scala is the backend)
  * TODO generalize records to be usable outside of function parameters
  * TODO encode tuples as records? (with _1, _2, etc. as names) */
trait AST extends Base with RecordsTyping { // TODO rm dep to ScalaTyping
  import AST._
  
  object Quasi extends quasi.Quasi[this.type, Any] { val base: AST.this.type = AST.this }
  import Quasi.QuasiContext
  
  
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
    import ru.{Typed => ScalaTyped, _}
    
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
        
      case BoundVal(n) => q"${TermName(n)}"
        
      case Var(init) => q"_root_.scp.lib.Var(${toTree(init)})" // a Var not in a letin position... weird... can happen?

      case ReadVar(BoundVal(n)) => q"${TermName(n)}"
        
      case SetVar(BoundVal(n), valu) => q"${TermName(n)} = ${toTree(valu)}"
        
      case Imperative(effs, res) =>
        q"..${effs map toTree}; ${toTree(res)}"
        
      case as @ Ascribe(v) =>
        //toTree(v)
        q"${toTree(v)}:${as.typ.asInstanceOf[ScalaTyping#TypeRep].typ}"
        
      case Thunk(body) => toTree(body)
        
      case RecordGet(self, name, tp) => q"${TermName(name)}"
        
      case a @ Abs(Typed(_, RecordType(fields)), body) =>
        q"(..${fields map { case(name,tp) => q"val ${TermName(name)}: ${tp.typ}" }}) => ${toTree(body)}"
        
      case Abs(p, body) =>
        val typ = p.typ.asInstanceOf[ScalaTyping#TypeRep].typ // TODO adapt API
        q"(${TermName(p.name)}: $typ) => ${toTree(body)}"
        
      case App(Abs(p, body), Var(init)) =>
        val typ = p.typ.asInstanceOf[ScalaTypeRep].targs.head.typ
        q"var ${TermName(p.name)}: $typ = ${toTree(init)}; ${toTree(body)}"
        
      case App(Abs(p, body), arg) =>
        val typ = p.typ.typ
        q"val ${TermName(p.name)}: $typ = ${toTree(arg)}; ${toTree(body)}"
        
      case dsln @ NewObject(tp) =>
        /** For extremely obscure reasons, I got problems with this line (on commit https://github.com/LPTK/SC-Paradise-Open-Terms-Proto/commit/7c63fba4486ac42c96fe55c754a7f5d636596d61)
          * The toolbox would crash with internal errors.
          * The only way I found to fix it is a hack to reconstruct a type tree from scratch using the owners chain...
          */
        //New(tq"${tp.typ}") // FAILS SOMETIMES -- depending on what happened before...
        
        ru.New(tp.typ match {
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
    
    def isPure = true
    
    def quoted = Quoted[Nothing, Nothing](this)
    
    /** Used to replace free variables with bound ones when an unquote captures bound variables */
    def subs(xs: (String, Rep)*): Rep = subs(xs.toMap)
    def subs(xs: Map[String, Rep]): Rep =
      if (xs isEmpty) this else transformPartial(this) { case r @ Hole(n) => xs getOrElse (n, r) }
    
    def extract(t: Rep): Option[Extract] = {
      //println(s"${this.show} << ${t.show}")
      //println(s"${this} << ${t}")
      
      val r: Option[Extract] = (this, t) match {
        case (_, Ascribe(v)) => // Note: even if 'this' is a Hole, it is needed for term equivalence to ignore ascriptions
          mergeAll(typ extract (t.typ, Covariant), extract(v))
          
        case (Hole(name), _) => // Note: will also extract holes... which is important to asses open term equivalence
          // Note: it is not necessary to replace 'extruded' symbols here, since we use Hole's to represent free variables (see case for Abs)
          typ extract (t.typ, Covariant) flatMap { merge(_, (Map(name -> t), Map(), Map())) }
          
        case (BoundVal(n1), Hole(n2)) if n1 == n2 => // This is needed when we do function matching (see case for Abs); n2 is to be seen as a FV
          Some(EmptyExtract)
          //typ.extract(t.typ, Covariant) // I think the types cannot be wrong here (case for Abs checks parameter types)
          
        case (_, Hole(_)) => None
          
        case (v1: BoundVal, v2: BoundVal) =>
          // Bound variables are never supposed to be matched;
          // if we match a binder, we'll replace the bound variable with a free variable first
          throw new AssertionError("Bound variables are not supposed to be matched.")
          //if (v1.name == v2.name) Some(EmptyExtract) else None // check same type?
          
        case (Const(v1), Const(v2)) => if (v1 == v2) Some(EmptyExtract) else None
          
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
            a <- mergeAll( (args1 zip args2) map { case (as,bs) => as extract bs } )  //oh_and print("[Args:] ") and println
            rt <- tp1 extract (tp2, Covariant)  //oh_and print("[RetType:] ") and println
            m0 <- merge(s, t)
            m1 <- merge(m0, a)
            m2 <- merge(m1, rt)
          } yield m2
          
        case (or: OtherRep, r) => or extractRep r
        case (r, or: OtherRep) => or getExtractedBy r
          
        case _ => None
      }
      
      //println(s">> ${r map {case(mv,mt,msv) => (mv mapValues (_ show), mt, msv mapValues (_ map (_ show)))}}")
      r
    }
    
    override def equals(that: Any) = that match { // TODO override hashCode...
      case that: Rep =>
        if (this eq that) true else repEq(this, that)
      case _ => false
    }
  }
  
  def showRep(r: Rep): String = astPrinter(r)
  
  trait OtherRep extends Rep { 
    // TODO add missing methods (toTree, ...) + update pattern matches
    
    def extractRep(that: Rep): Option[Extract]
    def getExtractedBy(that: Rep): Option[Extract]
    
    def transform(f: Rep => Rep): Rep
    
    def print(printer: RepPrinter): PrintResult
    
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
  
  private var inExtraction = false
  protected def isInExtraction = inExtraction
  
  override def wrapExtract(r: => Rep) = try {
    inExtraction = true
    super.wrapExtract(r)
  } finally inExtraction = false
  
  def process(r: => Rep) = {
    val res = r
    if (inExtraction && isOnline) res else applyTransform(res).rep
  }
  final def << (r: => Rep) = process(r)
  
  def const[A: TypeEv](value: A): Rep = this << Const(value)
  def boundVal(name: String, typ: TypeRep) = BoundVal(name)(typ)
  def setVar(vari: Rep, valu: Rep): Rep = SetVar(vari, valu)
  def freshVar(typ: TypeRep) = BoundVal("val$"+varCount)(typ) oh_and (varCount += 1)
  private var varCount = 0
  
  def lambda(params: Seq[BoundVal], body: => Rep): Rep = this << {
    if (params.size == 1) Abs(params.head, body)
    else {
      val ps = params.toSet
      val p = freshVar(RecordType(params.map(v => v.name -> v.typ).toList)) // would be nice to give the freshVar a name hint "params"
      val tbody = transformPartial(body) {
        case v @ BoundVal(name) if ps(v) => recordGet(p, name, v.typ)
      }
      Abs(p, tbody)
    }
  }
  
  override def ascribe[A: TypeEv](value: Rep): Rep =
    this << Ascribe[A](value)
  
  def newObject(tp: TypeRep): Rep = this << NewObject(tp)
  def moduleObject(fullName: String, tp: TypeRep): Rep = this << ModuleObject(fullName: String, tp: TypeRep)
  def methodApp(self: Rep, mtd: DSLSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep =
    this << MethodApp(self, mtd, targs, argss, tp) //and println
  
  // We encode thunks (by-name parameters) as functions from some dummy 'ThunkParam' to the result
  //def byName(arg: => Rep): Rep = this << dsl"(_: lib.ThunkParam) => ${Quote[A](arg)}".rep
  def byName(arg: => Rep): Rep = this << lambda(Seq(freshVar(typeRepOf[lib.ThunkParam])), arg)
  
  def recordGet(self: Rep, name: String, typ: TypeRep) = RecordGet(self, name, typ)
  
  
  def hole[A: TypeEv](name: String) = this << Hole[A](name)
  //def hole[A: TypeEv](name: String) = Var(name)(typeRepOf[A])
  
  def splicedHole[A: TypeEv](name: String): Rep = this << SplicedHole(name)
  
  
  /**
    * In xtion, represents an extraction hole
    * In ction, represents a free variable
    */
  case class Hole[+A: TypeEv](name: String) extends Rep {
    val typ = typeEv[A].rep
    override def isPure: Bool = false
  }
  case class SplicedHole[+A: TypeEv](name: String) extends Rep {
    val typ = typeEv[A].rep
    override def isPure: Bool = false
  }
  
  case class BoundVal(name: String)(val typ: TypeRep) extends Rep {
    def toHole = Hole(name)(TypeEv(typ))
    override def equals(that: Any) = that match { case that: AnyRef => this eq that case _ => false }
    //override def hashCode(): Int = name.hashCode // should be inherited
  }
  
  
  case class Const[A: TypeEv](value: A) extends Rep {
    val typ = typeEv[A].rep
  }
  case class Abs(param: BoundVal, body: Rep) extends Rep {
    def ptyp = param.typ
    val typ = funType(ptyp, body.typ)
    
    def fun(r: Rep) = transformPartial(body) { case `param` => r }
    
    def inline(arg: Rep): Rep = fun(arg) //body withSymbol (param -> arg)
    
    override def toString = s"Abs($param, $body)"
  }
  
  case class Ascribe[A: TypeEv](value: Rep) extends Rep {
    val typ = typeEv[A].rep
    
    override def extract(t: Rep): Option[Extract] = {
      val r0 = value.extract(t) getOrElse (return None)
      (typ extract (t.typ, ScalaTyping.Covariant)) flatMap (m => merge(r0, m))
    }
  }
  
  case class NewObject(typ: TypeRep) extends Rep
  
  case class ModuleObject(fullName: String, typ: TypeRep) extends Rep
  
  case class MethodApp(self: Rep, sym: DSLSymbol, targs: List[TypeRep], argss: List[ArgList], typ: TypeRep) extends Rep {
    override def isPure: Bool = false
  }
  
  //case class Record(fields: List[(String, Rep)]) extends Rep { // TODO
  //  val typ = RecordType
  //}
  case class RecordGet(self: Rep, name: String, typ: TypeRep) extends Rep
  
  
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
      //case a: Abs => Abs(a.pname, a.ptyp, (x: Rep) => tr(a.fun(x)))
      case Abs(p, b) =>
        //val p2 = tr(p)
        //Abs(p2, tr(b))
        Abs(p, tr(b)) // Note: we do not transform the parameter; could lead to surprising behaviors? (esp. in case of erroneous transform)
      case a: Ascribe[_] => Ascribe(tr(a.value))(TypeEv(a.typ))
      case Hole(_) | SplicedHole(_) | NewObject(_) => r
      case mo @ ModuleObject(fullName, tp) => mo
      case RecordGet(se, na, tp) => RecordGet(tr(se), na, tp)
      case MethodApp(self, mtd, targs, argss, tp) =>
        def trans(args: Args) = Args(args.reps map tr: _*)
        MethodApp(tr(self), mtd, targs, argss map {
          case as: Args => trans(as)
          case ArgsVarargs(as, vas) => ArgsVarargs(trans(as), trans(vas))
          case ArgsVarargSpliced(as, va) => ArgsVarargSpliced(trans(as), va)
        }, tp)
      //case s: Symbol => s
      case v: BoundVal => v
      case Const(_) => r
      case or: OtherRep => or.transform(f)
    })
    //println(s"Traversed $r, got $ret")
    //println(s"=> $ret")
    ret
  }
  
  def typ(r: Rep): TypeRep = r.typ
  
  
  
  object Typed { def unapply(r: Rep): Some[(Rep, TypeRep)] = Some(r,r.typ) }
  
  //lazy val UnitType = typeRepOf[Unit]
  lazy val ThunkParamType = typeRepOf[lib.ThunkParam]
  
  object App {
    def unapply(r: Rep) = r match {
      case MethodApp(fun, Function1ApplySymbol, Nil, Args(arg)::Nil, _) => Some(fun, arg)
      case _ => None
    }
  }
  object New {
    def unapply(r: Rep) = r match {
      case m @ MethodApp(NewObject(typ), _, targs, argss, _) =>
        assert(m.sym.isConstructor)
        Some(typ, targs, argss)
      case _ => None
    }
  }
  object Thunk {
    def unapply(r: Rep) = r match {
      case a: Abs if a.ptyp =:= ThunkParamType => Some(a.body)
      case _ => None
    }
  }
  object IfThenElse {
    val Symbol = loadSymbol(true, "scp.lib.package", "IfThenElse")
    def unapply(r: Rep) = r match {
      case MethodApp(self, Symbol, _::Nil, Args(cond, thn, els)::Nil, _) => Some(cond, thn, els)
      case _ => None
    }
  }
  object Imperative {
    val Symbol = loadSymbol(true, "scp.lib.package", "Imperative")
    def unapply(r: Rep) = r match {
      case MethodApp(self, Symbol, _::Nil, ArgsVarargs(Args(),Args(effs @ _*))::Args(res)::Nil, _) => Some(effs, res)
      case _ => None
    }
  }
  object Var {
    val Symbol = loadSymbol(true, "scp.lib.Var", "apply")
    def unapply(r: Rep) = r match {
      case MethodApp(self, Symbol, _::Nil, Args(init)::Nil, _) => Some(init)
      case _ => None
    }
  }
  object ReadVar {
    val Symbol = loadSymbol(false, "scp.lib.Var", "$bang")
    def unapply(r: Rep) = r match {
      case MethodApp(self, Symbol, Nil, Nil, _) => Some(self)
      case _ => None
    }
  }
  object SetVar {
    val Symbol = loadSymbol(false, "scp.lib.Var", "$colon$eq")
    def apply(vari: Rep, valu: Rep) = methodApp(vari, Symbol, Nil, Args(valu)::Nil, typeRepOf[Unit])
    def unapply(r: Rep) = r match {
      case MethodApp(self, Symbol, Nil, Args(valu)::Nil, _) => Some(self,valu)
      case _ => None
    }
  }
  
  
  type PrintResult = (String, Precedence)
  class RepPrinter extends (Rep => String) {
    
    var currentIndent = 0
    def indent[A](x: => A) = {
      currentIndent += 1
      try x finally currentIndent -= 1
    }
    
    val maxPrecedence = Precedence(Int.MaxValue/2)
    val minPrecedence = Precedence(Int.MinValue/2)
    
    val lambdaPrec = Precedence(50)
    val itePrec = Precedence(30)
    val opPrec = Precedence(200)
    
    def noWrap(r: Rep) = print(r)._1
    def wrap(r: Rep, prec: Precedence, assoc: Boolean = false) = print(r) match {
      case (s,p) if p.value > prec.value => s
      case (s,p) if assoc && p.value == prec.value => s
      case (s,_) => s"($s)"
    }
    def wrapAssoc(r: Rep, prec: Precedence) = wrap(r, prec, true)
    
    private val UnaryPrefix = "unary_"
    
    final def apply(r: Rep): String = print(r)._1
    def print(r: Rep): PrintResult = {
      val typ = r.typ
      r match {
        case Hole(name) => s"$$$name: $typ" -> minPrecedence  // used to be:  s"$$$$$name: $typ"
        case SplicedHole(name) => s"$$$name: $typ*" -> minPrecedence
        //case Var(name) => s"$name: $typ" -> minPrecedence
        case BoundVal(name) => name -> maxPrecedence
        case Const(value) => s"$value" -> maxPrecedence
        case Thunk(body) => s"=> ${noWrap(body)}" -> lambdaPrec
        case Abs(Typed(v, typ), body) => s"(${noWrap(v)}: $typ) => ${wrapAssoc(body, lambdaPrec)}" -> lambdaPrec
        case App(fun,arg) => s"${wrapAssoc(fun, 100)} ${wrap(arg, 100)}" -> 100
        case Ascribe(value) => s"${wrapAssoc(value,maxPrecedence)}<:$typ" -> maxPrecedence //s"($value: $typ)"
        //case Ascribe(value) => s"${wrapAssoc(value,maxPrecedence)}" -> maxPrecedence //s"($value: $typ)" // enable to get prettier printing
        case NewObject(_) => s"new $typ" -> maxPrecedence
        case RecordGet(self, fieldName, typ) => s"${wrapAssoc(self, maxPrecedence)}.$fieldName" -> maxPrecedence
        case ModuleObject(fullName, _) =>
          val path = fullName.splitSane('.')
          val prefix = if (path.size > 2) ".." else ""
          prefix+(path drop (path.size-2) mkString ".") -> maxPrecedence
        case Imperative(effs, res) =>
          s"{ ${ effs map (wrapAssoc(_,minPrecedence)) mkString "; " }; ${ wrapAssoc(res,minPrecedence) } }" -> maxPrecedence
        case IfThenElse(cond, Thunk(thn), Thunk(els)) =>
          s"if ${noWrap(cond)} then ${wrapAssoc(thn, itePrec)} else ${wrapAssoc(els, itePrec)}" -> itePrec
        case MethodApp(self, sym, Nil, Nil, _) if sym.name.decodedName.toString.startsWith(UnaryPrefix) =>
          s"${sym.name.decodedName.toString.drop(UnaryPrefix.size)}${wrap(self, opPrec)}" -> opPrec
        case MethodApp(self, sym, targs, argss, _) =>
          val symName = sym.name.decodedName.toString
          val isNotOp = sym.isConstructor || symName.head.isLetter
          //val rightAssoc = symName.last == ':'
          val prec = if (isNotOp) maxPrecedence else opPrec
          val selfStr = wrapAssoc(self, maxPrecedence) // Note: not 'prec' because the rules of prec/assoc for operators are complicated and not uniform
          val trunk = if (sym.isConstructor) s"$selfStr" else {
            if (isNotOp) s"$selfStr.$symName"
            else s"$selfStr $symName"
          }
          val sep = if (isNotOp || argss.isEmpty) "" else " "
          val args = argss match {
            case Args(arg)::Nil => wrap(arg, prec)
            case _ => argss map (_ show (this, isNotOp)) mkString
          }
          trunk + (targs.mkString("[",",","]")*(targs.size min 1)) + sep + args -> prec
        case or: OtherRep => or print this
      }
    }
  }
  object astPrinter extends RepPrinter
}













