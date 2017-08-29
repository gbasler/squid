# Typing Pattern Matching


## Basics

See paper submission on reusable quasiquotes.

We use on-the-fly local classes to type check pattern type holes (using `c.typecheck`).
Note that these local classes do not even appear in the final program 
– they are only used to type check the shallow code; 
including them in the generated boilerplate without symbol mismatches and owner chain corruptions would be tricky.
This does not seem to cause any problems down the line, 
except sometimes in IntelliJ.
Finally, when using `dbg_ir` we actually see them in the generated code, 
but these trees are just here for helping the user understanding, 
and are not actually used by scalac.


## Coercions based on subtyping knowledge obtained during pattern matching

This is to enable the same kind of reasonong about GADT type refinements in pattern matching.
See `PatMatTypeCoercion.scala` for a good example.

This is based on `<:<` evidence, so it's rather limited. 
For example, given `A <:< B` there is no way to coerce a `List[A]` as a `List[B]`
(one would have to use an explicit `map` operation)...


