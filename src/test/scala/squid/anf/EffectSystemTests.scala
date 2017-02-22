package squid
package anf

import utils._
import ir._
import squid.lib.{transparent, transparencyPropagating}

/**
  * Created by lptk on 22/02/17.
  */
class EffectSystemTests extends MyFunSuite(SimpleANFTests.DSLWithEffects) {
  import DSL.Predef._
  import EffectSystemTests._
  import SimpleEffect._
  
  def stmts(q: IR[Any,Nothing]) = q.rep.asBlock._1
  def stmtsNumIs(n: Int)(q: IR[Any,Nothing]) = assert(stmts(q).size == n)
  
  test("Basics") {
    
    assert(ir"42".rep.effect == Pure)
    assert(ir"42.toDouble".rep.effect == Pure)
    assert(ir"println".rep.effect == Impure)
    
    assert(ir"val n = 42; val m = n+1; println(m); 'ok".rep.asBlock._1.size == 1)
    
    ir"true && {identity(42.toDouble);true} && false" eqt ir"(true:Bool) && true && false"
    eqt(ir"true && {println;true} && false".rep.asBlock._1.head.fold(_._2,identity), ir"true && {println;true}".rep)
    
    assert(ir"IndexedSeq().size + Nil.size + Seq().size".rep.asBlock._1.size == 3)
    // ^ currently, `.size` on immutable collections is not viewed as pure; this is more tricky because `size` is defined
    // in a generic class, and so should only be pure if the receiver is an immutable collection... not expressible withb current infra
    
  }
  
  test("Lambdas") {
    
    assert(ir"(x:Any) => println".rep.effect == Latent)
    assert(ir"(() => println):(() => Any)".rep.effect == Latent)
    
    assert(ir"applyOnce0(() => println)".rep.effect == Impure)
    assert(ir"applyOnce1((x:Any) => println)".rep.effect == Impure)
    assert(ir"applyTwice0(() => () => println)".rep.effect == Impure)
    assert(ir"applyTwice1((x:Any) => (y:Any) => println)".rep.effect == Impure)
    
    assert(ir"(x:Any) => (x:Any) => println".rep.effect == Latent)
    assert(ir"((x:Any) => (x:Any) => println)(1)".rep.effect == Latent) // that happens because this is a let-binding!! (otherwise, should be `Impure`)
    assert(ir"((x:Any) => (x:Any) => println)(1)(2)".rep.effect == Impure)
    
    assert(ir"() => println".rep.effect == Latent)
    assert(ir"() => () => println".rep.effect == Latent)
    assert(ir"(() => () => println)()".rep.effect == Impure)
    assert(ir"(() => () => println)()()".rep.effect == Impure)
    
    // thanks to an add-hoc entry in `transparencyPropagatingMtds`:
    assert(ir"identity((x:Int)=>print(x))".rep.effect == Latent)
    assert(ir"identity((x:Int)=>x)".rep.effect == Pure)
    
  }
  
  test("Options") {
    
    assert(ir"Some(Some(42)).get.get".rep.asBlock._1.isEmpty)
    
    assert(ir"val a = Option; a.empty[Int]".rep.asBlock._1.isEmpty)
    
    assert(ir"Right(true)".rep.asBlock._1.isEmpty)
    // ^ both `scala.`package`.Right` and `scala.`package`.Right.apply(true)` successfully viewed as trivial by ANF
    
    // `???` scheduled before `s.map`, which makes transfos not straightforward! cf. RewritingTests
    ir"var s: Option[Int] = None; s.map[Int](???)" eqt ir"""{
      var s_0: scala.Option[scala.Int] = scala.None;
      val x_1 = s_0;
      val x_2 = ((scala.Predef.???): Nothing);
      x_1.map[scala.Int](x_2)
    }"""
    
    // Those are effectful and should not be elided!
    assert(ir"Some(42).map(a => println(a)); ()".rep.asBlock._1.size == 1)
    assert(ir"Some(()).getOrElse(println); 42".rep.asBlock._1.size == 1)
    
  }
  
  test("From Annotations") {
    
    ir"foo + foo" |> stmtsNumIs(0)
    ir"myId((x:Int) => x+foo)" |> stmtsNumIs(0)
    ir"myId((x:Int) => x+foo)(foo)" |> stmtsNumIs(0)
    ir"myId((x:Int) => x+foo)(readInt)" |> stmtsNumIs(1)
    ir"myId((x:Int) => {print(x);x})" |> stmtsNumIs(0) // returned expression is impure
    ir"myId((x:Int) => {print(x);x}); 42" eqt ir"42"   // expr with latent effect is ignored
    ir"val mid = myId((x:Int) => {print(x);x}); mid(42)" |> stmtsNumIs(0)
    ir"val mid = myId((x:Int) => {print(x);x}); mid(readInt)" |> stmtsNumIs(1)
    
  }
  
}
object EffectSystemTests {
  def applyOnce0(f: () => Unit) = f()
  def applyOnce1(f: Any => Unit) = f(())
  def applyTwice0(f: () => () => Unit) = f()()
  def applyTwice1(f: Any => Any => Unit) = f(())(())
  
  @transparent
  def foo = 42
  
  @transparencyPropagating
  def myId(f: Int => Int): Int => Int = f
  
}
