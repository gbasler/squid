---
layout: docs
title:  "Debugging Quasiquotes and Rewritings"
section: "tutorial"
subposition: 6
---

# Debugging Quasiquotes and Rewritings

```tut:invisible
import example.doc.IR
import IR.Predef._
import IR.Quasicodes._
```

## Debugging Quasiquotes

Replace `code"..."` with `dbg_code"..."` and look at the compilation messages.
They may help you figure out whether the quasiquote was compiled as expected
(sometimes, you may notice that the wrong types or implicit arguments may have been inferred).
Similarly, there is `dbg_rewrite` for debugging code rewritings.

## Debugging Rewritings

To see why rewritings did not fire or how they fired,
consider adding printing statements in the right-hand side of the rewriting,
or using `IR.debugFor(... code ...)`
to print detailed logging information about what happens in a limited scope.

See also the next section:

## Debugging Pattern Matching

When something doesn't match (for example, in a rewrite rule pattern),
it is usually best to isolate the failure and reproduced it
with a minimal pattern matching example enclosed in `IR.debugFor(... code ...)`.

For example, the following session uses logging info to pintpoint that the match failed because
mehod symbol `scala.Int.toFloat` is different from `scala.Int.toDouble`.

```tut
val pgrm = code"Some(123.toDouble.toString)"
IR.debugFor(pgrm match { case code"Some(123.toFloat.toString)" => case _ => })
```


