package squid
package haskellopt

import squid.utils._
import ammonite.ops._
import ammonite.ops.ImplicitWd._

/** This requires GHC on the PATH, and the GhcDump plugin for GHC. */
object HaskellDumper {
  
  val hsExt = "hs"
  
  def apply(filePath: FilePath, outputPath: Path): Unit = {
    assert(filePath.ext === hsExt, filePath.ext)
    
    def getPasses(dir: Path) =
      ls(dir) |? (_.baseName.startsWith(s"${filePath.baseName}.pass-"))
    
    mkdir! outputPath
    getPasses(outputPath) |! rm
    
    val cmd: Seq[Shellable] = List(
      'ghc,
      //"-fno-code", // seems to prevent the dumping of GHC core
      "-fforce-recomp",
      "-outputdir", outputPath.toString,
      "-fplugin", "GhcDump.Plugin",
      //"-O",
      
      // 'dumpdir' doesn't seem to work... so I'm moving the files manually below
      //s"-dumpdir=$outputPath",
      //s"-dumpdir", outputPath,
      //s"-dumpdir=dump",
      
      filePath.toString,
    )
    println(s"Executing:\n\t${cmd.head.s.head} ${cmd.tail.map('"'+ _.s.mkString(" ") +'"').mkString(" ")}")
    %.applyDynamic("apply")(cmd: _*)
    
    val passes = getPasses(Path(filePath,root)/up)
    val sortedPasses = passes.sortBy(_.baseName)
    println(s"Passes:${sortedPasses.map("\n\t"+_).mkString}")
    sortedPasses |! (f => mv(f, outputPath/f.last))
    
  }
  
}
