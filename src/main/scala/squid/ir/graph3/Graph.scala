// Copyright 2019 EPFL DATA Lab (data.epfl.ch)
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

package squid.ir
package graph3

import squid.ir.graph.CallId
import squid.utils._
import squid.utils.CollectionUtils.MutSetHelper
import squid.utils.meta.{RuntimeUniverseHelpers => ruh}

import scala.collection.immutable.ListSet
import scala.collection.mutable

class Graph extends AST with GraphScheduling with GraphRewriting with CurryEncoding { graph =>
  
  abstract class Rep {
    //val bound: Val
    val bound: Val = freshBoundVal(typ)
    def typ: TypeRep
    //def dfn = bound
    def children: Iterator[Rep]
    def iterator = graph.iterator(this)
    def showGraph = graph.showGraph(this)
    //def showFullGraph = graph.showGraph(this,true)
    def showRep = graph.showRep(this)
  }
  class ConcreteRep(var dfn: Def, var tokenHighways: List[Rep]) extends Rep {
    //val bound: Val = freshBoundVal(typ)
    def typ = dfn.typ
    def children = dfn.children
    override def toString = s"$bound = $dfn"
  }
  case class Call(cid: CallId, res: Rep, tokenHighways: List[Rep]) extends Rep {
    def typ = res.typ
    def children: Iterator[Rep] = Iterator.single(res)
  }
  
  //def dfn(r: Rep): Def = r.dfn
  def dfn(r: Rep): Def = r.bound
  //def rep(dfn: Def) = new Rep(dfn, dfn.children.toList)
  def rep(dfn: Def) = dfn match {
    case v: Val if reificationContext.contains(v) => reificationContext(v)
    case _ => new ConcreteRep(dfn, dfn.children.toList)
  }
  //def simpleRep(dfn: Def): Rep = rep(dfn)
  def repType(r: Rep) = r.typ
  
  
  
  val reificationContext = mutable.Map.empty[Val,Rep]
  
  def letinImpl(bound: BoundVal, value: Rep, body: => Rep) = { 
    require(!reificationContext.contains(bound))
    try {
      reificationContext += bound -> value
      body
    } finally reificationContext -= bound 
  }
  
  override def letin(bound: BoundVal, value: Rep, body: => Rep, bodyType: TypeRep) =
    letinImpl(bound,value,body)
  
  
  override def substituteVal(r: Rep, v: BoundVal, mkArg: => Rep): Rep = {
    val cid = new CallId("α")
    
    
    
    /*
    val occ = Option(lambdaBound.get(v)).getOrElse(???) // TODO B/E
    //println(s"OCC $occ")
    val mir = boundTo_!(occ).asInstanceOf[ConcreteNode].dfn.asInstanceOf[MirrorVal]
    assert(mir.v === v)
    
    val newOcc = mir.toRep
    val arg = mkArg
    val bran = Branch(Condition.simple(cid), arg, newOcc)
    
    rebind(occ, bran)
    lambdaBound.put(v,newOcc.bound)
    
    // Q: does this create code dup?
    /*
    val body = r.boundTo.mkRep
    rebind(r.bound, Pass(cid, body))
    */
    /*
    val origBody = mir.getAbs.body
    val body = origBody.boundTo.mkRep
    rebind(origBody.bound, Pass(cid, body))
    */
    
    val abs = mir.getAbs
    //println(s"ABS ${mir.abs}")
    assert(mir.getAbs =/= null)
    val body = abs.body
    //println(s"ABSd ${abs}")
    mir.rebindAbs(Abs(abs.param, Pass(cid, body).mkRep)(abs.typ))
    //println(s"NABS ${mir.abs}")
    
    Call(cid, r).mkRep
    */
    ???
  }
  
  
  def iterator(r: Rep): Iterator[Rep] = mkIterator(r)(false,mutable.HashSet.empty)
  def mkIterator(r: Rep)(implicit rev: Bool, done: mutable.HashSet[Rep]): Iterator[Rep] = done.setAndIfUnset(r, {
    Iterator.single(r) ++ r.children.flatMap(mkIterator)
  }, Iterator.empty)
  
  def showGraph(rep: Rep, full: Bool = false): String = s"$rep" + {
    val defsStr = iterator(rep).toList.distinct.filterNot(_ === rep).collect {
      // TODO use 'full'
      case r => s"\n\t$r;"
      //case r if full =>
      //  s"\n\t${r.bound} = ${r.boundTo.mkString(false,false)};"
      //case r @ Rep(ConcreteNode(d)) if !d.isSimple => s"\n\t${r.bound} = ${d};"
    }.mkString
    if (defsStr.isEmpty) "" else " where:" + defsStr
  }
  
  
  // TODO
  type XCtx = Unit
  def newXCtx: XCtx = ()
  
  
  
}

trait GraphScheduling

trait GraphRewriting
