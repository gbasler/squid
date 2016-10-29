package scp.scback

import ch.epfl.data.sc._
import pardis._
import deep.scalalib._
import deep.scalalib.collection._


object DSLBindingTest {
  //object DSL extends ir.Base
  //object DSL extends ir.Base with ArrayBufferOps
  //object DSL extends ir.Base with StringOps // FIXME
  object DSL extends ir.Base with ArrayBufferOps with NumericOps with ScalaPredefOps
  //object DSL extends ir.Base with NumericOps
  //object DSL extends ir.Base with ScalaCoreOps
  
  object SDSL extends AutoboundPardisIR(DSL)
  /*_*/
  SDSL.ab = AutoBinder(DSL, SDSL)
  /*_*/
  
}

