package scp
package quasi

import utils._

trait ScopeAnalyser { //self: MacroShared =>
  //import Ctx.universe._
  
  val uni: scala.reflect.api.Universe
  import uni._
  
  /*
  
  For a refinement like {val x: Int},
    the symbol of 'x' will have isMethod, asMethod.isGetter and asMethod.isStable
    if it was a def, it would have only the first
  
  */
  def bases_variables(typ: Type): (List[Type], List[(TermName, Type)]) = {
    println("[C] "+typ+" : "+typ.getClass)
    typ match {
      case st @ SingleType(pre: Type, sym: Symbol) =>
        bases_variables(sym.typeSignature) // or use 'st.widen'
      case RefinedType(parents: List[Type], decls: Scope) =>
        val (basess, varss) = parents map bases_variables unzip;
        //(parents map bases_variables foldLeft(Nil->Nil)){case() => ???} ++ (decls flatMap {
        //val (bases, vars) = decls flatMap {
        val vars = decls flatMap {
          //case sym =>
          //  println((sym, sym.isMethod, sym.asMethod.isGetter, sym.asMethod.isStable))
          //  None
          case sym: MethodSymbol if sym.isGetter =>
            List(sym.name -> sym.typeSignature)
          case _ => Nil
        };
        (basess.flatten, varss.flatten ++ vars)
      case x: RefinedType => ???
        
      case _ => (typ :: Nil) -> Nil
    }
  }
  //def variables(typ: Type): List[(TermName, Type)] = {
  //  //println("[C] "+typ.getClass)
  //  println("[C] "+typ+" : "+typ.getClass)
  //  typ match {
  //    case SingleType(pre: Type, sym: Symbol) =>
  //      //println(pre.widen)
  //      //println(sym)
  //      //println(sym.typeSignature)
  //      variables(sym.typeSignature)
  //    case RefinedType(parents: List[Type], decls: Scope) =>
  //      
  //      //decls collect {
  //      //  //case vd @ ValDef(mods: Modifiers, name: TermName, tpt: Tree, rhs: Tree) =>
  //      //  case sym: MethodSymbol if sym.isVal =>
  //      //    (sym.name, sym.typeSignature)
  //      //} toList;
  //      (parents flatMap variables) ++ (decls flatMap {
  //        //case sym =>
  //        //  println((sym, sym.isMethod, sym.asMethod.isGetter, sym.asMethod.isStable))
  //        //  None
  //        case sym: MethodSymbol if sym.isGetter =>
  //          List(sym.name -> sym.typeSignature)
  //        case _ => Nil
  //      }) toList;
  //      
  //    case x: RefinedType => ???
  //      
  //    case _ => Nil
  //  }
  //}
  
  
}
