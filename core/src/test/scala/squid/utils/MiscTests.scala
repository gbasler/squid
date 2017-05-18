package squid.utils

import org.scalatest.FunSuite

class MiscTests extends FunSuite {
  
  test("Infix If") {
    var a = 0
    (a = 1) optionIf false
    assert(a == 0)
    (a = 1) optionIf true
    assert(a == 1)
  }
  
}
