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
    
    TestHarness("HigherOrderHard", compileResult = false) // FIXME error in hs
    //TestHarness("HigherOrderHard", "0000"::Nil)
    //TestHarness("HigherOrderHard", "0001"::Nil)
    
  }
  
  test("HigherOrderRec") {
    
    //TestHarness("HigherOrderRec")
    //TestHarness("HigherOrderRec", exec = true) // FIXME never finishes
    //TestHarness("HigherOrderRec", "0000"::Nil)
    //TestHarness("HigherOrderRec", "0001"::Nil)
    
  }
  
  test("HigherOrderRec2") {
    TestHarness("HigherOrderRec2", dumpGraph = true)
    //TestHarness("HigherOrderRec2", dumpGraph = true, "0000"::Nil)
  }
  
  test("HigherOrderRec3") {
    //TestHarness("HigherOrderRec3")
    //TestHarness("HigherOrderRec3", compileResult = false)
    TestHarness("HigherOrderRec3", "0000"::Nil, compileResult = false) // FIXME error in hs
    //TestHarness("HigherOrderRec3", "0001"::Nil) // FIXME stack overflow
    
    // FIXME in old minimized version, get error in hs
    // We get:
    //_0(# f'2 #) = (\x -> ((_1(# {-P-}(f'2(x)) #)) (_0(# (_1(# {-P-}(f'2(x)) #)) #))))
    // We should get:
    //_0(# f'2 #) = (\x -> ((_1(# {-P-}(f'2(x)) #)) (_0(# (_1(# {-P-}(f'2) #)) #))))
  }
  
  test("HigherOrderRec4") {
    TestHarness("HigherOrderRec4", dumpGraph = true, compileResult = false) // FIXME error in hs
  }
  
  test("HigherOrderRec5") {
    //TestHarness("HigherOrderRec5")
    //TestHarness("HigherOrderRec5", "0000"::Nil, compileResult = false) // FIXME error in hs // FIXME stack overflow
    //TestHarness("HigherOrderRec5", "0001"::Nil, exec = true) // FIXME
  }
  
  test("BuildFoldr") {
    
    TestHarness("BuildFoldr", dumpGraph = true)
    
  }
  
  test("IterCont") {
    
    //TestHarness("IterCont", dumpGraph = true)
    TestHarness("IterCont", "0000"::Nil, dumpGraph = true, compileResult = false) // FIXME error in hs
    //TestHarness("IterCont", "0001"::Nil, dumpGraph = true, opt = true)
    //TestHarness("IterCont", "0001"::Nil, dumpGraph = true)
    
  }
  
}
