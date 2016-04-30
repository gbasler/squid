# Typing Considerations


## Embedding

Ideally, we should have
`typeEvImplicit` in `BaseDefs`
so IRs don't have to implement the current scope lookup to find extracted type variables.

Additionally, the method generating type reps (currently in `ScalaTypingMacros.typeRep`)
shoud be able to query if a given type rep has already been generated
and should return a list of definitions for the required rep plus its sub-reps,
so it does not do the work several times.



## Misc

### Method Parameters

Conceptually, `Option.empty[Int]` and `Option.empty[String]` are equivalent,
so they should extract each other (ie: compare equivalent under `=~=`).

Since type parameters in Scala are erased, if the difference in type parameter had any meaningfulness,
it would be reflected through a value argument (for example, an implicit argument),
and this difference would be detected while extracting the _values_.

However, currently we _need_ to perform the type parameter extraction,
as `Option.empty[$t]` should be able to extract `t = Int` from `Option.empty[Int]`.

