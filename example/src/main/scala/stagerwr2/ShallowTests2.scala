package stagerwr2

import Strm._

/**
  * Created by lptk on 28/06/17.
  */
object ShallowTests2 extends App {
  
  
  // complex paper example
  
  val f0 = { (n:Int,m:Int) =>
    fromRange(0,n) zip(
      fromRange(0,m).map(i => fromRange(0,i)).flatMap(x => x)
    ) filter {x => x._1 % 2 == 0} foreach println
  }
  val f1 = { (n:Int,m:Int) =>
    val p = fromRangeImpl(0,n).producer()
    fromRangeImpl(0,m) csme { i => 
      var cont_0 = false
      fromRangeImpl(0,i) csme { b =>
        var cont_1 = false
        p { a => if (a % 2 == 0) println((a,b)); cont_1 = true }
        cont_0 = cont_1
        cont_0 }; cont_0}
  }
  
  //println(f0(5,13))
  //println(f1(5,13))
  println(f0(27,16))
  println(f1(27,16))
  // ok
  
  
  
}
