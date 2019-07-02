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

import squid.utils._
import squid.utils.CollectionUtils.MapHelper

import scala.collection.mutable

abstract class ParameterPassingStrategy {
  def mkParams(fun: String, params: List[String]): String
  def mkArgs(fun: String, args: List[String]): String =
    if (args.isEmpty) fun else "(" + mkParams(fun, args) + ")"
}
object UnboxedTupleParameters extends ParameterPassingStrategy {
  def mkParams(fun: String, params: List[String]): String =
    if (params.isEmpty) fun else params.mkString(s"$fun(# ", ", ", " #)")
}
object CurriedParameters extends ParameterPassingStrategy {
  def mkParams(fun: String, params: List[String]): String =
    (fun :: params).mkString(" ")
}

abstract class HaskellAST(pp: ParameterPassingStrategy) {
  
  type Ident
  def printIdent(id: Ident): String
  def mkIdent(nameHitn: String): Ident
  
  def mkCtorStr(ctor: String) =
    if (ctor.head.isLetter || ctor === "[]" || ctor.head === '(') ctor else s"($ctor)"
  
  /** Important: `id` is used to uniquely identify the definition. */
  class Defn(val id: Ident, val params: List[Vari], val body: Expr) {
    def toBeInlined: Bool =
      body.isTrivial ||
        //false
        body.size <= params.size + 1 // makes sure to inline calls to trivial 'forwarding' defs like `_0(a, b) = a + b`
    
    def occurrences = body.occurrences -- params
    def shareableExprs = body.shareableExprs.filterKeys(k => !k.freeVars.exists(params.contains))
    
    def subs(v: Vari, e: Expr): Defn = if (params.contains(v)) this else new Defn(id, params, body.subs(v, e))
    def map(f: Expr => Expr): Defn = new Defn(id, params, body.map(f))
    
    def inline(args: List[Expr]) = {
      assert(params.size === args.size)
      //assert(!isRecursive)
      val res = (params,args).zipped.foldRight(body) { case ((p,a),acc) => Let(p,a,acc) }
      res
    }
    
    //val isRecursive = // would need to look at all calls in body
    
    lazy val simplifiedBody = body.simplify.cse
    
    def simplifyRec(implicit caseCtx: Map[(Expr,String),List[Vari]]) = {
      val s = body.simplifyRec // TODO make simplifyRec eq-preserving in case of no changes
      if (s eq body) this else new Defn(id, params, s)
    }
    
    def stringify: String = {
      s"${pp.mkParams(printIdent(id), params.map(_.id |> printIdent))} = ${simplifiedBody.stringify}"
    }
    
    override def equals(that: Any) = that match { case d: Defn => id === d.id case _ => false }
    override def hashCode() = id.hashCode
    
    override def toString = s"$id(${params.mkString(", ")})"
  }
  
  private[this] val noOccs: Map[Vari, Int] = Map.empty[Vari, Int].withDefaultValue(0)
  private[this] val noSubExprs: Map[Expr, Int] = Map.empty[Expr, Int].withDefaultValue(0)
  
  private[this] val findLets: Expr => (List[String], String) = {
    case Let(v,e,b) => findLets(b) ||> (_1 = s"${v.stringify} = ${e.stringify}" :: _)
    case e => Nil -> e.stringify
  }
  private[this] def stringifyLets(known: List[String], rest: Expr, paren: Bool): String = {
    val (discovered, restStr) = findLets(rest)
    val lets = known ++ discovered.reverse
    val str = lets match {
      case Nil => restStr
      case single :: Nil => s"let $single in $restStr"
      case many => s"let { ${many.mkString("; ")} } in $restStr"
    }
    if (paren) s"($str)" else str
  }
  
  sealed abstract class Expr {
    
    def isTrivial: Bool = this match {
      case _: Inline | _: Vari | Call(_, Nil) => true
      case _ => false
    }
    
    def stringify: String = this match {
      case Vari(id) => printIdent(id)
      case Inline(str) => str
      case App(lhs, rhs) => s"(${lhs.stringify} ${rhs.stringify})"
      case Lam(p, ds, b) =>
        s"(\\${p.id |> printIdent} -> ${stringifyLets(ds.map(_.stringify), b, paren = false)})"
      case Call(d, args) =>
        s"${pp.mkArgs(d.value.id |> printIdent, args.map(_.stringify))}"
      case Let(v,e,b) =>
        stringifyLets(s"${v.stringify} = ${e.stringify}" :: Nil, b, paren = true)
      case Case(scrut, arms) =>
        s"(case ${scrut.stringify} of {${arms.map { case (con, vars, rhs) =>
            mkCtorStr(con) + vars.map(_.id |> printIdent).map{" "+_}.mkString + s" -> ${rhs.stringify}"
        }.mkString("; ")}})"
      case CtorField(scrut, ctor, arity, idx) =>
        s"(case ${scrut.stringify} of ${mkCtorStr(ctor)} ${List.tabulate(arity)(i => if (i === idx) "arg" else "_").mkString(" ")} -> arg)"
    }
    
    def freeVars: Set[Vari] = occurrences.keySet
    
    /** Important: not all occurrences of case-bound variables may be visible  */
    lazy val occurrences: Map[Vari, Int] = this match {
      case Inline(str) => noOccs
      case v: Vari => (noOccs mergeValue (v -> 1))(_ + _)
      case App(lhs, rhs) => (lhs.occurrences mergeValues rhs.occurrences)(_ + _)
      case Lam(p, ds, b) => ds.foldLeft(b.occurrences){case (acc,e) => (acc mergeValues e.occurrences)(_ + _)} - p
      case Call(d, as) =>
        as.iterator.foldLeft(noOccs){case (acc,e) => (acc mergeValues e.occurrences)(_ + _)}
      case Let(v, e, b) => (e.occurrences mergeValues (b.occurrences - v))(_ + _)
      case Case(scrut, arms) => arms.foldLeft(scrut.occurrences){ case (acc,(con,vals,e)) =>
        (acc mergeValues (e.occurrences -- vals))(_ + _)}
      case CtorField(scrut, ctor, arity, idx) => scrut.occurrences // Approximation... we may later resolve this expr to a variable!
    }
    private[this] def countThis(exprs: Map[Expr,Int]): Map[Expr,Int] = (exprs mergeValue (this -> 1))(_ + _)
    lazy val shareableExprs: Map[Expr, Int] = this match {
      case Inline(str) => noSubExprs
      case v: Vari => noSubExprs
      case App(lhs, rhs) => (lhs.shareableExprs mergeValues rhs.shareableExprs)(_ + _) |> countThis
      case Lam(p, ds, b) =>
        ds.foldLeft(b.shareableExprs){case (acc,e) => (acc mergeValues e.shareableExprs)(_ + _)}
          .filterKeys(k => !k.freeVars(p)) |> countThis
      case Call(d, as) =>
        as.iterator.foldLeft(noSubExprs){case (acc,e) => (acc mergeValues e.shareableExprs)(_ + _)} |> countThis
      case Let(v, e, b) =>
        (e.shareableExprs mergeValues (b.shareableExprs - v))(_ + _) // Note: let's not try to share lets...
      case Case(scrut, arms) => arms.foldLeft(scrut.shareableExprs){ case (acc,(con,vals,e)) => (acc mergeValues
        e.shareableExprs.filterKeys(k => !k.freeVars.exists(vals.contains)))(_ + _) } // And let's not share cases, for perf reasons
      case CtorField(scrut, ctor, arity, idx) => scrut.shareableExprs |> countThis
    }
    def subs(v: Vari, e: Expr): Expr = this match {
      case c: Inline => c
      case w: Vari => if (w == v) e else w
      case App(lhs, rhs) => App(lhs.subs(v, e), rhs.subs(v, e))
      case lam @ Lam(p, ds, b) => Lam(p, ds.map(_.subs(v, e)), if (p == v) b else b.subs(v, e))
      case Call(d, as) => Call(d, as.map(_.subs(v, e)))
      case Let(p, e0, b) => Let(p, e0.subs(v, e), if (v == p) b else b.subs(v, e))
      case Case(scrut, arms) => Case(scrut.subs(v, e), arms.map { case (con,vals,body) =>
        (con, vals, if (vals.contains(v)) body else body.subs(v, e))
      })
      case CtorField(scrut, ctor, arity, idx) => CtorField(scrut.subs(v, e), ctor, arity, idx)
    }
    def map(f: Expr => Expr): Expr = f(this match {
      case _: Inline | _: Vari => this
      case App(lhs, rhs) => App(lhs.map(f), rhs.map(f))
      case lam @ Lam(p, ds, b) => Lam(p, ds.map(_.map(f)), b.map(f))
      case Call(d, as) => Call(d, as.map(_.map(f)))
      case Let(p, e0, b) => Let(p, e0.map(f), b.map(f))
      case Case(scrut, arms) => Case(scrut.map(f), arms.map { case (con,vals,body) =>
        (con, vals, body.map(f))
      })
      case CtorField(scrut, ctor, arity, idx) => CtorField(scrut.map(f), ctor, arity, idx)
    })
    lazy val size: Int = this match {
      case c: Inline => 1
      case w: Vari => 1
      case App(lhs, rhs) => lhs.size + rhs.size
      case Lam(p, ds, b) => b.size + ds.foldLeft(0)(_ + _.body.size + 1) + 1
      case Call(d, as) => as.foldLeft(0)(_ + _.size) + 1
      case Let(p, e, b) => e.size + b.size
      case Case(scrut, arms) => scrut.size + arms.foldLeft(0)(_ + _._3.size + 1)
      case CtorField(scrut, ctor, arity, idx) =>
        scrut.size // Note: could become a case, which is bigger... may also reduce to a variable, which is smaller!
    }
    
    def simplify: Expr = simplifyRec(Map.empty)
    def simplifyRec(implicit caseCtx: Map[(Expr,String),List[Vari]]): Expr = this match {
      case _: Inline | _: Vari => this
      case App(lhs, rhs) => App(lhs.simplifyRec, rhs.simplifyRec)
      case lam @ Lam(p, ds, b) => Lam(p, ds.map(_.simplifyRec), b.simplifyRec.cse)
      case Call(d, as) => Call(d, as.map(_.simplifyRec))
      case Let(p, e0, b) => Let(p, e0.simplifyRec, b.simplifyRec)
      case Case(scrut, arms) => Case(scrut.simplifyRec, arms.map { case (con,vals,body) =>
        (con, vals, body.simplifyRec(caseCtx + ((scrut -> con) -> vals))) })
      case CtorField(scrut, ctor, arity, idx) => caseCtx.get(scrut, ctor) match {
        case Some(vals) => vals(idx)
        case None => CtorField(scrut.simplifyRec, ctor, arity, idx)
      }
    }
    def cse: Expr = {
      //println(s"Shared in ${stringify}:\n\t${shareableExprs.filter(_._2 >= 2).map(kv=>kv._1.stringify->kv._2)}")
      val shared = shareableExprs.filter(_._2 >= 2)
      if (shared.isEmpty) this else {
        var toMap = shared.toList.sortBy(-_._1.size)
        val mapping = mutable.Map.empty[Expr,Vari]
        while (toMap.nonEmpty) {
          val (e,cnt) = toMap.head
          toMap = toMap.tail // TODO remove e's subexpression counts from the tail
          val v = Vari(mkIdent("sh"))
          mapping += (e -> v)
        }
        assert(mapping.nonEmpty)
        //println(s"MAPPING:\n\t${mapping.map(kv=>kv._1.stringify->kv._2)}")
        /*
        // doesn't work!
        mapping.foldRight(
          map(e => mapping.getOrElse(e, e))
        ){ case ((e,v),acc) => Let(v,e,acc) }
        */
        mapping.toList.sortBy(_._1.size).foldRight(this){ case ((e,v),acc) =>
          //println(s"LET ${v.stringify} = ${e.stringify} IN ${acc.stringify}")
          Let(v,e,acc.map(e0 => if (e0 === e) v else e0)) }
        // ^ Note that we will let-bind small expressions last, so if they ended up "looking" shared only because they
        //   were part of a big expression that was the one being shared, the let binding will be elided...
        //   All in all, this process has horrendous time complexity, but at least it works well for now.
      }
    }
    
    //override def toString = stringify
  }
  case class Inline(str: String) extends Expr
  case class Vari(id: Ident) extends Expr
  case class App(lhs: Expr, rhs: Expr) extends Expr
  sealed abstract case class Lam(param: Vari, subDefs: List[Defn], body: Expr) extends Expr
  object Lam {
    def apply(param: Vari, subDefs: List[Defn], body: Expr): Lam = {
      new Lam(param, subDefs.filterNot(_.toBeInlined), body){}
      /*
      // Can convert no-params defs to let bindings, but it's really not buying us much;
      //   not worth the trouble + perf hit of implementing an `isRecursive` field
      val (letBindings,subDefs2) = subDefs.filterNot(_.toBeInlined).partition(d => d.params.isEmpty && !d.isRecursive)
      new Lam(param, subDefs2, letBindings.foldRight(body){ case (d,acc) =>
        val v = Vari(d.id)
        Let(v, d.body, acc.map { 
          case Call(Lazy(`d`),args) => assert(args.isEmpty); v
          case Call(c,args) if c.value === d => ???
          case e @ Call(c,_) => println(s"${c.value} $d ${c.value === d}"); e
          case e => e
        })
      }){}
      */
    }
  }
  sealed abstract case class Call(d: Lazy[Defn], args: List[Expr]) extends Expr {
    override def toString = s"${d.value.id}@(${args.mkString(", ")})"
  }
  object Call {
    def apply(d: Lazy[Defn], args: List[Expr]): Expr =
      if (!d.isComputing && d.value.toBeInlined) d.value.inline(args) else 
        new Call(d, args) {}
  }
  sealed abstract case class Let(v: Vari, e: Expr, body: Expr) extends Expr
  object Let {
    def apply(v: Vari, e: Expr, body: Expr): Expr = body.occurrences(v) match {
      case 0 => body
      case 1 =>
        body.subs(v, e)
      case _ =>
        if (e.isTrivial) body.subs(v, e) else new Let(v, e, body){}
    }
  }
  case class Case(scrut: Expr, arms: List[(String,List[Vari],Expr)]) extends Expr
  case class CtorField(scrut: Expr, ctor: String, arity: Int, idx: Int) extends Expr
  
}
