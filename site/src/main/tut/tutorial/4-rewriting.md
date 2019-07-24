---
layout: docs
title:  "Code Rewriting"
section: "tutorial"
subposition: 4
---

# Code Rewriting

```tut:invisible
object IR extends squid.ir.SimpleAST
import IR.Predef._
import IR.Quasicodes._
import squid.utils._
```

In the [previous section](3-matching.html), we saw how to pattern match individual code fragments.
We can use the same syntax to do something more interesting:
automatically rewrite all subexpressions of a given program.
A rewrite rule is a _`code"pattern" => rewritten code`_ pair that will be matched and applied to each subexpression of an input program.
Therefore, it has to behave polymorphically with respect to the way it handles contexts...


## Reminder on Context Polymorphism

We have seen that context requirements are encoded using Scala intersection types,
and that context types could be abstracted as type parameters.
For instance, one can define:
```tut:silent
val s = Variable[String]
def intro[C](n: Code[Int, C]) = code"$s * $n"
def outro[C](q: Code[String, C & s.Ctx]) = code"{($s) => $q}"
```

(To learn more about `Variable`, see [this section](2-staging.html#advanced-topic-first-class-variable-symbols).)

Function `intro` reuses term `n` of context `C` and _introduces_ a free variable `s`,
yielding context `Code[String, C{val s: String}]`.
On the other hand, `outro` takes a term `q` of context `C` _extended with_ `s: String` and captures that variable
by constructing a bigger term and inserting `q` in a context where `y` is defined.

Here are a few usage examples:

```tut
{ val x = Variable[Int]
  val a = code"1 + $x"
  val b = intro[x.Ctx](a)
  val c = outro[x.Ctx](b) // type arguments cannot be inferred
  code"val $x = 3; $c".run.apply("test!") }
```


## Rewrite Rules

To transform a term `t`, one can use the following syntax:

```scala
t rewrite {
  case code"..." => code1
  case code"..." => code2
  ...
}
```

`rewrite` is a macro that makes sure each case only rewrites its pattern
to a term with the same type.
The context of terms extracted from rewrite rule patterns
are freshly-generated types of the form `<context @ l:c>`,
where `l` is the line and `c` is the column of the `case` corresponding to the rule.

In case the context on the right-hand of a case side is more restrictive than the context of the pattern,
the result of the rewriting will be associated with a context capturing those extra requirements.
For example, notice how the result of the rewriting below has type `Code[Unit,d.Ctx]` whereas the original term had type `Code[Unit,Any]`.
This is because we have introduced a free variable in the right-hand side of the rewrite rule.

```tut
val d = Variable[Double]
code"val x = 42; println(x.toDouble)" rewrite {
  case code"($n:Int).toDouble" => code"$d + 1" }
```

In the code above, the type of extracted term `n` is `Code[Double,<context @ 1:16>]` and
the type of the rewritten term `code"$d + 1"` has its context requirement refined as `Code[Double,<context @ 1:y> & d.Ctx]`.


## Fixed Point and Top-Down Rewriting

A similar macro, `fix_rewrite`, does the same as `rewrite` but applies the rewriting over and over again until the program stops changing (it reaches a fixed point).

By default, `rewrite` and `fix_rewrite` rewrites terms in a bottom-up fashion.
Top-down variants are also available as `topDown_rewrite` and `fix_topDown_rewrite`,
respectively.



## Limitations and Caveats

Beware of the power of `rewrite_fix` and `fix_topDown_rewrite`, which may expand a term unboundedly and diverge 
(triggering a recursion limit).

In some cases, even `topDown_rewrite` may introduce non-termination, 
as the rewriting is done in recrusive a top-down traversal order 
― if the rewritten term grows in size and is admissible to be transformed again, 
the rewriting may diverge.
Online normalization (such as automatic ANF conversion) may also sometimes trigger this problem in surprising ways. (See [this issue](https://github.com/epfldata/squid/issues/3), for example.)

See the [transformers documentation](https://github.com/epfldata/squid/blob/master/doc/Transformers.md)
if you want more control over transformations, and to be able to define more precise optimization passes.



## The Power Functions Again!

```tut:invisible
def power[C](n: Int, x: Code[Double,C]): Code[Double,C] = {
  if (n > 0) code"${power(n-1, x)} * $x"
  else code"1.0"
}
```

We are now equipped to write an optimization pass that will transform any program,
rewriting calls to `Math.pow(x,n)`
to a more efficient sequence of multiplications in the case where `n` is a small constant integer.
We make use of the context-polymorphic `power` function defined in the 
[section on multi-stage programming](2-staging.html#back-to-the-power-function).

```tut:silent
def opt[T,C](pgrm: Code[T,C]): Code[T,C] = pgrm rewrite {
  case code"Math.pow($x, ${Const(d)})"
  if d.isValidInt && (0 to 16 contains d.toInt)
  => power(d.toInt, code"$x")
}
```

The condition guard for this rewriting to apply is that the extracted constant `d`
should be a valid 32-bit integer (`Int` in Scala), and that it should be in the range `0 to 16`.
For example:

```tut
opt(code{ val x = readInt; println(Math.pow(x,4)) })
```

For completeness, we show below a self-contained version of `opt`,
that uses a for loop to build the resulting of the rewrite rule.
It demonstrates that indeed any staged computation can happen on the right-hand side of a rewriting,
combining the powers of rule-based program rewriting with multi-stage programming.
This implementation also let-binds the base argument to `pow` so as to avoid potential code duplication.

```tut:silent
def opt[T,C](pgrm: Code[T,C]): Code[T,C] = pgrm rewrite {
  case code"Math.pow($x, ${Const(d)})"
  if d.isValidInt && (0 to 16 contains d.toInt)
  =>
    val xv = Variable[Double]
    var acc = code"1.0" withContextOf xv
    for (n <- 1 to d.toInt) acc = code"$acc * $xv"
    code"val $xv = $x; $acc"
}
```

We use helper method `withContextOf` to type `code"1.0"` as `Code[Double,xv.Ctx]`
(it is equivalent to `code"1.0" : Code[Double,xv.Ctx]`)
so that we can later assign `code"$acc * $x"` to it in the loop
– otherwise, the variable would have an incompatible type and Scala would reject the assignment.
This helper method can also be applied to code values, so `code"1.0" withContextOf x`
would be given type `Code[Double,<context @ 2:16>]`.


Let us now try out our optimization!


```tut
import Math._
val normCode = opt(code{ (x:Double,y:Double) => sqrt(pow(x,2) + pow(y,2)) })
val norm = normCode.compile
norm(1,2)
```

The full code demonstrating this example is
[available in the squid/example project](https://github.com/LPTK/Squid/blob/master/example/src/main/scala/example/PowOptim.scala).



## Note on Intermediate Representations

Depending on one's use cases,
the power of rewrite rules can be multiplied by using an intermediate representation with desirable properties.
For example, when using an ANF IR
(see the reference on [intermediate representation](https://github.com/epfldata/squid/blob/master/doc/Intermediate_Representations.md)),
we do not need to worry about code duplication,
and defensive let-binding of extracted programs as done above becomes unnecessary.
For more on this, see
[our GPCE 2017 paper [2]](/squid/#gpce17).


