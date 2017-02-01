package example

import squid._
import utils._
import ir._
import utils.Debug.show

/**
  * Created by lptk on 01/02/17.
  */
trait VarNormalizer extends SimpleRuleBasedTransformer { self =>
  import base.Predef._
  import self.base.InspectableIROps
  import self.base.IntermediateIROps
  
  import squid.lib.Var
  
  rewrite {
  
    // Removal of Var[Unit]
    case ir"var $v: Unit = (); $body: $t" => // Note that Unit <: AnyVal and cannot take value `null`
      //body subs 'v -> ir"()"  // No, wrong type! (Error:(22, 23) Cannot substitute free variable `v: squid.lib.Var[Unit]` with term of type `Unit`)
      body rewrite { case ir"$$v.!" => ir"()" case ir"$$v:=(())" => ir"()" } subs 'v -> {throw RewriteAbort()}
      
  }
      
}
