# Hygienic Context Polymorphism

## Motivation

Better compositionality of context-polymorphic functions while enforcing hygiene.

Assumption:
we can do better than context disjointness implicits `A <> B`.

This should translate to transformers, that are currently only sound to apply on closed terms
(including in the `Return.recursing` pattern, which limits it greatly).
This is so that the free variables a transformer extrudes are not mixed with the ones already present in the term.


## Mechanisms

Pass around context evidence created on the spot that account for:
 * what part of the context was known by the caller (statically-known)
 * what the context actually contains (dynamically-known)

When a FV is introduced, if the dynamic base context already contains the same name,
we take a fresh name (for example appending a prime `'`).

On every usage of a quoted term (e.g., `subs` or `$`-insertion),
require a context implicit;
if previously-hidden variables are now public,
we can unify the renamed variables with the original variables, as this unicity is known statically.


## Example

```scala
def foo[C](r: IR[Int,C]): IR[Int,C{x:Int}] = {
  val r2 = ir"$r1 + (x? : Int) + (y? : Int)"
  println(r2)
  r2 subs 'y -> ir"42"
}
foo(ir"123") // prints ir"123 + x + y", returns ir"123 + x + 42"
val f = foo(ir"(x? : Int) + (y? : Int)") // prints ir"x + y + x' + y'", returns ir"x + y + x' + 42"
ir"$f" // returns ir"x + y + x + 42"
// ^ x' was unified because this was done in a context where 'x' is known to be in the base context
f subs x -> 666 // returns ir"666 + y + 666 + 42"
```

## Soundness Argument?

A formal development would be nice to make sure this all works out correclty.  
Language: lambda calc with quasiquotes, explicit free variables, contexts and _type intersection_.
No need for patmat (which could be viewed as elaborated syntax sugar).










