# Squid-Fusion

## Revamped Collections

### Simultaneous Aggregates

To allow for maximal performance,
Squid collections are fundamentally on-demand – computation does not happen until a folding operation is ran.
To make it clear where and when actual traversals happen, methods such as `count`, `sum` and `fold` return an aggregate `Aggr[T]` instead of just `T`, on which `run` must be called to get the result of type `T`.

Another advantage is that different aggregates can be composed together modularly



#### Example

What options does one have to aggregate different things in the current standard collections?
<!--Say one wants to aggregate all negative and all positive elements of a list into two new lists.-->

Say one wants to aggregate all `Left` and `Right` elements of a `List[Either[A,B]]` into two new lists.

In specific cases such as this one, there may exist a function that does what we want, such as `partition`.
But `partition` is boolean-blind. `ls.partition(_.isLeft)` returns two `List[Either[A,B]]` instead of `List[Left[A,Nothing]]` and `List[Right[Nothing,B]]` or even `List[A]` and `List[B]`. It is unfortunately not possible to `collect` both lists _during their construction_ so that does not happen.

Moreover, say one now wants to also know the sizes to these lists.
In Scala, `list.size` is linear, so it's a pity to call `.size` _after_ we have just traversed the two lists!

use fold:
reimplement all the aggregates _and_ pay for ridiculous boxing!
use an imperative loop: at least don't pay for boxing, but also reimplement the folds.

_**In Squid-Fusion, one simply writes:**_

```scala
def splitLeftRight[A,B](ls: List[Either[A,B]]): (List[A],List[B],Int) = {
  val p = ls.toProducer
  val traversal = for {
    n  <- p.count
    as <- p collect { case Left(a)  => a } toList
    bs <- p collect { case Right(b) => b } toList
  } yield (as,bs,n)
  traversal.run
}
```

TODO show perf improvement vs other approahces; + perfs after `optimize`

Naturally, better to stay with SF constructs than going back and forth with ScalaCol constructs.




### Generalized Splitting, a.k.a _Repeating_

Word count per sentence

```
(p: Producer[String]) =>
  p.repeat( _.asProducer.takeUntil('.')
                        .repeat(_.takeUntil(' '))
                        .filter(_.nonEmpty.run)
                        .count.run ) : Producer[Int]
```

Pair by 3, dropping first one: `p.repeat(_ take 3 drop 1)`




## See Also

[fs2](https://github.com/functional-streams-for-scala/fs2): Compositional, streaming I/O library for Scala

Akka Streams, and "streamz: A library that supports the conversion of Akka Stream Sources, Flows and Sinks to and from FS2 Streams, Pipes and Sinks, respectively. It also supports the usage of Apache Camel endpoints in FS2 Streams and Akka Stream Sources, Flows and SubFlows."

