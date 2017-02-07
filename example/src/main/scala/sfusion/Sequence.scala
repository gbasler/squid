package sfusion

import squid.utils._

import scala.collection.mutable
import squid.quasi.{phase, embed, dbg_embed}

import scala.collection.LinearSeq

/**
  * Created by lptk on 07/02/17.
  * 
  * Q: are the underlying producers supposed to be referentially-transparent? If one use gives size `n`,
  *    will the next use give the same size? (could be used to update size info)
  * 
  */
//@dbg_embed
@embed
final class Sequence[+A](val under: () => impl.Producer[A], val size: SizeInfo) {
  
  @inline @phase('Sugar)
  def bounded = size.isLeft || size == Right(true)
  
  @inline @phase('Impl)
  def map[B](f: A => B): Sequence[B] = new Sequence(() => impl.map(under())(f), size)
  
  @inline @phase('Impl)
  def zip[B](that: Sequence[B]): Sequence[(A,B)] = new Sequence(() => impl.zip(under(),that.under()), minSize(size,that.size))
  
  @inline @phase('Impl)
  def take(num: Int): Sequence[A] = new Sequence(() => impl.take(under())(num), minSize(size,Left(num)))
  
  @phase('Impl)
  def show(maxPrinted: Int = 10): String = s"Sequence(${
    val sb = new StringBuilder
    
    val p = under()
    impl.foreach(impl.take(p)(maxPrinted)){ e => if (sb.nonEmpty) sb += ','; 
      sb ++= s"$e" }
      //sb ++= e.toString }
    // ^ FIXME @embedded code tries to access toString from `__b__.loadTypSymbol("sfusion.Sequence.EmbeddedIn#Class$#__Defs__$#_4_#A")`! see below
    
    var shorten = false
    p{_ => shorten = true; false}
    // ^ if `p` has at least one element left, we need to use a shortening mark (append "...")
    
    if (shorten) sb ++= ",..."
    sb.result
  })"
  
  @phase('Sugar)
  override def toString: String = show()
  
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
object Sequence {
  
  @phase('Sugar)
  def apply[A](xs: A*): Sequence[A] = Sequence.fromIndexed(xs.toIndexedSeq)
  
  @phase('Sugar)
  def fromIndexed[A](is: IndexedSeq[A]): Sequence[A] = new Sequence(() => impl.fromIndexed(is), Left(is.size))
  
  @phase('Sugar)
  def fromIterable[A](ite: Iterable[A]): Sequence[A] = ??? // TODO
  
  @phase('Sugar)
  private def fromLinearImpl[A](xs: LinearSeq[A]) = impl.unfold(xs)(xs => xs.headOption map (h => (h,xs.tail)))
  @phase('Sugar)
  def fromLinear[A](xs: LinearSeq[A]): Sequence[A] = new Sequence(() => fromLinearImpl(xs), Right(xs.hasDefiniteSize))
  @phase('Sugar)
  def fromStream[A](xs: Stream[A]): Sequence[A] = fromLinear(xs)
  @phase('Sugar)
  def fromList[A](xs: List[A]): Sequence[A] = new Sequence(() => fromLinearImpl(xs), Left(xs.size)) // could factor with above...
  
}

