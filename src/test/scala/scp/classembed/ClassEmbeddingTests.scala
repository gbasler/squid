package scp
package classembed

import scp.ir2._
import scp.utils.meta.RuntimeUniverseHelpers
import utils._

class ClassEmbeddingTests extends MyFunSuite2 {
  import TestDSL2.Predef._
  
  test("Basics") {
    
    val Emb = MyClass.embedIn(TestDSL2)
    
    Emb.Object.Defs.foo.value eqt ir"(arg: Int) => MyClass.foo(arg.toLong).toInt"
    
    Emb.Object.Defs.swap[Int] eqt ir"(arg: (Int,Int)) => (n: Symbol) => n -> arg.swap"
    
  }
  
  test("Basic Lowering") {
    
    TestDSL2.embed(MyClass)
    val Desugaring = new TestDSL2.Lowering('Sugar) with TopDownTransformer
    import MyClass._
    
    val Emb = MyClass.EmbeddedIn(TestDSL2)
    
    ir"foo(42) * 2" transformWith Desugaring eqt
      ir"{val arg = 42; foo(arg.toLong).toInt} * 2"
    
    ir"foobar(42, .5) -> 666" transformWith Desugaring eqt
      //(ir"((a: Int, b: Double) => bar(foo(a))(b))(42, .5) -> 666" transformWith Desugaring) // FIXME (names of multi-param lambda...)
      (ir"((x: Int, y: Double) => bar(foo(x))(y))(42, .5) -> 666" transformWith Desugaring)
    
    ir"swap(foobar(42,.5),11)('ok)" transformWith Desugaring eqt
      ir"((x: (AnyVal,AnyVal)) => (name: Symbol) => name -> x.swap)(${
        ir"${Emb.Object.Defs.foobar.value}(42,.5)" transformWith Desugaring
      }, 11)('ok)"
    
  }
  
  test("Online Lowering") {
    //object TestDSL2 extends SimpleAST with ClassEmbedding with OnlineOptimizer with Lowering {
    object DSLBase extends SimpleAST with OnlineDesugaring {
      override val warnRecursiveEmbedding = false
      embed(MyClass)
    }
    import DSLBase.Predef._
    import MyClass._
    
    eqtWith(ir"foo(42) * 2",
      ir"{val arg = 42; foo(arg.toLong).toInt} * 2")(_ =~= _)
    assert(ir"foo(42.toLong) * 2" =~=
      ir"foo(42.toLong) * 2")
    eqtWith(ir"recLol(42)",
      //ir"((x: Int) => if (x <= 0) 0 else recLol(x-1)+1)(42)")(_ =~= _) // does not work, as it inlines `recLol`!
      ir"((x: Int) => if (x <= 0) 0 else ${
        import base._
        import RuntimeUniverseHelpers.sru
        `internal IR`[Int,{val x: Int}](rep(
          MethodApp(
            staticModule("scp.classembed.MyClass"),
            sru.typeOf[MyClass.type].member(sru.TermName("recLol")).asMethod,
            Nil,
            Args(ir"($$x:Int)-1".rep)::Nil,
            typeRepOf[Int])
        ))
      }+1)(42)")(_ =~= _)
    
    ir"recLolParam(42)('ko)" match {
      case ir"((x: Int) => (r: Symbol) => if (x <= 0) $a else $b : Symbol)(42)('ko)" =>
    }
    
  }
  
  test("Geom") {
    import geom._
    object DSLBase extends SimpleAST with OnlineDesugaring {
      embed(Vector, Matrix)
    }
    import DSLBase.Predef._
    
    println(ir"Vector.origin(3).arity") // TODO
    
    
  }
  
}
