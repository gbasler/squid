package scp

object Dummies {
  
  def byNameMethod(x: => Int) = x+1
  
  class MyClass {
    
    case class MyMemberClass(n: Int)
    
  }
  
  
  object Run {
    
    val f = (x: Int) => x + 1
    
  }
  
  object TypeMatching {
    class Expr[+A]
    class Abst[-A,+B] extends Expr[A => B]
    object Abst { def apply[A,B] = new Abst[A,B] }
    class Appl[+A,+B] extends Expr[B]
    object Appl { def apply[A,B] = new Appl[A,B] }
  }
  
  object VirtualizedConstructs {
    var ev = 0
    def setEv(value: Int) = ev = value
    def getEv = ev
  }
  
  object Functions {
    def takeByName(x: => Int) = x+1
    def takeFunction(f: Unit => Int) = f(())+1
  }
  
  object InheritedDefs {
    
    class Base {
      def foo = 42
      class Test[A] {
        def bar = 666
      }
      object Test { def apply[T] = new Test[T] }
      class TestObject
      object TestObject
    }
    object Derived extends Base {
      class TestDerived extends Test[Int]
      object TestDerived { def apply() = new TestDerived() }
      def apply() = new Derived
    }
    class Derived {
      def foo = 0.5
    }
    
  }
  
  object BasicEmbedding {
    
    case class MC[A](x:Int)(syms: A*)
    
    def foo(n: Int)(s: String) = s * n
    
  }  
  
  
}
