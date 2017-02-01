package squid.utils

import org.scalatest.FunSuite

class MiscTests extends FunSuite {
  
  test("Infix If") {
    var a = 0
    (a = 1) If false
    assert(a == 0)
    (a = 1) If true
    assert(a == 1)
  }
  
}
