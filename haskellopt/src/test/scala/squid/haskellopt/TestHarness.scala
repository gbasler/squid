package squid.haskellopt

import squid.utils._
import ammonite.ops._

class TestHarness {
  
  val dumpFolder = pwd/'haskellopt/'target/'dump
  val genFolder = pwd/'haskellopt_gen
  
  val sanityCheckFuel = 20
  //val sanityCheckFuel = 100
  
  def pipeline(filePath: Path, compileResult: Bool, dumpGraph: Bool, interpret: Bool): Unit = {
    
    val writePath_hs = genFolder/RelPath(filePath.baseName+".opt.hs")
    if (exists(writePath_hs)) rm(writePath_hs)
    
    val writePath_graph = genFolder/RelPath(filePath.baseName+".opt.graph")
    if (exists(writePath_graph)) rm(writePath_graph)
    
    val go = new GraphLoader
    val mod = go.loadFromDump(filePath)
    println(s"=== PHASE ${mod.modPhase} ===")
    
    //go.Graph.debugFor
    //go.Graph.RewriteDebug.debugFor
    {
    
    var ite = 0
    do {
      
      if (go.Graph.RewriteDebug.isDebugEnabled) {
        println(s"--- Graph ${ite} ---")
        println(mod.show)
        println(s"--- / ---")
      }
      
      val sanRes = go.Graph.sanityCheck(mod.toplvlRep, sanityCheckFuel)(go.Graph.CCtx.empty)
      if (sanRes.isEmpty)
        println(s"Note: sanity check stopped early given fuel = $sanityCheckFuel")
      else if (go.Graph.RewriteDebug.isDebugEnabled)
        println(s"Sanity check stopped with remaining fuel = $sanityCheckFuel")
      
      //println(go.Graph.scheduleRec(mod))
      
      ite += 1
      
      //Thread.sleep(200)
      
    } while (mod.letReps.exists(go.Graph.simplifyGraph(_, recurse = false)))
    
    /* Tries reducing some more...: */
    //go.Graph.betaReduced.clear()
    //while (mod.letReps.exists(go.Graph.simplifyGraph(_, recurse = false))) ite += 1
    
    println(s"--- Final Graph (${ite}) ---")
    
    }
    
    val graphStr = mod.show
    println(graphStr)
    if (dumpGraph) write(writePath_graph, graphStr, createFolders = true)
    
    if (interpret) {
      println("--- Interpreted ---")
      val res =
        //go.Graph.HaskellInterpDebug debugFor
        go.Graph.interpretHaskell(mod.lets("main"))
      //println(res.value)
      println(res)
      assert(res.isInstanceOf[scala.util.Success[_]], res)
    }
    
    //println(mod.show)
    val sch = 
      //go.Graph.HaskellScheduleDebug debugFor
      go.Graph.scheduleRec(mod)
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
    //if (exec) require(compileResult) // In fact, we may want to execute the interpreter only, and not any compiled code
    
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
      pipeline(dumpFolder/(testName+s".pass-$idxStr.cbor"), compileResult, dumpGraph && (idxStr === "0000"), interpret = exec)
      if (compileResult && exec) %%(execPath)(pwd)
    }
    
  }
  
}
object Clean extends App {
  object TestHarness extends TestHarness
  println(s"Cleaning...")
  ls(TestHarness.dumpFolder) |? (_.ext === "md5") |! { p => println(s"Removing $p"); os.remove(p) }
}
