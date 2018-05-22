// Copyright 2017 EPFL DATA Lab (data.epfl.ch)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package squid.lang

trait Optimizer {
  val base: InspectableBase
  import base._
  protected def pipeline: Rep => Rep
  //final def optimizeRep(pgrm: Rep): Rep = pipeline(pgrm)
  final def optimizeRep(pgrm: Rep): Rep = { // TODO do this Transformer's `pipeline`......?
    val r = pipeline(pgrm)
    if (!(r eq pgrm)) substitute(r) else r  // only calls substitute if a transformation actually happened
  }
  /** Applies the pipeline of optimizations on a whole program; i.e., this is not to be called as an online
    * transformation step (use `optimizeRep` for this use case instead). */
  def optimizeOffline(pgrm: Rep): Rep = optimizeRep(pgrm)
  final def optimize[T,C](pgrm: Code[T,C]): Code[T,C] = `internal Code`[T,C](optimizeOffline(pgrm.rep))
  
  def wrapOptim[A](id: String)(code: => A) = code
  
  
  def setContext(src:String): Unit = ()
  
}

trait LoggingOptimizer extends Optimizer {
  import base.Rep
  
  /** Whether to print optimization phases on standard output (for easy debugging). */
  val printResults = false
  
  /** Whether to show the original program, on `optimizeOffline` calls. */
  val logOriginalProgram = true
  
  /** Whether to show the program after the pipeline of transformations, on `optimizeOffline` calls. */
  val logResultAfterPipeline = true
  
  protected var ctx = Option.empty[scala.tools.nsc.io.File]
  override def setContext(src:String): Unit = {
      val f = scala.tools.nsc.io.File(src)
      if (f.exists) f.delete()
      ctx = Some(f)
  }
  
  protected def dump(phaseName: String, tree: Rep) = {
    lazy val str = s"// --- $phaseName ---\n"+base.showRep(tree)
    if (printResults) println(str)
    ctx foreach { _.appendAll(str+"\n") }
  }
  
  override def optimizeOffline(pgrm: Rep): Rep = {
    if (logOriginalProgram) dump("Embedded Program", pgrm)
    val res = super.optimizeRep(pgrm)
    if (logResultAfterPipeline) dump("After Optimization Pipeline", res)
    res
  }
  
}
