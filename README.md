# Systems Compiler Paradise -- Safe Scopes Prototype
Goals of this prototype:

 - experiment with safe open term extraction (static scope encoding)

 - try not to use polymorphic embedding approach

    use a single method for DSL features, instead of one method per feature

 - see if we can use invariant Reps and explicit subsumption nodes

    it may turn out to be very useful for targetting languages without subtyping
    
    in fact, let's try to have untyped reps, just to see!
    we use a distinct abstraction for type-safe quasiquotes (type `Quoted[Typ,Scp]`)
	

## TODO

Test to see if Dotty handles better overloaded names,
eg `AnyRef{val x: Int}{val x: String}`
-- would it make `AnyRef{val x: Int & String}`?


pass implicits explicitly, make SimpleEmbedding report typeEv implicit problems in a clean way


## Misc


allow the definition of open terms right away
they can be achieved anyway with something like:
```
val open: Q[Int,{x: String}] = dsl"(x: String) => x.length" match {
  case dsl"(x: String) => $body" => body
}
```
A syntax could be:
```
dsl[{x: String}]("x.length")
```
Problem: doesn't allow nice splice... or maybe it does, but with a bit more work

This is obviously a way to do open terms, but it may be a bit verbose
-- althoug probably safer with respect to generality (cf: quid of a language where we don't have the right compositionality for the other approach to work?)






