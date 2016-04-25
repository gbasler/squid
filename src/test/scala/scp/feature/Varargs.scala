package scp
package feature

/**
  * Note: Because of the implementation, we have the same restrictions as Scala's _*
  * For example, dsl"List(0, $args*)" is illegal the same way 'List(1, args:_*)' is illegal
  * It will generate the same error (in the case of QQ, *after* type-checking the deep embedding!):
  *   Error:(13, 14) no ': _*' annotation allowed here
  *   (such annotations are only allowed in arguments to *-parameters)
  *  
  *  With more effort, we could introduce the same flexibility as Scala QQ splicing, as in: q"List(0, ..$args)"
  */
class Varargs extends MyFunSuite {
  import TestDSL._
  
  val args = Seq(dsl"1", dsl"2", dsl"3")
  
  test("No varargs in type position") {
    assertDoesNotCompile(""" dsl"Map[Int,Int]()" match { case dsl"Map[$ts*]()" => } """)
  }
  
  test("Vararg Construction") {
    
    val a = dsl"List($args*)"
    
    eqt(a, dsl"List(1,2,3)")
    
    
    // Note:
    //val a = dbgdsl"List(0,$args*)" // Error:(13, 14) no ': _*' annotation allowed here
    
    // Note:
    // dsl"$$xs*" // Error:(31, 6) Embedding Error: value * is not a member of Nothing
    
  }
  
  test("Vararg Extraction") { // TODO
    
    //dsl"List(1,2,3)" match {
    //  case dbgdsl"List[Int]($xs*)" => assert(xs == args)
    //}
    //dsl"List(1,2,3)" match {
    //  case dbgdsl"List[Int](($xs:Seq[Int])*)" => assert(xs == args)
    //}
    
  }
  
}

