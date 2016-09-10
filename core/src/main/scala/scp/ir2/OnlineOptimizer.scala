package scp
package ir2

import utils._
import lang2._


trait OnlineOptimizer extends Optimizer with IntermediateBase {
  val base: this.type = this
  
  val transformExtractors = false
  
  /** It is important not to try and optimize extractors, as it may result in unexpected behavior and the loss of extraction holes.
    * For example, beta reduction on a redex pattern where the body  isextracted will "eat" the argument value
    * (it won't find any occurence of the binder in the body, which is just a hole!) */
  protected def processOnline(r: Rep) = if (isExtracting && !transformExtractors) r else optimizeRep(r)
  
  abstract override def readVal(v: BoundVal): Rep = processOnline(super.readVal(v))
  abstract override def const(value: Any): Rep = processOnline(super.const(value))
  abstract override def lambda(params: List[BoundVal], body: => Rep): Rep = processOnline(super.lambda(params, body))
  
  abstract override def staticModule(fullName: String): Rep = processOnline(super.staticModule(fullName))
  abstract override def module(prefix: Rep, name: String, typ: TypeRep): Rep = processOnline(super.module(prefix, name, typ))
  abstract override def newObject(tp: TypeRep): Rep = processOnline(super.newObject(tp))
  abstract override def methodApp(self: Rep, mtd: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep =
    processOnline(super.methodApp(self, mtd, targs, argss, tp))
  
  abstract override def byName(arg: => Rep): Rep = processOnline(super.byName(arg))
  
}

