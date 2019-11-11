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

package graph4

import squid.utils._
import squid.utils.CollectionUtils.MapHelper
import squid.lib.MutVar

import scala.collection.mutable

abstract class ParameterPassingStrategy {
  val requiresParens: Bool
  def mkParams(fun: String, params: List[String]): String
  def mkArgs(fun: String, args: List[String]): String =
    if (args.isEmpty) fun else "(" + mkParams(fun, args) + ")"
}
object UnboxedTupleParameters extends ParameterPassingStrategy {
  val requiresParens: Bool = false
  def mkParams(fun: String, params: List[String]): String =
    if (params.isEmpty) fun else params.mkString(s"$fun(# ", ", ", " #)")
}
object CurriedParameters extends ParameterPassingStrategy {
  val requiresParens: Bool = true
  def mkParams(fun: String, params: List[String]): String =
    (fun :: params).mkString(" ")
}

// TODO never try to share field accesses themselves! — treat them as zero size?
// TODO Should find a way to float let bindings into case right-hand-sides when possible,
//      so we don't end up with CtorField expressions that can't be simplified.
abstract class HaskellAST(pp: ParameterPassingStrategy) {
  
  val dropUnusedLets: Bool
  val inlineOneShotLets: Bool
  val inlineTrivialLets: Bool
  val inlineCalls: Bool
  val commonSubexprElim: Bool
  val useOnlyIntLits: Bool
  
  // Note: does not check for non-unique names, which could introduce shadowing bugs
  //val commuteLets = true
  val commuteLets = false
  
  val simplifyCases = true
  val mergeLets = false
  
  val baseIndent = "  "
  val letIndent = baseIndent * 3 // Haskell's stupid indentation rules require lots of indentation after a let...
  
  type Ident
  def printIdent(id: Ident): String
  def mkIdent(nameHitn: String): Ident
  
  def mkCtorStr(ctor: String) =
    if (ctor.head.isLetter || ctor === "[]" || ctor.head === '(' || ctor === "_") ctor else s"($ctor)"
  
  /** Important: `id` is used to uniquely identify the definition. */
  class Defn(val ide: Ident, val params: List[Vari], _body: Lazy[Expr]) {
    
    def body = _body.value
    
    def toBeInlined: Bool = !isRecursive && (
      body.isTrivial ||
        //false
        body.size <= params.size + 1 // makes sure to inline calls to trivial 'forwarding' defs like `_0(a, b) = a + b`
    )
    
    /** Note: count recursive calls as free occurrences/free variables. */
    def occurrences = body.occurrences -- params.iterator.map(_.ide) // not: - ide 
    def freeVars: Set[Ident] = occurrences.keySet
    def shareableExprs = body.shareableExprs.filterKeys(k => !k.freeVars.exists(params.contains))
    
    def subs(v: Vari, e: Expr): Defn = if (params.contains(v)) this else new Defn(ide, params, Lazy(body.subs(v, e)))
    def map(f: Expr => Expr): Defn = new Defn(ide, params, Lazy(body.map(f)))
    
    def inline(args: List[Expr]) = {
      assert(params.size === args.size)
      //assert(!isRecursive)
      val res = (params,args).zipped.foldRight(body) { case ((p,a),acc) => Let(p,a,acc) }
      res
    }
    
    lazy val isRecursive: Bool = _body.isComputing || body.allChildren.exists {
      case Call(d,_) =>
        (d.isComputing // duh!
        || d.value === this)
      case _ => false }
    
    lazy val simplifiedBody = body.simplify.cse
    
    def simplifyRec(implicit caseCtx: Map[(Expr,String),List[Vari]], changed: MutVar[Bool]) = {
      val s = body.simplifyRec // TODO make simplifyRec eq-preserving in case of no changes
      if (s eq body) this else new Defn(ide, params, Lazy(s))
    }
    
    def stringify: Str = stringify(baseIndent)
    def stringify(indent: Str): Str = {
      s"${pp.mkParams(printIdent(ide), params.map(_.ide |> printIdent))} = ${simplifiedBody.stringify(indent, 0)}"
    }
    
    override def equals(that: Any) = that match { case d: Defn => ide === d.ide case _ => false }
    override def hashCode() = ide.hashCode
    
    override def toString = s"$ide(${params.mkString(", ")})"
  }
  
  private[this] val noOccs: Map[Ident, Int] = Map.empty[Ident, Int].withDefaultValue(0)
  private[this] val noSubExprs: Map[Expr, Int] = Map.empty[Expr, Int].withDefaultValue(0)
  
  private[this] def findLets(indent: Str): Expr => (List[String], String) = {
    case Let(v,e,b) => findLets(indent)(b) ||> (_1 = s"${v.stringify(indent, 1)} = ${e.stringify(indent, 1)}" :: _)
    case LetDefn(d,b) => findLets(indent)(b) ||> (_1 = s"${d.stringify(indent)}" :: _)
    case e => Nil -> e.stringify(indent, 1)
  }
  private[this] def stringifyLets(indent: Str, known: List[String], rest: Expr): String = {
    val (discovered, restStr) = findLets(indent + letIndent)(rest)
    val lets = known ++ discovered
    lets match {
      case Nil => restStr
      case single :: Nil =>
        //s"let $single in $restStr"
        s"\n${indent}let $single in\n${indent}$restStr"
      case many =>
        if (mergeLets)
          //s"let { ${many.mkString("; ")} } in $restStr"
          s"let${many.map(s"\n$indent"+_).mkString}\n${indent}in $restStr"
        else many.map(l => s"\n${indent}let $l in").mkString + s"\n$indent$restStr" 
    }
  }
  
  sealed abstract class Expr {
    
    def isTrivial: Bool = this match {
      case _: Inline | _: Vari | Call(_, Nil) => true
      case CtorField(s, _, _, _) => s.isTrivial
      case _ => false
    }
    
    private[this] def parens(str: Str, cnd: Bool = true) = if (cnd) s"($str)" else str
    
    final def stringify: Str = stringify(baseIndent, 0)
    
    /** outerPrec: 0 for top-level; 1 for lambda and let; 2 for case arms; 3 for app LHS; 4 for app RHS */
    def stringify(indent: Str, outerPrec: Int): Str = this match {
      case Vari(id) => printIdent(id)
      case Inline(str) => str
      case App(App(Inline(op), lhs), rhs) if op.head === '(' && op.last === ')' && op.tail.head =/= ',' =>
        parens(s"${lhs.stringify(indent, 3)} ${op.tail.init} ${rhs.stringify(indent, 3)}",
          outerPrec > 2)
      case App(lhs, rhs) =>
        parens(s"${lhs.stringify(indent, 3)} ${rhs.stringify(indent, 4)}",
          outerPrec > 3)
      case Lam(p, ds, b) =>
        parens(s"\\${p.ide |> printIdent} -> ${stringifyLets(indent, ds.map(_.stringify(indent)), b)}",
          outerPrec > 1)
      case Call(d, args) =>
        s"${pp.mkArgs(d.value.ide |> printIdent, args.map(_.stringify(indent, if (pp.requiresParens) 4 else 0)))}"
      case Let(v,e,b) =>
        parens(stringifyLets(indent, s"${v.stringify(indent, 1)} = ${e.stringify(indent + letIndent, 1)}" :: Nil, b),
          outerPrec > 1)
      case LetDefn(d, b) =>
        parens(stringifyLets(indent, s"${d.stringify(indent + letIndent)}" :: Nil, b),
          outerPrec > 1)
      case Case(scrut, arms) =>
        parens(s"case ${scrut.stringify(indent, 1)} of { ${arms.map { case (con, vars, rhs) =>
          mkCtorStr(con) + vars.map(_.ide |> printIdent).map{" "+_}.mkString + s" -> ${rhs.stringify(indent, 2)}"
        }.mkString("; ")} }",
          outerPrec > 1)
      case CtorField(scrut, ctor, arity, idx) =>
        /*
        parens(s"case ${scrut.stringify(indent, 1)} of ${mkCtorStr(ctor)} ${
          List.tabulate(arity)(i => if (i === idx) "arg" else "_").mkString(" ")
        } -> arg",
          outerPrec > 1)
        */
        // Alternative, slightly more lightweight:
        parens(s"let ${mkCtorStr(ctor)} ${
          List.tabulate(arity)(i => if (i === idx) "arg" else "_").mkString(" ")
        } = ${scrut.stringify(indent, 1)} in arg",
          outerPrec > 0) // > 1 also works but is pretty hard to read, as in `case let (:) _ arg = xs in arg of { ... }`
    }
    
    def freeVars: Set[Ident] = occurrences.keySet
    def hasFreeVar(v: Vari): Bool = occurrences.contains(v.ide)
    
    /** Important: not all occurrences of case-bound variables may be visible  */
    lazy val occurrences: Map[Ident, Int] = this match {
      case Inline(str) => noOccs
      case v: Vari => (noOccs mergeValue (v.ide -> 1))(_ + _)
      case App(lhs, rhs) => (lhs.occurrences mergeValues rhs.occurrences)(_ + _)
      case Lam(p, ds, b) => ds.foldLeft(b.occurrences){case (acc,e) => (acc mergeValues e.occurrences)(_ + _)} - p.ide
      case Call(d, as) =>
        as.foldLeft((noOccs mergeValue (d.value.ide -> 1))(_ + _)){case (acc,e) => (acc mergeValues e.occurrences)(_ + _)}
      case Let(v, e, b) => (e.occurrences mergeValues (b.occurrences - v.ide))(_ + _)
      case LetDefn(d, b) => (d.occurrences mergeValues b.occurrences)(_ + _) - d.ide
      case Case(scrut, arms) => arms.foldLeft(scrut.occurrences){ case (acc,(con,vals,e)) =>
        (acc mergeValues (e.occurrences -- vals.iterator.map(_.ide)))(_ + _)}
      case CtorField(scrut, ctor, arity, idx) => scrut.occurrences // Approximation... we may later resolve this expr to a variable!
    }
    private[this] def countThis(exprs: Map[Expr,Int]): Map[Expr,Int] = (exprs mergeValue (this -> 1))(_ + _)
    lazy val shareableExprs: Map[Expr, Int] = this match {
      case Inline(str) => noSubExprs
      case v: Vari => noSubExprs
      case App(lhs, rhs) => (lhs.shareableExprs mergeValues rhs.shareableExprs)(_ + _) |> countThis
      case Lam(p, ds, b) =>
        ds.foldLeft(b.shareableExprs){case (acc,e) => (acc mergeValues e.shareableExprs)(_ + _)}
          .filterKeys(k => !k.hasFreeVar(p)) |> countThis
      case Call(d, as) =>
        as.iterator.foldLeft(noSubExprs){case (acc,e) => (acc mergeValues e.shareableExprs)(_ + _)} |> countThis
      case Let(v, e, b) =>
        (e.shareableExprs mergeValues (b.shareableExprs - v))(_ + _) // Note: let's not try to share lets...
      case LetDefn(d, b) => ??? // TODO similar to Lam (make sure no refs to params)
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
      case LetDefn(d, b) => LetDefn(d.subs(v, e), b.subs(v, e))
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
      case LetDefn(d, b) => LetDefn(d.map(f), b.map(f))
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
      case LetDefn(d, b) => d.body.size + b.size + 1//Int.MaxValue // FIXME?
      case Case(scrut, arms) => scrut.size + arms.foldLeft(0)(_ + _._3.size + 1)
      case CtorField(scrut, ctor, arity, idx) =>
        scrut.size // Note: could become a case, which is bigger... may also reduce to a variable, which is smaller!
    }
    
    import Iterator.{ single => Ite }
    def allChildren: Iterator[Expr] = Ite(this) ++ children.flatMap(_.allChildren)
    def children: Iterator[Expr] = this match {
      case _: Inline | _: Vari => Iterator.empty
      case App(lhs, rhs) => Ite(lhs) ++ Ite(rhs)
      case lam @ Lam(p, ds, b) => ds.iterator.map(_.body) ++ Ite(b)
      case Call(d, as) => as.iterator
      case Let(p, e0, b) => Ite(e0) ++ Ite(b)
      case LetDefn(d, b) => d.body.children ++ b.children
      case Case(scrut, arms) =>Ite(scrut) ++ arms.iterator.map(_._3)
      case CtorField(scrut, ctor, arity, idx) =>Ite(scrut)
    }
    
    def simplify: Expr = {
      val changed = MutVar(true)
      var res = this
      while (changed.!) { changed := false; res = res.simplifyRec(Map.empty, changed) }
      res
    }
    def simplifyRec(implicit caseCtx: Map[(Expr,String),List[Vari]], changed: MutVar[Bool]): Expr = this match {
      case _: Inline | _: Vari => this
      case App(lhs, rhs) => App(lhs.simplifyRec, rhs.simplifyRec)
      case lam @ Lam(p, ds, b) => Lam(p, ds.map(_.simplifyRec), b.simplifyRec.cse)
      case Call(d, as) => Call(d, as.map(_.simplifyRec))
      case Let(p, e0, b) => Let(p, e0.simplifyRec, b.simplifyRec)
      case LetDefn(d, b) => LetDefn(d.simplifyRec, b.simplifyRec)
      case Case(scrut, arms) => Case(scrut.simplifyRec, arms.map { case (con,vals,body) =>
        val newCtx = if (simplifyCases) caseCtx + ((scrut -> con) -> vals) else caseCtx
        (con, vals, body.simplifyRec(newCtx, changed)) })
      case CtorField(scrut, ctor, arity, idx) => caseCtx.get(scrut, ctor) match {
        case Some(vals) =>
          changed := true
          vals(idx)
        case None => CtorField(scrut.simplifyRec, ctor, arity, idx)
      }
    }
    def cse: Expr = if (!commonSubexprElim) this else {
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
  case class Vari(ide: Ident) extends Expr
  sealed abstract case class App(lhs: Expr, rhs: Expr) extends Expr
  object App {
    def apply(lhs: Expr, rhs: Expr): Expr = lhs match {
      case Let(v,e,b) if commuteLets  => Let(v,e,App(b,rhs))
      case LetDefn(d,b) if commuteLets => LetDefn(d, App(b,rhs))
      case _ =>
        rhs match {
          case Let(v,e,b) if commuteLets => Let(v,e,App(lhs,b))
          case LetDefn(d,b) if commuteLets => LetDefn(d, App(lhs,b))
          case _ =>
            new App(lhs, rhs){}
        }
    }
  }
  sealed abstract case class Lam(param: Vari, subDefs: List[Defn], body: Expr) extends Expr
  object Lam {
    def apply(param: Vari, subDefs: List[Defn], body: Expr): Lam = {
      new Lam(param, subDefs.filterNot(_.toBeInlined), body){}
      /* // TODO
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
    override def toString = s"${d.value.ide}@(${args.mkString(", ")})"
  }
  object Call {
    def apply(d: Lazy[Defn], args: List[Expr]): Expr =
      if (inlineCalls && !d.isComputing && d.value.toBeInlined) d.value.inline(args) else 
        new Call(d, args) {}
  }
  sealed abstract case class Let(v: Vari, e: Expr, body: Expr) extends Expr
  object Let {
    def apply(v: Vari, e: Expr, body: Expr): Expr = body.occurrences(v.ide) match {
      case 0 if dropUnusedLets => body
      case 1 if inlineOneShotLets && !(e.freeVars contains v.ide) =>
        body.subs(v, e)
      case _ =>
        if (e.isTrivial && inlineTrivialLets) body.subs(v, e)
        else e match {
          case Let(v2, e2, body2) if commuteLets => Let(v2, e2, Let(v, body2, body))
          case LetDefn(d, body2) if commuteLets => LetDefn(d, Let(v, body2, body))
          case _ => new Let(v, e, body){}
        }
    }
    def multiple(defs: List[Binding], body: Expr): Expr = {
      // Require that binding idents be unique:
      //defs.groupBy(Binding.ident).valuesIterator.foreach(vs => require(vs.size === 1, vs))
      /*
      // in the meantime, ensure uniqueness with this:
      val used = mutable.Set.empty[Ident]
      val unique = defs.filter { case b => used.setAndIfUnset(Binding.ident(b), true, false) }
      */
      val unique = defs // assumed; it's tested in `reorder`
      unique.foldRight(body) {
        case (Left((v,e)), acc) => Let(v,e,acc)
        case (Right(dfn), acc) => LetDefn(dfn,acc)
      }
    }
    def reorder(defs: List[Binding], body: Expr, dbg: Bool = false): Expr = {
      
      // Require that binding idents be unique:
      //defs.groupBy(Binding.ident).valuesIterator.foreach(vs => require(vs.size === 1, vs))
      val used = mutable.Set.empty[Ident] // probably more efficient than groupBy
      
      val nodes = defs.map { d =>
        val ide = Binding.ident(d)
        require(!used(ide), ide)
        used += ide
        (ide, d)
      }.toMap
      
      // Topological sort of the let bindings (which are possibly recursive)
      
      val visited = mutable.Set.empty[Ident]
      var ordered: List[Ident] = Nil
      
      // TODO mark loop breakers in cyclic definitions so they are not inlined
      def go(xs: List[Ident]): Unit = xs match {
        case x :: xs =>
          if (nodes.isDefinedAt(x) && !visited(x)) {
            visited += x
            val next = (nodes(x) match {
              case Left((v,e)) => e.freeVars
              case Right(dfn) => dfn.freeVars
            }).iterator.filterNot(visited).toList
            go(next)
            ordered ::= x
          }
          go(xs)
        case Nil =>
      }
      go(body.freeVars.filter(nodes.isDefinedAt).toList)
      
      //assert(ordered.size === defs.size, (ordered.size, defs.size, ordered, defs))
      //multiple(ordered.reverse.map(nodes), body)
      assert(ordered.size <= defs.size, (ordered.size, defs.size))
      if (ordered.size =/= defs.size) {
        //System.err.println(s"!!! ${ordered.size} =/= ${defs.size} for\n\t${ordered.map(printIdent)}\n\t${defs.map(d => printIdent(Binding.ident(d)))}")
        val err = new AssertionError(
          s"!!! WARNING !!! ${ordered.size} =/= ${defs.size} for" + (
            if (dbg) s"\n\t${ordered.map(printIdent)}\n\t${defs.map(d => printIdent(Binding.ident(d)))}"
            else s"\n\t${ordered}\n\t${defs.map(d => Binding.ident(d))}"
          ))
        
        // TODO solve problem and put this back:
        //if (dbg) err.printStackTrace()
        //else throw err
        
        //err.printStackTrace()
        System.err.println(err.getMessage)
      }
      multiple(ordered.reverse.map(nodes) ::: defs.filterNot(d => ordered.contains(Binding.ident(d))), body)
    }
  }
  sealed abstract case class LetDefn(defn: Defn, body: Expr) extends Expr
  object LetDefn {
    def apply(defn: Defn, body: Expr): Expr = body.occurrences(defn.ide) match {
      case 0 if dropUnusedLets => body
      case 1 if inlineOneShotLets && !(defn.freeVars contains defn.ide) =>
        var found = false
        def inlineDefn(e: Expr): Expr = e match {
          case c: Inline => c
          case w: Vari => w
          case App(lhs, rhs) => App(inlineDefn(lhs), if (found) rhs else inlineDefn(rhs))
          case lam @ Lam(p, ds, b) => Lam(p, ds.map(_.map(inlineDefn)), inlineDefn(b))
          case Call(d, as) =>
            if (d.value === defn) {
              found = true
              Let.multiple((defn.params,as).zipped.map((p,a) => Left(p, a)), defn.body)
            }
            else Call(d, as.map(a => if (found) a else inlineDefn(a)))
          case Let(p, e0, b) => Let(p, inlineDefn(e0), if (found) b else inlineDefn(b))
          case LetDefn(d, b) => LetDefn(d.map(inlineDefn), if (found) b else inlineDefn(b))
          case Case(scrut, arms) => Case(inlineDefn(scrut), arms.map { case (con,vals,body) =>
            (con, vals, if (found) body else inlineDefn(body))
          })
          case CtorField(scrut, ctor, arity, idx) => CtorField(inlineDefn(scrut), ctor, arity, idx)
        }
        inlineDefn(body)
      case _ =>
        new LetDefn(defn, body){}
    }
  }
  case class Case(scrut: Expr, arms: List[(String,List[Vari],Expr)]) extends Expr
  case class CtorField(scrut: Expr, ctor: String, arity: Int, idx: Int) extends Expr
  
  type Binding = (Vari, Expr) \/ Defn
  object Binding {
    def ident(b: Binding): Ident = b match {
      case Left((v,_)) => v.ide
      case Right(dfn) => dfn.ide
    }
  }
  
}
