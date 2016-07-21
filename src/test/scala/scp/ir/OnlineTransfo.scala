package scp
package ir

import scp.utils.Debug

class OnlineTransfo extends MyFunSuite(OnlineTransfo.DSL) {
  
  import DSL._
  import DSL.Quasi.QuasiContext
  
  test("Constructed Terms are Rewritten") {
    
    eqt(dsl"2 + (readInt+1) + 3",
        dsl"6 + readInt")
    
    eqt(dsl"4 * ((readInt + 1) * 2 + 3)",
        dsl"20 + 8 * readInt")
    
    eqt(dsl"readInt * (readInt + 1)",
        dsl"readInt + readInt * readInt") // Note: that's why we need ANF
    
  }
  
  test("Extractor Terms are Rewritten") {
    
    dsl"4" match {
      case dsl"2 + 2" =>
    }
    
    dsl"1 + readInt" match {
      case dsl"readInt + 1" =>
    }
    
    dsl"6 + readInt" match {
      case dsl"2 + (($n:Int)+1) + 3" => eqt(n, dsl"readInt")
    }
    
  }
  
  test("Online Rewriting is Properly Recursive") {
    
    // Note: I verified that Scala does NOT P/E this, although it does things like '2+2'
    eqt(dsl"readInt + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1",
        dsl"10 + readInt")
    
  }
  
}
object OnlineTransfo {
  
  object DSL extends AST with lang.ScalaTyping {
    import Quasi.QuasiContext
    
    rewrite { // Naive Addition/Multiplication Partial Evaluation
      
      // Addition P/E
      
      case dsl"0 + ($x: Int)" => x
        
      case dsl"(${Constant(m)}: Int) + (${Constant(n)}: Int)" => dsl"${n + m}"
        
      case dsl"($x: Int) + (${Constant(n)}: Int)"             => dsl"$n + $x"
      case dsl"($x: Int) + (($y: Int) + ($z: Int))"           => dsl"($x + $y) + $z"
        
        
      // Multiplication P/E
        
      case dsl"0 * ($x: Int)" => dsl"0"
      case dsl"1 * ($x: Int)" => x
        
      case dsl"(${Constant(m)}: Int) * (${Constant(n)}: Int)" => dsl"${n * m}"
        
      case dsl"($x: Int) * (${Constant(n)}: Int)"             => dsl"$n * $x"
      case dsl"($x: Int) * (($y: Int) * ($z: Int))"           => dsl"($x * $y) * $z"
      case dsl"($x: Int) * (($y: Int) + ($z: Int))"           => dsl"$x * $y + $x * $z" // Note: duplication of 'x'
      //case dsl"($x: Int) * (($y: Int) + ($z: Int))"           => dsl"val t = $x; t * $y + t * $z" // if not in ANF
      
    }
    /* // Note:
      case dsl"0 + ($x: Int)" => dsl"$x+($$y:Int)" // Error:(58, 34) Cannot rewrite a term of context [Unknown Context] to a stricter context [Unknown Context]{val y: Int}
    */
    
  }
}

