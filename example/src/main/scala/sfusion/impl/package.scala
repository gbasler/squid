package sfusion
package impl

import squid.quasi.{phase, embed, dbg_embed}
import squid.utils._

import scala.annotation.StaticAnnotation
import scala.collection.mutable

//class inline extends StaticAnnotation //  shadow Scala inlining to debug more easily

/**
  * Created by lptk on 07/02/17.
  * 
  * Laws that the implementations should follow:
  *   - if a producer returns `false`, it is guaranteed to produce at least another value
  *   - the values returned successively by a producer should only be a sequence of 0 to n `true` and then continually `false`
  *   - an empty producer (returned false before) should not call the consuming function at all, if asked to produce again
  *   - a producer should call the consumer at most once and then only as long as it last returned `true`
  *   - if a producer did not call its consumer, it should return `true`
  *   - if a producer's consumer last returned `true` but is not called again, the producer should return `true`
  * 
  * More a convention than a law:
  *   - a producer that has produced its last element should return true
  *   
  */

//package object impl {  // <- cannot be annotated!

//@dbg_embed
@embed
object `package` {
  
  // first bool for 'give me more'; last bool for 'terminated'
  type Producer[A] = (A => Bool) => Bool
  
  @inline @phase('Imperative)
  def single[A](v: A): Producer[A] = {
    k => {
      k(v)
      true
    }
  }
  
  @inline @phase('Imperative)
  def iterate[A](start: A)(next: A => A): Producer[A] = {
    var cur = start
    k => {
      //while (if (k(cur)) {cur = next(cur); true} else false) {}
      // Equivalent:
      while (k(cur) && {cur = next(cur); true}) {}
      false
    }
  }
  
  @inline @phase('Imperative)
  def fromIndexed[A](arr: IndexedSeq[A]): Producer[A] = {
    var i = 0
    val len = arr.length
    k => {
      while (i < len && {val a = arr(i); i += 1; k(a)}) {}
      i == len
    }
  }
  
  @inline @phase('Imperative)
  def unfold[A,B](start: B)(f: B => Option[(A,B)]): Producer[A] = {
    var b = start
    var isEmpty = false
    k => {
      while ({
        f(b) map { ab =>
          b = ab._2
          k(ab._1)
        } getOrElse {
          isEmpty = true
          false
        }
      }) {}
      isEmpty
    }
  }
  
  @inline @phase('Imperative)
  def continually[A](v: A): Producer[A] = {
    k => while(k(v)){}; false
  }
  
  /*
  @inline @phase('Imperative)
  def concat[A](lhs: Producer[A], rhs: Producer[A]): Producer[A] = {
    var curIsLhs = true
    var nextFromRhs = Option.empty[A]
    k => {
      if (curIsLhs) {
        var cont = true
        val lhsEnded = lhs { l => cont = k(l); cont }
        if (lhsEnded) {
          curIsLhs = false
          rhs{e => if (cont) cont = k(e) else nextFromRhs = Some(e); cont}
        } else false
      }
      else if (nextFromRhs.isEmpty) rhs(k) else {
        nextFromRhs = None
        k(nextFromRhs.get)
      }
    }
  }
  */
  // ^ Version above *may* be more efficient (especially in shallow mode), but uses `k` several times (which generates lots of code!)
  // TODO quantify the performance difference...
  @inline @phase('Imperative)
  def concat[A](lhs: Producer[A], rhs: Producer[A]): Producer[A] = {
    var curIsLhs = true
    k => {
      var cont = true
      var finished = false
      while (cont && !finished) {
        var next: Option[A] = None
        if (curIsLhs) {
          if (lhs { l => next = Some(l); false }) curIsLhs = false
        }
        if (next.isEmpty) {
          if (rhs { r => next = Some(r); false }) finished = true
        }
        next.fold(finished = true)(x => cont = k(x))
      }
      finished
    }
  }
  
  
  @inline @phase('Imperative)
  def foreach[A](s: Producer[A])(f: A => Unit): Bool = {
    s { a => f(a); true }
  }
  
  @inline @phase('Imperative)
  def fold[A,B](s: Producer[A])(z: B)(f: (B,A) => B): B = {
    var cur = z
    s { a => cur = f(cur,a); true }
    cur
  }
  
  @inline @phase('Imperative)
  def map[A,B](s: Producer[A])(f: A => B): Producer[B] = {
    k => s { a => k(f(a)) }
  }
  
  @inline @phase('Imperative)
  def take[A](s: Producer[A])(n: Int): Producer[A] = { // Note: could use takeWhile
    var taken = 0
    k => { s { a => taken < n && { taken += 1; k(a) && (taken < n) } } || taken == n }
  }
  
  @inline @phase('Imperative)
  def takeWhile[A](s: Producer[A])(pred: A => Bool): Producer[A] = {
    var stop = false
    k => { s { a => if (pred(a)) { k(a) } else {stop = true; false} } || stop }
  }
  
  @inline @phase('Imperative)
  def flatMap[A,B](s: Producer[A])(f: A => Producer[B]): Producer[B] = {
    var cur_s: Producer[B] = null
    k => {
      var completed = false
      var continue = false
      
      while({
        if (cur_s == null) s { a => cur_s = f(a); false }
        if (cur_s == null) completed = true
        else {
          if (cur_s { b => continue = k(b); continue }) cur_s = null
        }
        !completed && continue
      }){}
      
      completed
    }
  }
  
  @inline @phase('Imperative)
  def filter[A](s: Producer[A])(p: A => Bool): Producer[A] = {
    k => s { a => !p(a) || k(a) }
  }
  
  @inline @phase('Imperative)
  def zip[A,B](as: Producer[A], bs: Producer[B]): Producer[(A,B)] = {
    
    k => {
      var b_finished = false
      as { a =>
        var cont = true
        b_finished = bs { b =>
          cont = k((a,b))
          false
        }
        cont && !b_finished
      } || b_finished
    }
    
  }
  
  @inline @phase('Imperative)
  def all[A](xs: Producer[A])(pred: A => Bool): Bool = {
    //takeWhile(xs)(pred)(_ => true)
    xs { a => pred(a) }
  }
  
  @inline @phase('Imperative)
  def toBuffer[A](xs: Producer[A]): mutable.Buffer[A] = {
    val res = mutable.ArrayBuffer[A]()
    xs { a => res += a; true }
    res
  }
  
  
  
}

