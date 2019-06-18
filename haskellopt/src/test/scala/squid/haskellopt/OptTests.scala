package squid.haskellopt

import squid.utils._
import org.scalatest.FunSuite

class OptTests extends FunSuite {
  
  test("Lists") {
    //pipeline("haskellopt/target/dump/Lists.pass-0000.cbor")
    //pipeline("haskellopt/target/dump/Lists.pass-0001.cbor")
    //for (i <- 2 to 9) pipeline(s"haskellopt/target/dump/Lists.pass-000$i.cbor")
    TestHarness("Lists")
    // FIXMEnot does not reduce at all!
    // FIXMEnot generates extra params!
  }
  test("Basics") {
    TestHarness("Basics")
    //TestHarness("Basics", exec = true)
    //TestHarness("Basics", "0000"::Nil)
  }
  test("HigherOrder") {
    TestHarness("HigherOrder")
    //TestHarness("HigherOrder", "0000"::Nil)
  }
  test("HigherOrderHard") {
    //TestHarness("HigherOrderHard") // FIXME scope extrusion
    TestHarness("HigherOrderHard", compileResult = false)
    //TestHarness("HigherOrderHard", "0000"::Nil)
    //TestHarness("HigherOrderHard", "0001"::Nil)
  }
  test("HigherOrderRec") {
    //TestHarness("HigherOrderRec", exec = true) // FIXME [?]
    TestHarness("HigherOrderRec", compileResult = false)
    //TestHarness("HigherOrderRec")
    //TestHarness("HigherOrderRec", "0000"::Nil)
    //TestHarness("HigherOrderRec", "0001"::Nil)
  }
  test("HigherOrderRec2") {
    //TestHarness("HigherOrderRec2", dumpGraph = true) // FIXME scope extrusion
    TestHarness("HigherOrderRec2", dumpGraph = true, compileResult = false)
    //TestHarness("HigherOrderRec2", dumpGraph = true, "0000"::Nil)
  }
  test("HigherOrderRec3") {
    // FIXME not fully reduced: see k app
    //TestHarness("HigherOrderRec3") // FIXME scheduling diverges?!
    
    //TestHarness("HigherOrderRec3", "0000"::Nil)
    //TestHarness("HigherOrderRec3", "0001"::Nil)
  }
  test("HigherOrderRec4") {
    TestHarness("HigherOrderRec4", dumpGraph = true)
    //TestHarness("HigherOrderRec4", "0000"::Nil, dumpGraph = true)
  }
  test("HigherOrderRec5") {
    // FIXME not fully reduced: see k app
    //TestHarness("HigherOrderRec5") // FIXME scheduling diverges?!
    
    //TestHarness("HigherOrderRec5", "0000"::Nil) // FIXME perhaps if we didn't unroll the recursion, we wouldn't get into problems...
  }
  test("BuildFoldr") {
    //TestHarness("BuildFoldr", dumpGraph = true) // FIXME [?]
    TestHarness("BuildFoldr", dumpGraph = true, compileResult = false)
  }
  test("IterCont") {
    TestHarness("IterCont", dumpGraph = true)
  }
  test("IterCont2") {
    //TestHarness("IterCont2"/*, exec = true*/) // FIXME result [0,0,0] // FIXME scheduling diverges?!
    //TestHarness("IterCont2", "0000"::Nil) // FIXME
    
    //TestHarness("IterCont2", "0000"::Nil, exec = true)
    //TestHarness("IterCont2", "0000"::Nil)
    //TestHarness("IterCont2", "0001"::Nil)
    //TestHarness("IterCont2", "0001"::Nil, opt = true)
  }
  test("IterContLocal") {
    // TODO simplify case expressions
    // TODO simplify control-flow by some limited duplication, to avoid mostly useless and complicated sharing structures
    //TestHarness("IterContLocal") // FIXME SOF
    
    // FIXME SOF
    
    // FIXME looks like not totally reduced: see lambdas and application of k in nats -- it should have been counted in the stats!!
    //    $95 = ↓;🚫;↓;↓$73 @ $11;
    //    $73 = (f$6:6 ? 🚫;↓;↓$7 ¿ f$6);
    //    $7 = (f$6:1 ? 🚫$27 ¿ $73);
    //    $27 = {k$16 => $26};
    
    //TestHarness("IterContLocal")
    //TestHarness("IterContLocal", "0000"::Nil/*, exec = true*/) // FIXME result [0,0,0] // FIXME scheduling diverges?!
    //TestHarness("IterContLocal", exec = true) // FIXME result [0,0,0]
    //TestHarness("IterContLocal", "0001"::Nil, exec = true) // works now!
    
  }
  
}
