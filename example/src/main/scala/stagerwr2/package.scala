/**
  * Created by lptk on 23/06/17.
  */
package object stagerwr2 {
  
  type Consumer[A] = A => Unit
  type Producer[A] = Consumer[A] => Unit
  
  
}
