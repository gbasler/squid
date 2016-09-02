package scp
package quasi2

import utils._
import lang2._
import scp.utils.MacroUtils._

import collection.mutable
import reflect.macros.TypecheckException
import utils.CollectionUtils._
import quasi.EmbeddingException

import scala.reflect.macros.whitebox


case class QuasiException(msg: String) extends Exception(msg)

/** Holes represent free variables and free types (introduced as unquotes in a pattern or alternative unquotes in expressions).
  * Here, insertion unquotes are _not_ viewed as holes (they should simply correspond to a base.$[T,C](tree) node). */
class QuasiEmbedder[C <: whitebox.Context](val c: C) {
  import c.universe._
  
  object Helpers extends {val uni: c.universe.type = c.universe} with ScopeAnalyser[c.universe.type]
  import Helpers._
  
  val debug = { val mc = MacroDebugger(c); mc[MacroDebug] }
  
  
  val UnknownContext = typeOf[utils.UnknownContext]
  val scal = q"_root_.scala"
  

  /** holes: Seq(either term or type); splicedHoles: which holes are spliced term holes (eg: List($xs*))
    * holes in ction mode are interpreted as free variables (and are never spliced)
    * unquotedTypes contains the types unquoted in (in ction mode) plus the types found in the current scope
    * TODO: actually use `unquotedTypes`!
    * TODO maybe this should go in QuasiMacros... */
  def applyQQ(Base: Tree, tree: c.Tree, holes: Seq[Either[TermName,TypeName]], splicedHoles: collection.Set[TermName],
            unquotedTypes: Seq[(TypeName, Type, Tree)], unapply: Option[c.Tree], config: QuasiConfig): c.Tree = {
    
    //debug("HOLES:",holes)
    
    val (termHoles, typeHoles) = holes.mapSplit(identity) match {case (termHoles, typeHoles) => (termHoles toSet, typeHoles toSet)}
    
    val traits = typeHoles map { typ => q"trait $typ extends _root_.scp.quasi2.QuasiBase.`<extruded type>`" }  // QuasiBase.HoleType
    val vals = termHoles map { vname => q"val $vname: Nothing = ???" }
    
    debug("Tree:", tree)
    
    var termScope: List[Type] = Nil // will contain the scrutinee's scope or nothing
    
    val ascribedTree = unapply match {
      case Some(t) =>
        debug("Scrutinee type:", t.tpe)
        t.tpe.baseType(symbolOf[QuasiBase#IR[_,_]]) match {
          case tpe @ TypeRef(tpbase, _, tp::ctx::Nil) => // Note: cf. [INV:Quasi:reptyp] we know this type is of the form Rep[_], as is ensured in Quasi.unapplyImpl
            if (!(tpbase =:= Base.tpe)) throw EmbeddingException(s"Could not verify that `$tpbase` is the same as `${Base.tpe}`")
            val newCtx = if (ctx <:< UnknownContext) {
              // ^ UnknownContext is used as the dummy context parameter of terms rewritten in rewrite rules
              // It gets replaced by a brand new type to avoid wrongful sharing, as below:
              
              // Creates a brand new type for this particular context, so that it cannot be expressed outside of the rewrite rule (and thus one can't mix up rewriting contexts)
              val tname = TypeName(s"<context @ ${t.pos.line}:${t.pos.column}>")
              c.typecheck(q"class $tname; new $tname").tpe
              
            } else ctx
            debug("New context:",newCtx)
            termScope ::= newCtx
            if (tp.typeSymbol.isClass && !(Any <:< tp)) {
              // ^ If the scrutinee is 'Any', we're better off with no ascription at all, as something like:
              // 'List(1,2,3) map (_ + 1): Any' will typecheck to:
              // 'List[Int](1, 2, 3).map[Int, Any]((x$1: Int) => x$1+1)(List.canBuildFrom[Int]): scala.Any'
              tp match {
                case _: ConstantType => None // For some stupid reason, Scala typechecks `(($hole: String): String("Hello"))` as `"Hello"` ..................
                case _ =>
                  val purged = purgedTypeToTree(tp)
                  debug("Purged matched type:", purged)
                  Some(q"$tree: $purged") // doesn't work to force subterms of covariant result to acquire the right type params.....
                  //Some(q"_root_.scala.Predef.identity[$purged]($virtualizedTree)") // nope, this neither
              }
            } else None
          case NoType => assert(false, "Unexpected type: "+t.tpe); ??? // cf. [INV:Quasi:reptyp]
        }
      case None => None
    }
    
    def shallowTree(t: Tree) = {
      // we need tp introduce a dummy val in case there are no statements; otherwise we may remove the wrong statements when extracting the typed term
      val nonEmptyVals = if (traits.size + vals.size == 0) Set(q"val $$dummy$$ = ???") else vals
      val st = q"..$traits; ..$nonEmptyVals; $t"
      //val st = q"object Traits { ..$traits; }; import Traits._; ..$nonEmptyVals; $t"
      // ^ this has the advantage that when types are taken out of scope, their name is preserved (good for error message),
      // but it caused other problems... the implicit evidences generated for them did not seem to work
      if (debug.debugOptionEnabled) debug("Shallow Tree: "+st)
      st
    }
    
    /** Note: 'typedTreeType' is actually the type of the tree ASCRIBED with the scrutinee's type if possible... */
    val (typedTree, typedTreeType) = ascribedTree flatMap { t =>
      val st = shallowTree(t)
      try {
        val typed = c.typecheck(st)
        //val q"..$stmts; $finalTree: $_" = typed // Was having some match errors; maybe the ascription was removed when the type was not widened
        val (stmts, finalTree) = typed match {
          case q"..$stmts; $finalTree: $_" => stmts -> finalTree  // ie:  case Block(stmts, q"$finalTree: $_") =>
          //case q"..$stmts; $finalTree" => stmts -> finalTree // Note: apparently, typechecking can remove type ascriptions... (does not seem to happen anymore, though)
        }
        // Since the precise type is needed to define $ExtractedType$:
        Some( internal.setType(q"..$stmts; $finalTree", finalTree.tpe), typed.tpe )
      } catch {
        case e: TypecheckException =>
          debug("Ascribed tree failed to typecheck: "+e.msg)
          debug("  in:\n"+showPosition(e.pos))
          None
      }
    } getOrElse {
      try {
        val typed = c.typecheck(shallowTree(tree))
        if (ascribedTree.nonEmpty) {
          val expanded = unapply.get.tpe map (_.dealias)
          c.warning(c.enclosingPosition, s"""Scrutinee type: ${unapply.get.tpe}${
            if (expanded == unapply.get.tpe) ""
            else s"\n|  which expands to: $expanded"
          }
          |  seems incompatible with or is more specific than pattern type: IR[${typed.tpe}, _]${
            if (typeHoles.isEmpty) ""
            else s"\n|  perhaps one of the type holes (${typeHoles mkString ", "}) is unnecessary."
          }
          |Ascribe the scrutinee with ': IR[_,_]' or call '.erase' on it to remove this warning.""".stripMargin)
          // ^ TODO
        }
        typed -> typed.tpe
      } catch {
        case e: TypecheckException =>
          throw EmbeddingException.Typing(e.msg)
      }
    }
    
    val q"..$stmts; $finalTree" = typedTree
    
    // // For when traits are generated in a 'Traits' object:
    //val typeSymbols = (stmts collectFirst { case traits @ q"object Traits { ..$stmts }" =>
    //  stmts map { case t @ q"abstract trait ${name: TypeName} extends ..$_" =>
    //    debug("TRAITS",traits.symbol.typeSignature)
    //    (name: TypeName) -> internal.typeRef(traits.symbol.typeSignature, t.symbol, Nil) }
    //} get) toMap;
    val typeSymbols = stmts collect {
      case t @ q"abstract trait ${name: TypeName} extends ..$_" => (name: TypeName) -> t.symbol
    } toMap;
    val holeSymbols = stmts collect {
      case t @ q"val ${name: TermName}: $_ = $_" => t.symbol
    } toSet;
    
    debug(s"Typed[${typedTreeType}]: "+finalTree)
    
    
    apply(Base, finalTree, termScope, config, unapply, 
      typeSymbols, holeSymbols, holes, splicedHoles, termHoles, typeHoles, typedTree, typedTreeType, stmts)
    
  }
  
    
  
  
  
  
  
  
  
  
  def apply(baseTree: Tree, rawTree: c.Tree, termScopeParam: List[Type], config: QuasiConfig, unapply: Option[c.Tree],
            typeSymbols: Map[TypeName, Symbol], holeSymbols: Set[Symbol], 
            holes: Seq[Either[TermName,TypeName]], splicedHoles: collection.Set[TermName], 
            termHoles: Set[TermName], typeHoles: Set[TypeName], typedTree: Tree, typedTreeType: Type, stmts: List[Tree]): c.Tree = {
    
    
    val shortBaseName = TermName("__b__")
    val Base = q"$shortBaseName"
    
    
    val retType = rawTree.tpe.widen // Widen to avoid too-specific types like Double(1.0), and be more in-line with Scala's normal behavior
    
    var termScope = termScopeParam // will contain the scope bases of all unquoted stuff + in xtion, possibly the scrutinee's scope
    // Note: in 'unapply' mode, termScope's last element should be the scrutinee's context
    
    var importedFreeVars = mutable.Map[String, Type]() // will contain the free vars imported by inserted IR's
    
    //type Context = List[TermSymbol]
    type Context = Map[TermName, Type]
    val termHoleInfo = mutable.Map[TermName, (Context, Type)]()
    
    
    // TODO aggregate these in a pre-analysis phase to get right hole/fv types...
    var freeVariableInstances = List.empty[(String, Type)]
    //var insertions = List.empty[(String, Tree)]
    // TODO
    //rawTree analyse {
    //  case q"$baseTree.$$[$tpt,$scp](..$idts)" => // TODO check baseTree
    //}
    // TODO see if we found the same holes as in `holes` !
    
    //val tree = rawTree transform { case q"$b.$$[$t,$c]($args: _*): _*" => q"$b.$$[$t,$c]($args: _*)" }
    val tree = rawTree
    
    
    /** Embeds the type checked code with ModularEmbedding, but via the config.embed function, which may make the embedded
      * program go through an arbitrary base before ending up as Scala mirror code! */
    val codeTree = config.embed(c)(Base, new BaseUser[c.type](c) {
      def apply(b: Base)(insert: (macroContext.Tree, Map[String, b.BoundVal]) => b.Rep): b.Rep = {
        
        
        val myBaseTree = baseTree
        object QTE extends QuasiTypeEmbedder[macroContext.type, b.type](macroContext, b, str => debug(str)) {
          val helper = QuasiEmbedder.this.Helpers
          //val baseTree: c.Tree = Base  // throws: scala.reflect.macros.TypecheckException: not found: value __b__
          val baseTree: c.Tree = myBaseTree
        }
        
        /** Special-cases the default modular embedding with quasiquote-specific details */
        object ME extends QTE.Impl {
          
          def holeName(nameTree: Tree, in: Tree) = nameTree match {
            case q"scala.Symbol.apply(${Literal(Constant(str: String))})" => str
            case _ => throw QuasiException("Free variable must have a static name, and be of the form: $$[Type]('name) or $$('name)\n\tin: "+showCode(in))
          }
          
          lazy val ExtractedBinder = typeOf[scp.lib.ExtractedBinder].typeSymbol
          
          override def liftTerm(x: Tree, parent: Tree, expectedType: Option[Type], inVarargsPos: Boolean)(implicit ctx: Map[TermSymbol, b.BoundVal]): b.Rep = {
          object HoleName { def unapply(tr: Tree) = Some(holeName(tr,x)) }
          x match {
            
            /** This is to find repeated holes: $-less references to holes that were introduced somewhere else.
              * We convert them to proper hole calls and recurse. */
            case _ if holeSymbols(x.symbol) =>
              val tree = q"$baseTree.$$$$[${TypeTree(Nothing)}](scala.Symbol.apply(${x.symbol.name.toString}))"
              //debug("Repeated hole:",x,"->",tree)
              liftTerm(tree, parent, expectedType)
              
              
            /** Processes extracted binders in lambda parameters */
            case q"(..$params) => $body" =>
              val xtr = params filter (_.symbol.annotations exists (_.tree.tpe.typeSymbol == ExtractedBinder))
              xtr foreach { vd =>
                val nam = vd.name
                val typ = vd.symbol.typeSignature
                debug("PARAM-BOUND HOLE:",nam,typ)
                termHoleInfo += nam -> (Map(nam -> typ) -> typ)
              }
              super.liftTerm(x, parent, expectedType, inVarargsPos)
              
            /** Processes extracted binders in val bindings */
            case q"${vd @ ValDef(mods, TermName(name), tpt, rhs)}; $body" if vd.symbol.annotations exists (_.tree.tpe.typeSymbol == ExtractedBinder) =>
              val nam = TermName(name)
              
              val sign = vd.symbol.typeSignature
              val typ = if (mods.hasFlag(Flag.MUTABLE)) {
                val sym = typeOf[scp.lib.Var[Any]].typeSymbol
                internal.typeRef(typeOf[scp.lib.`package`.type], sym, sign :: Nil)
              }
              else sign
              
              debug("VAL-BOUND HOLE:",nam,typ)
              termHoleInfo += nam -> (Map(nam -> typ) -> typ)
              
              super.liftTerm(x, parent, expectedType, inVarargsPos)
              
              
            /** Replaces insertion unquotes with whatever `insert` feels like inserting.
              * In the default quasi config case, this will be the trees representing the inserted elements. */
            case q"$baseTree.$$[$tpt,$scp](..$idts)" => // TODO check baseTree:  //if base.tpe == Base.tpe => // TODO make that an xtor  // note: 'base equalsStructure Base' is too restrictive/syntactic
              //debug("UNQUOTE",idts)
              
              val (bases, vars) = bases_variables(scp.tpe)
              
              val varsInScope = ctx map { case (sym, bv) => sym.name -> (sym.typeSignature -> bv) }
              
              val (captured,free) = vars mapSplit {
                case(n,t) => varsInScope get n match {
                  case Some((tpe,bv)) =>
                    tpe <:< t ||
                      (throw EmbeddingException(s"Captured variable `$n: $tpe` has incompatible type with free variable `$n: $t` found in inserted IR $$(${idts mkString ","})"))
                    Left(n, bv)
                  case None => Right(n, t)
                }
              } // TODO check correct free var types
              
              termScope :::= bases
              importedFreeVars ++= free.iterator map { case (n,t) => n.toString -> t }
              
              def subs(ir: Tree) =
                insert(ir, captured.iterator map {case(n,bv) => n.toString->bv} toMap) // TODO also pass liftType(tpt.tpe)
              
              debug("Unquoting",s"$idts: IR[$tpt,$scp]", ";  env",varsInScope,";  capt",captured,";  free",free)
              
              // TODOmaybe: introduce a `splice` method in Base, get rid of SplicedHole, and make ArgsVararg smartly transform into ArgsVarargs when it sees a splice
              
              def assertVararg(t: Tree) = if (!inVarargsPos) throw QuasiException(s"Vararg splice unexpected in that position: ${showCode(t)}")
              idts match {
                case (idt @ q"$idts: _*") :: Nil =>
                  assertVararg(idt)
                  subs(idt)
                case idt :: Nil => subs(idt)
                case _ =>
                  assertVararg(q"$$(..$idts)")
                  subs(q"_root_.scala.Seq(..${idts}): _*")
              }
              
              
            // Note: For some extremely mysterious reason, c.typecheck does _not_ seem to always report type errors from terms annotated with `_*`
            case q"$baseTree.$$$$(scala.Symbol($stringNameTree))" => oh wait "Scala type checking problem."
              
            case q"$baseTree.$$$$_*[$tp]($nameTree)" => // TODO check baseTree
              val name = holeName(nameTree, nameTree)
              val notSeqNothingExpectedType = expectedType map { et =>
                (et baseType typeOf[Seq[Any]].typeSymbol) match {
                  case TypeRef(_, _, tp :: Nil) =>
                    if (splicedHoles(TermName(name))) tp else { // no need to warn here as the warning will happen in case below (other handling of $$)
                      if (tp <:< Nothing) c.warning(c.enclosingPosition, s"Type inferred for vararg hole '$name' was Nothing. This is unlikely to be what you want.")
                      et
                    }
                  case NoType => throw QuasiException(s"The expected type of vararg hole '$name' should extend Seq[_], found `${et}`")
                }}
              liftTerm(q"$baseTree.$$$$[${TypeTree(Nothing)}]($nameTree)", parent, notSeqNothingExpectedType) // No need to pass `tp` as the type argument; it ensures there will be a warning if hole has no type
              
            /** Replaces calls to $$(name) with actual holes */
            case q"$baseTree.$$$$[$tpt]($nameTree)" => // TODO check baseTree
              
              val name = holeName(nameTree, x)
              
              val holeType = expectedType match {
                case None if tpt.tpe <:< Nothing => throw EmbeddingException(s"No type info for hole '$name'" + (
                  if (debug.debugOptionEnabled) s", in: $parent" else "" )) // TODO: check only after we have explored all repetitions of the hole? (not sure if possible)
                case Some(tp) =>
                  assert(!SCALA_REPEATED(tp.typeSymbol.fullName.toString))
                  
                  if (tp <:< Nothing) parent match {
                    case q"$_: $_" => // this hole is ascribed explicitly; the Nothing type must be desired
                    case _ => macroContext.warning(macroContext.enclosingPosition,
                      s"Type inferred for hole '$name' was Nothing. Ascribe the hole explicitly to remove this warning.") // TODO show real hole in its original form
                  }
                  
                  val mergedTp = if (tp <:< tpt.tpe || tpt.tpe <:< Nothing) tp else tpt.tpe
                  debug(s"[Hole '$name'] Expected",tp," required",tpt.tpe," merged",mergedTp)
                  
                  mergedTp
              }
              
              
              //if (splicedHoles(TermName(name))) throw EmbeddingException(s"Misplaced spliced hole: '$name'") // used to be: q"$Base.splicedHole[${_expectedType}](${name.toString})"
              
              
              if (unapply isEmpty) {
                debug(s"Free variable: $name: $tpt")
                freeVariableInstances ::= name -> holeType
                // TODO maybe aggregate glb type beforehand so we can pass the precise type here... could even pass the _same_ hole!
              } else {
                val termName = TermName(name)
                val scp = ctx.keys map (k => k.name -> k.typeSignature) toMap;
                termHoleInfo get termName map { case (scp0, holeType0) =>
                  val newScp = scp0 ++ scp.map { case (n,t) => lub(t :: scp0.getOrElse(n, Any) :: Nil) }
                  newScp -> lub(holeType :: holeType0 :: Nil)
                } getOrElse {
                  termHoleInfo(termName) = scp -> holeType
                }
              }
              
              if (splicedHoles(TermName(name)))
                   b.splicedHole(name, liftType(holeType))
              else b.hole(name, liftType(holeType))
              
              
            case _ => 
              //debug("NOPE",x)
              super.liftTerm(x, parent, expectedType)
          }}
          
          override def liftTypeUncached(tp: Type, wide: Boolean, deal: Boolean): b.TypeRep = {
            val tname = tp.typeSymbol.name
            typeSymbols get tname.toTypeName filter (_ === tp.typeSymbol) map { sym =>
              b.typeHole(tname.toString)
            } getOrElse super.liftTypeUncached(tp, wide, deal)
          }
          
        }
        ME(tree, Some(typedTreeType))
      }
    })
    
    
    //debug("Free variables instances: "+freeVariableInstances)
    val freeVariables = freeVariableInstances.groupBy(_._1).mapValues(_.unzip._2) map {
      case (name, typs) => name -> glb(typs)
    }
    if (freeVariables nonEmpty) debug("Free variables: "+freeVariables)
    
    if (termHoleInfo nonEmpty) debug("Term holes: "+termHoleInfo)
    
    
    //val expectedType = typeIfNotNothing(typedTreeType)
    //val res = c.untypecheck(lift(finalTree, finalTree, expectedType)(Map()))
    val res = c.untypecheck(codeTree)
    debug("Result: "+showCode(res))
    
    
    // We surrounds names with _underscores_ to avoid potential name clashes (with defs, for example)
    unapply match {
        
      case None =>
        
        // TODO check conflicts in contexts
        
        if (termScope.size > 1) debug(s"Merging scopes; glb($termScope) = ${glb(termScope)}")
        
        val ctxBase = tq"${glb(termScope)}"
        
        /*
        // putting every freevar in a different refinement (allows name redefinition!)
        val context = (freeVars ++ importedFreeVars).foldLeft(ctxBase){ //.foldLeft(tq"AnyRef": Tree){
          case (acc, (n,t)) => tq"$acc{val $n: $t}"
        }
        */
        
        
        // Note: an alternative would be to put these into two different refinements,
        // so we could end up with IR[_, C{val fv: Int}{val fv: Double}] -- which is supposedly equivalent to IR[_, C{val fv: Int with Double}]
        val fv = freeVariables ++ (importedFreeVars.iterator map {
          case (name, tpe) => name -> (freeVariables get name map (tpe2 => glb(tpe :: tpe2 :: Nil)) getOrElse tpe)
        })
        
        val context = tq"$ctxBase { ..${ fv map { case (n,t) => q"val ${TermName(n)}: $t" } } }"
        
        q"val $shortBaseName: $baseTree.type = $baseTree; $baseTree.`internal IR`[$retType, $context]($Base.wrapConstruct($res))"
        //                                ^ not using $Base shortcut here or it will show in the type of the generated term
        
        
      case Some(selector) =>
        
        val noSefl = q"class A {}" match { case q"class A { $self => }" => self }
        val termHoleInfoProcessed = termHoleInfo mapValues {
          case (scp, tp) =>
            val scpTyp = CompoundTypeTree(Template(termScope map typeToTree, noSefl, scp map { case(n,t) => q"val $n: $t" } toList))
            (scpTyp, tp)
        }
        val termTypesToExtract = termHoleInfoProcessed map {
          case (name, (scpTyp, tp)) => name -> (
            if (splicedHoles(name)) tq"Seq[$baseTree.IR[$tp,$scpTyp]]"
            else tq"$baseTree.IR[$tp,$scpTyp]"
          )}
        val typeTypesToExtract = typeSymbols mapValues { sym => tq"$baseTree.IRType[$sym]" }
        
        val extrTyps = holes.map {
          case Left(vname) => termTypesToExtract(vname)
          case Right(tname) => typeTypesToExtract(tname) // TODO B/E
        }
        debug("Extracted Types: "+extrTyps.mkString(", "))
        
        val extrTuple = tq"(..$extrTyps)"
        //debug("Type to extract: "+extrTuple)
        
        val tupleConv = holes.map {
          case Left(name) if splicedHoles(name) =>
            val (scp, tp) = termHoleInfoProcessed(name)
            q"_maps_._3(${name.toString}) map (r => $Base.`internal IR`[$tp,$scp](r))"
          case Left(name) =>
            //q"Quoted(_maps_._1(${name.toString})).asInstanceOf[${termTypesToExtract(name)}]"
            val (scp, tp) = termHoleInfoProcessed(name)
            q"$Base.`internal IR`[$tp,$scp](_maps_._1(${name.toString}))"
          case Right(name) =>
            //q"_maps_._2(${name.toString}).asInstanceOf[${typeTypesToExtract(name)}]"
            q"$Base.`internal IRType`[${typeSymbols(name)}](_maps_._2(${name.toString}))"
        }
        
        val valKeys = termHoles.filterNot(splicedHoles).map(_.toString)
        val typKeys = typeHoles.map(_.toString)
        val splicedValKeys = splicedHoles.map(_.toString)
        
        
        val defs = typeHoles map { typName => // TODO rm
          val typ = typeSymbols(typName)
          val holeName = TermName(s"$$$typName$$hole")
          q"implicit val $holeName : $Base.IRType[$typ] = $Base.`internal IRType`[${typ}]($Base.typeHole(${typName.toString}))"
        }
        
        //val termType = tq"$Base.Quoted[${typedTree.tpe}, ${termScope.last}]"
        val termType = tq"$Base.SomeIR" // We can't use the type inferred for the pattern or we'll get type errors with things like 'x.erase match { case dsl"..." => }'
        
        val typeInfo = q"type $$ExtractedType$$ = ${typedTree.tpe}" // Note: typedTreeType can be shadowed by a scrutinee type
        val contextInfo = q"type $$ExtractedContext$$ = ${termScope.last}" // Note: the last element of 'termScope' should be the scope of the scrutinee...
        //val contextInfo = q"type $$ExtractedContext$$ = ${glb(termScope)}" // Note: the last element of 'termScope' should be the scope of the scrutinee...
        
        if (extrTyps.isEmpty) { // A particular case, where we have to make Scala understand we extract nothing at all
          
          assert(defs.isEmpty)
          
          q"""{
          val $shortBaseName: $baseTree.type = $baseTree
          new {
            $typeInfo
            $contextInfo
            def unapply(_t_ : $termType): Boolean = {
              ..$defs
              //..dslDefs
              //..dslTypes
              //val $$shortBaseName = $$baseTree
              val _term_ = $Base.wrapExtract($res)
              $Base.extract(_term_, _t_.rep) match {
                case Some((vs, ts, fvs)) if vs.isEmpty && ts.isEmpty && fvs.isEmpty => true
                case Some((vs, ts, fvs)) => assert(false, "Expected no extracted objects, got values "+vs+", types "+ts+" and spliced values "+fvs); ???
                case None => false
              }
            }
          }}.unapply($selector)
          """
        } else {
          // FIXME hygiene (Rep types...)
          
          /* Scala seems to be fine with referring to traits which declarations do not appear in the final source code (they were typechecked with context `c`),
          but it seems to creates confusion in tools like IntelliJ IDEA (although it's not fatal).
          To avoid it, the typed trait definitions are spliced here */
          val typedTraits = stmts collect { case t @ q"abstract trait $_ extends ..$_" => t } //filter (_ => false)
          
          q"""{
          val $shortBaseName: $baseTree.type = $baseTree
          ..$typedTraits
          new {
            $typeInfo
            $contextInfo
            def unapply(_t_ : $termType): $scal.Option[$extrTuple] = {
              ..${defs}
              //..dslDefs
              //..dslTypes
              val _term_ = $Base.wrapExtract($res)
              $Base.extract(_term_, _t_.rep) map { _maps_0_ =>
                val _maps_ = $Base.`internal checkExtract`(${showPosition(c.enclosingPosition)}, _maps_0_)(..$valKeys)(..$typKeys)(..$splicedValKeys)
                (..$tupleConv)
              }
            }
          }}.unapply($selector)
          """
        }
        
    }
    
  }
    
  
  
  
  /** Creates a type tree from a Type, where all non-class types are replaced with existentials */
  def purgedTypeToTree(tpe: Type): Tree = {
    val existentials = mutable.Buffer[TypeName]()
    def rec(tpe: Type): Tree = tpe match {
      case _ if !tpe.typeSymbol.isClass => //&& !(tpe <:< typeOf[Base.Param]) =>
        existentials += TypeName("?"+existentials.size)
        tq"${existentials.last}"
      case TypeRef(pre, sym, Nil) =>
        TypeTree(tpe)
      case TypeRef(pre, sym, args) =>
        AppliedTypeTree(Ident(sym.name),
          args map { x => rec(x) })
      case AnnotatedType(annotations, underlying) =>
        rec(underlying)
      case _ => TypeTree(tpe) // Note: not doing anything for other kinds of types, like ExistentialType...
    }
    val r = rec(tpe)
    val tpes = existentials map { tpn => q"type $tpn" }
    if (existentials.nonEmpty) tq"$r forSome { ..$tpes }" else r
  }
  
  
  def typeToTree(tpe: Type): Tree = {
    val r = tpe match {
      case TypeRef(pre, sym, Nil) =>
        TypeTree(tpe)
      case TypeRef(pre, sym, args) =>
        //AppliedTypeTree(Ident(sym.name),
        //  args map { x => typeToTree(x) })
        TypeTree(tpe)
      case AnnotatedType(annotations, underlying) =>
        typeToTree(underlying)
      case _ => TypeTree(tpe)
    }
    //println(s"typeToTree($tpe) = ${showCode(r)}")
    r
  }
  
  
  
}
