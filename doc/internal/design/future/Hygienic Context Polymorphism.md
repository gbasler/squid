# Hygienic Context Polymorphism

## Motivation

Better compositionality of context-polymorphic functions while enforcing hygiene.

Assumption:
we can do better than context disjointness implicits `A <> B`, 
which are inadequate for things like functions recursively extruding bindings
(to do that, we currently have to use a "placeholder" trick similar to unchecked gensym
– generated placeholders left in the end program will prevent it from compiling).

This hygiene improvement should translate to transformers. 
They are currently only sound to apply on closed terms in general
(including in the `Return.recursing` pattern, which limits it greatly).
This is because the free variables a transformer extrudes are not mixed with the ones already present in the term but unknown to the transformer.



## Simple Example

```scala
def foo[C:Ctx](r: IR[Int,C]): IR[Int,C{x:Int}] = {
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


## Proposed Scheme

### Idea: Reified Contexts and Negative Free Variable Tagging

A context evidence contain the set of FVs in the associated context 
as well as an identifying "brand". 
Context brands are used at runtime to tag free variables with the
abstract contexts that they are known **_not_** to belong to,
therefore allowing operations that affect only _visible_ free variables
(such as capture and substitution) to discern which variables they can
safely act on.

Following are some simple examples:

```scala
def foo[C:Ctx](a:IR[Int,C{x:Int}]):IR[Int,C] = a subs 'x -> ir"42"
def bar[C:Ctx](a:IR[Int,C]) = foo[C](a)
// ^ Note: [C] in defn of `bar` needed because Scala type inference fails here

val q = ir"(x?:String).length"
bar(q) // i.e. bar[{x: String}]
-> q // no subs happens cf `foo` sees `x` is in C and is not tagged (current impl does the wrong rw)
bar[{val x:String;val y:Int}](q) // same, returns q

foo(ir"x?:Int") // i.e. foo[Any](ir"x?:Int")
-> ir"42" // `foo` sees that C does not have an `x` so the FV cannot be in C; subs happens
foo[{x:Int}](ir"x?:Int") // `x` assumed to be in C, not in its refining clause (arbitrary, but sound)
-> ir"x?:Int" // no substitution happens as `x` is in C and is not tagged

// (note: these need explicit type args:)
foo[{y: String}](ir"(y?:String).length + (x?:Int)")
-> ir"(y?:String).length + 42" // no particular problem ({x:Int; y:String} =:= {y:String}{x:Int})
foo[{x: Int; y: String}](ir"(y?:String).length + (x?:Int)") // idem as previous
foo[{x:String}](ir"x?:Int") // note: weakening {x:Int} -> {x:String}{x:Int} =:= {x:String & x:Int}
-> ir"x?:Int" // no subs happens; perhaps counter-intuitively there is no problem here as from 
// the POV of the caller what is passed in is a (weakened) term of type IR[Int,{x:String & x:Int}]

foo(q) // Type mismatch -- note: `q:IR[Int,({val x:String}){val x:Int}]` fails but supposedly-
// equivalent `q:IR[Int,{val x:String with Int}]` and `q:IR[Int,({val x:Int}){val x:String}]` work...
foo[{val x:String}](q) // same... fails
foo[{val x:Int}](q) // same... fails
// but assuming it worked (we have a weakening in the first one {x:String} -> {x:String}{x:Int} =:= {x:String & x:Int})
-> q // it will just not do any subs, since `x` is in C
```

<!-- Code to paste in REPL:
import scala.reflect.runtime.universe._
object Code extends squid.ir.SimpleAST
import Code.Predef._
import Code.Quasicodes._
def foo[C:TypeTag](a:IR[Int,C{val x:Int}]):IR[Int,C] = { println(typeOf[C]); a subs 'x -ir"42" }
def bar[C:TypeTag](a:IR[Int,C]) = { println(typeOf[C]); foo[C](a) }
foo[{val x:String}](ir"x?:Int")
-->

<!-- REDUNDANT:

```scala
def foo[C:Ctx](a:IR[Int,C]):IR[Int,C{x:Int}] = a  // weakening happens here, C -> C{x:Int}
def bar[C:Ctx](a:IR[Int,C]) = foo(a) subs 'x -> ir"42"
val q = ir"x?:String"
bar(q) // as above, `bar` sees `x?` in C and not tagged
-> q
```

-->

Slightly more interesting example: `a` in `foo` is extended with a FV that will get tagged:

```scala
def foo[C:Ctx](a:IR[Int,C]):IR[Int,C{x:Int}] = ir"$a + (x?:Int)"
// ^ the FV `x?:Int` in the new term is tagged as "not in C" on construction!
def bar[C:Ctx](a:IR[Int,C]) = foo(a) subs 'x -> ir"42"
bar(q) // `bar` won't subs FV `x?:String` as it is considered to be in C -- but it does subs `x?:Int`
 -> ir"(x?:String).length + 42"
```



### Mechanism

Generate a fresh "brand" along with a set of names as context implicit for any ground contexts.
Intersection types aggregate the brands and names of their components (set unions).

When a term `a` of context `A_i & ... & {x_i:T_i}` is merged (composed) with a term `b` of context `B_i & ... & {y_i:S_i}`, 
statically require their context evidence,
and before insertion/composition brand all the FVs in `a` 
with all `B_i` brands that are not `A_i` brands, 
and vice versa for `b`.
(A refinement would be to only brand FV `x` of `a` with `B_i` only if it actually contains the `x` name.)

By "merged"/"composed" we includes doing composition within open code, 
as in `ir"$a + (x?:Int)"` which is treated like `ir"$a + ${ ir"x?:Int" : IR[Int,{x:Int}] }"`.

Additionally, remove FV brands that are no longer associated 
with any abstract base context of the target context of the composition.



### More Examples


#### Simplified Notation

```scala
def foo[C:Ctx,D:Ctx](a:IR[Int,C{x:Int}],b:IR[Int,D]): IR[Int,C & D] = ir"${a subs 'x -> ir"42"} + $b"
```

Is denoted by:

```
foo: [C,D] C{x:Int} => D => C & D = "${_ subs 'x => ""} + $_"
```



#### Substitution Leaves Abstracted/Hidden FVs untouched

```
foo: [C,D] C => D => C & D = ir"$_ + $_"
bar0: [C] C => C = q => foo(q,q)
bar1: [C] C => C{x} = q => foo(q,"x?")

a = bar1("(x?:String).length") : {x: String & Int}
  foo[{x:String}#1,{x:Int}#2]
  -> "(x?{#2}:String).length + x?{#1}"
-> "(x?{#2}:String).length + x?{#1}"
a subs 'x -> "???"  // subs "42" does not type-check here; expected type is `String & Int`!!
-> "(???:String).length + ???"

baz: [C] C => C = bar1(_) subs 'x -> ""

baz("(x?:String).length") : {x: String}
  bar1[{x:String}#1]("(x?:String).length")
    foo[{x:String}#1,{x:Int}#2]
    -> "(x?{#2}:String).length + x?{#1}"
  -> "(x?{#2}:String).length + x?{#1}"
  subs 'x  // can see that `x?{#1}` is not in #1 but `x?{#2}` ~> `x?` is
  -> "(x?:String).length + 42"
-> "(x?:String).length + 42"

bar2: [C] C => C = q => foo(q,"x?") subs 'x -> ""

bar2("(x?:String).length"): {x:String} == "(x?:String).length + 42"
-> "(x?:String).length + 42" // same as for `baz` above 
```


<!-- REPHRASED ABOVE:
#### Originally Hard Problem

```scala
def foo[C:Ctx,D:Ctx](a:IR[Int,C],b:IR[Int,D]): IR[Int,C & D] = ir"$a + $b"
def bar[C:Ctx](q:IR[Int,C]) = foo(q,ir"x?:Int") subs 'x -> ir"123"
bar(ir"(x?:Int)+42") // should equal ir"((x?:Int)+42)+123"
```

Will work out because `ir"$a + $b"` in `foo[C={x}#1,D={x}#2]` creates `ir"(x?{#2} + 42) + x?{#1}"`
and once in `foo[C={x}#1]` where `#2` does not exist anymore we get `ir"(x? + 42) + x?{#1}"`, 
meaning that only the second `x?` is known not to be in `C`, so the subs operates and returns `ir"((x?:Int)+42)+123"`.
-->


<!-- OLD STUFF, NOT UP TO DATE:

#### Variations

```
foo__: [C] C        -> C        = "$_ + 1"
fooXX: [C] C{x:Int} -> C{x:Int} = "$_ + 1"
fooX_: [C] C{x:Int} -> C        = "$_ + 1" subs 'x -> ""
foo_X: [C] C        -> C{x:Int} = "$_ + x?"
fooXX2:[C] C{x:Int} -> C{x:Int} = "$_ + x?"
foo_B: [C] C{x:Int} -> C        = "val x = 123; $_ + x"

val a = ir"x?:Int" // Rep(x?),{x}
val b = foo__(a)   // Rep(x?+1),{}
ir"$b+2"           // Rep(x?+1+2),{x}
ir"$b+(x?:Int)"    // Rep(x?+1+x?),{x}
// ^ because we see the target context contains `x`, it is not shadowed
val c = ir"x?:Int"
ir"$b+$c"  // both static contexts refer to `x` so no shadowing

val b = foo_X(a)   // Rep(x? + x?1),{}  -- `x?1` shadowing of `x` cf static ctx of `q` did not know of `x`
ir"$b+2"           // Rep(x? + x? + 2),{x}  -- `x?1` flattened to `x?0` (ie `x?`)
// because target context {x:Int} contains `x` and shadowing level of `x` in associated 
// local context evidence {x->0} is `0`

val b = foo_b(a)   // Rep(val x_0 = 123; + x? + x_0)

val b = fooXX2(a)  // Rep(x?+x?)
// ^ inserted `x?:Int` and `q` both have `x` in their static context, so no shadowing

val b = fooX_(a)   // Rep(42+1)
```

-->


#### From the paper

```scala
def intro[C:Ctx](n: Code[Int, C]): Code[String,C{s:String}] = code" (s? : String) take $n " 
def outro[C:Ctx](m: Code[String, C{ s: String }]): Code[String=>String,C]  = code" (s: String) => $m "
def compose[C](x: Code[Int, C]) = outro(intro(x))

val problem = ir"val s = 123; ${ compose(code"s? : Int") }" // ie: compose[{s:Int}]
compose[{s}#1](code"s? : Int")
  intro[{s}#1](code"s? : Int")
  -> code" s?{#1} take $s "
  outro[{s}#1](code" s?{#1} take s? ")
  -> code"(s_0: String) => s_0 take s?"
-> [same]
-> code"val s_1 = 123; (s_0: String) => s_0 take s_1"  // no problem!
```


#### More interesting example

See similar example in paper on MetaML and in modal type system for Lisp-like staging.

```scala
def rec[C:Ctx](n: Int, q: IR[Int,C]): IR[Int,C] =
  if (n == 0) q
  else ir"val x = ${Const(n)}; ${rec(n-1, ir"$q + (x?:Int)")}"

// ^ Note: should also work with `val fv = ir"x?:Int"; ...$fv...`

rec[{}#1](3,ir"42")
  rec[{}#1 & {x}#2](2,ir"42 + x?{#1}")  // note: could be just x? as #1 does not contain any `x`
    rec[{}#1 & {x}#2 & {x}#3](1,ir"42 + x?{#1} + x?{#1,#2}")
      rec[{}#1 & {x}#2 & {x}#3 & {x}#4](0,ir"42 + x?{#1} + x?{#1,#2} + x?{#1,#2,#3}")
      -> ir"42 + x?{#1} + x?{#1,#2} + x?{#1,#2,#3}"
      // ^ when inserted, we see only the last FV is marked as not belonging to any of {#1,#2,#3}
      // (which are the abstract context bases of the term in the current scope) 
      // so only that FV is supposed to be visible and is captured as x_0, giving ir"42 + x?{#1} + x?{#1,#2} + x_0"
    -> ir"val x_0 = 0; 42 + x?{#1} + x?{#1,#2} + x_0"
    // same for x?{#1,#2}
  -> ir"val x_1 = 1; val x_0 = 0; 42 + x?{#1} + x_1 + x_0"
  // same for x?{#1}
-> ir"val x_2 = 2; val x_1 = 1; val x_0 = 0; 42 + x_2 + x_1 + x_0"
```


--- 

Note: in current implem, we get unhygienic binding mixup:

```scala
> { def rec[C](n: Int, q: IR[Int,C]): IR[Int,C with AnyRef] = if (n == 0) q
      else ir"val x = ${Const(n)}; ${rec(n-1, ir"$q + (x?:Int)")}" }
defined function rec
> rec(0,ir"42")
res4: IR[Int, Any with AnyRef] = ir"42"
> rec(1,ir"42")
res5: IR[Int, Any with AnyRef] = ir"""{
  val x_0 = 1;
  (42).+(x_0)
}"""
> rec(2,ir"42")
res6: IR[Int, Any with AnyRef] = ir"""{
  val x_0 = 2;
  val x_1 = 1;
  (42).+(x_1).+(x_1)
}"""
> rec(3,ir"42")
res7: IR[Int, Any with AnyRef] = ir"""{
  val x_0 = 3;
  val x_1 = 2;
  val x_2 = 1;
  (42).+(x_2).+(x_2).+(x_2)
}"""
```

<!-- ACTUALLY NEEDS SOME HELP FOR TYPING:

```scala
> { def rec[C](n: Int, q: IR[Int,C{}]): IR[Int,C with AnyRef] = if (n == 0) q
      else ir"val x = ${Const(n)}; ${rec[C{val x:Int}](n-1, ir"$q + (x?:Int)")}" }
```

-->


Question: could context eivdence be picked up from local variables of compatible context?
(Similar to what happens for type evidence now.)
Would that be desirable, too confusing or producing problems?



## Soundness Argument?

A formal development would be nice to make sure this all works out correclty.  
Language: lambda calc with quasiquotes, explicit free variables, contexts and _type intersection_.
No need for patmat (which could be viewed as elaborated syntax sugar).




## Older Considerations

### Problems with Erased Weakening

**EDIT:** this was solved differently, by combining negative occurrences and name sets.

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
def foo[C](q:IR[Int,C{x:Int}]): IR[Int,C] = q subs 'x -> ir"0"
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








