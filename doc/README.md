# Squid Documentation

Since [this commit](https://github.com/epfldata/sc/commit/fac187dab32f406da4b2a9d2cc6250483256abbf),
Squid requires at least Scala 2.11.3.
Indeed, it turns out that 2.11.2 rejects definitions such as `object A { type T >: Nothing <: Any}` with "only classes can have declared but undefined members."

