package scp
package feature

import org.scalatest.Assertions

class AutomaticTypeUnquoting extends MyFunSuite {
  import TestDSL._
  
  def foo[A:TypeEv](a:Q[A,_]) = println(typeEv[A])
  
  test("Extracted Type") {
    val r: HPair[Q[HPair[_],{}]] = dsl"HPair(1,2)".erase match {
      case dsl"HPair[$t]($a,$b)" =>
        eqt(t.rep, typeRepOf[Int])
        eqt(implicitTypeOf(a), typeRepOf[Int])
        HPair(dsl"HPair($b,$a)", dsl"HPair[$t]($b,$a)")
    }
    eqt(r.first, dsl"HPair(2,1)")
    eqt(r.second, dsl"HPair(2,1)")
    
    eqt(r.first.trep, typeRepOf[HPair[Int]])
    assertDoesNotCompile(" implicitTypeOf(r.first) ") // Error:(22, 19) Cannot generate a type representation for: scp.HPair[_]
  }
  
  test("Nested Extracted Type") {
    
    val r: Q[HPair[_],{}] = dsl"HPair(HPair(1,2), HPair(3,4))".erase match {
      case dsl"HPair[$t]($a,$b)" =>
        eqt(t.rep, typeRepOf[HPair[Int]])
        
        //a match {
        //  case dbgdsl"$p: $$t" => // TODO
        //    dsl"$p: t"
        //    dsl"$p: $t"
        //}
        
        val s1 = b.erase match {
          case dsl"HPair[$u]($x,$y)" =>
            eqt(u.rep, typeRepOf[Int])
            val r1: Q[HPair[_],{}] = dsl"HPair[$t]($b,$a)"
            val r2: Q[HPair[_],{}] = dsl"HPair($b,$a)"
            //val r2: Q[HPair[_],{}] = dsl"HPair(HPair($x,$y),$a)" // note: this infers HPair[Object], so it will fail to compare equivalent!
            eqt(r1, r2)
            r2
        }
        
        import scala.language.existentials
        // ^ Warning:(47, 38) inferred existential type scp.TestDSL.Quoted[scp.HPair[scp.HPair[u]],Any] forSome { type u <: scp.lang.Base.HoleType }, which cannot be expressed by wildcards,  should be enabled
        
        val s2 = dsl"$a -> $b".erase match {
          case dsl"HPair[$u]($x,$y) -> HPair[u]($v,$w)" =>
            val r1 = dsl"HPair($x,$y)"
            val r2 = dsl"HPair($v,$w)"
            dsl"HPair($r2, $r1)"
        }
        
        assertDoesNotCompile(" implicitTypeOf(s2) ") // Error:(53, 23) Cannot refer to hole type out of the scope where it is extracted.
        
        eqt(s1, s2)
        s2
    }
    
    eqt(r, dsl"HPair(HPair(3,4), HPair(1,2))")
    
  }
  
  test("Usage of Extracted Types Out of Extraction Scope") { // FIXME
    
    val xt = dsl"Some(42)".erase match {
      case dsl"Some($x: $t)" => x
    }
    
    assertDoesNotCompile(""" dsl"Left($xt)" """) // Cannot refer to hole type 't' out of the scope where it is extracted.
    
    assertDoesNotCompile(""" evOfTermsType(xt) """) // Cannot refer to hole type 't' out of the scope where it is extracted.
    
  }
  
  test("Manual Type") { // users are not supposed to do that sorta things
    class X
    implicit val xev: TypeEv[X] = TypeEv(typeHole("Test"))
    
    //val x = Quoted[X,{}](const(new X)(xev))
    val x = dsl"${new X}" // constant
    
    val p = dsl"HPair($x, $x)"
    eqt(p.trep, typeRepOf[HPair[X]])
  }
  
  test("Redundant Manual Type") {
    val int = QuotedType[Int](typeRepOf[Int])
    dsl"HPair(1,2)"
    dsl"HPair[$int](1,2)"
  }
  
}



