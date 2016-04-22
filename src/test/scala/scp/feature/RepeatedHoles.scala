package scp
package feature

class RepeatedHoles extends MyFunSuite {
  import TestDSL._
  
  val e = dsl"HPair(HPair(1,1), HPair(1,1)).second".erase
  
  test("Repeated Term Holes") {
    
    e match {
      case dsl"HPair[$tp]($p,p).second" =>
        eqt(tp.rep, typeRepOf[HPair[Int]])
        eqt(p, dsl"HPair(1,1)")
    }
    e match {
      case dsl"HPair[$tp](p,$p).second" =>
        eqt(tp.rep, typeRepOf[HPair[Int]])
        eqt(p, dsl"HPair(1,1)")
    }
  }
  
  test("Repeated Term and Type Holes") {
    
    e match {
      case dsl"HPair(HPair[$tn](n,n),HPair[tn]($n,n)).second" =>
        eqt(tn.rep, typeRepOf[Int])
        eqt(n, dsl"1")
    }
    e match {
      case dsl"HPair($a:$t, $_).second" =>
        eqt(t.rep, typeRepOf[HPair[Int]])
    }
    
  }
  
}

