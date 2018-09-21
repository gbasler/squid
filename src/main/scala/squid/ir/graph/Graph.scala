package squid.ir
package graph

import squid.utils._
import squid.utils.CollectionUtils.MutSetHelper

import scala.collection.mutable

/* In the future, we may want to remove `with CurryEncoding` and implement proper multi-param lambdas... */
class Graph extends AST with CurryEncoding {
  
  //val edges = mutable.Map.empty[Rep,Def]
  val edges = mutable.Map.empty[Node,Def] // invariant: Def never a Node... (that Node<:Def is a little messed up)
  
  //class Rep(v: Val)
  type Rep = Val
  class Node(name: String)(typ: TypeRep, annots: List[Annot]=Nil) extends BoundVal("@"+name: String)(typ, annots) {
    def get = edges(this)
    //override def toString = if (get.isTrivial) {
    //  println(name,get,get.getClass)
    //  get.toString
    //} else super.toString
  }
  //type Node = Rep
  object Node {
    //def unapply(r: Node): Def|>Option = edges get r
    def unapply(r: Node): Some[Def] = edges(r) into Some.apply
  }
  override protected def freshNameImpl(n: Int) = "$"+n
  
  //override def prettyPrint(d: Def) = d match {
  //  case Node(d) if d.isTrivial =>
  //    ???
  //    prettyPrint(d)
  //  case _ => super.prettyPrint(d)
  //}
  override def prettyPrint(d: Def) = (new DefPrettyPrinter)(d)
  class DefPrettyPrinter extends super.DefPrettyPrinter {
    override def apply(r: Rep): String = r match {
      case Node(d) if d.isTrivial => apply(d)
      case _ => super.apply(r)
    }
    //override def apply(d: Def): String = if (get.isTrivial) {
  }
  
  // Implementations of AST methods:
  
  def rep(dfn: Def) = dfn match {
    case v: Val => v
    //case _ => freshBoundVal(dfn.typ) alsoApply {edges += _ -> dfn}
    case _ => new Node(freshName.tail)(dfn.typ) alsoApply {edges += _ -> dfn}
  }
  
  //def dfn(r: Rep) = edges(r)
  //def dfn(r: Rep) = r match { case Node(d) => d  case bv => bv }
  def dfn(r: Rep) = r
  def dfnOrGet(r: Rep) = r match { case Node(d) => d  case bv => bv }
  
  def repType(r: Rep) = r|>dfn typ
  
  //override def showRep(r: Rep) = r match {
  //  //case Node(_:NonTrivialDef) => super.showRep(r)
  //  //case Node(d) => 
  //  case Node(d) if r.isTrivial => 
  //    println(d,d.getClass)
  //    d.toString
  //  case _ => super.showRep(r)
  //}
  //override def showRep(r: Rep) = if (r.isTrivial) sh
  
  //override def showRep(r: Rep) = {
  def showGraph(r: Rep) = {
    val printed = mutable.Set.empty[Node]
    //println(iterator(r).toList)
    //showGraph(r)
    //iterator(r).collect{ case nde @ Node(_: NonTrivialDef|_: Rep) if !printed(nde) =>
    //iterator(r).collect{ case nde @ Node(_: NonTrivialDef|_: Node) if !printed(nde) =>
    iterator(r).collect{ case nde @ Node(_: NonTrivialDef) if !printed(nde) =>
      printed(nde) = true
      //nde.toString
      //s"$nde = ${nde|>dfn}"
      s"$nde = ${nde.get}"
    }.toList.reverse.mkString(";\n")
  }
  //def showGraph(r: Rep) = {
  //}
  def iterator(r: Rep): Iterator[Rep] = mkIterator(r)(mutable.HashSet.empty)
  def mkIterator(r: Rep)(implicit done: mutable.HashSet[Rep]): Iterator[Rep] =
    //if (done(r)) Iterator.empty else {
    //  done(r) = true
    //  Iterator.single(r) ++ defn.mkIterator
    //}
    done.setAndIfUnset(r, Iterator.single(r) ++ mkDefIterator(dfnOrGet(r)), Iterator.empty)
  def mkDefIterator(dfn: Def)(implicit done: mutable.HashSet[Rep]): Iterator[Rep] = dfn match {
    case Constant(_)|BoundVal(_)|CrossStageValue(_, _)|HoleClass(_, _)|StaticModule(_) => Iterator.empty
    case MethodApp(self, mtd, targs, argss, tp) =>
      mkIterator(self) ++ argss.flatMap(_.reps.flatMap(mkIterator))
    //case Abs(_, _) | Ascribe(_, _) | MethodApp(_, _, _, _, _) | Module(_, _, _) | NewObject(_) | SplicedHoleClass(_, _) => ???
  }
  
  
  override def letin(bound: BoundVal, value: Rep, body: => Rep, bodyType: TypeRep) = ??? // oops, need a Node here
  
  
  
  
  /*
  import squid.quasi.MetaBases
  import squid.utils.meta.{RuntimeUniverseHelpers => ruh}
  import ruh.sru
  
  override def scalaTreeIn(MBM: MetaBases)(SRB: MBM.ScalaReflectionBase, rep: Rep, ExtrudedHandle: (BoundVal => MBM.u.Tree)): MBM.u.Tree = ANFDebug.muteFor { muteFor {
    new ReinterpreterToScala {
      val MetaBases: MBM.type = MBM
      val newBase: SRB.type = SRB
      override val extrudedHandle = ExtrudedHandle
    } apply rep
  }}
  
  abstract class ReinterpreterToScala extends super.ReinterpreterToScala {
    val MetaBases: MetaBases
    import MetaBases.u._
    val newBase: MetaBases.ScalaReflectionBase
    
    //val repCache = mutable.Map[Int, newBase.Rep]()
    
    override def apply(d: Def) = d match {
      case Imperative(es,r) => q"..${es map apply}; ..${r |> apply}"
      case _ => super.apply(d)
    }
  }
  */
  
}
