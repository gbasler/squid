package scp
package feature

import utils.Debug._
import scala.reflect.runtime.{universe => sru}

class Base {
  def foo = 42
  class Test[A] {
    def bar = 666
  }
  class TestObject
  object TestObject
}
object Derived extends Base {
  class TestDerived extends Test[Int]
}

class InheritedDefs extends MyFunSuite {
  import TestDSL._
  
  test("Inherited Methods") {
    
    val d = dsl"Derived"
    
    eqt(d.trep, typeRepOf[Derived.type])
    same(dsl"Derived.foo".run, 42)
    same(dsl"$d.foo".run, 42)
    
  }
  
  test("Inherited Classes") {
    
    val ndti = dsl"new Derived.Test[Int]"
    val ndtd = dsl"new Derived.TestDerived"
    
    eqt(ndti.trep, SimpleTypeRep(sru.typeOf[Derived.Test[Int]]))
    assert(ndti.trep =:= SimpleTypeRep(sru.typeOf[Derived.Test[Int]]))
    assert(!(ndtd.trep =:= SimpleTypeRep(sru.typeOf[Derived.Test[Int]])))
    assert(ndtd.trep <:< SimpleTypeRep(sru.typeOf[Derived.Test[Int]]))
    
    same(dsl"$ndtd.bar".run, 666)
    
    
    val manual_ndtd = {
      val dtp = SimpleTypeRep(sru.typeOf[Derived.TestDerived])
      methodApp(newObject(dtp),
        loadSymbol(false, "scp.feature.Derived.TestDerived", "<init>"), Nil, Args()::Nil, dtp)
    }
    eqt(ndtd.rep, manual_ndtd)
    
  }
  
  test("Inherited Objects") { // FIXME
    
    //val to = dbgdsl"Derived.TestObject"
    //eqt(to.trep, typeRepOf[Derived.TestObject.type])
    
    /*
    // works
    val TestObject$macro$12 = SimpleTypeRep(sru.typeOf[Derived.TestObject.type])
    val q = Quoted[scp.feature.Derived.TestObject.type, Any](
      TestDSL.moduleObject("_root_.scp.feature.Derived.TestObject", TestObject$macro$12))
    println(q.run)
    */
    
    /*
    println(TestDSL.DynamicTypeRep.apply("scp.feature.Derived"))
    println(TestDSL.DynamicTypeRep.apply("scp.feature.Derived").typ =:= sru.typeOf[Derived.type])
    
    println(TestDSL.DynamicTypeRep.apply("scp.feature.Derived.TestObject"))
    */
  }
  
  
}





