package sfusion

import squid.utils._

import scala.annotation.unchecked.uncheckedVariance
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by lptk on 18/02/17.
  */
class Producer[+A](protected val under: () => impl.Producer[A]) { prodSelf =>
  
  def map[B](f: A => B): Producer[B] = new Producer(() => impl.map(under())(f))
  def flatMap[B](f: A => Producer[B]): Producer[B] = new Producer(() => impl.flatMap(under())(f andThen (_.under())))
  
  def withFilter(pred: A => Bool) = filter(pred)
  def filter(pred: A => Bool): Producer[A] = new Producer(() => impl.filter(under())(pred))
  
  
  def run[R](k: Consumer[A] => Aggr[R]) = {
    val aggr = k(new Consumer[A]{
      private[this] var cur = null.asInstanceOf[A]  // TODO better error
      private[Producer] def iter: A => Unit = a => cur = a
      private[Producer] def get: A = cur
    }).asInstanceOf[Aggr[R]]
    impl.foreach(under())(aggr.iter)
    aggr.get
  }
  
  def repeat[R](k: Consumer[A] => Aggr[R]): Producer[R] = ???
  def groupBy[K,R](by: A => K)(k: (K,Consumer[A]) => Aggr[R]): Map[K,R] = hashBy(by)(k)
  def hashBy[K,R](by: A => K)(k: (K,Consumer[A]) => Aggr[R]): Map[K,R] = ???
  def trieBy[K,R](by: A => K)(k: (K,Consumer[A]) => Aggr[R]): Map[K,R] = ???
  
  /** TODO zip,take,drop,etc. */
  trait Consumer[+R] {
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
  
  //def asBounded = this |>? { case self: Bounded => self }
  def asSized = this |>? { case self: Sized[A] with this.type => self }
  def size_? = asSized Then (_.size)
  
}
object Producer {
  def fromIndexed[A](as: IndexedSeq[A]) = new Producer[A](() => impl.fromIndexed(as)()) with Sized[A] { def size = as.size }
}

// not terribly useful; not implemented to simplify the lib:
//trait Bounded { self: Producer[_] => }

trait Sized[+A] extends Producer[A] /*with Bounded*/ { self =>
  def size: Int
  override def map[B](f: A => B): Producer[B] with Sized[B] = new Producer(() => impl.map(under())(f)) with Sized[B] {
    def size: Int = self.size
  }
  // TODO:
  //override def repeat[R](k: Consumer[A] => Aggr[R]): Producer[R] with Bounded
}





object Test extends App {
  
  val s = Producer.fromIndexed(1 to 3).map(x => x oh_and print(s"[$x]"))
  val p = s.map(_+1)
  
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


