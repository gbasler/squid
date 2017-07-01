package stagerwr2
package compiler

import Embedding.Predef._
import Embedding.Quasicodes._

import example.VarNormalizer
import squid.utils._
import squid.ir._
import squid.lang._
import squid.anf.analysis
import squid.anf.transfo
import Embedding.Rep
import Embedding.{Block, AsBlock, WithResult, GeneralClosure}

/*

TODO explore what to do on the pullable arg of a zip
  are algebraic normalization rules enough?
  is there something to do to fold .producer()?

*/

class DbgCompiler extends Compiler { override val printResults = true }

class Compiler extends Optimizer {
  
  val base: Embedding.type = Embedding
  
  val printResults = false
  
  var ctx = Option.empty[scala.tools.nsc.io.File]
  override def setContext(src:String) = {
    val f = scala.tools.nsc.io.File(src)
    if (f.exists) f.delete()
    ctx = Some(f)
  }
  
  def dump(phaseName:String,tree:base.Rep) = {
    lazy val str = s"// --- $phaseName ---\n"+base.showRep(tree)
    if (printResults) println(str)
    ctx foreach { _.appendAll(str+"\n") }
  }
  
  def pipeline = (r: Rep) => {
    
    dump("Source",r)
    
    val r0 = ImplFlowOptimizer pipeline r
    dump("ImplFlowOptimizer",r0)
    //r0
    
    val r1 = ImplLowering pipeline r0
    dump("ImplLowering",r1)
    //r1
    
    //val r2 = FlatMapFusion pipeline r1
    //dump("FlatMapFusion",r2)
    ////r2
    
    val r3 = LowLevel pipeline r1
    dump("LowLevel",r3)
    r3
    
  }
  
}

//object LowLevel extends Embedding.Lowering('LL) with FixPointTransformer with TopDownTransformer
object LowLevel extends Embedding.SelfTransformer with FixPointRuleBasedTransformer with TopDownTransformer {
  
  rewrite {
    //case ir"Strm.loopWhile($cnd)" => ir"while($cnd)()"
    case ir"Strm.loopWhile($cnd)" => ir"var cont = true; while(cont)(cont = $cnd)"
  }
  
}


// TODO warn when Strm ctors remain (they may not be let-bound!)
object ImplInlining extends Embedding.Lowering('Impl)
object ImplCtorInline extends Embedding.SelfIRTransformer with IRTransformer with FixPointTransformer {
  def transform[T,C](code: IR[T,C]): IR[T,C] = (code match {
      
    case ir"(if ($c) $thn else $els : Strm[$ta]).producer" => ir"if ($c) $thn.producer else $els.producer" // TODO rm?
      
    case ir"new Strm($pf).producer" => pf
    case ir"val $st = new Strm[$t]($pf); $body: $bt" =>
      body rewrite { case ir"$$st.producer" => pf } subs 'st -> {System.err.println("huh s");return code}
      
    case ir"Strm.pullable($as:Strm[$ta])" => as
      
    case ir"Strm.doFlatMap[$ta,$tb]($pa, a => $body)" =>
      println(s"Trying to flatten $pa with $body")
      
      //val bodyFun = f transformWith ImplLowering
      
      import base.SimplePredef.unsound
      import base.SimplePredef.{Rep=>Code}
      
      val r = NeatClosure2.doFlatMapStaged(pa,(x:Code[ta.Typ]) => body subs 'a -> x)
      println(r)
      
      r
      //???
      
    // works, but for the paper I want a simpler interface that accepts dealing with open terms
      /*
    //case ir"($as:Strm[$ta]).flatMap[$tb]($f)" =>
    //case ir"Strm.doFlatMap[$ta,$tb]($pa,$f)" =>
    case ir"Strm.doFlatMap[$ta,$tb]($pa,$f)" =>
      
      println(s"Trying to flatten $pa with $f")
      val f0 = f transformWith ImplLowering
      println(s"ie $f0")
      
      //f0 match {
      //  case ir"() => $body"
      //}
      
      import Embedding.SimplePredef.{Rep=>Code,_}
      
      implicit class CodeOps[T](private val r: Code[T]) /*extends AnyVal*/ {
        def capture[C] = r: IR[T,C]
      }
      
      //val body = (x: Code[ta.Typ]) => f subs 'a -> x  // TODO use
      
      ///
      //val NeatClosure(clos) = f0 match {
      //  case ir"(a:$ta) => $body" => body
      //}
      val body = f0 match {
        case ir"(a:$$ta) => $body : Producer[$$tb]" => body
      }
      val NeatClosure(clos) = body
      
      val res = clos.make { (term,reset) =>
        println("TR: ",term,reset)
        ir"""
          var curA: Option[$ta] = None
          (k:Consumer[$tb]) => {
            var consumed = false
            Strm.loopWhile {
              if (!curA.isDefined) $pa{a => curA = Some(a); ${reset.capture[{val a:ta.Typ}]}() }
              curA.fold(false) { a =>
                ${term.capture[{val a:ta.Typ}]} { b =>
                  k(b)
                  consumed = true
                }
                if (!consumed) { curA = None; true } else false
              }
            }
          }
        """
      }
      println(res)
      ///
      
      //val body = f0 match {
      //  case ir"(a:$$ta) => $body : Producer[$$tb]" =>
      //    (x: Code[ta.Typ]) => body subs 'a -> x
      //}
      //val closedBody = 
      //val NeatClosure(clos) = body
      
      res.get
      */
      
      
    case _ => code
  }).asInstanceOf[IR[T,C]]
}
//object ImplLowering extends Embedding.TransformerWrapper(ImplInlining, ImplCtorInline) with TopDownTransformer with FixPointTransformer
object ImplLowering extends Embedding.TransformerWrapper(ImplInlining, ImplCtorInline) with BottomUpTransformer with FixPointTransformer
//object ImplLowering extends Embedding.TransformerWrapper(ImplCtorInline,ImplInlining) with TopDownTransformer with BottomUpTransformer // STUPID



import Strm._
import Embedding.Predef._


object ImplFlowOptimizer extends Embedding.SelfTransformer with FixPointRuleBasedTransformer with BottomUpTransformer with FixPointTransformer { self =>
//object ImplFlowOptimizer extends Embedding.SelfTransformer with FixPointRuleBasedTransformer with TopDownTransformer with FixPointTransformer { self =>
  
  rewrite {
      
    // Syntactic Normalization
    
    case ir"(if ($c) $thn else $els : Strm[$ta])" => 
      ir"conditionally($c)($thn,$els)"
    
      
    // Bubble up pullable
      
    case ir"pullable($as:Strm[$ta]).map[$tb]($f)" =>
      ir"pullable($as.map[$tb]($f))"
      
    case ir"pullable($as:Strm[$ta]).flatMap[$tb]($f)" =>
      ir"$as.flatMap[$tb]($f)" // flattened streams are not pullable :-(
      
    case ir"pullable($as:Strm[$ta]).filter($pred)" =>
      ir"pullable($as.filter($pred))"
      
    case ir"pullable($as:Strm[$ta]).take($n)" =>
      ir"pullable($as.take($n))"
      
    case ir"conditionally[$ta]($c)(pullable($thn),pullable($els))" =>
      ir"pullable(conditionally[$ta]($c)($thn,$els))"
      
      
    // Folding
      
    case ir"consumeWhile(pullable($as:Strm[$ta]))($f)" =>
      ir"consumeWhile($as)($f)"
      
    case ir"consumeWhile(($as: Strm[$ta]).map[$tb]($f))($g)" =>
      ir"consumeWhile($as)($f andThen $g)"
    
    case ir"consumeWhile(($as: Strm[$ta]).take($n))($f)" =>
      //ir"var taken = 0; consumeWhile($as){a => taken < $n && $f(a) }"
      ir"var taken = 0; consumeWhile($as){a => val t = taken; taken = t+1; t < $n && $f(a) }"
    
    case ir"consumeWhile(($as: Strm[$ta]).flatMap[$tb]($f))($g)" =>
      //ir"consumeWhileNested($as)($f andThen $g)"
      ir"consumeWhileNested($as)($f)($g)"
      
    case ir"consumeWhile(($as:Strm[$ta]).filter($pred))($f)" =>
      //ir"consumeWhile($as)(a => if ($pred(a)) $f(a) else true)"
      ir"consumeWhile($as)(a => !$pred(a) || $f(a))"
      
    // this actually makes code bigger, by duplicating the whole loop (instead of just the inside)
    //   it's still arguably better since it means the branch is executed only once instead of at every iteration
    case ir"consumeWhile(conditionally[$ta]($c)($thn,$els))($f)" =>
      ir"if ($c) consumeWhile($thn)($f) else consumeWhile($els)($f)"
      
      
    // for paper:
    case ir"consumeWhile(fromArrayImpl[$ta]($xs))($f)" =>
      ir"val len = $xs.length; var i = 0; var cont = true; while(i < len && cont) { cont = $f($xs(i)); i += 1 }"
      
      
    // Zipping
      
    case ir"consumeWhile(pullable($as:Strm[$ta]).zipWith[$tb,$tc]($bs)($f))($g)" =>
      ir"consumeWhileZipped($bs,$as.producer())((b,a) => $g($f(a,b)))"
      
    case ir"consumeWhile(($as:Strm[$ta]).zipWith[$tb,$tc](pullable($bs))($f))($g)" =>
      //ir"val p = $bs.producer(); consumeWhile($as) { a => p { b => $g($f(a,b)) } }"
      //ir"consumeWhileZipped($as,$bs.producer())($f andThen $g)"
      ir"consumeWhileZipped($as,$bs.producer())((a,b) => $g($f(a,b)))"
      
    case ir"consumeWhile(($as:Strm[$ta]).zipWith[$tb,$tc]($bs)($f))($g)" =>
      
      println("AS: "+as)
      println("BS: "+bs)
      
      // TODO heuristic for choosing the one to try and flatten first?
      
      //val as0 = ir"(k:$ta=>Bool) => consumeWhile($as)(k)" transformWith ImplFlowOptimizer
      //val bs0 = bs transformWith ImplFlowOptimizer
      val as0 = as transformWith ImplLowering
      //val bs0 = bs transformWith ImplLowering
      
      println(as0)
      //println(bs0)
      
      ir"consumeWhile(pullable($as0).zipWith($bs)($f))($g)"
      
      //???
      
      
      
      ///*
      
    // Normalization (helps with flatMap streamlining)
      
      
    case ir"($as: Strm[$ta]).map[$tb]($f).take($n)" =>
      //println(0)
      ir"$as.take($n).map[$tb]($f)"
      
    //case ir"($as: Strm[$ta]).map[$tb]($f).drop($n)" =>
    //  ir"$as.drop($n).map[$tb]($f)"
      
    case ir"($as: Strm[$ta]).take($n).take($m)" =>
      ir"$as.take($n max $m)"
      
    //case ir"($as: Strm[$ta]).take($n).drop($m)" =>
    //  ir"$as.drop($m).take($n - $m)" // FIXME careful with drop(-n) ... normally valid
    //  
    //case ir"($as: Strm[$ta]).drop($n).drop($m)" =>
    //  ir"$as.drop($n + $m)"
      
      
    case ir"($as: Strm[$ta]).map[$tb]($fb).map[$tc]($fc)" =>
      ir"$as.map($fb andThen $fc)"
      
    case ir"($as: Strm[$ta]).flatMap[$tb]($fb).map[$tc]($fc)" =>
      //ir"$as.flatMap(_.map(_.map($fb andThen $fc))"
      ir"$as.flatMap(as => $fb(as).map($fc))"
      
    case ir"($as: Strm[$ta]).map[$tb]($fb).flatMap[$tc]($fc)" =>
      ir"$as.flatMap($fb andThen $fc)"
      
    // TODO?
    //case ir"($as: Strm[$ta]).filter($pred)" =>
    //  ir"$as.flatMap(a => singleIf(a,$pred(a)))"
      
      //*/
      
    
    
  }
  
}





