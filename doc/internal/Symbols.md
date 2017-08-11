# Symbols

## Method Symbols

One of the common gotchas when using QQs, 
especially when comparing terms for equivalence like in the tests,
is that overridden methods are considered as having distinct symbols,
and thus the default IR consider calls to them as different.

For example, a particularly annoying thing is that method `==` 
has a different symbol for `Any`, `AnyRef`, `Int`, etc.
What's particularly problematic with `==` is that it's a "special",
synthetic method.  
See the `squid.anf.transfo.EqualityNormalizer` for a good way to ignore these issue: the transformer replaces all `==` methods with
calls to the same `equals` method as defined on `Any`.




