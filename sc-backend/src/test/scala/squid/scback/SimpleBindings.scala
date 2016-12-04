package squid.scback

import squid.utils._
import ch.epfl.data.sc._
import pardis._
import squid.MyFunSuiteTrait
import squid.ir.SimpleAST
import squid.scback.PardisBinding.DefaultPardisMixin

object SimpleBindings {
  
  class Test {
    def foo[A <: Seq[Int]](a:A):A = ???
  }
  class Test2[B <: Seq[Double]] {
    def foo2[A <: Seq[Int]](a:A,b:Option[B]):(A,B) = ???
  }
  class Test3 {
    def foo3 = ???
  }
  
}

class SimpleBindings extends MyFunSuiteTrait {
  import SimpleBindings._
  implicit val tpi = types.IntType
  
  val DSL: sqd.type = sqd
  
  object sc extends ir.Base with DefaultPardisMixin {
    implicit class Test(a:Rep[SimpleBindings.Test]) {
      def foo[A <: Seq[Int]](a:Rep[A])(implicit tp: sc.TypeRep[A]):Rep[A] = ???
    }
    implicit class Test2[B <: Seq[Double]](a:Rep[SimpleBindings.Test2[B]])(tp: sc.TypeRep[B]) {
      def foo2[A <: Seq[Int]](a:Rep[A],b:Rep[Option[B]])(implicit tp: sc.TypeRep[A]):Rep[(A,B)] = unit(42).asInstanceOf[Rep[(A,B)]]
    }
    
    // Note: this will actually load the symbol: loadTypSymbol("squid.scback.SimpleBindings#sc$#Test2")
    def __newTest2[A <: Seq[Double]](implicit tp: sc.TypeRep[A]): Rep[Test2[A]] = ???
    // correct version:
    //def __newTest2[A <: Seq[Double]](implicit tp: sc.TypeRep[A]): Rep[SimpleBindings.Test2[A]] = ???
    
    implicit class Test3(a:Rep[SimpleBindings.Test3]) {
      def foo3 = ???
    }
    
    type `ignore squid.scback.SimpleBindings.Test3`
  }
  
  /*_*/
  
  object sqd extends SimpleAST
  
  val ab = AutoBinder(sc, sqd)
  //val ab = AutoBinder.dbg(sc, sqd)
  
  import sqd.Predef._
  
  test("Constructing a call to SimpleBindings.Test2.foo2") {
    
    import squid.utils.meta.{RuntimeUniverseHelpers => ruh}
    import scala.reflect.runtime.{universe => sru}
    import ab.coerceTypeRep
    
    val tp = sqd.loadTypSymbol(ruh.encodedTypeSymbol(sru.typeOf[SimpleBindings.Test2[_]].typeSymbol.asType))
    val mk = ab.map(sqd.loadMtdSymbol(tp, "foo2", None))
    val z = sc.unit(0)//(types.IntType)
    val ls = z :: z :: z :: Nil
    val tls: List[sc.TypeRep[Any]] = List(types.IntType, types.IntType)
    //val bl = sc.reifyBlock( mk(ls,tls,tls) )(types.AnyType)
    assert(mk(ls,tls,tls) == sc.unit(42))
    
  }
  
  
  
}
