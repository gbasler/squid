package example

import org.scalatest.FunSuite
import squid.MyFunSuite
import squid.ir._

object Code extends squid.ir.SimpleANF
import Code.Predef._

object DeTup extends Code.SelfTransformer with TupleVarOptim with TopDownTransformer
object Norm extends Code.SelfTransformer with VarNormalizer with TopDownTransformer
object DeTupFix extends Code.TransformerWrapper(DeTup) with FixPointTransformer
object DeTupNormFix extends Code.TransformerWrapper(DeTup,Norm) with FixPointTransformer

/**
  * Created by lptk on 01/02/17.
  */
class TupleVarTests extends MyFunSuite(Code) {
  
  test("Recursive Detupling") {
    
    val c0 = ir"""
      var a = (1,(2,()))
      println(a._1, a._2._1, a._2._2)
    """
    
    c0 transformWith DeTupFix eqt ir"""
      var a = 1
      var b = 2
      var c = ()
      println(a,b,c)
    """
    
  }
  
  test("Recursive Detupling with nulls and units") {
    
    val c0 = ir"""
      var a: (Int,(String,Unit)) = null
      a = (1,("ok",()))
      println(a._1, a._2._1, a._2._2)
    """
    
    println(c0 transformWith DeTupNormFix)
    
    //// FIXME rm wrong assignments to null
    //c0 transformWith DeTupNormFix eqt ir"""
    //  var a = 1
    //  var b = "ok"
    //  println(a,b,())
    //"""
    
  }
  
  test("Removal of Var[Unit]") {
    
    val c0 = ir"var vu = (); println(vu); vu = (); vu"
    c0 transformWith DeTupNormFix eqt ir"println(()); ()"
    
  }
  
  
  
}
