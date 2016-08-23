package scp
package ir2

import utils._
import lang2._


trait OnlineTransformer extends FixPointTransformer with InspectableBase {
  val base: this.type = this
  
  abstract override def readVal(v: BoundVal): Rep = transform(super.readVal(v))
  abstract override def const(value: Any): Rep = transform(super.const(value))
  abstract override def lambda(params: List[BoundVal], body: => Rep): Rep = transform(super.lambda(params, body))
  
  abstract override def moduleObject(fullName: String, isPackage: Boolean): Rep = transform(super.moduleObject(fullName, isPackage))
  abstract override def staticModule(fullName: String): Rep = transform(super.staticModule(fullName))
  abstract override def module(prefix: Rep, name: String, typ: TypeRep): Rep = transform(super.module(prefix, name, typ))
  abstract override def newObject(tp: TypeRep): Rep = transform(super.newObject(tp))
  abstract override def methodApp(self: Rep, mtd: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep =
    transform(super.methodApp(self, mtd, targs, argss, tp))
  
  abstract override def byName(arg: => Rep): Rep = transform(super.byName(arg))
  
  
  
}

//class OfflineTransformer[B <: lang.Base](val base: B) extends Transformer  // TODO

