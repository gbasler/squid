package squid.scback

import squid.utils._
import ch.epfl.data.sc._
import pardis._
import deep.scalalib._
import deep.scalalib.collection._
import PardisBinding._
import ch.epfl.data.sc.pardis
import ch.epfl.data.sc.pardis.types.PardisType
import pardis.deep.scalalib.collection.SeqOps

/**
  * Created by lptk on 22/11/16.
  */
object PardisBinding {
  
  trait NoStringCtor { self: ir.Base =>
    /** For some unfathomable reason, there are 16 String ctors at compile time, but reflection only shows 15.
      * The one that's missing is {{{(x$1: Array[Char], x$2: Boolean)String}}}, which does not even appear in the Java
      * docs or in Java source. Scala-compiler hack? */
    type `ignore java.lang.String.<init>`
  }
  trait DefaultPardisMixin extends NoStringCtor { self: ir.Base =>
    
    val typeExtractedBinder = ExtractedBinderType
    
  }
  
  trait DefaultRedirections[DSL <: pardis.ir.Base] extends squid.lang.Base { self: AutoboundPardisIR[DSL] => //with SeqOps =>
    
    lazy val SeqApplySymbol = loadMtdSymbol(loadTypSymbol("scala.collection.generic.GenericCompanion"), "apply", None)
    
    // Special-case some problematic methods:
    abstract override def methodApp(self: Rep, mtd: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep = mtd match {
        
      case SeqApplySymbol if baseName(tp) == "Seq" =>  // distinguish from an `apply` on `ArrayBuffer`
        val ta :: Nil = targs
        blockWithType(tp) {
          SeqIRs.SeqApplyObject[Any]((argss match {
            case ArgList(as @ _*) :: Nil => sc.__liftSeq[Any](as map toExpr)(ta)
            case ArgsVarargSpliced(Args(), spliced) :: Nil => spliced |> toExpr
          }).asInstanceOf[R[Seq[Any]]])(ta)
        }
        
      case _ => 
        super.methodApp(self, mtd, targs, argss, tp)
        
    }
  }
  
  case object ExtractedBinderType extends PardisType[Any] {
    def rebuild(newArguments: PardisType[_]*): PardisType[_] = this
    val name: String = "ExtractedBinder"
    val typeArguments: List[PardisType[_]] = Nil
  }
  
}
