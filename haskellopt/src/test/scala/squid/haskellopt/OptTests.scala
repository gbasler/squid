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
  
}
