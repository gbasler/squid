package scp
package quasi

import collection.mutable
import reflect.macros.TypecheckException
import reflect.macros.whitebox
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

import lang._

case class EmbeddingException(msg: String) extends Exception(msg)

/** 
  * Note: currently, we generate free-standing functions (eg: const(...)), without qualifying them.
  * This can be useful, but also confusing.
  * We may require the presence of an implicit to retrieve a $base object.
  *
  * TODO: better let-binding of type evidences, binding common subtypes...
  * 
  * TODO: cache extractors, so as not to recreate them all the time!
  * 
  */
class Embedding[C <: whitebox.Context](val c: C) extends utils.MacroShared with ScopeAnalyser {
  import c.universe._
  import utils.MacroUtils._
  
  type Ctx = c.type
  val Ctx: Ctx = c
  
  val uni: c.universe.type = c.universe
  
  
  val debug = { val mc = MacroDebugger(c); mc[MacroDebug] }
  
  val scal = q"_root_.scala"
  
  val ByNameParamClass = c.universe.definitions.ByNameParamClass
  val RepeatedParamClass = c.universe.definitions.RepeatedParamClass // JavaRepeatedParamClass
  
  val Any = typeOf[Any]
  val Nothing = typeOf[Nothing]
  val Unit = typeOf[Unit]
  val UnknownContext = typeOf[utils.UnknownContext]
  
  def typeIfNotNothing(tp: Type) = {
    assert(tp != NoType)
    if (tp <:< Nothing) None else Some(tp)
  }
  
  
  //def apply(tree: c.Tree, holes: Seq[Either[TermName,TypeName]], unquotedTypes: Seq[Tree], unapply: Option[c.Tree]): c.Tree = {
  /** holes: Seq(either term or type); splicedHoles: which holes are spliced term holes (eg: List($xs*))
    * holes in ction mode are interpreted as free variables (and are never spliced)
    * unquotedTypes contains the types unquoted in (in ction mode) plus the types found in the current scope */
  def apply(Base: Tree, tree: c.Tree, holes: Seq[Either[TermName,TypeName]], splicedHoles: collection.Set[TermName], unquotedTypes: Seq[(TypeName, Type, Tree)], unapply: Option[c.Tree]): c.Tree = {
    
    //debug("HOLES:",holes)
    
    val (termHoles, typeHoles) = (holes.collect{case Left(n) => n}.toSet, holes.collect{case Right(n) => n}.toSet)
    
    //val traits = typeHoles map { typ => q"trait $typ" }
    val traits = typeHoles map { typ => q"trait $typ extends _root_.scp.lang.Base.HoleType" }
    val vals = termHoles map { vname => q"val $vname: Nothing = ???" }
    
    val virtualizedTree = virtualize(tree)
    debug("Virtualized:", virtualizedTree)
    
    var termScope: List[Type] = Nil // will contain the scope bases of all unquoted stuff + in xtion, possibly the scrutinee's scope
    // Note: in 'unapply' mode, termScope's last element should be the scrutinee's context
    
    var importedFreeVars = mutable.Map[TermName, Type]() // will contain the free vars of the unquoted stuff
    
    val ascribedTree = unapply match {
      case Some(t) =>
        debug("Scrutinee type:", t.tpe)
        t.tpe.baseType(symbolOf[Base#Quoted[_,_]]) match {
          case tpe @ TypeRef(tpbase, _, tp::ctx::Nil) if tpbase =:= Base.tpe => // Note: cf. [INV:Quasi:reptyp] we know this type is of the form Rep[_], as is ensured in Quasi.unapplyImpl
            val newCtx = if (ctx <:< UnknownContext) {
              val tname = TypeName("[Unknown Context]")
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
                  Some(q"$virtualizedTree: $purged") // doesn't work to force subterms of covariant result to acquire the right type params.....
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
        //val q"..$stmts; $finalTree: $_" = typed // Note: apparently, typechecking can remove type ascriptions.......
        val (stmts, finalTree) = typed match {
          //case q"..$stmts; scala.Predef.identity[$_]($finalTree)" => stmts -> finalTree
          case q"..$stmts; $finalTree: $_" => stmts -> finalTree  // ie:  case Block(stmts, q"$finalTree: $_") =>
          //case q"..$stmts; $finalTree" => stmts -> finalTree
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
        val typed = c.typecheck(shallowTree(virtualizedTree))
        if (ascribedTree.nonEmpty) {
          val expanded = unapply.get.tpe map (_.dealias)
          c.warning(c.enclosingPosition, s"""Scrutinee type: ${unapply.get.tpe}${
            if (expanded == unapply.get.tpe) ""
            else s"\n|  which expands to: $expanded"
          }
          |  seems incompatible with or is more specific than pattern type: Quoted[${typed.tpe}, _]${
            if (typeHoles.isEmpty) ""
            else s"\n|  perhaps one of the type holes is unnecessary: " + typeHoles.mkString(", ")
          }
          |Ascribe the scrutinee with ': Quoted[_,_]' or call '.erase' on it to remove this warning.""".stripMargin)
          // ^ TODO
        }
        typed -> typed.tpe
      } catch {
        case e: TypecheckException =>
          throw EmbeddingException(e.msg) // TODO better reporting
      }
    }
    
    val q"..$stmts; $finalTree" = typedTree
    
    val typeSymbols = stmts collect {
      case t @ q"abstract trait ${name: TypeName} extends ..$_" => (name: TypeName) -> t.symbol
    } toMap;
    // // For when traits are generated in a 'Traits' object:
    //val typeSymbols = (stmts collectFirst { case traits @ q"object Traits { ..$stmts }" =>
    //  stmts map { case t @ q"abstract trait ${name: TypeName} extends ..$_" =>
    //    debug("TRAITS",traits.symbol.typeSignature)
    //    (name: TypeName) -> internal.typeRef(traits.symbol.typeSignature, t.symbol, Nil) }
    //} get) toMap;
    
    debug(s"Typed[${typedTreeType}]: "+finalTree)
    
    val retType = finalTree.tpe.widen // Widen to avoid too-specific types like Double(1.0), and be more in-line with Scala's normal behavior
    
    
    
    
    def checkType(tp: TypeSymbol, where: Tree) {
      assert(!SCALA_REPEATED(tp.fullName), "Oops, looks like a vararg slipped from grasp!")
      //if (tp.isClass && !tp.isModuleClass && !lang.types(tp.fullName) && !typeHoles(tp.name) && !splicedType(tp)) { // potential problem: `Set` indexed with `Type`?
      //  if (!tp.isClass) {
      //    // Unnecessary (and expensive) check here; the implicit not found will be raised later!
      //    //if (c.inferImplicitValue(c.typecheck(tq"TypeEv[$tp]", c.TYPEmode).tpe) == EmptyTree)
      //    //  throw EmbeddingException(s"'${tp}' has no implicit TypeEv[${tp.name}]\nin: $where")
      //  } else
      //  throw EmbeddingException(s"Type '${tp.fullName}' is not supported in language '$lang'\nin: $where"
      //    + (if (debug.debugOptionEnabled) s" [$tp]" else ""))
      //}
    }
    
    def typeToTreeChecking(tpe: Type, where: Tree): Tree = {
      tpe foreach (tp => if (tp.typeSymbol.isType) checkType(tp.typeSymbol.asType, where))
      typeToTree(tpe)
    }
    
    
    //type Scp = Set[TermSymbol]
    type Scp = List[TermSymbol]
    
    //var varCount = -1
    
    //val typesToExtract = mutable.Map[Name, Tree]()
    //val holeTypes = mutable.Map[Name, Type]()
    val termHoleInfo = mutable.Map[TermName, (Scp, Type)]()
    
    /** usedFeatures: Map (prefix type, method) -> name of local var in which the DSLSymbol is stored */
    val usedFeatures = mutable.Map[(Type, MethodSymbol), TermName]()
    
    val freeVars = mutable.Buffer[(TermName, Type)]()
    
    val usedTypes = mutable.Map[Type, TermName]()
    def getType(tp: Type) = {
      val name = usedTypes.getOrElseUpdate(tp, { // TODO use =:= for better type comparison...
        c.freshName[TermName](tp.typeSymbol.name.encodedName.toTermName)
      })
      q"$name"
    }
    
    
    
    
    
    
    
    
    
    
    /** `parent` is used to display an informative tree in errors; `expectedType` for knowing the type a hole should have  */
    def lift(x: c.Tree, parent: Tree, expectedType: Option[Type])(implicit ctx: Map[TermSymbol, TermName]): c.Tree = {
      //debug(s"TRAVERSING",x,"<:",expectedType getOrElse "?")
      //debug(s"TRAVERSING",x,s"[${x.tpe}]  <:",expectedType getOrElse "?")
      
      def rec(x1: c.Tree, expTpe: Option[Type])(implicit ctx: Map[TermSymbol, TermName]): c.Tree = lift(x1, x, expTpe)
      def recNoType(x1: c.Tree)(implicit ctx: Map[TermSymbol, TermName]): c.Tree = lift(x1, x, None)
      
      x match {
        
        //case q"$base.spliceOpen(${Literal(Constant(code: String))})" =>
        //  return injectScope(c.parse(code)) // TODO catch parse error!
          
        //case q"$base.open(${Literal(Constant(name: String))})" => // doesn't match...
        //  return ???
          
          
        case Ident(name: TermName) if termHoles(name) =>
          
          val _expectedType = expectedType match {
            case None => throw EmbeddingException(s"No type info for hole '$name'" + (
              if (debug.debugOptionEnabled) s", in: $parent" else "" )) // TODO: check only after we have explored all repetitions of the hole? (not sure if possible)
            case Some(tp) =>
              assert(!SCALA_REPEATED(tp.typeSymbol.fullName.toString))
              
              if (tp <:< Nothing) parent match {
                case q"$_: $_" => // this hole is ascribed explicitly; the Nothing type must be desired
                case _ => c.warning(c.enclosingPosition,
                  s"Type inferred for hole '$x' was Nothing. Ascribe the hole explicitly to remove this warning.")
              }
              
              if (unapply isEmpty)
                freeVars += (name.toTermName -> tp)
              val scp = ctx.keys.toList
              termHoleInfo(name.toTermName) = scp -> tp
              typeToTreeChecking(tp, parent)
          }
          
          //debug(expectedType, _expectedType)
          
          //debug("Extr", _expectedType)
          
          //typesToExtract(name) = tq"Rep[${_expectedType}]"
          
          val r =
            if (splicedHoles(name)) throw EmbeddingException(s"Misplaced spliced hole: '$x'") // used to be: q"$Base.splicedHole[${_expectedType}](${name.toString})" 
            else q"$Base.hole[${_expectedType}](${name.toString})(${getType(expectedType.get)})"
          
          return r
          
          
        case typ @ TypeTree() if typeHoles(typ.symbol.name.toTypeName) =>
          val name = typ.symbol.name.toTypeName
          
          val tsym = typeSymbols(name)
          
          //typeHoleInfo(name) = tsym
          // ^ typeHoleInfo used to aggregate info we already had; removed for efficiency -- although it could have helped find bugs where we don't find some type holes 
          
          return tq"${tsym}"
          
        case InferredType(tps @ _*)
        if unapply isDefined => // only check for top/bot in extraction mode, where it matters critically
          //debug(">> Inferred:", tps)
          tps foreach { tp =>
            // Q: why not call this in `checkType`? We miss cases where checkType raises an error before we can analyse the return type...
            (if (Any <:< tp) Some("Any")
            else if (tp <:< Nothing) Some("Nothing")
            else None) foreach { top_bot =>
                c.warning(c.enclosingPosition, // TODO show the actual shallow code
                s"""/!\\ Type $top_bot was inferred in quasiquote pattern /!\\
                    |    This may make the pattern fail more often than expected.
                    |    in application: 
                    |      $parent
                    |${if (debug.debugOptionEnabled) showPosition(x.pos) else ""
                    }(If you wish to make this warning go away, you can specify $top_bot explicitly in the pattern.)"""
                .stripMargin)
            }
          }
          
        case _ =>
          
      }
      
      if (x.tpe != null && x.tpe.typeSymbol.isType) checkType(x.tpe.typeSymbol.asType, parent)
      
      x match {
          
        case q"" => throw EmbeddingException("Empty program fragment")
          
        case c @ Literal(Constant(x)) =>
          q"$Base.const($c)"
          //q"$base.const($c)"
          
        //case Ident(name) if !x.symbol.isTerm => q"" // FIXME: can happen when calling rec with a param valdef...
        case Ident(name) if x.symbol.isTerm && (ctx isDefinedAt x.symbol.asTerm) => q"${ctx(x.symbol.asTerm)}"
          
        case id @ Ident(name) if x.symbol.isTerm && !x.symbol.isModule =>
          throw EmbeddingException(s"Cannot refer to local variable '$name' (from '${id.symbol.owner}'). " +
            s"Use antiquotation (syntax '$$$name') in order to include the value in the DSL program.")
          
        case q"$from.this.$name" if !x.symbol.isModule =>  // !x.tpe.termSymbol.isModule
          if (from.toString.nonEmpty)
            throw EmbeddingException(s"""Cannot refer to member '$name' from '$from'.
                                        |Try explicitly qualifying the name, as in '$from.$name'.""".stripMargin)
          else throw EmbeddingException(s"""Cannot refer to member '$name'. Try explicitly qualifying it.""")
          
          /*
        case q"${x @ q"..$mods val ${name: TermName}: $_ = $v"}; $b" =>
        //case q"${x @ ValDef(mods: Modifiers, name: TermName, tpt: Tree, rhs)}; $b" =>
          // TODO check mods?
          
          //varCount += 1
          //val name = TermName("x"+varCount)
          q"letin(${rec(v)}, ($name: Rep[${x.symbol.typeSignature}]) => ${rec(b)(ctx + (x.symbol -> name))})"

        ////case q"${x: ValDef}" =>
        //case q"${x @ ValDef(mods: Modifiers, name: TermName, tpt: Tree, rhs)}" =>
        //  debug(x, mods)
        //  ???
          */
          
        case q"${vdef @ q"$mods val ${name: TermName}: $t = $v"}; ..$b" =>
          assume(!t.isEmpty) //if (!t.isEmpty)
          recNoType(t)
          //debug("LETIN", vdef.symbol.typeSignature, x.tpe, q"..$b".tpe)
          
          val v2 = rec(v, typeIfNotNothing(vdef.symbol.typeSignature))
          val body = rec(
            internal.setType( q"..$b",x.tpe ), // doing this kind of de/re-structuring loses the type
            expectedType)(ctx + (vdef.symbol.asTerm -> name))
          
          val retType = expectedType getOrElse x.tpe
          
          q"letin[${vdef.symbol.typeSignature},${retType}](${name.toString}, $v2, ($name: Rep) => $body)(${
            getType(vdef.symbol.typeSignature)},${getType(retType)})"
         
        case q"val $p = $v; ..$b" =>
          rec(q"$v match { case $p => ..$b }", expectedType)
          
        //case q"val $p: $t = $v" => ???
          
          
        case q"$base.unquote[$t]($idt)" => // TODO need to ask for evidence here?
        //case q"$base.splice[$t]($idt)($ev)" => // TODO need to ask for evidence here?
          // TODO generalize; a way to say "do not lift"
          //q"$base.spliceDeep[$t]($idt)"
          //q"$base.spliceDeep(const($idt))($ev)"
          q"$base.unquoteDeep(const($idt))"
          
        //case q"$base.splice[$t]($idt)($ev)" => q"$base.spliceDeep[$t]($idt)"
        //case q"$base.splice[$t,$scp]($idt)($ev)" =>
        case q"$base.unquote[$t,$scp]($idt)" =>
          //debug(">>",t,scp)
          //debug(">>",variables(scp.tpe))
          //debug(">>",bases_variables(scp.tpe))
          
          val (bases, vars) = bases_variables(scp.tpe)
          
          val varsInScope = ctx.values.toSet
          
          val (captured,free) = vars partition {case(n,t) => varsInScope(n)} // TODO check correct free var types
          
          termScope ++= bases
          //importedFreeVars ++= vars filter {case(n,t) => !varsInScope(n)} // TODO check correct free var types
          importedFreeVars ++= free
          
          val bindings = captured map {case(n,t) => q"${n.toString} -> $n"}
          
          debug("Unquoting",s"$idt: Q[$t,$scp]", ";  env",varsInScope,";  capt",captured,";  free",free)
          
          q"$base.unquoteDeep($idt.rep).subs(..${bindings})"
          
          
        //case _ if x.tpe.termSymbol.isModule => // TODOne try x.symbol
        //case _: TermTree if x.tpe.termSymbol.isModule => // Nope, turns out modules like 'scala' won't match this
        case _ if x.symbol != null && x.symbol.isModule => // Note: when testing 'x.tpe.termSymbol.isModule', we sometimes ended up applying this case on some type parameters!
          //debug(showAttrs(x.symbol))
          //debug(">>", x.tpe, x.tpe <:< typeOf[AnyRef], x.symbol.typeSignature, x.symbol.typeSignature <:< typeOf[AnyRef])
          if (x.symbol.isJava) {
            assume(!(x.symbol.typeSignature <:< typeOf[AnyRef]))
            q"$Base.moduleObject(${x.symbol.fullName}, ${getType(internal.thisType(x.symbol.companion))}.rep)"
          } else {
            val objName = x.tpe match { case SingleType(tp,sym) => s"${tp.typeSymbol.fullName}.${sym.name}" }
            q"$Base.moduleObject($objName, ${getType(x.tpe)}.rep)"
          }
          
        case SelectMember(obj, f) =>
          
          //val dslDef = DSLDef(path, x.symbol.info.toString, mod)
          //if (!lang.defs(dslDef)) throw EmbeddingException(s"Feature '$dslDef' is not supported in language '$lang'")
          
          val method = x.symbol.asMethod // FIXME safe?
          
          debug("Dsl feature: "+method.fullName+" : "+method.info)
          
          def refMtd = {
            val name = usedFeatures.getOrElseUpdate((obj.tpe, method), {
              c.freshName[TermName](method.name.encodedName.toTermName)
            })
            q"$name"
          }
          
          val tp = q"${getType(x.tpe)}.rep"
          val self = lift(obj, x, Some(obj.tpe))
          
          x match {
              
            case q"$a.$_" =>
              //if (dslDef.module) q"$fname"
              //else
              //  q"$fname[..$selfTargs](${lift(obj, x, Some(obj.tpe))})"
              //??? // TODO
              //val self = if (dslDef.module) q"None" else q"Some(${lift(obj, x, Some(obj.tpe))})"
              //val mtd = q"scp.lang.DSLDef(${dslDef.fullName}, ${dslDef.info}, ${dslDef.module})"
              
              q"$Base.methodApp($self, ${refMtd}, Nil, Nil, $tp)"
              
              
            case MultipleTypeApply(_, targs, argss) =>
              
              targs foreach recNoType
              val targsTree = q"List(..${targs map (tp => q"${getType(tp.tpe)}.rep")})"
              
              object VarargParam {
                def unapply(param: Type): Option[Type] = param match {
                  case TypeRef(_, RepeatedParamClass, tp :: Nil) => Some(tp)
                  case _ => None
                }
              }
              
              def mkArgs(acc: List[Tree]) = q"$Base.Args(..${acc.reverse})"
              
              /** Note that in Scala, vararg parameters cannot be by-name, so we don't check for it */
              def processArgs(acc: List[Tree])(args_params: (List[Tree], Stream[Type])): Tree = args_params match {
                case ((a @ q"$t : _*") :: Nil, Stream(VarargParam(pt))) =>
                  //debug(s"vararg splice [$pt]", t)
                  t match {
                    case q"$base.spliceVararg[$t,$scp]($idts)" if base.tpe == Base.tpe => // TODO make that an xtor  // note: 'base equalsStructure Base' is too restrictive/syntactic
                      val splicedX = recNoType(q"$Base.unquote[$t,$scp]($$x$$)")
                      q"${mkArgs(acc)}($idts map (($$x$$:$Base.Q[$t,$scp]) => $splicedX): _*)" // ArgsVarargs creation using Args.apply
                    case _ =>
                      val sym = symbolOf[scala.collection.Seq[_]]
                      q"${mkArgs(acc)} splice ${rec(t, Some(internal.typeRef(internal.thisType(sym.owner), sym, pt :: Nil)))}" // ArgsVarargSpliced
                  }
                  
                case ((t @ Ident(name: TermName)) :: Nil, Stream(VarargParam(pt))) if splicedHoles(name) =>
                  //debug(s"hole vararg [$pt]", t)
                  termHoleInfo(name) = ctx.keys.toList -> pt
                  // Note: contrary to normal holes, here we do not emit a warning if its inferred type is Nothing
                  // The warning would be annoying because hard to remove anyway (cf: cannot ascribe a type to a vararg splice)
                  q"${mkArgs(acc)} splice $Base.splicedHole[$pt](${name.toString})"
                  
                case (as, Stream(VarargParam(pt))) =>
                  //debug(s"vararg [$pt]", as)
                  q"$Base.ArgsVarargs(${mkArgs(acc)}, ${processArgs(Nil)(as, Stream continually pt)})"
                  
                case (a :: as, pt #:: pts) =>
                  pt match {
                    case TypeRef(_, ByNameParamClass, pt :: Nil) =>
                              processArgs(q"$Base.byName[$pt](${rec(a, Some(pt))})" :: acc)(as, pts)
                    case _ => processArgs(                      rec(a, Some(pt))    :: acc)(as, pts)
                  }
                case (Nil, Stream.Empty) => mkArgs(acc)
                case (Nil, pts) if !pts.hasDefiniteSize => mkArgs(acc)
                //case _ => // TODO B/E
              }
              
              val args = (argss zip (f.tpe.paramLists map (_.toStream map (_ typeSignature)))) map processArgs(Nil)
              
              q"$Base.methodApp($self, ${refMtd}, $targsTree, $args, $tp)"
              
              
            //case q"new $tp[..$targs]" => ??? // TODO
              
              // TODO $a.$f(...$args)
              // TODO $f[..$targs](...$args)
          }
          
        case New(tp) =>
          recNoType(tp) // so it checks for inferred Nothing/Any
          checkType(x.tpe.typeSymbol.asType, x)
          q"$Base.newObject(${getType(x.tpe)}.rep)"
          
        case Apply(f,args) => throw EmbeddingException(s"Cannot refer to function '$f' (from '${x.symbol.owner}')") // TODO handle rep function use here?
          
        case q"$f(..$args)" =>
          // I suspect this is never used
          assume(false, """case tq"$tpt[..$targs]"""")
          q"app(${recNoType(f)}, ${args map recNoType})" // TODO replace recNoType
          //q"$base.app(${rec(f)}, ${args map rec})"
          
        case q"() => $body" => q"$Base.thunk(${rec(body, expectedType)})"
  
  
          /** Old lambda hoist that only worked for converting one-parameter lambdas into 'abs'.
            * Could still be used as a special case (with a default definition of abs in BaseDefs), simply because it generates less code. */
          /*
        case q"(${p @ ValDef(mods, name, tpt, _)}) => $body" =>
          //varCount += 1
          //val name = TermName("x"+varCount)
          //q"abs(($name: Rep[${p.symbol.typeSignature}]) => ${rec(body)(ctx + (p.symbol.asTerm -> name))})"
          //rec(q"{p}")
          recNoType(tpt)
          q"$Base.abs[${p.symbol.typeSignature}, ${body.tpe}](${name.toString}, ($name: Rep) => ${
            rec(body, typeIfNotNothing(body.tpe)/*Note: could do better than that: match expected type with function type...*/)(ctx + (p.symbol.asTerm -> name))
          })(${getType(p.symbol.typeSignature)}, ${getType(body.tpe)})"
          */
          
        case q"(..$ps) => $bo" =>
          val (params, bindings) = ps map {
            case p @ ValDef(mods, name, tpt, _) =>
              recNoType(tpt)
              val tp = getType(p.symbol.typeSignature)
              q"(${name.toString} -> $tp.rep)" -> (p.symbol.asTerm -> name)
          } unzip;
          val body = q"{ ($$args$$: Seq[Rep]) => ..${
            bindings.unzip._2.zipWithIndex map {case(name,i) => q"val $name = $$args$$($i)"}
          }; ${
            rec(bo, typeIfNotNothing(bo.tpe)/*Note: could do better than that: match expected type with function type...*/)(ctx ++ bindings)
          } }"
          q"$Base.lambda(Seq(..$params), $body)"
          
          
        // TODO
        //case q"$v match { case ..$cases }" =>
        //  PatMat(liftn(v), cases map {
        //    case cq"$pat if $cond => $body" => MatchClause(pat match {
        //      case pq"()" => liftn(q"()")
        //      case _ => ??? // TODO
        //    }, liftn(cond), liftn(body)) // TODO
        //  })
          
          
        case q"$t: $typ" =>
          val tree = lift(t, x, Some(typ.tpe)) // Executed before `rec(typ)` to keep syntactic order!
          q"ascribe[${rec(typ, Some(x.tpe))}](${tree})(${getType(typ.tpe)})"
        
        
        
        case q"{ $st; () }" => rec(st, None)
        // ^ Note: Passing Some(Unit) doesn't make much sense, since it is probably in this form because 'st' is NOT Unit
          
        // Note: 'st' cannot be a val def, cf handled above
        // Note: 'st' cannot be another statement, cf handled by virtualization
        /*case q"{ $st; ..$sts }" if sts.nonEmpty => assert(false)
          q"${rec(st)}; ${rec(q"..$sts")}"*/
          

        case typ @ Ident(tn: TypeName) =>
          //debug("Typ", typ)
          if (typeSymbols isDefinedAt tn) tq"${typeSymbols(tn)}"
          else {
            checkType(typ.tpe.typeSymbol.asType, parent)
            typ
          }
          
        case tq"$tpt[..$targs]" =>
          //debug("TypApp", tpt, targs)
          tq"$tpt[..${targs map recNoType}]"
          
        case ValDef(mods, name, typ, valu) =>
          throw EmbeddingException("Statement in expression position: "+x+(if (debug.debugOptionEnabled) s" [${x.getClass}]" else ""))
          
        case _ => throw EmbeddingException("Unsupported feature: "+x+(if (debug.debugOptionEnabled) s" [${x.getClass}]" else ""))
        //case _ => debug("Unsupported feature: "+x); q""
          
      }
      
      
      
      
    }
      
    
    
    
    
    
    
    
    
    val expectedType = typeIfNotNothing(typedTreeType)
    
    val res = c.untypecheck(lift(finalTree, finalTree, expectedType)(Map()))
    debug("Result: "+showCode(res))
    
    
    
    
    val dslDefs = usedFeatures map {
      case ((typ,sym), name) =>
        val typeName = typ.widen match {
          case ThisType(sym) => sym.fullName
            
          // Cases eliminated by widen:
          //case ConstantType(_) => typ.typeSymbol.fullName
          //case SingleType(prefix,tsym) =>
            
          case TypeRef(prefix, tsym, args)
          if { val s = prefix.typeSymbol; s.isModule || s.isPackage || s.isModuleClass } => // NOTE: to keep in sync with [ScalaTypingMacros.typeRep]
            s"${prefix.typeSymbol.fullName}.${tsym.name}"
          case _ => throw EmbeddingException(s"Type $typ is not a stable type accessible from the root package.")
        }
        
        //val mod = typ.termSymbol.isModule // does not work if we splice-in the module, as in:  dsl"$predef.readInt"
        val mod = typ.typeSymbol.isModule || typ.typeSymbol.isModuleClass
        
        val alts = typ.member(sym.name).alternatives
        if (alts.size > 1)
             q"val $name = $Base.loadOverloadedSymbol($mod, $typeName, ${sym.name.toString}, ${alts.indexOf(sym)})"
        else q"val $name = $Base.loadSymbol($mod, $typeName, ${sym.name.toString})"
    }
    
    val dslTypes = usedTypes map {
      case (typ, name) => q"val $name = $Base.typeEvImplicit[$typ]"
    }
    
    
    // We surrounds names with _underscores_ to avoid potential name clashes (with defs, for example)
    unapply match {
        
      case None => //res
        
        // Note: 'freeVars' is kinda useless
        // TODO use typesToExtract
        
        /*
        // TODO rm dup checks
        
        //println(freeVars)
        
        // warning for duplicate freevar defs
        freeVars.groupBy(_._1).filter{case(name,defs) => defs.size > 1} foreach {
          case(name,defs) =>
            //debug("DUP"+(name,defs))
            val defsStr = defs.map{case(n,t) => s"$n: $t"}.mkString("\n\t", "\n\t", "") * (defs.size min 1)
            c.warning(c.enclosingPosition, s"Free variable $name is defined several times, as:$defsStr")
        }
        */
        
        // putting all freevars in one refinement
        //val context = tq"{..${freeVars map {case(n,t) => q"val $n: $t"}}}"
        
        // TODO check conflicts
        
        if (termScope.size > 1) debug(s"Merging scopes; glb($termScope) = ${glb(termScope)}")
        
        val ctxBase = //termScope.foldLeft(tq"AnyRef": Tree){}
          //tq"AnyRef with ..$termScope"
          tq"${glb(termScope)}"
        
        // putting every freevar in a different refinement (allows name redefinition!)
        val context = (freeVars ++ importedFreeVars).foldLeft(ctxBase){ //.foldLeft(tq"AnyRef": Tree){
          case (acc, (n,t)) => tq"$acc{val $n: $t}"
        }
        
        q"..$dslTypes; ..$dslDefs; Quoted[$retType, $context]($res)"
        
        //val typeInfo = q"type $$ConstructedType$$ = ${typedTree.tpe}"
        //q"$typeInfo; ..$dslTypes; ..$dslDefs; Quoted[$retType, $context]($res)"
        
      case Some(selector) =>
        
        //val typesToExtract = holeTypes map {
        //  case (n: TermName, tp) => n -> tq"Rep[$tp]"
        //  case (n: TypeName, tp) => n -> tq"TypeRep[$tp]"
        //}
        val noSefl = q"class A {}" match { case q"class A { $self => }" => self }
        val termHoleInfoProcessed = termHoleInfo mapValues {
          case (scp, tp) =>
            val scpTyp = CompoundTypeTree(Template(termScope map typeToTree, noSefl, scp map { sym => q"val ${sym.name}: ${sym.typeSignature}" }))
            (scpTyp, tp)
        }
        val termTypesToExtract = termHoleInfoProcessed map {
          case (name, (scpTyp, tp)) => name -> (
            if (splicedHoles(name)) tq"Seq[Quoted[$tp,$scpTyp]]"
            else tq"Quoted[$tp,$scpTyp]"
          )}
        val typeTypesToExtract = typeSymbols mapValues { sym => tq"QuotedType[$sym]" }
        
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
            q"_maps_._3(${name.toString}) map (r => Quoted[$tp,$scp](r))"
          case Left(name) =>
            //q"Quoted(_maps_._1(${name.toString})).asInstanceOf[${termTypesToExtract(name)}]"
            val (scp, tp) = termHoleInfoProcessed(name)
            q"Quoted[$tp,$scp](_maps_._1(${name.toString}))"
          case Right(name) =>
            //q"_maps_._2(${name.toString}).asInstanceOf[${typeTypesToExtract(name)}]"
            q"QuotedType[${typeSymbols(name)}](_maps_._2(${name.toString}))"
        }
        
        val valKeys = termHoles.filterNot(splicedHoles).map(_.toString)
        val typKeys = typeHoles.map(_.toString)
        val splicedValKeys = splicedHoles.map(_.toString)
        
        
        val defs = typeHoles map { typName =>
          val typ = typeSymbols(typName)
          val holeName = TermName(s"$$$typName$$hole")
          q"implicit val $holeName : TypeEv[$typ] = TypeEv(typeHole[${typ}](${typName.toString}))"
        }
        
        //val termType = tq"$Base.Quoted[${typedTree.tpe}, ${termScope.last}]"
        val termType = tq"SomeQ" // We can't use the type inferred for the pattern or we'll get type errors with things like 'x.erase match { case dsl"..." => }'
        
        val typeInfo = q"type $$ExtractedType$$ = ${typedTree.tpe}" // Note: typedTreeType can be shadowed by a scrutinee type
        val contextInfo = q"type $$ExtractedContext$$ = ${termScope.last}" // Note: the last element of 'termScope' should be the scope of the scrutinee...
        
        if (extrTyps.isEmpty) { // A particular case, where we have to make Scala understand we extract nothing at all
          assert(traits.isEmpty && defs.isEmpty)
            //def unapply(_t_ : SomeRep): Boolean = {
            //  _term_.extract(_t_.rep) match {
          q"""
          new {
            $typeInfo
            $contextInfo
            ..$defs
            ..$dslDefs
            ..$dslTypes
            def unapply(_t_ : $termType): Boolean = {
              val _term_ = $res
              $Base.extract(_term_, _t_.rep) match {
                case Some((vs, ts, fvs)) if vs.isEmpty && ts.isEmpty && fvs.isEmpty => true
                case Some((vs, ts, fvs)) => assert(false, "Expected no extracted objects, got values "+vs+", types "+ts+" and spliced values "+fvs); ???
                case None => false
              }
            }
          }.unapply($selector)
          """
        } else {
          // FIXME hygiene (Rep types...)
          
          /* Scala seems to be fine with referring to traits which declarations do not appear in the final source code,
          but it seems to creates confusion in tools like IntelliJ IDEA (although it's not fatal).
          To avoid it, the typed trait definitions are spliced here */
          val typedTraits = stmts collect { case t @ q"abstract trait $_ extends ..$_" => t }
          
            //def unapply(_t_ : SomeRep): $scal.Option[$extrTuple] = {
            //  _term_.extract(_t_.rep) map { _maps_ =>
          q"""{
          ..$typedTraits
          new {
            $typeInfo
            $contextInfo
            ..${defs}
            ..$dslDefs
            ..$dslTypes
            def unapply(_t_ : $termType): $scal.Option[$extrTuple] = {
              val _term_ = $res
              $Base.extract(_term_, _t_.rep) map { _maps_0_ =>
                val _maps_ = $Base.`private checkExtract`(${showPosition(c.enclosingPosition)}, _maps_0_)(..$valKeys)(..$typKeys)(..$splicedValKeys)
                (..$tupleConv)
              }
            }
          }}.unapply($selector)
          """
        }
    }
    
  }
  
  
  
  
  
  
  
  
  
  
  val scp = q"_root_.scp"
  
  val virtualize = transformer (rec => {
    //case q"..$sts; $r" => q"$scp.lib.Impure(..${sts map (st => q"() => $st")})($r)"
    case q"$s; ..$sts; $r" if !s.isDef => q"$scp.lib.Imperative(${rec(s)})(${rec(q"..$sts; $r")})"
    case q"if ($cond) $thn else $els" =>
      q"$scp.lib.IfThenElse(${rec(cond)}, ${rec(thn)}, ${rec(els)})"
    case q"while ($cond) $loop" =>
      q"$scp.lib.While(${rec(cond)}, ${rec(loop)})"
  })
  
  object SelectMember {
    private def getQual(t: c.Tree): Option[c.Tree] = t match {
      case q"$a.$m" => Some(a)
      case _ => None
    }
    def unapply(t: c.Tree): Option[(c.Tree,c.Tree)] = (t match {
      case q"$a[..$ts]" if ts.nonEmpty  => getQual(a) map ((_, t))
      case Apply(a, _) => unapply(a)
      case a => getQual(a) map ((_, t))
    }) ensuring(_.map(_._2.symbol != null) getOrElse true, s"[Internal Error] Extracted null symbol for $t")
  }
  
  
  object MultipleTypeApply {

    def apply(lhs: Tree, targs: List[Tree], argss: List[List[Tree]]): Tree = {
      val tpeApply = if (targs.isEmpty) lhs else TypeApply(lhs, targs)
      argss.foldLeft(tpeApply)((agg, args) => Apply(agg, args))
    }

    def unapply(value: Tree): Option[(Tree, List[Tree], List[List[Tree]])] = value match {
      case Apply(x, y) =>
        Some(x match {
          case MultipleTypeApply(lhs, targs, argss) =>
            (lhs, targs, argss :+ y) // note: inefficient List appension
          case TypeApply(lhs, targs) =>
            (lhs, targs, Nil)
          case _ =>
            (x, Nil, y :: Nil)
        })

      case TypeApply(lhs, targs) =>
        Some((lhs, targs, Nil))

      case _ => None
    }
  }
  
  object InferredType {
    def unapplySeq(tpt: TypeTree) = tpt match {
      case typ @ TypeTree() if typ.pos.isEmpty // an inferred type will have an empty position (apparently)
      && (typ.pos == NoPosition // sometimes these types have no position, like in the VirtualizedConstructs test...
      || typ.pos.point == 0) // only check for inferred types (types specified explicitly should have a non-null offset [I guess?])
      =>
        typ.tpe match {
          case TypeRef(_, RepeatedParamClass, t :: Nil) => Some(Seq(t))
          case _ => Some(Seq(typ.tpe))
        }
      case tq"$_[..$tps]" if tpt.tpe.typeArgs.size != tps.size => // for arguments to 'new'
        //Some(tps map (_.tpe))
        Some(tpt.tpe.typeArgs)
      case typ =>
        //debug(">> Not inferred:", tpt, typ.pos, typ.pos.point)
        None
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
  
  
}

/*

/* Note: the following matches Any/Nothing when inserted by the user (I think) */
//case tq"Any" | tq"Nothing" =>
/* Note: while the following matches those inferred, apparently */
//case typ @ TypeTree() =>

*/




















