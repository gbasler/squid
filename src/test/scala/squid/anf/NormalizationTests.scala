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
  
  test("Pure Options") {
    
    assert(ir"Some(Some(42)).get.get".rep.asBlock._1.isEmpty)
    
    // `???` scheduled before `s.map`, which makes transfos not straightforward! cf. RewritingTests
    ir"var s: Option[Int] = None; s.map[Int](???)" eqt ir"""{
      var s_0: scala.Option[scala.Int] = scala.None;
      val x_1 = s_0;
      val x_2 = ((scala.Predef.???): Nothing);
      x_1.map[scala.Int](x_2)
    }"""
    
    // FIXME: those are effectful and should not be elided!
    //ir"Some(42).map(a => println(a)); ()" |> println
    //ir"Some(()).getOrElse(println); 42" |> println
    
  }
  
  object ONorm extends DSL.SelfTransformer with transfo.OptionNormalizer with TopDownTransformer
  
  test("Option Normalization") {
    
    ir"Option.empty[Int] map (_+1)" transformWith ONorm eqt 
      ir"if ((None:Option[Int]).isDefined) Some((None:Option[Int]).get+1) else None"
    
    // FIXME: assertion failed
    //ir"Option('ok)" transformWith ONorm eqt 
    //  ir"if ('ok == null) None else Some('ok)"
    
  }
  
}
