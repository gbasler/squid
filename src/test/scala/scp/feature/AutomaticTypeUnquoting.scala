package scp
package feature

class AutomaticTypeUnquoting extends MyFunSuite {
  import TestDSL._
  
  def foo[A:TypeEv](a:Q[A,_]) = println(typeEv[A])
  
  test("Extracted Type") {
    val r: HPair[Q[HPair[_],{}]] = dsl"HPair(1,2)".erase match {
    //val (r: Q[HPair[_],{}], s: Q[HPair[_],{}]) = dsl"HPair(1,2)".erase match {
      case dsl"HPair[$t]($a,$b)" =>
        //println(t)
        //foo(a) // FIXME
        //dsl"HPair($b,$a)"
        //dsl"HPair[$t]($b,$a)"
        //dsl"HPair($b,$a)" -> dsl"HPair[$t]($b,$a)"
        HPair(dsl"HPair($b,$a)", dsl"HPair[$t]($b,$a)")
    }
    //println(r)
    //eqt(r.first, dsl"HPair(2,1)") // FIXME
    //eqt(r.second, dsl"HPair(2,1)") // FIXME
  }
  
  test("Nested Extracted Type") {
    
    val r: Q[HPair[_],{}] = dsl"HPair(HPair(1,2), HPair(3,4))".erase match {
      case dsl"HPair[$t]($a,$b)" =>
        eqt(t.rep, typeRepOf[HPair[Int]])
        //a match {
        //  case dbgdsl"$p: t" => // FIXME; see Quasi.scala:221
        //    dsl"$p: t"
        //    dsl"$p: $t"
        //}
        b.erase match {
          case dsl"HPair[$u]($x,$y)" =>
            eqt(u.rep, typeRepOf[Int])
            //val r0: Q[HPair[_],_] = dsl"HPair[t]($b,$a)" // FIXME
            val r1: Q[HPair[_],{}] = dsl"HPair[$t]($b,$a)"
            val r2: Q[HPair[_],{}] = dsl"HPair($b,$a)"
            //eqt(r0, r1)
            //eqt(r1, r2) // FIXME
            r2
        }
    }
    
    //eqt(r, dsl"HPair(HPair(3,4), HPair(1,2))")
    
  }
  
  
  // TODO adapt:
  
  //test("Manual Type") { // users are not supposed to do that sorta things
  //  
  //  class X
  //  val xtp: TypeRep[X] = typeHole("Test")
  //  
  //  val x = const(new X)(TypeEv(xtp))
  //  
  //  exp"HPair($x, $x)"
  //  
  //}
  //
  //test("Redundant Manual Type") {
  //  
  //  val int = typeRepOf[Int]
  //  
  //  exp"HPair(1,2)"
  //  
  //  exp"HPair[$int](1,2)"
  //  
  //}
  
}



