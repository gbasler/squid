package sfusion2
package compiler

import squid.utils._
import squid.ir._
import squid.lang._
import squid.anf.analysis
import squid.anf.transfo
import example.VarNormalizer // TODO move to main Squid

/**
  * Created by lptk on 13/06/17.
  */
object Embedding
  //extends squid.ir.SimpleANF
  extends squid.ir.SchedulingANF
  with ScalaCore
  with ClassEmbedder with OnlineOptimizer with analysis.BlockHelpers with StandardEffects
  //with Strm.Lang // FIXME
{
  object Desug extends Desugaring
  //object Norm extends SelfTransformer with transfo.StandardNormalizer
  object Norm extends SelfTransformer with SimpleRuleBasedTransformer 
    with CurryEncoding.ApplicationNormalizer 
    with transfo.OptionNormalizer 
    //with CPSOptionNormalizer
    with transfo.TupleNormalizer
    with transfo.FunctionNormalizer
    with transfo.IdiomsNormalizer
    //
    with VarNormalizer
    with transfo.LogicNormalizer
    with transfo.EqualityNormalizer 
  {
    import base.Predef._
    //import self.base.InspectableIROps
    //import self.base.IntermediateIROps
    //rewrite {
    //  case ir"val x: $xt = if ($cnd) $thn else $els; $body: $bt" =>
    //    println(cnd,thn,els,body)
    //    ir"val k = (x:$xt) => $body; if ($cnd) k($thn) else k($els)"
    //}
  }
  //object LFNorm extends SelfTransformer with transfo.LogicFlowNormalizer // Q: good idea online? (probably not!)
  def pipeline = Desug.pipeline andThen Norm.pipeline
  //def pipeline = Desug.pipeline andThen Norm.pipeline andThen LFNorm.pipeline
  
  //override val transformExtractors = true
  
  embed(Strm)
  //embed(impl.`package`)
  
  //import squid.utils.meta.RuntimeUniverseHelpers.sru
  //transparentTyps += sru.typeOf[sfusion.`package`.type].typeSymbol.asType // for minSize, addToSize & co.
  
  Norm
  //Embedding.transformExtractors = true
  
}

/*

trait CPSOptionNormalizer extends SimpleRuleBasedTransformer { self =>
  val base: SimpleANF with ScalaCore
  import base.Predef._
  import self.base.InspectableIROps
  import self.base.IntermediateIROps
  
  import base.{RepDef,BoundVal,IR}
  object BV { def unapply(x: IR[_,_]) = x match {
    case IR(RepDef(bv: BoundVal)) => Some(bv)
    case _ => None }}
  
  rewrite {
      
    // Simplifications
    
    case ir"(Some[$t]($v):Option[t]).get" => ir"$v" // TODO other ones.... (annoying, seems Some.get is not the same symbol as Option.get) -- thankfully only isEmpty seems to be in the same situation
    case ir"Some[$t]($v).get" => ir"$v"
    //case ir"None.get" => ir"???" // TODO generate explicit assertion failure/contract violation?
    case ir"Some[$t]($v).getOrElse($d)" => ir"$v"
    case ir"None.getOrElse[$t]($d)" => ir"$d"
    //case ir"Some[$t]($_).isDefined" => ir"true" // FIXME allow `_` in patterns...
    case ir"Some[$t]($v).isDefined" => ir"true"
    case ir"None.isDefined" => ir"false"
      
    case ir"Some[$t]($v).fold[$r]($els)($thn)" => ir"$thn($v)"
    case ir"(Some($v:$t):Option[t]).fold[$r]($els)($thn)" => ir"$thn($v)"
    case ir"(None:Option[$t]).fold[$r]($els)($thn)" => ir"$els"
      
      
    // Feature Streamlining
    
    case ir"Option.empty[$t]" => ir"None"
    //case ir"Option[$t]($v)" => ir"if ($v == null) None else Some($v)"
    //case ir"($opt:Option[$t]).nonEmpty" => ir"$opt.isDefined"
    //case ir"($opt:Option[$t]).isEmpty" => ir"!$opt.isDefined"
    //case ir"($opt:Option[$t]).fold[$s]($dflt)($thn)" => ir"if ($opt.isDefined) $thn($opt.get) else $dflt"
    //case ir"($opt:Option[$t]).filter($f)" => ir"if ($opt.isDefined && $f($opt.get)) $opt else None"
    //case ir"($opt:Option[$t]).map[$mt]($f)" => ir"if ($opt.isDefined) Some($f($opt.get)) else None"
    //case ir"($opt:Option[$t]).orElse[t]($other)" => ir"if ($opt.isDefined) $opt else $other" // TODO test with different type params
    //// TODO handle other features... e.g. flatMap
      
    case ir"($opt:Option[$t]).map[$mt]($f)" => ir"$opt.fold[Option[$mt]](None)(x => Some($f(x)))"
    
      /*
    //case ir"val $opt: Option[$r] = $optv; $body: $bt" =>
    //case ir"val $opt: Option[$r] = if ($cnd) $thn else $els; $body: $bt" =>
    case ir"val $opt: Option[$r] = if ($cnd) $thn else $els; $body: $bt" =>
      ir"val k = (opt:Option[$r]) => $body; if ($cnd) k($thn) else k($els)"
      */
      
    //case ir"val $x: $xt = if ($cnd) $thn else $els; $body: $bt" =>  // FIXME java.util.NoSuchElementException: key not found: x
    case ir"val x: $xt = if ($cnd) $thn else $els; $body: $bt" =>
      println(cnd,thn,els,body)
      ir"val k = (x:$xt) => $body; if ($cnd) k($thn) else k($els)"
      
      
      
      
      
      
      /*
    //case ir"val ${v @ BV(opt)}: Option[$r] = if ($cnd) $thn else $els; $body: $bt" =>
    case ir"val ${opt}: Option[$r] = if ($cnd) $thn else $els; $body: $bt" =>
      //if (opt.rep.dfn.)
      //val BV(optbv) = opt
      val optbv = opt.rep.dfn.asInstanceOf[BoundVal]
      println(opt,body)
      println(body.rep.occurrences)
      //println(body.rep.occurrences(opt.rep))
      //println(body.rep.occurrences(base.rep(optbv)))
      //println(body.rep.occurrences(base.rep(optbv.toHole())))
      //println(body.rep.occurrences(ir"opt?:Option[$r]".rep))
      //println(opt.rep.)
      ???
      */
      
      
    // Commuting with IF
    
    case ir"(if ($c) $th else $el : Option[$t]).get" => ir"if ($c) $th.get else $el.get"
    case ir"(if ($c) $th else $el : Option[$t]).getOrElse($d)" => ir"if ($c) $th.getOrElse($d) else $el.getOrElse($d)"
    case ir"(if ($c) $th else $el : Option[$t]).isDefined" => ir"if ($c) $th.isDefined else $el.isDefined"
    
  }
  
}
*/

