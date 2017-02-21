package sfusion

import squid.utils._

import scala.annotation.unchecked.uncheckedVariance
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/*
Brittle mess:
  `Producer[A] with SizedIndexed[A]` works all the time (it redefined Mapped/Sliced so they don't conflict)
  `Producer[A] with Sized with Indexed` works fine until it is mapped; then it breaks -- result cannot be interpreted as `Producer[A] with Sized with Indexed`!

Note: works well in Dotty:

  Welcome to Scala.next (pre-alpha, git-hash: f467be6)  (Java HotSpot(TM) 64-Bit Server VM, Java 1.8.0_65).
  Type in expressions to have them evaluated.
  Type :help for more information.
  scala> trait Prod[+A] { type Mapped[+A] <: Prod[A]; def map[B]: Mapped[B] = null.asInstanceOf[Mapped[B]] }
  defined trait Prod
  scala> trait Sorted[+A] extends Prod[A] { type Mapped[+A] <: Sorted[A]; def sort = 'ok }
  defined trait Sorted
  scala> trait Sized[+A] extends Prod[A] { type Mapped[+A] <: Sized[A]; def size = 0 }
  defined trait Sized
  scala> def foo(x:Sorted[Int] & Sized[Int]) = x.map[Int].map[Int].map[String].sort
  def foo(x: Sorted[Int] & Sized[Int]): Symbol
  scala> def foo(x:Sorted[Int] & Sized[Int]) = x.map[Int].map[Int].map[String].size
  def foo(x: Sorted[Int] & Sized[Int]): Int

But not scalac:

  Welcome to the Ammonite Repl 0.5.5
  (Scala 2.11.7 Java 1.8.0_65)
  > trait Prod[+A] { type Mapped[+A] <: Prod[A]; def map[B]: Mapped[B] = null.asInstanceOf[Mapped[B]] }
  defined trait Prod
  > trait Sorted[+A] extends Prod[A] { type Mapped[+A] <: Sorted[A]; def sort = 'ok }
  defined trait Sorted
  > trait Sized[+A] extends Prod[A] { type Mapped[+A] <: Sized[A]; def size = 0 }
  defined trait Sized
  > def foo(x:Sorted[Int] with Sized[Int]) = x.map[Int].map[Int].map[String].size
  defined function foo
  > def foo(x:Sorted[Int] with Sized[Int]) = x.map[Int].map[Int].map[String].sort
  Main.scala:57: value sort is not a member of x.Mapped[Int]#Mapped[Int]#Mapped[String]
  def foo(x:Sorted[Int] with Sized[Int]) = x.map[Int].map[Int].map[String].sort
                                                                           ^
  Compilation Failed
  > def foo(x:Sized[Int] with Sorted[Int]) = x.map[Int].map[Int].map[String].sort
  defined function foo
  > def foo(x:Sized[Int] with Sorted[Int]) = x.map[Int].map[Int].map[String].size
  Main.scala:57: value size is not a member of x.Mapped[Int]#Mapped[Int]#Mapped[String]
  def foo(x:Sized[Int] with Sorted[Int]) = x.map[Int].map[Int].map[String].size
                                                                           ^
  Compilation Failed
  
*/
/**
  * Created by lptk on 18/02/17.
  */
//class Producer[+A](under: => impl.Producer[A], val size_? : Option[Int], val sizeUpperBound_? : Option[Int], index_? : Option[Int => A]) { prodSelf =>
abstract class Producer[+A](under: => impl.Producer[A]) { prodSelf =>
  protected type TA = A @uncheckedVariance
  import Producer._
  assert(size_? forall (_ === sizeUpperBound_?.get))
  
  //type KeepingBoundSorted[B] >: Producer[B] with Bounded with Indexed <: Producer[B]
  //type KeepingSizeSorted[B] >: Producer[B] with Sized with Indexed <: Producer[B]
  //type KeepingSizeIndexed[B] >: Producer[B] with Sized with Indexed <: Producer[B]
  //type KeepingSizeSortedIndexed[B] >: Producer[B] with Sized with Indexed with Order#Sorted <: Producer[B]
  
  //type Mapped[B] >: Producer[B] with Sized with Indexed with Order[B]#Sorted with Equiv[B]#Distinct <: Producer[B]
  type Mapped[+B] <: Producer[B]
  type Filtered[+B] <: Producer[B]
  type Sliced[+B] <: Producer[B]
  
  //def map[B](f: A => B): Mapped[B] = build(impl.map(under)(f), size_?, sizeUpperBound_?, index_? map (_ andThen f), None, None).asInstanceOf[Mapped[B]]
  def map[B](f: A => B): Mapped[B] = build(impl.map(under)(f), size_?, sizeUpperBound_?, index_? map (_ andThen f), None, None)
  
  //def map[B](f: A => B): Mapped[B] = (new Producer(impl.map(under)(f))).asInstanceOf[Mapped[B]]
  def flatMap[B](f: A => Producer[B]): Producer[B] = ??? //rebuild(impl.flatMap(under)(f andThen (_.under)), None, None, None)

  def withFilter(pred: A => Bool) = filter(pred)
  def filter(pred: A => Bool): Filtered[A] = build(impl.filter(under)(pred), None, sizeUpperBound_?, None, None, None) // unnecessary: size_? orElse sizeUpperBound_?
  //def filter(pred: A => Bool): Producer[A] = keepBounded(impl.filter(under)(pred), identity)

  //def take(num: Int) = withBound(impl.take(underFun)(num), size_?)
  def take(num: Int): Sliced[A] = {
    //val newSize = size_? Then (_ min num)
    //rebuild(impl.take(underFun)(num), newSize, newSize orElse (sizeUpperBound_? Then (_ min num)))
    //rebuild(impl.take(under)(num), size_? Then (_ min num), sizeUpperBound_? Then (_ min num), index_?)
    ???
  }
  def drop(num: Int): Sliced[A] = ??? //rebuild(impl.drop(under)(num), size_? Then (_ - num min 0), sizeUpperBound_? Then (_ - num min 0), index_? map (_ compose ((_:Int) - num)))
  def dropWhile(pred: A => Bool): Sliced[A] = build(impl.dropWhile(under)(pred), None, sizeUpperBound_?, None, None, None)
  def takeWhile(pred: A => Bool): Sliced[A] = build(impl.takeWhile(under)(pred), None, sizeUpperBound_?, None, None, None)


  def run[R](k: Consumer[A] => Aggr[R]) = {
    val aggr = k(new Consumer[A]{
      private[this] var cur = null.asInstanceOf[A]  // TODO better error
      private[Producer] def iter: A => Unit = a => cur = a
      private[Producer] def get: A = cur
    })
    impl.foreach(under)(aggr.iter)
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

  //def size(implicit ev: this.type <:< Capa[Sized]) = size_?.get

  ///*
  def asBounded = this |>? { case self: Bounded with this.type => self }
  //def asSized = this |>? { case self: Sized[A] with this.type => self }
  def asSized = this |>? { case self: Sized with this.type => self }
  def asIndexed = this |>? { case self: Indexed with this.type => self }
  //*/
  def size_? = asSized Then (_.size)
  def sizeUpperBound_? = asBounded Then (_.sizeUpperBound)
  //protected def index_? : Option[Int => A] = None
  def index_? : Option[Int => A] = asIndexed Then (_.index)
  //val size_? : Option[Int], val sizeUpperBound_? : Option[Int]

}
object Producer {
  //trait Capa[+C]
  /*
  protected def rebuild[B](newUnder: => impl.Producer[B], newSize: Option[Int], newBound: Option[Int], newIndex: Option[Int => B]) =
    //new Producer(newUnder, newSize Unless (newBound === Some(0)) Then , newSize orElse newBound, newIndex)
    //new Producer(newUnder, newBound If (_ === Some(0)) Else newSize, newSize orElse newBound, newIndex)
    //new Producer(newUnder)
    ???
  */
  //def build[B,P<:Producer[B]](under: => impl.Producer[B], size: Option[Int], bound: Option[Int], index: Option[Int => B], distinct: Option[Equiv[B]], sorted: Option[Order[B]]): Producer[B] = {
  def build[B,P<:Producer[B]](under: => impl.Producer[B], size: Option[Int], bound: Option[Int], index: Option[Int => B], distinct: Option[Equiv[B]], sorted: Option[Order[B]]): P = {
    val p = 
    (size, bound, index, distinct, sorted) match {
      case (Some(s), b, None, None, None) => new Producer[B](under) with Sized { def size = s }
      //case (Some(s), b, Some(i), None, None) => new Producer[B](under) with Sized with Indexed {
      //  type Mapped[+B] = Producer[B] with Sized with Indexed
      //  type Sliced[+B] = Producer[B] with Sized with Indexed
      //  def size = s; def index = i
      //}
      case (Some(s), b, Some(i), None, None) => new Producer[B](under) with SizedIndexed[B] { def size = s; def index = i }
      case (None, Some(b), None, None, None) => new Producer[B](under) with Bounded { def sizeUpperBound = b }
      case (Some(s), b, Some(i), Some(d), Some(so)) => ???
      case _ => ???
    }
    p.asInstanceOf[P]
    //???
  }
  
  val UnknownBound = Int.MaxValue
  //lazy val PayForIt = ???
  //def fromIndexed[A](as: IndexedSeq[A]) = new Producer[A](impl.fromIndexed(as)()) with Sized { def size = as.size }
  //def fromIndexed[A](as: IndexedSeq[A]) = new Producer[A](impl.fromIndexed(as)(), Some(as.size), Some(as.size), Some(as.apply)) with Capa[Sized with Indexed]
  
  //class SizedIndexed[+A] extends Producer[A] with Indexed with Sized
  trait SizedIndexed[+A] extends Producer[A] with Indexed with Sized {
    type Mapped[+B] = Producer[B] with SizedIndexed[B]
    type Sliced[+B] = Producer[B] with SizedIndexed[B]
  }
  
  //def fromIndexed[A](as: IndexedSeq[A]) = new Producer[A](impl.fromIndexed(as)()) with Sized with Indexed { def size = as.size; def index = as.apply }
  //def fromIndexed[A](as: IndexedSeq[A]): Producer[A] with Sized with Indexed = build(impl.fromIndexed(as)(),Some(as.size),None,Some(as.apply _),None,None)
  //def fromIndexed[A](as: IndexedSeq[A]): Producer[A] with Indexed with Sized = build(impl.fromIndexed(as)(),Some(as.size),None,Some(as.apply _),None,None)
  //def fromIndexed[A](as: IndexedSeq[A]): SizedIndexed[A] = build(impl.fromIndexed(as)(),Some(as.size),None,Some(as.apply _),None,None)
  
  // Behave differently!!?
  def fromIndexed[A](as: IndexedSeq[A]): Producer[A] with SizedIndexed[A] = build(impl.fromIndexed(as)(),Some(as.size),None,Some(as.apply _),None,None)
  //def fromIndexed[A](as: IndexedSeq[A]): Producer[A] with Sized with Indexed = build[A,Producer[A] with Sized with Indexed](impl.fromIndexed(as)(),Some(as.size),None,Some(as.apply _),None,None)
  
}
import Producer._

///*

trait Bounded { self: Producer[_] =>
  def sizeUpperBound: Int
  //type Mapped[B] <: Producer[B] with Bounded
  type KeepingBound[B] = Producer[B] with Bounded
  def keepBounded[B](newUnder: => impl.Producer[B], boundT: Int => Int): KeepingBound[B] = 
    sizeUpperBound |> boundT |> (newBound => new Producer[B](newUnder) with Bounded { def sizeUpperBound = newBound })
  //def keepSized[B](newUnder: => impl.Producer[B], sizeT: Int => Int)
}

trait Sized extends Bounded { self: Producer[_] =>
  type Mapped[+B] <: Producer[B] with Sized
  type Sliced[+B] <: Producer[B] with Sized
  type Filtered[+B] <: Producer[B] with Bounded
  
  def sizeUpperBound = size
  def size: Int

  //type KeepingSize[B] = Producer[B] with Sized
  //def keepSized[B](newUnder: => impl.Producer[B], sizeT: Int => Int): KeepingSize[B] = 
  //  size |> sizeT |> (newSize => new Producer[B](newUnder) with Sized { def size = newSize })

  //override def map[B](f: A => B): Producer[B] with Sized[B] = new Producer(() => impl.map(underFun)(f)) with Sized[B] {
  //  def size: Int = self.size
  //}
  // TODO:
  //override def repeat[R](k: Consumer[A] => Aggr[R]): Producer[R] with Bounded
}

// TODO
trait Indexed { self: Producer[_] =>
  type Mapped[+B] <: Producer[B] with Indexed
  type Sliced[+B] <: Producer[B] with Indexed
  
  def index: Int => TA
  def apply(i: Int): TA = index(i)
}
//*/

abstract class Order[-A] {
  def compare(lhs: A, rhs: A): Bool
  
  trait Sorted { self: Producer[_] =>
    type Sliced[+B] <: Producer[B] with Sorted
    type Filtered[+B] <: Producer[B] with Sorted
    
    def mergeSorted[B >: TA](that: Producer[B] with Sorted): Producer[B] with Sorted = ???
    def binaryFind = ??? // TODO
  }
}
abstract class Equiv[-A] {
  def equiv(lhs: A, rhs: A): Bool
  trait Distinct { self: Producer[_] =>
    def find[B >: TA](x: B): Option[B] = ???
  }
}
abstract class Hash[-A] extends Equiv {
  def hash(a: A): Int
}

/*
Brittle mess:
  `Producer[A] with SizedIndexed[A]` works all the time (it redefined Mapped/Sliced so they don't conflict)
  `Producer[A] with Sized with Indexed` works fine until it is mapped; then it breaks -- result cannot be interpreted as `Producer[A] with Sized with Indexed` anymore!
*/
object Test extends App {
  
  //def fromIndexed2[B](as: IndexedSeq[B]): Producer[B] with Sized with Indexed = fromIndexed[B](as)
  //def fromIndexed3: Producer[Int] with Sized with Indexed = fromIndexed(1 to 3)
  //def fromIndexed3 = fromIndexed(1 to 3)
  
  val s = fromIndexed(1 to 3).map(x => x oh_and print(s"[$x]"))
  //val s = fromIndexed2(1 to 3).map(x => x oh_and print(s"[$x]"))
  //val s = fromIndexed3.map(x => x oh_and print(s"[$x]"))
  val p = s.map(_+1)
  
  //println(Producer.fromIndexed(1 to 3).map(_+1).size)
  println(p.size)
  println(p.index(1))
  
  
  //def test(p: Producer[Int] with Sized with Indexed) = {
  //def test(q: Producer[Int] with Indexed with Sized): Producer[Int] with Sized with Indexed = {
  def test[A](q: Producer[A] with Indexed with Sized): Producer[A] with Sized with Indexed = {
    println(q.size, q.index)
    q
  }
  val q = test(p)
  println(q.size, q.index)
  val q2 = test(q)
  //val q2 = test(q.map(_+1)) // nope
  //val q0 = q.map(_+1); val q2 = test(q0) // nope
  println(q2.size, q2.index)
  
  //val fromIndexed3: Producer[Int] with Sized with Indexed = fromIndexed(1 to 3)  // nope!
  //val fromIndexed3 = fromIndexed(1 to 3)
  //test(fromIndexed3.map(_+1))
  
  
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


