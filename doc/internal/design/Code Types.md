# Code Types


## Variance


### Soundness

Unlike `Code[_,C]`, the `CodeType[_]` type should be invariant.

If it were covariant, it would be fine for extraction, since extraction can fail. For example:
```scala
val tp = CodeTypeOf[Int] : CodeType[Any]
code"0.5" match {
  case code"$x: $$tp" => // won't match
}
```

However, we can also unquote types in construction, where it is clearly unsound:
```scala
val tp: CodeType[Any] = codeTypeOf[Int]
val ls: ClosedCode[List[Any]] = code"List[$tp](false)"
// ^ would type check but be wrong, as we'd get: ls = code"List[Int](false)"
```

One can always use types like `CodeType[_ <: Person]` when needed.

Another advantage of invariance is that
it makes quoted types usable as implicit evidences.
(When covariant, it makes the implicit search constantly ambiguous.)


### Limitations

This can lead to some surprises.
One intuitively thinks it should be possible to extract a `CodeTye[T]`
from an `x : Code[T,C]`, but in fact it's not,
because `x.Typ : Code[x.Typ,C]` and we only have `x.Typ <: T` (not an equality)
as `x` could have been up-casted after being constructed.

To make this kind of things possible,
one would need to carry around more precise code types, such as
`Code[T,C]{type Typ = T}`,
but this will currently not work as quasiquotes do not provide this more specific type.
They probably should, perhaps configurable in the `QuasiConfig`.
We could have a `PreciseCode[T,C] = Code[T,C]{type Typ = T}` synonym.

An alternative is to use a wrapper class, such as:

```scala
case class InvCode[T,C](cde: Code[T,C])(implicit val T: CodeType[T])
```


