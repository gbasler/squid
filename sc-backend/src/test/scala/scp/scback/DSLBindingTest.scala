package scp.scback

import ch.epfl.data.sc._
import pardis._
import deep.scalalib._
import deep.scalalib.collection._


object DSLBindingTest {
  trait NoStringCtor {
    /** For some unfathomable reason, there are 16 String ctors at compile time, but reflection only shows 15.
      * The one that's missing is {{{(x$1: Array[Char], x$2: Boolean)String}}}, which does not even appear in the Java
      * docs or in Java source. Scala-compiler hack? */
    type `ignore java.lang.String.<init>`
  }
  
  //object DSL extends ir.Base
  //object DSL extends ir.Base with SeqOps
  //object DSL extends ir.Base with ArrayBufferOps
  //object DSL extends ir.Base with StringOps with NoStringCtor
  //object DSL extends ir.Base with ArrayBufferOps with NumericOps with ScalaPredefOps
  //object DSL extends ir.Base with NumericOps
  object SCDSL extends ir.Base with ScalaCoreOps with NoStringCtor
  
  object SDSL extends AutoboundPardisIR(SCDSL)
  /*_*/
  SDSL.ab = {
    import scala.collection.mutable.ArrayBuffer
    AutoBinder(SCDSL, SDSL)
  }
  /*_*/
  
}

