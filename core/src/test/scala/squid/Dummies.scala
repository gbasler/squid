package squid

object Dummies {
  
  def byNameMethod(x: => Int) = x+1
  
  class MyClass {
    
    case class MyMemberClass(n: Int)
    
  }
  
  
}
