package scp
package scback

import ch.epfl.data.sc.pardis.ir.Constant
import ch.epfl.data.sc.pardis.ir.FunctionNode
import ch.epfl.data.sc.pardis.ir.PardisFunArg
import ch.epfl.data.sc.pardis.ir.PardisNode
import ch.epfl.data.sc.pardis.ir.PardisVarArg
import scp.utils._

class PardisTestSuite extends MyFunSuiteTrait with DSLBindingTest {
  val DSL: Sqd.type = Sqd
  
  import Sqd.Predef._
  import Sqd.Quasicodes._
  
  
  // Helper Methods
  
  def stmts_ret(x: IR[_,_]) = x.rep match {
    case SC.Block(sts, r) => sts -> r
  }
  def stmts(x: IR[_,_]) = stmts_ret(x)._1
  def ret(x: IR[_,_]) = stmts_ret(x)._2
  
  def dfn(x: IR[_,_]) = {
    val sts = stmts(x)
    assert(sts.size == 1)
    sts.head.rhs
  }
  
  /** Compares the contents of blocks, ignoring symbols */
  def sameDefs(x: IR[_,_],y: IR[_,_], dbg: Bool = false): Unit = {
    if (dbg) println(s"Comparing:\n->\t$x\n->\t$y")
    val (s0,r0) = stmts_ret(x)
    val (s1,r1) = stmts_ret(y)
    sameDefs(s0,r0,s1,r1)
  }
  def sameDefs(s0:List[SC.Stm[_]],r0:SC.Rep[_],s1:List[SC.Stm[_]],r1:SC.Rep[_]): Unit = {
    
    def sameFunArgs(xs: List[PardisFunArg], ys: List[PardisFunArg]) = {
      assert(xs.size == ys.size)
      xs zip ys foreach {
        case Constant(a) -> Constant(b) => assert(a==b)
        case (_:SC.Sym[_]) -> (_:SC.Sym[_]) => // ignore
        case (_:PardisVarArg) -> (_:PardisVarArg) => // ignore
        case (rhs0:SC.Block[_]) -> (rhs1:SC.Block[_]) =>
          sameDefs(rhs0.stmts, rhs0.res, rhs1.stmts, rhs1.res)
        case a -> b => fail(s"$a ? $b")
      }
    }
    
    assert(s0.size == s1.size)
    s0 zip s1 foreach {
      case SC.Stm(_,rhs0:FunctionNode[_]) -> SC.Stm(_,rhs1:FunctionNode[_]) =>
        assert(rhs0.name == rhs1.name)
        assert(rhs0.typeParams.size == rhs1.typeParams.size)
        rhs0.typeParams zip rhs1.typeParams foreach (ab => assert(ab._1 == ab._2))
        sameFunArgs(rhs0.funArgs, rhs1.funArgs)
      case SC.Stm(_,rhs0:SC.Block[_]) -> SC.Stm(_,rhs1:SC.Block[_]) =>
        sameDefs(rhs0.stmts, rhs0.res, rhs1.stmts, rhs1.res)
      case SC.Stm(_,rhs0:PardisNode[_]) -> SC.Stm(_,rhs1:PardisNode[_]) =>
        assert(rhs0.nodeName == rhs1.nodeName)
        sameFunArgs(rhs0.funArgs, rhs1.funArgs)
      case _ => fail
    }
  }
  def sameDefsAfter(x: IR[_,_], f: IR[_,_]=>IR[_,_]): Unit = sameDefs(x, f(x))
  
  
  
}

