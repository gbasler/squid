package squid.ir
package graph

import squid.utils._
import squid.utils.CollectionUtils.MutSetHelper

import scala.collection.mutable

/* In the future, we may want to remove `with CurryEncoding` and implement proper multi-param lambdas... */
class Graph extends AST with CurryEncoding { graph =>
  
  object GraphDebug extends PublicTraceDebug
  
  //val edges = mutable.Map.empty[Rep,Def]
  val edges = mutable.Map.empty[Val,Def] // invariant: Def never a Node... (that Node<:Def is a little messed up)
  
  //class Rep(v: Val)
  type Rep = Val
  class SyntheticVal(name: String, typ: TypeRep, annots: List[Annot]=Nil) extends BoundVal("@"+name: String)(typ, annots) {
    override def toString = {
      val d = dfnOrGet(this)
      if (d.isSimple) super.toString else s"${super.toString} = ${d}"
    }
  }
  //class Node(name: String)(typ: TypeRep, annots: List[Annot]=Nil) extends BoundVal("@"+name: String)(typ, annots) {
  //  def get = edges(this)
  //  //override def toString = if (get.isSimple) {
  //  //  println(name,get,get.getClass)
  //  //  get.toString
  //  //} else super.toString
  //}
  //type Node = Rep
  //type Node = Val
  object Rep {
    def unapply(r: Rep): Def|>Option = edges get r
    //def unapply(r: Rep): Some[Def] = edges(r) into Some.apply
  }
  override protected def freshNameImpl(n: Int) = "$"+n
  
  class Id
  type CtorSymbol = Class[_]
  
  case class Call(call: Id, result: Rep) extends SyntheticVal("C", result.typ) {
    
  }
  //case class Arg(nodes: mutable.Map[Option[Id], Rep]) extends SyntheticVal("C") {
  case class Arg(cid: Id, cbr: Rep, els: Option[Rep]) extends SyntheticVal("A", cbr.typ) {
    
  }
  case class Split(scrut: Rep, branches: Map[CtorSymbol, Rep]) extends SyntheticVal("S", branches.head._2.typ) {
    
  }
  
  //override def prettyPrint(d: Def) = d match {
  //  case Node(d) if d.isSimple =>
  //    ???
  //    prettyPrint(d)
  //  case _ => super.prettyPrint(d)
  //}
  override def prettyPrint(d: Def) = (new DefPrettyPrinter)(d)
  class DefPrettyPrinter extends super.DefPrettyPrinter {
    override def apply(r: Rep): String = r match {
      case Rep(d) if d.isSimple => apply(d)
      case _ => super.apply(r)
    }
    //override def apply(d: Def): String = if (get.isSimple) {
  }
  
  // Implementations of AST methods:
  
  def rep(dfn: Def) = dfn match {
    case v: Val => v
    //case _ => freshBoundVal(dfn.typ) alsoApply {edges += _ -> dfn}
    case _ => new SyntheticVal(freshName.tail, dfn.typ) also {edges += _ -> dfn}
  }
  
  //def dfn(r: Rep) = edges(r)
  //def dfn(r: Rep) = r match { case Node(d) => d  case bv => bv }
  def dfn(r: Rep): Def = r
  def dfnOrGet(r: Rep) = r match { case Rep(d) => d  case bv => bv }
  
  def repType(r: Rep) = r|>dfn typ
  
  //override def showRep(r: Rep) = r match {
  //  //case Node(_:NonTrivialDef) => super.showRep(r)
  //  //case Node(d) => 
  //  case Node(d) if r.isSimple => 
  //    println(d,d.getClass)
  //    d.toString
  //  case _ => super.showRep(r)
  //}
  //override def showRep(r: Rep) = if (r.isSimple) sh
  
  //override def showRep(r: Rep) = {
  def showGraph(r: Rep) = {
    val printed = mutable.Set.empty[Rep] // TODO rm
    //println(iterator(r).toList)
    //showGraph(r)
    //iterator(r).collect{ case nde @ Node(_: NonTrivialDef|_: Rep) if !printed(nde) =>
    //iterator(r).collect{ case nde @ Node(_: NonTrivialDef|_: Node) if !printed(nde) =>
    //iterator(r).collect{ case nde @ Rep(d: NonTrivialDef) if {assert(!printed(nde));!printed(nde)} =>
    iterator(r).collect{ case nde @ Rep(d) if !d.isSimple && {assert(!printed(nde));!printed(nde)} =>
      printed(nde) = true
      //nde.toString
      //s"$nde = ${nde|>dfn}"
      //s"$nde = ${nde.get}"
      s"$nde = ${d};\n"
    //}.toList.reverse.mkString(";\n")
    }.toList.reverse.mkString + r
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
    case MethodApp(self, mtd, targs, argss, tp) =>
      mkIterator(self) ++ argss.flatMap(_.reps.flatMap(mkIterator))
    case Abs(_, b) => mkIterator(b)
    case Ascribe(r, _) => mkIterator(r)
    case Module(r, _, _) => mkIterator(r)
    case Rep(d) => mkDefIterator(d)
    case Constant(_)|BoundVal(_)|CrossStageValue(_, _)|HoleClass(_, _)|StaticModule(_) => Iterator.empty
    //case Abs(_, _) | Ascribe(_, _) | MethodApp(_, _, _, _, _) | Module(_, _, _) | NewObject(_) | SplicedHoleClass(_, _) => ???
  }
  
  
  override def letin(bound: BoundVal, value: Rep, body: => Rep, bodyType: TypeRep) =
    //??? // oops, need a Node here
    {edges += bound -> value} thenReturn body
  
  
  
  
  import squid.quasi.MetaBases
  import squid.utils.meta.{RuntimeUniverseHelpers => ruh}
  import ruh.sru
  
  override def scalaTreeIn(MBM: MetaBases)(SRB: MBM.ScalaReflectionBase, rep: Rep, ExtrudedHandle: (BoundVal => MBM.u.Tree)): MBM.u.Tree =
    SimpleASTBackend.scalaTreeIn(MBM)(SRB, reinterpret(rep, SimpleASTBackend)(bv =>
      SimpleASTBackend.bindVal(bv.name,bv.typ.asInstanceOf[SimpleASTBackend.TypeRep],Nil).toRep), bv => {
        import MBM.u._
        //q"""scala.sys.error(${bv.name}+" not bound")"""
        q"""squid.lib.unbound(${bv.name})"""
      })
  /*
  import squid.quasi.MetaBases
  import squid.utils.meta.{RuntimeUniverseHelpers => ruh}
  import ruh.sru
  
  override def scalaTreeIn(MBM: MetaBases)(SRB: MBM.ScalaReflectionBase, rep: Rep, ExtrudedHandle: (BoundVal => MBM.u.Tree)): MBM.u.Tree = GraphDebug.muteFor { muteFor {
    new ReinterpreterToScala {
      val MetaBases: MBM.type = MBM
      val newBase: SRB.type = SRB
      override val extrudedHandle = ExtrudedHandle
    } applyRep rep
  }}
  
  abstract class ReinterpreterToScala extends super.ReinterpreterToScala {
    val MetaBases: MetaBases
    import MetaBases.u._
    val newBase: MetaBases.ScalaReflectionBase
    
    def applyRep(r: Rep) = super.apply(r)
    
    //val repCache = mutable.Map[Int, newBase.Rep]()
    
    override def apply(d: Def) = d match {
      case Imperative(es,r) => q"..${es map applyRep}; ..${r |> applyRep}"
      case _ => super.apply(d)
    }
  }
  */
  
  abstract class Reinterpreter extends super.Reinterpreter {
    //def apply(r: Rep) = apply(dfn(r):Def)
    //def apply(r: Rep) = apply(dfnOrGet(r):Def)
    //def apply(r: Rep) = apply(r match {
    //  case Rep(d: NonTrivialDef) => r
    //  case Rep(d) => d
    //  case _ => r
    //})
    def apply(r: Rep) = {
      val d = dfnOrGet(r)
      //if (d.isSimple) apply(d)
      //else apply(r:Def)
      apply(if (d.isSimple) d else r)
    }
    def applyTopLevel(r: Rep) = {
      val rtyp = rect(r.typ)
      //iterator(r).collect{ case nde @ Rep(d: NonTrivialDef) => nde -> d }.foldRight(apply(r)){
      //  case (nde -> d, term) => newBase.letin(nde |> recv, apply(d), term, rtyp)
      //}
      //iterator(r).collect{ case nde @ Rep(d: NonTrivialDef) => nde -> d }.foldRight(() => apply(r)){
      //  case (nde -> d, termFun) =>
      //    val b = nde |> recv
      //    val v = apply(d)
      //    bound += nde -> b
      //    () => newBase.letin(b, v, termFun(), rtyp)
      //}
      //iterator(r).collect{ case nde @ Rep(d: NonTrivialDef) => nde -> d }.foldLeft(() => apply(r)){
      iterator(r).collect{ case nde @ Rep(d) if !d.isSimple => nde -> d }.foldLeft(() => apply(r)){
        case (termFun, nde -> d) =>
          val b = nde |> recv
          bound += nde -> b
          () => {
            val v = apply(d)
            newBase.letin(b, v, termFun(), rtyp)
          }
      }
    }
  }
  override def reinterpret(r: Rep, NewBase: squid.lang.Base)(ExtrudedHandle: (BoundVal => NewBase.Rep) = DefaultExtrudedHandler): NewBase.Rep =
    new Reinterpreter {
      val newBase: NewBase.type = NewBase
      override val extrudedHandle = ExtrudedHandle
    } applyTopLevel r apply ()
  
  
  implicit class GraphRepOps(private val self: Rep) {
    //def reduceStep: Rep = graph.reduceStep(self) thenReturn self
    def reduceStep = self optionIf graph.reduceStep _
    def showGraph = graph.showGraph(self)
    def iterator = graph.iterator(self)
  }
  
  implicit class GraphDefOps(private val self: Def) {
    def isSimple = self match {
      case _: SyntheticVal => false
      case Constant(_)|BoundVal(_)|CrossStageValue(_, _)|HoleClass(_, _)|StaticModule(_) => true
      case _ => false
    }
  }
  
  def reduceStep(r: Rep): Bool = {
    
    r match {
      case Apply(f,arg) =>
        ???
      case _ => false
    }
    
  }
  
  
  
}
