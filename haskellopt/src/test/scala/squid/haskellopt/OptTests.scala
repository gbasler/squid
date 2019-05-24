package squid.haskellopt

import squid.utils._
import org.scalatest.FunSuite

class OptTests extends FunSuite {
  
  test("Lists") {
    
    //pipeline("haskellopt/target/dump/Lists.pass-0000.cbor")
    //pipeline("haskellopt/target/dump/Lists.pass-0001.cbor")
    //for (i <- 2 to 9) pipeline(s"haskellopt/target/dump/Lists.pass-000$i.cbor")
    
    TestHarness("Lists")
    
  }
  
  test("Basics") {
    
    TestHarness("Basics")
    //TestHarness("Basics", "0000"::Nil)
    
  }
  
  test("HigherOrder") {
    
    TestHarness("HigherOrder")
    //TestHarness("HigherOrder", "0000"::Nil)
    
  }
  
  test("HigherOrderHard") {
    
    TestHarness("HigherOrderHard")
    //TestHarness("HigherOrderHard", "0000"::Nil)
    //TestHarness("HigherOrderHard", "0001"::Nil)
    
  }
  
  test("HigherOrderRec") {
    
    //TestHarness("HigherOrderRec")
    TestHarness("HigherOrderRec", exec = true)
    //TestHarness("HigherOrderRec", "0000"::Nil)
    //TestHarness("HigherOrderRec", "0001"::Nil)
    
  }
  
  test("HigherOrderRec2") {
    TestHarness("HigherOrderRec2")
    //TestHarness("HigherOrderRec2", "0000"::Nil)
  }
  
  test("HigherOrderRec3") {
    //TestHarness("HigherOrderRec3")
    TestHarness("HigherOrderRec3", "0000"::Nil)
    //TestHarness("HigherOrderRec3", "0001"::Nil) // FIXME
  }
  
  test("HigherOrderRec4") {
    TestHarness("HigherOrderRec4")
  }
  
  test("HigherOrderRec5") {
    //TestHarness("HigherOrderRec5")
    TestHarness("HigherOrderRec5", "0000"::Nil)
    //TestHarness("HigherOrderRec5", "0001"::Nil, exec = true) // FIXME
  }
  
  test("BuildFoldr") {
    
    TestHarness("BuildFoldr")
    
  }
  
  test("IterCont") {
    
    //TestHarness("IterCont")
    TestHarness("IterCont", "0000"::Nil, compileResult = false) // FIXME
    //TestHarness("IterCont", "0001"::Nil, opt = true)
    //TestHarness("IterCont", "0001"::Nil)
    
  }
  
}
