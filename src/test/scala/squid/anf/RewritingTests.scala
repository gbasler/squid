package squid
package anf

import utils._
import ir._

/**
  * Created by lptk on 02/02/17.
  * 
  * TODO port some of the seq matching tests from scBackend
  * 
  */
class RewritingTests extends MyFunSuite(SimpleANFTests.DSL) {
  import DSL.Predef._
  
  test("Rewriting simple expressions only once") {
    
    val a = ir"println((50,60))"
    val b = a rewrite {
      case ir"($x:Int,$y:Int)" =>
        ir"($y:Int,$x:Int)"
      case ir"(${Const(n)}:Int)" => Const(n+1)
    }
    b eqt ir"println((61,51))"
    
  }
  
  test("Rewriting Sequences of Bindings") {
    
    val a = ir"val aaa = readInt; val bbb = readDouble.toInt; aaa+bbb"  // FIXMElater: why isn't name 'bbb' conserved?
    val b = a rewrite {
      case ir"readDouble.toInt" =>
        ir"readInt"
    }
    b eqt ir"val aa = readInt; val bb = readInt; aa+bb"
    
    {
      val a = ir"val a = 11.toDouble; val b = 22.toDouble; val c = 33.toDouble; (a,b,c)"
      val c = a rewrite {
        case ir"val a = ($x:Int).toDouble; val b = ($y:Int).toDouble; $body: $bt" =>
          ir"val a = ($x+$y).toDouble/2; val b = a; $body"
      }
      c eqt ir"val a = (11+${ir"22"}).toDouble/2; val c = 33.toDouble; (a,a,c)"
      
      /*
      // TODO make this work like the one above
      val d = a rewrite {
        case ir"val a = ($x:Int).toDouble; ($y:Int).toDouble" =>
          ir"($x+$y).toDouble/2"
      }
      println(d)
      //d eqt ir"val a = (11+${ir"22"}).toDouble/2; val c = 33.toDouble; (a,a,c)"
      */
    }
    
    
  }
  
  test("Rewriting Sequences of Effects") {
    
    val c0 = ir"print(1); print(2); print(3); print(4)"
    
    c0 rewrite {
      case ir"print(1); print(2)" => ir"print(12)"
    } eqt ir"print(12); print(3); print(4)"
    
    c0 rewrite {
      case ir"print(2); print(3)" => ir"print(23)"
    } eqt ir"print(1); print(23); print(4)"
    
    c0 rewrite {
      case ir"print(3); print(4)" => ir"print(34)"
    } eqt ir"print(1); print(2); print(34)"
    
  }
  
  test("Rewriting Trivial Arguments") {
    
    val a = ir"val a = 0.toDouble; val b = 0.toDouble; val c = 0.toDouble; (a,b,c)"
    val b = a rewrite {
      case ir"(${Const(n)}:Int).toDouble" => ir"${Const(n+1)}.toDouble"
    }
    b eqt ir"val a = 1.toDouble; val b = 1.toDouble; val c = 1.toDouble; (a,b,c)"
    
  }
  
  test("Rewriting named effects") {
    
    def f(x: IR[_,{}]) = x rewrite { case ir"readInt; readInt" => ir"42" }
    ir"val n = readInt; val m = readInt; println" |> f eqt ir"println"
    ir"val n = readInt; val m = readInt; println(m)" |> f eqt ir"println(42)"
    ir"val n = readInt; val m = readInt; println(m+n)" |> (a => a |> f eqt a)
    
    // Should also work with early return!
    def g(x: IR[_,{}]) = x rewrite { case ir"readInt; readInt" => Return(ir"readInt; readInt+1") }
    ir"val n = readInt; val m = readInt; print(n+m)" |> (a => a |> g eqt a)
    ir"val n = readInt; val m = readInt; print(n+m); readInt; readInt" |> g eqt
      ir"val n = readInt; val m = readInt; print(n+m); readInt; readInt+1"
    
  }
  
  test("Named rewriting of effects") {
    
    def f(x: IR[_,{}]) = x rewrite { case ir"val n = readInt; $body: $bt" => ir"val n = ???; $body" }
    ir"println; readInt; println" |> f eqt ir"println; val lol = ???; println"
    //ir"println; readInt; println" |> f eqt ir"println; ???; println" // TODO allow this; make repEq use toBlock...?
    
    def g(x: IR[_,{val n:Int;val m:Int}]) = x rewrite { case ir"val n = readInt; n+1" => ir"readDouble.toInt" }
    ir"println(readInt+1)" |> g eqt ir"println(readDouble.toInt)"
    ir"val n = readInt; println(n+1)" |> g eqt ir"println(readDouble.toInt)"
    ir"readInt; (m?:Int)+1" |> (a => a |> g eqt a)
    ir"readInt; (n?:Int)+1" |> (a => a |> g eqt a)
    ir"val x = readInt; (n?:Int)+1" |> (a => a |> g eqt a)
    
  }
  
  test("Soundly Swallowing Bindings or Not") {
    
    def f(x: IR[_,{}]) = x rewrite { case ir"readInt; $body: $bt" => body }
    ir"readInt;1" |> f eqt ir"1"
    ir"readInt+1" |> (a => a |> f eqt a)
    
    def fe(x: IR[_,{}]) = x rewrite { case ir"readInt; $body: $bt" => Return(body) }
    ir"readInt;1" |> fe eqt ir"1"
    ir"readInt+1" |> (a => a |> fe eqt a)
    
    def g(x: IR[_,{}]) = x rewrite { case ir"readInt; println($x)" => ir"print($x)" }
    ir"val ri = readInt; println(ri); ri+1" |> (a => a |> g eqt a)
    ir"val ri = readInt; println(ri); 42" |> (a => a |> g eqt a)
    ir"val ri = readInt; println(123); 42" |> g eqt ir"print(123); 42"
    ir"val ri = readInt; println(123)" |> g eqt ir"print(123)"
    ir"val ri = readInt; println(ri)" |> (a => a |> g eqt a)
    ir"readInt; println(0)" |> g eqt ir"print(0)"
    ir"readInt; println(0); 1" |> g eqt ir"print(0); 1"
    
    def ge(x: IR[_,{}]) = x rewrite { case ir"readInt; println($x)" => Return(ir"print($x)") }
    ir"val ri = readInt; println(ri); ri+1" |> (a => a |> ge eqt a)
    ir"val ri = readInt; println(ri); 42" |> (a => a |> ge eqt a)
    ir"val ri = readInt; println(123); 42" |> ge eqt ir"print(123); 42"
    ir"val ri = readInt; println(123)" |> ge eqt ir"print(123)"
    ir"val ri = readInt; println(ri)" |> (a => a |> ge eqt a)
    ir"readInt; println(0)" |> ge eqt ir"print(0)"
    ir"readInt; println(0); 1" |> ge eqt ir"print(0); 1"
    
  }
  
  
  ///*
  test("Pure Expression Rewriting") {
    
    def f(r: IR[Any,{}]) = r rewrite {
      case ir"($v:squid.lib.Var[Option[$t]]).!.isDefined" => 
        ir"$v.!.nonEmpty"
    }
    
    /*
    // The difficulty is since `isDefined` is pure, we end up with `val x = s.! ; println(x.isDefined)`,
    // which the pattern cannot not match without some magic behind the scenes.
    
    ir"var s: Option[Int] = None; println(s.isDefined)" |> f eqt
      ir"var s: Option[Int] = None; println(s.nonEmpty)"
    
    ir"var s: Option[Int] = None; println(s.isDefined); s" |> f eqt
      ir"var s: Option[Int] = None; println(s.nonEmpty); s"
    
    ir"var s: Option[Int] = None; println(s.isDefined); println(s.isDefined)" |> f eqt
      ir"var s: Option[Int] = None; println(s.nonEmpty); println(s.nonEmpty)"
    */
    
    // TODO try interleaving
    //println(ir"var s: Option[Int] = None; println(s); println(s.isDefined)" |> f)
    ir"var s: Option[Int] = None; println(s); println(s.isDefined)" |> f eqt
      ir"var s: Option[Int] = None; println(s); println(s.nonEmpty)"
    //println(ir"var s: Option[Int] = None; println(s); s = Some(42); println(s.isDefined)" |> f)
    ir"var s: Option[Int] = None; println(s); s = Some(42); println(s.isDefined)" |> f eqt
      ir"var s: Option[Int] = None; println(s); s = Some(42); println(s.nonEmpty)"
    
    def g(r: IR[Any,{}]) = r rewrite {
      case ir"($v:squid.lib.Var[Option[$t]]).!.fold($d)(identity)" => 
        ir"$v.!.getOrElse($d)"
    }
    
    //println(ir"var s: Option[Int] = None; println(s.fold(42)(identity))" |> g)
    
    // by name, so no problem:
    //println(ir"var s: Option[Int] = None; println(s.fold(readInt)(identity))" |> g)
    
    //def h(r: IR[Any,{}]) = r rewrite {
    def h(r: IR[_,{}]) = r rewrite {
      //case ir"($v:squid.lib.Var[Option[$t]]).!.map($f).flatten" =>  // FIXME no "inferred `Nothing`" warning? 
      //case ir"($v:squid.lib.Var[Option[$t]]).!.map[Option[$mt]]($f).flatten" => 
      //case ir"($v:squid.lib.Var[Option[$t]]).!.map[$mt]($f).flatten($ev)" => // FIXME no warning
      case ir"($v:squid.lib.Var[Option[$t]]).!.map[$mt]($f).flatten[$ft]($ev)" => // FIXME no warning
        //ir"$v.!.flatMap($f)"
        ir"$v.!.flatMap($f andThen $ev)"
    }
    
    //base debugFor 
    //println(ir"var s: Option[Int] = None; println(s.map(Some(_)).flatten)" |> h)
    
    //println(ir"var s: Option[Int] = None; println(s.map(Some(_)).flatten[Int])" |> h)
    ir"var s: Option[Int] = None; println(s.map(Some(_)).flatten)" |> h eqt  // Q: or `flatten[Int]` ?
      ir"var s: Option[Int] = None; println(s.flatMap( (Some(_:Int)).andThen[Option[Any]](scala.Predef.$$conforms[Some[Int]]) ))"
    // ^ FIXME (I thin kthis used to work at some point...)
    
    ir"var s: Option[Int] = None; println(s.map(Option(_)).flatten[Int])" |> h eqt  // Q: or `flatten[Int]` ?
      //ir"var s: Option[Int] = None; println(s.flatMap( (Option(_:Int)) andThen scala.Predef.$$conforms[Some[Int]] ))"
      ir"var s: Option[Int] = None; println(s.flatMap( (Option(_:Int)) andThen scala.Predef.$$conforms[Option[Int]] ))"
    
    //println(ir"var s: Option[Int] = None; println(s.map(??? : Int => Option[Int]).flatten[Int])" |> h)
    //ir"var s: Option[Int] = None; println(s.map(??? : Int => Option[Int]).flatten[Int])" |> h eqt
    //  ir"var s: Option[Int] = None; println(s.flatMap( (??? : Int => Option[Int]) andThen scala.Predef.$$conforms[Option[Int]] ))"
    
    val a = ir"var s: Option[Int] = None; println(s.map(??? : Int => Option[Int]).flatten[Int])" |> h
    val b = ir"var s: Option[Int] = None; println(s.flatMap( (??? : Int => Option[Int]) andThen scala.Predef.$$conforms[Option[Int]] ))"
    // FIXME: currently fail! because of ??? matching and extraction of covariant function parameter type...
    //base debugFor (a eqt a)
    //base debugFor (a eqt b)
    //base debugFor (b eqt b)
    
    ir"var s: Option[Int] = None; println(s.map( genie[Int => Option[Int]] ).flatten[Int])" |> h eqt
      ir"var s: Option[Int] = None; println(s.flatMap( genie[Int => Option[Int]] andThen scala.Predef.$$conforms[Option[Int]] ))"
    
    //println(ir"var s: Option[Int] = None; println(s.map(Some(_)))" |> h)
    
    
    // TODO ^ try with effectful andThen ...
    
    // TODO ^ try several calls with difft params
    
    
    def i(r: IR[_,{}]) = r rewrite {
      case ir"($v:squid.lib.Var[Option[$t]]).!.map[$mt]($f)" =>
        //ir"None"
        ir"None map $f"
      case ir"($v:squid.lib.Var[Option[$t]]).!.filter($f)" =>
        ir"println($f); None"
      case ir"($v:squid.lib.Var[Option[$t]]).!.foreach[$ft]($f)" =>
        ir"println($f)"
      case ir"($v:squid.lib.Var[Option[Int]]).!.get" => ir"42"
    }
    //println(ir"var s: Option[Int] = None; println(s.map(_+1)); println(s.map(_+2))" |> i)
    ir"var s: Option[Int] = None; println(s.map(_+1)); println(s.map(_+2))" |> i eqt
      ir"var s: Option[Int] = None; println(None.map((_:Int)+1)); println(None.map((_:Int)+2))"
    
    // FIXedME
    /*println*/(ir"var s: Option[Int] = None; println(s.filter(genie[Int=>Bool]))" |> i)
    /*println*/(ir"var s: Option[Int] = None; println(s.foreach(genie[Int=>Bool]))" |> i)
    
    println(ir"var s: Option[Int] = None; val x = s; x.get + x.get" |> i)
    
    
    
  }
  
  test("Dang") {
    import squid.lib.Var
    
    //val c = ir"var s: Option[Int] = None; val x = s; x.get + x.get"
    //println(c rewrite {
    def f(c: IR[Any,{}]) = c rewrite {
      case ir"val $x = ($v:Var[Option[Int]]).! ; $body: $bt" =>
        val body2 = body rewrite {
          //case ir"($$x).get" => ir"$v.!.getOrElse(???)"
          //case ir"($$x).get" => ir"$x.getOrElse(???)"  // nope
          case ir"($$x).get" => ir"(x? : Option[Int]).getOrElse(???)"  // nope
        } //subs
        ir"val x = $v.!; $body2"
        
    }
    //}) 
    
    ir"var s: Option[Int] = None; val x = s; x.get + x.get" |> f |> println
    ir"var s: Option[Int] = None; val x = s; x.get + x.size" |> f |> println
    
    
    // FIXME this should rewrite, introducing a FV `y` ...
    /*
    println(ir"val x = readInt; x+1" rewrite {
      case ir"val y = readInt; $body: $bt" => body
    })
    */
    
  }
  //*/
  
}
