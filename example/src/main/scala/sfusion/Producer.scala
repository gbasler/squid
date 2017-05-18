package sfusion

import squid.utils._

import scala.annotation.unchecked.uncheckedVariance
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by lptk on 18/02/17.
  * 
  * Example use case: a prompt with bounded history
  * {{{
  *   Producer.continually(readLine).map(_.stripMargins).takeUntil("exit").run(c => for {
  *     history <- c.takeRight(10).buffer  // uses circular buffer; TODO compile efficiently with takeRight
  *     () <- c.foreach { /* compute and print result, possibly using history */ }
  *   } yield ())
  * }}}
  * 
  * Note: we could also use `run` to do some dataflow programming -- here we'd really need a compiler passs to analyse
  * how many values need to be retained (as opposed to all of them or a fixed number specified by the user).
  * See also: http://tomasp.net/coeffects/
  * 
  */
abstract class Producer[+A](underFun: => impl.Producer[A]) { prodSelf =>
  def under = underFun
  
  type Mapped[+B] <: Producer[B]
  def map[B](f: A => B): Mapped[B]
  def flatMap[B](f: A => Producer[B]): Producer[B] = new ProducerImpl(impl.flatMap(under)(f andThen (_.under)))
  
  type Filtered[+B] <: Producer[B]
  def withFilter(pred: A => Bool) = filter(pred)
  def filter(pred: A => Bool): Filtered[A]
  def dropWhile(pred: A => Bool): Filtered[A]
  def takeWhile(pred: A => Bool): Filtered[A]
  
  type Sliced[+B] <: Producer[B]
  def slice(from: Int, to: Int): Sliced[A]
  
  type Taken[+B] <: BoundedProducer[B]
  def take(num: Int): Taken[A]
  
  type Dropped[+B] <: Producer[B]
  def drop(num: Int): Dropped[A]
  
  abstract class Consumer[+R] {
    
    def fold[S](z:S)(s:(S,R)=>S): Aggr[S]
    def build[S,T](z:S)(s:(S,R)=>Unit)(mk: S => T): Aggr[T]
    def map[S](f: R => S): Consumer[S]
    //def map[S](f: (=>R) => S): Consumer[S]  // does not seem to make any significant change
    def flatMap[S](f: (=> R) => Aggr[S]): Aggr[S]
    def withFilter(pred: R => Bool) = filter(pred)
    def filter(pred: R => Bool): Consumer[Option[R]]
    
    def count: Aggr[Int] = fold(0)((s, _) => s+1)
    def sum[B >: R](implicit num: Numeric[B]): Aggr[B] = {
      val B = implicitly[Numeric[B]]
      fold(B.zero)(B.plus)
    }
    def toList: Aggr[List[R]] = build(ListBuffer.empty[R])(_ += _)(_.result)

    def toBufferOf[S>:R]: Aggr[mutable.ArrayBuffer[S]] = build(mutable.ArrayBuffer.empty[S])(_ += _)(identity)
    // ^ does not work well with type inference in for-comprehensions, so we also offer the following:
    def toBuffer: Aggr[mutable.ArrayBuffer[_<:R]] = toBufferOf[R]
    
  }
  trait Aggr[+R] extends Consumer[R] {
    def map[S](f: R => S): Aggr[S]
    //def map[S](f: (=>R) => S): Aggr[S]
    def filter(pred: R => Bool): Aggr[Option[R]]
  }
  
  /** Not public as it is unsafe to aggregate a potentially infinite Producer.
    * Use a size-limiting method such as `take` first, or make your finiteness assumption explicit with `assumeBounded`. */
  protected def run[R](k: Consumer[A] => Aggr[R]): R
  
  /** Related to coflatmap/extend from comonads */
  def repeat[R](k: Consumer[A] => Aggr[R]): Producer[R] = ???
  
  def asSized = this |>? { case self: SizedProducer[A] with this.type => self }
  def size_? = asSized Then (_.size)
  def asBounded = this |>? { case self: BoundedProducer[A] with this.type => self }
  def sizeUpperBound_? = asBounded Then (_.sizeUpperBound)
  
  def assumeBounded: BoundedProducer[A] = new BoundedProducerImpl(under, Int.MaxValue)
  
}
private abstract class AbstractProducer[A](under: => impl.Producer[A]) extends Producer[A](under) { prodSelf =>
  
  protected def run[R](k: super.Consumer[A] => super.Aggr[R]): R = {
    val aggr = k(new Consumer[A]{
      private[this] var cur = null.asInstanceOf[A]  // TODO better error
      def iter: A => Unit = a => cur = a
      def get: A = cur
    })
    val ag = aggr.asInstanceOf[Aggr[R]] // could avoid this one by using `type Consumer[+R] <: ConsumerAPI[R]` in Producer
    impl.foreach(under)(ag.iter)
    ag.get
  }
  
  /** TODO zip,take,drop,etc. most importantly: collect */
  abstract class Consumer[+R] extends super.Consumer[R] {
    def iter: A => Unit
    def get: R
    
    def fold[S](z:S)(s:(S,R)=>S): Aggr[S] = {
      var cur = z
      new Aggr(a => {iter(a); cur = s(cur,get)},cur)
    }
    def build[S,T](z:S)(s:(S,R)=>Unit)(mk: S => T): Aggr[T] = {
      var cur = z
      new Aggr(a => {iter(a); s(cur,get)},mk(cur))
    }

    def map[S](f: R => S): Aggr[S] = new Aggr(iter,f(get))
    //def map[S](f: (=>R) => S): Aggr[S] = new Aggr(iter,f(get))
    def flatMap[S](f: (=> R) => AbstractProducer.super.Aggr[S]): Aggr[S] = {
      var cell = null.asInstanceOf[R]
      val newAggr = f(cell).asInstanceOf[Aggr[S]]
      new Aggr(a => {iter(a); cell = get; newAggr.iter(a)},newAggr.get)
    }

    def filter(pred: R => Bool): Aggr[Option[R]] = {
      var filtered = Option.empty[R]
      new Aggr(a => {
        iter(a)
        val g = get
        filtered = if (pred(g)) Some(g) else None
      }, filtered)
    }
    
  }
  class Aggr[+R](iterFun: A => Unit, getFun: => R) extends Consumer[R] with super.Aggr[R] {
    def iter: A => Unit = iterFun
    def get: R = getFun
  }
  
}
private class ProducerImpl[A](under: => impl.Producer[A]) extends AbstractProducer[A](under) { prodSelf =>
  import Producer._
  
  //type Mapped[B] >: Producer[B] with Sized with Indexed with Order[B]#Sorted with Equiv[B]#Distinct <: Producer[B]
  type Mapped[+B] = Producer[B]
  type Filtered[+B] = Producer[B]
  type Sliced[+B] = Producer[B]
  type Taken[+B] = BoundedProducer[B]
  type Dropped[+B] = Producer[B]
  
  def map[B](f: A => B): Mapped[B] = new ProducerImpl(impl.map(under)(f))
  def filter(pred: A => Bool): Filtered[A] = new ProducerImpl(impl.filter(under)(pred))
  def slice(from: Int, to: Int): Sliced[A] = ???
  def take(num: Int): Taken[A] = new BoundedProducerImpl(impl.take(under)(num), num)
  def drop(num: Int): Dropped[A] = new ProducerImpl(impl.drop(under)(num))
  def dropWhile(pred: A => Bool): Filtered[A] = new ProducerImpl(impl.dropWhile(under)(pred))
  def takeWhile(pred: A => Bool): Filtered[A] = new ProducerImpl(impl.takeWhile(under)(pred))
  
}
object Producer {
  
  val UnknownBound = Int.MaxValue
  //lazy val PayForIt = ???
  
  def fromIndexed[A](as: IndexedSeq[A]): SizedProducer[A] = new SizedProducerImpl[A](impl.fromIndexed(as), as.size)
  
}
import Producer._

trait BoundedProducer[+A] extends Producer[A] {
  val sizeUpperBound: Int
  
  type Mapped[+B] <: BoundedProducer[B]
  type Filtered[+B] <: BoundedProducer[B]
  type Sliced[+B] <: SizedProducer[B]
  type Taken[+B] <: BoundedProducer[B]
  type Dropped[+B] <: BoundedProducer[B]
  
  // Making `run` public because it is now safe:
  abstract override def run[R](k: Consumer[A] => Aggr[R]) = super.run(k)
  
  def groupBy[K,R](by: A => K)(k: (K,Consumer[A]) => Aggr[R]): Map[K,R] = hashBy(by)(k)
  def hashBy[K,R](by: A => K)(k: (K,Consumer[A]) => Aggr[R]): Map[K,R] = ???
  def trieBy[K,R](by: A => K)(k: (K,Consumer[A]) => Aggr[R]): Map[K,R] = ???
  
}
private class BoundedProducerImpl[A](underFun: => impl.Producer[A], val sizeUpperBound: Int) extends AbstractProducer[A](underFun) with BoundedProducer[A] {
  
  type Mapped[+B] = BoundedProducer[B]
  type Filtered[+B] = BoundedProducer[B]
  type Sliced[+B] = SizedProducer[B]
  type Taken[+B] = BoundedProducer[B]
  type Dropped[+B] = BoundedProducer[B]
  
  def map[B](f: A => B): Mapped[B] = new BoundedProducerImpl(impl.map(under)(f), sizeUpperBound)
  def filter(pred: A => Bool): Filtered[A] = new BoundedProducerImpl(impl.filter(under)(pred), sizeUpperBound)
  def slice(from: Int, to: Int): Sliced[A] = ???
  def take(num: Int): Taken[A] = new BoundedProducerImpl(impl.take(under)(num), num)
  def drop(num: Int): Dropped[A] = new BoundedProducerImpl(impl.drop(under)(num), sizeUpperBound - num max 0)
  def dropWhile(pred: A => Bool): Filtered[A] = new BoundedProducerImpl(impl.dropWhile(under)(pred), sizeUpperBound)
  def takeWhile(pred: A => Bool): Filtered[A] = new BoundedProducerImpl(impl.takeWhile(under)(pred), sizeUpperBound)
  
}

trait SizedProducer[+A] extends BoundedProducer[A] {
  val size: Int
  
  type Mapped[+B] <: SizedProducer[B]
  type Filtered[+B] <: BoundedProducer[B]
  type Sliced[+B] <: SizedProducer[B]
  type Taken[+B] <: SizedProducer[B]
  type Dropped[+B] <: SizedProducer[B]
}
private class SizedProducerImpl[A](underFun: => impl.Producer[A], val size: Int) extends AbstractProducer[A](underFun) with SizedProducer[A] {
  val sizeUpperBound = size
  
  type Mapped[+B] = SizedProducer[B]
  type Filtered[+B] = BoundedProducer[B]
  type Sliced[+B] = SizedProducer[B]
  type Taken[+B] = SizedProducer[B]
  type Dropped[+B] = SizedProducer[B]
  
  def map[B](f: A => B): SizedProducer[B] = new SizedProducerImpl(impl.map(under)(f), size)
  def filter(pred: A => Bool): Filtered[A] = new BoundedProducerImpl(impl.filter(under)(pred),sizeUpperBound)
  def slice(from: Int, to: Int): Sliced[A] = ???
  def take(num: Int): Taken[A] = new SizedProducerImpl(impl.take(under)(num), size min num)
  def drop(num: Int): Dropped[A] = new SizedProducerImpl(impl.drop(under)(num), size - num max 0)
  def dropWhile(pred: A => Bool): Filtered[A] = new BoundedProducerImpl(impl.dropWhile(under)(pred),size)
  def takeWhile(pred: A => Bool): Filtered[A] = new BoundedProducerImpl(impl.takeWhile(under)(pred),size)
  
}

object Test extends App {
  
  //val s = fromIndexed(1 to 3).map(x => x oh_and print(s"[$x]")).take(4)
  val s = fromIndexed(1 to 5).map(x => x alsoDo print(s"[$x]")).take(4).drop(1)
  val p = s.map(_+1)
  
  //println(Producer.fromIndexed(1 to 3).map(_+1).size)
  println(p.size)
  //println(p.index(1))
  
  def test[A](q: SizedProducer[A]): SizedProducer[A] = {
    //println(q.size, q.index)
    println(q.size)
    q.map(a => println(a) thenReturn a)
  }
  val q = test(p)
  //println(q.size, q.index)
  println(q.size)
  val q2 = test(q)
  //println(q2.size, q2.index)
  println(q2.size)
  
  // Prevented statically:  TODO test
  //println(q.run(_.map(_+1)))
  //println(fromIndexed(1 until 1).run(_.map(_+1)))
  //println(fromIndexed(1 until 1).map(_.toString).run(_.map(identity)))
  
  //println(p.run(_.count))

  //val r = p.run(p => p.count flatMap (n => p.sum.filter(s => s == 0).map(s => ))

  //val r = p.run(p => p.count flatMap (n => p.sum.map(s => n->s)))
  //val r = p.run(p => p.count flatMap (n => p.sum.filter(s => s == 0).map(s => n->s)))
  //val r = p.run(p => p.count flatMap (n => p.sum.map(s => n->s).filter(s => s == 0)))

  ///*
  val r = p.run(p => for {
    a <- p
    //() = println(p) // TODO B/E -- note: interestingly, this works if it's outside the `for`
    //() = println(a) // TODO B/E
    n <- p.count
    //() = println(n) // nope -- but should be made to work
    s <- p.sum
    //s2 <- p.map(_+1).sum
    ls1 <- p.toList
    ls2 <- p.map(_+n).toList  // note: uses Consumer.map -- showing it's a useful method, although it can currently be used in an unsafe way

    //b <- p.toBuffer
    b <- p.toBufferOf[Int]

    //b <- p.toBuffer[Int]

    //() = println(n)
    //() = println(s)
    //if s == n
    //m <- p.count
  } yield (a,n,s,ls1,ls2,b))
  //*/
  println(r)


}


