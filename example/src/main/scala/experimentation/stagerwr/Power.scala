package stagerwr

import squid.utils._

import Embedding.Predef._
import Embedding.Quasicodes._
import squid.lib.{transparent,transparencyPropagating}

import Math.{pow,sqrt}


// FIXME: accessors and ctor not set @transparent...

//case class Position(@transparent x: Double, @transparent y: Double)
//case class Position(@transparencyPropagating x: Double, @transparencyPropagating y: Double)

//case class Planet(@transparencyPropagating pos: Position, @transparencyPropagating mass: Double)

// Note: @transparencyPropagating so that even when 
// TODO have a @pure/@transparentType/@transparent annotation to add on the Position CLASS itself, for a similar effect
class Position(private val _x: Double, private val _y: Double) { @transparencyPropagating def x = _x; @transparencyPropagating def y = _y; }
object Position { def apply(_x: Double, _y: Double) = new Position(_x,_y) }

class Planet(private val _pos: Position, private val _mass: Double) { @transparencyPropagating def pos = _pos; @transparencyPropagating def mass = _mass; }
object Planet { def apply(pos: Position, mass: Double) = new Planet(pos,mass) }



/* Created by lptk on 18/06/17. */
object Power extends App {
  
  def gravityForce(p0: Planet, p1: Planet) =
    p0.mass * p1.mass / 
      Math.pow(distance(p0.pos,p1.pos), 2)
  def distance(x0: Position, x1: Position) =
    Math.sqrt(pow(x0.x - x1.x, 2) + pow(x0.y - x1.y, 2))
  
}
