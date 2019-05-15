package squid
package haskellopt
package ghcdump

import squid.utils._
import ammonite.ops._
import ammonite.ops.ImplicitWd._

/** This requires GHC on the PATH, and the GhcDump plugin for GHC. */
object CallGHC {
  
  val hsExt = "hs"
  
  def ensureExec(execName: Symbol): Shellable = {
    println("Looking for " + execName.name)
    println("Found " + %%('which, execName).out.string.stripSuffix("\n"))
    execName
  }
  
  def apply(filePath: FilePath, outputPath: Path, opt: Bool = false): Unit = {
    assert(filePath.ext === hsExt, filePath.ext)
    
    def getPasses(dir: Path) =
      ls(dir) |? (_.baseName.startsWith(s"${filePath.baseName}.pass-"))
    
    mkdir! outputPath
    getPasses(outputPath) |! rm
    
    val cmd = List[Shellable](
      ensureExec('ghc),
      //"-fno-code", // seems to prevent the dumping of GHC core
      "-fforce-recomp",
      "-outputdir", outputPath.toString,
      "-fplugin", "GhcDump.Plugin",
      "-c", // Stop after generating object (.o) file
      
      "-ddump-to-file",
      "-ddump-ds",
      "-ddump-simpl",
      "-ddump-simpl-iterations",
      "-ddump-rule-rewrites",
      
      // 'dumpdir' doesn't seem to work... so I'm moving the files manually below
      //s"-dumpdir=$outputPath",
      //s"-dumpdir", outputPath,
      //s"-dumpdir=dump",
      
      filePath.toString,
    ) ++ (
      if (opt) List[Shellable](
        "-O",
        //"-fno-enable-rewrite-rules", // creates references to non-exported functions such as `GHC.Base.mapFB`; ALSO seems to disable encoding of list literals with build
        "-fno-specialise", // creates fictive functions with illegal names, such as `GHC.Show.$w$cshowsPrec4`
      ) else Nil
    )
    println(s"Executing:\n\t${cmd.head.s.head} ${cmd.tail.map('"'+ _.s.mkString(" ") +'"').mkString(" ")}")
    %.applyDynamic("apply")(cmd: _*)
    
    val passes = getPasses(Path(filePath,root)/up)
    val sortedPasses = passes.sortBy(_.baseName)
    println(s"Passes:${sortedPasses.map("\n\t"+_).mkString}")
    sortedPasses |! (f => mv(f, outputPath/f.last))
    
  }
  
}
