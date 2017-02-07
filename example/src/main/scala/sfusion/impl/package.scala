package sfusion
package impl

import squid.quasi.{phase, embed, dbg_embed}
import squid.utils._

import scala.annotation.StaticAnnotation
import scala.collection.mutable

class inline extends StaticAnnotation

/**
  * Created by lptk on 07/02/17.
  */

//package object impl {  // <- cannot be annotated!

//@dbg_embed
@embed
object `package` {
  
  // first bool for 'give me more'; last bool for 'terminated'
  type Producer[A] = (A => Bool) => Bool
  
  @inline @inline @phase('Imperative)
  def fromIndexed[A](arr: IndexedSeq[A]): Producer[A] = {
    var i = 0
    val len = arr.length
    k => {
      //while (i < len && k{val a = arr(i); i += 1; a}) {} // FIXME doesn't seem to desugar right; array access still happens
      //while (if (i < len) {val a = arr(i); i += 1; k(a)} else false) {}
      while (and(i < len, {val a = arr(i); i += 1; k(a)})) {}
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
  def take[A](s: Producer[A])(n: Int): Producer[A] = { // TODO use takeWhile
    var taken = 0
    k => { s { a => if (taken < n) { taken += 1; k(a) && (taken < n) } else false } || taken == n }
  }
  
  @inline @phase('Imperative)
  def takeWhile[A](s: Producer[A])(pred: A => Bool): Producer[A] = {
    k => { s { a => if (pred(a)) { k(a) } else false } }
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
    k => s { a => if (p(a)) k(a) else true }
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
  
  
  @inline 
  def and(b0: Bool, b1: => Bool) = b0 && b1
  
}

