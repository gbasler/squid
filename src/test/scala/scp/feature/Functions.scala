package scp
package feature

/**
  * Note: Because of the implementation, we have the same restrictions as Scala's _*
  * For example, dsl"List(0, $args*)" is illegal the same way 'List(1, args:_*)' is illegal
  * It will generate the same error (in the case of QQ, *after* type-checking the deep embedding!):
  *   Error:(13, 14) no ': _*' annotation allowed here
  *   (such annotations are only allowed in arguments to *-parameters)
  *  
  *  With more effort (and support from the IR!), we could introduce the same flexibility as Scala QQ splicing, as in: q"List(0, ..$args)"
  */
class Functions extends MyFunSuite {
  import TestDSL._
  import Functions._
  
  test("Thunks") {
    
    val tbn = dsl"takeByName(42)"
    
    tbn matches {
      case dsl"takeByName(42)" =>
    } and {
      case dsl"takeByName($n)" =>
        eqt(n, dsl"42")
    }
    
    same(tbn.run, 43)
    same(dsl"takeFunction(_ => 42)".run, 43)
    
  }
  
  test("Functions of Arity 1") {
    
    val f = dsl"(x: Int) => x + 1"
    same(f.run apply 1, 2)
    eqt(dsl"${f}${dsl"1"}", dsl"$f(1)")
    
  }
  
  test("Functions of Arity 2") {
    
    val f = dsl"(x: Int, y: Symbol) => x+y.toString"
    same(f.run apply (1,'ok), "1'ok")
    same(dsl"$f(1,'ok)".run, "1'ok")
    
  }
  
  test("Functions of Arity 3") {
    
    val f = dsl"(a: Int, b: Double, c: Float) => a * b + c"
    same(f.run apply (3,.5,13f), 14.5)
    same(dsl"$f(3,.5,13f)".run, 14.5)
    
  }
  
  test("Functions of Arity 22") {
    val f = dsl"""
      (x1:Int, x2:Int, x3:Int, x4:Int, x5:Int, x6:Int, x7:Int, x8:Int, x9:Int, x10:Int, x11:Int, x12:Int, x13:Int, x14:Int, x15:Int, x16:Int, x17:Int, x18:Int, x19:Int, x20:Int, x21:Int, x22:Int) =>
       x1  +   x2  +   x3  +   x4  +   x5  +   x6  +   x7  +   x8  +   x9  +   x10  +   x11  +   x12  +   x13  +   x14  +   x15  +   x16  +   x17  +   x18  +   x19  +   x20  +   x21  +   x22
    """
    same(f.run apply (2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2), 44)
  }
  
}
object Functions {
  def takeByName(x: => Int) = x+1
  def takeFunction(f: Unit => Int) = f(())+1
}


