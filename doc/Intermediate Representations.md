# Intermediate Representations in Squid

The Squid framework is _generic_ (or _reusable_) in the Intermediate Representation (IR).
This means Squid quasiquotes and infrastructure can manipulate programs encoded in different underlying representations.
In addition, it is possible to customize an IR by adding online transformations that apply on the fly, as terms are constructed.
<a name="qsr"></a>

The associated examples can be found in the 
[example](/example/src/main/scala/example) folder.


## Summary

### 1. [Types of Intermediate Representations](#ir-types)
### 2. [Online Rewriting and Normalization](#rwg-norm)
### 3. [Automatic Method Embedding](#mtd-embed)
### 4. [Details of Squid-Provided IRs](#details)












<a name="ir-types"></a>
## Types of Intermediate Representations

By default, Squid provides a small set of customizable general-purpose IRs.
It is also possible to implement your own IR, or bind an existing one 
–– such as the IR of a domains-specific language (DSL).


### The Simple Abstract Syntax Tree (AST)

This is the simplest IR configuration, where programs are represented as trees without any changes from the source –– except, of course, that the trees are fully typed and the names are fully qualified.

Declare your embedding as:
```scala
object Embedding extends squid.ir.SimpleAST
```

<!-- **Details:**  -->
<!-- #### Details -->


### The A-Normal Form (ANF)

ANF is similar to AST (in fact, it extends AST), but ANF additionally re-normalizes terms on the fly as they are constructed, 
so that all effectful sub-expressions are let-bound.


#### Basic Functionalities

The name of the corresponding base class is `squid.ir.SimpleANF`.
It is possible to extend this class so as to:

 * Specify a set of standard Scala effects (which include some of Scala's predefined libraries). For this, mix in the `squid.ir.StandardEffects` trait.
 
 * Let-bind pure expressions during code generation/printing, in order to avoid re-computations. For this, use `squid.ir.SchedulingANF` instead of `squid.ir.SimpleANF`.  
 **Caveat:** this functionality is still in its infancy; 
 it is currently sub-optimal in a number of ways and will be improved in the future.

For example, to benefit from standard effects and pure expressions scheduling,
declare your embedding as:
```scala
import squid.ir.{SchedulingANF, StandardEffects}
object Embedding extends SchedulingANF with StandardEffects
```


#### Effect System

The effect system is basic but surprisingly potent. 
It is manual and does not yet have effects inference, though that should be possible to implement.  
For a more in-depth discussion, see [[2]](/README.md#gpce17), §4.2.

There are two sorts of effects: 

 * _Immediate effects_ are those that are visible as soon as the expression is evaluated, 
   such as in `println(3)`.
 
 * _Latent effects_ are those that only become visible later, for example expression `t = () => println(3)` is considered "pure" (no immediate effects) but has latent effect since applying `t` as in `t()` will produce an immediate effect.
 
Methods that are said to be **_transparent_** "execute" the latent effect of their arguments. 
In case the arguments are effect-less, the method is indeed "referentially transparent". Examples of transparent methods are `List.map` –– if that method is called with a function argument that has latent effect, this call is effectful; and `Function.apply` –– if that method is called on a (function) this-parameter that has latent effect, that call is effectful.

Methods that are said to be **_transparency-propagating_** propagate the latent effect of their arguments unchanged. An example is `Stream.map` because streams have lazy semantics:
if that method is called with a function that has latent effect, 
it will return a stream object with latent effect. 
Only _consumers_ that force evaluation such as `Stream.fold` have immediate effect.

Note that to be perfectly sound, we would need a more precise way to describe effects (with a stratified notion of latency), but this simple system makes concessions for the sake of simplicity.


#### Adding Custom Effects by Annotation
<!-- ##### By Annotation -->

To add custom effects, annotate the methods you know to be transparent and transparency-propagating with `squid.lib.transparent` and `squid.lib.transparencyPropagating` respectively.

For example, in:
```scala
class Baz { @transparencyPropagating def baz: Baz = this }
```

Method `baz` is now considered transparent, 
so `code"(b:Baz) => b.baz.baz.baz"` will create a code representation that does _not_ let-bind any of the intermediate sub-expressions –– printing it shows

```scala
code"((b_0: example.doc.IntermediateRepresentations.Baz) => b_0.baz.baz.baz)"
```

Without the annotation, we would get:

```scala
code"""((b_0: example.doc.IntermediateRepresentations.Baz) => {
  val x_1 = b_0.baz;
  val x_2 = x_1.baz;
  x_2.baz
})"""
```

There are currently some limitations to effects annotations:
they are not yet taken into consideration when applied to constructors and fields, 
in which case a workaround is to use `apply` methods in the companion objects and field accessors,
which can both be annotated.  
Another way to document effects is also presented next.


<!-- ##### Manually -->
<!-- #### Adding Custom Effects Manually -->
<!-- #### Adding Custom Effects Reflectively -->
#### Adding Custom Effects by Reflection

Say we have the following class:

```scala
case class Foo() { def foo = 123 }
```

In order to document that `Foo.foo` and `Foo.apply` are transparent without modifying the definition of `Foo`, 
we can some reflective code into the body of our `Embedding` object, as follows:

```scala
object Embedding extends SchedulingANF with StandardEffects {
  // For the method in class Foo:
  transparentMtds += methodSymbol[Foo]("foo")
  // For the method in the companion object Foo:
  transparentMtds += methodSymbol[Foo.type]("apply")
}
```

There is also a way to specify "transparent types", 
that are considered unable to hold latent effects and whose methods are all considered to be transparent. So one could have written:

```scala
  transparentTyps += typeSymbol[Foo]
  transparentTyps += typeSymbol[Foo.type]
```


#### Subtleties of the Effect System


Consider the following uses of the class `Foo` defined and effect-annotated above:

```scala
code"(new Foo).foo + 1"
code"(f:Foo) => f.foo + 1"
```

In both cases the `.foo` expression will be let-bound. 
This is because `new Foo` is not seen as pure (there is in fact currently no way to annotate constructors), and therefore could hide latent effects – and `foo` being transparent, it could be effectful. Similarly, the `Foo` parameter `f` has unknown origin and could very well carry latent effects.

Both of these cases are solved if we use the `transparentTyps += typeSymbol[Foo]` line proposed above, as this indicates that `Foo` cannot carry latent effects.









### Future Work

We are currently designing a new ANF-based IR that will make it easier to perform code analysis and rewriting, and that will also be much more performant –– the current IRs are designed for extensibility and correctness first, so they are quite bloated and suboptimal with respect to performance.



### Making or Binding Your Own IR

It is possible to make an entirely new IR or bind an existing one, 
by implementing various object algebras such as `Base`, `IntermediateBase` and `InspectableBase`
depending on your needs.  
For more details, see our paper [[1]](/README.md#scala17) and see also the sub-project `sc-backend`, which defines a binding for a pre-existing IR in the style of LMS.












<a name="rwg-norm"></a>
## Online Rewriting and Normalization



### Example: Online Partial Evaluation

TODO




<a name="mtd-embed"></a>
## Automatic Method Embedding

TODO




<a name="details"></a>
## Details of Squid-Provided IRs

_In order to use the IRs, it is **not** required to know what follows, 
but this information might be useful to advanced users who wish to sometimes bypass the high-level quasiquote-based interface._

TODO











