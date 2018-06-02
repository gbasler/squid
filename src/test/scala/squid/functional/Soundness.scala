// Copyright 2019 EPFL DATA Lab (data.epfl.ch)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package squid
package functional

class Soundness extends MyFunSuite {
  import TestDSL.Predef._
  import Soundness._
  
  test("Term's Type Matching") {
    
    code"Some(42)".erase match {
        
      case code"Some[Nothing]($x:Nothing)" =>
        code"$x:String".run // would crash
        fail
        
      case code"Some[Any]($x)" =>
        // ok
        
    }
    
  }
  
  test("Function Matching") {
    val f = code"(x: Int) => x"
    f.erase match {
        
      case code"$f: (Any => Int)" =>
        code"$f(1.5)".run // would crash
        fail
        
      case code"$f: (Int => Any)" =>
        same(code"$f(15)".run, 15)
        
    }
  }
  
  test("Mutable References") {
    
    val r42 = code"Ref(42)".erase
    
    /* We used to check method call return types, now we don't (for flexibility reasons).
     * We also match method type arguments covariantly.
     * This combination seems like it could lead to unsoundness... (see below)
     * but in practice, I found that everything seems to remain fine.
     * Some observations:
     *  - The Right Thing to do would be to match method type parameters with the variance according to the method's
     *    signature, but that would be pretty complex (and probably expensive) to implement.
     *  - In fact, it seems we could get away with not checking method type arguments at all! Indeed, any actual
     *    unsoundness (besides potential ones mentioned below that rely on features we don't yet have) will have to at
     *    some point extract a value to do something with — but the types of extracted values _are_ always checked, so
     *    problematic cases cannot arise in practice. That's why it's fine, for now, to match them covariantly. We don't
     *    actually want to remove any check on them, because there might be holes waiting to get filled in by the check.
     *    A credible alternative would be to implement bivariant extraction for them.
     *  - This covariant matching turned out problematic in dbStage when I was trying to match types, using `Set[$t]` as
     *    the scrutinee and `Set[T]` as the patterns. But that just goes to show we should have a proper first-class way
     *    of matching types instead! In the meantime, one can use `$_: Set[$t]` as the patterns, which will be matched
     *    invariantly because the _type_ Set is invariant. */
    r42 matches {
      
      case r @ code"Ref($v: Any)" => // Note: used to raise: // Warning:(33, 18) /!\ Type Any was inferred in quasiquote pattern /!\
      
    } and {
      
      case r @ code"Ref[Any]($_)" =>
        //code"$r.value = 0.5" // does not compile: Error:(36, 9) Embedding Error: value value is not a member of Any
        // ^ would crash, but 'r' does not get the precise type inferred in the xtor -- it gets Code[Any,{}]
      
      // Note: with the 'x as y' pattern-synonym extractor, we could write 'case code"$r as Ref[Any]($_)"'
      // but it should not match either (thanks to the return-type check in methodApp extraction)
      // EDIT -- now it would!
      
    }
    
    r42 match {
      //case r @ code"Ref[Any]($_)" =>
      //  fail
        
      case code"$r: Ref[Any]" =>
        val unsound = code"$r.value = 0.5"
        unsound.run // would crash
        fail
      case code"$r: Ref[$t]" =>
        assertDoesNotCompile(""" code"$r.value = 0.5" """) // Error:(30, 9) Embedding Error: type mismatch; found: Double(0.5); required: t
        
    }
    
  }
  
  
}
object Soundness {
  case class Ref[A](var value: A)
}
