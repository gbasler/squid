package sfusion

import org.scalatest.FunSuite
import squid.ir.ClassEmbedder

/**
  * Created by lptk on 07/02/17.
  */
class HighLevel extends FunSuite {
  
  val s123 = Sequence(1,2,3)
  val strm123 = Sequence.fromStream(Stream(1,2,3))
  val strmInf0 = Sequence.fromStream(Stream.continually(0))
  val strmNat = Sequence.fromStream(Stream.iterate(0)(_+1))
  
  test("Equality") {
    
    assert(s123 == s123)
    assert(s123 != Sequence(1,2,3,4))
    assert(s123 != Sequence(1,2))
    assert(strmInf0 == strmInf0)
    assert(strmInf0 != Sequence.fromStream(Stream.continually(0))) // cannot compare infinite sequences
    assert(strmNat != strmInf0)
    
  }
  
  test("Show") {
    assert(Sequence.fromIndexed(1 to 100).show(10) == "Sequence(1,2,3,4,5,6,7,8,9,10,...)")
    assert(s123.show() == "Sequence(1,2,3)")
    assert(s123.show(3) == "Sequence(1,2,3)")
    assert(s123.show(2) == "Sequence(1,2,...)")
    assert(s123.show(1) == "Sequence(1,...)")
    assert(s123.show(0) == "Sequence(,...)")
    assert(strm123.show(3) == "Sequence(1,2,3)")
    assert(strm123.show(2) == "Sequence(1,2,...)")
    assert(strmInf0.show(5) == "Sequence(0,0,0,0,0,...)")
    assert(strmNat.show(5) == "Sequence(0,1,2,3,4,...)")
    assert((strmNat zip strmInf0 take 10 show 5) == "Sequence((0,0),(1,0),(2,0),(3,0),(4,0),...)")
    assert((strmNat zip strmInf0 take 10 show 10) == "Sequence((0,0),(1,0),(2,0),(3,0),(4,0),(5,0),(6,0),(7,0),(8,0),(9,0))")
  }
  
  test("Map") {
    
    assert(s123.map(_ + 1 toDouble) == Sequence(2.0,3.0,4.0))
    
  }
  
  test("Zip") {
    
    val z0 = Sequence((1,0),(2,1),(3,2))
    assert(s123.zip(strmNat) == z0)
    assert(z0 == s123.zip(strmNat))
    assert(strmNat.zip(s123) == z0.map(_.swap))
    
    val strm = Stream.iterate(0)(_+1).map(a => (a,0)).take(10)
    val z1 = Sequence.fromStream(strm.force) // `force` is to get a definiteSize so `equals` does not immediately bail out...
    val z2 = Sequence.fromList(strm.toList)
    val s = strmNat zip strmInf0 take 10
    assert(s == z1)
    assert(z1 == s)
    assert(s == z2)
    assert(z2 == s)
    
  }
  
  
  
  test("Misc") {
    //println
    {
      
    }
  }
  
}
