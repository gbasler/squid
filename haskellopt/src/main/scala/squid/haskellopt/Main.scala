package squid
package haskellopt

import ammonite.ops._

// TODO CLI for haskellopt
object Main extends App {
  
  // Quick test:
  
  val mod = DumpReader(pwd/'haskellopt/'target/'dump/"Lists.pass-0000.cbor", DumpPrinter)
  
  // Note: with -O, GHC converts lists (even list literals!) to build/foldr, at pass 1:
  //val mod = DumpReader(pwd/'haskellopt/'target/'dump/"Lists.pass-0001.cbor", DumpPrinter)
  
  println(mod.moduleName)
  println(mod.modulePhase)
  println("Bindings:"+mod.moduleTopBindings.map("\n\t"+_.str).mkString)
  
}

object MainAll extends App {
  HaskellDumper(
    pwd/'haskellopt/'src/'test/'haskell/"Lists.hs",
    pwd/'haskellopt/'target/'dump,
  )
  
  Main.main(args)
}
