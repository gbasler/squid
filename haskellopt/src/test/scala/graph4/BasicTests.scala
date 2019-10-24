package graph4

import squid.utils._
import org.scalatest.FunSuite

class BasicTests extends FunSuite {
  object TestHarness extends TestHarness
  
  test("Basics") {
    TestHarness("Basics", dumpGraph = true)
    //TestHarness("Basics", dumpGraph = true, prefixFilter = "gTest")
    //TestHarness("Basics", dumpGraph = true, prefixFilter = "fTest")
  }
  
  test("BasicRec") {
    TestHarness("BasicRec", dumpGraph = true)
  }
  
}
