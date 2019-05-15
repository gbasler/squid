package squid.haskellopt

import squid.utils._
import ammonite.ops._

object TestHarness {
  
  val dumpFolder = pwd/'haskellopt/'target/'dump
  
  def pipeline(filePath: Path): Unit = {
    val writePath = pwd/'haskellopt_gen/RelPath(filePath.baseName+".opt.hs")
    if (exists(writePath)) rm(writePath)
    val go = new GraphOpt
    val mod = go.loadFromDump(filePath)
    println(s"=== PHASE ${mod.modPhase} ===")
    
    var ite = 0
    do {
      println(s"--- Graph ${ite} ---")
      println(mod.show)
      println(go.Graph.scheduleRec(mod))
      ite += 1
    } while (mod.letReps.exists(go.Graph.simplifyGraph(_, recurse = false)))
    
    //println(mod.show)
    val sch = go.Graph.scheduleRec(mod)
    println("--- Scheduled ---")
    println(sch)
    println("--- Generated ---")
    val modStr = sch.toHaskell(go.imports.toList.sorted)
    println(modStr)
    write(writePath, modStr, createFolders = true)
  }
  
  def apply(testName: String): Unit = {
    val srcPath = pwd/'haskellopt/'src/'test/'haskell/(testName+".hs")
    val md5Path = dumpFolder/(testName+".md5")
    val md5 = read! md5Path optionIf (exists! md5Path)
    
    val srcMd5 = %%('md5, srcPath)(pwd).out.string
    
    if (!md5.contains(srcMd5)) {
      println(s"Compiling $srcPath...")
      
      import ghcdump._
      CallGHC(
        srcPath,
        dumpFolder,
        //opt = true,
      )
      
      if (exists! md5Path) rm! md5Path
      write(md5Path, srcMd5)
    }
    
    pipeline(dumpFolder/(testName+".pass-0000.cbor"))
    pipeline(dumpFolder/(testName+".pass-0001.cbor"))
    
  }
  
}
object Clean extends App {
  println(s"Cleaning...")
  ls(TestHarness.dumpFolder) |? (_.ext === "md5") |! { p => println(s"Removing $p"); os.remove(p) }
}
