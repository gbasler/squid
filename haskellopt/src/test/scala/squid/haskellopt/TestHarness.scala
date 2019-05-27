package squid.haskellopt

import squid.utils._
import ammonite.ops._

object TestHarness {
  
  val dumpFolder = pwd/'haskellopt/'target/'dump
  val genFolder = pwd/'haskellopt_gen
  
  def pipeline(filePath: Path, compileResult: Bool, dumpGraph: Bool): Unit = {
    
    val writePath_hs = genFolder/RelPath(filePath.baseName+".opt.hs")
    if (exists(writePath_hs)) rm(writePath_hs)
    
    val writePath_graph = genFolder/RelPath(filePath.baseName+".opt.graph")
    if (exists(writePath_graph)) rm(writePath_graph)
    
    val go = new GraphLoader
    val mod = go.loadFromDump(filePath)
    println(s"=== PHASE ${mod.modPhase} ===")
    
    var ite = 0
    do {
      /*
      println(s"--- Graph ${ite} ---")
      println(mod.show)
      */
      //println(go.Graph.scheduleRec(mod))
      ite += 1
    } while (mod.letReps.exists(go.Graph.simplifyGraph(_, recurse = false)))
    
    println(s"--- Final Graph (${ite}) ---")
    val graphStr = mod.show
    println(graphStr)
    if (dumpGraph) write(writePath_graph, graphStr, createFolders = true)
    
    //println(mod.show)
    val sch = go.Graph.scheduleRec(mod)
    println("--- Scheduled ---")
    
    //go.Graph.HaskellScheduleDebug debugFor
    //println(sch)
    
    //import squid.ir.graph.{SimpleASTBackend => AST}
    //println("REF: "+AST.showRep(go.Graph.treeInSimpleASTBackend(mod.toplvlRep))) // diverges in graphs with recursion
    
    println("--- Generated ---")
    val ghcVersion = %%('ghc, "--version")(pwd).out.string.stripSuffix("\n")
    val moduleStr = sch.toHaskell(go.imports.toList.sorted, ghcVersion)
    println(moduleStr)
    write(writePath_hs, moduleStr, createFolders = true)
    
    if (compileResult) %%(ghcdump.CallGHC.ensureExec('ghc), writePath_hs)(pwd)
  }
  
  def apply(testName: String,
            passes: List[String] = List("0000", "0001"),
            opt: Bool = false,
            compileResult: Bool = true,
            dumpGraph: Bool = false,
            exec: Bool = false,
           ): Unit = {
    import ghcdump._
    if (exec) require(compileResult)
    
    val srcPath = pwd/'haskellopt/'src/'test/'haskell/(testName+".hs")
    val md5Path = dumpFolder/(testName+".md5")
    val md5 = read! md5Path optionIf (exists! md5Path)
    
    val srcMd5 = %%(CallGHC.ensureExec('md5), srcPath)(pwd).out.string
    
    if (!md5.contains(srcMd5)) {
      println(s"Compiling $srcPath...")
      
      CallGHC(
        srcPath,
        dumpFolder,
        opt = opt,
      )
      
      if (exists! md5Path) rm! md5Path
      write(md5Path, srcMd5)
    }
    
    for (idxStr <- passes) {
      val execPath = genFolder/RelPath(testName+s".pass-$idxStr.opt")
      if (exists(execPath)) os.remove(execPath)
      pipeline(dumpFolder/(testName+s".pass-$idxStr.cbor"), compileResult, dumpGraph)
      if (exec) %%(execPath)(pwd)
    }
    
  }
  
}
object Clean extends App {
  println(s"Cleaning...")
  ls(TestHarness.dumpFolder) |? (_.ext === "md5") |! { p => println(s"Removing $p"); os.remove(p) }
}
