package scp
package ir2

import scp.utils.meta.RuntimeUniverseHelpers
import utils._

trait Lowering extends Transformer {
  val base: ir2.AST with ClassEmbedder
  import base._
  
  val warnRecursiveEmbedding = true
  
  val loweredPhases: Set[Symbol]
  
  import ClassEmbedder.Errors._
  def transform(rep: Rep): Rep = rep match {
    case RepDef(ma @ MethodApp(self, sym, targs, argss, typ)) if ma.phase exists loweredPhases =>
      methodDef(sym, targs) match {
        case Right(IR(r)) =>
          argss.foldLeft(r) {
            case (r, Args(reps @ _*)) =>
              val ruh = RuntimeUniverseHelpers
              base.rep(MethodApp(r, ruh.FunctionType.symbol(reps.size).toType member ruh.sru.TermName("apply") asMethod, Nil, Args(reps:_*)::Nil, Predef.implicitType[Nothing].rep)) // FIXME ret type!
            //case _ => ??? // TODO
          }
        case Left(Recursive) =>
          if (warnRecursiveEmbedding) System.err.println(s"Warning: Recursive value ${sym fullName} cannot be fully embedded.")
          rep
        case Left(Missing) =>
          System.err.println(s"Warning: Could not find definition for lowered method: ${sym fullName}${sym typeSignature} @ phase ${ma.phase get}") // TODO B/W
          rep
      }
    case _ => rep
  }
  
}
trait OnlineDesugaring extends OnlineOptimizer with Lowering with ClassEmbedder { self: ir2.AST with ClassEmbedder =>
  val loweredPhases = Set('Sugar)
}
