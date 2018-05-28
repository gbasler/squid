---
layout: docs
title:  "Basic Concepts"
section: "tutorial"
subposition: 1
---

# Basic Concepts

## Setting Up an IR

The only step required before we can start manipulating programs in Squid
is to instantiate an intermediate representation (IR)
to manipulate the programs in.
Different configurable IRs are provided by Squid with different properties,
pros, and cons that you should consider in light of your intended use cases.

The choice of an IR impacts the way programs are internally represented,
and thus affects the semantics of some operations performed on code fragments,
such as pattern-matching and transformation.
More on that is explained in the
[Intermediate Representation](https://github.com/epfldata/squid/blob/master/doc/Intermediate_Representations.md) reference;
for now, we will just use the simplest IR there is:
a simple abstract syntax tree (AST).
To instantiate this IR, proceed as follows:

```tut:silent
object IR extends squid.ir.SimpleAST
import IR.Predef._
```

Importing `IR.Predef._` brings into scope all the definitions we will need
in order to manipulate code in a type-safe way.
Note that it is important to _not_ import the whole content of the IR with `import IR._`,
as this would import many more definitions not useful to us.


## Types of Programs

As part of an IR's `Predef`, a few important types are defined.
They are summarized in the table below:

|Type|Equivalent to|Description|
|---|---|---|
|`Code[T,C]`||code fragments of type `T` and context `C`|
|`ClosedCode[T]`|`Code[T,Any]`|closed code fragments|
|`OpenCode[T]`|`Code[T,Nothing]`|open code fragments|
|`CodeType[T]`||program type representations|
|`Variable[T]`||variable symbols|

<br>
Note that the `Code[+Typ,-Ctx]` class is covariant 
<!-- in its first parameter -->
in `Typ` and contravariant in `Ctx`,
so for any `T`, `C` and `D`,
we have the subtyping relations
`ClosedCode[T] <: Code[T,C] <: Code[T, C & D] <: OpenCode[T]`
<!-- Moreover, for any  -->
where `C & D` is the intersection of types `C` and `D`,
equivalent to `C with D` in Scala.
These relations are explained and justified later in the Squid tutorial,
but intuitively a _closed_ code fragment is one that can be used in _any_ context (`C = Any`),
while an _open_ code fragment is one that _no_ context (`C = Nothing`)
can be statically guaranteed to satisfy.
The `&` type operator is defined in `squid.utils`,
so to use it you will have to import `squid.utils._`
(or, more selectively `import squid.utils.&`).

## Quasiquotation and Code Composition

<!-- note: the old QQ tutorial had much more bla bla here: https://github.com/epfldata/squid/blob/master/doc/tuto/Quasiquotes.md#introduction -->

A quasiquote is a quote in which some "holes" are left to be filled in by arbitrary values.
The most well-known example of quasiquotation in Scala is interpolated string literals,
of syntax `s"..."` in which `$` is used to denote holes.
<!-- Consider the following example: -->
For example:

```tut
val a = s"2"
s"($a + $a) * 2"  // inserts 'a' into two quasiquote holes
```

The primary way of manipulating programs in Squid is to use _code_ quasiquotes,
which are like interpolated string literals,
except that instead of representing a sequence of characters,
they represent fragments of programs that are internally encoded in an intermediate representation (IR).

To use quasiquotes, do not forget to import your `IR.Predef._`, as explained above.
The syntax is `code"..."`, as shown below:

```tut
val a = code"2"
code"($a + $a) * 2"
```

Looking at the types in the REPL session above,
we can see that we are manipulating _closed terms_ of type `Int`.
We also notice that the textual representation of the printed term
is different from the one we used in the original quasiquote,
because the term is internally represented in a normalized form by our IR,
where any information about parentheses and operator-syntax is lost.

The `Const` function can be used to _lift_ normal values to terms (code values):

```tut
Const(42)
List(1,2,3).map(n => code"${Const(n)}.toDouble")
```


## Quasi​_code_ Syntax Alternative
<!-- ^ note: there is a zero-length space between 'Quasi' and '_code_'  -->

The syntax `code{ ... $(x)... }`, referred to as _quasicode_,
can be used in place of `code" ... ${x}... "` in expression position (but not in `case` patterns).
This has several advantages, including good IDE integration – autocompletion, syntax and error highlighting, jump-to-definition, etc.

In order to use this syntax, import `IR.Quasicodes._` first. For example:

```tut
import IR.Quasicodes._
val a = code{2}
code{($(a) + $(a)) * 2}
```

