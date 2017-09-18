package example.doc

import squid.lib.transparencyPropagating
import squid.lib.transparent

object IntermediateRepresentations extends App {
  
  import squid.ir.{SchedulingANF, StandardEffects}
  
  class Baz { @transparencyPropagating def baz: Baz = this }
  
  case class Foo() { def foo = 123 }
  case class Bar() { def bar = 456 }
  
  object Embedding extends SchedulingANF with StandardEffects {
    transparentMtds += methodSymbol[Foo]("foo")
    transparentMtds += methodSymbol[Foo.type]("apply")
    transparentTyps += typeSymbol[Bar]
    transparentTyps += typeSymbol[Bar.type]
  }
  {
    import Embedding.Predef._
    println(code"(new Foo).foo + 1") // let-binds
    println(code"Foo().foo + 1") // does not let-bind
    println(code"(new Bar).bar + 1") // does not let-bind
    println(code"(b:Baz) => b.baz.baz.baz") // does not let-bind
  }
  
}
