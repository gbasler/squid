package squid
package functional

import utils.GenHelper

import scala.collection.mutable

class CompilationTests extends MyFunSuite {
  import DSL.Predef._
  import DSL.Quasicodes._
  
  test("Miscellaneous") {
    
    val q = code{
      val buff = new mutable.ArrayBuffer[Int]()
      for (i <- 1 to 11) buff += i * 2
      buff.reduce(_ + _)
    }
    val res = 132
    assert(q.run == res)
    assert(q.compile == res)
    
  }
  
}
