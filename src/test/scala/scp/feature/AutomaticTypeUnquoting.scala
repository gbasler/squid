package scp
package feature

import org.scalatest.Assertions

class AutomaticTypeUnquoting extends MyFunSuite2 {
  import TestDSL2.Predef._
  
  def foo[A:TypeEv](a:Q[A,_]) = println(typeEv[A])
  
  test("Extracted Type") {
    val r: HPair[Q[HPair[_],{}]] = ir"HPair(1,2)".erase match {
      case ir"HPair[$t]($a,$b)" =>
        eqt(t.rep, typeRepOf[Int])
        eqt(implicitTypeOf(a), typeRepOf[Int])
        HPair(ir"HPair($b,$a)", ir"HPair[$t]($b,$a)")
    }
    eqt(r.first, ir"HPair(2,1)")
    eqt(r.second, ir"HPair(2,1)")
    
    eqt(r.first.trep, typeRepOf[HPair[Int]])
    assertDoesNotCompile(" implicitTypeOf(r.first) ") // Error:(27, 27) Embedding Error: Unsupported feature: Existential type 'scp.HPair[_]'
    // Used to be: // Error:(22, 19) Cannot generate a type representation for: scp.HPair[_]
  }
  
  test("Nested Extracted Type") {
    
    val r: Q[HPair[_],{}] = ir"HPair(HPair(1,2), HPair(3,4))".erase match {
      case ir"HPair[$t]($a,$b)" =>
        eqt(t.rep, typeRepOf[HPair[Int]])
        
        a match {
          case ir"$p: $$t" =>
            //ir"$p: t"  // Note: this syntax is not available (nor useful)
            ir"$p: $t"
        }
        
        val s1 = b.erase match {
          case ir"HPair[$u]($x,$y)" =>
            eqt(u.rep, typeRepOf[Int])
            val r1: Q[HPair[_],{}] = ir"HPair[$t]($b,$a)"
            val r2: Q[HPair[_],{}] = ir"HPair($b,$a)"
            //val r2: Q[HPair[_],{}] = ir"HPair(HPair($x,$y),$a)" // note: this infers HPair[Object], so it will fail to compare equivalent!
            eqt(r1, r2)
            r2
        }
        
        import scala.language.existentials
        // ^ Warning:(47, 38) inferred existential type scp.TestDSL.Quoted[scp.HPair[scp.HPair[u]],Any] forSome { type u <: scp.lang.Base.HoleType }, which cannot be expressed by wildcards,  should be enabled
        
        val s2 = ir"$a -> $b".erase match {
          case ir"HPair[$u]($x,$y) -> HPair[u]($v,$w)" =>
            val r1 = ir"HPair($x,$y)"
            val r2 = ir"HPair($v,$w)"
            ir"HPair($r2, $r1)"
        }
        
        assertDoesNotCompile(" implicitTypeOf(s2) ") // Error:(53, 23) Cannot refer to hole type out of the scope where it is extracted.
        
        eqt(s1, s2)
        s2
    }
    
    eqt(r, ir"HPair(HPair(3,4), HPair(1,2))")
    
  }
  
  test("Usage of Extracted Types Out of Extraction Scope") { // FIXME
    
    val xt = ir"Some(42)".erase match {
      case ir"Some($x: $t)" => x
    }
    
    assertDoesNotCompile(""" ir"Left($xt)" """) // Cannot refer to hole type 't' out of the scope where it is extracted.
    
    assertDoesNotCompile(""" evOfTermsType(xt) """) // Cannot refer to hole type 't' out of the scope where it is extracted.
    
  }
  
  // This hack does not work anymore:
  /*test("Manual Type") { // Note: users are not supposed to do that sorta things
    class X
    implicit val xev: TypeEv[X] = base.`internal IRType`(base.typeHole("Test"))
    
    //val x = Quoted[X,{}](const(new X)(xev))
    val x = ir"${Const(new X)}" // constant  // Problem: type tag
    
    val p = ir"HPair($x, $x)"
    eqt(p.trep, typeRepOf[HPair[X]])
  }*/
  
  test("Redundant Manual Type") {
    val int = base.`internal IRType`[Int](typeRepOf[Int])
    ir"HPair(1,2)"
    ir"HPair[$int](1,2)"
  }
  
}



