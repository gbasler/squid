package scp
package quasi2

import utils._
import utils.meta._
import lang2._
import quasi.EmbeddingException

import collection.mutable

/**
  * Builds the embedding in `base` of a piece of type-checked Scala code.
  * Caches the loading of symbols, modules and type applications so they are executed only once.
  * 
  * TODO: move the caching code to a BaseCacher class, and combine it with a BaseForwarder
  * 
  * TODO: would it be possible to write out method apps (like for Var and Imperative) using a quasiquote itself..!? (would need circular macro dependency)
  * 
  * TODO check that expectedType is used whenever possible (dont blindly lift x.tpe instead...)
  * 
  */
abstract class ModularEmbedding[U <: scala.reflect.macros.Universe, B <: Base](val uni: U, val base: B, debug: String => Unit = println) extends UniverseHelpers[U] {
  import uni._
  import base._
  
  
  /** @throws ClassNotFoundException */
  def className(cls: ClassSymbol): String
  
  
  
  def dbg(x: => Any, xs: Any*) = debug((x +: xs) mkString " ")
  
  lazy val (varCls, varModCls, varTypSym) = {
    val varCls = srum.runtimeClass(sru.typeOf[scp.lib.Var[Any]].typeSymbol.asClass).getName
    (varCls, srum.runtimeClass(sru.typeOf[scp.lib.Var.type].typeSymbol.asClass).getName, loadTypSymbol(varCls))
  }
  lazy val scpLibVar = moduleObject("scp.lib.Var", false)
  
  lazy val scpLib = moduleObject("scp.lib.package", false)
  lazy val scpLibTyp =
    //loadTypSymbol(srum.runtimeClass(sru.typeOf[scp.lib.`package`.type].typeSymbol.asClass).getName)
    loadTypSymbol("scp.lib.package$") // ^ equivalent
  
  
  private val typSymbolCache = mutable.HashMap[String, TypSymbol]()
  def loadTypSymbol(fullName: String): TypSymbol =
    typSymbolCache.getOrElseUpdate(fullName, base.loadTypSymbol(fullName))
  
  private val mtdSymbolCache = mutable.HashMap[(TypSymbol, String, Int), MtdSymbol]()
  def loadMtdSymbol(typ: TypSymbol, symName: String, index: Option[Int] = None, static: Boolean = false): MtdSymbol =
    mtdSymbolCache.getOrElseUpdate((typ,symName,index getOrElse 0), base.loadMtdSymbol(typ, symName, index, static))
  
  private val moduleCache = mutable.HashMap[String, Rep]()
  def moduleObject(fullName: String, isPackage: Boolean): Rep =
    moduleCache.getOrElseUpdate(fullName, base.moduleObject(fullName, isPackage))
  
  private val typeCache = mutable.HashMap[Type, TypeRep]()
  
  
  def getTyp(tsym: TypeSymbol): TypSymbol = {
    
    // Note: doesn't work properly to inspect tsym.toType.
    // For example, a refinement won't be matched by `case RefinedType(_, _)` but by `case TypeRef(_, _, _) =>` ...
    
    // In Scala one can call methods on Any that are really AnyRef methods, eg: (42:Any).hashCode
    // Assuming AnyRef for Any may not be perfectly safe, though... (to see)
    val sym = if (tsym.fullName == "scala.Any") AnyRef.typeSymbol else tsym
    
    val cls = className(sym.asClass  /* TODO B-E */)
    
    //debug(s"""Getting type for symbol $tsym -- class "$cls"""")
    loadTypSymbol(cls)
  }
  def getMtd(typ: TypSymbol, mtd: MethodSymbol): MtdSymbol = {
    //debug(s"Getting mtd $mtd "+mtd.isStatic)
    
    val alts =
      //mtd.owner.typeSignature
      mtd.owner.asType.toType
        .member(mtd.name).alternatives
    
    val index = if (alts isEmpty) None else {
      if (mtd.isJava) {
        /** Note: sometimes useless, sometimes not...
          * Java overloading order may NOT be the same here and via runtime reflection...
          * Last I tried, it was indeed different for, e.g. 'lastIndexOf'
          * This is to try and pick the right runtime reflection overloading index.
          * 
          * Note: other possible impl approach: runtimeClass(cls).getName |> Class.forName |> srum.classSymbol */
        
        val runtimeMtd = imp.importSymbol(mtd).asMethod
        dbg("Java method:", runtimeMtd, ":", runtimeMtd.typeSignature)
        
        val runtimeOwner = runtimeMtd.owner.asClass
        val runtimeAlts = runtimeOwner.toType.members.filter(_.name.toString == mtd.name.toString).toSeq
        // ^ Note: other ways of obtaining the list did not work! (eg: .member(TermName(mtd.name)))
        
        /*//
        dbg("CT",alts.map("\n"+_.typeSignature))
        dbg("RT",runtimeAlts.map("\n"+_.typeSignature).toList)
        *///
        
        val ind = runtimeAlts.indexWhere{
          case s if s.isMethod && s.asMethod.paramLists.head.size == mtd.paramLists.head.size =>
            s.asMethod.paramLists.head zip runtimeMtd.paramLists.head forall {
              case (ps, pm) =>
                //dbg("ArgT", ps.typeSignature.typeSymbol.fullName, pm.typeSignature.typeSymbol.fullName)
                ps.typeSignature.typeSymbol.fullName.toString == pm.typeSignature.typeSymbol.fullName.toString
            }
          case _ => false
        }
        
        dbg("Compile-time index:",alts.indexOf(mtd))
        dbg("Runtime index:",ind)
        
        if (ind < 0) {
          System.err.println(s"Could not find Java overload for $mtd:${mtd.typeSignature} by runtime reflection. The overload picked may be the wrong one.")
          Some(alts.indexOf(mtd))
        }
        else Some(ind)
      }
      else Some(alts.indexOf(mtd))
    }
    
    getMtd(typ, mtd.name.toString, index, mtd.isStatic && mtd.isJava)
  }
  def getMtd(typ: TypSymbol, name: String, index: Option[Int] = None, isJavaStatic: Boolean = false): MtdSymbol = {
    loadMtdSymbol(typ, name.toString, index, isJavaStatic)
  }
  
   
  // TODO pull in all remaining cases from Embedding
  /** @throws EmbeddingException */
    def liftTerm(x: Tree, parent: Tree, expectedType: Option[Type], inVarargsPos: Boolean = false)(implicit ctx: Map[TermSymbol, BoundVal]): Rep = {
    def rec(x1: Tree, expTpe: Option[Type])(implicit ctx: Map[TermSymbol, BoundVal]): Rep = liftTerm(x1, x, expTpe)
    
    //debug(List(s"TRAVERSING",x,s"[${x.tpe}]  <:",expectedType getOrElse "?") mkString " ")
    //dbg(s"TRAVERSING",showCode(x),s"[${x.tpe}]  <:",expectedType getOrElse "?")
    
    x match {
      
      case q"" => throw EmbeddingException("Empty program fragment")
        
      case q"""({
        val $$u: $_ = $_
        val $$m: $_ = $_
        $$u.TypeTag.apply[$tpt]($$m, $_)
      }): $_      
      """ => throw EmbeddingException.Unsupported(s"TypeTag construction (for $tpt)")
        
        
      /** --- --- --- Literals --- --- --- */
      case c @ Literal(Constant(x)) => base.const(x)
        
      /** --- --- --- BOUND VARIABLE REFERENCES --- --- --- */
      case Ident(name) if x.symbol.isTerm && (ctx isDefinedAt x.symbol.asTerm) =>
        val ref = readVal(ctx(x.symbol.asTerm))
        
        if (x.symbol.asTerm.isVar) {
          val varTyp = x.symbol.typeSignature
          val mtd = loadMtdSymbol(varTypSym, "$bang", None)
          
          methodApp(ref, mtd, Nil, Nil, liftType(varTyp))
        }
        else ref
        
      /** --- --- --- VARIABLE ASSIGNMENTS --- --- --- */
      case q"$vari = $valu" =>
        val ref = readVal(ctx(vari.symbol.asTerm))
        
        val varTyp = vari.symbol.typeSignature
        val mtd = loadMtdSymbol(varTypSym, "$colon$eq", None)
        
        methodApp(ref, mtd, Nil, Args(rec(valu, Some(vari.symbol.typeSignature)))::Nil, liftType(Unit))
        
      /** --- --- --- VAL/VAR BINDINGS --- --- --- */
      case q"${vdef @ ValDef(mods, name, tpt, rhs)}; ..$b" =>
        assume(!tpt.isEmpty)
        traverseTypeTree(tpt)
        
        val (value, valType) = {
          
          val value = rec(rhs, typeIfNotNothing(vdef.symbol.typeSignature))
          
          /** We used to get the type from vdef's symbol.
            * However, with var virtualization, this symbol refers to the original var def of type T, while the type should be Var[T] */
          //val valType = vdef.symbol.typeSignature
          
          if (mods.hasFlag(Flag.MUTABLE)) {
            val varType = liftType(tpt.tpe) // be careful not to use the type of the initial value
            
            val mtd = loadMtdSymbol(loadTypSymbol(varModCls), "apply")
            
            //val tp = typeApp(moduleObject("scp.lib.package", false), varTypSym, varType :: Nil)
            val tp = typeApp(scpLib, varTypSym, varType :: Nil)
            methodApp(scpLibVar, mtd, varType :: Nil, Args(value)::Nil, tp) -> tp
          }
          else value -> liftType(rhs.tpe) // Q: is it really sound to take the value's type? (as opposed to the declared one) -- perhaps we'd also need to annotate uses...
        }
        
        val bound = bindVal(name.toString, valType)
        
        val body = rec(
          internal.setType( q"..$b",x.tpe ), // doing this kind of de/re-structuring loses the type
          expectedType)(ctx + (vdef.symbol.asTerm -> bound))
        
        letin(bound, value, body, liftType(expectedType getOrElse x.tpe))
        
        
      /** --- --- --- MODULE REFERENCES --- --- --- */
      // Note: when testing 'x.tpe.termSymbol.isModule', we sometimes ended up applying this case on some type parameters!
      case q"$pre.${name}" if x.symbol != null && x.symbol.isModule =>
        //dbg("MODULE",x,x.symbol,x.tpe,x.tpe.typeSymbol,x.tpe.typeSymbol.isModuleClass)
        if (x.tpe.typeSymbol.isStatic) liftModule(x.symbol.asModule.moduleClass.asType.toType)
        else module(rec(pre, Some(pre.tpe)), name.toString, liftType(x.tpe)) // TODO use expectedType?
        
        
      case q"${Ident(name: TermName)}" if x.symbol != null && x.symbol.isModule =>
        if (x.tpe.typeSymbol.isStatic) liftModule(x.symbol.asModule.moduleClass.asType.toType)
        else throw EmbeddingException.Unsupported("Non-qualified, non-static module reference")
        
        
      /** --- --- --- METHOD APPLICATIONS --- --- --- */
      case SelectMember(obj, f) =>
        
        val method = x.symbol.asMethod // FIXME safe?
        
        debug("Dsl feature: "+method.fullName+" : "+method.info)
        
        //debug("OBJ "+obj.tpe.typeSymbol)
        
        def refMtd = getMtd(getTyp(obj.tpe.typeSymbol.asType), method)
        
        val tp = liftType(x.tpe)
        val self = liftTerm(obj, x, Some(obj.tpe))
        
        x match {
          case q"$a.$_" =>
            
            methodApp(self, refMtd, Nil, Nil, tp)
            
            
          case MultipleTypeApply(_, targs, argss) =>
            
            targs foreach traverseTypeTree
            
            val targsTree = targs map (tp => liftType(tp.tpe))
            
            object VarargParam {
              def unapply(param: Type): Option[Type] = param match {
                case TypeRef(_, RepeatedParamClass, tp :: Nil) => Some(tp)
                case _ => None
              }
            }
            
            def mkArgs(acc: List[Rep]) = Args(acc.reverse: _*)
            
            /** Note that in Scala, vararg parameters cannot be by-name, so we don't check for it */
            def processArgs(acc: List[Rep])(args_params: (List[Tree], Stream[Type])): ArgList = args_params match {
                
              case ((a @ q"$t : _*") :: Nil, Stream(VarargParam(pt))) =>
                //debug(s"vararg splice [$pt]", t)
                val sym = symbolOf[scala.collection.Seq[_]]
                mkArgs(acc) splice rec(t, Some(internal.typeRef(internal.thisType(sym.owner), sym, pt :: Nil))) // ArgsVarargSpliced
                
              case ((a @ q"$t : _*") :: Nil, pts) =>
                throw EmbeddingException(s"Vararg splice unexpected in that position: ${showCode(a)}")
                
              case (as, Stream(VarargParam(pt))) =>
                //debug(s"vararg [$pt]", as)
                ArgsVarargs(mkArgs(acc), Args(as map { a => liftTerm(a, parent, Some(pt), inVarargsPos = true) }: _*))
                
              case (a :: as, pt #:: pts) =>
                pt match {
                  case TypeRef(_, ByNameParamClass, pt :: Nil) =>
                            processArgs(byName(rec(a, Some(pt))) :: acc)(as, pts)
                  case _ => processArgs(                 rec(a, Some(pt))    :: acc)(as, pts)
                }
              case (Nil, Stream.Empty) => mkArgs(acc)
              case (Nil, pts) if !pts.hasDefiniteSize => mkArgs(acc)
                
              //case _ => // TODO B/E
            }
            
            
            val args = (argss zip (f.tpe.paramLists map (_.toStream map (_ typeSignature)))) map processArgs(Nil)
            
            methodApp(self, {refMtd}, targsTree, args, tp)
            
            
        }
        
      /** --- --- --- LAMBDAS --- --- --- */
      case q"(..$ps) => $bo" =>
        val (params, bindings) = ps map {
          case p @ ValDef(mods, name, tpt, _) =>
            traverseTypeTree(tpt)
            val bv = bindVal(name.toString, liftType(p.symbol.typeSignature))
            bv -> (p.symbol.asTerm -> bv)
        } unzip;
        
        val body = rec(bo, typeIfNotNothing(bo.tpe) orElse expectedType flatMap FunctionType.unapply)(ctx ++ bindings)
        lambda(params, body)
        
        
      /** --- --- --- IMPERATIVE STATEMENTS --- --- --- */
      case q"$s; ..$sts; $r" =>
        
        var (hs, tl) = (s :: Nil, sts)
        while (tl.nonEmpty && !tl.head.isDef) {
          hs ::= tl.head
          tl = tl.tail
        }
        
        val effects = Args()(hs.reverse map (t => rec(t, Some(Any))): _*)
        
        val body = rec(internal.setType( q"..$tl; $r",x.tpe ), // doing this kind of de/re-structuring loses the type
          expectedType)
        
        val argss = effects :: Args(body) :: Nil
        
        val imp = getMtd(scpLibTyp, "Imperative")
        val retTyp = liftType(x.tpe)
        
        methodApp(scpLib, imp, retTyp::Nil, argss, retTyp)
        
        
      /** --- --- --- IF THEN ELSE --- --- --- */
      case q"if ($cond) $thn else $els" =>
        
        val branchTp = lub(thn.tpe :: els.tpe :: Nil) // x.tpe will have the type of whatever was expected in place of the ITE (eg: Unit, even if the branches are Int)
        val tp = typeIfNotNothing(branchTp) orElse expectedType
        val retTyp = liftType(tp getOrElse branchTp)
        
        val mtd = getMtd(scpLibTyp, "IfThenElse")
        methodApp(scpLib, mtd, retTyp::Nil, Args(
          rec(cond, Some(Boolean)),
          byName(rec(thn, tp)),
          byName(rec(els, tp))
        ) :: Nil, retTyp)
        
      /** --- --- --- WHILE LOOPS --- --- --- */
      case q"while ($cond) $loop" =>
        val mtd = getMtd(scpLibTyp, "While")
        val retTyp = liftType(Unit)
        methodApp(scpLib, mtd, Nil, Args(
          byName(rec(cond, Some(Boolean))),
          byName(rec(loop, typeIfNotNothing(loop.tpe) orElse expectedType))
        ) :: Nil, retTyp)
        
      /** --- --- --- TYPE ASCRIPTIONS --- --- --- */
      case q"$t: $typ" =>
        val tree = liftTerm(t, x, Some(typ.tpe)) // Executed before `traverseTypeTree(typ)` to keep syntactic order!
        traverseTypeTree(typ)
        ascribe(tree, liftType(typ.tpe))
      
        
      /** --- --- --- STUPID BLOCK --- --- --- */
      case Block(Nil, t) => liftTerm(t, x, expectedType)  // Note: used to be q"{ $t }", but that turned out to also match things not in a block!
        
        
      /** --- --- --- ERRORS --- --- --- */
        
      case q"${s: DefDef}; ..$_" =>
        throw EmbeddingException.Unsupported(s"Definition '$s'")
        
      case _: DefDef =>
        throw EmbeddingException("Statement in expression position: "+x/*+(if (debug.debugOptionEnabled) s" [${x.getClass}]" else "")*/)
        
      case _ => throw EmbeddingException.Unsupported(""+x/*+(if (debug.debugOptionEnabled) s" [${x.getClass}]" else "")*/)
        
    }
  }
  
  
  /** wide/deal represent whether we have already tried to widened/dealias this type */
  final def liftType(tp: Type, wide: Boolean = false, deal: Boolean = false): TypeRep = {
    val dtp = tp//.dealias
    typeCache.getOrElseUpdate(dtp, liftTypeUncached(dtp, wide, deal))
  }
  
  /** Note: we currently use `moduleType` for types that reside in packages and for objects used as modules...
    * Note: widening may change Int(0) to Int (wanted) but also make 'scala' (as in 'scala.Int') TypeRef-matchable !! */
  def liftTypeUncached(tp: Type, wide: Boolean, deal: Boolean): TypeRep = typeCache.getOrElseUpdate(tp, (debug(s"Mathing type $tp")) before tp match {
    //case _ if tp =:= Any =>
    //  // In Scala one can call methods on Any that are really AnyRef methods, eg: (42:Any).hashCode
    //  // Assuming AnyRef for Any not be perfectly safe, though... (to see)
    //  liftType(AnyRef)
  
    case _ if tp.asInstanceOf[scala.reflect.internal.Types#Type].isErroneous => // `isErroneous` seems to return true if any sub-component has `isError`
      throw EmbeddingException(s"Internal error: type `$tp` contains an error...")
      
    case ExistentialType(syms, typ) =>
      // TODO still allow implicit search (only prevent making a type tag of it)
      throw EmbeddingException.Unsupported(s"Existential type '$tp'")
      /*
      // TODO at least warn properly...
      val symSet = syms.toSet
      val etp = typ.map{case TypeRef(_,s,Nil) if symSet contains s => Any case t => t}
      System.err.println(s"Warning: Erased existential `$tp` to `$etp`")
      liftType(etp)
      */

    case SingleType(pre, sym) if sym.isStatic && sym.isModule => // TODO understand diffce with ThisType 
      staticModuleType(sym.fullName)
      
    case tpe @ RefinedType(tp :: Nil, scp) if scp.isEmpty =>
      liftType(tp)
      
    case tpe @ RefinedType(tpes, scp) if !(tpe <:< typeOf[QuasiBase.`<extruded type>`]) =>
      debug(s"Detected refinement: $tpes, $scp")
      throw EmbeddingException.Unsupported(s"Refinement type '$tpe'")
      
    case TypeRef(prefix, sym, targs) if tp.typeSymbol.isClass && !(tp <:< typeOf[QuasiBase.`<extruded type>`]) =>
      //dbg(s"sym: $sym;", "tpsym: "+tp.typeSymbol)
      dbg(s"TypeRef($prefix, $sym, $targs)")
      
      try {
        val tsym = getTyp(tp.typeSymbol.asType) // may throw ClassNotFoundException
        
        val self = liftModule(prefix)
        
        typeApp(self, tsym, targs map (t => liftType(t)))
        // ^ Note: getTyp(sym.asType) did not always work...
        
      } catch {
        case e: ClassNotFoundException =>
          // Types like `scala.Any` do not have an associated class (though Any is currently already handled in the case above)
          unknownTypefallBack(tp)
      }

    case ConstantType(Constant(v)) => constType(v, liftType(tp.widen))
      
    //case _ if tp.typeSymbol.isModuleClass =>
    case TypeRef(prefix, sym, targs) if tp.typeSymbol.isModuleClass =>
      assert(false)
      ???
      //typeApp(liftModule(tp.typeSymbol.owner.asType.toType), getTyp(tp.typeSymbol), Nil)
      
    case _ => // TODO verify no holes in 'tp'! If holes, try widening first
      
      (wide, deal) match { // Note: could also compare if the widened/dealiased is == (could be more efficient, cf less needless recursive calls)
        case (false, _) => liftType(tp.widen, true, deal)
        case (true, false) => liftType(tp.widen, true, true)
        case (true, true) =>
          debug(s"Unknown type, falling back: $tp")
          unknownTypefallBack(tp)
      }
      
  })
  
  def liftModule(tp: Type): Rep = tp.dealias match {
    case ThisType(sym) =>
      // FIXME handle non-stable `this` types, by making a hole!
      assert(sym.isStatic) // TODO BE
      moduleObject(sym.fullName, sym.isPackage)
    case SingleType(pre,sym) =>
      //dbg(tp,tp.dealias,tp.widen,pre,sym)
      /*
      assert(sym.isStatic, s"Symbol $sym is not static.") // TODO BE
      //assert(sym.isPackage && sym.isPackageClass && sym.isModule) // fails
      moduleObject(sym.fullName, sym.isPackage)
      */
      if (sym.isStatic)
        moduleObject(sym.fullName, sym.isPackage)
      else liftModule(tp.widen) // TODO prevent infloop


    case TypeRef(pre,sym,targs) =>
      //dbg(pre,sym,targs)
      //dbg(">>>>>>>",sym,sym.isStatic)
      assert(sym.isStatic, s"Symbol $sym is not static.") // TODO BE
      moduleObject(sym.fullName, sym.isPackage)
      
  }
  
  def unknownTypefallBack(tpe: Type) = {
    val tag = mkTag(tpe)
    uninterpretedType(tag)
  }
  
  
  def traverseTypeTree(tpt: Tree) = ()
  
  
  def apply(code: Tree, expectedType: Option[Type] = None) = {
    
    liftTerm(code, code, expectedType  orElse Some(code.tpe))(Map())
    
  }
  
  
  
  
  
  
}
















