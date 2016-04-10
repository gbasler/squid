package scp
package gen

import scala.reflect.runtime.universe._

/** 
  */
object SimpleReification {
  
  def apply(useQQ: Boolean, numClasses: Int, numMethodsPerClass: Int, numMethodUses: Int = 2) = {
    
    //def rep(t: Tree) = if (useQQ) q"Quoted[$t, {}]" else q"Exp[$t]"
    //def rep(t: Tree) = if (useQQ) t else tq"Exp[$t]"
    def rep(t: Tree) = if (useQQ) t else tq"_root_.scp.gen.TestDSLExp.Exp[$t]"
    
    val (classDefss, classUses) = (1 to numClasses) map { n =>
      val cname = TypeName("DSLClass"+n)
      val deepTyp = rep(Ident(cname))
      val (methodNames, methodDefs) = (1 to numMethodsPerClass) map { m =>
        val mname = TermName("method"+m)
        mname -> q"def $mname(x: $deepTyp): $deepTyp = ???"
      } unzip;
      val ops = if (useQQ) Nil else List(q"implicit class ${TypeName(cname+"Ops")} (self: $deepTyp) { ..${
        methodNames map { mname => q"def $mname(x: $deepTyp): $deepTyp = ???" }
      } }")
      val methodUses = List.fill(numMethodUses)(methodNames).flatten.foldLeft(q"x": Tree) {
        case (acc, mname) => q"$acc $mname x"
      }
      val function =
        if (useQQ) q"(x: $cname) => $methodUses"
        else q"((x: ${rep(Ident(cname))}) => $methodUses) : ${rep(tq"($cname => $cname)")}"
      (q"class $cname { ..$methodDefs }" :: ops) -> function
    } unzip;
    
    val pgrm = q"..$classUses"
    
    val reif = if (useQQ) q"StringContext(${showCode(pgrm)}).dsl()" else pgrm
    
    val importDSL = if (useQQ) q"import TestDSL._" else q"import gen.TestDSLExp._"
    
    val classDefs = classDefss map (_ head)
    
    q"""
      object Main {
        import scp._
        $importDSL
        ..${classDefss.flatten}
        $reif
      }
    """
    
  }
  
  
}










