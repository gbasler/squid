package squid.haskellopt

import org.scalatest.FunSuite

class Lists extends FunSuite {
  
  def pipeline(filePath: String): Unit = {
    import ammonite.ops._
    val rp = RelPath(filePath)
    val writePath = pwd/'haskellopt_gen/RelPath(rp.baseName+".opt.hs")
    if (exists(writePath)) rm(writePath)
    val go = new GraphOpt
    val mod = go.loadFromDump(rp)
    val modStr = go.Graph.scheduleRec(mod).toHaskell(go.imports.toList.sorted)
    write(writePath, modStr, createFolders = true)
  }
  
  test("Lists 0") {
    
    pipeline("haskellopt/target/dump/Lists.pass-0000.cbor")
    pipeline("haskellopt/target/dump/Lists.pass-0001.cbor")
    
  }
  
}
