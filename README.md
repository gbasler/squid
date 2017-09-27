# **Squid** ― Scala Quoted DSLs


**[Squid quasiquotes tutorial](doc/tuto/Quasiquotes.md)**


## Introduction

**Squid** (for the approximative contraction of **Sc**ala **Qu**ot**ed** **D**SLs)
is a **metaprogramming** framework 
that facilitates the **type-safe** manipulation of **Scala programs**.
In particular, it is geared towards
the implementation of **library-defined optimizations** [[2]](#gpce17) and 
helps with the compilation of **Domain-Specific Languages** (DSL) embedded in Scala [[1]](#scala17).

<!-- TODO: give concrete application examples to pique curiosity/generate interest -->

**Caution:** Squid is still experimental, and the interfaces may change slightly in the future (especially those interfaces used to implement new intermediate representations).


### Installation

Squid currently supports Scala versions `2.11.3` to `2.11.8` 
(more recent versions might work as well, but have not yet been tested).

The project is not yet published on Maven, 
so in order to use it you'll have to clone this repository
and publish Squid locally,
which can be done easily by executing the script in `bin/publishLocal.sh`.

In your project, add the following to your `build.sbt`:

```scala
libraryDependencies += "ch.epfl.data" %% "squid" % "0.2-SNAPSHOT"
```

Some features related to [library-defined optimizations](#qsr), 
such as `@embed` and @macroDef, require the use of the macro-paradise  plugin.
To use these features, add the following to your `build.sbt`:

```scala
val paradiseVersion = "2.1.0"

autoCompilerPlugins := true

addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full)
```



## Squid Quasiquotes

Quasiquotes are the main primitive tool of Squid. 
They are used to construct, compose and decompose program fragments.
Unlike the standard [Scala Reflection quasiquotes](https://docs.scala-lang.org/overviews/quasiquotes/intro.html),
Squid's quasiquotes are statically-typed and hygienic, 
ensuring that manipulated programs remain well-typed 
and that variable bindings and other symbols do not get mixed up.
(On the other hand, Squid quasiquotes can only manipulate expressions, not class/def/type definitions.)

To start using quasiquotes in their simplest configuration, 
paste the following code in a file or REPL session:

```scala
object Embedding extends squid.ir.SimpleAST
import Embedding.Predef._
```

There are two forms of quasiquotes that co-exist in Squid:

### Simple Quasiquotes

Simple quasiquotes, written `code"t"` 
– or just `c"t"` as a shorthand –
have type `Code[T]`, 
where `T` is the type of `t`, the quoted Scala term.

Using unquotes within quasiquotes (syntax `${...}` or `$identifier`), 
code fragments can be composed and decomposed.
For example, the following program prints `Result: code"scala.io.StdIn.readInt()"`:

```scala
import scala.io.StdIn.readInt // Squid QQs pick up local imports

val inc = code"123" // a constant, equivalent to Const(123)

code"readInt + $inc" match {
  case code"($n: Int) + ${Const(m)}" =>
    // in this scope, we have n: Code[Int] and m: Int
    println("Result: " + n)
}
```

Quasiquotes can be used for [multi-stage programming (MSP)](https://en.wikipedia.org/wiki/Multi-stage_programming)
– they have methods `run` and `compile` for interpreting and runtime-compiling terms, respectively.
In fact, Squid quasiquotes are similar to [MetaOCaml](http://okmij.org/ftp/ML/MetaOCaml.html),
except for a few major differences (see [[1]](#scala17)).
Indeed, Squid quasiquotes:

 * can be used in pattern matching (which is not possible in MetaOCaml);
 
 * are "reusable", in that they can manipulate different underlying intermediate representations;
 
 * are implemented using macros 
 (without modifying the Scala compiler).

The latter point means that unlike MetaOCaml, 
Squid quasiquotes do not support references that cross quotation boundaries.
For example, given a function `foo` of type `Code[Int] => Code[Int]`, 
the following is not valid:

```scala
c"(x:Int) => ${ foo(c"x + 1") }"
```

Thankfully, we can use use the fact that Squid automatically lifts functions of type `Code[Int] => Code[Int]` to type `Code[Int => Int]` upon insertion (with `$`), and immediately inlines the result. So the following has the desired behavior:

```scala
c"(x:Int) => ${ (x0:Int) => foo(c"$x0 + 1") }(x)"
```

(Note that in the above, `x0` can be named simply `x`, which will not introduce name clashes.)  

As a more complete example of this mechanism, the following function `pow` let-binds its `base` argument as `b` 
before multiplying it `exp` times.
The `for` loop happens at code-generation time, and thus does not appear as part of the generated code:

```scala
def power(base: Code[Double], exp: Int): Code[Double] = {
  code"""val b = $base; ${ (x: Code[Double]) =>
    var cur = c"1.0"
    for (i <- 1 to exp) cur = c"$cur * $x"
    cur
  }(b)"""
}

assert (
  power(c"readDouble", 3)
    == 
  c"val tmp = readDouble; 1.0 * tmp * tmp * tmp"
)
```




### Contextual Quasiquotes

Contextual quasiquotes, written `ir"t"`, 
have type `IR[T,C] <: Code[T]`
where `C` is a type that encodes the _context requirements_ of term `t`.

This is a yet safer flavor of quasiquotes, 
which makes sure that no open programs (program that contain free variables) 
are ever evaluated (with `.run` or `.compile`), 
and that every variable reference in a closed program 
is correctly scoped within its binder.

[A detailed tutorial on these more advanced of quasiquotes is available here](doc/tuto/Quasiquotes.md).



### Quasicode

Another syntactic form for expressing code is available in Squid,
called _quasicode_, as exemplified below:

```scala
import Embedding.Quasicodes._
val inc = code{123}
code{ readInt + ${inc} }
```

This syntax can be nicer to use when reifying big swathes of code,
because it gets more support from IDEs:
syntax coloring, click-to-definition, etc. – although IntelliJ's [language injection](https://www.jetbrains.com/help/idea/using-language-injections.html) feature can also help with the quoted form.

Quasicodes have some limitations when compared to quasiquotes.
For example, they cannot be used in patterns, 
and they can be less flexible with respect to type-checking in advanced cases.




## Program Transformation Support

See the [detailed tutorial](doc/tuto/Transformers.md).

<!-- give example, eg:
```scala
object PowerOptim extends IR.TopDownTransfo {
  new Rewrite[Double] {
    def apply[C] = {
      case dsl"math.pow($x, ${Constant(n)})"
        if n.isValidInt && (0 <= n && n <= 16) =>
          power(n.toInt)(x)
    }
  }
}
```
-->


## Squid Macros

Squid offers an alternative to Scala macros,
similar to [Scalameta](http://scalameta.org/) 
but using Squid's type-safe, hygienic quasiquotes and infrastructure.

The feature is not yet merged with the master branch.

A usage example:

```scala
@macroDef(Embedding)
def power(base: Double, exp: Int): Double = {
  exp match {
    case Const(exp) =>
      code"""val b = $base; ${ (x: Code[Double]) =>
        var cur = c"1.0"
        for (i <- 1 to exp) cur = c"$cur * $x"
        cur
      }(b)"""
    case _ => code"Math.pow($base, $exp.toDouble)"
  }
}
```



<a name="qsr"></a>
## Library-Defined Optimizations (Quoted Staged Rewriting)

Support for automatic library embedding, 
controlled inlining
and compile-time optimization,
explained in [[2]](#gpce17).








## Publications

<a name="scala17">[1]</a>: 
Lionel Parreaux, Amir Shaikhha, and Christoph E. Koch. 2017.
[Squid: Type-Safe, Hygienic, and Reusable Quasiquotes](https://conf.researchr.org/event/scala-2017/scala-2017-papers-squid-type-safe-hygienic-and-reusable-quasiquotes). In Proceedings of the 2017 8th ACM SIGPLAN Symposium on Scala (SCALA 2017). 
<!-- https://doi.org/10.1145/3136000.3136005 -->

<a name="gpce17">[2]</a>: 
Lionel Parreaux, Amir Shaikhha, and Christoph E. Koch. 2017.
[Quoted Staged Rewriting: a Practical Approach to Library-Defined Optimizations](https://conf.researchr.org/event/gpce-2017/gpce-2017-gpce-2017-staged-rewriting-a-practical-approach-to-library-defined-optimization).
In Proceedings of the 2017 ACM SIGPLAN International Conference on Generative Programming: Concepts and Experiences (GPCE 2017).  
(Get the paper [here](https://infoscience.epfl.ch/record/231076).)

<a name="popl18">[3]</a>:
Lionel Parreaux, Antoine Voizard, Amir Shaikhha, and Christoph E. Koch
Unifying Analytic and Statically-Typed Quasiquotes. To appear in Proc. POPL 2018.


