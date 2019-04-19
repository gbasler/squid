# Sea of Scopes Graph (a.k.a, Graph IR)

This folder contains the SSG/Graph IR implementation.

* File `Graph.scala` contains the basic definitions of the graph
    along with the definitions required to implement an intermediate representation in Squid.

* File `GraphRewriting.scala` deals with (1) simplifying a program graph,
    which includes applying β reduction exhaustively;
    and (2) applying user-defined rewrite rules,
    which are defined as in
    [this example](/src/test/scala/squid/ir/graph3/GraphRewritingTests.scala#L86).

* File `GraphScheduling.scala` deals essentially with the scheduling process
    to safely convert a program graph back into a normal program
    (this is implemented as a function that converts a program graph into any existing Squid IR).

The code is relatively short,
because most of the compiler infrastructure
(such as program node definitions and standard transformations)
is inherited from the Squid framework.

The **unit tests** for the SSG can be found in
[this file](/src/test/scala/squid/ir/graph3/GraphRewritingTests.scala),
and can be run with the [SBT](https://www.scala-sbt.org/) command:

    sbt testOnly squid.ir.graph3.GraphRewritingTests

in the top-level folder of the repository.

For optimizing the example Haskell program,
we used an encoding of the relevant Haskell functions in Scala, defined in
[this file](/src/test/scala/squid/ir/graph2/Prelude.scala),
we used our IR to apply optimizations, and then output a Haskell program using
[this function](/src/test/scala/squid/ir/graph2/ToHaskell.scala),
to be fed to GHC (version 8.4.3 with the flags `-O2 -fforce-recomp -fno-strictness`).
We measured the speed of the program using the Criterion performance measurement Haskell library.


 