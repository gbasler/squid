package scp
package ir2

import org.scalatest.FunSuite

import lang2._
import quasi2._
import scala.reflect.runtime.{universe => sru}
import MacroTesters._

/** Due to reflection problems, I've commented some of these tests because they sometimes crash the compiler (and sometimes not!) */
class StaticExecTests extends FunSuite {
  
  type AST = SimpleAST
  
  def same[A](xy: (A, A)) = assert(xy._1 == xy._2)
  //def same(cond: Boolean) = assert(cond)
  
  test("Constants") {
    
    same( staticExecAsConst[AST]{ 42 } -> 42 )
    // ie:
    same( staticExecAndSource[AST]{ 42 } )
    
    same( staticExecAndSource[AST]{ "ok" } )
    
    same( staticExecAndSource[AST]{ 'c' } )
    
    //same( staticExecAndSource[AST]{ 'Cool.hashCode } ) // FIXME: lol, enabling this makes { "ok".take(1)+"ko" } fail
    
  }
  
  test("Basic") {
    
    same(staticExecAndSource[AST]{ "ok".reverse } )
    same(staticExecAndSource[AST]{ "ok".take(1)+"ko" } )
    same(staticExecAndSource[AST]{ {0 -> 1}.swap.hashCode } )
    
  }
  
  test("Bindings") {
    
    same(staticExecAndSource[AST]{ val x = 0; x + 1 } )
    same(staticExecAndSource[AST]{ ((x: Int) => x + 1)(42) } )
    same(staticExecAndSource[AST]{ { x: Int => x + 1}.apply(42) } )
    
  }
  
  ///*
  test("Variables") {
    
    //same(staticExecAndSource[AST]{ lib.Var(0) } ) // FIXME
    same(staticExecAndSource[AST]{ var x = ("ok" + "ko".reverse).length; x-=1; (x+=1, x, 'lol).hashCode } )
    
  }
  
  test("Java") {
    
    // overloading
    same(staticExecAndSource[AST]{ "ok".indexOf('k'.toInt) })
    same(staticExecAndSource[AST]{ "ok".indexOf('k') })
    same(staticExecAndSource[AST]{ "okok".indexOf("ok") })
    same(staticExecAndSource[AST]{ "okok".lastIndexOf("ok") })
    same(staticExecAndSource[AST]{ String.valueOf(true) } )
    
    //same(staticExecAndSource[AST]{ "ok"+String.valueOf("ko") } ) // FIXME 2-way cache compiler bug
    //same(staticExecAndSource[AST]{ ("ok" + "ko"*2).length }) // FIXME
    
  }
  //*/
  
  
}














