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
    TestHarness("HigherOrderRec")
    //TestHarness("HigherOrderRec", exec = true)
    //TestHarness("HigherOrderRec", "0000"::Nil)
    //TestHarness("HigherOrderRec", "0001"::Nil)
  }
  test("HigherOrderRec2") {
    TestHarness("HigherOrderRec2", dumpGraph = true)
    //TestHarness("HigherOrderRec2", dumpGraph = true, "0000"::Nil)
  }
  test("HigherOrderRec3") {
    TestHarness("HigherOrderRec3")
    //TestHarness("HigherOrderRec3", "0000"::Nil)
    //TestHarness("HigherOrderRec3", "0001"::Nil)
  }
  test("HigherOrderRec4") {
    TestHarness("HigherOrderRec4", dumpGraph = true)
    //TestHarness("HigherOrderRec4", "0000"::Nil, dumpGraph = true)
  }
  test("HigherOrderRec5") {
    TestHarness("HigherOrderRec5")
    //TestHarness("HigherOrderRec5", "0000"::Nil)
  }
  test("BuildFoldr") {
    TestHarness("BuildFoldr", dumpGraph = true)
  }
  test("IterCont") {
    TestHarness("IterCont", dumpGraph = true)
  }
  test("IterCont2") {
    TestHarness("IterCont2")
    //TestHarness("IterCont", "0001"::Nil)
    //TestHarness("IterCont", "0001"::Nil, opt = true)
  }
  test("IterContLocal") {
    //TestHarness("IterContLocal") // FIXME generate additional case
  }
  
}
