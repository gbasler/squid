# Squid Quasiquotes Tutorial


## Summary

###1. [Introduction](#introduction)
###2. [Code Composition & Multi-Stage Programming](#code-composition--multi-stage-programming)
###3. [Code Extraction with Pattern Matching](#code-extraction-with-pattern-matching)
###4. [Term Rewriting](#term-rewriting)
###5. [Advanced Topics on Term Rewriting](#advanced-topics-on-term-rewriting)
###6. [Debugging Quasiquotes](#debugging-quasiquotes)




## Introduction


### Getting Started

All examples in this section can be executed in a REPL.
Simply paste the following at the beginning of the session:
```scala
object Code extends squid.ir.SimpleAST
import Code.Predef._
import Code.Quasicodes._
```


### Code Quotation

In Scala, text quotation is written between two `"` (double quotation marks).
`"abc"` represents the string of characters made of characters `'a'`, `'b'` and `'c'`.

Squid allows one to also quote _code_ instead of _text_.
The syntax is `ir"..."`,
which represents a program fragment in some _intermediate representation_ (IR) – hence the `ir` prefix.
For example, the value `ir"2 + 2"` is _not_ some string of characters,
but an abstract syntax tree representing the expression `2 + 2`.
It can be viewed as syntactic sugar for an explicit representation of code
via the composition of function applications,
which could look like:
`IntAddition(Constant(2),Constant(2))`.

A _quasi_-quote is a quote in which parts are left out to be filled later (called _holes_).
In the particular case of text quotation, quasiquotes are also called _string interpolations_,
and are written with an `s` prefix and `${...}` escapes.
The value `s"All work and no play makes ${x} a dull boy."`
in a context where
`val x = "Jack"`
is equivalent to
`"All work and no play makes Jack a dull boy."`.
Note that when the expression we escape is a simple identifier, we can write
`$x` instead of `${x}`.

It is also possible to _quasi_-quote code, with the same syntax `ir` as seen above,
using `${...}` for escape.
This allows us to compose code fragments together to form bigger programs.

### Note on Quasi-_code_

The syntax `ir{ ... $(x)... }`, referred to as _quasicode_,
can be used in place of `ir" ... ${x}... "` in expression mode (but not in `case` patterns).
This has several advantages, including good IDE integration: autocompletion, syntax and error highlighting, jump-to-definition, etc.


## Code Composition & Multi-Stage Programming

### Building and Running Programs

As a simple example of composition, consider the following REPL session:

```scala
> val a = ir"2"
a: IR[Int,{}] = ir"2"

> val b = ir"$a + $a"
b: IR[Int,{}] = ir"(2).+(2)"
```

**Note**: for clarity, we have simplified the types displayed in the REPL (for example shortening `Code.IR` to `IR`).

By looking at the types in the REPL, we can tell we are manipulating terms of type `Int`.
We will see later what the second type parameter means (here `{}`).

The `Const` function can be used to _lift_ normal values to terms (code values):

```scala
> ir"${Const(42)}.toDouble"
res: IR[Double,{}] = ir"42.toDouble"
```

It is possible to _execute_ the code we have composed using the function `run`.

```scala
> b.run
res: Int = 4
```

This will interpret the constructed program and return its value.
In order to avoid the overhead associated with interpretation,
it is also possible to _compile_ the code at runtime.
The `compile` method will compile and execute an program fragment.

```scala
> b.compile
res: Int = 4
```

**Caveat: This syntax is currently unavailable as it has not yet been ported from an older version of the framework**


These features allow what is commonly known as
_multi-stage programming_ (MSP).
The canonical example of MSP is that of the _power_ function.
We start from a normal, inefficient implementation of the power function for `Double` precision numbers:

```scala
> def power(n: Int, x: Double): Double =
    if (n > 0) power(n-1)(x) * x
    else 1.0
power: (n: Int, x: Double)Double

> power(2, 3)
res8: Double = 9.0
```

And then, by simply adding staging annotations (in the form of `IR` types)
to indicate what computations should be delayed (as opposed to executed in the current stage),
we construct a _code generator_, that given any program fragment of type `Double`,
constructs a sequence of multiplications of that number.

```scala
> def power(n: Int, x: IR[Double,{}]): IR[Double,{}] =
    if (n > 0) ir"${power(n-1, x)} * $x"
    else ir"1.0"
power: (n: Int, x: IR[Double,{}])IR[Double,{}]

> power(2, ir"3.0")
res: IR[Double,{}] = ir"1.0.*(3.0).*(3.0)"
```

We will see in the next section how to
use this code generator to great effect for optimizing our programs.



### Contexts and Open Terms


Open terms are terms that contain unbound variable references (free variables).
Squid quasiquote disallow the implicit definition of open terms.
For example, `ir"x + 1"` is illegal, because `x` is unbound.
However, the `$$x` double-dollar syntax can be used to explicitly ask for a free variable, as in:
`ir"($$str: String).length"`, which contains an unbound variable `str` of type `String`.
Scala's local type inference means we will usually need a type annotation with each explicit free variable introduction.

In order to keep track of what free variables are contained in a term,
terms have type `IR[Typ,Ctx]`,
where the second type parameter `Ctx` represents the _context requirement_ of the term.
We write context requirements using Scala's syntax for structural types.
For example, `{}` is the empty context and `{val n: Int}` is a context in which a value `n` of type `Int` must be defined.

```scala
> val strlen = ir"($$str: String).length"
strlen: IR[Int,{val str: String}] = ir"($str: String).length()"
```

The `IR` class is contravariant in its `Ctx` type argument,
so that a term with a context `C` can be used as a term with a _more specific_ context `C' <: C`. 
For example, we have:  
`IR[Int,{}]  <:  IR[Int,{val ls: Seq[Int]}]  <:  IR[Int,{val ls: List[Int]}]`  
This is because `List[Int] <: Seq[Int]` and so `{val ls: List[Int]} <: {val ls: Seq[Int]}`.


**Note**: When you write out something like `ir"42"` in the REPL,
you might actually see `IR[Int,Any]` instead of `IR[Int,{}]`.
This is because in Scala, the empty structural type `{}` (equivalent to `AnyRef` or Java's `Object`) is a subtype
of the more general type `Any`, which is inferred by the quasiquote engine.
You can read it as: `ir"42"` can be used in _**any**_ context.

Naturally, it is prohibited to `run` open terms. The system statically makes sure of that:

```scala
> strlen.run
<console>:20: error: Cannot prove that AnyRef <:< Any{val str: String}.
       strlen.run
              ^
```

Finally, one of the most important mechanisms is that of free variable capture.
When inserting an open term `t` inside a bigger term where more bindings are available,
the corresponding _free_ variables in `t` will be captured, and will no more be free.
For example, consider:

```scala
> val len123 = ir"val str = List(1,2,3).toString; $strlen"
len123: IR[Int,{}] =
ir"""{
  val str_0 = scala.collection.immutable.List.apply[scala.Int](1, 2, 3).toString();
  str_0.length()
}"""

> len123.run
res: Int = 13
```


### Free Variables Renaming and Substitution

Given an open term, one can replace all its occurrences of a free variable by applying the `subs` method.

```scala
> val q = ir"($$x: Int) + 1"
q: IR[Int, {val x: Int}] = ir"($x: Int) + 1"

> val s = q.subs((Symbol("x"), ir"42"))
s: IR[Int, {}] = ir"42 + 1"
```

`Symbol` is a Scala construct from the standard library which has a dedicated syntax, so that
expression `Symbol("x")` can be written simply `'x`.
Furthermore, calls like `a.foo(b)` can be written `a foo b`,
where identifiers like `foo` used operators have the least precedence (lower than symbol-based operators).
Finally, a tuple `(a,b)` can be written `a -> b` (standard Scala syntactic sugar).
As a result, we will simply write the following, equivalent to the code above:

```scala
> val s = q subs 'x -> ir"42"
s: IR[Int, {}] = ir"42 + 1"
```

It is also possible to only rename free variables. The two following lines are equivalent:

```scala
> val q0 = q rename 'x -> 'y
q: IR[Int, {val y: Int}] = ir"($y: Int) + 1"

> val q1 = q subs 'x -> ir"$$y: Int"
q: IR[Int, {val y: Int}] = ir"($y: Int) + 1"
```

These operations will turn out to be crucially essential in Section _Rewritings_.



### Back to the Power Function


We saw that the `power` function defined above, when partially applied, yields a code generator which
takes a program of type `Double` and returns a program of the same type:

```scala
> val p3 = power(3, _ : IR[Double,{}])
p3: IR[Double,{}] => IR[Double,{}] = <function1>

> p3(ir"2.0")
res: IR[Double,{}] = ir"1.0.*(2.0).*(2.0).*(2.0)"
```

What we would now like to have is a term of type `IR[Double => Double,{}]`,
that we can compile and execute efficiently. 
We have to pass a variable reference to `power` instead of a closed term.
To allow for this, we make `power` polymorphic
in the context of the term it multiplies with itself (the body of the function does not change):

```scala
> def power[C](n: Int, x: IR[Double,C]): IR[Double,C] =
    if (n > 0) ir"${power(n-1, x)} * $x"
    else ir"1.0"
power: [C](n: Int, x: IR[Double,C])IR[Double,C]

> val x_5 = power(5, ir"$$x: Double")
res15: IR[Double,Any{val x: Double}] = ir"1.0.*($x).*($x).*($x).*($x).*($x)"
```

We can now generate on the fly efficicent code for calculating the _n_-th power of any `Double`:

```scala
> val power5 = ir"(x: Double) => ${x_5}".compile
power5: Double => Double = <function1>

> power5(1.5)
res: Double = 7.59375
```

The last line will execute the function `power5`,
which is really just as fast as if we had written out:  
`val power5 = (x: Double) => x * x * x * x * x`


### Conclusions on MSP

This whole exercise may seem a little pointless,
as it is not a big deal to define different versions of the power function by hand.
However, one has to see the big picture:
MSP becomes truly useful when we construct non-trivial,
highly-polymorphic program fragments for which every manual specializations would be too tedious or unfeasible to maintain.
Additionally, the programs we generate may even depend on runtime input values,
making it impossible to write the code by hand prior to running the program.

Another way to see it is that with MSP, to be efficient we do 
not have to write and maintain lots of boilerplate and repetitive code:
we can leverage generative techniques instead,
with solid static guarantees that the generated code will be well-formed (well-typed and well-scoped).






## Code Extraction with Pattern Matching

### Code as Data, in a Statically Typed Setting

We have seen that in the tradition of _ML_ and particularly _MetaML_,
Squid is statically typed and prevents the occurrence of type mismatch or unbound variable reference errors at runtime.
On the other hand, in the tradition of _Lisp_, Squid views code as (executable) data that one can _inspect_ (or _analyse_) and _rewrite_.
This is only possible thanks to the notion of static context requirements, presented in the previous section.

In order to analyse general data and extract patterns from it, the canonical functional way is to use pattern matching.
In Squid, one can pattern-match code just like one pattern-matches data.
Holes in patterns behave like extractors, allowing to take apart code expressions:
 
```scala
> val m = ir"readInt + 1" match { case ir"($n: Int) + 1" => n }
m: IR[Int,{}] = ir"scala.Predef.readInt()"

> val ir"($m: Int) + 1" = ir"readInt + 1"
m: IR[Int,{}] = ir"scala.Predef.readInt()"
```

(In the code above, the second REPL line is equivalent to the first one, using Scala syntactic sugar.)

Pattern-matching is sensitive to contexts.
For example, extracting a subterm in a context where a binding exists
will give to the extracted sybterm a type that reflects the potential dependency:

```scala
> val funBody = ir"(x: Int) => x + 1" match { case ir"(y: Int) => $b" => b }
funBody: IR[Int, {val y: Int}] = ir"($y: Int) + 1"
```

Notice in the code above that _**bound variable names do not matter**_:
we extracted function body `b` using `y` as the parameter name it is supposed to refer to.
It all worked out properly, although the code we matched was written using `x` as the parameter name!

Just like `Const` can be used to construct constants into the code by _lifting_ current-stage values,
we can also use it to _extract_ constant current-stage values:

```scala
> funBody match { case ir"($z: Int) + (${Const(k)}: Int)" => (z,k) }
res: (IR[Int, {val y: Int}], Int) = (ir"$y: Int", 1)
```

Notice that in the examples above, it is necessary to provide the type of extraction holes because the `+` operation
in Scala is ad-hoc polymorphic. Writing a pattern like `case ir"($x: Long) * $y"` will give us an error such as:
```
ambiguous reference to overloaded definition,
both method * in class Long of type (x: Char)Long
and  method * in class Long of type (x: Byte)Long
match argument types (Nothing)
```

**Caveat**: Because of the infamous `any2stringadd` implicit convertion, syntax `ir"($x: Int) + $y"` will make Scala think that
`y` has type `String` and `+` is string concatenation...



### Note on Parametric Polymorphism

In general, in order to use a non-concrete type `T` in quasiquotes,
one will have to provide an implicit type representation evidence, of type `IRType[T]`.

For example, this is how to implement a function returning a program that builds a singleton list from a value:

```scala
> def mkSingle[T,C](x: IR[T,C])(implicit ev: IRType[T]) = ir"List($x)"
> mkSingle(ir"42")
res: IR[List[Int],{}] = ir"scala.collection.immutable.List.apply[scala.Int](42)"
```

Scala  also provides the equivalent shortcut syntax:

```scala
def mkSingle[T:IRType,C](x: IR[T,C]) = ir"List($x)"
```




### Matching Types

In order to make code manipulation polymorphic, we need a way to extract types as well as values.
This is done with a similar syntax.
Consider the following rewriting, which implements β-reduction (lambda application inlining):

```scala
> def beta[T:IRType,C](x: IR[T,C]) = x match {
  case ir"((x: $t) => $body: T)($a)" => body subs 'x -> a
}
> beta(ir"((x: Int) => x + x + 1)(readInt)")
res: IR[Int,{}] = ir"scala.Predef.readInt().+(scala.Predef.readInt()).+(1)"
```

In the right-hand side of this `case` expression,
extracted value `body` has type `IR[T,C{val x: t}]` (see the section on [Context Polymorphism](#context-polymorphism) to understand `C{val x: t}`)
where `t` is some locally-defined abstract type (introduce by the `ir` pattern macro),
and extracted value `t` is a representation of that type, of type `IRType[t]`.

It is sometimes useful to use an extracted type in type position outside of a quasiquote.
This is not possible directly, as an extracted type `t` is a type representation _value_ (of type `IRType`), not a proper Scala _type_. 
In order to do that, one will have to use the `Typ` type member defined in every `IRType`.
For instance, `Option[IR[t.Typ,C]]` is equivalent to `Option[IR[t,C]]`, althought the latter cannot be written directly.

Thanks to an implicit macro provided by Squid,
extracted types will be picked up automatically from the context in which they appear and used as implicit type representation evidence.

**Note**:
Of course, β-reduction is unsound if no care is being taken to avoid duplicating the argument.
A better implementation would be:

```scala
> def beta[T:IRType,C](x: IR[T,C]) = x match {
  case ir"((x: $t) => $body: T)($a)" => ir"val x = $a; $body"
}
> beta(ir"((x: Int) => x + x + 1)(readInt)")
res: IR[Int,{}] =
ir"""{
  val x_0 = scala.Predef.readInt();
  x_0.+(x_0).+(1)
}"""
```

Another way to avoid the problem would be to use an ANF IR,
in which every non-trivial expressions is bound to a temporary values,
so this problem could not exist.







## Term Rewriting

Once you know how to use pattern matching on code, you can use the same syntax to do something more interesting:
automatically rewriting all subexpressions of a program.
A rewriting rule is a _`pattern => rewritten code`_ pair that will be applied to each subexpression of your input program.
Therefore, it has to behave polymorphically with respect to the way it handles contexts...


### Context Polymorphism

We have seen that context requirements are encoded using Scala structural types.
But that's not the complete story!
A structural type such as `{val x: Int; val y: String}` is really just a structural _refinement_ on
`AnyRef` (which is the same as `Object` in Java), also written `AnyRef{val x: Int; val y: String}`.
In Scala, structural refinements can apply on any type, including abstract types and type parameters,
which makes it very handy to define context-polymorphic functions that refine those polymorphic contexts.

For instance, one can define:
```scala
def intro[C](n: IR[Int, C]) = ir"($$s: String) take $n"
def outro[C](q: IR[String, C{val s: String}]) = ir"(s: String) => $q"
```

Function `intro` reuses term `n` of context `C` and _introduces_ a free variable `s`,
yielding context `IR[String, C{val s: String}]`.
On the other hand, `outro` takes a term `q` of context `C` _extended with_ `s: String` and captures that variable
by constructing a bigger term and inserting `q` in a context where `y` is defined.

Here are a few usage examples:

```scala
> val a = ir"$$x: Int"
a: IR[Int, {val x: Int}] = ir"$x"

> val b = intro(a)
b: IR[Int, {val s: String; val x: Int}] = ir"scala.Predef.augmentString($s).take($x)"

> val c = outro[{val x: Int}](b)
c: IR[String => String, {val x: Int}] = ir"(s_0: java.lang.String) => scala.Predef.augmentString(s_0).take($x)"

> ir"val x = 42; $c"
res0: IR[String => String, {}] =
ir"""{
  val x_0 = 42;
  (s_1: java.lang.String) => scala.Predef.augmentString(s_1).take(x_0)
}"""

> val f = res0.run; f("test")
f: String => String = <function1>
res1: String = tes
```

**Some Properties of Structural Refinements**:
Squid quasiquotes will on occasion "merge" types together using intersection types (in current Scala, written using `with`).
We have `C{val x: A; val y: Int} with D{val x: B}` equivalent to `(C with D){val x: A with B; val y: Int}`.
Also, double refinement `C{val x: A}{val y: B}` is just `C{val x: A; val y: B}`.


### Rewriting Rules

To transform a term `t`, one can use the following syntax:

```scala
t rewrite {
  case ir"..." => code1
  case ir"..." => code2
  ...
}
```

`rewrite` is a macro that will make sure the cases only rewrite to terms with the same type as what is matched.
The context of terms extracted from rewrite rule patterns will be of the form `<context @ x:y>`,
where `x` is the line and `y` is the column of the `case` corresponding to the rule.

In case the context of the right-hand side is more restrictive than the context of the pattern,
the result of the rewriting will be associated with a context capturing those extra requirements.
For example, notice how the result of the rewriting below has type `IR[Unit,{val d: Double}]` whereas the original term had type `IR[Unit,{}]`.
This is because we have introduced a free variable in the right-hand side of the rewriting rule.

```scala
ir"val x = 42; println(x.toDouble)" rewrite { case ir"($n:Int).toDouble" => ir"($$d: Double)+1" }
res: IR[Unit,{val d: Double}] =
ir"""{
  val x_0 = 42;
  scala.Predef.println(($d).+(1))
}"""
```

In the code above, the type of extracted term `n` is `IR[Double,<context @ 1:16>]` and
the type of the rewritten term `ir"($$d: Double)+1"` has its context requirement refined as `IR[Double,<context @ 1:y>{val d: Double}]`.





### Limitations and Caveats

Beware that such rewritings are recursive by default: they will try to find a fixed point,
and may apply repeatedly until they reach the recursion limit if there are none!

See the [Transformers Tutorial](Transformers.md) if you want more control and to be able to define precise optimization passes.



### The Power Functions Again!

We are now equipped to write an optimization pass that will transform any program and rewrite calls to `Math.pow(x,n)`
to a more efficient sequence of multiplications, in the case where `n` is a small constant integer.
We make use of the context-polymorphic `power` function defined in the previous section.

```scala
def opt[T,C](pgrm: IR[T,C]) = pgrm rewrite {
  case ir"Math.pow($x, ${Const(d)})"
  if d.isValidInt && (0 to 16 contains d.toInt)  =>  power(d.toInt, ir"$x")
}
```

The condition guard for this rewriting to apply is that the extracted constant `d`
should be a valid 32-bit integer (`Int` in Scala), and that it should be in the range `0 to 16`.

For completeness, we show below a self-contained version of `opt`,
that uses a for loop to build the resulting of the rewrite rule.
It demonstrates that indeed any staged computation can happen on the right-hand side of a rewriting,
combining the powers of pattern-based program analysis and Multi-Stage Programming!

```scala
def opt[T,C](pgrm: IR[T,C]) = pgrm rewrite {
  case ir"Math.pow($x, ${Const(d)})"
  if d.isValidInt && (0 to 16 contains d.toInt) =>
    var acc = ir"1.0" withContextOf x
    for (n <- 1 to d.toInt) acc = ir"$acc * $x"
    acc
}
```

We use helper method `withContextOf` to type `ir"1.0"` as `IR[Double,<context @ 2:16>]`
so that we can later assign `ir"$acc * $x"` to it in the loop
– otherwise, the variable would have an incompatible type and Scala would reject the assignment.

Let us now try out our optimization!


```scala
> import Math._
> val normCode = opt(ir{ (x:Double,y:Double) => sqrt(pow(x,2) + pow(y,2)) })
normCode: IR[(Double, Double) => Double,{}] =
ir"((x_0: Double, y_1: Double) => java.lang.Math.sqrt(1.0.*(x_0).*(x_0).+(1.0.*(y_1).*(y_1))))"

> val norm = normCode.compile
norm: (Double, Double) => Double = <function2>

> norm(1,2)
res: Double = 2.23606797749979
```


The code code demonstrating this example is also
[available in the example package](https://github.com/LPTK/Squid/blob/master/example/src/main/scala/example/PowOptim.scala).



## Advanced Topics on Term Rewriting


### Sequential Rewritings

[TODO]

```scala
  case ir"val arr = new collection.mutable.ArrayBuffer[$t]($v); arr.clear; arr" => ir"new collection.mutable.ArrayBuffer[$t]()"
```


### Speculative Rewritings

[TODO]






## Debugging Quasiquotes

Replace `ir"..."` with `dbg_ir"..."` and look at the compilation messages.
Similarly, there is `dbg_rewrite`.

To see why rewritings did not fire or how they fired,
consider adding printing statements in the right-hand side of the rewriting,
or use `base.debugFor(... code ...)` to generate precise logging information.





