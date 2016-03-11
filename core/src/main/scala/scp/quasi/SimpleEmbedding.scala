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
  * 
  * TODO: let-bind dsl defs so as to make generated code cleaner, and avoid redundancy
  * 
  * 
  */
class SimpleEmbedding[C <: whitebox.Context](val c: C) extends utils.MacroShared with ScopeAnalyser {
  import c.universe._
  import utils.MacroUtils._
  
  type Ctx = c.type
  val Ctx: Ctx = c
  
  val uni: c.universe.type = c.universe
  
  
  val debug = { val mc = MacroDebugger(c); mc[MacroDebug] }
  
  val scal = q"_root_.scala"
  
  val ByNameParamClass = c.universe.definitions.ByNameParamClass
  val RepeatedParamClass = c.universe.definitions.RepeatedParamClass // JavaRepeatedParamClass
  
  
  //def apply(tree: c.Tree, holes: Seq[Either[TermName,TypeName]], splicedTypes: Seq[Tree], unapply: Option[c.Tree]): c.Tree = {
  def apply(Base: Tree, tree: c.Tree, holes: Seq[Either[TermName,TypeName]], splicedTypes: Seq[(TypeName, Type, Tree)], unapply: Option[c.Tree]): c.Tree = {
    
    val (termHoles, typeHoles) = (holes.collect{case Left(n) => n}.toSet, holes.collect{case Right(n) => n}.toSet)
    
    //val splicedType = splicedTypes.map(_._2.typeSymbol.asType).toSet
    
    val traits = typeHoles map { typ => q"trait $typ" }
    val vals = termHoles map { vname => q"val $vname: Nothing = ???" }
    
    
    val hasImplicit = mutable.Buffer[Type]() // would be better with a Set, but would have to override equals and hashCode to conform to =:=
                                             // note: or simply widen the type until fixed point... (would that be enough?)
    val (spliceImplicits, types) = splicedTypes flatMap {
      case (name, typ, trep) =>
        if (hasImplicit exists (_ =:= typ)) None
        else {
          hasImplicit += typ
          Some((q"implicit def ${name.toTermName} : TypeEv[$typ] = TypeEv($trep)",
            //q"trait ${typ.typeSymbol.name.toTypeName}"))
            q"type ${typ.typeSymbol.name.toTypeName} = $typ"))
        }
    } unzip;
    
    val virtualizedTree = virtualize(tree)
    debug("Virtualized:", virtualizedTree)
    
    var termScope: List[Type] = Nil // will contain the scope bases of all spliced stuff + in xtion, possibly the scrutinee's scope
    var importedFreeVars = mutable.Map[TermName, Type]() // will contain the free vars of the spliced stuff
    
    val ascribedTree = unapply match {
      case Some(t) =>
        debug("Scrutinee type:", t.tpe, t.tpe.typeSymbol, t.tpe.typeSymbol.name)
        t.tpe match {
          case tpe @ TypeRef(_, _, tp :: sc :: Nil) => // Note: cf. [INV:Quasi:reptyp] we know this type is of the form Rep[_], as is ensured in Quasi.unapplyImpl
            termScope ::= sc
            if (tp.typeSymbol.isClass) {
              val purged = purgedTypeToTree(tp)
              debug("Purged matched type:", purged)
              Some(q"$virtualizedTree: $purged")
            } else None
          case tp => assert(false, "Unexpected type: "+tp.typeSymbol); ??? // cf. [INV:Quasi:reptyp]
        }
      case None => None
    }
    
    def shallowTree(t: Tree) = {
      // we need tp introduce a dummy val in case there are no statements; otherwise we may remove the wrong statements when extracting the typed term
      val nonEmptyVals = if (types.size + traits.size + vals.size == 0) Set(q"val $$dummy$$ = ???") else vals
      val st = q"..$types; ..$traits; ..$nonEmptyVals; $t"
      if (debug.debugOptionEnabled) debug("Shallow Tree: "+st)
      st
    }
    
    val typedTree = ascribedTree flatMap { t =>
      val st = shallowTree(t)
      try {
        val typed = c.typecheck(st)
        val q"..$stmts; $finalTree: $_" = typed
        Some(q"..$stmts; $finalTree")
      } catch {
        case e: TypecheckException =>
          debug("Ascribed tree failed to typecheck: "+e.msg)
          debug("  in:\n"+showPosition(e.pos))
          None
      }
    } getOrElse {
      try {
        val r = c.typecheck(shallowTree(virtualizedTree))
        if (ascribedTree.nonEmpty) {
          val expanded = unapply.get.tpe map (_.dealias)
          c.warning(c.enclosingPosition, s"""Scrutinee type: ${unapply.get.tpe}${
            if (expanded == unapply.get.tpe) ""
            else s"\n|  which expands to: $expanded"
          }
          |  seems incompatible with or is more specific than pattern type: Rep[${r.tpe}]${
            if (typeHoles.isEmpty) ""
            else s"\n|  perhaps one of the type holes is unnecessary: " + typeHoles.mkString(", ")
          }
          |Ascribe the scrutinee with ': Rep[_]' to remove this warning.""".stripMargin)
          // ^ TODO
        }
        r
      } catch {
        case e: TypecheckException =>
          throw EmbeddingException(e.msg) // TODO better reporting
      }
    }
    
    val q"..$stmts; $finalTree" = typedTree
    
    val typeSymbols = stmts collect {
      case t @ q"abstract trait ${name: TypeName} extends ..$_" => (name: TypeName) -> t.symbol
    } toMap;
    
    debug("Typed: "+finalTree)
    
    val retType = finalTree.tpe
    
    
    
    
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
    val typeHoleInfo = mutable.Map[Name, Symbol]()
    
    //val usedFeatures = mutable.Buffer[(TermName, DSLDef)]()
    val usedFeatures = mutable.Map[DSLDef, TermName]()
    
    val freeVars = mutable.Buffer[(TermName, Type)]()
    
    /** `parent` is used to display an informative tree in errors; `expectedType` for knowing the type a hole should have  */
    def lift(x: c.Tree, parent: Tree, expectedType: Option[Type])(implicit ctx: Map[TermSymbol, TermName]): c.Tree = {
      //debug(x.symbol,">",x)
      
      def rec(x1: c.Tree)(implicit ctx: Map[TermSymbol, TermName]): c.Tree = lift(x1, x, None)
      
      x match {
        
        //case q"$base.spliceOpen(${Literal(Constant(code: String))})" =>
        //  return injectScope(c.parse(code)) // TODO catch parse error!
          
        //case q"$base.open(${Literal(Constant(name: String))})" => // doesn't match...
        //  return ???
          
          
        case Ident(name) if termHoles(name.toTermName) =>
          
          val _expectedType = expectedType match {
            case None => throw EmbeddingException(s"No type info for hole position") // TODO B/E
            case Some(tp) =>
              assert(!SCALA_REPEATED(tp.typeSymbol.fullName.toString))
              if (unapply isEmpty)
                freeVars += (name.toTermName -> tp)
              val scp = ctx.keys.toList
              termHoleInfo(name.toTermName) = scp -> tp
              typeToTreeChecking(tp, parent)
          }
          
          //debug(expectedType, _expectedType)
          
          //debug("Extr", _expectedType)
          
          //typesToExtract(name) = tq"Rep[${_expectedType}]"
          return q"hole[${_expectedType}](${name.toString})"
          
          
        case typ @ TypeTree() if typeHoles(typ.symbol.name.toTypeName) =>
          val name = typ.symbol.name.toTypeName
          
          val tsym = typeSymbols(name)
          
          //typesToExtract(name) = tq"TypeRep[${tsym}]"
          //holeTypes(name) = tsym.typeSignature
          typeHoleInfo(name) = tsym
          
          return tq"${tsym}"
          
        case _ =>
          
      }
      
      if (x.tpe != null && x.tpe.typeSymbol.isType) checkType(x.tpe.typeSymbol.asType, parent)
      
      x match {
          
        case q"" => throw EmbeddingException("Empty program fragment")
          
        case c @ Literal(Constant(x)) =>
          q"const($c)"
          //q"$base.const($c)"
          
          
        case Ident(name) if ctx isDefinedAt x.symbol.asTerm => q"${ctx(x.symbol.asTerm)}"
          
        case id @ Ident(name) =>
          throw EmbeddingException(s"Cannot refer to local variable '$name' (from '${id.symbol.owner}'). " +
            s"Use splicing '$$$name' in order to include the value in the DSL program.")
          
        case q"$from.this.$name" =>
          throw EmbeddingException(s"Cannot refer to member '$name' from '$from'")
          
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
          
        case q"${vdef @ q"$mods val ${name: TermName}: $_ = $v"}; ..$b" =>
          //varCount += 1
          //val name = TermName("vdef"+varCount)
          //q"letin(${rec(v)}, ($name: Rep[${vdef.symbol.typeSignature}]) => ${rec(q"..$b")(ctx + (vdef.symbol -> name))})"
          q"letin(${rec(v)}, ($name: Rep) => ${rec(q"..$b")(ctx + (vdef.symbol.asTerm -> name))})"
         
        case q"val $p = $v; ..$b" =>
          rec(q"$v match { case $p => ..$b }")
          
          
        case q"$base.splice[$t]($idt)" => q"$base.spliceDeep[$t]($idt)" // TODO generalize; a way to say "do not lift"
        //case q"$base.splice[$t]($idt)($ev)" => q"$base.spliceDeep[$t]($idt)"
        //case q"$base.splice[$t,$scp]($idt)($ev)" =>
        case q"$base.splice[$t,$scp]($idt)" =>
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
          
          debug("env",varsInScope,"; capt",captured,"; free",free)
          
          q"$base.spliceDeep($idt.rep).subs(..${bindings})"
          
          
        case SelectMember(obj, f) =>
          
          val path = TypeName(x.symbol.fullName).decodedName.toString
          
          val dslDef = DSLDef(path, x.symbol.info.toString, obj.tpe.termSymbol.isModule)
          debug("Dsl feature: "+dslDef.deepName)
          
          //if (!lang.defs(dslDef)) throw EmbeddingException(s"Feature '$dslDef' is not supported in language '$lang'")
          
          val fname = TermName(dslDef.deepName).encodedName.toTermName
          
          //val selfTargs = obj.tpe match {
          //  case TypeRef(_, _, ts) => ts map (typeToTreeChecking(_, parent)) //map typeToTree//(adaptType)
          //  case _ => Nil
          //}
          
          def refMtd(mtd: DSLDef) = {
            //val name = c.freshName[TermName](mtd.shortName)
            //usedFeatures += (mtd -> name)
            val name = usedFeatures.getOrElseUpdate(mtd, {
              //c.freshName[TermName](mtd.shortName)
              c.freshName[TermName](TermName(mtd.shortName).encodedName.toTermName)
            })
            q"$name"
          }
          
          x match {
              
            case q"$a.$_" =>
              //if (dslDef.module) q"$fname"
              //else
              //  q"$fname[..$selfTargs](${lift(obj, x, Some(obj.tpe))})"
              //??? // TODO
              val self = if (dslDef.module) q"None" else q"Some(${lift(obj, x, Some(obj.tpe))})"
              //val mtd = q"scp.lang.DSLDef(${dslDef.fullName}, ${dslDef.info}, ${dslDef.module})"
              
              q"$Base.dslMethodApp($self, ${refMtd(dslDef)}, Nil, Nil, null)" // TODO tp: TypeRep
              
              
            case MultipleTypeApply(_, targs, argss) =>
              
              def processedTargs = targs map rec
              
              val paramTpess = f.tpe.paramLists map (_ map (_ typeSignature))
              val paramsStream = paramTpess.map(paramTpes => paramTpes.lastOption match {
                case Some(TypeRef(pre, RepeatedParamClass, List(tp))) =>
                  Stream(paramTpes.take(paramTpes.size-1): _*) ++ Stream.continually(tp)
                case Some(_) | None =>
                  Stream(paramTpes: _*)
              })
              
              //debug("ARGS",argss)
              
              def mkArgs = (argss zip paramsStream) map {
                case (args, params) => (args zip params) map {
                  case (arg, TypeRef(_, ByNameParamClass, param :: Nil)) => lift(arg, x, Some(param))
                  case (arg, param) => lift(arg, x, Some(param))
              }}
              
              //if (dslDef.module)
              //  q"$fname[..${processedTargs}](...${mkArgs})"
              //else {
              //  val self = lift(obj, x, Some(obj.tpe))  // Executed here to keep syntactic order!
              //  q"$fname[..${(selfTargs ++ processedTargs)}](...${List(self) :: mkArgs})"
              //}
              
              val self = if (dslDef.module) q"None" else q"Some(${lift(obj, x, Some(obj.tpe))})"
              //val mtd = q"scp.lang.DSLDef(${dslDef.fullName}, ${dslDef.info}, ${dslDef.module})"
              val targsTree = q"List(..${processedTargs map (tp => q"typeEv[$tp].rep")})"
              val args = q"List(..${mkArgs map (xs => q"List(..$xs)")})"
              
              q"$Base.dslMethodApp($self, ${refMtd(dslDef)}, $targsTree, $args, null)" // TODO tp: TypeRep
              
              
              
            //case q"new $tp[..$targs]" => ??? // TODO
              
              // TODO $a.$f(...$args)
              // TODO $f[..$targs](...$args)
          }
        
        case Apply(f,args) => throw EmbeddingException(s"Cannot refer to function '$f' (from '${x.symbol.owner}')") // TODO handle rep function use here?
          
        case q"$f(..$args)" =>
          q"app(${rec(f)}, ${args map rec})"
          //q"$base.app(${rec(f)}, ${args map rec})"
          
        //case q"($p) => $body" =>
        case q"(${p @ ValDef(mods, name, _, _)}) => $body" =>
          //varCount += 1
          //val name = TermName("x"+varCount)
          //q"abs(($name: Rep[${p.symbol.typeSignature}]) => ${rec(body)(ctx + (p.symbol.asTerm -> name))})"
          q"$Base.abs[${p.symbol.typeSignature}, ${body.tpe}](($name: Rep) => ${rec(body)(ctx + (p.symbol.asTerm -> name))})"
          
        // TODO
        //case q"(..$params) => $expr" => ???
        //  
        //case q"$v match { case ..$cases }" =>
        //  PatMat(liftn(v), cases map {
        //    case cq"$pat if $cond => $body" => MatchClause(pat match {
        //      case pq"()" => liftn(q"()")
        //      case _ => ??? // TODO
        //    }, liftn(cond), liftn(body)) // TODO
        //  })
          
          
        case q"$t: $typ" =>
          val tree = lift(t, x, Some(typ.tpe)) // Executed before `rec(typ)` to keep syntactic order!
          q"ascribe[${rec(typ)}](${tree})"
        
        
        case q"{ $st; ..$sts }" if sts.nonEmpty =>
          // Note: 'st' cannot be a val def, cf handled above
          q"${rec(st)}; ${rec(q"..$sts")}"
          

        case typ @ Ident(tn: TypeName) =>
          //debug("Typ", typ)
          if (typeSymbols isDefinedAt tn) tq"${typeSymbols(tn)}"
          else {
            checkType(typ.tpe.typeSymbol.asType, parent)
            typ
          }
          
        case tq"$tpt[..$targs]" =>
          //debug("TypApp", tpt, targs)
          tq"$tpt[..${targs map rec}]"
          
        case _ => throw EmbeddingException("Unsupported feature: "+x+(if (debug.debugOptionEnabled) s" [${x.getClass}]" else ""))
        //case _ => debug("Unsupported feature: "+x); q""
          
      }
      
      
      
      
    }
      
    
    
    
    
    
    
    
    
    
    
    val res = c.untypecheck(lift(finalTree,finalTree,None)(Map()))
    debug("Result: "+showCode(res))
    
    
    
    
    val dslDefs = usedFeatures map {
      case (DSLDef(fullName, info, module), name) => q"val $name = scp.lang.DSLDef(${fullName}, ${info}, ${module})"
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
        
        debug(s"Merging scopes; glb($termScope) = ${glb(termScope)}")
        
        val ctxBase = //termScope.foldLeft(tq"AnyRef": Tree){}
          //tq"AnyRef with ..$termScope"
          tq"${glb(termScope)}"
        
        // putting every freevar in a different refinement (allows name redefinition!)
        val context = (freeVars ++ importedFreeVars).foldLeft(ctxBase){ //.foldLeft(tq"AnyRef": Tree){
          case (acc, (n,t)) => tq"$acc{val $n: $t}"
        }
        
        q"..$types; ..$spliceImplicits; ..$dslDefs; Quoted[$retType, $context]($res)"
        
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
        val termTypesToExtract = termHoleInfoProcessed mapValues {
          case (scpTyp, tp) =>
            tq"Quoted[$tp,$scpTyp]"
        }
        val typeTypesToExtract = typeHoleInfo mapValues {
          sym => tq"QuotedType[$sym]"
        }
        
        val extrTyps = holes.map {
          case Left(vname) => termTypesToExtract(vname)
          case Right(tname) => typeTypesToExtract(tname) // TODO B/E
        }
        debug("Extracted Types: "+extrTyps.mkString(" "))
        
        val extrTuple = tq"(..$extrTyps)"
        debug("Type to extract: "+extrTuple)
        
        val tupleConv = holes.map {
          case Left(name) =>
            //q"Quoted(_maps_._1(${name.toString})).asInstanceOf[${termTypesToExtract(name)}]"
            val (scp, tp) = termHoleInfoProcessed(name)
            q"Quoted[$tp,$scp](_maps_._1(${name.toString}))"
          case Right(name) =>
            //q"_maps_._2(${name.toString}).asInstanceOf[${typeTypesToExtract(name)}]"
            q"QuotedType[${typeHoleInfo(name)}](_maps_._2(${name.toString}))"
        }
        
        val valKeys = q"Set(..${termHoles.map(_.toString)})"
        val typKeys = q"Set(..${typeHoles.map(_.toString)})"
        
        
        val defs = typeHoles flatMap { typName =>
          val typ = typeSymbols(typName)
          val holeName = TermName(s"$$$typName$$hole")
          //List(q"val $holeName : TypeRep[${typ}] = typeHole[${typ}](${typName.toString})",
          List(q"val $holeName : TypeRep = typeHole[${typ}](${typName.toString})",
          q"implicit def ${TermName(s"$$$typName$$implicit")} : TypeEv[$typ] = TypeEv($holeName)")
        }
        
        
        if (extrTyps.isEmpty) { // A particular case, where we have to make Scala understand we extract nothing at all
          assert(traits.isEmpty && defs.isEmpty)
            //def unapply(_t_ : SomeRep): Boolean = {
            //  _term_.extract(_t_.rep) match {
          q"""
          new {
            ..$spliceImplicits
            ..$dslDefs
            def unapply(_t_ : SomeQ): Boolean = {
              val _term_ = $res
              $Base.extract(_term_, _t_.rep) match {
                case Some((vs, ts)) if vs.size == 0 && ts.size == 0 => true
                case Some((vs, ts)) => assert(false, "Expected no extracted objects, got values "+vs+" and types "+ts); ???
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
            ..$spliceImplicits
            ..${defs}
            ..$dslDefs
            def unapply(_t_ : SomeQ): $scal.Option[$extrTuple] = {
              val _term_ = $res
              $Base.extract(_term_, _t_.rep) map { _maps_ =>
                assert(_maps_._1.keySet == $valKeys, "Extracted value keys "+_maps_._1.keySet+" do not correspond to specified keys "+$valKeys)
                assert(_maps_._2.keySet == $typKeys, "Extracted type keys "+_maps_._2.keySet+" do not correspond to specified keys "+$typKeys)
                (..$tupleConv)
              }
            }
          }}.unapply($selector)
          """
        }
    }
    
  }
  
  def virtualize(t: Tree) = t transform {
    case q"if ($cond) $thn else $els" =>
      q"_root_.tagless.lib.deep.IfThenElse($cond, $thn, $els)"
  }
  
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
  
  
  /** Creates a type tree from a Type, where all non-class types are replaced with existentials */
  def purgedTypeToTree(tpe: Type): Tree = {
    val existentials = mutable.Buffer[TypeName]()
    def rec(tpe: Type): Tree = tpe match {
      case _ if !tpe.typeSymbol.isClass =>
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




















