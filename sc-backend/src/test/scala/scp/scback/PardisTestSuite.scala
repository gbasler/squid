package scp
package scback

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
  
  
}

