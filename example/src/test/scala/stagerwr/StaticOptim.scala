package stagerwr

import squid._
import squid.ir.{BindingNormalizer, TopDownTransformer}
import utils._
import squid.quasi.{phase, embed, dbg_embed}

object StaticOptim extends App {
  import stagerwr.MyOptimizer.{optimize,dbg_optimize}
  
  import Definitions._
  
  //println(dbg_optimize{ Definitions.lol })
  
  // TODO better error:
  /*
  val a = 123
  dbg_optimize(a)
  */
  
  
  {
    val pl0 = Planet(Position(1,2),3)
    val pl1 = Planet(Position(4,5),6)
    
    gravityForce(pl0, pl1) alsoApply println
    
    /*dbg_*/optimize{ gravityForce(pl0, pl1) } alsoApply println
    //optimize{ distance(pl0.pos, pl1.pos) } alsoApply println
    
  }
  
  
  
  
  
  
  
  
  println("Done.")
  
}


//@embed
//object Defs {
//  @phase('Sugar)
//  def lol = 42
//}
//
///* Created by lptk on 18/06/17. */
//object StaticOptim extends App {
//  
//  //object Stopt extends StaticOptimizer[Optimizer]
//  object Stopt extends StaticOptimizer[stagerwr.Optimizer] {
//    Embedding.embed(Defs)
//  }
//  import Stopt._
//  //import Stopt.optimize
//  
//  // FIXME
//  
//  /*
//  ////def opt = optimize {}
//  import Math.{sqrt,pow}
//  //val G = 6.67E-11
//  //def gravityForce(plan0: Planet, plan1: Planet) = dbg_optimize {
//  //  G * plan0.mass * plan1.mass / 
//  //    pow(distance(plan0.pos,plan1.pos), 2)
//  //}
//  def distance(p0: Position, p1: Position) = optimize {
//    sqrt(pow(p0.x - p1.x, 2) + pow(p0.y - p1.y, 2))
//  }
//  //
//  //val pl0 = Planet(Position(1,2),3)
//  //val pl1 = Planet(Position(4,5),6)
//  //gravityForce(pl0, pl1) alsoApply println
//  */
//  
//  
//  
//  
//  
//  
//  
//  
//  
//  println("Done.")
//  
//  
//  
//  
//  
//  //def lol[T](x: T) = dbg_optimize { println(x) } // FIXedME compile-time assertion error
//  //println(lol(1))
//  
//}
