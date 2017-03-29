package squid
package anf.transfo

import utils._
import ir._
import utils.Debug.show

trait VarSimplification extends SimpleRuleBasedTransformer { self =>
  val base: AST
  import base.Predef._
  import self.base.InspectableIROps
  import self.base.IntermediateIROps
  
  import squid.lib.Var
  import squid.lib.uncheckedNullValue
  
  rewrite {
    
    //case ir"var $v: $vt = $init; $body: $bt" =>
    case ir"val $v = Var[$vt]($init); $body: $bt" =>
      //ir"???"
      
      //val newV = ir"newV? : Var[$vt]"
      val newV = ir"v? : Var[$vt]"
      
      //var curVal: Option[IR[vt.Typ]] = None
      var curVal = Option(init)
      var isAssigned = false
      
      // TODO do not re-read variable when value cannot have changed
      var lastReadInto: Option[IR[vt.Typ,{}]] = None
      
      // TODO rm useless reads
      
      val body2 = body rewrite {
        //case ir"$$v.!" if curVal.isDefined => curVal.get
        
        //case ir"$$v.!" =>
        //  curVal.getOrElse(ir"$newV.!")
        case ir"val $value = $$v.!; $b:$t" =>
        //case ir"val ${value @ base.IR(base.RepDef(base.BoundVal(x)))} = $$v.!; $b:$t" =>
          //curVal.fold(
          //  lastReadInto.fold
          //){b subs 'value -> _}
          println(s"CV: $curVal for body $b")
          (curVal orElse lastReadInto).fold({
            lastReadInto = Some(value.asInstanceOf[IR[Nothing,Any]])
            //lastReadInto = Some(value)
            //ir"val value "
            Return.transforming(b)(b => ir"val value = $v.!; $b")
          }){b subs 'value -> _}
          
        case ir"$$v:=$x" =>
          curVal = Some(x.asInstanceOf[IR[Nothing,Any]]) // TODO dynamic context cast!
          //if (!(Some(x) =~= curVal)) isAssigned = true
          if (!curVal.exists(_ =~= x)) isAssigned = true
          ir"$newV:=$x"
          
        case ir"(x:$xt) => $y:$yt" =>
          // what if latent effect?!
          curVal = None
          Return(ir"(x:$xt) => $y")
          
        case ir"if ($cond) $thn else $els : $t" =>
          //val oldVal = curVal
          Return.transforming(cond,thn,els)((cond,thn,els) => ir"if ($cond) $thn else $els")
          
      } /*rewrite {
        
        case ir"val value = $$v.!" =>
          
      }*/
      
      if (!isAssigned) {
        val body2 = body rewrite {
          case ir"$$v.!" => ir"value? : $vt"
        } subs 'v -> Abort() // TODO actually go to case below...
        ir"val value = $init; $body2"
      } else {
        //val body3 = body2 subs 'v -> 
        //ir"var v: $vt = $init; $body2" // FIXME
        ir"val v = Var[$vt]($init); $body2"
      }
      
      
  }
  
}

