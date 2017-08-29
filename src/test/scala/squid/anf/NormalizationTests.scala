package squid
package anf

import utils._
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
  
  test("Normalization of Unit Values In Patterns") {
    
    ir"val a = println; 123" match {
      case ir"val $x = println; $body" =>
        fail // bindings of type Unit are currently removed; this test make sure that they are NOT removed in patterns 
        // if the binder is extracted (as above), which would raise a runtime assertion error (missing extracted term).
      case ir"println; $body" =>
        body eqt ir"123"
    }
    
  }
  
  object ONorm extends DSL.SelfTransformer with transfo.OptionNormalizer with TopDownTransformer
  
  test("Option Normalization") {
    
    ir"Option.empty[Int] map (_+1)" transformWith ONorm eqt 
      ir"if ((None:Option[Int]).isDefined) Some((None:Option[Int]).get+1) else None"
    
    // FIXME: assertion failed; at squid.ir.RuntimeSymbols$$anonfun$1.apply(RuntimeSymbols.scala:55)
    //ir"Option('ok)" transformWith ONorm eqt 
    //  ir"if ('ok == null) None else Some('ok)"
    
  }
  
  object INorm extends DSL.SelfTransformer with transfo.IdiomsNormalizer with TopDownTransformer
  
  test("Misc Normalizations") {
    
    ir"42.toString.toString" transformWith INorm eqt ir"42.toString"
    
    ir""" (("a":String)+"b").toString.toString |> println """ transformWith INorm eqt ir""" (("a":String)+"b").toString |> println """
    
    ir"42.asInstanceOf[Int]" transformWith INorm eqt ir"42" // Note: actually produces ir"42:Int" 
    ir"(if (true) 42 else 43).asInstanceOf[Int]" transformWith INorm eqt ir"if (true) 42 else 43"
    
    val x = ir"???.asInstanceOf[Int]" transformWith INorm
    x eqt ir"???"
    x.rep.dfn.isInstanceOf[base.Ascribe]
    
    // Q: why ascription not removed? in:
    //println(ir"val envVar = (42:Any).asInstanceOf[Int]; println(envVar)")
    //println(ir"var a: Int = 0; a = (readInt:Any).asInstanceOf[Int]; println(a)")
    
  }
  
}
