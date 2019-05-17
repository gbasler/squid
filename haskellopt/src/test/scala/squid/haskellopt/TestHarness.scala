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
      //println(go.Graph.scheduleRec(mod))
      ite += 1
    } while (mod.letReps.exists(go.Graph.simplifyGraph(_, recurse = false)))
    
    //println(mod.show)
    val sch = go.Graph.scheduleRec(mod)
    println("--- Scheduled ---")
    
    //go.Graph.HaskellScheduleDebug debugFor
    println(sch)
    
    //import squid.ir.graph.{SimpleASTBackend => AST}
    //println("REF: "+AST.showRep(go.Graph.treeInSimpleASTBackend(mod.toplvlRep))) // diverges in graphs with recursion
    
    println("--- Generated ---")
    val moduleStr = sch.toHaskell(go.imports.toList.sorted)
    println(moduleStr)
    write(writePath, moduleStr, createFolders = true)
    
    %%(ghcdump.CallGHC.ensureExec('ghc), writePath)(pwd)
  }
  
  def apply(testName: String, passes: List[String] = List("0000", "0001")): Unit = {
    import ghcdump._
    
    val srcPath = pwd/'haskellopt/'src/'test/'haskell/(testName+".hs")
    val md5Path = dumpFolder/(testName+".md5")
    val md5 = read! md5Path optionIf (exists! md5Path)
    
    val srcMd5 = %%(CallGHC.ensureExec('md5), srcPath)(pwd).out.string
    
    if (!md5.contains(srcMd5)) {
      println(s"Compiling $srcPath...")
      
      CallGHC(
        srcPath,
        dumpFolder,
        //opt = true,
      )
      
      if (exists! md5Path) rm! md5Path
      write(md5Path, srcMd5)
    }
    
    for (idxStr <- passes)
      pipeline(dumpFolder/(testName+s".pass-$idxStr.cbor"))
    
  }
  
}
object Clean extends App {
  println(s"Cleaning...")
  ls(TestHarness.dumpFolder) |? (_.ext === "md5") |! { p => println(s"Removing $p"); os.remove(p) }
}
