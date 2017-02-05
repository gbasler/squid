# Transforming Code

## Term Rewriting

### Basic Operators

`rewrite` and `fix_rewrite`

other traversal orders...


### Transformation Control Operators

`Abort`, `Return`, `Return.transforming` and the generalization `Return.recursing`.

#### Caveat about `Return.transforming` and `Return.recursing`

Contrary to using implicit top-down recursion (bundled with `rewrite` by default),
these operators can lead to surprising results because they somewhat lack hygiene
if they are used to recursively extract a binding with the same extractor case.

For example, consider the following REPL session,
and notice that the version not using an explicit `Return.transforming` 
has the expected result:

```scala
> ir"val a = readInt; val b = readInt; print(a)" rewrite {
    case ir"val x = readInt; $body: $bt" =>
      Return.transforming(body)(body => ir"val x = readDouble.toInt; $body")
  }
res0: ir"val x_0 = readDouble.toInt; val x_1 = readDouble.toInt; print(x_1)"  // prints `x_1` ... oops

> ir"val a = readInt; val b = readInt; print(a)" rewrite {
    case ir"val x = readInt; $body: $bt" =>
      ir"val x = readDouble.toInt; $body"
  }
res1: ir"val x_0 = readDouble.toInt; val x_1 = readDouble.toInt; print(x_0)"  // prints `x_0`, okay
```

## Transformer Classes

underlie term rewriting





