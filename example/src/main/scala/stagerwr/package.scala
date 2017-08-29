/**
  * Created by lptk on 16/06/17.
  */
package object stagerwr {
  
  //type Producer[A] = (A => Unit) => Unit
  
  type Consumer[A] = A => Unit
  type Producer[A] = Consumer[A] => Unit
  
}
