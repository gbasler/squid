package stagerwr

import squid._
import squid.ir.{BindingNormalizer, TopDownTransformer}
import utils._
import squid.quasi.{phase, embed, dbg_embed}
import squid.lib.{transparent,transparencyPropagating}

/**
  * Created by lptk on 20/06/17.
  */
class MyOptimizer extends stagerwr.Optimizer {
  
  //println("E "+Embedding.methods)
  Embedding.embed(Definitions)
  //println("E "+Embedding.methods)
  
  Embedding.addTransparentMethod(Embedding.methodSymbol[math.`package`.type]("pow"))
  Embedding.addTransparentMethod(Embedding.methodSymbol[math.`package`.type]("sqrt"))
  
  import Embedding.Predef._
  import math.{pow,sqrt}
  
  Embedding.Norm.rewrite {
    //case ir"42" => ir"43"
    case ir"Math.pow($base,$exp)" => ir"pow($base,$exp)"
    case ir"Math.sqrt($x)" => ir"pow($x,0.5)"
    case ir"pow(pow($x,${Const(a)}),${Const(b)})" if b.isWhole => ir"pow($x,${Const(a * b)})"
    case ir"pow($x,1)" => x
  }
  
}
object MyOptimizer extends StaticOptimizer[MyOptimizer]

@embed
//@dbg_embed
object Definitions {
  
  @phase('Sugar)
  def lol = 42
  
  import Math.{pow,sqrt}
  
  @phase('Sugar)
  def gravityForce(p0: Planet, p1: Planet) =
    p0.mass * p1.mass / 
      Math.pow(distance(p0.pos,p1.pos), 2)
  
  @phase('Sugar)
  def distance(x0: Position, x1: Position) =
    Math.sqrt(pow(x0.x - x1.x, 2) + pow(x0.y - x1.y, 2))
  
  
}

