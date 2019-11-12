package graph4

import squid.utils._
import org.scalatest.FunSuite

class InterpTests extends FunSuite {
  object TestHarness extends TestHarness
  import CheckDSL.check
  
  test("InterpSimple") (
    // TODO test version with two params and a param cycle (graph used to diverge)
    TestHarness("InterpSimple",
      //dumpGraph = true,
    )(
      check('test)(497)
    )
  )
  
  test("InterpTrivial") (
    TestHarness("InterpTrivial",
      //prefixFilter = "test1",
      dumpGraph = true,
    )(
      check('test3)(127)
    )
  )
  
  test("InterpTrivialRec") (
    // TODO counteract scheduled code regression since <this commit>, due to unrolling up to UnrollingFactor
    TestHarness("InterpTrivialRec",
      //prefixFilter = "test1",
      dumpGraph = true,
    )(
      check('test2_10)(List(true,true,false,true,true,false,true,true,false,true)).doNotExecute, // FIXME wrong sched: [True,True,False,True,True,False,True,False,True,True]
      check('test3_10)(List(true,true,true,false,true,true,true,false,true,true)).doNotExecute, // FIXME wrong sched: [True,True,True,False,True,True,True,False,True,False]
    )
  )
  
}
