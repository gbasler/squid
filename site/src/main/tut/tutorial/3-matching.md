---
layout: docs
title:  "Code Pattern Matching"
section: "tutorial"
subposition: 3
---

# Code Pattern Matching

```tut:invisible
object IR extends squid.ir.SimpleAST
import IR.Predef._
```

## Code as Data, in a Statically Typed Setting

We have seen that metaprogramming in
Squid is statically typed and prevents the occurrence of type mismatches or unbound variable errors,
in the tradition of _MetaML_.
On the other hand, in the tradition of _Lisp_, Squid views code as (executable) data that one can _inspect_ (or _analyse_) and _rewrite_.
This is made possible in a type-safe way by statically tracking context requirements, as presented in
[the previous section](2-staging.html#contexts-and-open-terms).

The functional way of analysing data and extracting patterns from it is to use pattern matching.
In Squid, one can pattern-match program fragments just like one pattern-matches data.
Holes in patterns behave like extractors, allowing programmers to take apart code expressions:
 
```tut
val pgrm = code"readInt * 2 + 1"
val m = pgrm match { case code"($n: Int) + 1" => n }
val code"($m: Int) + 1" = pgrm
```

(In the code above, the second REPL line is equivalent to the first one, using Scala syntactic sugar.)

Pattern-matching is sensitive to contexts.
For example, extracting a subterm in a context where a binding exists
will give to the extracted subterm a type that reflects the potential dependency:

```tut:silent
val funBody = code"(x: Int) => x + 1" match { case code"($y: Int) => $b" => b }
```

The code above matches a lambda that binds some parameter `y` of type `Int` in some body `b`;
it extracts two values: `y` of type `Variable[Int]` and `b` of type `Code[Int, y.Ctx]`.
Note that since `y` is only defined withing the pattern matching branch,
the type inferred by Scala for `funBody`,
which is _dependent_ on `y`, is an existential:

```tut:invisible
import scala.language.existentials
```

```tut
funBody
```

In practice, we will avoid losing track of the variables our code values depend upon, as above,
because existentials in Scala are not very useful.

**Note:** by now, it should be clear that _**the names of bound variable do not matter**_
― we extracted function body `b` using `y` as the parameter,
even though the code we matched was written using a parameter named `x`.

Just like `Const` can be used to construct constants into code by _lifting_ current-stage values,
we can also use it to _extract_ constant current-stage values:

```tut
funBody match { case code"($z: Int) + (${Const(k)}: Int)" => k }
```

In the examples above, it is necessary to provide the type of extracted terms,
because the `+` operation
in Scala is ad-hoc polymorphic. Writing a pattern like `case code"($x: Long) * $y"` will give us an error such as:
```
ambiguous reference to overloaded definition,
both method * in class Long of type (x: Char)Long
and  method * in class Long of type (x: Byte)Long
match argument types (Nothing)
```

**Caveat**: Because of
[the infamous `any2stringadd`](https://issues.scala-lang.org/browse/SI-194)
implicit conversion, syntax `code"($x: Int) + $y"` will make Scala think that
`y` has type `String` and `+` is string concatenation...



## Note on Parametric Polymorphism

In general, to use a non-concrete type `T` within a quasiquote,
one has to provide an implicit _type representation_ evidence, of type `CodeType[T]`.
For example, the `mkEmpty` function below returns a program that builds an empty list for any given type `T`:

```tut
def mkEmpty[T](implicit ev: CodeType[T]) = code"List.empty[T]"
mkEmpty[Option[Int]]
```

As we can see, `Option[Int]` is persisted in the resulting code fragment (it is not erased).
This is because code fragments are _internally_ typed in addition to being statically typed
― they carry type representations at runtime, which is necessary for precise pattern matching.

Note that Scala also provides the equivalent shortcut syntax for passing implicit evidence:

```tut:silent
def mkEmpty[T:CodeType] = code"List.empty[T]"
```

**Note for advanced users:** it is sometimes possible to retrieve type representations
from existing code values so that no implicit type evidence is needed.
Below is an example:

<!-- 
```tut:silent
def mkSingle[T:CodeType,C](x: Code[T,C]) = code"List($x)"
``` -->
```tut:silent
def mkSingle[T,C](x: Code[T,C]) = code"List[${x.Typ}](${x.withUnderlyingTyp})"
```

Because `Code` is covariant, but `CodeType` is invariant,
the type representation stored inside a term `x` of type `Code[T,C]`
does _not_ necessarily have type `CodeType[T]`;
instead, in general it will have type `CodeType[T0]` for some `T0 <: T`.
This `T0` can be referred to as `x.Typ`.
In the above,
`x` has type `Code[T,C]` but a `Code[x.Typ,C]` is required, which can be obtained by calling `x.withUnderlyingTyp`.


## Matching Types

In order to make code manipulation polymorphic, we need a way to extract types as well as values.
This is done with a similar syntax.
Consider the following rewriting, which implements β-reduction (lambda application inlining):

```tut:silent
def beta[T:CodeType,C](x: Code[T,C]) = x match {
  case code"(($v: $t) => $body: T)($a)" => body.subs(v) ~> a
}
```
```tut
beta(code"((x: Int) => x + x + 1)(readInt)")
```

In the right-hand side of this `case` expression,
extracted value `body` has type `Code[T, C & v.Ctx]` (see the tutorial section on
[contexts](2-staging.html#contexts-and-open-terms) to understand this syntax),
and extracted variable `v` has type `Variable[t.Typ]`
where `t` is the local extracted type representation extracted from the pattern.
Note that `t` is a _value_ representing a type, not a type per se. 
So one cannot write `Option[Code[t,C]]`, for example; instead one has to write `Option[Code[t.Typ,C]]`
where path-dependent type `t.Typ` _reflects_ in the type system what `t` represents at runtime,
a.k.a what `t` _reifies_.
Perhaps paradoxically, `t` can be viewed as having type `t: CodeType[t.Typ]`.


Thanks to an implicit macro provided by Squid,
extracted types will be picked up automatically from the context in which they appear and used as implicit type representation evidence.

**Note**:
Of course, in the presence of effects,
the β-reduction above is unsound as it duplicates the argument.
An alternative implementation could be:

```tut:silent
def beta[T:CodeType,C](x: Code[T,C]) = x match {
  case code"(($v: $t) => $body: T)($a)" => code"val $v = $a; $body" // theoretically equivalent
}
```
```tut
beta(code"((x: Int) => x + x + 1)(readInt)")
```

Another way of avoiding the problem with code duplication would be to use an ANF IR
(see reference on [intermediatde representation](https://github.com/epfldata/squid/blob/master/doc/Intermediate_Representations.md)),
in which every non-trivial subexpressions is bound to a temporary values,
so this problem would not exist.



## Comparing Types and Terms

Squid provides a way of comparing program framents and their type representations for equivalence and subtyping.
This is done with the `=~=` and `<:<` functions respectively,
as demonstrated below:

```tut
code"val x = 0; x+1" =~= code"val y = 0; y+1"
code"List(1,2,3)".Typ <:< codeTypeOf[List[AnyVal]]
```

Pattern matching can also make subtyping hypotheses about extracted terms,
which is sometimes required for making a pattern type check.
This is done with the `$t where (t <:< ...)` and `$t where (... <:< t)` syntaxes,
as below:

```tut
def test[T:CodeType,C](cde: Code[T,C]) = cde match {
  case code"($self: Option[$t where (t <:< T)]).getOrElse[T]($other)" =>
    code"$self.fold($other)(identity)"
  case _ => cde
}
test(code"Some(1).getOrElse[Any]('ko)")
```

(In Scala's `Option[A]` class, method `getOrElse` has type `[B >: A](default: => B): B`.)

**Caveat:** currently, these type bounds are
[not dynamically checked](https://github.com/epfldata/squid/issues/15),
so you will need to check them manually if necessary, as in:

```scala
  case code"$cde: $t where (t <:< List[Any])" =>  // wrong, cf. current limitation
  case code"$cde: $t where (t <:< List[Any])" if t <:< codeTypeOf[List[Any]] => // ok
```



## Note on the Legacy Nominal Context System

Squid will let you match binding without _extracting_ a first-class `Variable` value,
as in the following:

```tut
val funBody = code"(x: Int) => x + 1" match { case code"(y: Int) => $b" => b }
```

This works,
and results in an open term whose context is a structural type `{val y: Int}`,
which can be bound later:

```tut
code"List(1,2,3).foreach(y => println($funBody))"
```

In fact, you can even create such open terms manually, using the `?y` syntax:

```tut
assert(funBody =~= code"(?y:Int) + 1")
```

However, **this approach is strongly discouraged**,
as it is not hygienic.
Indeed, combined with context abstraction, it can lead to type mismatches in generated code
(for details on this, see [our POPL 2018 paper [3]](/squid/#popl18)).

Some properties of contexts as structural types:
Squid quasiquotes will on occasion "merge" types together using intersection types (in current Scala, written using `with`).
We have `C{val x: A; val y: Int} with D{val x: B}` equivalent to `(C with D){val x: A with B; val y: Int}`.
Also, double refinement `C{val x: A}{val y: B}` is just `C{val x: A; val y: B}`.




