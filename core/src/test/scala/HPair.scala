package scp

case class HPair[+A](first: A, second: A) {
  
  def map[B](f: A => B): HPair[B] = HPair(f(first), f(second))
  
  def swap = HPair(second, first)
  
}
class MutHPair[A](first: A, second: A) extends HPair[A](first, second)
