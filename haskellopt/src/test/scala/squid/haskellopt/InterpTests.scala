package squid.haskellopt

import squid.utils._
import org.scalatest.FunSuite

class InterpTests extends FunSuite {
  object TestHarness extends TestHarness
  
  test("InterpBench") {
    //TestHarness("InterpBench")  // scheduling seems to diverge
  }
  test("InterpSimple") {
    // NOTE: With pgm = [] and pgm = [True], works well.
    //       Problems start with pgm = [True,False]: graph becomes messy (but still works) and scheduling loses it.
    TestHarness("InterpSimple")
    // ^ scheduling seems to diverge when statements are lifted inside `test`
    // Otherwise, the generated code seems very whacky and contains many top-level bindings like `_2 = _2`
    // The graph works though (returns the right result), though it's cluttered with completely redundant branches (merging branch nodes will help).
    //   Moreover, careful inspection shows the graph has managed to reduce the two case matches!
    //   It essentially goes through a pretty complicated path to do `(123 + 1) * 2` as expected. 
  }
  
}
