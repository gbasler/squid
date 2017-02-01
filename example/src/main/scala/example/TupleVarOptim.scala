package example

import squid._
import utils._
import ir._
import utils.Debug.show

/**
  * Created by lptk on 15/09/16.
  * 
  * Assumptions:
  *   Variables not initialized with null are assumed never to contain null. (arbitrary)
  *     So if you intend for a variable to be assignable to null, instead of writing `var a = init`, write `var a: T = null; a = init`
  *     Note: could instead use @Nullable annotation... (cleaner approach)
  * 
  */
trait TupleVarOptim extends SimpleRuleBasedTransformer { self =>
  import base.Predef._
  import self.base.InspectableIROps
  import self.base.IntermediateIROps
  
  import squid.lib.Var
  
  rewrite {
    
    case ir"val $tup = Var($init: ($ta, $tb)); $body: $t" =>
      //println((ta,tb)) // makes the rwr not compile -- FIXME
      //println(ta->tb) // makes the rwr not compile
      
      //show(body)
      
      val a = ir"a? : Var[$ta]"
      val b = ir"b? : Var[$tb]"
      
      val isInitializedWithNull = init =~= ir"null"  // TODO more precise?
      
      if (isInitializedWithNull) {
        
        val isNull = ir"isNull? : Var[Bool]"
        var hasNull = isInitializedWithNull
        
        val body2 = body rewrite {
          case ir"($$tup !)._1" => ir"assert(!$isNull.!); $a !"
          case ir"($$tup !)._2" => ir"assert(!$isNull.!); $b !"
          case ir"($$tup !) == null" => ir"$isNull.!"
          case ir"$$tup := ($va: $$ta, $vb: $$tb)" => ir"$a := $va; $b := $vb; $isNull := false"
          case ir"$$tup := $vab" => ir"val n = $vab == null; if (!n) { $a := $vab._1; $b := $vab._2; $isNull := n }"
          case ir"$$tup := null" => ir"$isNull := true"
          case ir"$$tup !" => ir"($a.!, $b.!)"
        }
        
        val body3 = body2 subs 'tup -> ({
          throw RewriteAbort(s"tup is still used! in: $body2")} : IR[tup.Typ,{}])
        ir" val isNull = Var(${Const(isInitializedWithNull)}); val a = Var(${nullValue[ta.Typ]});  val b = Var(${nullValue[tb.Typ]});  $body3 "
        
      } else {
          
        val body2 = body rewrite {
          case ir"($$tup !)._1" => ir"$a !"
          case ir"($$tup !)._2" => ir"$b !"
          case ir"$$tup := ($va: $$ta, $vb: $$tb)" => ir"$a := $va; $b := $vb"
          case ir"$$tup := $vab" => ir"$a := $vab._1; $b := $vab._2"
          case ir"$$tup := null" => ???; ir"???" // FIXME proper error
          case ir"$$tup !" => ir"($a.!, $b.!)"
        }
        
        //show(body2)
        
        val body3 = body2 subs 'tup -> ({
          //println(s"tup is still used! in: ${newBody rep}")
          throw RewriteAbort(s"tup is still used! in: $body2")} : IR[tup.Typ,{}])
        
        ir" val init = $init; val a = Var(init._1);  val b = Var(init._2);  $body3 "
        
      }
      
      
      
    case ir"val $tup = ($a: $ta, $b: $tb); $body: $t" => // assume ANF, so that a/b are trivial
      
      val newBody = body rewrite {
        case ir"$$tup._1" => ir"$a"
        case ir"$$tup._2" => ir"$b"
        case ir"$$tup == null" => ir"false"
      }
      //val newwBody2 = newBody subs 'tup -> ir"($a,$b)"  // can't do that as otherwise the transformer would have no fixed point
      val newwBody2 = newBody subs 'tup -> ({throw RewriteAbort()} : IR[tup.Typ,{}])
      newwBody2
    
  }
  
}


object TupleVarOptimTests extends App {
  object DSL extends SimpleAST
  import DSL.Predef._
  import DSL.Quasicodes._
  
  object Optim extends DSL.SelfTransformer with TupleVarOptim with TopDownTransformer
  
  var pgrm = ir{
    var t = (readInt, readInt)
    if (t._1 > t._2) t = (t._2, t._1)
    t._1 to t._2
  }
  
  show(pgrm)
  
  pgrm = pgrm transformWith Optim
  show(pgrm)
  
  //show(pgrm.run)
  
  
}


object TupleVarOptimTestsANF extends App {
  object DSL extends SimpleANF
  import DSL.Predef._
  import DSL.Quasicodes._
  
  object Optim extends DSL.SelfTransformer with TupleVarOptim with TopDownTransformer
  
  var pgrm = ir{
    var t = (readInt, readInt)
    if (t._1 > t._2) t = (t._2, t._1)
    t._1 to t._2
  }
  
  //show(pgrm rep)
  show(pgrm)
  
  pgrm = pgrm transformWith Optim
  
  //show(pgrm rep)
  show(pgrm)
  
  //show(pgrm.run)
  
  
}




