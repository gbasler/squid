package graph4

import squid.utils._
import org.scalatest.FunSuite

class BasicTests extends FunSuite {
  object TestHarness extends TestHarness
  
  // TODO allow selecting defs to test in isolation
  
  test("Basics") {
    TestHarness("Basics", dumpGraph = true)
  }
  
  test("BasicRec") {
    TestHarness("BasicRec", dumpGraph = true)
  }
  
}
