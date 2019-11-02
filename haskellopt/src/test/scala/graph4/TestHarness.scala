package graph4

import squid.utils._
import squid.haskellopt.ghcdump
import ammonite.ops._

class TestHarness {
  
  val dumpFolder = pwd/'haskellopt/'target/'dump
  val genFolder = pwd/'haskellopt_gen2
  
  val sanityCheckFuel = 20
  //val sanityCheckFuel = 100
  
  def mkGraph =
    new GraphIR
  
  def pipeline(filePath: Path, compileResult: Bool, dumpGraph: Bool, checks: Seq[CheckDSL], interpret: Bool, schedule: Bool, prefixFilter: Str): Unit = {
    
    val writePath_hs = genFolder/RelPath(filePath.baseName+".opt.hs")
    if (exists(writePath_hs)) rm(writePath_hs)
    
    val writePath_graph = genFolder/RelPath(filePath.baseName+".opt.graph")
    if (exists(writePath_graph)) rm(writePath_graph)
    
    val loadingStartTime = System.nanoTime
    
    val go = new GraphLoader(mkGraph)
    val mod = go.loadFromDump(filePath, prefixFilter)
    
    val Inter = new go.Graph.Interpreter
    def check(c: CheckDSL): Unit = if (c.fname.name + " " startsWith prefixFilter) {
      val liftedArgs = c.args.map(Inter.lift)
      val liftedValue = Inter.lift(c.value)
      //println(s"Eval... $c")
      
      //Inter.debugFor
      {
        val fun = Inter(mod.modDefs.toMap.apply(c.fname.name))
        val applied = liftedArgs.foldLeft(fun){ (f,a) => f.value.app(Lazy(a)) }.value
        println(s"Eval:  $c  ~~>  $applied  =?=  $liftedValue")
        assert(applied === liftedValue, s"Interpreter result $applied did not equal expected result $liftedValue")
      }
    }
    
    val loadingEndTime = System.nanoTime
    val loadingTime = loadingEndTime-loadingStartTime
    println(s"Loading time: ${loadingTime/1000/1000} ms")
    
    val rewriteStartTime = System.nanoTime
    
    println(s"=== PHASE ${mod.modPhase} ===")
    
    //go.Graph.debugFor
    go.Graph.RewriteDebug.debugFor
    {
    
    var ite = 0
    do {
      
      if (go.Graph.RewriteDebug.isDebugEnabled) {
        println(s"--- Graph ${ite} ---")
        println(mod.showGraph)
        println(s"--- / ---")
        Thread.sleep(100)
      }
      
      // TODO sanity checks
      /*
      val sanRes = go.Graph.sanityCheck(mod.toplvlRep, sanityCheckFuel)(go.Graph.CCtx.empty)
      if (sanRes.isEmpty)
        println(s"Note: sanity check stopped early given fuel = $sanityCheckFuel")
      else if (go.Graph.RewriteDebug.isDebugEnabled)
        println(s"Sanity check stopped with remaining fuel = $sanRes, given fuel = $sanityCheckFuel")
      */
      //println(go.Graph.scheduleRec(mod))
      
      mod.checkRefs()
      
      checks.foreach(check)
      
      ite += 1
      
      //Thread.sleep(200)
      
    } while (mod.simplify())
    //} while (mod.letReps.exists(go.Graph.simplifyGraph(_, recurse = false))
    //  || go.Graph.simplifyHaskell(mod.toplvlRep).exists { r => // FIXME rewrite several in a row?
    //    if (go.Graph.RewriteDebug.isDebugEnabled) {
    //      println(s"Rewrote $r")
    //    }
    //    true
    //  })
    
    /* Tries reducing some more...: */
    //while (mod.simplify()) ite += 1
    
    println(s"--- Final Graph (${ite}) ---")
    
    }
    
    val rewriteEndTime = System.nanoTime
    val rewriteTime = rewriteEndTime-rewriteStartTime
    println(s"Rewrite time: ${rewriteTime/1000/1000} ms")
    
    val graphStr = mod.showGraph
    println(graphStr)
    val infoText = s"-- Beta reductions:  ${mod.betaReductions}\n-- Incl. one-shot:  ${mod.oneShotBetaReductions}\n" +
      s"-- Case reductions:  ${mod.caseReductions}\n" +
      s"-- Field reductions:  ${mod.fieldReductions}\n"
    val graphText = infoText + 
      graphStr.replaceAll("\u001B\\[[;\\d]*m", "") + // remove ASCII color codes (https://stackoverflow.com/a/14652763/1518588)
      "\n"
    if (dumpGraph) write(writePath_graph, graphText, createFolders = true)
    
    if (interpret) {
      println("--- Interpreted ---")
      ??? // TODO
      /*
      val res =
        //go.Graph.HaskellInterpDebug debugFor
        go.Graph.interpretHaskell(mod.lets("main"))
      //println(res.value)
      println(res)
      assert(res.isInstanceOf[scala.util.Success[_]], res)
      */
    }
    
    if (!schedule) return
    
    val scheduleStartTime = System.nanoTime
    
    //println(mod.show)
    val sch =
      if (go.Graph.debugScheduling) go.Graph.ScheduleDebug debugFor go.Graph.schedule(mod)
      else go.Graph.schedule(mod)
      //go.Graph.schedule(mod, naive = true)
    
    val scheduleEndTime = System.nanoTime
    val scheduleTime = scheduleEndTime-scheduleStartTime
    println(s"Scheduling time: ${scheduleTime/1000/1000} ms")
    
    println("--- Scheduled ---")
    
    //go.Graph.HaskellScheduleDebug debugFor
    //println(sch)
    
    //import squid.ir.graph.{SimpleASTBackend => AST}
    //println("REF: "+AST.showRep(go.Graph.treeInSimpleASTBackend(mod.toplvlRep))) // diverges in graphs with recursion
    
    println("--- Generated ---")
    val ghcVersion = %%('ghc, "--version")(pwd).out.string.stripSuffix("\n")
    val stringifyStartTime = System.nanoTime
    val moduleStr = sch.toHaskell(go.imports.toList.sorted, ghcVersion)
    val stringifyEndTime = System.nanoTime
    val stringifyTime = stringifyEndTime-stringifyStartTime
    println(s"Stringify time: ${stringifyTime/1000/1000} ms")
    println(moduleStr)
    write(writePath_hs, moduleStr, createFolders = true)
    
    println(s"=> Loading+Rewrite+Schedule+Stringify time:" +
      s" ${loadingTime/1000/1000} + ${rewriteTime/1000/1000} + ${scheduleTime/1000/1000} + ${stringifyTime/1000/1000}" +
      s" = ${(loadingTime + rewriteTime + scheduleTime + stringifyTime)/1000/1000} ms\n")
    
    if (compileResult) %%(ghcdump.CallGHC.ensureExec('ghc), writePath_hs)(pwd)
    
  }
  
  def apply(testName: String,
            //passes: List[String] = List("0000", "0001"),
            passes: List[String] = List("0000"),
            opt: Bool = false,
            compileResult: Bool = true,
            dumpGraph: Bool = false,
            exec: Bool = false,
            schedule: Bool = true,
            prefixFilter: Str = ""
           )(checks: CheckDSL*): Unit = {
    import ghcdump._
    //if (exec) require(compileResult) // In fact, we may want to execute the interpreter only, and not any compiled code
    
    val srcPath = pwd/'haskellopt/'src/'test/'haskell2/(testName+".hs")
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
      pipeline(dumpFolder/(testName+s".pass-$idxStr.cbor"),
        compileResult, dumpGraph && (idxStr === "0000"), checks, interpret = exec, schedule = schedule, prefixFilter = prefixFilter)
      if (compileResult && exec) %%(execPath)(pwd)
    }
    
  }
  
}

object Clean extends scala.App {
  object TestHarness extends TestHarness
  println(s"Cleaning...")
  ls(TestHarness.dumpFolder) |? (_.ext === "md5") |! { p => println(s"Removing $p"); os.remove(p) }
}

case class CheckDSL(fname: Symbol, args: Seq[Any], value: Any) {
  override def toString = fname.name + args.map(" "+_).mkString
}
object CheckDSL {
  def check(fname: Symbol, args: Any*)(value: Any): CheckDSL = CheckDSL(fname, args, value)
}
