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



## Subtleties with repeated holes and contexts

In general, we make the assumption that holes in a pattern should always
be given a code value if the pattern is to match some program fragment.
This means that, while an IR could easily add special semantics to an
`AND` operator in patterns to indicate that two sub-patterns match,
the same is not true of an `OR` operator
(because holes can leave on only one of the branches).

In addition, even if some hole appears
on both sides of an hypothetical `OR`,
the way we assign context requirements to repeated holes get in the way:
we use the _intersection_ of the context requirements in each position,
such that in pattern `code"val x: List[Int] = $xs; ($e:List[Int]) ++ (xs:List[Int])"`,
`xs` has type `Code[List[Int],OuterContext]`,
whereas the second occurrence alone would have type `Code[List[Int],OuterContext{val x: List[Int]}]`.
For this to be sound, we assume the IR's notion of term equality to be exhaustive (ie: free variables appearing in one
term should be reflected in free variables appearing in another term deemed equal to it).


