package scp
package lang2

trait Optimizer {
  val base: IntermediateBase
  import base._
  protected def pipeline: Rep => Rep
  final def optimizeRep(pgrm: Rep): Rep = pipeline(pgrm)
  final def optimize[T,C](pgrm: IR[T,C]): IR[T,C] = `internal IR`[T,C](optimizeRep(pgrm.rep))
}
