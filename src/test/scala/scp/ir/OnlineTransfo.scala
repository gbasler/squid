package scp
package ir

import scp.utils.Debug

class OnlineTransfo extends MyFunSuite(OnlineTransfo.DSL) {
  
  import DSL._
  import DSL.Quasi.QuasiContext
  
  test("Naive Addition/Multiplication Partial Evaluation") {
    
    eqt(dsl"2 + (readInt+1) + 3",
        dsl"6 + readInt")
    
    eqt(dsl"4 * ((readInt + 1) * 2 + 3)",
        dsl"20 + 8 * readInt")
    
    eqt(dsl"readInt * (readInt + 1)",
        dsl"readInt + readInt * readInt") // Note: that's why we need ANF
    
  }
  
  
}
object OnlineTransfo {
  
  object DSL extends AST with lang.ScalaTyping {
    import Quasi.QuasiContext
    
    rewrite {
      
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
      //case dsl"($x: Int) * (($y: Int) + ($z: Int))"           => dsl"val t = $x; t * $y + t * $z" // only good with ANF
      
    }
    
  }
}

