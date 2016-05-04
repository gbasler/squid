# **Squid** — Scala Quoted DSL's
<!-- TODOs
# Squid – Scala DSL Compiler

Rename Quoted/dsl to Code/code
eg:
  val n = code"42"; code"$n * 2" : Code[Int,{}]
or:
  val n = rep"42"; rep"$n * 2" : Rep[Int,{}]
-->

## Introduction

**Squid** – which stands for the approximative contraction of **Sc**ala **Qu**oted **D**SL's –
is a framework for the optimization and compilation of
Domain-Specific Languages (DSL) defined in Scala, based around the concept of quasiquotes.
Squid implements both compile-time and runtime Multi-Stage Programming
as well as type-safe pattern-matching of program fragments and type-safe program transformation.
<!-- TODO: introduce first an example of optimization with @optimize -->

[TODO: complete presentation stub] 

Squid quasiquotes have the following properties:

 - They are **well-typed**: ensure gen pgrm well-typed

 - Hygienic: scope extrusion

 - Support for extraction

 - They support **multiple stages**: run

 - They are **IR-independent**: can use different backends




## Syntax and Semantics of Quasiquotes

### Basics

```scala
> val f = dsl"(x: Int) => x + 1"
f: Quoted[Int => Int, {}] = dsl"(x: Int) => x + 1"
```


```scala
> val n = dsl"42"; val m = dsl"2 + $n"
n: Quoted[Int, {}] = dsl"42"
m: Quoted[Int, {}] = dsl"2 + 42"
> dsl"2 + ${ Constant(7*6) }"
res0: Quoted[Int, {}] = dsl"2 + 42"
```


### Term Composition


### Term Extraction


### Splicing

```scala
dsl"Seq(1,2,3)" match {
  case dsl"Seq[Int]($xs: _*)" =>  xs : Q[Seq[Int], {}]  }
```

```scala
dsl"Seq(1,2,3)" match {
  case dsl"Seq[Int](${xs: __*})" =>  xs : Seq[Q[Int, {}]]
  case dsl"Seq[Int]($xs*)" =>        xs : Seq[Q[Int, {}]]  }
```




### Open Terms


### Free variables

```scala
dsl"($$x: String).length"
```

they can be achieved anyway with something like:
```scala
//  open: Q[Int, {x: String}]
val open = dsl"(x: String) => x.length" match {
  case dsl"(x => $body: String)" => body
}
```


### Type Extraction


<!--def opt[C](ls: Q[List[_], C]) = ls match {-->
```scala
def opt[C](x: Q[Any, C]) = x match {
  case dsl"List[$t]($xs*).size" => dsl"${xs.size}"
  case _ => x
}
```




### Miscellaneous

#### Varargs



#### Alternative Unquote Syntax

```scala
val x = dsl"42"
dsl"Seq($x)" match {
  case dsl"Seq($$x)" => ...
}
```

Another example, featuring type alternative unquote

```scala
val x, y : Q[_,_] = ...
x match {
  case dsl"List[$t]($a,$b)" =>
    y match {
      case dsl"List[$$t]($c,$d)" =>
        dsl"List($a,$c)" // : Q[List[t], _]
    }
}
```

As a particular case, if used for the value, the one for the type becomes unnecessary

```scala
...
  case dsl"List[$t]($a,$b)" =>
    y match {
      case dsl"List($c,$$a)" =>
        dsl"List($a,$c)" // : Q[List[t], _]
    }
```


Note: this could be rewritten using *repeated holes*...


#### Repeated Holes



```scala
val x, y : Q[_,_] = ...
dsl"$x -> $y" match {
  case dsl"List[$t]($a,$b) -> List[t]($c,a)" =>
    dsl"List($a,$c)" // : Q[List[t], _]
}
```

order does not matter


#### Escaped Unquote Syntax

```scala
dsl""" println($$(dsl"1", dsl"2")) """
```


## Program Transformation

Example

```scala
object PowerOptim extends IR.TopDownTransfo {
  new Rewrite[Double] {
    def apply[C] = {
      case dsl"math.pow($x, ${Constant(n)})"
        if n.isValidInt && (0 <= n && n <= 16) =>
          power(n.toInt)(x)
    }
  }
}
```



## Compilation to Heterogeneous Targets


## Implementation Details

The way types are loaded – need a stable base


## References



