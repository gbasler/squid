# **Squid** ― type-safe metaprogramming for Scala

![stability-unstable](https://img.shields.io/badge/stability-unstable-yellow.svg)
[![Join the chat at https://gitter.im/epfldata-squid/Lobby](https://badges.gitter.im/epfldata-squid/Lobby.svg)](https://gitter.im/epfldata-squid/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)



## Introduction

**Squid** (for the approximative contraction of **Sc**ala **Qu**ot**ed** **D**SLs)
is a **metaprogramming** framework 
that facilitates the **type-safe** manipulation of **Scala programs**.
Squid extends multi-stage programming capabilities with support for code inspection and code transformation. It has special support for 
**library-defined optimizations** [[2]](#gpce17) and 
helps with the compilation of **domain-specific languages** (DSL) embedded in Scala [[1]](#scala17).
Squid uses **advanced static typing** techniques to prevent common metaprogramming errors, such as scope extrusion [[3]](#popl18).

[Link to the Squid tutorial.](http://epfldata.github.io/squid/tutorial)

<!-- TODO: give concrete application examples to pique curiosity/generate interest -->

**Caution:** Squid is still experimental, and the interfaces it exposes may slightly change in the future. This applies especially to the semi-internal interfaces used to implement intermediate representation backends (the `Base` trait and derived).


<a name="early-example">

## An Early Example

To give a quick taste of Squid's unique mix of capabilities,
here is a basic (contrived!) example of program manipulation.
The full source code can be [found here, in the example folder](https://github.com/LPTK/Squid/blob/master/example/src/main/scala/example/PowOptim.scala).
Much more detailed explanations of each feature can be found in the [tutorial](http://epfldata.github.io/squid/tutorial).

The core of Squid's frontend is its type- and scope-safe quasiquotation engine.
We can write program fragments, or _code values_, by using the `code` interpolator:

```scala
import scala.io.StdIn

val c0 = code"Some(StdIn.readDouble)" // resolves types and imports statically

println(c0) // code"scala.Some.apply[scala.Double](scala.io.StdIn.readDouble())"
```

We can then compose programs incrementally from smaller parts:

```scala
val c1: ClosedCode[Double] = code"$c0.get"
// ClosedCode[T], an alias for Code[T,Any], is the type of closed terms

println(c1) // code"scala.Some.apply[scala.Double](scala.io.StdIn.readDouble()).get"

// A function that manipulates code fragments in context/scope C:
def mkPow[C](b: Code[Double,C], e: Code[Double,C]) = code"Math.pow($b, $e)"

val c2: ClosedCode[Double] = mkPow(c1, code"3")

// method `=~=` tests for alpha-equivalence of code values:
assert(c2 =~= code"java.lang.Math.pow(Some(StdIn.readDouble).get, 3.0)")
```

Now, let us define a `Math.pow` optimizer.
We'll do a top-down rewriting until a fixed point is reached,
thanks to the `fix_topDown_rewrite` method:

```scala
def optPow[T,C](pgrm: Code[T,C]): Code[T,C] = pgrm fix_topDown_rewrite {
  case code"Math.pow($x, 0.0)"       => code"1.0"
  case code"Math.pow($x, ${Const(d)})" // `Const` matches constants, so `d` has type Double
    if d.isWhole && 0 < d && d < 16  => code"Math.pow($x, ${Const(d-1)}) * $x"
}
```

And finally, let us build a compiler for fast power functions!
Given an integer `n`, we return a function that has been runtime-compiled to multiply any number with itself `n` times, using only plain multiplications:

```scala
def mkFastPow(n: Int): Double => Double = {
  val powCode = code"${ (x: Variable[Double]) => mkPow(code"$x", Const(n)) }"
  // ^ The line above lifts a function of type `(v: Variable[Double]) => Code[Double, v.Ctx]`
  //   into a program fragment of type `ClosedCode[Double => Double]`
  val powCodeOpt = optPow(powCode) // optimize our pow function
  powCodeOpt.compile // produce efficient bytecode at runtime
}
val p4fast = mkFastPow(4) // equivalent to: `(x: Double) => 1.0 * x * x * x * x`
println(p4fast(42)) // 3111696.0
```


This is just the beginning.
Squid has much more under the hood, including:
normalizing intermediate representations;
online optimization;
mixin program transformers;
cross-stage persistence;
automatic lifting and controlled inlining of library definitions;
painless compile-time optimization...
An overview of Squid's features is given [below](#overview),
and a short example of some of them can be found
[here](http://epfldata.github.io/squid/AdditionalExample.md).




## Getting Started

Squid currently supports Scala versions `2.12.x` and `2.11.y` for `y > 2`
(other versions might work as well, but have not been tested).

In your project, add the following to your `build.sbt`:

```scala
resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies += "ch.epfl.data" %% "squid" % "0.4.0-SNAPSHOT"
```

Some features related to [library-defined optimizations](#qsr) and [squid macros](#smacros), 
such as `@embed` and `@macroDef`, 
require the use of the [macro-paradise](https://docs.scala-lang.org/overviews/macros/paradise.html)  plugin.
To use these features, add the following to your `build.sbt`:

```scala
val paradiseVersion = "2.1.0"

autoCompilerPlugins := true

addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full)
```

In case you wish to use a more recent version of Squid that has not yet been published,
you'll have to clone this repository
and publish Squid locally,
which can be done by executing the script in `bin/publishLocal.sh`.


<a name="overview">

## Overview of Features


### Squid Quasiquotes

Quasiquotes are the primitive tool that Squid provides to manipulate program fragments 
–– building, composing and decomposing them.
Quasiquotes are central to most aspects of program transformation in Squid.

[You can find an in-depth tutorial about Squid quasiquotes here.](http://epfldata.github.io/squid/tutorial)

**Note:**
In the original Squid papers [[1]](#scala17) and [[2]](#gpce17),
we used `Code[T]` as the type of program fragments.
With the introduction of scope safety and our POPL 2018 paper [[3]](#popl18),
this type now takes an extra parameter, as in `Code[T,C]` where `C` represent the term's context requirements.  
One cans still use type `OpenCode[T]` when context requirements are not important; this type has limited capabilities (no `run` or `compile`, for instance), but can be turned into a closed code type with method `unsafe_asClosedCode`.
On the other hand, `ClosedCode[T]` (a synonym for `Code[T,{}]`) is the type of closed program fragments.


#### Type-Safe Code Manipulation

Unlike the standard [Scala Reflection quasiquotes](https://docs.scala-lang.org/overviews/quasiquotes/intro.html),
Squid quasiquotes are statically-typed and hygienic, 
ensuring that manipulated programs remain well-scoped and well-typed 
and that variable bindings and other symbols do not get mixed up.
Still, Squid quasiquotes support a flexible pattern-matching syntax 
and facilities to traverse programs recursively while applying transformations.


While Squid quasiquotes focus on expressions (not definitions), Squid also provides a way to _embed_ arbitrary class and object definitions so that their methods can be inlined effortlessly, at the discretion of the metaprogrammer.


As a quick reference for Squid users, 
we provide a [cheat sheet](https://github.com/epfldata/squid/tree/master/doc/reference/Quasiquotes.md) that summarizes the features of Squid quasiquotes. Also see the [quasiquotes tutorial](http://epfldata.github.io/squid/tutorial).



<a name="msp"/>
<a name="qsr"/>

### Multi-Stage Programming and Quoted Staged Rewriting

Squid fully support the multi-staged programming paradigm (MSP), 
allowing the composition and evaluation of program fragments at runtime
(via runtime compilation or reflective interpretation).

In addition, since Squid provides type-safe code inspection capabilities
(a novelty in the field of statically-typed staging),
it can be used to achieve quoted staged rewriting (QSR) [[2]](#gpce17),
an approach to program optimization that mixes the advantages of user-defined rewrite rules, strategic program transformation and MSP.



<a name="ldo"/>

### Library-Defined Optimizations

Squid provides tools to create _static optimizers_, 
which are used to optimize _at compile time_ delimited portions of a user's codebase.
Together with quoted staged rewriting, 
this capability allows for quite flexible and safe library-defined optimizations [[2]](#gpce17).

[Click here to learn more about static optimizers.](https://github.com/epfldata/squid/tree/master/doc/Transformers.md#static-optimizers)



<a name="transformers"/>

### Program Transformation Support

Squid supports the definition and composition of custom program transformers and transformation strategies.
This is achieved via Scala mixin-composition and quasiquote-based rewriting declarations.
Squid transformers are type-preserving, 
and they make sure that transformed programs remain well-typed and well-scoped.

[Click here to learn more about Squid transformers.](https://github.com/epfldata/squid/tree/master/doc/Transformers.md)


<a name="irs"/>

### Intermediate Representations

Squid quasiquotes, and Squid's infrastructure in general, are unique 
in that they are generic in the actual intermediate representation (IR)
used to encode program fragments.
Custom IRs can be implemented and plugged into Squid to gain the high-level, type-safe features offered by Squid. 
This is done by implementing Squid's object algebra interface [[1]](#scala17).

[Click here to learn more about Squid intermediate representations.](lso see the [quasiquotes tutorial](https://github.com/epfldata/squid/tree/master/doc/Intermediate_Representations.md)



<a name="smacros"/>

### Squid Macros

Squid macros are an experimental type-safe alternative to legacy scala-reflect macros, based on Squid's infrastructure.
The current implementation is a very rough prototype that should not yet be relied upon.

As an example, here is [the short program transformation](#early-example) 
showed at the beginning of this document,
rewritten as a Squid macro:

```scala
@macroDef(Embedding)
def myMacro(pgrm0: Double) = {
  // in this scope, `pgrm0` has type `Code[Double]`
  val pgrm1 = pgrm0 transformWith (new Lowering('MyPhase) with BottomUpTransformer)
  val pgrm2 = pgrm1 rewrite {
    case code"($xs:List[$t]).::($x).head" => x
    case code"(${Const(n)}:Int) + (${Const(m)}:Int)" => Const(n+m)
  }
  pgrm2
}

// the following should appear in a different project:
myMacro(Test.foo(1 :: 2 :: 3 :: Nil) + 1) // expands into `2.toDouble`
```

(Note: the `macroDef` feature currently lives in experimental branch `squid-macros`.)




## Applications of Squid

Squid is still quite new and "bleeding edge".
See the [examples folder](example/src/main/scala/) for some example uses.
A little query compiler built with Squid can be found [here](https://github.com/epfldata/sc-public/tree/master/relation-dsl-squid). Another LINQ-inspired query engine build with Squid can be found [here](https://github.com/epfldata/dbstage).
We also built a small staged linear algebra library prototype [[4]](#ecoop19),
to be released soon.





## Publications

<a name="scala17">[1]</a>: 
Lionel Parreaux, Amir Shaikhha, and Christoph E. Koch. 2017.
[Squid: Type-Safe, Hygienic, and Reusable Quasiquotes](https://conf.researchr.org/event/scala-2017/scala-2017-papers-squid-type-safe-hygienic-and-reusable-quasiquotes). In Proceedings of the 2017 8th ACM SIGPLAN Symposium on Scala (SCALA 2017).
(Get the paper [here](https://infoscience.epfl.ch/record/231700).)
<!-- https://doi.org/10.1145/3136000.3136005 -->

<a name="gpce17">[2]</a>: 
Lionel Parreaux, Amir Shaikhha, and Christoph E. Koch. 2017.
[Quoted Staged Rewriting: a Practical Approach to Library-Defined Optimizations](https://conf.researchr.org/event/gpce-2017/gpce-2017-gpce-2017-staged-rewriting-a-practical-approach-to-library-defined-optimization).
In Proceedings of the 2017 ACM SIGPLAN International Conference on Generative Programming: Concepts and Experiences (GPCE 2017). **Best Paper Award.**
(Get the paper [here](https://infoscience.epfl.ch/record/231076).)

<a name="popl18">[3]</a>: 
Lionel Parreaux, Antoine Voizard, Amir Shaikhha, and Christoph E. Koch. 2018.
[Unifying Analytic and Statically-Typed Quasiquotes](https://popl18.sigplan.org/event/popl-2018-papers-unifying-analytic-and-statically-typed-quasiquotes).
In Proceedings of the ACM on Programming Languages (POPL 2018).
(Get the paper [here](https://infoscience.epfl.ch/record/232427).)

<a name="ecoop19">[4]</a>: 
Amir Shaikhha, Lionel Parreaux. 2019.
[Finally, a Polymorphic Linear Algebra Language](https://2019.ecoop.org/details/ecoop-2019-papers/5/Finally-a-Polymorphic-Linear-Algebra-Language).
In 33rd European Conference on Object-Oriented Programming (ECOOP 2019).
(Get the paper [here](https://infoscience.epfl.ch/record/266001).)


