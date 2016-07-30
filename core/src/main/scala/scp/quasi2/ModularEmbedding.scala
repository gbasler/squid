package scp
package quasi2

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
  def liftTerm(x: Tree, parent: Tree, expectedType: Option[Type])(implicit ctx: Map[TermSymbol, BoundVal]): Rep = {
    def rec(x1: Tree, expTpe: Option[Type])(implicit ctx: Map[TermSymbol, BoundVal]): Rep = liftTerm(x1, x, expTpe)
    
    //debug(List(s"TRAVERSING",x,s"[${x.tpe}]  <:",expectedType getOrElse "?") mkString " ")
    
    x match {
      
      case q"" => throw EmbeddingException("Empty program fragment")
        
      /** --- --- --- Literals --- --- --- */
      case c @ Literal(Constant(x)) =>
        base.const(x)(mkTag(c.tpe))
        
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
        
        methodApp(ref, mtd, Nil, Args(rec(valu, Some(vari.symbol.typeSignature)))::Nil, liftType(varTyp))
        
      /** --- --- --- VAL/VAR BINDINGS --- --- --- */
      case q"${vdef @ ValDef(mods, name, tpt, rhs)}; ..$b" =>
        assume(!tpt.isEmpty)
        traverseTypeTree(tpt)
        
        val (value, valType) = {
          
          val value = rec(rhs, typeIfNotNothing(vdef.symbol.typeSignature))
          
          /** We used to get the type from vdef's symbol.
            * However, with var virtualization, this symbol refers to the original var def of type T, while the type should be Var[T] */
          //val valType = vdef.symbol.typeSignature
          val valType = liftType(rhs.tpe)
          
          if (mods.hasFlag(Flag.MUTABLE)) {
            
            val mtd = loadMtdSymbol(loadTypSymbol(varModCls), "apply")
            
            val tp = typeApp(moduleObject("scp.lib.package", false), varTypSym, valType::Nil)
            methodApp(moduleObject("scp.lib.package.Var", false), mtd, Nil, Args(value)::Nil, tp) -> tp
          }
          else value -> valType
        }
        
        val bound = bindVal(name.toString, valType)
        
        val body = rec(
          internal.setType( q"..$b",x.tpe ), // doing this kind of de/re-structuring loses the type
          expectedType)(ctx + (vdef.symbol.asTerm -> bound))
        
        letin(bound, value, body, liftType(x.tpe))
        
        
      /** --- --- --- MODULE REFERENCES --- --- --- */
      case _ if x.symbol != null && x.symbol.isModule => // Note: when testing 'x.tpe.termSymbol.isModule', we sometimes ended up applying this case on some type parameters!
        //debug(List(">>", x.tpe, x.tpe <:< typeOf[AnyRef], x.symbol.typeSignature, x.symbol.typeSignature <:< typeOf[AnyRef]) mkString " ")
        
        /*if (x.symbol.isJava) {
          assume(!(x.symbol.typeSignature <:< typeOf[AnyRef]))
          moduleObject(x.symbol.fullName)
        } else {
          val objName = x.tpe match { case SingleType(tp,sym) => s"${tp.typeSymbol.fullName}.${sym.name}" }
          moduleObject(objName)
        }*/
        moduleObject(x.symbol.fullName, x.symbol.isPackage)
        
        
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
                t match {
                  //case q"$base.spliceVararg[$t,$scp]($idts)" => //if base.tpe == Base.tpe => // TODO make that an xtor  // note: 'base equalsStructure Base' is too restrictive/syntactic
                    /*
                    val splicedX = recNoType(q"$Base.unquote[$t,$scp]($$x$$)")
                    //q"${mkArgs(acc)}($idts map (($$x$$:$Base.Q[$t,$scp]) => $splicedX): _*)" // ArgsVarargs creation using Args.apply
                    mkArgs(acc)(idts map (($$x$$:$Base.Q[$t,$scp]) => $splicedX): _*)" // ArgsVarargs creation using Args.apply
                    */
                    //???
                  case _ =>
                    val sym = symbolOf[scala.collection.Seq[_]]
                    //q"${mkArgs(acc)} splice ${rec(t, Some(internal.typeRef(internal.thisType(sym.owner), sym, pt :: Nil)))}" // ArgsVarargSpliced
                    mkArgs(acc) splice rec(t, Some(internal.typeRef(internal.thisType(sym.owner), sym, pt :: Nil)))
                }
                
              //case ((t @ Ident(name: TermName)) :: Nil, Stream(VarargParam(pt))) if splicedHoles(name) =>
                /*
                //debug(s"hole vararg [$pt]", t)
                termHoleInfo(name) = ctx.keys.toList -> pt
                // Note: contrary to normal holes, here we do not emit a warning if its inferred type is Nothing
                // The warning would be annoying because hard to remove anyway (cf: cannot ascribe a type to a vararg splice)
                
                //q"${mkArgs(acc)} splice $Base.splicedHole[$pt](${name.toString})"
                mkArgs(acc) splice splicedHole[$pt](${name.toString})"
                */
                //???
                
              case (as, Stream(VarargParam(pt))) =>
                //debug(s"vararg [$pt]", as)
                ArgsVarargs(mkArgs(acc), processArgs(Nil)(as, Stream continually pt).asInstanceOf[Args])
                
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
        
        val effects = Args()(hs map (t => rec(t, Some(Any))): _*)
        
        val body = rec(internal.setType( q"..$tl; $r",x.tpe ), // doing this kind of de/re-structuring loses the type
          expectedType)
        
        val argss = effects :: Args(body) :: Nil
        
        val imp = getMtd(scpLibTyp, "Imperative")
        val retTyp = liftType(x.tpe)
        
        methodApp(scpLib, imp, retTyp::Nil, argss, retTyp)
        
        
      /** --- --- --- IF THEN ELSE --- --- --- */
      case q"if ($cond) $thn else $els" =>
        
        val tp = lub(thn.tpe :: els.tpe :: Nil) // x.tpe will have the type of whatever was expected in place of the ITE (eg: Unit, even if the branches are Int)
        val retTyp = liftType(tp)
        
        val mtd = getMtd(scpLibTyp, "IfThenElse")
        methodApp(scpLib, mtd, retTyp::Nil, Args(
          rec(cond, Some(Boolean)),
          byName(rec(thn, Some(tp))),
          byName(rec(els, Some(tp)))
        ) :: Nil, retTyp)
        
      /** --- --- --- WHILE LOOPS --- --- --- */
      case q"while ($cond) $loop" =>
        val mtd = getMtd(scpLibTyp, "While")
        val retTyp = liftType(Unit)
        methodApp(scpLib, mtd, Nil, Args(
          byName(rec(cond, Some(Boolean))),
          byName(rec(loop, Some(loop.tpe)))
        ) :: Nil, retTyp)
        
      /** --- --- --- TYPE ASCRIPTIONS --- --- --- */
      case q"$t: $typ" =>
        val tree = liftTerm(t, x, Some(typ.tpe)) // Executed before `traverseTypeTree(typ)` to keep syntactic order!
        traverseTypeTree(typ)
        ascribe(tree, liftType(typ.tpe))
        
        
        
        
      /** --- --- --- ERRORS --- --- --- */
        
      case q"${s: DefDef}; ..$_" =>
        throw EmbeddingException(s"Embedding of definition '$s' is not supported.")
        
      case _: DefDef =>
        throw EmbeddingException("Statement in expression position: "+x/*+(if (debug.debugOptionEnabled) s" [${x.getClass}]" else "")*/)
        
      case _ => throw EmbeddingException("Unsupported feature: "+x/*+(if (debug.debugOptionEnabled) s" [${x.getClass}]" else "")*/)
        
    }
  }
  
  
  /** Note: we currently use `moduleType` for types that reside in packages and for objects used as modules...
    * Note: widening may change Int(0) to Int (wanted) but also make 'scala' in 'scala.Int' TypeRef-matchable !! */
  def liftType(tp: Type, rec: Boolean = false): TypeRep = typeCache.getOrElseUpdate(tp, tp match {
    //case _ if tp =:= Any =>
    //  // In Scala one can call methods on Any that are really AnyRef methods, eg: (42:Any).hashCode
    //  // Assuming AnyRef for Any not be perfectly safe, though... (to see)
    //  liftType(AnyRef)
      
    case TypeRef(prefix, sym, targs) =>
      
      try {
        val tsym = getTyp(tp.typeSymbol.asType) // may throw ClassNotFoundException
        
        val self = liftModule(prefix)
        
        typeApp(self, tsym, targs map (t => liftType(t)))
        // ^ Note: getTyp(sym.asType) did not always work...
        
      } catch {
        case e: ClassNotFoundException =>
          // Types like `scala.Any` do not have an associated class (though Any is currently already handled in the case above)
          uninterpretedType(mkTag(tp))
      }
      
    case _ => // TODO verify no holes in 'tp'! If holes, try widening first
      val s = tp.typeSymbol
      if (s.isModule || s.isPackage || s.isModuleClass) {
        ??? // TODO B/E
      }
      else if (tp <:< typeOf[AnyRef]) {
        val tag = mkTag(tp)
        uninterpretedType(tag)
      }
      else {
        assert(!rec) // TODO B/E
        liftType(tp.widen, true)
      }
  })
  
  def liftModule(tp: Type): Rep = tp match {
    case ThisType(sym) =>
      // FIXME handle non-stable `this` types with holes!!
      assert(sym.isStatic) // TODO BE
      moduleObject(sym.fullName, sym.isPackage)
    case SingleType(pre,sym) =>
      assert(sym.isStatic) // TODO BE
      //assert(sym.isPackage && sym.isPackageClass && sym.isModule) // fails
      moduleObject(sym.fullName, sym.isPackage)
  }
  
  
  def traverseTypeTree(tpt: Tree) = ()
  
  
  def apply(code: Tree) = {
    
    liftTerm(code, code, Some(code.tpe))(Map())
    
  }
  
  
  
  
  
  
}
















