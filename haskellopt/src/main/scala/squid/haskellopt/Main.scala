package squid
package haskellopt

import squid.utils._
import ghcdump.{CallGHC, Printer, Reader}
import ammonite.ops._
import squid.haskellopt.MainOpt.go

// TODO CLI for haskellopt
object Main extends App {
  
  // Quick test:
  
  //val mod = Reader(pwd/'haskellopt/'target/'dump/"Basics.pass-0000.cbor", Printer)
  //val mod = Reader(pwd/'haskellopt/'target/'dump/"Basics.pass-0001.cbor", Printer)
  //val mod = Reader(pwd/'haskellopt/'target/'dump/"Lists.pass-0000.cbor", Printer)
  //val mod = Reader(pwd/'haskellopt/'target/'dump/"Lists.pass-0001.cbor", Printer)
  val mod = Reader(pwd/'haskellopt/'target/'dump/"HigherOrderRec2.pass-0000.cbor", Printer)
  
  // Note: with -O, GHC converts lists (even list literals!) to build/foldr, at pass 1:
  //val mod = DumpReader(pwd/'haskellopt/'target/'dump/"Lists.pass-0001.cbor", DumpPrinter)
  
  println(mod.moduleName)
  println(mod.modulePhase)
  println("Bindings:"+mod.moduleTopBindings.map("\n\t"+_.str).mkString)
  
}

object MainAll extends App {
  CallGHC(
    //pwd/'haskellopt/'src/'test/'haskell/"Lists.hs",
    pwd/'haskellopt/'src/'test/'haskell/"Basics.hs",
    pwd/'haskellopt/'target/'dump,
    //opt = true,
  )
  
  Main.main(args)
  //MainOpt.main(args)
}

object MainOpt extends App {
  
  val go = new GraphLoader
  val pgrm = go.loadFromDump(pwd/'haskellopt/'target/'dump/"Basics.pass-0000.cbor")
  //val pgrm = go.loadFromDump(pwd/'haskellopt/'target/'dump/"Lists.pass-0000.cbor")
  //val pgrm = go.loadFromDump(pwd/'haskellopt/'target/'dump/"Lists.pass-0001.cbor")
  println(pgrm.show)
  val sch = go.Graph.scheduleRec(pgrm)
  println("--- Scheduled ---")
  println(sch)
  println("--- Generated ---")
  println(sch.toHaskell(go.imports.toList, "?"))
  
  //val ls1 = pgrm.lets("ls1")
  //println(ls1.showGraph)
  //println(go.Graph.scheduleRec(ls1))
  
}
