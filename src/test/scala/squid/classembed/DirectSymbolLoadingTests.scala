package squid
package classembed

import utils._
import squid.ir._
import squid.lang.ScalaCore
import squid.utils.meta.RuntimeUniverseHelpers
import squid.quasi.{embed,dbg_embed}

import DirectSymbolLoadingTests._

@embed
class TestClass {
  def foo = 123
}
object TestClass {
  def bar = "ok"
  def overloaded(x:Int) = 1
  def overloaded(x:Bool) = 2
}

class DirectSymbolLoadingTests extends MyFunSuite(TestDSL) {
  import TestDSL.Predef._
  
  test("Methods Loading Count") {
    
    assert(mtdLoadCount == Map())
    
    assert(ir"TestClass.bar".run == "ok")
    
    assert(mtdLoadCount("bar") == 1)
    
    assert(ir"TestClass.bar + 1".run == "ok1")
    
    assert(mtdLoadCount("bar") == 1)
    
    assert(!mtdLoadCount.contains("foo"))
    
    assert(ir"(new TestClass).foo".run == 123)
    assert(ir"(tc:TestClass) => tc.foo+1".run apply (new TestClass) equals 124)
    
    assert(mtdLoadCount("foo") == 1)
    
  }
  
  test("Overloaded Methods") {
    
    assert(ir"TestClass.overloaded(0)".run == 1)
    
    assert(ir"TestClass.overloaded(true)".run == 2)
    
  }
  
}

object DirectSymbolLoadingTests {
  
  val mtdLoadCount = collection.mutable.Map[String,Int]()
  
  object TestDSL extends SimpleAST with ScalaCore with TestClass.Lang {
    override def loadMtdSymbol(typ: ScalaTypeSymbol, symName: String, index: Option[Int], static: Bool): MtdSymbol = {
      mtdLoadCount(symName) = mtdLoadCount.getOrElse(symName, 0) + 1
      super.loadMtdSymbol(typ, symName, index, static)
    }
  }
  
}

