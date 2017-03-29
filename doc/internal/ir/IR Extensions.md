# IR Extensions

## Intro

The idea is to store lazy values in nodes that can be accessed by program transformations

Ideally, process should be extensible in at least two dimensions: add different analyses, and add cases to an existing analysis


## Usage Statistics and Forward References

In many cases, it would be useful to be able to see
 * how many times a given value is used
 * more powerful: _where_ a given value is used.. give the associated expressions
 – this would only go one level deep (one method invokation); which is expected in ANF anyway  
EG: remove mutable variables that are only accessed or only set; easily specialize data structures, etc. <- although these can all already be done with basic QQ (but not necessarily efficiently) 


## Number of executions approximation

Each node should store a mapping from bindings it contains to number of times the binding will be "executed", relatively to the current node.

Build these inductively. For a loop on a known range, multiply the map values in the body. When closure invokation behaviour is unknown, propagate `None`.

Accumulated iteration numbers could be of IR type (not necessarily constant/statically known) as long as the expression is pure.
For example, it could be `ir"42"` as well as `ir"ls.size * 2"`

NOTE: this would hint that in ANF it would be beneficial to represent effectful statements (expressions which result is discarded) as bound values; so we can refer to them uniquely...  
An `ignore(x:Any):Unit` function could help (it's useful to see the value cannot be used, as it is of type `Unit`; also makes code-gen nicer).

(Could also make a special Let construct in ANF, more efficient than a redex; or relegate that to a future FastANF alternative IR.)


 
## Representing Special Branching constructs like ITE and patmat

The exclusivity of each branch of a patmat (where ITE could be represented as a patmat)
can be very useful to represent/express.  
EG: variable value propagation can be precise knowing this info.

It is easy to encode ad-hoc variable value propagation, but we'd like the process to be reusable and extensible (cf. introduction of new control flow constructs/methods)


### The forgetful elaboration approach

One way (not ideally efficient)
would be to have an extensible transformer that lowers various constructs into simpler ones that ignore their fine-grained semantics and only encode the interesting properties, like control-flow etc.

For example, both `if (blah) X else Y` and `v match { case Some(w) => X case _ => Y }`
would reduce to `CFlow.xor(X,Y)`

Note: the abstraction could be in a simplistic, efficient IR to minimize overhead  
Additionally, it could be stored lazily in each node (see sections above)

Then use patmat to match the code with this simpler abstraction 

Tricky part is combining the resulting abstraction with a traversal and transformation of the real code...  
Just use symbol maps?

Could have a `rebuilder` bundled with the extracted stuff, that allows the extracted arguments to be "re-packaged" into the real code...

```
ir"if (blah) X else Y" match {
  case CFlow.xor(rebuild,x,y) =>
    rebuild(foo(x),bar(y))
}
// ->  ir"if (blah) fooed-X else barred-Y"
```

...which leads us to:

### The _abstract constructs_ approach

Use custom extractors that don't just _match_ but also allow to _reconstruct_ values after having transformed the subexpressions.

See the first prototypes @ f76aeaa58b325df6726d5dd53c8321312b747f14

Ideally, should eventually look like:

```
ir"if (blah) X else Y" match {
  case ir"${t @ CFlow.xor(x,y)}" =>
    t.rebuild(foo(x),bar(y))
}
// ->  ir"if (blah) fooed-X else barred-Y"
```

(Currently the `@` pattern binder is not well-supported in `rewrite`.
There might also be path-dependency problems since `CFlow.xor` should introduce its own sub-contexts.)







