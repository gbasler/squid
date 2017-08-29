package stagerwr

import squid.utils._
import Embedding.Predef._
import Embedding.SimplePredef.{Rep => Code, _}

/**
  * Created by lptk on 21/06/17.
  */
object SimplerStaging extends App {
  
  abstract class Stream[A]() {
    def map[B](f: Code[A] => Code[B]) = Linear(this) // TODO
    def flatMap[B](f: Code[A] => Stream[B]) = Nested(this, f)
    //def filter(p: Code[A] => Code[Bool]) = flatMap(x => )
    
    def fold[R](z: Code[R])(f: (Code[R],Code[A]) => Code[R]): Code[R] = {
      
      // continuation k needs to be polymorphic to accomodate the state of each interpreted operator 
      /*
      def aux[X](s: Stram[X])(k: ()): Code[R] = 
        x match {
          case FromArray(arr) =>
            ir"val a = $arr; var i = 0; "
            k()
          //case Linear(s)
        }
      */
      
      ???
    }
    
  }
  case class Nested[A,B](strm: Stream[A], f: Code[A] => Stream[B]) extends Stream[B]
  case class Linear[A](strm: Stream[A]) extends Stream[A]
  case class FromArray[A](arr: Code[Array[A]]) extends Stream[A]
  
  
  
  
}
