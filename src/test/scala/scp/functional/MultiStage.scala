package scp
package functional

class MultiStage extends MyFunSuite2 {
  import TestDSL2.Predef._
  
  // FIXME class loading
  //test("Nested Quoted Types") {
  //  
  //  val reprep = ir"MultiStage.rep" : Q[ Q[Double, {}], {} ]
  //  val rep = reprep.run
  //  
  //  assert(rep == MultiStage.rep)
  //  
  //}
  
  test("Nested dsl expressions") { // FIXME
    
    {
      val irir = ir""" ir"42" """ : IR[IR[Int,{}],{}]
      val ir = irir.run : IR[Int,{}]
      assert(ir =~= ir"42")
      assert(ir.run == 42)
    }
    
    
    {
      val irir = ir""" ir"scala.None" """ : IR[IR[Option[Int],{}],{}]
      val ir = irir.run : IR[Option[Int],{}]
      assert(ir =~= ir"scala.None")
      assert(ir.run == scala.None)
    }
    
    
    // FIXME: makes the compiler loop indefinitely on gen'd code...:
    
    //val irir = ir""" ir"scala.None: Option[Int]" """ : IR[IR[Option[Int],{}],{}]
    
    {
      //val irir = ir""" ir"42.toDouble" """ : IR[IR[Double,{}],{}]
      //val ir = irir.run : IR[Double,{}]
      //assert(ir =~= ir"42.toDouble")
      //assert(ir.run == 42.toDouble)
    }
    
    {
      //val irir = ir""" ir"List(1,2,3)" """ : IR[IR[List[Int],{}],{}]
      //val ir = irir.run : IR[Int,{}]
      //assert(ir =~= ir"List(1,2,3)")
      //assert(ir.run == List(1,2,3))
    }
    
  }
  
}
object MultiStage {
  import TestDSL2.Predef._
  
  val rep = ir"42.toDouble"
  
}




