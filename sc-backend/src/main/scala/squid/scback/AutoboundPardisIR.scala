package squid
package scback

import ch.epfl.data.sc._
import ch.epfl.data.sc.pardis.deep.scalalib.ScalaPredefIRs.Println
import ch.epfl.data.sc.pardis.deep.scalalib.ScalaPredefOps
import ch.epfl.data.sc.pardis.ir.PardisFunArg
import pardis._
import squid.utils._

import scala.collection.mutable
import squid.ir.IRException
import squid.utils.CollectionUtils.TraversableOnceHelper
import squid.utils.meta.{RuntimeUniverseHelpers => ruh}

import scala.reflect.runtime.{universe => sru}
import scala.language.existentials


/** PardisIR that uses an `AutoBinder` to associate shallow method symbols to their deep embedding,
  * and special-cases a few core methods */
class AutoboundPardisIR[DSL <: ir.Base](val DSL: DSL) extends PardisIR(DSL) {
  var ab: AutoBinder[DSL.type, this.type] = _
  
  val ImperativeSymbol = loadMtdSymbol(loadTypSymbol("squid.lib.package$"), "Imperative", None)
  val IfThenElseSymbol = loadMtdSymbol(loadTypSymbol("squid.lib.package$"), "IfThenElse", None)
  val PrintlnSymbol = loadMtdSymbol(loadTypSymbol("scala.Predef$"), "println", None)
  
  /** Note: we have to wrap every method call (and corresponding statements) inside a Block.
    * It would work to simply let all expressions reify themselves in the enclosing block, but then we lose original
    * names (let-binding gets a symbol as the value and has to withSubs(bound -> sym)).
    * And original names are currently important for `rewriteRep` to distinguish explicitly-bound variables.
    * This is why we put everything in a block, and then in `letin` rewrites blocks that return a symbol use the value
    * bound in the let-in instead of that symbol. */
  def methodApp(self: Rep, mtd: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep = {
    //println("METHOD "+mtd.name+" in "+mtd.owner)
    assert(ab =/= null, s"The AutoBinder variable `ab` in $this has not been initialized.")
    
    mtd match {
        
      case ImperativeSymbol =>
        return blockWithType(tp) {
          val ArgList(efs @ _*)::Args(r)::Nil = argss
          efs foreach toExpr
          r
        }
        
      // The autobinder does not see deep `println`, as it is not defined in an object (but directly in the cake!)
      case PrintlnSymbol => sc match {
        case ir: ScalaPredefOps => return blockWithType(types.UnitType)(Println(argss.head.reps.head |> toExpr))
        case _ => throw IRException("This IR does not extend `ScalaPredefOps` and thus does not support `println`.") }
        
      case IfThenElseSymbol =>
        //val Args(cond, thn, els)::Nil = argss
        //return ir.__ifThenElse(cond.asInstanceOf[R[Bool]], thn, els)(tp.asInstanceOf[TR[Any]])
        
        // Crashes Scalac:
        //val Args(cond, TopLevelBlock(thn), TopLevelBlock(els))::Nil = argss
        //val (cond,thn,els) = argss match { case Args(cond, TopLevelBlock(thn), TopLevelBlock(els))::Nil => (cond,thn,els) } 
        
        val Args(cond, thn: ABlock @unchecked, els: ABlock @unchecked)::Nil = argss
        return blockWithType(tp)(sc.IfThenElse(toExpr(cond).asInstanceOf[R[Bool]], thn, els)(tp))
        
      case Function1ApplySymbol =>
        val arg = argss.head.asInstanceOf[Args].reps.head
        return blockWithType(tp)(sc.__app(self.asInstanceOf[R[Any=>Any]])(arg.typ.asInstanceOf[TR[Any]], tp.asInstanceOf[TR[Any]])(arg |> toExpr))
        
      case _ =>
    }
    
    val mk = ab.map.getOrElse(mtd, throw IRException(
      s"Could not find a deep representation for $mtd${mtd.typeSignature} in ${mtd owner}; perhaps it is absent from the DSL cake or the auto-binding failed."))
    
    blockWithType(tp){
    
    assert(argss.size == mtd.paramLists.size)
    def argsTail = (argss zip mtd.paramLists) flatMap { case (as,ps) =>
      val (nonRepeatedReps, repeatedReps) = as match {
        // FIXMElater: will fail on ArgsVarargSpliced
        case Args(as @ _*) =>
          assert(as.size == ps.size)
          (as, Nil)
        case ArgsVarargs(as,vas) =>
          assert(as.reps.size+1 == ps.size)
          (as.reps,vas.reps)
      }
      (nonRepeatedReps zipAnd ps) { (a, p) => p.typeSignature match {
        case sru.TypeRef(_, ruh.ByNameParamClass, tp) => a |> toBlock
        case tp => a |> toExpr
      }} ++ (repeatedReps map toExpr) // Note: repeated parameters cannot be by-name
    }
    
    val args = self match {
      case New(_) => assert(targs isEmpty); argsTail
      case null => argsTail
      case _ => (self |> toExpr) :: argsTail
    }
    
    type TRL = sc.TypeRep[Any] |> List
    
    // Method makers currently call toAtom; we don't need to do it here.
    mk(args.asInstanceOf[List[PardisFunArg]],
      // `self` will be null if it corresponds to a static module (eg: `Seq`)
      // in that case, the method type parameters are expected to be passed in first position:
      (targs If (self == null)
             Else self.typ.typeArguments
        ).asInstanceOf[TRL],
      targs.asInstanceOf[TRL])
    
    }
    
  }
  
}


