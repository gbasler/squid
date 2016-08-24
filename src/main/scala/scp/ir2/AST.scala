package scp
package ir2

import scala.collection.mutable
import lang2._
import quasi2._
import utils.meta.RuntimeUniverseHelpers
import RuntimeUniverseHelpers.sru
import utils._
import Tuple2List.asList


case class IRException(msg: String) extends Exception(msg)

/** 
  * TODO: more efficent online rewriting by pre-partitioning rules depending on what they can match!
  * 
  **/
trait AST extends InspectableBase with ScalaTyping with RuntimeSymbols { self: IntermediateBase =>
  
  
  /* --- --- --- Required defs --- --- --- */
  
  def rep(dfn: Def): Rep
  def dfn(r: Rep): Def
  
  
  
  /* --- --- --- Provided defs --- --- --- */
  
  
  def readVal(v: BoundVal): Rep = rep(v)
  
  def hole(name: String, typ: TypeRep) = rep(Hole(name)(typ))
  def splicedHole(name: String, typ: TypeRep): Rep = rep(SplicedHole(name)(typ))
  
  def const(value: Any): Rep = rep(Constant(value))
  def bindVal(name: String, typ: TypeRep) = new BoundVal(name)(typ)
  def freshBoundVal(typ: TypeRep) = BoundVal("val$"+varCount)(typ) oh_and (varCount += 1)
  private var varCount = 0
  
  // TODO: more efficient and safe: add record info to BoundVal and wrap in ReadVal that contains which field is accessed
  def lambda(params: List[BoundVal], body: => Rep): Rep = rep({
    if (params.size == 1) Abs(params.head, body)
    else {
      val ps = params.toSet
      val p = freshBoundVal(recordType(params map(v => v.name -> v.typ))) // would be nice to give the freshVar a name hint "params"
      val tbody = bottomUpPartial(body) {
        case RepDef(v @ BoundVal(name)) if ps(v) => rep(recordGet(rep(p), name, v.typ))
      }
      Abs(p, tbody)
    }
  })
  
  override def ascribe(self: Rep, typ: TypeRep): Rep = rep(Ascribe(self, typ))
  
  def newObject(tp: TypeRep): Rep = rep(NewObject(tp))
  def moduleObject(fullName: String, isPackage: Boolean): Rep = rep({
    ModuleObject(fullName: String, isPackage)
  })
  def staticModule(fullName: String): Rep = rep({
    ModuleObject(fullName: String, false)
  })
  def module(prefix: Rep, name: String, typ: TypeRep): Rep = rep(Module(prefix, name, typ))
  def methodApp(self: Rep, mtd: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep =
    rep(MethodApp(self, mtd, targs, argss, tp))
  
  def byName(arg: => Rep): Rep =
    lambda(bindVal("$BYNAME$", uninterpretedType[Unit]) :: Nil, arg) // FIXME proper impl
  // TODO
  // We encode thunks (by-name parameters) as functions from some dummy 'ThunkParam' to the result
  //def byName(arg: => Rep): Rep = dsl"(_: lib.ThunkParam) => ${Quote[A](arg)}".rep
  //def byName(arg: => Rep): Rep = ??? //lambda(Seq(freshBoundVal(typeRepOf[lib.ThunkParam])), arg)
  
  def recordGet(self: Rep, name: String, typ: TypeRep) = RecordGet(self, name, typ)
  
  
  def substitute(r: Rep, defs: Map[String, Rep]): Rep = bottomUp(r) { r => dfn(r) match {
    case h @ Hole(n) => defs getOrElse (n, r)
    case h @ SplicedHole(n) => defs getOrElse (n, r)
    case _ => r
  }}
  
  def bottomUp(r: Rep)(f: Rep => Rep): Rep = f({
    val d = dfn(r)
    //println(s"Traversing $r")
    val tr = (r: Rep) => bottomUp(r)(f)
    val ret = d match {
      case Abs(p, b) =>
        Abs(p, tr(b)) // Note: we do not transform the parameter; could lead to surprising behaviors? (esp. in case of erroneous transform)
      case Ascribe(r,t) => Ascribe(tr(r),t)
      case Hole(_) | SplicedHole(_) | NewObject(_) => d
      case mo @ ModuleObject(fullName, tp) => mo
      case Module(pre, name, typ) => Module(tr(pre), name, typ)
      case RecordGet(se, na, tp) => RecordGet(tr(se), na, tp)
      case MethodApp(self, mtd, targs, argss, tp) =>
        def trans(args: Args) = Args(args.reps map tr: _*)
        MethodApp(tr(self), mtd, targs, argss map {
          case as: Args => trans(as)
          case ArgsVarargs(as, vas) => ArgsVarargs(trans(as), trans(vas))
          case ArgsVarargSpliced(as, va) => ArgsVarargSpliced(trans(as), tr(va))
        }, tp)
      case v: BoundVal => v
      case Constant(_) => d
      //case or: OtherRep => or.transform(f) // TODO?
    }
    //println(s"Traversed $r, got $ret")
    //println(s"=> $ret")
    rep(ret)
  })
  
  //def bottomUpPartial(r: Rep)(f: PartialFunction[Rep, Rep]): Rep = bottomUp(r)(r => f applyOrElse (r, identity[Rep]))
  
  
  
  
  def extract(xtor: Rep, xtee: Rep): Option[Extract] = dfn(xtor) extractImpl xtee
  
  //def spliceExtract(xtor: Rep, t: Args): Option[Extract] = ???
  def spliceExtract(xtor: Rep, args: Args): Option[Extract] = xtor match {
    case RepDef(SplicedHole(name)) => Some(Map(), Map(), Map(name -> args.reps))
    case RepDef(h @ Hole(name)) => // If we extract ($xs*) using ($xs:_*), we have to build a Seq in the object language and return it
      //val rep = methodApp(
      //  moduleObject("scala.collection.Seq", SimpleTypeRep(ru.typeOf[Seq.type])),
      //  loadSymbol(true, "scala.collection.Seq", "apply"),
      //  h.typ.asInstanceOf[ScalaTypeRep].targs.head :: Nil, // TODO check; B/E
      //  Args()(args.reps: _*) :: Nil, h.typ)
      //Some(Map(name -> rep), Map(), Map())
      //Seq(12)
      val rep = methodApp(
        moduleObject("scala.collection.Seq", false),
        loadMtdSymbol(
          //loadTypSymbol("scala.collection.Seq"),
          loadTypSymbol("scala.collection.generic.GenericCompanion"),
          "apply", None),
        h.typ.tpe.typeArgs.head :: Nil,
        Args()(args.reps: _*) :: Nil, h.typ)
      Some(Map(name -> rep), Map(), Map())
    case _ => throw IRException(s"Trying to splice-extract with invalid extractor $xtor")
  }
  
  
  /**
  * Note: Works well with FVs (represented as holes),
  * since it checks that each extraction extracts exactly a hole with the same name
  */
  def repEq(a: Rep, b: Rep): Boolean = {
    val a_e_b = a extract b
    if (a_e_b.isEmpty) return false
    (a_e_b, b extract a) match {
      //case (Some((xs,xts)), Some((ys,yts))) => xs.keySet == ys.keySet && xts.keySet == yts.keySet
      case (Some((xs,xts,fxs)), Some((ys,yts,fys))) =>
        // Note: could move these out of the function body:
        val extractsHole: ((String, Rep)) => Boolean = {
          case (k: String, RepDef(Hole(name))) if k == name => true
          case _ => false
        }
        val extractsTypeHole: ((String, TypeRep)) => Boolean = {
          //case (k: String, TypeHoleRep(name)) if k == name => true // FIXME
          case _ => false
        }
        fxs.isEmpty && fys.isEmpty && // spliced term lists are only supposed to be present in extractor terms, which are not supposed to be compared
        (xs forall extractsHole) && (ys forall extractsHole) && (xts forall extractsTypeHole) && (yts forall extractsTypeHole)
      case _ => false
    }
  }

  
  
  
  
  
  /* --- --- --- Node Definitions --- --- --- */
  
  
  case class BoundVal(name: String)(val typ: TypeRep) extends Def {
    def toHole(newName: String) = Hole(newName)(typ, Some(this))
    override def equals(that: Any) = that match { case that: AnyRef => this eq that  case _ => false }
    //override def hashCode(): Int = name.hashCode // should be inherited
    override def toString = s"[$name:$typ]"
  }
  
  /**
  * In xtion, represents an extraction hole
  * In ction, represents a free variable
  */
  case class Hole(name: String)(val typ: TypeRep, val originalSymbol: Option[BoundVal] = None) extends Def {
    override def toString = s"$$$name<:$typ"
  }
  case class SplicedHole(name: String)(val typ: TypeRep) extends Def { // TODO use
    override def toString = s"$$$name<:$typ*"
  }
  
  case class Constant(value: Any) extends Def {
    lazy val typ = value match {
      case () => TypeRep(RuntimeUniverseHelpers.Unit)
      case _ => constType(value)
    }
  }
  case class Abs(param: BoundVal, body: Rep) extends Def {
    def ptyp = param.typ
    val typ = funType(ptyp, repType(body))
    
    def inline(arg: Rep) = bottomUpPartial(body) { //body withSymbol (param -> arg)
      case rep if dfn(rep) === `param` => arg
    }
    
    override def toString = s"$param => $body"
    //override def toString = s"$param [$typ]=> $body"
  }
  
  case class Ascribe(self: Rep, typ: TypeRep) extends Def
  
  case class NewObject(typ: TypeRep) extends Def
  
  case class ModuleObject(fullName: String, isPackage: Boolean) extends Def {
    //assert(!isPackage) // TODO; we shouldn't use this anymore
    val typ =
    if (isPackage) uninterpretedType(RuntimeUniverseHelpers mkTag RuntimeUniverseHelpers.srum.staticPackage(fullName).typeSignature)
    else staticModuleType(fullName)
  }
  
  case class MethodApp(self: Rep, sym: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], typ: TypeRep) extends Def {
    override def isPure: Bool = false
    override def toString: String =
      //s"$self.${sym.name}<${sym.typeSignature}>[${targs mkString ","}]${argss mkString ""}"
      s"$self.${sym.name}[${targs mkString ","}]${argss mkString ""}" + s"->$typ" // + s"(->$typ)"
  }
  case class Module(prefix: Rep, name: String, typ: TypeRep) extends Def
  
  //case class Record(fields: List[(String, Rep)]) extends Def { // TODO
  //  val typ = RecordType
  //}
  case class RecordGet(self: Rep, name: String, typ: TypeRep) extends Def
  
  sealed trait Def { // Q: why not make it a class with typ as param?
    val typ: TypeRep
    def isPure = true
    
    def extractImpl(r: Rep): Option[Extract] = {
      //println(s"${this.show} << ${t.show}")
      dbg(s"${this} << ${r}")
      
      val d = dfn(r)
      val ret: Option[Extract] = nestDbg { (this, d) match {
        case (_, Ascribe(v,tp)) => // Note: even if 'this' is a Hole, it is needed for term equivalence to ignore ascriptions
          mergeAll(typ extract (tp, Covariant), extractImpl(v))
        case (Ascribe(v,tp), _) =>
          mergeAll(tp extract (d.typ, Covariant), v.extract(r))
          
        case (Hole(name), _) => // Note: will also extract holes... which is important to asses open term equivalence
          // Note: it is not necessary to replace 'extruded' symbols here, since we use Hole's to represent free variables (see case for Abs)
          typ extract (d.typ, Covariant) flatMap { merge(_, (Map(name -> r), Map(), Map())) }
          
        case (BoundVal(n1), Hole(n2)) if n1 == n2 => // This is needed when we do function matching (see case for Abs); n2 is to be seen as a FV
          Some(EmptyExtract)
          //typ.extract(t.typ, Covariant) // I think the types cannot be wrong here (case for Abs checks parameter types)
          
        case (_, Hole(_)) => None
          
        case (v1: BoundVal, v2: BoundVal) =>
          // Bound variables are never supposed to be matched;
          // if we match a binder, we'll replace the bound variable with a free variable first
          throw new AssertionError("Bound variables are not supposed to be matched.")
          //if (v1.name == v2.name) Some(EmptyExtract) else None // check same type?
          
        case (Constant(v1), Constant(v2)) => if (v1 == v2) Some(EmptyExtract) else None
          
        case (a1: Abs, a2: Abs) =>
          // The body of the matched function is recreated with a *free variable* in place of the parameter, and then matched with the
          // body of the matcher, so what the matcher extracts contains potentially (safely) extruded variables.
          for {
            pt <- a1.ptyp extract (a2.ptyp, Contravariant)
            b <- a1.body.extract(a2.inline(rep(a2.param.toHole(a1.param.name)))) // 'a2.param.toHole' is a free variable that 'retains' the memory that it was bound to 'a2.param'
            m <- merge(pt, b)
          } yield m
          
        case (ModuleObject(fullName1,tp1), ModuleObject(fullName2,tp2)) if fullName1 == fullName2 =>
          Some(EmptyExtract) // Note: not necessary to test the types: object with identical paths should be identical

        case Module(pref0, name0, tp0) -> Module(pref1, name1, tp1) =>
          // Note: if prefixes are matchable, module types should be fine
          // If prefixes are different _but_ type is the same, then it should be the same module!
          // TODO also cross-test with ModuleObject, and ideally later MethodApp... 
          pref0 extract pref1 flatMap If(name0 == name1) orElse extractType(tp0,tp1,Invariant)
          
        case (NewObject(tp1), NewObject(tp2)) => tp1 extract (tp2, Covariant)
          
        case (MethodApp(self1,mtd1,targs1,args1,tp1), MethodApp(self2,mtd2,targs2,args2,tp2))
          //if mtd1 == mtd2
          if {debug(s"Comparing ${mtd1.fullName} == ${mtd2.fullName}, ${mtd1 == mtd2}"); mtd1 == mtd2}
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
              //mergeAll( (targs1 zip targs2 zip mtd1.typeParams) map { case ((a,b),p) => a extract (b, Covariant) } )
              mergeAll( (targs1 zip targs2) map { case (a,b) => a extract (b, Covariant) } )
            }
            a <- mergeAll( (args1 zip args2) map { case (as,bs) => extractArgList(as, bs) } )  //oh_and print("[Args:] ") and println
            rt <- tp1 extract (tp2, Covariant)  //oh_and print("[RetType:] ") and println
            m0 <- merge(s, t)
            m1 <- merge(m0, a)
            m2 <- merge(m1, rt)
          } yield m2
          
        //  // TODO?
        //case (or: OtherRep, r) => or extractRep r
        //case (r, or: OtherRep) => or getExtractedBy r
          
        case _ => None
      }
      } // closing nestDbg
      
      //println(s">> ${r map {case(mv,mt,msv) => (mv mapValues (_ show), mt, msv mapValues (_ map (_ show)))}}")
      dbg(s">> $ret")
      ret
    }
    
  }
  
  object RepDef {
    def unapply(x: Rep) = Some(dfn(x))
  }
  
  
  
  /* --- --- --- Node Reinterpretation --- --- --- */
  
  
  def getClassName(cls: sru.ClassSymbol) = RuntimeUniverseHelpers.srum.runtimeClass(cls).getName
  
  /** FIXME handle encoding of multi-param lambdas
    * TODO cache symbols (use forwarder?) */
  trait Reinterpreter {
    val newBase: Base
    def apply(r: Rep): newBase.Rep
    
    protected val bound = mutable.Map[BoundVal, newBase.BoundVal]()
    
    protected def apply(d: Def): newBase.Rep = d match {
      case cnst @ Constant(v) => newBase.const(v)
      case Abs(bv, body) if bv.name == "$BYNAME$" => newBase.byName(apply(body))
      case Abs(bv, body) =>
        bv.typ.tpe match {
          case RecordType(fields) =>
            val params = fields map {case(n,t) => n -> bindVal(n, t)} toMap;
            val adaptedBody = bottomUpPartial(body) {
              case RepDef(RecordGet(RepDef(`bv`), name, _)) => readVal(params(name))
            }
            newBase.lambda(params.valuesIterator map recv toList, apply(adaptedBody))
          case _ =>
            newBase.lambda({ recv(bv)::Nil }, apply(body))
        }
      case MethodApp(self, mtd, targs, argss, tp) =>
        val typ = newBase.loadTypSymbol(getClassName(mtd.owner.asClass))
        val alts = mtd.owner.typeSignature.member(mtd.name).alternatives
        val newMtd = newBase.loadMtdSymbol(typ, mtd.name.toString, if (alts.isEmpty) None else Some(alts.indexOf(mtd)), mtd.isStatic)
        newBase.methodApp(
          apply(self),
          newMtd,
          targs map (t => rect(t)),
          argss map (_.map(newBase)(a => apply(a))),
          rect(tp))
      case ModuleObject(fullName, isPackage) => newBase.moduleObject(fullName, isPackage)
      case Module(pre, name, typ) => newBase.module(apply(pre), name, rect(typ))
      case bv @ BoundVal(name) => newBase.readVal(bound(bv))
      case Ascribe(r,t) => newBase.ascribe(apply(r), rect(t))
      case h @ Hole(n) =>
        //newBase.hole(apply(r), rect(t))
        newBase match {
          case newQuasiBase: QuasiBase => newQuasiBase.hole(n, rect(h.typ).asInstanceOf[newQuasiBase.TypeRep]).asInstanceOf[newBase.Rep] // TODO find better syntax for this crap
          case _ => newBase.asInstanceOf[QuasiBase]; ??? // TODO B/E
        }
    }
    protected def recv(bv: BoundVal) = newBase.bindVal(bv.name, rect(bv.typ)) and (bound += bv -> _)
    def rect(r: TypeRep): newBase.TypeRep = reinterpretType(r, newBase)
    
  }
  object Reinterpreter {
    def apply(NewBase: Base)(app: (Rep, Def => NewBase.Rep) => NewBase.Rep) =
      new Reinterpreter { val newBase: NewBase.type = NewBase; def apply(r: Rep) = app(r, apply) }
  }
  
  
  
  
  
}
























