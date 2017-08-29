package qqpaper

import squid._
import quasi.macroDef
import stagerwr.Embedding
import stagerwr.Embedding.Predef._

//import utils.MacroUtils.{MacroSetting, MacroDebug, MacroDebugger}

/**
  * Created by lptk on 21/06/17.
  */
object MacroExample {
  
  
  
  @macroDef(Embedding)
  def power_naive(base: Double, exp: Int): Double = {
    
    exp match {
      case Const(exp) =>
        var cur = ir"1.0"
        for (i <- 1 to exp) cur = ir"$cur * $base"
        cur
      case _ => ir"Math.pow($base, $exp.toDouble)"
    }
    
  }
  
  
  @macroDef(Embedding)
  def power(base: Double, exp: Int): Double = {
    
    exp match {
      case Const(exp) =>
        ir"val base = $base; ${ (base: IR[Double,Any]) =>
          var cur = ir"1.0"
          for (i <- 1 to exp) cur = ir"$cur * $base"
          cur
        }(base)"
      case _ => ir"Math.pow($base, $exp.toDouble)"
    }
    
  }
  
  
  
  
  
  
  
  
}
