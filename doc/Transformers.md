# Squid Transformers

This document introduces the transformation API and capabilities of Squid.  
Prerequisite: [Squid Quasiquotes](/doc/tuto/Quasiquotes.md).  
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
which is already implemented by the main transformer classes and overridden by [transformer modifiers](#transformer-modifiers).

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
[Transformer Modifiers](#transformer-modifiers).


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
 such as ``case code"(${Const(`a`)}:Int"`` in a context where `val a = 3`;
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

We describe `TopDownTransformer` in the [Transformer Modifiers](#transformer-modifiers) section.


### Fine-Grained Transformation Control (experimental)

Squid provides some ways to control the way recursive transformers are applied:
`Abort`, `Return`, `Return.transforming` and `Return.recursing` –– [see here](/doc/reference/experimental/Transformation_Control.md) for more details.

However, these capabilities are experimental and currently, only the `SimpleANF`/`SchedulingANF` IRs handle all of them.



## Lowering Transformers

<!-- Lowering transformers (of class `squid.ir.Lowering`) a -->
Class `squid.ir.Lowering` provides built-in facilities to automatically inline functions defined in your libraries.
To do that, you first need to `@embed` the class or object of your choice and annotate its methods with a `@phase('SomePhaseName)`, 
then construct a `Lowering('SomePhaseName)` which effect is to perform the inlining
(n.b., by default only on the top-level term, _not_ recursively and _not_ in a fixed point unless modified with the appropriate traits).

Note that given some IR base object `Embedding`,
class `Embedding.Lowering` is equivalent to `Embedding.SelfTransformer with squid.ir.Lowering`.

As a full example, consider:

```scala
import squid.quasi.{phase,embed}

@embed
object MyLib {
  @phase('SomePhase)
  def f(x: Int) = 2 * x
}
object MyEmbedding extends squid.ir.SimpleAST {
  embed(MyLib)
}
object Inline_f extends MyEmbedding.Lowering('SomePhase) with TopDownTransformer
object MyTests extends App {
  import MyEmbedding.Predef._
  println(code"MyLib.f(MyLib.f(2))" transformWith Inline_f)
  // the above prints:
  //   code"""{
  //     val x_1 = {
  //       val x_0 = 2;
  //       (2).*(x_0)
  //     };
  //     (2).*(x_1)
  //   }"""
}
```

(Note: in order to remove the block structure, 
you can apply normalizing rewritings or use an [ANF IR](/doc/Intermediate%20Representations.md#the-a-normal-form-anf))


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

Prerequisite: [Intermediate Representations](/doc/Intermediate%20Representations.md).

It is possible to apply transformations on-the-fly, 
as soon as program sub-terms are constructed.

To do that, make your IR base extend `squid.ir.OnlineOptimizer`,
and define its `pipeline: Rep => Rep` method 
either by extending one of the above a transformer or by defining it yourself.
For example, your embedding could look like:

```scala
object NormEmbedding extends squid.ir.SimpleANF with OnlineOptimizer with squid.lang.ScalaCore {
  object Desug extends Lowering('Sugar)
  object Norm extends SelfTransformer with squid.anf.transfo.StandardNormalizer
  def pipeline = Desug.pipeline andThen Norm.pipeline
}
```





## Static Optimizers

<!-- ### Basics -->

Squid allows programs to be transformed and optimized at compile-time.
This is done via the `squid.StaticOptimizer` class, instantiated as follows:

```scala
object MyOptimizer extends StaticOptimizer[MyTransformer]
// where, for example:
class MyTransformer extends Embedding.SelfTransformer 
    with SimpleRuleBasedTransformer with TopDownTransformer {
  rewrite {
    case code"Nil.size"    => code"0"
    case code"List().size" => code"0"
  }
}
```

Then, _in a different project_ than the one where `MyTransformer` is defined,
use `MyOptimizer.optimize` to optimize pieces of code at compile-time!

```scala
import MyOptimizer.optimize
optimize { println(Nil.size) }
// ^ this is rewritten at compile-time into: { println(0) }
```

In order to see what is generated by `optimize`, you have two options:
 
 * replace `optimize` with `dbg_optimize`, which will print some information on the standard output during compilation; or
 
 * (more involved, [will eventually change](https://github.com/epfldata/squid/issues/19)) introduce an implicit of type `DumpFolder.type`
 (the name of that value will be used to determine the folder where dump files will be generated), for example ``implicit val `/tmp` = DumpFolder`` and then use
 `optimizeAs('MyOptTest){...}`. This will create a file `/tmp/MyOptTest` containing debugging information produced by the optimizer (only works if the optimizer is set to produce debugging info by overriding `Optimizer.setContext` and `Optimizer.wrapOptim`).
 

<!-- ### Details -->

In fact, `StaticOptimizer` can be applied not only on transformers, 
but more generally on any class that extends `squid.lang.Optimizer`
(such as `squid.ir.Transformer`, or an IR base that also extend `OnlineOptimizer` [as above](#OnlineTransformers)). 






