package squid
package anf
package transfo

import utils._
import ir._

/**
  * Created by lptk on 12/02/17.
  */
class VarOptTests extends MyFunSuite(SimpleANFTests.DSL) {
  import DSL.Predef._
  
  //object VInl extends DSL.SelfTransformer with VarInliner // with TopDownTransformer // TODO 
  object VInl extends DSL.SelfTransformer with VarFlattening with TopDownTransformer 
  
  test("Options") {
    
  }
  
  test("Dang") {
    /*
    //println(ir"var a = Some(42); a.get" transformWith Inl)  // FIXME?!
    println(ir"var a: Option[Int] = Some(42); a.get" transformWith VInl)
    
    println(ir"var a: Option[Int] = Some(42); if (a.isDefined) {println('lol);println(a.get)}" transformWith VInl)
    */
    
    //val v = ir"v?:squid.lib.Var[Int]"  // NOTE: prevents the optim!! cf. var inlining matches the var with same name 'v'...
    val v = ir"u?:squid.lib.Var[Int]"
    val w = ir"w?:squid.lib.Var[Int]"
    println(ir"""
    var x: Option[Int] = Some(42);
    if (x.isDefined)
  {
    val x_0 = $v.!;
    val x_1 = x_0.<(100);
    val x_7 = x_1.&&({
      val x_2 = $v.!;
      val x_3 = x_2.+(1);
      $v := x_3;
      val x_4 = $w.!;
      val x_5 = x_4.+(x.get);
      $w := x_5;
      val x_6 = $v.!;
      x_6.<(100)
    });
    //`?[cont:squid.lib.package.Var[Boolean]]?` = x_7
    println(x_7)
  }
else
  //`?[finished:squid.lib.package.Var[Boolean]]?` = true
  println(true)
""" transformWith VInl)
    
  }
  
  
  test("VarVar") {
    import squid.lib.Var
    
    val q0 = ir"var v = Var(0); println(v.!); v = Var(1); v.! + 1" and println
    val q1 = q0 transformWith VInl and println
    
  }
  
  
  test("VarPE") {
    
    val q0 = ir"var v = 0; var w = v; println(v+w); if (v>0) {println(w)}" and println
    //val q0 = ir"var v = 0; var w = v+1; println(v+w); if (v>0) {println(w)}" and println // does not simplify fully :-(
    //val q0 = ir"var v = 0; var w = v+1; println(v+w); v = w; if (v>0) {println(w)}" and println // does not simplify :-(
    val q1 = q0 transformWith (new DSL.SelfTransformer with VarSimplification with TopDownTransformer with FixPointRuleBasedTransformer) and println
    
  }
  
  
  test("NonlocalVar") {
    import squid.lib.Var
    
    //ir"println(Var(42))".rep and println
    ir"println(Var(42))" and println
    ir"val v = Var(42); v := 0; println(v)" and println
    ir"println(Var(42).!)" and println
    
  }
    
  
}
