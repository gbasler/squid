package squid
package ir

class TransformerTest extends MyFunSuite/*(new SimpleAST)*/ {
  import DSL.Predef._
  
  object T extends SimpleRuleBasedTransformer with TopDownTransformer {
    val base: DSL.type = DSL
  }
  
  test ("Simple Rewritings") {
    T.rewrite {
      case ir"666" => ir"999"
      case ir"42.toFloat" => ir"42f" 
      case ir"(${Const(n)}: Int).toDouble" => ir"${Const(n.toDouble)}"
    }
    eqt(T.transform(ir"666".rep), ir"999".rep)
    eqt(T.transform(ir"42.toFloat".rep), ir"42f".rep)
    eqt(T.transform(ir"22.toDouble".rep), ir"22.0".rep)
    
    
    assertDoesNotCompile("""
      T.rewrite { case ir"0.5" => ir"42" }
    """) // Error:(25, 34) Cannot rewrite a term of type Double to a different type Int
    
    assertDoesNotCompile("""
      T.rewrite { case ir"123" => ir"($$n:Int)" }
    """) // Error:(26, 34) Cannot rewrite a term of context [Unknown Context] to an unrelated context Any{val n: Int}
    
  }
  
  test ("Rewritings With Subpatterns") {
    
    T.rewrite {
      case ir"(${ ir"($n: Int)+111" }: Int) * .5" => ir"$n * .25"
    }
    eqt(T.transform(ir"(readInt + 111) * .5".rep), ir"readInt * .25".rep)
    
  }
  
  test ("No Additional Free Variables") {
    
    assertDoesNotCompile("""
      T.rewrite { case ir"123" => ir"($$n:Int)" }
    """) // Error:(26, 34) Cannot rewrite a term of context [Unknown Context] to an unrelated context Any{val n: Int}
    
  }
  
  test ("Function Rewritings") {
    
    assertDoesNotCompile("""
      T.rewrite { case ir"(x: Int) => $b: Int" => b }
    """) // Error:(26, 50) Cannot rewrite a term of type Int => Int to a different type Int
    
    assertDoesNotCompile("""
      T.rewrite { case ir"(x: Int) => $b: Int" => ir"(y: Int) => $b" }
    """) // Error:(30, 50) Cannot rewrite a term of context [Unknown Context] to a stricter context [Unknown Context]{val x: Int}
    
    T.rewrite { case ir"(x: Int) => ($b: Int) * 32" => ir"val x = 42; (y: Int) => $b + y" }
    
    eqt(T.transform(ir"(x: Int) => (x-5) * 32".rep), ir"val u = 42; (v: Int) => (u - 5) + v".rep)
    
  }
  
  
  test ("Polymorphic Rewritings") {
    
    T.rewrite {
      case ir"List[$t]($xs*).size" => ir"${Const(xs.size)}"
      case ir"($ls: List[$t0]).map($f: t0 => $t1).map($g: t1 => $t2)" =>
        ir"$ls.map($f andThen $g)"
    }
    
    eqt(T.transform(ir"List(1,2,3).size".rep), ir"3".rep)
    eqt(T.transform(ir"List(1,2,3) map (_ + 1) map (_.toDouble)".rep),
                    ir"List(1,2,3) map { ((_:Int) + 1) andThen ((_:Int).toDouble) }".rep)
    
  }
  
  
  
}

