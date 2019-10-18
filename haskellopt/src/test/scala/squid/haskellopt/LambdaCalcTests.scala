package squid.haskellopt

import squid.utils._
import org.scalatest.FunSuite

class LambdaCalcTests extends FunSuite {
  object TestHarness extends TestHarness
  
  test("Church") {
    TestHarness("Church")
    /* even just `2 I` generates overcomplicated graph! Has many trivial branch redexes...
    
    This:
      module Church = {
        x$3:4↑$a
      } where:
        $a = {y$5 => $9};
        $9 = ([↓]x$3:4 ? x$c:8↑[↓;🚫]$d ¿ $18);
        $d = (x$c:8 ? 🚫$8 ¿ $15);
        $8 = ([↓]x$3:4 ? x$c:10↑[↓;🚫]$d ¿ $1d);
        $1d = ↓$4 @ y$5;
        $4 = (x$3:4 ? 🚫$e ¿ x$3);
        $e = {x$c => $d};
        $15 = (x$c:10 ? 🚫y$5 ¿ x$c);
        $18 = ↓$4 @ $8;
    
    Can be simplified to, essentially:
      [a↑](\y. u) where {
        u = [↓]a ? v ¿ [↓]w @ v
        v = [↓]a ? y ¿ [↓]w @ y
        w = a ? 🚫(\x. x) ¿ z
      }
    
    */
  }
  
}
