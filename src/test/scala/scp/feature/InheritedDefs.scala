package scp
package feature

import utils.Debug._

import scala.reflect.runtime.{universe => sru}
import scp.utils.meta.RuntimeUniverseHelpers.srum

//class Base {
//  def foo = 42
//  class Test[A] {
//    def bar = 666
//  }
//  class TestObject
//  object TestObject
//}
//object Derived extends Base {
//  class TestDerived extends Test[Int]
//}
//class Derived {
//  def foo = 0.5
//}

class InheritedDefs extends MyFunSuite2 {
  import TestDSL2.Predef._
  import Dummies.InheritedDefs._ // FIXME class loading
  
  test("Inherited Methods") {
    
    val d = ir"Derived"
    
    assert(d.trep.tpe =:= typeRepOf[Derived.type].tpe)
    assert(d.trep.tpe =:= sru.typeOf[Derived.type])
    
    eqt(d.trep, typeRepOf[Derived.type])
    same(ir"Derived.foo".run, 42)
    same(ir"$d.foo".run, 42)
    
    //same(ir"(new Derived()).foo".run, 0.5) // FIXME new
    same(ir"(Derived()).foo".run, 0.5)
    
    //same(TestDSL2.loadMtdSymbol(TestDSL2.loadTypSymbol("scp.feature.Derived"), "foo", None), sru.typeOf[Derived.type].member(sru.TermName("foo"))) // FIXME class loading
    //same(TestDSL.loadSymbol(false, "scp.feature.Derived", "foo"), sru.symbolOf[Derived].typeSignature.member(sru.TermName("foo"))) // TODO
  }
  
  test("Inherited Classes") {
    
     // FIXME new
    //val ndti = ir"new Derived.Test[Int]"
    //val ndtd = ir"new Derived.TestDerived"
    val ndti = ir"Derived.Test[Int]"
    val ndtd = ir"Derived.TestDerived()"
    
    eqt(ndti.trep, new base.TypeRep(sru.typeOf[Derived.Test[Int]]))
    assert(ndti.trep =:= new base.TypeRep(sru.typeOf[Derived.Test[Int]]))
    assert(!(ndtd.trep =:= new base.TypeRep(sru.typeOf[Derived.Test[Int]])))
    assert(ndtd.trep <:< new base.TypeRep(sru.typeOf[Derived.Test[Int]]))
    
    same(ir"$ndtd.bar".run, 666)
    
    
    val manual_ndtd = {
      import TestDSL2._
      val dtp = TypeRep(sru.typeOf[Derived.TestDerived])
      methodApp(newObject(dtp),
        loadMtdSymbol(loadTypSymbol(srum.runtimeClass(sru.typeOf[scp.Dummies.InheritedDefs.Derived.TestDerived]).getName), "<init>", None), Nil, Args()::Nil, dtp)
    }
    //eqt(ndtd.rep, manual_ndtd) // TODO new
    
  }
  
  test("Inherited Objects") { // FIXME
    
    //val to = dbgir"Derived.TestObject"
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





