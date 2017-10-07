package squid
package quasi

import squid.utils.meta.UniverseHelpers
import utils._

trait ScopeAnalyser[U <: scala.reflect.api.Universe] extends UniverseHelpers[U] {
  
  import uni._
  
  /*
  Implementation notes:
  
  For a refinement like {val x: Int},
    the symbol of 'x' will have isMethod, asMethod.isGetter and asMethod.isStable
    if it was a def, it would have only the first
  
  */
  /** Extracts the relevant info from a CONTEXT type */
  def bases_variables(typ: Type): (List[Type], List[(TermName, Type)]) = {
  //def bases_variables_safe(typ: Type): Option[List[Type] -> List[(TermName, Type)]] = {
    //println("[C] "+typ+" : "+typ.getClass)
    typ match {
      //case MethodType(ls,t) =>
      ////  bases_variables(sym.typeSignature)
      //  ???
      //case ByNameParamClass
      case st @ SingleType(pre: Type, sym: Symbol) =>
        bases_variables(sym.typeSignature) // or use 'st.widen'
        //bases_variables_safe(sym.typeSignature) // or use 'st.widen'
      case RefinedType(parents: List[Type], decls: Scope) =>
        val (baseSyms, varSyms) = parents map bases_variables unzip;
        //val (baseSyms, varSyms) = parents flatMap bases_variables_safe unzip;
        val vars = decls flatMap {
          case sym: MethodSymbol if sym.isGetter =>
            val typ = sym.typeSignature match {
              case NullaryMethodType(typ) => typ
              //case typ => typ
            }
            List(sym.name -> typ)
          case _ => Nil
        };
        (baseSyms.flatten, varSyms.flatten ++ vars)
        //Some(baseSyms.flatten, varSyms.flatten ++ vars)
      //case x: RefinedType => ???
      case _ => (typ :: Nil) -> Nil
      //case _ => None
    }
  }
  //def bases_variables(typ: Type): (List[Type], List[(TermName, Type)]) = bases_variables_safe(typ).get
  
  val glb2: (Type, Type) => Type = (a,b) => glb(a :: b :: Nil)
  
  //def mergeVars(vars: List[(TermName, Type)]) = {
  //  vars.groupBy(_._1) map {
  //    case (n, homonyms) => n -> homonyms.map(_._2).reduce(glb2)
  //  } toList
  //}
  def mergeVars(vars: List[(TermName, Type)]): Map[TermName, Type] = {
    vars.groupBy(_._1) map {
      case (n, homonyms) => n -> homonyms.map(_._2).reduce(glb2)
    } toMap
  }
  
  def mkContext(bases: List[Type], vars: List[(TermName, Type)]): Tree = {
    //println(s"MK CTX $bases ${glb(bases)}")
    val newBases = bases filter (b => !(b =:= AnyRef))
    tq"(${glb(newBases)}){ ..${ mergeVars(vars) map { case(na,tp) => q"val $na: $tp" } } }"
  }
  
  
}
