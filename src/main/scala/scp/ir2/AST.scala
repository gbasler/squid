package scp
package ir2

import lang2._
import utils._
import utils.CollectionUtils._
import utils.meta.{RuntimeUniverseHelpers => ruh}


/** 
  * TODO: more efficent online rewriting by pre-partitioning rules depending on what they can match!
  * 
  **/
trait AST extends InspectableBase with ScalaTyping with ASTReinterpreter with RuntimeSymbols with ClassEmbedder with ASTHelpers { self =>
  
  
  /* --- --- --- Required defs --- --- --- */
  
  def rep(dfn: Def): Rep
  def dfn(r: Rep): Def
  
  
  
  /* --- --- --- Provided defs --- --- --- */
  
  
  /** Invoked when the Rep is only transitory and not supposed to be kept around in the AST, as in for pattern matching a Def with QQs */
  def simpleRep(dfn: Def): Rep = rep(dfn)
  
  def readVal(v: BoundVal): Rep = rep(v)
  
  def hole(name: String, typ: TypeRep) = rep(Hole(name)(typ))
  def splicedHole(name: String, typ: TypeRep): Rep = rep(SplicedHole(name)(typ))
  
  def const(value: Any): Rep = rep(Constant(value))
  def bindVal(name: String, typ: TypeRep, annots: List[Annot]) = new BoundVal(name)(typ, annots)
  def freshBoundVal(typ: TypeRep) = BoundVal(freshName)(typ, Nil) oh_and (varCount += 1)
  protected def freshNameImpl(n: Int) = "val$"+n
  protected final def freshName: String = freshNameImpl(varCount)
  private var varCount = 0
  
  /** AST does not implement `lambda` and only supports one-parameter lambdas. To encode multiparameter-lambdas, consider mixing in CurryEncoding */
  def abs(param: BoundVal, body: => Rep): Rep = rep(Abs(param, body)(lambdaType(param.typ::Nil, body.typ)))
  
  override def ascribe(self: Rep, typ: TypeRep): Rep = if (self.typ =:= typ) self else rep(self match {
    case RepDef(Ascribe(trueSelf, _)) => Ascribe(trueSelf, typ) // Hopefully Scala's subtyping is transitive
    case _ => Ascribe(self, typ)
  })
  
  def newObject(tp: TypeRep): Rep = rep(NewObject(tp))
  def staticModule(fullName: String): Rep = rep({
    StaticModule(fullName: String)
  })
  def module(prefix: Rep, name: String, typ: TypeRep): Rep = rep(Module(prefix, name, typ))
  def methodApp(self: Rep, mtd: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep =
    rep(MethodApp(self, mtd, targs, argss, tp))
  
  def byName(arg: => Rep): Rep =
    lambda(bindVal("$BYNAME$", uninterpretedType[Unit], Nil) :: Nil, arg) // FIXME proper impl  TODO use annot
  // TODO
  // We encode thunks (by-name parameters) as functions from some dummy 'ThunkParam' to the result
  //def byName(arg: => Rep): Rep = dsl"(_: lib.ThunkParam) => ${Quote[A](arg)}".rep
  //def byName(arg: => Rep): Rep = ??? //lambda(Seq(freshBoundVal(typeRepOf[lib.ThunkParam])), arg)
  
  def recordGet(self: Rep, name: String, typ: TypeRep) = RecordGet(self, name, typ)
  
  
  def inline(param: BoundVal, body: Rep, arg: Rep) = bottomUp(body) {
    case RepDef(`param`) => arg
    case r => r
  }
  
  override def substituteLazy(r: Rep, defs: Map[String, () => Rep]): Rep = if (defs isEmpty) r else bottomUp(r) { r => dfn(r) match {
    case h @ Hole(n) => defs get n map (_()) getOrElse r
    case h @ SplicedHole(n) => defs get n map (_()) getOrElse r
    case _ => r
  }}
  override def substitute(r: Rep, defs: Map[String, Rep]): Rep = if (defs isEmpty) r else bottomUp(r) { r => dfn(r) match {
    case h @ Hole(n) => defs getOrElse (n, r)
    case h @ SplicedHole(n) => defs getOrElse (n, r)
    case _ => r
  }}
  
  def mapDef(f: Def => Def)(r: Rep) = {
    val d = dfn(r)
    val newD = f(d)
    if (newD eq d) r else rep(newD)
  }
  
  def transformRep(r: Rep)(pre: Rep => Rep, post: Rep => Rep = identity): Rep = (new RepTransformer(pre,post))(r)
  class RepTransformer(pre: Rep => Rep, post: Rep => Rep) {
    
    //def apply(r: Rep): Rep = r |> pre |> dfn |> apply |> rep |> post
    /* Optimized version that prevents recreating too many Rep's: */
    def apply(_r: Rep): Rep = {
      val r = pre(_r)
      post(r |> mapDef(apply))
    }
    
    def apply(d: Def): Def = {
      //println(s"Traversing $r")
      val rec: Rep => Rep = apply
      val ret = d match {
        case a @ Abs(p, b) =>
          val newB = rec(b)
          if (newB eq b) d else Abs(p, newB)(a.typ) // Note: we do not transform the parameter; it could lead to surprising behaviors (esp. in case of erroneous transform)
        case Ascribe(r,t) => 
          val newR = rec(r)
          if (newR eq r) d else Ascribe(newR,t)
        case Hole(_) | SplicedHole(_) | NewObject(_) => d
        case StaticModule(fullName) => d
        case Module(pref, name, typ) =>
          val newPref = rec(pref)
          if (newPref eq pref) d else Module(newPref, name, typ)
        case RecordGet(se, na, tp) => RecordGet(rec(se), na, tp)
        case MethodApp(self, mtd, targs, argss, tp) =>
          val newSelf = rec(self)
          var sameArgs = true
          def tr2 = (r: Rep) => {
            //if (r.asInstanceOf[ANF#Rep].uniqueId == 125)
            //  42
            val newR = rec(r)
            if (!(newR eq r))
              sameArgs = false
            newR
          }
          def trans(args: Args) = Args(args.reps map tr2: _*)
          val newArgss = argss map { // TODO use List.mapConserve in Args if possible?
            case as: Args => trans(as)
            case ArgsVarargs(as, vas) => ArgsVarargs(trans(as), trans(vas))
            case ArgsVarargSpliced(as, va) => ArgsVarargSpliced(trans(as), tr2(va))
          }
          if ((newSelf eq self) && sameArgs) d else MethodApp(newSelf, mtd, targs, newArgss, tp)
        case v: BoundVal => v
        case Constant(_) => d
        //case or: OtherRep => or.transform(f) // TODO?
      }
      //println(s"Traversed $r, got $ret")
      ret
    }
  }
  
  def bottomUp(r: Rep)(f: Rep => Rep): Rep = transformRep(r)(identity, f)
  def topDown(r: Rep)(f: Rep => Rep): Rep = transformRep(r)(f)
  
  //def bottomUpPartial(r: Rep)(f: PartialFunction[Rep, Rep]): Rep = bottomUp(r)(r => f applyOrElse (r, identity[Rep]))
  
  
  
  
  protected def extract(xtor: Rep, xtee: Rep): Option[Extract] = dfn(xtor) extractImpl xtee
  
  //def spliceExtract(xtor: Rep, t: Args): Option[Extract] = ???
  protected def spliceExtract(xtor: Rep, args: Args): Option[Extract] = xtor match {
    case RepDef(SplicedHole(name)) => Some(Map(), Map(), Map(name -> args.reps))
    case RepDef(h @ Hole(name)) => // If we extract ($xs*) using ($xs:_*), we have to build a Seq in the object language and return it
      //val rep = methodApp(
      //  moduleObject("scala.collection.Seq", SimpleTypeRep(ru.typeOf[Seq.type])),
      //  loadSymbol(true, "scala.collection.Seq", "apply"),
      //  h.typ.asInstanceOf[ScalaTypeRep].targs.head :: Nil, // TODO check; B/E
      //  Args()(args.reps: _*) :: Nil, h.typ)
      //Some(Map(name -> rep), Map(), Map())
      //Seq(12)
      
      val tp = ruh.sru.lub(args.reps map (_.typ.tpe) toList)
      
      val rep = methodApp(
        staticModule("scala.collection.Seq"),
        loadMtdSymbol(
          //loadTypSymbol("scala.collection.Seq"),
          loadTypSymbol("scala.collection.generic.GenericCompanion"),
          "apply", None),
        tp :: Nil,
        Args()(args.reps: _*) :: Nil, staticTypeApp(loadTypSymbol("scala.collection.Seq"), tp :: Nil))
      Some(Map(name -> rep), Map(), Map())
    case _ => throw IRException(s"Trying to splice-extract with invalid extractor $xtor")
  }
  
  
  /**
  * Note: Works well with FVs (represented as holes),
  * since it checks that each extraction extracts exactly a hole with the same name
  */
  def repEq(a: Rep, b: Rep): Boolean = {
    val a_e_b = a extractRep b
    if (a_e_b.isEmpty) return false
    (a_e_b, b extractRep a) match {
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

  
  case class Lowering(phases: Symbol*) extends ir2.Lowering with SelfTransformer {
    val loweredPhases = phases.toSet
  }
  class Desugaring extends Lowering('Sugar)
  object Desugaring extends Desugaring
  
  
  object Const extends ConstAPI {
    import meta.RuntimeUniverseHelpers.sru
    def unapply[T: sru.TypeTag](ir: IR[T,_]): Option[T] = dfn(ir.rep) match {
      case cst @ Constant(v) if cst.typ <:< sru.typeTag[T].tpe => Some(v.asInstanceOf[T])
      case _ => None
    }
  }
  
  
  /* --- --- --- Node Definitions --- --- --- */
  
  
  lazy val ExtractedBinderSym = loadTypSymbol(ruh.encodedTypeSymbol(ruh.sru.typeOf[scp.lib.ExtractedBinder].typeSymbol.asType))
  
  type Val = BoundVal
  case class BoundVal(name: String)(val typ: TypeRep, val annots: List[Annot]) extends Def {
    def isExtractedBinder = annots exists (_._1.tpe.typeSymbol === ExtractedBinderSym)
    
    def toHole(model: BoundVal): Extract -> Hole = {
      val newName = model.name
      val extr: Extract =
        //if (model.annots exists (_._1.tpe.typeSymbol === ExtractedBinderSym)) (Map(newName -> rep(this)),Map(),Map())
        if (model isExtractedBinder) (Map(newName -> readVal(this)),Map(),Map())
        else EmptyExtract
      extr -> Hole(newName)(typ, Some(this))
    }
    override def equals(that: Any) = that match { case that: AnyRef => this eq that  case _ => false }
    //override def hashCode(): Int = name.hashCode // should be inherited
  }
  def boundValType(bv: BoundVal) = bv.typ
  
  /**
  * In xtion, represents an extraction hole
  * In ction, represents a free variable
  * TODO Q: should holes really be pure?
  */
  case class Hole(name: String)(val typ: TypeRep, val originalSymbol: Option[BoundVal] = None) extends NonTrivialDef
  case class SplicedHole(name: String)(val typ: TypeRep) extends NonTrivialDef
  
  case class Constant(value: Any) extends Def {
    lazy val typ = value match {
      case () => TypeRep(ruh.Unit)
      case cls: Class[_] => // Runtime classes are considered like constants, for some reason
        val tp = ruh.srum.classSymbol(cls).toType
        val clsSym = ruh.srum.classSymbol(cls.getClass)
        staticTypeApp(loadTypSymbol(ruh.encodedTypeSymbol(clsSym)), tp :: Nil)
      case _ => constType(value)
    }
  }
  case class Abs(param: BoundVal, body: Rep)(val typ: TypeRep) extends Def {
    def ptyp = param.typ
    //val typ = funType(ptyp, repType(body))
    
    // FIXME handle multi-param Abs...
    def inline(arg: Rep) = bottomUpPartial(body) { //body withSymbol (param -> arg)
      case rep if dfn(rep) === `param` => arg
    }
    
  }
  
  case class Ascribe(self: Rep, typ: TypeRep) extends Def
  
  case class NewObject(typ: TypeRep) extends NonTrivialDef
  
  case class StaticModule(fullName: String) extends Def {
    lazy val typ = staticModuleType(fullName)
  }
  
  case class MethodApp(self: Rep, sym: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], typ: TypeRep) extends NonTrivialDef {
    
    lazy val phase = { // TODO cache annotations for each sym
      import ruh.sru._
      sym.annotations.iterator map (_ tree) collectFirst {
        case q"new $tp(scala.Symbol.apply(${Literal(ruh.sru.Constant(name:String))}))" if tp.symbol.fullName == "scp.quasi2.phase" =>
          Symbol(name)
      }
    }
    
  }
  case class Module(prefix: Rep, name: String, typ: TypeRep) extends Def
  
  //case class Record(fields: List[(String, Rep)]) extends Def { // TODO
  //  val typ = RecordType
  //}
  case class RecordGet(self: Rep, name: String, typ: TypeRep) extends Def
  
  def isPure(d: Def) = d match {
    case _: MethodApp => false
    case Hole(_)|SplicedHole(_) => false
    case _ => true
  }
  
  sealed trait NonTrivialDef extends Def { override def isTrivial: Bool = false }
  sealed trait Def { // Q: why not make it a class with typ as param?
    val typ: TypeRep
    def isTrivial = true
    
    lazy val isPureDef = self.isPure(this)  // TODO rename to isPure
    
    def extractImpl(r: Rep): Option[Extract] = {
      //println(s"${this.show} << ${t.show}")
      //dbgs(s"${this} << ${r}")
      dbgs(s"${Console.BOLD}M${Console.RESET}  "+this);dbgs("<< "+r)
      //dbgs("   "+this);dbgs("<< "+r.show)
      //dbgs(s"${showRep(rep(this))} << ${showRep(r)}")  // may create mayhem, as showRep may use scalaTree, which uses a Reinterpreter that has quasiquote patterns!
      
      val d = dfn(r)
      val ret: Option[Extract] = nestDbg { (this, d) match {
          
        case (_, Ascribe(v,tp)) => // Note: even if 'this' is a Hole, it is needed for term equivalence to ignore ascriptions
          //mergeOpt(typ extract (tp, Covariant), extractImpl(v))
          /** ^ It should not matter what the matchee is ascribed to. We'd like `42` and `42:Any` to be equi-matchable */
          extractImpl(v)
          
        case (Ascribe(v,tp), _) =>
          mergeOpt(tp extract (d.typ, Covariant), v.extract(r))
          
        case (Hole(name), _) => // Note: will also extract holes... which is important to asses open term equivalence
          // Note: it is not necessary to replace 'extruded' symbols here, since we use Hole's to represent free variables (see case for Abs)
          typ extract (d.typ, Covariant) flatMap { merge(_, (Map(name -> r), Map(), Map())) }
          
        case (BoundVal(n1), Hole(n2)) if n1 == n2 => // This is needed when we do function matching (see case for Abs); n2 is to be seen as a FV
          Some(EmptyExtract)
          //typ.extract(t.typ, Covariant) // I think the types cannot be wrong here (case for Abs checks parameter types)
          
        case (bv: BoundVal, h: Hole) if h.originalSymbol exists (_ === bv) => // This is needed when we match an extracted binder with a hole that comes from that binder
          Some(EmptyExtract)
          
        case (_, Hole(_)) => None
          
        case (v1: BoundVal, v2: BoundVal) =>
          // Bound variables are never supposed to be matched;
          // if we match a binder, we'll replace the bound variable with a free variable first
          //throw new AssertionError("Bound variables are not supposed to be matched.")
          
          // actually now with extracted bindings they may be...
          if (v1 == v2) Some(EmptyExtract) else None // check same type?
          
          //if (v1.name == v2.name) Some(EmptyExtract) else None // check same type?
          
        /** It may be okay to consider constants `1` (Int) and `1.0` (Double) equivalent;
          * however, it's probably a good idea to ensure transitivity of the match-relation, so that (a <~ b and b <~ c) => a <~ c
          * but we'd have 1 <~ 1.0 and 1.0 <~ ($x:Double) but not 1 <~ ($x:Double) */
        //case (Constant(v1), Constant(v2)) => if (v1 == v2) Some(EmptyExtract) else None
        case (Constant(v1), Constant(v2)) => mergeOpt(extractType(typ, d.typ, Covariant), if (v1 == v2) Some(EmptyExtract) else None)
          
        case (a1: Abs, a2: Abs) =>
          // The body of the matched function is recreated with a *free variable* in place of the parameter, and then matched with the
          // body of the matcher, so what the matcher extracts contains potentially (safely) extruded variables.
          for {
            pt <- a1.ptyp extract (a2.ptyp, Contravariant)
            //b <- a1.body.extract(a2.inline(rep(a2.param.toHole(a1.param.name))))
            (hExtr,h) = a2.param.toHole(a1.param)
            b <- a1.body.extract(a2.inline(rep(h))) flatMap (merge(hExtr, _)) // 'a2.param.toHole' is a free variable that 'retains' the memory that it was bound to 'a2.param'
            m <- merge(pt, b)
          } yield m
          
        case (StaticModule(fullName1), StaticModule(fullName2)) if fullName1 == fullName2 =>
          Some(EmptyExtract) // Note: not necessary to test the types: object with identical paths should be identical

        case Module(pref0, name0, tp0) -> Module(pref1, name1, tp1) =>
          // Note: if prefixes are matchable, module types should be fine
          // If prefixes are different _but_ type is the same, then it should be the same module!
          // TODO also cross-test with ModuleObject, and ideally later MethodApp... 
          pref0 extract pref1 flatMap If(name0 == name1) orElse extractType(tp0,tp1,Invariant)
          
        case (NewObject(tp1), NewObject(tp2)) => tp1 extract (tp2, Covariant)
          
        case (MethodApp(self1,mtd1,targs1,args1,tp1), MethodApp(self2,mtd2,targs2,args2,tp2))
          //if mtd1 == mtd2
          //if {val r = mtd1 == mtd2; debug(s"Symbol: ${mtd1.fullName} ${if (r) "===" else "=/="} ${mtd2.fullName}"); r}
          if mtd1 === mtd2 || { debug(s"Symbol: ${mtd1.fullName} =/= ${mtd2.fullName}"); false }
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
          
            /** It should not be necessary to match return types, knowing that we already match all term and type arguments.
              * On the other hand, it might break legitimate things like (()=>42).apply():Any =~= (()=>42:Any).apply(),
              * in which case the return type of .apply is Int inthe former and Any in the latter, but everything else is equivalent */
            //rt <- tp1 extract (tp2, Covariant)  //oh_and print("[RetType:] ") and println
            rt = EmptyExtract
          
            m0 <- merge(s, t)
            m1 <- merge(m0, a)
            m2 <- merge(m1, rt)
          } yield m2

        case RecordGet(s0,n0,t0) -> RecordGet(s1,n1,t1) if n0 == n1 =>
          s0 extract s1
          
        //  // TODO?
        //case (or: OtherRep, r) => or extractRep r
        //case (r, or: OtherRep) => or getExtractedBy r
          
        case _ => None
      }
      } // closing nestDbg
      
      //println(s">> ${r map {case(mv,mt,msv) => (mv mapValues (_ show), mt, msv mapValues (_ map (_ show)))}}")
      dbgs(s">> $ret")
      ret
    }
    
    override def toString = prettyPrint(this)
  }
  
  object RepDef {
    def unapply(x: Rep) = Some(dfn(x))
  }
  
  
  
  
  
  object Typed { def unapply(r: Def): Some[(Def, TypeRep)] = Some(r,r.typ) }
  
}











