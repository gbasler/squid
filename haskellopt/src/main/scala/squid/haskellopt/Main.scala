package squid
package haskellopt

import ammonite.ops._

// TODO CLI for haskellopt
object Main extends App {
  
  // Quick test:
  HaskellDumper(
    pwd/'haskellopt/'src/'test/'haskell/"Lists.hs",
    pwd/'haskellopt/'target/'dump,
  )
  
}
