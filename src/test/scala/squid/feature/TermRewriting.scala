package squid
package feature

import squid.ir.RewriteAbort
import utils._

class TermRewriting extends MyFunSuite {
  import TestDSL.Predef.{anyContextIsEmptyContext => _, _} // `anyContextIsEmptyContext` interferes with extension `eqt` in `test("Aborted Rewritings")`
  
  test("Basics") {
    
    eqt(ir"42" rewrite {
      case ir"42" => ir"666"
    }, ir"666")
    
    eqt(ir"println(42)" rewrite {
      case ir"42" => ir"666"
    }, ir"println(666)")
    
  }
  
  test("Rewrite is Top-Down") {
    
    eqt(ir"println(5)" rewrite {
      case ir"${Const(n)}: Int" if n > 1 =>
        if (n % 2 == 0) ir"${Const(n/2)} + ${Const(n/2)}"
        else ir"${Const(n/2)} + ${Const(n/2 + 1)}"
    }, ir"println { (1:Int).+(1:Int).+((1:Int).+((1:Int).+(1:Int))) }") // Ascriptions to prevent Scala from folding the constants
    
    eqt(ir"List(1,2)" rewrite {
      case ir"1" => ir"readInt"
      case ir"2" => ir"readInt"
      case ir"List(readInt,readInt)" => ir"Nil"
    }, ir"List(readInt,readInt)")
    
  }
  
  test("Captures Free Variables in Type") {
    
    val open = ir"$$a: Int"
    
    val r = ir"List(readInt)" rewrite {
      case ir"readInt" => ir"$open+1"
    }
    eqt(r, ir"List(($$a: Int)+1)")
    r [ List[Int] IR Any{val a: Int} ]
    
  }
  
  test("Captures Unknown Context in Type") {
    
    def insert[C](x: Int IR C) = {
      var r = ir"List(readInt)" rewrite { case ir"readInt" => ir"$x+1" }
      r = r : List[Int] IR C
      r : List[Int] IR C{val x:Int}
      assertDoesNotCompile(" r = r : List[Int] IR C{val x:Int} ")
      r
    }
    
    val r = insert(ir"$$a: Int")
    
    eqt(r, ir"List(($$a: Int)+1)")
    r [ List[Int] IR Any{val a: Int} ]
    
  }
  
  test("Aborted Rewritings") {
    
    val one = ir"1"
    
    ir"(1,2,3,4,5)" rewrite {
      case ir"${Const(n)}: Int" =>
        if (n % 2 != 0) throw RewriteAbort("Not even!")
        ir"${Const(n/2)}+${Const(n/2)}"
    } eqt ir"(1, $one+$one, 3, $one+$one+($one+$one), 5)"
    
  }
  
  test("Literals in patterns") {
    
    ir"'abc -> 'def" rewrite {
      case ir"Symbol(${Const("abc")})" =>
        ir"'lol"
    } eqt ir"'lol -> 'def"
    
  }
  
  test("Non-trivial name-pattern bindings in patterns") {
    
    ir"println(1.toDouble+1,2.toDouble+1)" rewrite {
      case ir"(${c @ Const(n)}:Int).toDouble+1" =>
        //ir"$c.toDouble + ${Const(n+1.0)}"
        ir"($c,${Const(n+1.0)})._2"
    } eqt ir"println((1,2.0)._2,(2,3.0)._2)"
    
  }
  
}
