package scp
package classembed

import scp.ir2._
import scp.utils.meta.RuntimeUniverseHelpers
import utils._

class ClassEmbeddingTests extends MyFunSuite2 {
  import TestDSL2.Predef._
  TestDSL2 embed MyClass
  
  test("Basics") {
    
    val Emb = MyClass.embedIn(TestDSL2)
    
    Emb.Object.Defs.foo.value eqt ir"(arg: Int) => MyClass.foo(arg.toLong).toInt"
    
    Emb.Object.Defs.swap[Int] eqt ir"(arg: (Int,Int)) => (n: Symbol) => n -> arg.swap"
    
    Emb.Class.Defs.foo.value eqt Emb.Class.Defs.foz.value
    
    Emb.Class.Defs.foo.value eqt ir"(mc: MyClass) => (arg: Int) => mc.baz + arg"
    
  }
  
  test("Basic Lowering") {
    
    val Desugaring = new TestDSL2.Desugaring with TopDownTransformer
    val BindNorm = new TestDSL2.SelfTransformer with BindingNormalizer with TopDownTransformer // or should it use `BottomUpTransformer`?
    
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
    
    
    
    val desugared = ir"val mc = new MyClass; mc foz 42" transformWith Desugaring
    
    desugared eqt ir"""{
      val mc_0: scp.classembed.MyClass = new scp.classembed.MyClass();
      ({
        ( (__self_1: scp.classembed.MyClass) =>
            ((x_2: scala.Int) => __self_1.baz.+(x_2)) ) (mc_0)
      })(42)
    }"""
    /* ^ Note: the printed transformed tree is of the form below, but it needs binding normalization to compare equivalent: */
    val result = ir"""{
      val mc_0: scp.classembed.MyClass = new scp.classembed.MyClass();
      ({
        val __self_1: scp.classembed.MyClass = mc_0;
        ((x_2: scala.Int) => __self_1.baz.+(x_2))
      })(42)
    }"""
    
    val desugaredNormalized = desugared transformWith BindNorm
    
    desugaredNormalized eqt result
    
    desugaredNormalized eqt ir"""{
      val mc_0: scp.classembed.MyClass = new scp.classembed.MyClass();
      val a_1: scp.classembed.MyClass = mc_0;
      val b_2: Int = 42;
      a_1.baz.+(b_2)
    }"""
    
  }
  
  test("Online Lowering") {
    object DSLBase extends SimpleAST with OnlineDesugaring {
      override val warnRecursiveEmbedding = false
      embed(MyClass)
    }
    import DSLBase.Predef._
    import MyClass._
    
    eqtBy(ir"foo(42) * 2",
      ir"{val arg = 42; foo(arg.toLong).toInt} * 2")(_ =~= _)
    assert(ir"foo(42.toLong) * 2" =~=
      ir"foo(42.toLong) * 2")
    
    // FIXME now (that StaticOptimizer uses calls to postProcess) this makes a StackOverflow; TODO properly detect it...
    /*
    eqtBy(ir"recLol(42)",
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
    */
    
    ir"recLolParam(42)('ko)" match {
      case ir"((x: Int) => (r: Symbol) => if (x <= 0) $a else $b : Symbol)(42)('ko)" =>
    }
    
  }
  
  test("Online Lowering and Normalization") {
    
    //object DSLBase extends SimpleAST with OnlineDesugaring with BindingNormalizer { // Error:(28, 12) object DSLBase inherits conflicting members
    object DSLBase extends SimpleAST with OnlineOptimizer { self =>
      object BindingNormalizer extends SelfTransformer with BindingNormalizer
      def pipeline = Desugaring.pipeline andThen BindingNormalizer.pipeline
      embed(MyClass)
    }
    import DSLBase.Predef._
    import MyClass._
    
    eqtBy(ir"swap(foobarCurried(42)(.5),11)('ok)", ir"""{
      val a_0: scala.Tuple2[scala.Double, scala.Int] = scala.Tuple2.apply[scala.Double, scala.Int]({
        val a_1: Int = 42;
        val b_2: Double = 0.5;
        scp.classembed.MyClass.bar({
          val x_3: scala.Int = a_1;
          scp.classembed.MyClass.foo(x_3.toLong).toInt
        })(b_2)
      }, 11);
      val b_4: scala.Symbol = scala.Symbol.apply("ok");
      scala.Predef.ArrowAssoc[scala.Symbol](b_4).->[scala.Tuple2[scala.AnyVal, scala.AnyVal]](a_0.swap)
    }""")(_ =~= _)
    
  }
  
  test("Geom") {
    import geom._
    object DSLBase extends SimpleAST with OnlineDesugaring {
      embed(Vector, Matrix)
    }
    import DSLBase.Predef._
    
    eqtBy(ir"Vector.origin(3).arity", ir"val v = Vector.origin(3); v.coords.length")(_ =~= _)
    
    // TODO (vararg)
    //println(ir"Vector.apply(1,2,3)")
    //val Emb = Vector.EmbeddedIn(TestDSL2); println(Emb.Object.Defs.apply.value)
    
  }
  
}
