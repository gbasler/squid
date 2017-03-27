# Hygienic Context Polymorphism

## Motivation

Better compositionality of context-polymorphic functions while enforcing hygiene.

Assumption:
we can do better than context disjointness implicits `A <> B`.

This should translate to transformers, that are currently only sound to apply on closed terms in general
(including in the `Return.recursing` pattern, which limits it greatly).
This is so that the free variables a transformer extrudes are not mixed with the ones already present in the term but unknown to the transformer.



## Problems with Erased Weakening

It turns out that the main problem in coming up with a sound scheme for context polymorphism is _erased weakening_,
coming from the fact that `IR[Typ,Ctx]` is contravariant in `Ctx`, so that `IR[T,A] <: IR[T,B]` as long as `A :> B`.

The need for reified weakening can be seen as there are two valid abstraction paths from `{x:Int}` to `C & {x:Int}`,
direct and indirect:

```
{x:Int} -> C{x:Int}       where C == {}
{x:Int} -> C -> C{x:Int}  where C == {x:Int}
```

Say we start with simple term `ir"x?:Int" : IR[Int,{x:Int}]`, 
and by either weakening path end up calling
a function:
```scala
def foo[C](q:IR[Int,C{x:Int}]): IR[Int,C] = q subs 'x -> ir"0"`.
```
The function should behave differently depending on which path was taken.
Indeed:
 * if it _does not_ substitute `x`, the direct caller sees the result type as `IR[Int,C]` and `C` may be `{}`, in which case there is an unsoundness (closed term is not really closed).
 * if it _does_ substitute `x`, the indirect caller ends up in a paradoxical state, particularly evident if the free variable hidden in `C` was `ir"x?:String"` instead of `ir"x?:Int"`,
 in which case we have an obvious type unsoundness.
 It is not acceptable to use intensional type analysis to do the substitution only if the types align, 
 as it means transformers will behave non-parametrically depending on the names and types contained in abstract contexts.

Therefore, we probably need to drop the contravariance of `IR[Typ,_]` 
and use implicit conversions instead, 
which does have several downsides (no more general subtyping relations that extend to variant containers).


## Proposed Scheme

### Idea

Context "brands" are used at runtime to tag free variable with the
abstract contexts that are supposed to contain them,
therefore hiding them from operations that affect visible free variables 
(such as capture and substitution).


Following is a simple example of why reified weakening is needed:

```scala
def foo[C:Ctx](a:IR[Int,C]):IR[Int,C{x:Int}] = a  // weakening happens here
def bar[C:Ctx](a:IR[Int,C]) = foo(a) subs 'x -> ir"42"
val q = ir"x?:String"  // problem: FV x is not tagged at this point; need reified weakening to tag it!
foo(q) 
```

Note: if `a` in `foo` is otherwise used, it will get its FVs tagged without a problem:

```scala
def foo[C:Ctx](a:IR[Int,C]):IR[Int,C{x:Int}] = ir"$a + (x?:Int)"
// ^ the FV `x:String` in `a` is tagged before `a` is used here!
```




### Mechanism

Generate fresh "brands" as context implicits for ground contexts.
Intersection types aggregate the brands of their components (set union).
When a term with context `A_i & ... & {x_i:T_i}` is used (including when simply weakened!), 
statically require its context evidence ,
and brand all its FVs that are not yet branded and are not statically known to be in the term (not an `x_i`), 
with the brand of the abstract context bases (all `A_i`).
Additionally, remove FV brands that are no longer associated with any `A_i`.



### More Examples


#### Notation

```scala
def foo[C:Ctx,D:Ctx](a:IR[Int,C{x:Int}],b:IR[Int,D]): IR[Int,C & D] = ir"${a subs 'x -> ir"42"} + $b"
```

Is denoted by:

```
foo: [C,D] C{x:Int} -> D -> C & D = "${_ subs 'x -> ""} + $_"
```



#### Substitution Leaves Abstracted/Hidden FVs untouched

```
foo: [C,D] C -> D -> C & D = ir"$_ + $_"
bar0: [C] C -> C = q -> foo(q,q)
bar1: [C] C -> C{x} = q -> foo(q,"x?")

bar1("(x?:String).length"): {x:String & Int} == "(x?:String).length + x?" 

bar2 [C] C -> C = q -> foo(q,"x?") subs 'x -> ""

bar2("(x?:String).length"): {x:String} == "(x?:String).length + 42"
```






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










