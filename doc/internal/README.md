# Internal Doc


## Persisting Symbol References

In order to refer to symbols at a later stage, I used to save their `sym.fullName` and `sym.info` (eqt to `sym.typeSignature`) as strings.
However, it turns out Scala's runtime relection mirror doesn't display types the same way! For example, it shows `java.lang.String` instead of `String` for the compile-time mirror.

Workaround: I now save the `showRaw` of the erasure of the `sym.info`. It seems to work so far. (I use the erasure to simplify the type expression...)



## Implementing `run`

### Java Reflective Invocation [fail]

I tried using the Scala method symbol to load the method's class through reflection, and call the corresponding Java method.
This did not work for things like `Int.toDouble`, because Scala does some blackm magic with pseudo-primitive types,
and it doesn't seem possible to call methods on them (we get a Java Invocation exception).

Example prototype code:
```
val self2 = self map runRep getOrElse null
val cls = Class.forName(mtd.owner.fullName)
val m = cls.getMethod(mtd.name.toString)
m.invoke(self2) // ignoring arguments for now
```


### Code Gen (works, but more heavyweight)

Transform the program to a Scala tree and `eval` it with a Scala compiler toolbox.





