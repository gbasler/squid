# Quoted Types


## Variance

Unlike `Quoted`, `QuotedType` should be invariant
If it were covariant, it would be fine for extraction, since extraction can fail. For example:
```
val tp = quotedTypeOf[Int] : QuotedType[Any]
dsl"0.5" match {
  case dsl"$x: $$tp" => // won't match
}
```

However, we can also unquote types in construction, where it is clearly unsound:
```
val tp = quotedTypeOf[Int] : QuotedType[Any]
dsl"'ko : $tp".run  // compiles, but crashes, as we get:  dsl"'ko : Int" : Q[Any,{}]
```

One can always use types like `QuotedType[_ <: Person]` when needed.

Another advantage of invariance is that
it makes quoted types usable as implicit evidences.
(When covariant, it makes the implicit search constantly ambiguous.)




<!-- OLD

-->


