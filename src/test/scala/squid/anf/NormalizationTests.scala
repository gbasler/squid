package squid
package anf

import ir._

/**
  * Created by lptk on 03/02/17.
  */
class NormalizationTests extends MyFunSuite(SimpleANFTests.DSLWithEffects) {
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
  
  object ONorm extends DSL.SelfTransformer with transfo.OptionNormalizer with TopDownTransformer
  
  test("Option Normalization") {
    
    ir"Option.empty[Int] map (_+1)" transformWith ONorm eqt 
      ir"if ((None:Option[Int]).isDefined) Some((None:Option[Int]).get+1) else None"
    
    // FIXME: assertion failed; at squid.ir.RuntimeSymbols$$anonfun$1.apply(RuntimeSymbols.scala:55)
    //ir"Option('ok)" transformWith ONorm eqt 
    //  ir"if ('ok == null) None else Some('ok)"
    
  }
  
}
