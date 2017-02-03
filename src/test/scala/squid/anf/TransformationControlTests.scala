package squid
package anf

import ir._

/**
  * Created by lptk on 03/02/17.
  */
class TransformationControlTests extends MyFunSuite(SimpleANFTests.DSL) {
  import DSL.Predef._
  
  import collection.mutable.ArrayBuffer
  
  test("Predef.Abort") {
    
    val c0 = ir"val a = ArrayBuffer[Int](1,2,3); println(a(1)); a.size"
    def f(x: IR[_,{}]) = x rewrite {
      case ir"val arr = ArrayBuffer[$t]($xs*); $body: $bt" =>
        body subs 'arr -> Abort()
    }
    f(c0) eqt c0
    f(ir"val a = ArrayBuffer[Int](1,2,3); println('ok)") eqt ir"println('ok)"
    
    intercept[IllegalAccessError](Abort())
    
  }
  
  test("Predef.Return") {
    
    // TODO
    
  }
  
  test("Predef.Return.transforming") {
    
    // TODO
    
  }
  
  import base.Return // TODO in predef
  
  test("Bad Return Type") { // TODO test what should not compile
    
    val a = ir"List[Int](readInt)"
    println(a)
    //val b = /*base debugFor*/ (a dbg_rewrite {
    val b = (a rewrite {
      case ir"readInt" =>
        //Return(ir"'lol")
        //Return(ir"lol? : Double") // fails to compile; good
        //ir"(lol? : Double).toInt" // infers ctx extension
        Return(ir"(lol? : Double).toInt")
    })
    println(b)
    //println(b:Int)
    
  }
  
  test("Abort and Return in pattern guard") {
    
    // TODO (test nested RwR)
    
  }
  
  test("Early Return in Middle of Block") { // FIXME
    
    val c0 = ir"print(1); print(2); print(3); print(4)"
    println(c0 rewrite {
      case ir"print(2); print(3)" =>
        Return(ir"print(23)")
    })
    
  }
  
  
  
}
