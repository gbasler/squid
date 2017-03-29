# Transformations


## Caveats

### Transformation Order

Transformation of let-bindings used to start with the body, then the bound expression,
because `val a = v; b` is encoded as `((a:T) => b)(v)`!

I have changed this by adding a special case.
The idea is that traversing the program in ANF should be a (very coarse) approximation of evaluation order – at least as long as no function takes a closure to execute it later on at some arbitrary point

### Traversal Control Operators

These are `Abort(msg)`, `Return(code)` and the `Return.transforming` overloads.
All interrupt the current rewriting, and any code coming after them will become unreachable (unless the underlying exceptions are caught).

Note: semantics of `Abort(msg)` should be the same as `Return(x)` where `x` is equivalent to the term that was matched...  
One difference is that `Abort` has a `Nothing` return type, and will crash if invoked outside RwR.  
Q: for practicality, shouldn't Return also be of type Nothing?








