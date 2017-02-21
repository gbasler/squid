package sfusion

import squid.utils._

import scala.annotation.unchecked.uncheckedVariance
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by lptk on 18/02/17.
  */
abstract class Producer[+A](underFun: => impl.Producer[A]) { prodSelf =>
  def under = underFun
  
  type Mapped[+B] <: Producer[B]
  def map[B](f: A => B): Mapped[B]
  def flatMap[B](f: A => Producer[B]): Producer[B] = new ProducerImpl(impl.flatMap(under)(f andThen (_.under)))
  
  type Filtered[+B] <: Producer[B]
  def withFilter(pred: A => Bool) = filter(pred)
  def filter(pred: A => Bool): Filtered[A]
  
  type Sliced[+B] <: Producer[B]
  def slice(from: Int, to: Int): Sliced[A]
  
  type SoftSliced[+B] <: Producer[B]
  def take(num: Int): SoftSliced[A] // TODO more precise type (bounded)
  def drop(num: Int): SoftSliced[A]
  def dropWhile(pred: A => Bool): Filtered[A]
  def takeWhile(pred: A => Bool): Filtered[A]
  
  /** Not public as it is unsafe to aggregate a potentially infinite Producer.
    * Use a size-limiting method such as `take` first, or make your finiteness assumption explicit with `assumeBounded`. */
  protected def run[R](k: Consumer[A] => Aggr[R]) = {
    val aggr = k(new Consumer[A]{
      private[this] var cur = null.asInstanceOf[A]  // TODO better error
      private[Producer] def iter: A => Unit = a => cur = a
      private[Producer] def get: A = cur
    })
    impl.foreach(under)(aggr.iter)
    aggr.get
  }
  
  def repeat[R](k: Consumer[A] => Aggr[R]): Producer[R] = ???
  
  /** TODO zip,take,drop,etc. */
  trait Consumer[+R] {
    // TODO move these details into invariant ProducerImpl
    
    // Should not need `uncheckedVariance`, if Scala allowed:
    //private[Producer.this] def iter: A => Unit
    private[Producer] def iter: (A @uncheckedVariance) => Unit
    private[Producer] def get: R

    def fold[S](z:S)(s:(S,R)=>S): Aggr[S] = {
      var cur = z
      new Aggr(a => {iter(a); cur = s(cur,get)},cur)
    }
    def build[S,T](z:S)(s:(S,R)=>Unit)(mk: S => T): Aggr[T] = {
      var cur = z
      new Aggr(a => {iter(a); s(cur,get)},mk(cur))
    }

    def map[S](f: R => S): Aggr[S] = new Aggr(iter,f(get))
    def flatMap[S](f: (=> R) => Aggr[S]): Aggr[S] = {
      var cell = null.asInstanceOf[R]
      val newAggr = f(cell).asInstanceOf[Aggr[S]]
      new Aggr(a => {iter(a); cell = get; newAggr.iter(a)},newAggr.get)
    }

    def withFilter(pred: R => Bool) = filter(pred)

    def filter(pred: R => Bool): Aggr[Option[R]] = {
      var filtered = Option.empty[R]
      new Aggr(a => {
        iter(a)
        val g = get
        filtered = if (pred(g)) Some(g) else None
      }, filtered)
    }

    def count: Aggr[Int] = fold(0)((s, _) => s+1)
    def sum[B >: R](implicit num: Numeric[B]): Aggr[B] = {
      val B = implicitly[Numeric[B]]
      fold(B.zero)(B.plus)
    }
    def toList: Aggr[List[R]] = build(ListBuffer.empty[R])(_ += _)(_.result)

    def toBufferOf[S>:R]: Aggr[mutable.ArrayBuffer[S]] = build(mutable.ArrayBuffer.empty[S])(_ += _)(identity)
    // ^ does not work well with type inference in for-comprehensions:
    def toBuffer: Aggr[mutable.ArrayBuffer[_<:R]] = toBufferOf[R]

  }
  class Aggr[+R](iterFun: A => Unit, getFun: => R) extends Consumer[R] {
    private[Producer] def iter: (A @uncheckedVariance) => Unit = iterFun
    private[Producer] def get: R = getFun
  }
  
  def asSized = this |>? { case self: SizedProducer[A] with this.type => self }
  def size_? = asSized Then (_.size)
  def asBounded = this |>? { case self: BoundedProducer[A] with this.type => self }
  def sizeUpperBound_? = asBounded Then (_.sizeUpperBound)
  
  def assumeBounded: BoundedProducer[A] = new BoundedProducerImpl(under, Int.MaxValue)
  
}
private class ProducerImpl[+A](under: => impl.Producer[A]) extends Producer[A](under) { prodSelf =>
  import Producer._
  
  //type Mapped[B] >: Producer[B] with Sized with Indexed with Order[B]#Sorted with Equiv[B]#Distinct <: Producer[B]
  type Mapped[+B] = Producer[B]
  type Filtered[+B] = Producer[B]
  type Sliced[+B] = Producer[B]
  type SoftSliced[+B] = Producer[B]
  
  def map[B](f: A => B): Mapped[B] = new ProducerImpl(impl.map(under)(f))
  def filter(pred: A => Bool): Filtered[A] = new ProducerImpl(impl.filter(under)(pred))
  def slice(from: Int, to: Int): Sliced[A] = ???
  def take(num: Int): SoftSliced[A] = new ProducerImpl(impl.take(under)(num))
  def drop(num: Int): SoftSliced[A] = ??? //rebuild(impl.drop(under)(num), size_? Then (_ - num min 0), sizeUpperBound_? Then (_ - num min 0), index_? map (_ compose ((_:Int) - num)))
  def dropWhile(pred: A => Bool): Filtered[A] = new ProducerImpl(impl.dropWhile(under)(pred))
  def takeWhile(pred: A => Bool): Filtered[A] = new ProducerImpl(impl.takeWhile(under)(pred))
  
}
object Producer {
  
  val UnknownBound = Int.MaxValue
  //lazy val PayForIt = ???
  
  def fromIndexed[A](as: IndexedSeq[A]): SizedProducer[A] = new SizedProducerImpl[A](impl.fromIndexed(as)(), as.size)
  
}
import Producer._

abstract class BoundedProducer[+A](underFun: => impl.Producer[A], val sizeUpperBound: Int) extends Producer[A](underFun) {
  type Mapped[+B] <: BoundedProducer[B]
  type Filtered[+B] <: BoundedProducer[B]
  type Sliced[+B] <: SizedProducer[B]
  type SoftSliced[+B] <: BoundedProducer[B]
  
  // Making `run` public
  override def run[R](k: Consumer[A] => Aggr[R]) = super.run(k)
  
  def groupBy[K,R](by: A => K)(k: (K,Consumer[A]) => Aggr[R]): Map[K,R] = hashBy(by)(k)
  def hashBy[K,R](by: A => K)(k: (K,Consumer[A]) => Aggr[R]): Map[K,R] = ???
  def trieBy[K,R](by: A => K)(k: (K,Consumer[A]) => Aggr[R]): Map[K,R] = ???
  
}
private class BoundedProducerImpl[+A](underFun: => impl.Producer[A], sizeUpperBound: Int) extends BoundedProducer[A](underFun,sizeUpperBound) {
  
  type Mapped[+B] = BoundedProducer[B]
  type Filtered[+B] = BoundedProducer[B]
  type Sliced[+B] = SizedProducer[B]
  type SoftSliced[+B] = BoundedProducer[B]
  
  def map[B](f: A => B): Mapped[B] = new BoundedProducerImpl(impl.map(under)(f), sizeUpperBound)
  def filter(pred: A => Bool): Filtered[A] = new BoundedProducerImpl(impl.filter(under)(pred),sizeUpperBound)
  def slice(from: Int, to: Int): Sliced[A] = ???
  def take(num: Int): SoftSliced[A] = new BoundedProducerImpl(impl.take(under)(num),num)
  def drop(num: Int): SoftSliced[A] = new BoundedProducerImpl(impl.take(under)(num),sizeUpperBound - num max 0)
  def dropWhile(pred: A => Bool): Filtered[A] = new BoundedProducerImpl(impl.dropWhile(under)(pred),sizeUpperBound)
  def takeWhile(pred: A => Bool): Filtered[A] = new BoundedProducerImpl(impl.takeWhile(under)(pred),sizeUpperBound)
  
}

abstract class SizedProducer[+A](underFun: => impl.Producer[A], val size: Int) extends BoundedProducer[A](underFun,size) {
  type Mapped[+B] <: SizedProducer[B]
  type Filtered[+B] <: BoundedProducer[B]
  type Sliced[+B] <: SizedProducer[B]
  type SoftSliced[+B] <: SizedProducer[B]
}
private class SizedProducerImpl[+A](underFun: => impl.Producer[A], size: Int) extends SizedProducer[A](underFun,size) {
  
  type Mapped[+B] = SizedProducer[B]
  type Filtered[+B] = BoundedProducer[B]
  type Sliced[+B] = SizedProducer[B]
  type SoftSliced[+B] = SizedProducer[B]
  
  def map[B](f: A => B): SizedProducer[B] = new SizedProducerImpl(impl.map(under)(f), size)
  def filter(pred: A => Bool): Filtered[A] = new BoundedProducerImpl(impl.filter(under)(pred),sizeUpperBound)
  def slice(from: Int, to: Int): Sliced[A] = ???
  def take(num: Int): SoftSliced[A] = new SizedProducerImpl(impl.take(under)(num), size min num)
  def drop(num: Int): SoftSliced[A] = new SizedProducerImpl(impl.take(under)(num), size - num max 0)
  def dropWhile(pred: A => Bool): Filtered[A] = new BoundedProducerImpl(impl.dropWhile(under)(pred),size)
  def takeWhile(pred: A => Bool): Filtered[A] = new BoundedProducerImpl(impl.takeWhile(under)(pred),size)
  
}

object Test extends App {
  
  val s = fromIndexed(1 to 3).map(x => x oh_and print(s"[$x]"))
  val p = s.map(_+1)
  
  //println(Producer.fromIndexed(1 to 3).map(_+1).size)
  println(p.size)
  //println(p.index(1))
  
  def test[A](q: SizedProducer[A]): SizedProducer[A] = {
    //println(q.size, q.index)
    println(q.size)
    q.map(a => println(a) before a)
  }
  val q = test(p)
  //println(q.size, q.index)
  println(q.size)
  val q2 = test(q)
  //println(q2.size, q2.index)
  println(q2.size)
  
  
  //println(p.run(_.count))

  //val r = p.run(p => p.count flatMap (n => p.sum.filter(s => s == 0).map(s => ))

  //val r = p.run(p => p.count flatMap (n => p.sum.map(s => n->s)))
  //val r = p.run(p => p.count flatMap (n => p.sum.filter(s => s == 0).map(s => n->s)))
  //val r = p.run(p => p.count flatMap (n => p.sum.map(s => n->s).filter(s => s == 0)))

  ///*
  val r = p.run(p => for {
    a <- p
    n <- p.count
    //() = println(n)
    s <- p.sum
    //s2 <- p.map(_+1).sum
    ls1 <- p.toList
    ls2 <- p.map(_+n).toList

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


