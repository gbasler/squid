package sfusion

import org.scalatest.FunSuite
import squid.ir.ClassEmbedder
import squid.utils._
import impl._

/**
  * Created by lptk on 07/02/17.
  * 
  * TODO an assertEq function that also tries different consumption modes and watches return values
  * 
  */
class ImplTests extends FunSuite {
  
  test("Unfold") {
    
    val s = Stream.continually(0)
    assert(take(unfold(s){xs => xs.headOption map (h => (h,xs.tail))})(10) |> toBuffer equals Seq.fill(10)(0))
    
  }
  
  test("Take") {
    
    def s = fromIndexed(1 to 10)
    assert((take(s)(3) |> toBuffer) == Seq(1,2,3))
    
    assert(take(s)(3)(a => true))
    
    //val t = s
    //println(take(s)(30)(a => println(a) before true))
    //println(take(s)(30)(a => true))
    
    assert((take(continually(0))(10) |> toBuffer) == Seq.fill(10)(0))
    
  }
  
  
}
