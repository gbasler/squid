package sfusion2

import squid.lib.transparencyPropagating
import squid.lib.transparent
import squid.quasi.overloadEncoding
import squid.utils._
import squid.quasi.{phase, embed, dbg_embed}

//import CPSOptions._

/**
  * Created by lptk on 13/06/17.
  */
@embed
case class Strm[+A](producer: () => Producer[A]) {
//class Strm[+A](val producer: () => Producer[A]) {
  
  @transparencyPropagating
  @phase('Impl)
  def map[B](f: A => B): Strm[B] = Strm(() => {
    val p = producer()
    () => p() map f
    //() => optMap(p())(f)
  })
  
  @transparent
  @phase('Impl)
  //def doWhile(f: A => Bool): Unit = {
  //  val p = producer()
  //  //var curElt: Option[A] = None
  //  //do p() map (Some(_)) foreach (curElt = _)
  //  //do p() foreach (a => curElt = Some(a))
  //  //while (curElt.isDefined)
  //  Strm.loopWhile {
  //    //p() foreach (a => curElt = Some(a))
  //    val elt = p()
  //    elt foreach f
  //    //curElt = elt
  //    //curElt.isDefined
  //    elt.isDefined
  //  }
  //}
  //def doWhile(f: A => Bool): Unit = {
  def doWhile(f: A => Bool): Bool = { // TODO rm bool return; makes for cleaner code when not needed...
    val p = producer()
    //var wantedToContinue = false
    var reachedEnd = false
    Strm.loopWhile {
      val elt = p()
      //elt foreach f
      //elt.isDefined
      //elt.fold(false)(f)
      elt.fold{reachedEnd = true; false}(f)
    }
    reachedEnd
  }
  
  @transparencyPropagating
  def take(n: Int): Strm[A] = Strm(() => {
    val p = producer()
    var taken = 0
    () => if (taken < n) { taken += 1; p() } else None
  })
  
  @transparencyPropagating
  def drop(n: Int): Strm[A] = Strm(() => {
    val p = producer()
    var dropped = 0
    () => {
      while (dropped < n) { p(); dropped += 1 };
      p()
    }
  })
  
  //
  
  //@phase('Sugar)
  @phase('Fold)
  def foreach(f: A => Unit): Unit = doWhile { x => f(x); true }
  
  //@phase('Sugar)
  @phase('Fold)
  def fold[B](z: B)(f: (B,A) => B): B = {
    var curVal = z
    //doWhile(curVal = f(curVal, _))
    doWhile { a => curVal = f(curVal, a); true }
    curVal
  }
  
  @phase('Sugar)
  def flatten[B](implicit ev: A <:< Strm[B]) = flatMap(ev)
  
  @phase('Sugar)
  def flatMap[B](f: A => Strm[B]): Strm[B] = Strm.flatten(map(f))
  
  @phase('Sugar)
  def filter(p: A => Bool): Strm[A] = ???
  
  @phase('Sugar)
  def zip[B](that: Strm[B]): Strm[(A,B)] = zipWith(that)(_ -> _)
  
  @phase('Impl)
  @transparencyPropagating
  def zipWith[B,C](that: Strm[B])(combine: (A,B) => C): Strm[C] = Strm(() => {
    //var cur
    val p0 = producer()
    val p1 = that.producer()
    //() => combine(p0(), p1())
    //() => p0().fold(None){ x0 => p1().fold(None){ x1 => Some(combine(x0,x1)) } }
    () => p0().flatMap{ x0 => p1().map{ x1 => combine(x0,x1) } }
  })
  
}
object Strm {
  
  //def apply[+A](producer: () => Producer[A])
  
  @phase('Impl)
  @transparencyPropagating
  def flatten[A](that: Strm[Strm[A]]): Strm[A] = Strm(() => {
    //???
    val p = that.producer()
    var curAs: Option[Producer[A]] = None
    () => {
      var curA: Option[A] = None
      //if (curA.isDefined) curA()
      //if (!curA.isDefined) curA = 
      loopWhile {
        //curA.isDefined || {
        //  curA = p()
        //}
        val as = curAs orElse {
          val as = p() map (_.producer())
          curAs = as
          as
        }
        //curA.isDefined && 
        as.fold(false) { as =>
          //curA = as()
          //!curA.isDefined && { curAs = None; true }
          val a = as()
          //curA = a
          //!a.isDefined && { curAs = None; true }
          if (a.isDefined) { curA = a; false } else { curAs = None; true }
        }
      }
      curA
    }
  })
  
  //@phase('Sugar)
  def flatflatdoWhile[A,B,C](ass: Strm[Strm[A]], bss: Strm[Strm[B]])(f: (A,B) => C): Bool = {
    //val pas = ass.producer()
    //val pbs = bss.producer()
    ////var curAs: Option[Producer[A]] = None
    //var curAs: Option[A] = None
    //loopWhile {
    //  pas().fold(false) { as =>
    //    
    //  }
    //  //f(a,b)
    //}
    ???
    true // FIXME correct?
  }
  
  @phase('Impl)
  @transparencyPropagating
  def fromIndexed[A](xs: IndexedSeq[A]): Strm[A] = Strm(() => {
    var i = 0
    val len = xs.length
    () => {
      //println("> "+i)
      if (i < len) { val r = Some(xs(i)); i += 1; r }
      else None
    }
  })
  
  @phase('Impl)
  @transparencyPropagating
  def fromIterable[A](xs: Iterable[A]): Strm[A] = Strm(() => {
    val it = xs.iterator
    () => if (it.hasNext) Some(it.next) else None
  })
  
  @phase('Impl)
  @transparencyPropagating
  def iterate[A](start: A)(next: A => A): Strm[A] = Strm(() => {
    var cur = start
    () => {
      val c = cur
      val r = Some(c)
      cur = next(c)
      r
    }
  })
  
  
  
  @transparent
  def loopWhile(cnd: => Bool) = {
    while(cnd)()
  }
  
}