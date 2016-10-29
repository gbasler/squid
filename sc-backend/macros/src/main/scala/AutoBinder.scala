package scp
package scback

import collection.mutable

import ch.epfl.data.sc._
import pardis.ir.Base
import scp.utils._

import scala.language.experimental.macros
import scala.reflect.macros.TypecheckException
import meta.RuntimeUniverseHelpers.{sru}

class AutoBinder[B <: pardis.ir.Base, SB <: lang2.Base](val _b_ : B, val _sb_ : SB) {
  
  /** type argss for self's type, method type args, method args */
  type MtdMaker = (List[_b_.Rep[Any]], List[_b_.TypeRep[Any]], List[_b_.TypeRep[Any]]) => _b_.Rep[_]
  
  val map = mutable.Map[_sb_.MtdSymbol, MtdMaker]()
  
}

/** TODO also bind method for deep companion objects */
object AutoBinder {
  
  def apply(base: Base, sb: lang2.Base): AutoBinder[base.type,sb.type] = macro applyImpl
  
  import scala.reflect.macros.blackbox
  def applyImpl(c: blackbox.Context)(base: c.Tree, sb: c.Tree) = {
    import c.universe._
    
    //var n = 0; def disp(x: Any) = x oh_and println(s"$n> $x") oh_and (n += 1)
    def disp(x: Any) = x
    
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
          val saneTyp = originalType.substituteTypes(tpSyms, tpTops)
          
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
    
    
    type E = Either[ClassSymbol, TypeSymbol]
    
    val __newPrefix = "__new"
    val ignoredName = Set(__newPrefix, "__newDef", "__newVar")
    
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
          (typ -> ((Right(typSym.asType):E) -> mtd -> originalSym)) :: Nil
      }
    }).flatten
    
    val implClassMtds = (for {
      mem <- base.tpe.members
      if mem.isClass
      cls = mem.asClass  
      if cls.isImplicit
    } yield {
      s"<<< ( ${cls.name} ) >>> " |> disp
      
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
    
    disp("\n-----------\n")
    
    object Helpers extends {val uni: c.universe.type = c.universe} with meta.UniverseHelpers[c.universe.type]
    import Helpers.encodedTypeSymbol
    
    import quasi2.{MetaBases, ModularEmbedding}
    object MBM extends MetaBases {
      val u: c.universe.type = c.universe
      //def freshName(hint: String) = c.freshName(TermName(hint))
      private var cnt = 0
      def freshName(hint: String) = TermName(s"_${cnt}_$hint") oh_and (cnt += 1)
    }
    val MB = new MBM.MirrorBase(q"_sb_")
    
    val modEmb = new ModularEmbedding[sru.type,MB.type](sru, MB, debug = x => disp(x))
    
    val allClasses = ctorMtds ++ implClassMtds
    //val allClasses = ctorMtds
    
    val fillingCode = allClasses flatMap {
      
      case typ -> ((_, mtd) -> Some(origMtd))
      if mtd.paramLists.flatten exists (p => p.isImplicit && !p.name.toString.startsWith("overload") && (p.typeSignature.baseType(TypeRepSym) == NoType))
      =>
        val impls = mtd.paramLists.flatten filter (p => p.isImplicit && (p.typeSignature.baseType(TypeRepSym) == NoType))
        c.warning(c.enclosingPosition, s"Method ${mtd} from ${mtd owner} was evicted because it has non-TypeRep implicit parameters: $impls")
        Nil
        
      case typ -> ((trunk,mtd) -> Some(origMtd)) =>
        val t = modEmb.loadTypSymbol(encodedTypeSymbol(origMtd.owner.asType))
        val m = modEmb.getMtd(t, origMtd.asMethod.asInstanceOf[modEmb.uni.MethodSymbol])
        
        val tps = typ.typeSymbol.typeSignature.typeParams
        val tpTops = tps.map(_.typeSignature).map {
          case TypeBounds(a,b) => b
        }
        
        val tpSyms = typ.typeArgs map (_.typeSymbol)
        val saneTyp = typ.substituteTypes(tpSyms, tpTops)
        
        // TODO factor with code in `findCorrespondingShallow`
        val tparamTops = mtd.typeParams map { case TypeBounds(a,b) => b  case _ => typeOf[Any] }
        val explicitParams = mtd.paramLists collect { case ps if ps.headOption map (p => !p.isImplicit) getOrElse true => ps }
        val paramTypes = explicitParams map (_ map (_ typeSignature))
        def saneParams(offset: Int) = paramTypes map (_.zipWithIndex.map { case pt->i => q"rs(${i+offset}).asInstanceOf[${pt.substituteTypes(tpSyms ++ mtd.typeParams, tpTops ++ tparamTops)}]" })
        
        val overloadPrefix = "overload"
        val overloadArg = mtd.paramLists.flatten collectFirst {
          case p if p.isImplicit && (p.name.toString startsWith overloadPrefix) =>
            q"_b_.${ TermName("overloaded" + (p.name.toString drop overloadPrefix.length)) }" }
        //overloadArg |> disp
        
        def implArgs(pickFrom: Tree) = {
          val args = overloadArg ++ (mtd.typeParams.indices map (i => q"$pickFrom($i)"))
          if (args isEmpty) Nil
          else List(args)
        }
        
        trunk match {
            
          case Left(cls) =>
            q"map += $m -> ((rs: List[_b_.Rep[Any]], ts: List[_b_.TypeRep[Any]], ts2: List[_b_.TypeRep[Any]]) => new _b_.${cls.name}[..${tpTops}](rs(0).asInstanceOf[_b_.Rep[${saneTyp}]])(...${
              if (tpTops.isEmpty) Nil
              else List(tpTops.zipWithIndex map {case tp->i => q"ts($i).asInstanceOf[_b_.TypeRep[$tp]]"})
            }).${mtd.name}(...${ saneParams(1) })(...${ implArgs(q"ts2") }))" :: Nil
            
          case Right(tp) =>
            q"map += $m -> ((rs: List[_b_.Rep[Any]], ts: List[_b_.TypeRep[Any]], ts2: List[_b_.TypeRep[Any]]) => _b_.${TermName(__newPrefix+tp.name)}[..${/*saneTargs*/ tpTops}](...${
              saneParams(0)
            })(...${ implArgs(q"ts") }))" :: Nil
            
        }
        
      case _ -> (_ -> None) => Nil
        
    }
    
    val ret = q"new AutoBinder[${base.tpe}, ${sb.tpe}]($base,$sb) { ..${MB.mkSymbolDefs}; ..$fillingCode }"
    
    println(s"Generated: ${showCode(ret)}")
    
    ret
  }
  
}

