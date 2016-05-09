package scp
package functional

class Soundness extends MyFunSuite {
  import TestDSL._
  import Soundness._
  
  test("Term's Type Matching") {
    
    dsl"Some(42)".erase match {
        
      case dsl"Some[Nothing]($x:Nothing)" =>
        dsl"$x:String".run // would crash
        fail
        
      case dsl"Some[Any]($x)" =>
        // ok
        
    }
    
  }
  
  test("Function Matching") {
    val f = dsl"(x: Int) => x"
    f.erase match {
        
      case dsl"$f: (Any => Int)" =>
        dsl"$f(1.5)".run // would crash
        fail
        
      case dsl"$f: (Int => Any)" =>
        same(dsl"$f(15)".run, 15)
        
    }
  }
  
  test("Mutable References") {
    
    val r42 = dsl"Ref(42)".erase
    
    r42 match {
      //case r @ dsl"Ref($v: Any)" => // Warning:(33, 18) /!\ Type Any was inferred in quasiquote pattern /!\
      case r @ dsl"Ref[Any]($_)" =>
        //dsl"$r.value = 0.5" // does not compile: Error:(36, 9) Embedding Error: value value is not a member of Any
        // ^ would crash, but 'r' does not get the precise type inferred in the xtor -- it gets Quoted[Any,{}]
        fail
      // Note: with the 'x as y' pattern-synonym extractor, we could write 'case dsl"$r as Ref[Any]($_)"'
      // but it should not match either (thanks to the return-type check in methodApp extraction)
      case dsl"$r: Ref[Any]" =>
        val unsound = dsl"$r.value = 0.5"
        unsound.run // would crash
        fail
      case dsl"$r: Ref[$t]" =>
        assertDoesNotCompile(""" dsl"$r.value = 0.5" """) // Error:(30, 9) Embedding Error: type mismatch; found: Double(0.5); required: t
    }
    
  }
  
  
}
object Soundness {
  case class Ref[A](var value: A)
}
