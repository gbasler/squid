package squid
package anf

import ir._

/**
  * Created by lptk on 03/02/17.
  */
class NormalizationTests extends MyFunSuite(SimpleANFTests.DSL) {
  import DSL.Predef._
  
  test("Normalization of Tail Bindings") {
    
    ir"readInt" eqt ir"val n = readInt; n"
    
    ir"print(0); val r = {println; readInt}; r" eqt ir"print(0); println; readInt"
    
  }
  
  test("Normalization of Unit Values") {
    
    ir"val a = (); a" eqt ir"()"
    
    ir"val a = identity(()); a" eqt ir"identity(())"
    
    ir"val a = println; print(a)" eqt ir"println; print(())"
    
    ir"val a = ??? ; print(a)" |> (a => a eqt a)
    
    ir"println; ()" eqt ir"println"
    
    ir"val a = ??? ; ??? ; a" eqt ir"val a = ??? ; ???"  // Note: this happens because  ???.typ <:< Unit  and  ???.typ <:< a.typ  and  a.isPure
    
  }
  
}
