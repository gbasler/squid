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
  
}
object Functions {
  def takeByName(x: => Int) = x+1
  def takeFunction(f: Unit => Int) = f(())+1
}
