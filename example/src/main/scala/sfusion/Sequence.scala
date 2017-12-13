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

package sfusion

import squid.ir.SquidObject
import squid.quasi.overloadEncoding
import squid.utils._

import scala.collection.mutable
import squid.quasi.{phase, embed, dbg_embed}

import scala.collection.LinearSeq

/*
  * Q: are the underlying producers supposed to be referentially-transparent? If one use gives size `n`,
  *    will the next use give the same size? (could be used to update size info)
  * 
  * Note: for the library to behave well, `under()` should be "pseudo-referentially transparent" in the sense that all
  * calls to `under()` and subsequent uses of the result (which may close over state) should behave independently of 
  * when they happen.
  * 
  */
//@dbg_embed
@embed
final class Sequence[+A](val under: () => impl.Producer[A], val size: SizeInfo) {
  
  @inline @phase('Sugar)
  def bounded = size.isLeft || size == Right(true)
  
  @inline @phase('Impl)
  def +: [B >: A] (e: B): Sequence[B] = new Sequence(() => impl.concat(impl.single(e), under()), addToSize(size,1)) // TODO impl and use prepend/append
  
  @inline @phase('Impl)
  def ++ [B >: A] (that: Sequence[B]): Sequence[B] = new Sequence(() => impl.concat(under(), that.under()), addSizes(size,that.size))
  
  //@inline @phase('Impl)
  //def ++ (xs: Sequence[A]): Sequence[A] = new Sequence(() => impl.map(under())(f), plus(size))
  
  
  @inline @phase('Impl)
  def count: Int = impl.fold(under())(0)((acc,_) => acc+1)
  
  @inline @phase('Impl)
  def map[B](f: A => B): Sequence[B] = new Sequence(() => impl.map(under())(f), size)
  
  @inline @phase('Impl)
  def flatMap[B](f: A => Sequence[B]): Sequence[B] = new Sequence(() => impl.flatMap(under())(f andThen (_.under())), Right(true))
  
  @inline @phase('Impl)
  def foreach(f: A => Unit): Unit = impl.foreach(under())(f)
  
  def buffer = {
    val buf = impl.toBuffer(under())
    new Sequence(() => impl.fromIndexed(buf), Left(buf.size))
  }
  
  @inline @phase('Impl)
  def fold[B](z: B)(f: (B,A) => B): B = impl.fold(under())(z)(f)
  
  @inline @phase('Impl)
  def filter(pred: A => Bool): Sequence[A] = new Sequence(() => impl.filter(under())(pred), size) // FIXME size
  
  @inline @phase('Impl)
  def takeWhile(pred: A => Bool): Sequence[A] = new Sequence(() => impl.takeWhile(under())(pred), size) // FIXME size
  
  @inline @phase('Impl)
  def takeUntil[B >: A](xs: B*): Sequence[A] = {
    val xSet = xs.toSet
    takeWhile(!xSet(_))
  }
  
  @inline @phase('Impl)
  def dropWhile(pred: A => Bool): Sequence[A] = new Sequence(() => impl.dropWhile(under())(pred), size) // FIXME size
  
  @inline @phase('Impl)
  def dropElems[B >: A](xs: B*): Sequence[A] = {
    val xSet = xs.toSet
    dropWhile(xSet)
  }
  
  @inline @phase('Impl)
  def forall(pred: A => Bool): Bool = impl.all(under())(pred)
  
  
  @inline @phase('Impl)
  def zip[B](that: Sequence[B]): Sequence[(A,B)] = new Sequence(() => impl.zip(under(),that.under()), minSize(size,that.size))
  
  @inline @phase('Impl)
  def take(num: Int): Sequence[A] = new Sequence(() => impl.take(under())(num), minSize(size,Left(num)))
  
  @inline @phase('Impl)
  def drop(num: Int): Sequence[A] = new Sequence(() => impl.drop(under())(num), minSize(size,Left(num)))
  
  @overloadEncoding
  @phase('Impl)
  // TODO compare perf with old impl (cf commit b18e142b79baee6c48ed09425cc5b062cf036c82)
  def show(maxPrinted: Int = 10): String = s"Sequence(${
    val sep = ","
    var truncated = true
    val u = impl.onFinish(under())(() => truncated = false)
    val withSep = impl.mapHeadTail(u)(a => s"$a")(a => s",$a")
    val withTrunc = impl.take(withSep)(maxPrinted)
    val flat = impl.fold(withTrunc)("")(_ + _)
    if (truncated) flat+",..." else flat
  })"
  
  @phase('Sugar)
  def showAll: String = show(Int.MaxValue)
  
  @phase('Sugar)
  //override def toString: String = show() // FIXME or at least warn!! (cf. lack of `()`) -- used to work because we were not reconstructing lowered methodApp types
  override def toString(): String = show()
  
  @phase('Impl)
  override def hashCode = size.hashCode // TODO use `scala.util.hashing.MurmurHash3.seqHash(seq)`
  
  @phase('Impl)
  override def equals(any: Any) = {
    any.isInstanceOf[Sequence[Any]] && {
      val that = any.asInstanceOf[Sequence[Any]]
      (that.size == size || size == Right(true) || that.size == Right(true)) && (that.under == under || size != Right(false) && {
        impl.all(impl.zip(under(),that.under()))(a => a._1 == a._2)
      })
    }
  }
}
object Sequence extends SquidObject {
  
  @phase('Sugar)
  def apply[A](xs: A*): Sequence[A] = Sequence.fromIndexed(xs.toIndexedSeq)
  
  @phase('Sugar)
  def iterate[A](start: A)(next: A => A): Sequence[A] = new Sequence(() => impl.iterate(start)(next), Right(false))
  
  // TODO use a by-name param (currently not well supported by @embed)
  @phase('Sugar)
  def continually[A](computation: () => A): Sequence[A] = new Sequence(() => impl.continually(computation), Right(false))
  
  @phase('Sugar)
  def fromIndexed[A](is: IndexedSeq[A]): Sequence[A] = new Sequence(() => impl.fromIndexed(is), Left(is.size))
  
  @phase('Sugar)
  // Note: could pattern-match and use more specialized constructs when possible, eg for `List`s
  def fromIterable[A](ite: Iterable[A]): Sequence[A] = new Sequence(() => impl.fromIterator(ite.iterator), Right(false))
  
  @phase('Sugar)
  def fromStream[A](xs: Stream[A]): Sequence[A] =
    if (xs.hasDefiniteSize) new Sequence(() => impl.fromIterator(xs.iterator), Left(xs.size)) // Note: this is inefficient... probably not even needed/wanted
    else fromIterable(xs)
  
  @phase('Sugar)
  def fromList[A](xs: List[A]): Sequence[A] = new Sequence(() => impl.fromIterator(xs.iterator), Left(xs.size)) // could factor with above...
  
}

