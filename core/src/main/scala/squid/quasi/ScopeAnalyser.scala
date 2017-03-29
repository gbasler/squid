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
  def bases_variables(typ: Type): (List[Type], List[(TermName, Type)]) = {
    //println("[C] "+typ+" : "+typ.getClass)
    typ match {
      case st @ SingleType(pre: Type, sym: Symbol) =>
        bases_variables(sym.typeSignature) // or use 'st.widen'
      case RefinedType(parents: List[Type], decls: Scope) =>
        val (baseSyms, varSyms) = parents map bases_variables unzip;
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
      case x: RefinedType => ???
        
      case _ => (typ :: Nil) -> Nil
    }
  }
  
  val glb2: (Type, Type) => Type = (a,b) => glb(a :: b :: Nil)
  
  def mergeVars(vars: List[(TermName, Type)]) = {
    vars.groupBy(_._1) map {
      case (n, homonyms) => n -> homonyms.map(_._2).reduce(glb2)
    } toList
  }
  
  def mkContext(bases: List[Type], vars: List[(TermName, Type)]): Tree = {
    //println(s"MK CTX $bases ${glb(bases)}")
    val newBases = bases filter (b => !(b =:= AnyRef))
    tq"(${glb(newBases)}){ ..${ mergeVars(vars) map { case(na,tp) => q"val $na: $tp" } } }"
  }
  
  
}
