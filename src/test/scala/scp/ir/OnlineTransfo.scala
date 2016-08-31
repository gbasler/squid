package scp
package ir2

import scp.utils.Debug

class OnlineTransfo extends MyFunSuite2(OnlineTransfo.DSL) {
  import DSL.Predef._
  
  test("Constructed Terms are Rewritten") {
    
    eqt(ir"2 + (readInt+1) + 3",
        ir"6 + readInt")
    
    eqt(ir"4 * ((readInt + 1) * 2 + 3)",
        ir"20 + 8 * readInt")
    
    eqt(ir"readInt * (readInt + 1)",
        ir"readInt + readInt * readInt") // Note: that's why we need ANF
    
  }
  
  test("Extractor Terms are Rewritten") {
    
    ir"4" match {
      case ir"2 + 2" =>
    }
    
    ir"1 + readInt" match {
      case ir"readInt + 1" =>
    }
    
    ir"6 + readInt" match {
      case ir"2 + (($n:Int)+1) + 3" => eqt(n, ir"readInt")
    }
    
  }
  
  test("Online Rewriting is Properly Recursive") {
    
    // Note: I verified that Scala does NOT P/E this, although it does things like '2+2'
    eqt(ir"readInt + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1",
        ir"10 + readInt")
    
  }
  
}
object OnlineTransfo {
  
  object DSL extends SimpleAST with OnlineOptimizer with SimpleRuleBasedTransformer {
    import Predef._
    
    override val transformExtractors = true
    
    rewrite { // Naive Addition/Multiplication Partial Evaluation
      
      // Addition P/E
      
      case ir"0 + ($x: Int)" => x
        
      case ir"(${Const(m)}: Int) + (${Const(n)}: Int)" => ir"${Const(n + m)}"
        
      case ir"($x: Int) + (${Const(n)}: Int)"             => ir"${Const(n)} + $x"
      case ir"($x: Int) + (($y: Int) + ($z: Int))"           => ir"($x + $y) + $z"
        
        
      // Multiplication P/E
        
      case ir"0 * ($x: Int)" => ir"0"
      case ir"1 * ($x: Int)" => x
        
      case ir"(${Const(m)}: Int) * (${Const(n)}: Int)" => ir"${Const(n * m)}"
        
      case ir"($x: Int) * (${Const(n)}: Int)"             => ir"${Const(n)} * $x"
      case ir"($x: Int) * (($y: Int) * ($z: Int))"           => ir"($x * $y) * $z"
      case ir"($x: Int) * (($y: Int) + ($z: Int))"           => ir"$x * $y + $x * $z" // Note: duplication of 'x'
      //case ir"($x: Int) * (($y: Int) + ($z: Int))"           => ir"val t = $x; t * $y + t * $z" // if not in ANF
      
    }
    /* // Note:
      case ir"0 + ($x: Int)" => ir"$x+($$y:Int)" // Error:(58, 34) Cannot rewrite a term of context [Unknown Context] to a stricter context [Unknown Context]{val y: Int}
    */
    
  }
}

