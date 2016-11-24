package squid
package scback

import collection.mutable
import ch.epfl.data.sc._
import ch.epfl.data.sc.pardis.deep.scalalib.collection.ArrayBufferIRs.ArrayBuffer
import pardis.ir.{ANFNode, Base}
import squid.utils._

import scala.language.experimental.macros
import scala.reflect.macros.TypecheckException
import meta.RuntimeUniverseHelpers.sru
import squid.lang
import utils.MacroUtils.{MacroSetting, MacroDebug, MacroDebugger}

import scala.reflect.macros.whitebox

class AutoBinder[B <: pardis.ir.Base, SB <: lang.Base](val _b_ : B, val _sb_ : SB) {
  
  /** type argss for self's type, method type args, method args */
  type MtdMaker = (List[_b_.Rep[Any]], List[_b_.TypeRep[Any]], List[_b_.TypeRep[Any]]) => _b_.Rep[_]  // currently returns Rep because it uses the implicit Rep class methods
  //type MtdMaker = (List[_b_.Rep[Any]], List[_b_.TypeRep[Any]], List[_b_.TypeRep[Any]]) => ANFNode   // TODO call the Def-making methods instead
  
  implicit def coerceRep[A](r: _b_.Rep[_]): _b_.Rep[A] = r.asInstanceOf[_b_.Rep[A]]
  implicit def coerceTypeRep[A](tp: _b_.TypeRep[_]): _b_.TypeRep[A] = tp.asInstanceOf[_b_.TypeRep[A]]
      
  val map = mutable.Map[_sb_.MtdSymbol, MtdMaker]()
  
}

/** TODO also bind method for deep companion objects */
object AutoBinder {
  
  def apply(base: Base, sb: lang.Base): AutoBinder[base.type,sb.type] = macro applyImpl
  @MacroSetting(debug = true) def dbg(base: Base, sb: lang.Base): AutoBinder[base.type,sb.type] = macro applyImpl
  
  import scala.reflect.macros.blackbox
  def applyImpl(c: blackbox.Context)(base: c.Tree, sb: c.Tree): c.Tree = {
    import c.universe._
    
    val debug = { val mc = MacroDebugger(c.asInstanceOf[whitebox.Context]); mc[MacroDebug] }
    
    //var n = 0; def disp(x: Any) = x oh_and println(s"$n> $x") oh_and (n += 1)
    //def disp(x: Any) = x
    def disp(x: Any) = debug(x)
    
    val anyMem = typeOf[AnyRef].members.toSet
    
    val RepSym = typeOf[pardis.ir.Expression[_]].typeSymbol
    val TypeRepSym = typeOf[pardis.types.PardisType[_]].typeSymbol
    
    
    def findCorrespondingShallow(mtd: MethodSymbol, isCtor: Boolean, originalType: Type) = {
      val name = mtd.name
      
      val symByName = if (isCtor) originalType.member(termNames.CONSTRUCTOR)
        else originalType.member(name)
      
      (symByName If (_.alternatives.size == 1) filter (_.isMethod) map (_.asMethod)) orElse {
          //disp(s"Method $name in $cls does not correspond to an actual method or is overloaded.")
          
          val tps = originalType.typeSymbol.typeSignature.typeParams
          val tpSyms = originalType.typeArgs map (_.typeSymbol)
          val tpTops = tps.map(_.typeSignature).map { case TypeBounds(a,b) => b }
          val saneTyp = internal.typeRef(originalType.typeSymbol.owner.asType.toType, originalType.typeSymbol, tpTops)
          
          val tparamTops = mtd.typeParams map { case TypeBounds(a,b) => b  case _ => typeOf[Any] }
          val paramTypesMaybe = mtd.paramLists.headOption map (_ map (_.typeSignature.baseType(RepSym)) map { case NoType => None case TypeRef(_,_,tp::Nil) => Some(tp) })
          val paramTypes = paramTypesMaybe flatMap (ps => if (ps.forall(_ isDefined)) ps map (_ get) into Some.apply else None)
          val saneParams = paramTypes map (_ map (pt => q"??? : ${pt.substituteTypes(tpSyms ++ mtd.typeSignature.typeArgs.map(_.typeSymbol), tpTops ++ tparamTops)}") ) toList
          
          val tree = if (isCtor) q"new $saneTyp(...${saneParams})"
            else q"(??? : $saneTyp).${name}[..${ tparamTops }](...${saneParams})"
          
          
          s"Typing "+showCode(tree) |> disp
          
          try {
            val typed = c.typecheck(tree)
            typed match {
              case m if m.symbol.isMethod =>
                "Method Symbol: "+m.symbol |> disp
                Some(m.symbol.asMethod)
              case _ =>
                // TODO warn
                None
            }
            
          }
          catch {
            case e: TypecheckException =>
              c.warning(c.enclosingPosition, s"Typing failed for `${showCode(tree)}` with: "+e.msg)
              None
          }
          
      }
      
    }
    
    /** Methods marked by the user as to be ignore, with syntax {{{type `ignore fullName`}}} */
    val ignorePrefix = "ignore "
    val ignore = (for {
      mem <- base.tpe.members
      name = mem.name.decodedName.toString
      if mem.isType && name.startsWith(ignorePrefix)
      mtdName = name drop ignorePrefix.length
    } yield mtdName).toSet
    
    debug("IGNORING: "+ignore)
    
    
    type E = Either[ClassSymbol, Tree]
    
    val __newPrefix = "__new"
    val ignoredName = Set(__newPrefix, "__newDef", "__newVar", "__newVarNamed")
    
    val ctorMtds = (for {
      mem <- base.tpe.members
      if mem.isMethod
      mtd = mem.asMethod
      mname = mtd.name.toString
      if mname startsWith __newPrefix
      if !ignoredName(mname)
    } yield {
      val typeName = mname drop __newPrefix.length
      base.tpe.member(TypeName(typeName)) |> {
        case NoSymbol =>
          c.warning(c.enclosingPosition, s"Could not find '$typeName' type corresponding to cake $mtd")
          Nil
        case typSym =>
          val typ = typSym.asType.toType
          val originalSym = findCorrespondingShallow(mtd, true, typ)
          (typ -> ((Right(q"_b_.${TermName(__newPrefix+typSym.name)}"):E) -> mtd -> originalSym)) :: Nil
      }
    }).flatten
    
    
    val ignoredObjName = Set("Def", "Record", "Predef") //, "ConcatDynamic", "RepToSeqRep")
    
    val objectMtds = (for {
      mem <- base.tpe.members
      if mem.isModule
      obj = mem.asModule
      //_ = disp(obj, obj.companion)
      if obj.companion If (_ =/= NoSymbol) filter (_.asClass.isCaseClass) isEmpty;
      if !ignoredObjName(obj.name.toString)
      mems = obj.typeSignature.members.toSet -- anyMem filterNot (_ isConstructor)
      if mems.nonEmpty
      _ = s"<<< ( ${obj} ) >>> " |> disp
      tree = q"${obj.name}"
      typ <- scala.util.Try(c.typecheck(tree).tpe) match {
        case scala.util.Success(typ) => Some(typ)
        case scala.util.Failure(e: TypecheckException) =>
          // Things that fail may be Java classes like "String" which cannot be typechecked as values
          c.warning(c.enclosingPosition, s"Typing failed for `${showCode(tree)}` with: "+e.msg)
          None
      }
    } yield {
      
      val symMappings = for {
        mem <- mems
        if mem.isMethod
        mtd = mem.asMethod
      } yield {
        mtd |> disp
        
        val originalSym = findCorrespondingShallow(mtd, false, typ)
        originalSym |> disp
        (typ -> ((Right(q"_b_.${obj.name}.${mtd.name}"):E) -> mtd -> originalSym))
      }
      
      symMappings
      
    }).flatten
    
    
    val implClassMtds = (for {
      mem <- base.tpe.members
      if mem.isClass
      cls = mem.asClass  
      if cls.isImplicit
    } yield {
      s"<<< ( ${cls} ) >>> " |> disp
      
      val Seq(ctor) -> mems = cls.typeSignature.members partition (_ isConstructor)
      val repTyp = ctor.asMethod.paramLists.head.head.typeSignature
      
      repTyp.baseType(RepSym) match {
        case TypeRef(pre, RepSym, typ::Nil) =>
          val symMappings = for {
            mem <- mems
            if !anyMem(mem)
            if mem.isMethod
            mtd = mem.asMethod
          } yield {
            val originalSym = findCorrespondingShallow(mtd, false, typ)
            Left(cls) -> mtd -> originalSym
          }
          symMappings map (typ -> _)
      }
      
    }).flatten
    
    
    val allClasses = ctorMtds ++ implClassMtds ++ objectMtds
    //val allClasses = ctorMtds
    //val allClasses = objectMtds
    
    
    
    disp("\n-----------\n")
    
    object Helpers extends {val uni: c.universe.type = c.universe} with meta.UniverseHelpers[c.universe.type]
    import Helpers.{encodedTypeSymbol, RepeatedParamClass}
    
    import quasi.{MetaBases, ModularEmbedding}
    object MBM extends MetaBases {
      val u: c.universe.type = c.universe
      //def freshName(hint: String) = c.freshName(TermName(hint))
      private var cnt = 0
      def freshName(hint: String) = TermName(s"_${cnt}_$hint") oh_and (cnt += 1)
    }
    val MB = new MBM.MirrorBase(q"_sb_")
    
    val modEmb = new ModularEmbedding[sru.type,MB.type](sru, MB, debug = x => disp(x))
    
    val fillingCode = allClasses flatMap {
      
      case typ -> ((_, mtd) -> Some(origMtd))
      if mtd.paramLists.flatten exists (p => p.isImplicit && !p.name.toString.startsWith("overload") && (p.typeSignature.baseType(TypeRepSym) == NoType))
      =>
        val impls = mtd.paramLists.flatten filter (p => p.isImplicit && (p.typeSignature.baseType(TypeRepSym) == NoType))
        c.warning(c.enclosingPosition, s"Method ${mtd.name} from ${mtd owner} was evicted because it has non-TypeRep implicit parameters: $impls")
        Nil
        
      case typ -> ((trunk,mtd) -> Some(origMtd))
      if !ignore(origMtd.fullName) && !ignore(origMtd.owner.fullName)
      && !ignore(mtd.fullName) && !ignore(mtd.owner.fullName)
      =>
        val t = modEmb.loadTypSymbol(encodedTypeSymbol(origMtd.owner.asType))
        val m = modEmb.getMtd(t, origMtd.asMethod.asInstanceOf[modEmb.uni.MethodSymbol])
        
        val tps = typ.typeSymbol.typeSignature.typeParams
        val tpTops = tps.map(_.typeSignature).map {
          case TypeBounds(a,b) => b
        }
        
        val tpSyms = typ.typeArgs map (_.typeSymbol)
        val originalType = typ
        //val saneTyp = internal.typeRef(originalType.typeSymbol.owner.asType.toType, originalType.typeSymbol, tpTops)
        
        // TODO factor with code in `findCorrespondingShallow`
        val tparamTops = mtd.typeParams map (_ typeSignature) map { case TypeBounds(a,b) => b  case _ => typeOf[Any] }
        //disp("MTD: "+mtd); disp("TPT: "+tparamTops)
        val explicitParams = mtd.paramLists collect { case ps if ps.headOption map (p => !p.isImplicit) getOrElse true => ps }
        val paramTypes = explicitParams map (_ map (_ typeSignature))
        def saneParams(offset: Int) = paramTypes.zipWithIndex map {case ps->j => ps.zipWithIndex.map {
          case TypeRef(_,RepeatedParamClass,pt::Nil)->i =>
            assert(j == paramTypes.indices.last) // For convenience, assume the repeated argument is the very last argument in the argument lists
            q"rs.drop(${j+i+offset}).asInstanceOf[List[${pt.substituteTypes(tpSyms ++ mtd.typeParams, tpTops ++ tparamTops)}]]: _*"
          case pt->i => q"rs(${j+i+offset}).asInstanceOf[${pt.substituteTypes(tpSyms ++ mtd.typeParams, tpTops ++ tparamTops)}]"
            // ^ Note: asInstanceOf is necessary here as Scala will not be able to infer some of the tricky argument types
        }}
        
        val overloadPrefix = "overload"
        
        def implArgs(pickFrom: Tree) = {
          var i = 0
          val args = mtd.paramLists.flatten filter (_ isImplicit) map {
            case p if p.name.toString startsWith overloadPrefix => q"_b_.${ TermName("overloaded" + (p.name.toString drop overloadPrefix.length)) }"
            case p if p.typeSignature.baseType(TypeRepSym) =/= NoType => q"$pickFrom($i)" oh_and (i += 1)
            //case _ => "??? "+mtd.name |> disp; ??? // TODO proper error
          }
          if (args isEmpty) Nil
          else List(args)
        }
        
        trunk match {
            
          case Left(cls) =>
            // Used to be `(rs(0).asInstanceOf[_b_.Rep[${saneTyp}]])`, but now `coerceRep` does it for us.
            q"map += $m -> ((rs: List[_b_.Rep[Any]], ts: List[_b_.TypeRep[Any]], ts2: List[_b_.TypeRep[Any]]) => new _b_.${cls.name}[..${tpTops}](rs(0))(...${
              if (tpTops.isEmpty) Nil
              else List(tpTops.zipWithIndex map {case tp->i => q"ts($i)"})
            }).${mtd.name}(...${ saneParams(1) })(...${ implArgs(q"ts2") }))" :: Nil
            
          case Right(pre) =>
            q"map += $m -> ((rs: List[_b_.Rep[Any]], ts: List[_b_.TypeRep[Any]], ts2: List[_b_.TypeRep[Any]]) => ${pre}[..${/*saneTargs*/ tpTops}](...${
              saneParams(0)
            })(...${ implArgs(q"ts") }))" :: Nil
            
        }
        
      case _ -> (_ -> None) => Nil
      case _ -> (_ -> Some(m)) => Nil  // `m` was in `ignored`
        
    }
    
    val ret = q"""new AutoBinder[${base.tpe}, ${sb.tpe}]($base,$sb) {
      ..${MB.mkSymbolDefs}; ..$fillingCode
    }"""
    
    debug(s"Generated: ${showCode(ret)}")
    println(s"Generated AutoBinder: ${showCode(ret).count(_ == '\n')} lines of code for ${allClasses.size} deep methods.")
    
    ret
  }
  
}

