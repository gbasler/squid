package squid
package anf.transfo

import utils._
import ir._
import utils.Debug.show

/**
  * Created by lptk on 11/02/17.
  * 
  * Transforms option variables into a pair of variables (one boolean and oen containing the value if any).
  * This transformation will not successfully apply if the code is not normal w.r.t `OptionNormalizer`.
  * 
  */
trait VarInliner extends SimpleRuleBasedTransformer { self =>
  import base.Predef._
  import self.base.InspectableIROps
  import self.base.IntermediateIROps
  
  import squid.lib.Var
  
  rewrite {
    
    
    // Removal of Var[Unit]
    case ir"var $v: Unit = (); $body: $t" => // Note that Unit <: AnyVal and cannot take value `null`
      //body subs 'v -> ir"()"  // No, wrong type! (Error:(22, 23) Cannot substitute free variable `v: squid.lib.Var[Unit]` with term of type `Unit`)
      body rewrite { case ir"$$v.!" => ir"()" case ir"$$v:=(())" => ir"()" } subs 'v -> Abort()
    
    
    // Removal of Var[Option[_]]
    case ir"var $v: Option[$t] = $init; $body: $bt" =>
      val isDefined = ir"isDefined? : Var[Bool]"
      val optVal = ir"optVal? : Var[$t]"
      
      // Note: applying sub-rewritings sch as `case ir"$$v.!.isDefined" =>` is not going to work well, because it will
      // not rewrite cases such as `var a = Option(42); val v = a; (v.isDefined, v.isDefined)`...
      // Instead, we really need another level of usage-in-body rewriting for `ir"$$v.!"` bound values.
      
      //println(s"REM $v "+v.rep)
      
      val body2 = body rewrite {
        case ir"val $x = $$v.! ; $sbody: $bt" =>
          val sbody2 = sbody rewrite {
            case ir"$$x.isDefined" => ir"$isDefined.!"
            case ir"$$x.get" => 
              val r = ir"$optVal.!" // TODO assert...?
              //println(s"R $r")
              r
          }
          //println("RW1 "+sbody)
          //println("RW2 "+sbody2)
          sbody2 subs 'x -> Abort()
        case ir"$$v := None" => ir"$isDefined := false"
        case ir"$$v := Some($x:$$t)" => ir"$optVal := $x; $isDefined := true"
        case ir"$$v := $x" =>
          Abort() // TODO rewrite to if-then-else rebuilding the option -- note this may be tricky if options are not
          // normalized online, as it could lead to transforming `opt.isEmpty` into the ite before it is rewritten to
          // `!opt.isDefined` and handled properly by this transformer...
      }
      
      //println("RW "+body2)
      
      val body3 = body2 subs 'v -> Abort()
      //val body3 = body2 subs 'v -> {
      //  println(s"REMAINING 'v' IN $body2")
      //  Abort()}
      
      // FIXME make the `var` syntax work in:
      // TODO use `uncheckableValue`
      ir"""
        val isDefined = Var($init.isDefined)
        val optVal = Var($init getOrElse ${nullValue[t.Typ]})
        $body3
      """
      
      
    // Removal of Var[Tuple2[_]]
    case ir"var $v: ($ta,$tb) = $init; $body: $bt" =>
      val lhs = ir"lhs? : Var[$ta]"
      val rhs = ir"rhs? : Var[$tb]"
      
      val body2 = body rewrite {
        case ir"val $x = $$v.! ; $sbody: $bt" =>
          val sbody2 = sbody rewrite {
            case ir"$$x._1" => ir"$lhs.!"
            case ir"$$x._2" => ir"$rhs.!"
          }
          sbody2 subs 'x -> Abort()
        case ir"$$v := ($a:$$ta,$b:$$tb)" => ir"$lhs := $a; $rhs := $b"
        case ir"$$v := $x" => Abort() // TODO?
      }
      
      val body3 = body2 subs 'v -> Abort()
      
      ir"""
        val lhs = Var($init._1)
        val rhs = Var($init._2)
        $body3
      """
      
      
      
  }
      
}

