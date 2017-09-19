# Squid Transformers

This document introduces the transformation API and capabilities of Squid.  
Prerequisite: [Squid Quasiquotes](doc/tuto/Quasiquotes.md).  
The associated examples can be found in the 
[example](/example/src/main/scala/example) folder.



## Annotation-Based Interface (to be released)

The simplest way to create transformers is to use annotation-based combinators,
like in:

```scala
@fixedPoint @bottomUp val MyTransformer = rewrite {
  case code"..." => ...
}
```

However, this interface is **currently in development**,
and will only be available in the next version of Squid, 0.2.  
In the meantime, one can build transformers using the more general tools described in the rest of this document.




## The Transformer Traits

The main trait for all low-level Squid transformers is `squid.ir.Transformer`.
This trait has a single abstract method `transform(rep: Rep): Rep` 
that describes how to process IR terms. (The internal representation of IR terms uses type `Rep`,
while `Code[T]` and `IR[T,C]` are the corresponding high-level types 
–– both have a `.rep` method to access their internal `Rep`.)

When designing and composing Squid transformers, 
one will generally not need to worry about the `transform` method, 
which is already implemented by the main transformer classes and overridden by [transformer modifiers](#TransformerModifiers).

A high-level transformer interface is also available as 
`squid.ir.IRTransformer`, which has one abstract method `transform[T,C](code: IR[T,C]): IR[T,C]`.
This interface is used by high-level Squid functionalities, so that users can use high-level types instead of `Rep`.



## Linking a Transformer to an Embedding

If you try to instantiate a `Transformer`-derived class while only providing an implementation for `transform`,
you will hit an error of the form _"object creation impossible, since value `base` in trait `Transformer` of type `squid.lang.InspectableBase` is not defined"_.
This is because the transformer needs to know on which IR base (the embedding object) it is supposed to apply.

To remedy this, assuming your IR base is named `Embedding`, either: 
 
 * declare a field `val base: Embedding.type = Embedding` inside your transformer class
 (using the explicit `Embedding.type` annotation is important, 
 to prevent Scala from losing path-dependent type information);
 
 * or make your transformer class extend `Embedding.SelfTransformer`, 
 which does exactly the above for you.



## Rule-Based Transformers

### Principles

The most useful subtype of `Transformer` is `squid.ir.RuleBasedTransformer`, 
implemented by `squid.ir.SimpleRuleBasedTransformer` and `squid.ir.FixPointRuleBasedTransformer`.
This interface provides the top-level `rewrite` macro that is used to register rewrite rules using the quasiquote syntax, 
and allows the IR to implement efficient matching and rewriting routines based on these rules.

`SimpleRuleBasedTransformer` tries to match a term with its rewrite rules once,
while `FixPointRuleBasedTransformer` tries to match it again and again as long as the program keeps changes.

For example, the following definition transforms an addition of one term to itself into a multiplication by `2`:

```scala
import Embedding.Predef._
object MyTransformer extends Embedding.SelfTransformer with SimpleRuleBasedTransformer {
  rewrite {
    case code"($a:Int) + (a:Int)"
      => code"$a; $a * 2"
      // ^ we insert `a` twice in the result just in case it contains side effects;
      //   in which case we need to execute them twice, as in the original program
  }
}
println(code"Nil.size + Nil.size".transformWith(MyTransformer))
// prints: code"scala.collection.immutable.Nil.size; scala.collection.immutable.Nil.size.*(2)"
```

Note that this transformer only affects the top-level term.
For example, it will leave the term `code"print(Nil.size+Nil.size)"` unchanged.
In order to automatically recurse down inside sub-terms, see the section on 
[Transformer Modifiers](#TransformerModifiers).


<!-- **Limitations:**  -->
### Limitations

The `rewrite` macro currently has a few limitations, including:
 
 * every pattern must be of the form `case code"..."`; 
 if you want to apply a custom extractor on the top-level term, 
 it is possible to write: `case code"${ CustomXtor(...) } : $t"`.
 where `$t` helps the Scala type-checker to determine which type is being matched 
 –– without it, Squid will complain that it cannot determine the type of the pattern.
 
 * pattern-matching inside the body of a rewrite rule is not yet supported; 
 to work around that limitation, 
 extract the body of the rewrite rule into a separate helper function.
 
 * the shape of patterns inside unquotes still has some unsupported cases,
 such as `case code"(${Const(`a`)}:Int"` in a context where `val a = 3`;
 however, note that ``case code"(${Const(42)}:Int"`` works.

For a full description of the limitations of different Squid constructs, 
see the [Limitations](Limitations.md) document.


### Term-Level Rewriting

<!-- **Note:**  -->
The term-level `rewrite` and `fix_rewrite` macros, which have syntax `t rewrite { ... }` 
are actually just syntax sugar for defining a transformer using the "top-level" `rewrite` syntax we have described above.

Given a term `t` belonging to some IR base `Embedding`, syntax `t rewrite { ... }` basically expands into:

```scala
object LocalTransformer extends Embedding.SelfTransformer 
    with SimpleRuleBasedTransformer with TopDownTransformer {
  rewrite { ... }
}
t.transformWith(LocalTransformer)
```

The `t fix_rewrite { ... }` syntax expands to similar code, 
but with `FixPointRuleBasedTransformer` instead of `SimpleRuleBasedTransformer`.

We describe `TopDownTransformer` in the [Transformer Modifiers](#TransformerModifiers) section.





## Lowering Transformers

TODO



## Transformer Modifiers

Mixing some transformer modifier trait inside a transformer class 
can alter that transformer's semantics by overriding its `transform` method.

<!-- ### TopDownTransformer and BottomUpTransformer -->
### Top-Down and Bottom-Up Strategies

The traits 
`squid.ir.TopDownTransformer`
and
`squid.ir.BottomUpTransformer`
turn any transformer into a transformer that does the same action 
but on every sub-term of the given program, recursively.

For example,
`FixPointRuleBasedTransformer with TopDownTransformer`
will traverse a term top-down (starting from the whole program, then recursing into each sub-expression), and for each sub-term it will apply the rewrite rules until a fixed point is reached.



### Fixed Point

Trait `FixPointTransformer` modifies a transformer to apply over and over 
until the program stops changing.

**Note:** 
`SimpleRuleBasedTransformer with FixPointTransformer` 
has the same semantics as 
`FixPointRuleBasedTransformer`, 
but the latter a little more robust and efficient.

It is important to understand the difference between,
for example,
`FixPointTransformer with TopDownTransformer`
and 
`TopDownTransformer with FixPointTransformer`.
The former applies the fixed point on each sub-term, 
while the latter applies the fixed point on the whole top-down traversal
(so it may traverse a term top-down several times!).
It is possible to combine them all as
`FixPointRuleBasedTransformer with TopDownTransformer with FixPointTransformer`
to do both fixed points actions (on each sub-terms and on the whole traversal).



### Wrapper Composition

Not exactly a transformer modifier, 
but class `squid.lang.InspectableBase.TransformerWrapper`, 
can be used to compose different transformers and apply them one after the other.

For example,
`new Embedding.TransformerWrapper(T0,T1,...) with BottomUpTransformer`
applies `T0` and then `T1` (etc.) on each sub-term of a program,
in a bottom-up order (starting from the leaves).





## Online Transformers

Prerequisite: [Intermediate Representations](/doc/Intermediate Representations.md).

TODO



## Static Optimizers

TODO

`squid.lang.Optimizer`

`pipeline: Rep => Rep`






