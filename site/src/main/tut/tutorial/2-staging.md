---
layout: docs
title:  "Multi-Stage Programming"
section: "tutorial"
subposition: 2
---

# Multi-Stage Programming

```tut:invisible
object IR extends squid.ir.SimpleAST
import IR.Predef._
```

## Evaluating Programs

In the [previous section](1-basics.html),
we saw how to construct and compose code fragments using code quasiquotes.
It is possible to _evaluate_ or _execute_ the resulting code at runtime.
One way of doing this is to use the `run` function:

```tut
val cde = { val a = code"2" ; code"($a + $a) * 2" }
cde.run
```

This Squid functionality uses Java runtime reflection
to interpret the constructed program and return its value.
In order to avoid the overhead associated with interpretation,
it is also possible to _compile_ the code at runtime
before executing the resulting Java bytecode,
which will be as fast executing normally-compiled code.
This is done via the `compile` method:

```tut
cde.compile
```

**Note:** Squid can also transform and optimize programs at compile time,
which avoids the overhead of runtime compilation
(and avoids having the Scala compiler as a runtime dependency!).
This possibility is explained in the 
[Static Optimizers](https://github.com/epfldata/squid/blob/master/doc/Transformers.md#static-optimizers)
section of the Squid reference on transformers.



## Staging The Power Function

The features we have seen allow what is commonly known as
**_multi-stage programming_** (MSP),
whereby we separate the execution of a program into distinct code-generation phases or _stages_.
Each stage executes some parts of the program that are known at this stage,
and delays the execution of the rest by emitting code for it.
The last stage consists in a program
much simpler and more efficient than the original,
where most abstractions have typically been removed.

The canonical example of MSP is that of the _power_ function.
We start from a normal, inefficient implementation of the power function for `Double` precision numbers,
which proceeds by recursion:

```tut
def power(n: Int, x: Double): Double = {
  if (n > 0) power(n-1, x) * x
  else 1.0
}
power(2, 3)
```

And then, by simply adding staging annotations (in the form of `Code` types and `code` quasiquotes)
to indicate what computations should be delayed
as opposed to executed immediately (i.e., in the "current stage"),
we construct a _code generator_ ― given any program fragment of type `Double`,
we construct a sequence of multiplications of that number:

```tut
def power(n: Int, x: ClosedCode[Double]): ClosedCode[Double] = {
  if (n > 0) code"${power(n-1, x)} * $x"
  else code"1.0"
}
val pgrm = power(3, code"0.5")
pgrm.compile
```

The example above is not very convincing,
because generating the code of `pgrm` and then compiling it will certainly be slower
than if we had called the original (unstaged) power function!

In fact, the whole point of MSP is to generate and compile pieces of code that can be _reused_ many times,
in order to amortize the cost of runtime compilation.
What we really want here is to use `power` to generate partially-evaluated functions
that efficiently compute the power of _any number_, given a fixed exponent.


## Generating Specialized Power Implementations

<!-- For example, we'd like to end up with something like `` -->
<!-- In other words,  -->
For any given exponent `n`, for any given exponent `n` we'd like to generate the code of a function
`(x: Double) => x * x * ... [n times] ... * x`.
Let us try to do that naively for exponent `3`:

```tut:fail
val pow3 = code"(x: Double) => ${ power(3, code"x") }"
```

It does not work, because `x` is undefined in `code"x"`,
even though it looks like we're binding it in the outer quote.
This is a limitation of string interpolation macros in Scala.
To fix this, we need to _escape_ the inner insertion _and_ quotation, as follows
(due to macro limitations,
this code will not work if you defined `power` in the REPL,
but it will work in a normal Scala file):

```scala
val pow3 = code"""(x: Double) => $${ power(3, code"x") }"""
```

Notice the use of triple-quotation `"""` to escape the inner quotation,
and the double-dollar sign `$$` to escape the inner insertion.

On the other hand, no escapes are needed in the alternative _quasicode_ syntax
(which also works better in the REPL):

```tut:silent
import IR.Quasicodes._
```
```tut:fail
val pow3 = code{ (x: Double) => ${ power(3, code{x}) } }
```



Our code still does not work, though!
The reason is given in the error message:
our `power` function expects to receive an argument of type `ClosedCode[Double]`,
but we pass it a different type, `Code[Double,x.type]`...
This was to be expected, since the term `code"x"` is obviously not "closed"
--- it contains a free variable `x`, which is only bound in the outer quote.
If `code"x"` was to be assigned type `ClosedCode[Int]`,
Squid's type system would become unsound as 
we could easily crash the program,
for example by calling `code"x".run` or by storing `code"x"` in a mutable variable
and reusing it later, outside of the outer quote where `x` is bound.

Thankfully, the fix is easy:
we need to make our `power` function _parametric_ in the context that it accepts:


```tut:silent
def power[C](n: Int, x: Code[Double,C]): Code[Double,C] = {
  if (n > 0) code"${power(n-1, x)} * $x"
  else code"1.0"
}
```

We can now construct our `pow3` function:


```tut
val pow3 = code{ (x: Double) => ${ power(3, code{x}) } }
```




## Dealing with Context Types

In the previous subsection,
we saw an open term of type `Code[Double,x.type]`,
where the free variable `x` was bound in some outer quote.
The singleton `x.type` is used as a "phantom type"
to indicate a dependency on the corresponding variable.
But what happens if there are several free variables in the term?

In general, a context type is made of an _intersection_ of _context requirements_,
where a context requirement can be an abstract type or type parameter `C`
or a specific variable requirement like `x.type`.

For example, `Code[Double, C & x.type & y.type]`
is the type of a code fragment whose context requirement is `C` augmented
with the requirements for local variables `x` and `y`,
where `&` denotes type intersections
(equivalent to `A with B` in legacy Scala syntax).
The `&` notation can be imported from `squid.utils`.

Here is an example where the type `Code[Double, C & x.type & y.type]` comes up:


```tut:silent
import squid.utils.&

def foo[C](ls: Code[List[Int],C]) = code {
  ${ls}.map(_.toString).fold("")((x, y) => ${
    val res: Code[String, C & x.type & y.type] = code{x + ", " + y}
    code{ println("Folding: " + y); ${res} }
  })
}
```

Calling `foo(code"List(1,2,3)")` will result in a code fragment
that maps and folds `List(1,2,3)`,
which can be verified using the term equivalence method `=~=` as below:

```tut
foo(code"List(1,2,3)") =~= code"""
  List(1,2,3).map(_.toString).fold("")((x, y) => {
    println("Folding: " + y); x + ", " + y })"""
```




The `Code` class is contravariant in its `Ctx` type argument,
so that a term with a context `C` can be used as a term with a _more specific_ context `C' <: C`. 
For example, we have:  
`ClosedCode[Int] = Code[Int,Any]  <:  Code[Int, v.type]  <:  Code[Int, v.type & w.type]  <:  OpenCode[Int] = Code[Int,Nothing]`



**Note:**
The current Scala compiler has an arbitrary limitation that prevents _users_
from writing `x.type` if the type of `x` is not a subtype of `AnyRef`,
for example if we have `x: Int`.
To work around this, Squid provides a `singleton.scope` macro utility,
which is used as follows:

```tut:silent
import squid.utils.typing.singleton.scope
```
```tut
code{ (x: Int) => ${ val c: Code[Int, scope.x] = code{x + 1}; c } }
```







## Last Words on MSP

Beyond the simplistic power function examples,
MSP becomes truly useful when we start applying it to the construction of non-trivial,
highly-polymorphic programs for which manual specialization would be too tedious or unfeasible to maintain.

Because it is based on runtime program generation and compilation,
MSP allows the program generation process itself to depend on runtime input values,
which enables entirely new patterns of performance-oriented software engineering.

In conclusion, MSP allows programmers to design abstract yet performant programs,
sparing them the burden of having to write and maintain lots of boilerplate and repetitive code.
Programmers can leverage generative techniques instead,
with solid static guarantees that the generated code will be well-formed (well-typed and well-scoped).



## Epilogue: Don't Want to Track Contexts Statically?

Tracking contexts in the type system provides complete scope-safety for Squid metaprograms,
but can make metaprogramming more verbose and difficult.
Squid also supports a style of programming where one only manipulates `OpenCode` fragments,
and where dynamic checks are used to convert `OpenCode` to `ClosedCode` when needed.

For example, below we redefine the `power` function in this style:

```tut:silent
def pow(n: Int)(v: OpenCode[Double]): OpenCode[Double] = {
  var cde = if (n == 0) code"1.0" else v
  for (i <- 1 until n) cde = code"$cde * $v"
  cde
}
def mkPow(n: Int) = code"(x: Double) => ${pow(n) _}(x)"
```

In the definition of `mkPow` above, we used automatic function lifting,
which converts an `OpenCode[A] => OpenCode[B]`
to an `OpenCode[A => B]` on insertion into a program fragment.

We can then try to `close` the result of `mkPow(5)` at runtime, returning an `Option[ClosedCode[Double => Double]]`:

```tut
val pow5 = mkPow(5).close
val power5 = pow5.get.compile
power5(1.5)
```

Naturally, trying to close an open term will fail and return `None`.










**If you are interested in learning a more advanced technique for manipulating free variables and open terms, read on. Otherwise, you can stop reading now!**















# Advanced Topic: First-Class Variable Symbols

In addition to often being unsafe in several contexts (see [our paper [3]](/squid/#popl18)),
traditional approaches to MSP such as MetaML and Dotty's new quoted terms
do not provide a way to explicitly manipulate variable bindings.

In Squid, not only is _scope safety_ ensured statically using the type system,
but we can manipulate variables explicitly
(which will become especially useful
when we start matching code fragments in the next section).
This is the topic of this section.


## The Variable Type

Squid allows the manipulation of first-class variable symbols,
of type `Variable[T]`.
Each `Variable` instance is unique,
and can be used to hygienically bind and reference named values in the constructed program.

For example, the following `openCde` fragment contains an unbound reference to variable `v`:

```tut
val v = Variable[Int]
val openCde = code"println($v + 1)"
```

Notice the type of `openCde`, as printed in the REPL:
it is no longer a `ClosedCode`, but a `Code` type with a context!
This is explained in the next section.

We can now bind the `v` reference in `openCde` to a value definition,
and run the resulting code:

```tut
val closedCde = code"val $v = 123; $openCde"
closedCde.run
```

**Note:** By default, the name of a first-class variable
(as it will appear in the generated program) is inferred
from the name of the variable it is assigned to
(like "`v`" above).
It is also possible to pass a name explicitly
with syntax `Variable[T]("myName")`.
<!-- It is important however to understand that -->
However,
variable names have no impact on the semantics of program manipulation,
and are just there to help debugging.
For example, two distinct variables with the same name will _not_ be mixed up:

```tut
val v0, v1 = Variable[Int]("myName") // v0 and v1 distinct but share same name
code"($v0: Int) => ($v1: Int) => $v0 + $v1"
```

Notice that the code generated by Squid in the above example
contains distinct binders for the two distinct variables we used,
and that there is no unintended variable capture (shadowing).
This exemplifies a property of code manipulation systems known as _hygiene_.



## Contexts and Open Terms

Open terms are terms that contain unbound variable references (i.e., free variables).
Squid quasiquotes disallow the use of undeclared variables;
for example, `code"x + 1"` is illegal, because `x` is unbound.
However, unbound references to first-class variable may be inserted in code fragments,
which results in open terms, like `openCde` as defined in the previous section.

Squid is a scope-safe metaprogramming framework,
in that it statically keeps track of open terms,
making sure that they can never be mistaken for closed ones.
For example, we cannot mistakenly try to evaluate an open term:

```tut:fail
openCde.run
```

In order to keep track of what free variables are contained in a term,
Squid code fragments have type `Code[T,C]`,
where the second type parameter `C` represents the _context requirement_ of the term.
Context requirements are expressed as type intersections
where each component represents one set of context requirement.
In Scala, an empty type intersection corresponds to type `Any`;
indeed, [remember](1-basics.html#types-of-programs)
that `ClosedCode[T]` is a type synonym equivalent to `Code[T,Any]`.
You can read it as: a `ClosedCode` term is valid to use in _**any**_ context.

The context requirement associated with a variable symbol `v` is `v.Ctx`,
where `Ctx` is an abstract type member of `v` (a path-dependent types),
which means that each _instance_ of `Variable` contains its own unique `Ctx` type,
that cannot be mixed up with the `Ctx` from an other instance.


The `Code` class is contravariant in its `Ctx` type argument,
so that a term with a context `C` can be used as a term with a _more specific_ context `C' <: C`. 
For example, we have:  
`ClosedCode[Int] = Code[Int,Any]  <:  Code[Int, v.Ctx]  <:  Code[Int, v.Ctx & v1.Ctx]  <:  OpenCode[Int] = Code[Int,Nothing]`
where `A & B` is the type intersection of `A` and `B`
(which is equivalent to `A with B` in legacy Scala syntax).



## Free Variable Substitution

One can replace all the free occurrences of a variable `v`
in an open term `t`
by using the `subs` syntax below:

```tut
val t = code"$v0 + $v1 + 1"
val s = t.subs(v0) ~> code"42"
```

"Renaming" a free variable `v0` to `v1` is achieved by
substituting free occurrences of `v0` with code references to `v1`, as in:

```tut
t.subs(v0) ~> v1.toCode
```

**Note:** syntax `v.toCode` converts a variable `v` of type `Variable[T]`
to a code fragment of type `Code[T]`.
It is equivalent to `code"$v"`.

These operations will turn out to be crucial in the
[Code Rewritings](4-rewriting.html) section of this tutorial.



## Back to the Power Function


We saw that the `power` function defined above, when partially applied, yields a code generator which
takes a program of type `Double` and returns a program of the same type:

```tut
val p3 = power(3, _ : ClosedCode[Double])
p3(code"2.0")
```

What we would now like to have is a term of type `ClosedCode[Double => Double]`,
that we can compile and execute efficiently. 
To do this, we have to pass a variable reference to `power` instead of a closed term,
and to allow for this, we must make `power` polymorphic
in the context its `base` parameter (the body of the function does not need to change):

```tut:silent
def power[C](n: Int, x: Code[Double,C]): Code[Double,C] = {
  if (n > 0) code"${power(n-1, x)} * $x"
  else code"1.0"
}
```

We can now construct function, say for exponent `5`:

```tut
val pow5 = { val x = Variable[Double]; code"{ $x => ${power(5, x.toCode)} }" }
```

Squid actually has
[some syntax sugar](https://github.com/epfldata/squid/pull/53)
to allow writing the above as:

```tut:silent
val pow5 = code"(x: Double) => ${(x:Variable[Double]) => power(5, x.toCode)}"
```

(It would be easy to perform some rewriting after the fact to remove the useless `1.0 *` from the generated code, 
or even to partially evaluate it away automatically using an online transformer 
(like in [this example](https://github.com/epfldata/squid/blob/40fcf341959a49a7bcb52cf4ca613cac3a43d07b/src/test/scala/squid/ir/OnlineTransfo.scala#L68-L92)).
For more details on this subject, see the
[documentation on transformers](https://github.com/epfldata/squid/blob/master/doc/Transformers.md).)


We can now generate on the fly efficient code for calculating the 5th power of any `Double`:

```tut
val power5 = pow5.compile
power5(1.5)
```

The last line will execute the function `power5`,
which is really just as fast as if we had written out:

```tut:silent
val power5 = (x: Double) => 1.0 * x * x * x * x * x
```

