package squid.haskellopt

import squid.utils._
import ammonite.ops._

object SynthBench {
  // Try to:
  //  - go back to tuples
  //  - add all smaller cases (it seems to make GHC inline much more aggressively...)
  
  //val sizes = 1 to 25
  val sizes = 1 to 15
  //val sizes = 20 to 40 by 5
  //val sizes = 15 to 40 by 5
  //val sizes = 1 to 30 by 5
  //val sizes = (1 to 10) ++ (12 to 20 by 2)
  //val sizes = (1 to 10)
  //val sizes = (5 to 30 by 5)
  //val sizes = (2 to 20 by 2)
  
  def genPgrm(sizes: Iterable[Int], fileName: String, bench: Bool, exportOnlyMain: Bool, forceInline: Bool,
              generic: Bool = false,
              lists: Bool = false, multiCase: Bool = false,
  ) = {
    val imports = "Criterion.Main" :: Nil
    
    //val (`(`,`)`) = if (lists) ("[","]") else ("(",")")
    val `[` = if (lists) "[" else "("
    val `]` = if (lists) "]" else ")"
    
    val defs = for {
      size <- sizes
      indices = 0 until size
      xs = indices map ("x"+_)
      ys = indices map ("y"+_)
      //prod = s"prod_$size :: [Int] -> [Int] -> Int\nprod_$size [${xs mkString ","}] [${ys mkString ","}] = ${
      //  (xs zip ys).foldLeft("0"){case (acc,xy) => acc + " + " + xy._1 + " * " + xy._2}}"
      //usage = s"test_$size n = sum (map (\\i -> prod_$size [${
      //  indices map (i => s"i + $i") mkString ", "}] [${
      //  indices map (i => s"i + $i") mkString ", "}]) [0..n])"
      
      //(unit,op) = ("0,"+")
      (unit,op) = ("1","*")
      //(unit,op) = ("666","`mod`")
      
      prod =
        (if (forceInline) s"{-# INLINE prod_$size #-}\n" else "") + 
        (if (lists) if (generic) s"prod_$size :: Num a => ${`[`}a${`]`} -> a\n" else s"prod_$size :: ${`[`}Int${`]`} -> Int\n"
        else {
          assert((`[`,`]`) === ("(",")"))
          def tupled(str: String) = List.fill(size)(str).mkString("(",",",")")
          if (generic) s"prod_$size :: Num a => ${tupled("a")} -> a\n" else s"prod_$size :: ${tupled("Int")} -> Int\n"
        }) +
        (if (!lists || !multiCase)
        s"prod_$size ${`[`}${xs mkString ","}${`]`} = ${
          (xs zip ys).foldLeft(unit){case (acc,xy) => acc + s" $op " + xy._1}}"
        else (xs.reverse.tails.map { xsr =>
          val xs = xsr.reverse
          s"prod_$size ${`[`}${xs mkString ","}${`]`} = ${
            (xs zip ys).foldLeft(unit){case (acc,xy) => acc + s" $op " + xy._1}}"
        }.mkString("\n")))
      usage = s"test_$size n = sum (map (\\i -> prod_$size ${`[`}${
        //indices map (i => s"i + $i") mkString ", "
        indices map (i => s"i ^ $i") mkString ", "
      }${`]`}) [0..n])"
    } yield s"$prod\n$usage"
    
    val main = if (bench) {
      val benchmarks = for {
        size <- sizes
      //} yield s"""bench "prod_$size" $$ whnf test_$size 1000"""
      } yield s"""bench "$size" $$ whnf test_$size 1000"""
      s"defaultMain ${benchmarks.map("\n|    "+_).mkString("[",",","\n|  ]")}"
    } else
      s"do\n  ${sizes.map(size => s"print (test_$size 1000)").mkString("\n  ")}"
    
      //|module Main (main) where
    val pgrmStr =
      s"""
      |-- Generated Haskell code from SynthBench
      |
      |module Main${if (exportOnlyMain) "(main)" else ""} where
      |
      |${imports.map("import "+_).mkString("\n")}
      |
      |${defs.mkString("\n\n")}
      |
      |main = $main
      |
      |""".tail.stripMargin
    
    val path = pwd/'haskellopt/'src/'test/'haskell/fileName
    
    if (exists! path) rm! path
    write(path, pgrmStr)
  }
  
  
}
object MkAllSynthBench extends App {
  //SynthBench.genPgrm(SynthBench.sizes, "VectorsBench.hs", bench = true, exportOnlyMain = false, forceInline = false)
  SynthBench.genPgrm(SynthBench.sizes, "VectorsBench.hs", bench = true, exportOnlyMain = false, forceInline = false, generic = true)
}

object MeasureSynthBenchSizes extends BenchTests with App {
  
  //val opt = "-O0"
  //val opt = "-O1"
  val opt = "-O2"
  
  def getTerms(dumpLines: Iterator[String]): Int = {
    val sizes = dumpLines.dropWhile(_ =/= "Result size of CorePrep").drop(1).next
    println(s"SIZES: $sizes")
    //val terms = sizes.drop("  = {terms: ".length).takeWhile(_ =/= ',').replaceFirst(",","").toInt
    val terms = sizes.drop("  = {terms: ".length).replaceFirst(",","").takeWhile(c => c =/= ',' && c =/= ' ').toInt
    println(s"TERMS: $terms")
    terms
  }
  
  val coreSizes = for {
    size <- SynthBench.sizes
    //size <- 5::Nil
  } yield {
    
    val fileName = s"VectorsBench_$size.hs"
    val path = pwd/'haskellopt/'src/'test/'haskell/fileName
    
    def termsFor(forceInline: Bool) = {
      //SynthBench.genPgrm(size :: Nil, fileName, bench = false, exportOnlyMain = exportOnlyMain)
      SynthBench.genPgrm(size :: Nil, fileName, bench = true, exportOnlyMain = false, forceInline = forceInline)
      %("ghc", opt, "-fforce-recomp", "-ddump-to-file", "-ddump-prep", path)(pwd)
      val dump = read(pwd/'haskellopt/'src/'test/'haskell/s"VectorsBench_$size.dump-prep")
      //val sizes = dump.lines.dropWhile(_ =/= "Result size of CorePrep").drop(1).next
      //println(s"SIZES: $sizes")
      ////val terms = sizes.drop("  = {terms: ".length).takeWhile(_ =/= ',').replaceFirst(",","").toInt
      //val terms = sizes.drop("  = {terms: ".length).replaceFirst(",","").takeWhile(c => c =/= ',' && c =/= ' ').toInt
      //println(s"TERMS: $terms")
      val terms = getTerms(dump.lines)
      terms
    }
    val terms0 = termsFor(true)
    val terms1 = termsFor(false)

    //???
    //size -> terms

    ///Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt_gen/bench/VectorsBench_1.pass-0000.opt.hs
    ///Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt_gen/VectorsBench_1.pass-0000.opt.hs

    val path2 = pwd/'haskellopt_gen/'bench/s"VectorsBench_$size.pass-0000.opt.hs"
    TestHarness(s"VectorsBench_$size", "0000"::Nil, compileResult = false)
    %("ghc", opt, "-fforce-recomp", "-ddump-to-file", "-ddump-prep", path2)(pwd)
    val dump2 = read(pwd/'haskellopt_gen/'bench/s"VectorsBench_$size.pass-0000.opt.dump-prep")
    val terms2 = getTerms(dump2.lines)

    (size, terms0, terms1, terms2)
  }
  //println(s"Core sizes:${coreSizes.map(is => "\n"+is._1+","+is._2).mkString("")}")
  println(s"Core sizes:${coreSizes.map("\n"+_.toString.tail.init).mkString("")}")
  
}
