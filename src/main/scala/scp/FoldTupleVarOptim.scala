package scp

import utils._
import ir2._
import utils.Debug.show

import scp.lib.Var

class FoldTupleVarOptim extends FixPointRuleBasedTransformer with TopDownTransformer {
  val base: NormDSL.type = NormDSL
  import base.Predef._
  
  rewrite {
    
    case ir"($ls: List[$ta]).foldLeft[$tb]($init)($f)" =>
      
      ir""" var cur = $init
            $ls foreach { x => cur = $f(cur, x) }
            cur """
      
      /* Or the explicitly-virtualized version: */ 
      //ir""" val cur = Var($init)
      //      $ls foreach { x => cur := $f(cur!, x) }
      //      cur! """
      
      
    case ir"($ls: List[$t]) foreach ($f: t => Any)" =>
    //case ir"($ls: List[$t]).foreach[$t2]($f)" => // also works
      
      ir""" var iter = $ls
            while (iter.nonEmpty)
            { $f(iter.head); iter = iter.tail } """
      
  }
  
  
  def tuple2Components[A:IRType,B:IRType,C](ab: IR[(A,B),C]): Option[ IR[A,C]->IR[B,C] ] = ab match {
    case ir"($a:A, $b:B)" => Some(a -> b)
    //case dbg_ir"$ab" => // FIXME? purged type of scrutinee forgets type parameters!! -- can we do better?
    case _ => None
  }
  
  rewrite {
    
    //case ir"var $tup: ($ta, $tb) = $init; $body: $t" => // FIXME give right type to tup FV
    
    //case ir"val $tup: Var[($ta, $tb)] = Var($init); $body: $t" => // FIXME: binding `x @ ir...`
    
    case ir"val $tup = Var($init: ($ta, $tb)); $body: $t" =>
      
      val a = ir"$$a: Var[$ta]"
      val b = ir"$$b: Var[$tb]"
      
      //val (a,b) = (ir"$$a: Var[$ta]", ir"$$b: Var[$tb]") // FIXME rewrite brittleness
      
      //val (ia,ib) = (ir"1",ir"2")
      //val (ia,ib) = init match { case ir"($a, $b)" => a -> b } // FIXME: Error:scala: unexpected UnApply
      //val (init_a,init_b) = tuple2Components(init)
      
      val initComps = tuple2Components(init)
      //show(initComps) // initComps =	Some((ir"1",ir"0")) : Option[scp.utils.->[FoldTupleVarOptim.this.base.Predef.IR[ta,<context @ 49:10>],FoldTupleVarOptim.this.base.Predef.IR[tb,<context @ 49:10>]]]
      
      val newBody = body rewrite {
        case ir"($$tup !)._1" => ir"$a !"
        case ir"($$tup !)._2" => ir"$b !"
        case ir"$$tup := ($va: $$ta, $vb: $$tb)" => ir"$a := $va; $b := $vb"
        case ir"$$tup !" => ir"($a.!, $b.!)"
      }
      
      //show(newBody)
      
      val newwBody2 = newBody subs 'tup -> ({
        //println("ABORT!! "+newBody)
        throw RewriteAbort(s"tup is still used! in: $newBody")} : IR[tup.Typ,{}])
      
      //val newwBody2 = newBody subs 'tup -> ir"Var($a.!,$b.!)" // not what we want! this can be an invalid position for a Var
      //val newwBody2 = newBody subs 'tup -> ir"???"
      
      //show(newwBody2)
      
      
      //ir""" val init = $init; val a = Var(init._1); val b = Var(init._2); $newwBody2 """
      //ir""" val init = $init; var a = init._1; var b = init._2; $newwBody2 """ // FIXME give right type to FV
      
      initComps map (ab => ir" val a = Var(${ab._1}); val b = Var(${ab._2}); $newwBody2 ") getOrElse
         ir" val init = $init; val a = Var(init._1);  val b = Var(init._2);  $newwBody2 "
      
      /* or equivalently: */
      
      /* FIXME: precise typing lost, probably because of rewrite's untypecheck */ 
      //initComps match {
      //  case Some((u,v)) => ir" val a = Var($u); val b = Var($v); $newwBody2 "
      //  case None => ir" val init = $init; val a = Var(init._1);  val b = Var(init._2);  $newwBody2 "
      //}
      
      
  }
  
  
  // Hackish workaround to inline FUnction2 that does not work:
  /*{
    import base._
    object Fun2 { def unapply(x: IR[_,_]) = x match {
      case IR(RepDef(Abs(Typed(p, TypeRep(RecordType(a->ta,b->tb))),body))) =>
        Some(a,b,body)
      case _ => None
    }}
    object Fun2App { def unapply(x: IR[_,_]) = x match {
      case ir"(${Fun2(a,b,body)}: (($tta,$ttb) => $t))($arg0,$arg1)" =>
        Some(a,b,body,arg0,arg1)
      case _ => None
    }}
    rewrite {
      // FIXME:
      case ir"((px: $tx, py: $ty) => $body: $t)($ax, $ay)" =>
        body subs 'px -> ax subs 'py -> ay
        // In the meantime:  
        //case ir"(${IR(RepDef(Abs(Typed(p, TypeRep(RecordType(a->ta,b->tb))),body)))}: (($tta,$ttb) => $t))($arg0,$arg1)" => // FIXME makes `rewrite` crash
      //case ir"(${Fun2(a,b,body)}: (($tta,$ttb) => $t))($arg0,$arg1)" => // FIXME makes `rewrite` crash: Error:scala: unexpected UnApply
      case ir"${Fun2App(a,b,body,arg0,arg1)}: $t" => // FIXME makes `rewrite` crash: Error:scala: unexpected UnApply
        IR[t.Typ,{}](bottomUpPartial(body) {
          case RepDef(RecordGet(re, `a`, _)) => arg0.rep
          case RepDef(RecordGet(re, `b`, _)) => arg1.rep
        })
    }
  }*/
  
}
object FoldTupleVarOptim extends FoldTupleVarOptim

