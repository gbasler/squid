package sfusion
package algo

import squid.quasi.{phase, embed, dbg_embed}
import squid.utils._

/**
  * Created by lptk on 08/02/17.
  */
@embed
object `package` {
  
  /** Returns a sequence of all prime numbers, as computed rather inefficiently. */
  @phase('Sugar)
  def simpleSieve: Sequence[Int] = {
    val posNats = Sequence.iterate(1)(_+1)
    val odds = posNats.map(_*2+1)
    val divs = 2 +: odds
    2 +: (posNats filter { n =>
      val sqrt = math.sqrt(n).ceil.toInt
      divs.takeWhile(_ <= sqrt).forall(d => n % d != 0)
    })
  }
  
  /** Returns the sum of the first `n` prime numbers. */
  @phase('Sugar)
  def primeSum(n: Int): Int = simpleSieve.take(n).fold(0)(_ + _)
  
}

