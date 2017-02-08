package sfusion

import org.scalatest.FunSuite
import squid.TestDSL
import squid.ir._

/**
  * Created by lptk on 07/02/17.
  */
object Timings {
  
  val PRIMES = 10000
  val N = 30
  val `OptimTests.this` = this
  
  object SieveSequOpt extends App {
    
    def test = { /* Paste gen'd code */ }
    
    println(test)
    
    for (i <- 0 until N)
      time(test)
    
  }
  
  object SieveSequ extends App {
    
    def test = {
      val posNats = Sequence.iterate(1)(_+1)
      val odds = posNats.map(_*2+1)
      val divs = 2 +: odds
      val primes = 2 +: (posNats filter { n =>
        val sqrt = math.sqrt(n).ceil.toInt
        divs.takeWhile(_ <= sqrt).forall(d => n % d != 0)
      })
      primes.take(PRIMES).fold(0)(_ + _)
    }
    
    println(test)
    
    for (i <- 0 until N)
      time(test)
    
  }
  
  object SieveStreams extends App {
    
    def test = {
      val posNats = Stream.iterate(1)(_+1)
      val odds = posNats.map(_*2+1)
      val divs = 2 #:: odds
      val primes = 2 #:: (posNats filter { n =>
        val sqrt = math.sqrt(n).ceil.toInt
        divs.takeWhile(_ <= sqrt).forall(d => n % d != 0)
      })
      primes.take(PRIMES).sum
    }
    
    println(test)
    
    for (i <- 0 until N)
      time(test)
    
  }
  
  def time[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    println("Elapsed time: " + (t1 - t0) + "ns")
    result
  }
  
}

