# Typing Considerations


## Embedding

Ideally, we should have
`typeEvImplicit` in `BaseDefs`
so IRs don't have to implement the current scope lookup to find extracted type variables.

Additionally, the method generating type reps (currently in `ScalaTypingMacros.typeRep`)
shoud be able to query if a given type rep has already been generated
and should return a list of definitions for the required rep plus its sub-reps,
so it does not do the work several times.



## Pattern Matching, a.k.a Extraction

### Method Parameters

Conceptually, `Option.empty[Int]` and `Option.empty[String]` are equivalent,
so they should extract each other (ie: compare equivalent under `=~=`).

Since type parameters in Scala are erased, if the difference in type parameter had any meaningfulness,
it would be reflected through a value argument (for example, an implicit argument),
and this difference would be detected while extracting the _values_.

However, currently we _need_ to perform the type parameter extraction,
as `Option.empty[$t]` should be able to extract `t = Int` from `Option.empty[Int]`.


Note: currently (as of Aug'17), we match method type parameters covariantly, which is unsound,
as it makes us match, for example, `val a = ArrayBuffer[Int]; ...` with `case ir"val $arr = ArrayBuffer[Any](); $body:$bt"`.
The right thing to do would be to determine the matching variance based on the method signature (looking at the return type of method `ArrayBuffer.apply`, in this case).  
A workaround for now is to use a pattern guard: `if arr.typ =:= irTypeOf[ArrayBuffer[Any]]`.





### Typing of Extraction Holes

It's normal to have insertions typed as (`base.$$[Nothing]:T`) when using quasiquotes (and looking at the debugging output); the type parameter is only here to support quasicode.

For example, the debugging output can look like:

```
Typed[xt]: squid.anf.analysis.BlockHelpers.placeHolder[xt](BlockHelpers.this.Predef.base.$$[Nothing](scala.Symbol.apply("ANON_HOLE$macro$15")))
```


