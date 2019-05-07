package squid
package ir
package graph3

import utils._
import squid.lib.matching._
import squid.lib
import squid.ir.graph.{SimpleASTBackend => AST}

object RecursiveGraphTests extends Graph

class RecursiveGraphTests extends MyFunSuite(RecursiveGraphTests) with GraphRewritingTester[RecursiveGraphTests.type] {
  import DSL.Predef._
  import DSL.Quasicodes._
  import haskell.Prelude.compose
  
  object Tr extends SimpleRuleBasedTransformer with RecursiveGraphTests.SelfTransformer {
    
  }
  
  def letrec[A:CodeType,B:CodeType](f: Variable[A => B] => OpenCode[A => B]): ClosedCode[A => B] = {
    val rec = Variable[A => B]
    val body = f(rec)
    
    // does not work: (capture does not happen because val bindings are not supposed to be recursive)
    //code"val $rec = $body; $rec".unsafe_asClosedCode
    
    import RecursiveGraphTests._
    val cde = letin(rec.`internal bound`, body.rep, rec.rep, rec.toCode.Typ.rep)
    Code(cde)
  }
  
  test("Count") {
    
    val f = letrec((f: Variable[Int => Int]) => code"(n: Int) => if (n > 0) $f(n-1) else 0")
    val cde = code"($f,$f)"
    println(cde.rep.showGraph)
    
    // Does not work: the recursive binding is not visible while scheduling its own body
    /*
    RecursiveGraphTests.ScheduleDebug debugFor
      println(cde.rep.show)
    */
  }
  
  test("Omega") {
    
    // TODO
    
  }
  
}
