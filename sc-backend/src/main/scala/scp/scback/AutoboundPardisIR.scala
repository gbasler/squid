package scp
package scback

import ch.epfl.data.sc._
import ch.epfl.data.sc.pardis.deep.scalalib.ScalaPredefOps
import pardis._
import scp.utils._

import scala.collection.mutable
import scp.ir2.IRException


/** PardisIR that uses an `AutoBinder` to associate shallow method symbols to their deep embedding,
  * and special-cases a few core methods */
class AutoboundPardisIR[DSL <: ir.Base](val DSL: DSL) extends PardisIR(DSL) {
  var ab: AutoBinder[DSL.type, this.type] = _
  
  val ImperativeSymbol = loadMtdSymbol(loadTypSymbol("scp.lib.package$"), "Imperative", None)
  val IfThenElseSymbol = loadMtdSymbol(loadTypSymbol("scp.lib.package$"), "IfThenElse", None)
  val PrintlnSymbol = loadMtdSymbol(loadTypSymbol("scala.Predef$"), "println", None)
  
  def inlineBlock(b: ir.Block[Any]) = b |> toAtom  // TDOO proper impl
  //  b.stmts foreach 
  //  b.res
  //}
  
  /** TODO a general solution to by-name parameters */
  def methodApp(self: Rep, mtd: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep = {
    //println(mtd, mtd.owner)
    
    mtd match {
        
      case ImperativeSymbol => return argss.tail.head.reps.head
        
      case PrintlnSymbol => return ir.asInstanceOf[ScalaPredefOps].println(argss.head.reps.head)
        
      case IfThenElseSymbol =>
        //val Args(cond, thn, els)::Nil = argss
        //return ir.__ifThenElse(cond.asInstanceOf[R[Bool]], thn, els)(tp.asInstanceOf[TR[Any]])
        
        // Crashes Scalac:
        //val Args(cond, TopLevelBlock(thn), TopLevelBlock(els))::Nil = argss
        //val (cond,thn,els) = argss match { case Args(cond, TopLevelBlock(thn), TopLevelBlock(els))::Nil => (cond,thn,els) } 
        
        val Args(cond, thn0, els0)::Nil = argss
        val (thn,els) = (thn0.asInstanceOf[TopLevelBlock[Any]].b, els0.asInstanceOf[TopLevelBlock[Any]].b)
        
        return ir.__ifThenElse(cond.asInstanceOf[R[Bool]], thn |> inlineBlock, els |> inlineBlock)(tp.asInstanceOf[TR[Any]])
        
      case Function1ApplySymbol =>
        val arg = argss.head.asInstanceOf[Args].reps.head
        return ir.__app(self.asInstanceOf[R[Any=>Any]])(arg.tp.asInstanceOf[TR[Any]], tp.asInstanceOf[TR[Any]])(arg)
        
      case _ =>
    }
    
    //println(ab.map.get(mtd))
    val mk = ab.map.getOrElse(mtd, throw IRException(
      s"Could not find a deep representation for $mtd in ${mtd owner}; perhaps it is absent from the DSL cake or the auto-binding failed."))
    
    val argsTail = argss.flatMap(_.reps)
    
    //println(argss)
    val args = self match {
      case New(tps) => assert(targs isEmpty); argsTail
      case _ => self :: argsTail
    }
    
    type TRL = ir.TypeRep[Any] |> List
    mk(args, self.tp.typeArguments.asInstanceOf[TRL], targs.asInstanceOf[TRL])
  }
  
}


