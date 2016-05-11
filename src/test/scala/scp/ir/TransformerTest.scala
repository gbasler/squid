package scp
package ir

class TransformerTest extends MyFunSuite(new AST with lang.ScalaTyping) {
  //object DSL extends AST with lang.ScalaTyping
  import DSL.Quasi.QuasiContext
  //import DSL.rewrite // TODO
  import DSL.{applyTransform => _, _}
  
  object T extends Transformer {
    val base: DSL.type = DSL
  }
  
  test ("Simple Rewritings") {
    T.rewrite {
      case dsl"666" => dsl"999"
      case dsl"42.toFloat" => dsl"42f" 
      case dsl"(${Constant(n)}: Int).toDouble" => dsl"${n.toDouble}"
    }
    eqt(T.applyTransform(dsl"666"), dsl"999")
    eqt(T.applyTransform(dsl"42.toFloat"), dsl"42f")
    eqt(T.applyTransform(dsl"22.toDouble"), dsl"22.0")
    
    
    assertDoesNotCompile("""
      T.rewrite { case dsl"0.5" => dsl"42" }
    """) // Error:(25, 34) Cannot rewrite a term of type Double to a different type Int
    
    assertDoesNotCompile("""
      T.rewrite { case dsl"123" => dsl"($$n:Int)" }
    """) // Error:(26, 34) Cannot rewrite a term of context [Unknown Context] to an unrelated context Any{val n: Int}
    
  }
  
  test ("Rewritings With Subpatterns") {
    
    T.rewrite {
      case dsl"(${ dsl"($n: Int)+111" }: Int) * .5" => dsl"$n * .25"
    }
    eqt(T.applyTransform(dsl"(readInt + 111) * .5"), dsl"readInt * .25")
    
  }
  
  test ("No Additional Free Variables") {
    
    assertDoesNotCompile("""
      T.rewrite { case dsl"123" => dsl"($$n:Int)" }
    """) // Error:(26, 34) Cannot rewrite a term of context [Unknown Context] to an unrelated context Any{val n: Int}
    
  }
  
  test ("Function Rewritings") {
    
    assertDoesNotCompile("""
      T.rewrite { case dsl"(x: Int) => $b: Int" => b }
    """) // Error:(26, 50) Cannot rewrite a term of type Int => Int to a different type Int
    
    assertDoesNotCompile("""
      T.rewrite { case dsl"(x: Int) => $b: Int" => dsl"(y: Int) => $b" }
    """) // Error:(30, 50) Cannot rewrite a term of context [Unknown Context] to a stricter context [Unknown Context]{val x: Int}
    
    T.rewrite { case dsl"(x: Int) => ($b: Int) * 32" => dsl"val x = 42; (y: Int) => $b + y" }
    
    eqt(T.applyTransform(dsl"(x: Int) => (x-5) * 32"), dsl"val u = 42; (v: Int) => (u - 5) + v")
    
  }
  
  
  test ("Polymorphic Rewritings") {
    
    T.rewrite {
      case dsl"List[$t]($xs*).size" => dsl"${xs.size}"
      case dsl"($ls: List[$t0]).map($f: t0 => $t1).map($g: t1 => $t2)" =>
        dsl"$ls.map($f andThen $g)"
    }
    
    eqt(T.applyTransform(dsl"List(1,2,3).size"), dsl"3")
    eqt(T.applyTransform(dsl"List(1,2,3) map (_ + 1) map (_.toDouble)"),
                         dsl"List(1,2,3) map { ((_:Int) + 1) andThen ((_:Int).toDouble) }")
    
  }
  
  
  
}

