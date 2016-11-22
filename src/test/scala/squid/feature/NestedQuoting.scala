package squid
package feature

class NestedQuoting extends MyFunSuite {
  
  import TestDSL.Predef._
  
  
  test("Simple Nesting") {
    
    assert(ir"42.toString * 2" =~= ir"42.toString * 2")
    
    assert(ir"42.toString * 2" =~= ir"${ ir"42.toString" } * 2")
    
  }
  
  
  test("Block Nesting") {
    
    assert(ir"42.toString * 2" =~= ir"${ val n = ir"42"; ir"$n.toString" } * 2")
    
    assert(ir"42.toDouble.toString * 2" =~= ir"${ val n = ir"42.toDouble"; ir"$n.toString" } * 2")
    
  }
  
  
  test("Double Nesting") {
    
    assert(ir"42.toDouble.toString * 2" =~= ir"${ val str = ir"${ val n = ir"42"; ir"$n.toDouble" }.toString"; str } * 2")
    
    assert(ir"42.toDouble.toString * 2" =~= ir"${ val n = ir"42"; val str = ir"${ ir"$n.toDouble" }.toString"; str } * 2")
    
  }
  
  
  
  
}





