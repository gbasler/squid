---
layout: docs
title:  "Getting Started"
section: "tutorial"
subposition: 0
---

# Getting Started

You can try all the examples in this tutorial
by launching a Scala REPL session,<sup>[[1]](#howtorepl)</sup>
and copy-pasting the following lines:

```tut:silent
object IR extends squid.ir.SimpleAST
import IR.Predef._
```

<a name="howtorepl">[1]</a> 
the simplest way of launching a REPL session is to clone the Squid repository,
and in the cloned folder type `sbt console`
(only requires having [SBT](https://www.scala-sbt.org/) installed).

To find complete installation info, please refer to
the [Installation – Getting Started](/squid/#installation--getting-started) section of the home page.

