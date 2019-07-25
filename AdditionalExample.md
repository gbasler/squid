## Another Short Example

To give a quick taste of Squid's capabilities,
here is a very basic example of program manipulation.
The full source code can be [found here, in the example folder](https://github.com/epfldata/squid/tree/master/example/src/main/scala/example/doc/IntroExample.scala).

Assume we define some library function `foo` as below:

```scala
@embed object Test {
  @phase('MyPhase) // phase specification helps with mechanized inlining
  def foo[T](xs: List[T]) = xs.head
}
```

The `@embed` annotation allows Squid to the see the method implementations inside an object or class, so that they can be inlined later automatically (as shown below) 
–– note that this annotation is _not_ required in general, as non-annotated classes and methods can also be used inside quasiquotes.

What follows is an example REPL session demonstrating some program manipulation
using Squid quasiquotes, 
transformers and first-class term rewriting:

```scala
// Syntax `code"t"` represents term `t` in some specified intermediate representation
> val pgrm0 = code"(Test.foo(1 :: 2 :: 3 :: Nil) + 1).toDouble"
pgrm0: ClosedCode[Double] = code"""
  val x_0 = Test.foo[scala.Int](scala.collection.immutable.Nil.::[scala.Int](3).::[scala.Int](2).::[scala.Int](1));
  x_0.+(1).toDouble
"""
// ^ triple-quotation """ is for multi-line strings

// `Lowering('P)` builds a transformer that inlines all functions marked with phase `P`
// Here we inline `Test.foo`, which is annotated at phase `'MyPhase`
> val pgrm1 = pgrm0 transformWith (new Lowering('MyPhase) with BottomUpTransformer)
pgrm1: ClosedCode[Double] = code"scala.collection.immutable.Nil.::[scala.Int](3).::[scala.Int](2).::[scala.Int](1).head.+(1).toDouble"

// Next, we perform a fixed-point rewriting to partially-evaluate
// the statically-known parts of our program:
> val pgrm2 = pgrm1 fix_rewrite {
    case code"($xs:List[$t]).::($x).head" => x
    case code"(${Const(n)}:Int) + (${Const(m)}:Int)" => Const(n + m)
  }
pgrm2: ClosedCode[Double] = code"2.toDouble"

// Finally, let's runtime-compile and evaluate that program!
> pgrm2.compile
res0: Double = 2.0
```

Naturally, this simple REPL session can be generalized into a proper domain-specific compiler that will work on any input program 
(for example, see the stream fusion compiler [[2]](#gpce17)).

It is then possible to turn this into a static program optimizer,
so that writing the following expression 
expands at compile time into just `println(2)`,
as show in the
[IntroExampleTest](https://github.com/epfldata/squid/tree/master/example/src/test/scala/example/doc/IntroExampleTest.scala#L25)
file:

```scala
MyOptimizer.optimize{ println(Test.foo(1 :: 2 :: 3 :: Nil) + 1) }
```

We could also turn this into a dedicated [Squid macro](#smacros),
an alternative to the current Scala-reflection macros.

