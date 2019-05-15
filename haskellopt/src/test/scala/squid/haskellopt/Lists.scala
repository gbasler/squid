package squid.haskellopt

import squid.utils._
import org.scalatest.FunSuite

class Lists extends FunSuite {
  
  def pipeline(filePath: String): Unit = {
    import ammonite.ops._
    val rp = RelPath(filePath)
    val writePath = pwd/'haskellopt_gen/RelPath(rp.baseName+".opt.hs")
    if (exists(writePath)) rm(writePath)
    val go = new GraphOpt
    val mod = go.loadFromDump(rp)
    println(s"=== PHASE ${mod.modPhase} ===")
    
    var ite = 0
    do {
      println(s"--- Graph ${ite} ---")
      println(mod.show)
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
  
  test("Lists 0") {
    
    pipeline("haskellopt/target/dump/Lists.pass-0000.cbor")
    pipeline("haskellopt/target/dump/Lists.pass-0001.cbor")
    //for (i <- 2 to 9) pipeline(s"haskellopt/target/dump/Lists.pass-000$i.cbor")
    
  }
  
}
