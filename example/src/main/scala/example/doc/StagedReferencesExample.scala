package example.doc

import squid.utils._
import example.doc.{SimpleEmbed => Embedding}
import Embedding.Predef._
import Embedding.Quasicodes._

object StagedReferencesExample extends App {
  
  trait Lattice {
    type This
    def merge(that: This): This
  }
  case class BoolOrLattice(private var self: Bool) extends Embedding.SelfAssignedVariable[BoolOrLattice] with Lattice {
    type This = BoolOrLattice
    def merge(that: This): This = new BoolOrLattice(that.self || self)
    def &&= (that: BoolOrLattice): Unit = self &&= that.self
    def ||= (that: BoolOrLattice): Unit = self ||= that.self
    def && (that: BoolOrLattice): BoolOrLattice = new BoolOrLattice(that.self && self)
    def || (that: BoolOrLattice): BoolOrLattice = new BoolOrLattice(that.self || self)
  }
  
  abstract class StagedProgram {
    
    def mkRules[Ctx](vl: Assignment[Ctx])(rulesProgram: IR[Unit,Ctx]) = {
      println(s"Making rules from program: $rulesProgram")
      vl.compile(ir"() => $rulesProgram")
    }
    
  }
  
  class MyProgram extends StagedProgram {
    
    val b1 = new BoolOrLattice(true)
    val b2 = new BoolOrLattice(false)
    val b3 = new BoolOrLattice(false)
    
    val executeRules = mkRules(b1 & b2 & b3)(ir"""
      $b3 ||= $b2 && $b1
      $b2 ||= $b1 || $b2
    """)
    
  }
  
  val mp = new MyProgram
  
  println(mp.b1, mp.b2, mp.b3)
  mp.executeRules()
  println(mp.b1, mp.b2, mp.b3)
  mp.executeRules()
  println(mp.b1, mp.b2, mp.b3)
  
}
