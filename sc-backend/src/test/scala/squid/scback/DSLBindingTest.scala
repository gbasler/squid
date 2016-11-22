package squid.scback

import squid.utils._

import ch.epfl.data.sc._
import pardis._
import deep.scalalib._
import deep.scalalib.collection._


trait DSLBindingTest {
  trait NoStringCtor {
    /** For some unfathomable reason, there are 16 String ctors at compile time, but reflection only shows 15.
      * The one that's missing is {{{(x$1: Array[Char], x$2: Boolean)String}}}, which does not even appear in the Java
      * docs or in Java source. Scala-compiler hack? */
    type `ignore java.lang.String.<init>`
  }
  
  //object SC extends ir.Base
  //object SC extends ir.Base with SeqOps
  //object SC extends ir.Base with ArrayBufferOps
  //object SC extends ir.Base with StringOps with NoStringCtor
  //object SC extends ir.Base with ArrayBufferOps with NumericOps with ScalaPredefOps
  //object SC extends ir.Base with NumericOps
  //object SC extends ir.Base with ScalaCoreOps with NoStringCtor
  object SC extends ir.Base with ScalaCoreOps with NoStringCtor with ContOps
  
  object Sqd extends AutoboundPardisIR(SC) {
    
    lazy val SeqApplySymbol = loadMtdSymbol(loadTypSymbol("scala.collection.generic.GenericCompanion"), "apply", None)
    
    // Special-case some problematic methods:
    override def methodApp(self: Rep, mtd: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep = mtd match {
        
      case SeqApplySymbol if baseName(tp) == "Seq" =>  // distinguish from an `apply` on `ArrayBuffer`
        val ta :: Nil = targs
        SeqIRs.SeqApplyObject[Any]((argss match {
          case ArgList(as @ _*) :: Nil => sc.__liftSeq[Any](as map toExpr)(ta)
          case ArgsVarargSpliced(Args(), spliced) :: Nil => spliced |> toExpr
        }).asInstanceOf[R[Seq[Any]]])(ta)
        
      case _ => 
        super.methodApp(self, mtd, targs, argss, tp)
        
    }
  }
  
  /*_*/
  Sqd.ab = {
    import scala.collection.mutable.ArrayBuffer
    AutoBinder(SC, Sqd)
  }
  /*_*/
  
}

