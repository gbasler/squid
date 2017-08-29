package stagerwr

import squid.lib.transparencyPropagating
import squid.lib.transparent
import squid.quasi.overloadEncoding
import squid.utils._
import squid.quasi.{phase, embed, dbg_embed}

import Strm.loopWhile

/**
  * Created by lptk on 13/06/17.
  */
@embed
//case class Strm[+A](producer: () => Producer[A]) {
case class Strm[A](producer: () => Producer[A]) {
  
  @transparencyPropagating
  @phase('Impl)
  def map[B](f: A => B): Strm[B] = Strm(() => {
  //def map[B](f: A => B): Strm[B] = Strm[B](() => {
    val p = producer()
    //k => k(p(f))
    k => p(f andThen k)
    //(k:B=>Unit) => p(f andThen k)
  })
  //@phase('Sugar)
  //def map[B](f: A => B): Strm[B] = Strm(() => Strm.doMap(producer(),f))
  
  //@phase('Sugar)
  //@phase('Fold)
  @phase('Impl)
  def foreach(f: A => Unit): Unit = {
    val p = producer()
    var mayHaveLeft = true
    while (mayHaveLeft) {
      mayHaveLeft = false
      p { a =>
        f(a)
        mayHaveLeft = true
      }
    }
  }
  
  //@transparent
  //@phase('Impl)
  ////def doWhile(f: A => Bool): Unit = {
  ////  val p = producer()
  ////  //var curElt: Option[A] = None
  ////  //do p() map (Some(_)) foreach (curElt = _)
  ////  //do p() foreach (a => curElt = Some(a))
  ////  //while (curElt.isDefined)
  ////  Strm.loopWhile {
  ////    //p() foreach (a => curElt = Some(a))
  ////    val elt = p()
  ////    elt foreach f
  ////    //curElt = elt
  ////    //curElt.isDefined
  ////    elt.isDefined
  ////  }
  ////}
  ////def doWhile(f: A => Bool): Unit = {
  //def doWhile(f: A => Bool): Bool = { // TODO rm bool return; makes for cleaner code when not needed...
  //  val p = producer()
  //  //var wantedToContinue = false
  //  var reachedEnd = false
  //  Strm.loopWhile {
  //    val elt = p()
  //    //elt foreach f
  //    //elt.isDefined
  //    //elt.fold(false)(f)
  //    elt.fold{reachedEnd = true; false}(f)
  //  }
  //  reachedEnd
  //}

  @transparencyPropagating
  @phase('Impl)
  def take(n: Int): Strm[A] = Strm(() => {
    val p = producer()
    var taken = 0
    k => if (taken < n) { taken += 1; p(k) }
  })

  //@transparencyPropagating
  //def drop(n: Int): Strm[A] = Strm(() => {
  //  val p = producer()
  //  var dropped = 0
  //  () => {
  //    while (dropped < n) { p(); dropped += 1 };
  //    p()
  //  }
  //})
  //
  ////
  //
  ////@phase('Sugar)
  //@phase('Fold)
  //def foreach(f: A => Unit): Unit = doWhile { x => f(x); true }
  //
  //@phase('Sugar)
  //@phase('Fold)
  @phase('Impl)
  def fold[B](z: B)(f: (B,A) => B): B = {
    var curVal = z
    foreach { a => curVal = f(curVal, a) }
    curVal
  }
  //
  //@phase('Sugar)
  //def flatten[B](implicit ev: A <:< Strm[B]) = flatMap(ev)
  //
  //@phase('Sugar)
  //def flatMap[B](f: A => Strm[B]): Strm[B] = Strm.flatten(map(f))
  
  @phase('Sugar)
  def flatten[B](implicit ev: A <:< Strm[B]) = flatMap(ev)
  
  /*
  //@phase('Impl)
  @phase('FlatMap)
  def flatMap[B](f: A => Strm[B]): Strm[B] = Strm(() => {
    val p = producer()
    //var curA: Option[A] = None
    var curBs: Option[Producer[B]] = None
    //k => {
    //  if (!curBs.isDefined) p(as => curBs = Some(f(as).producer()))
    //  curBs.map { as =>
    //    as(k)
    //  }
    //}
    k => {
      var consumed = false
      loopWhile {
        if (!curBs.isDefined) p(as => curBs = Some(f(as).producer()))
        //curBs.map { bs =>
        //  //bs(k)
        //  bs { b =>
        //    k(b)
        //    consumed = true
        //  }
        //}
        //if (!consumed) { curBs = None; true } else false
        curBs.fold(false) { bs =>
          bs { b =>
            k(b)
            consumed = true
          }
          if (!consumed) { curBs = None; true } else false
        }
        //!consumed
      }
    }
  })
  */
  
  @phase('Impl)
  //@phase('Sugar)
  @transparencyPropagating
  //def flatMap[B](f: A => Strm[B]): Strm[B] = Strm(() => {
  //  //Strm.flatten(map(f).producer())
  //  Strm.flatten(map(f andThen (_.producer())).producer())
  //})
  def flatMap[B](f: A => Strm[B]): Strm[B] = Strm(() => {
    Strm.doFlatMap(producer(), f andThen (_.producer()))
  })
  

  //@phase('Sugar)
  @phase('Impl)
  @transparencyPropagating
  def filter(pred: A => Bool): Strm[A] = Strm(() => {
    val p = producer()
    //k => p { a => println("> "+a);if (pred(a)) k(a) }
    k => {
      var continue = true
      while(continue) {
        continue = false
        p { a => if (pred(a)) k(a) else continue = true }
      }
    }
  })
  
  @phase('Sugar)
  def zip[B](that: Strm[B]): Strm[(A,B)] = zipWith(that)(_ -> _)
  
  @phase('Impl)
  @transparencyPropagating
  def zipWith[B,C](that: Strm[B])(combine: (A,B) => C): Strm[C] = Strm(() => {
    val p0 = producer()
    val p1 = that.producer()
    ////() => combine(p0(), p1())
    ////() => p0().fold(None){ x0 => p1().fold(None){ x1 => Some(combine(x0,x1)) } }
    //() => p0().flatMap{ x0 => p1().map{ x1 => combine(x0,x1) } }
    k => p0 { a => p1 { b => k(combine(a, b)) } }
  })
  
}
object Strm {
  
  
  @phase('Impl)
  @transparencyPropagating
  def singleIf[A](a: A, cond: Bool): Strm[A] = Strm(() => {
    //k => if (cond(a)) k(a)
    //k => if (cond) k(a)
    var singleConsumed = false
    k => if (cond && !singleConsumed) { k(a); singleConsumed = true }
  })
  
  
  
  //@phase('FlatMap)
  //def flatten[A](p: Producer[Strm[A]]): Producer[A] = {
  //  var curAs: Option[Producer[A]] = None
  //  k => {
  //    var consumed = false
  //    loopWhile {
  //      if (!curAs.isDefined) p(as => curAs = Some(as.producer()))
  //      curAs.fold(false) { as =>
  //        as { a =>
  //          k(a)
  //          consumed = true
  //        }
  //        if (!consumed) { curAs = None; true } else false
  //      }
  //    }
  //  }
  //}
  @phase('FlatMap)
  def flatten[A](p: Producer[Producer[A]]): Producer[A] = {
    var curAs: Option[Producer[A]] = None
    k => {
      var consumed = false
      loopWhile {
        if (!curAs.isDefined) p(as => curAs = Some(as))
        curAs.fold(false) { as =>
          as { a =>
            k(a)
            consumed = true
          }
          if (!consumed) { curAs = None; true } else false
        }
      }
    }
  }
  
  //@phase('FlatMap)
  //def doMap[A,B](p: Producer[A], f: A => B): Producer[B] = {
  //  k => p(f andThen k)
  //}
  
  //def flattenNice[A,E,B](xs: Producer[A], ef: A => E, f: (A,E) => Producer[B]): () => Producer[B] = () => {
  
  @phase('FlatMap)
  def doFlatMap[A,B](p: Producer[A], f: A => Producer[B]): Producer[B] = {
    var curBs: Option[Producer[B]] = None
    k => {
      var consumed = false
      loopWhile {
        if (!curBs.isDefined) p(a => curBs = Some(f(a)))
        curBs.fold(false) { as =>
          as { a =>
            k(a)
            consumed = true
          }
          if (!consumed) { curBs = None; true } else false
        }
      }
    }
  }
  
  
  
  ////def apply[+A](producer: () => Producer[A])
  //
  //@phase('Impl)
  //@transparencyPropagating
  //def flatten[A](that: Strm[Strm[A]]): Strm[A] = Strm(() => {
  //  //???
  //  val p = that.producer()
  //  var curAs: Option[Producer[A]] = None
  //  () => {
  //    var curA: Option[A] = None
  //    //if (curA.isDefined) curA()
  //    //if (!curA.isDefined) curA = 
  //    loopWhile {
  //      //curA.isDefined || {
  //      //  curA = p()
  //      //}
  //      val as = curAs orElse {
  //        val as = p() map (_.producer())
  //        curAs = as
  //        as
  //      }
  //      //curA.isDefined && 
  //      as.fold(false) { as =>
  //        //curA = as()
  //        //!curA.isDefined && { curAs = None; true }
  //        val a = as()
  //        //curA = a
  //        //!a.isDefined && { curAs = None; true }
  //        if (a.isDefined) { curA = a; false } else { curAs = None; true }
  //      }
  //    }
  //    curA
  //  }
  //})
  //
  ////@phase('Sugar)
  //def flatflatdoWhile[A,B,C](ass: Strm[Strm[A]], bss: Strm[Strm[B]])(f: (A,B) => C): Bool = {
  //  //val pas = ass.producer()
  //  //val pbs = bss.producer()
  //  ////var curAs: Option[Producer[A]] = None
  //  //var curAs: Option[A] = None
  //  //loopWhile {
  //  //  pas().fold(false) { as =>
  //  //    
  //  //  }
  //  //  //f(a,b)
  //  //}
  //  ???
  //  true // FIXME correct?
  //}
  
  @phase('Impl)
  @transparencyPropagating
  def fromIndexed[A](xs: IndexedSeq[A]): Strm[A] = Strm(() => {
    var i = 0
    val len = xs.length
    (k:A=>Unit) => {
      //println("> "+i)
      if (i < len) { k(xs(i)); i += 1 }
    }
  })
  @phase('Impl)
  @transparencyPropagating
  def range(from: Int, to: Int): Strm[Int] = Strm(() => {
    var i = from
    k => {
      //if (i <= to) { k(i); i += 1 }
      val iv = i
      if (iv <= to) { k(iv); i = iv + 1 }
    }
  })
  
  //@phase('Impl)
  //@transparencyPropagating
  //def fromIterable[A](xs: Iterable[A]): Strm[A] = Strm(() => {
  //  val it = xs.iterator
  //  () => if (it.hasNext) Some(it.next) else None
  //})
  //
  //@phase('Impl)
  //@transparencyPropagating
  //def iterate[A](start: A)(next: A => A): Strm[A] = Strm(() => {
  //  var cur = start
  //  () => {
  //    val c = cur
  //    val r = Some(c)
  //    cur = next(c)
  //    r
  //  }
  //})
  
  
  
  @transparent
  def loopWhile(cnd: => Bool) = {
    while(cnd)()
  }
  
}