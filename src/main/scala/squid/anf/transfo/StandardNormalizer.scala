package squid
package anf.transfo

import utils._
import ir._
import lang.{ScalaCore,InspectableBase}
import utils.Debug.show


trait StandardNormalizer extends SimpleRuleBasedTransformer 
  with CurryEncoding.ApplicationNormalizer 
  with OptionNormalizer 
  with TupleNormalizer
  with FunctionNormalizer
  with IdiomsNormalizer


/**
  * Created by lptk on 11/02/17.
  * 
  * Goal: reduce all Option operations to Some, None, isDefined and get, so as to simplify further passes.
  * 
  * TODO for Either
  * TODO generalize, for all ADTs? (reinterpret all ADTs as Either's?)
  * 
  */
trait OptionNormalizer extends SimpleRuleBasedTransformer { self =>
  val base: InspectableBase with ScalaCore
  import base.Predef._
  import self.base.InspectableIROps
  import self.base.IntermediateIROps
  
   // To generate on cases statically-known to be failures;
   // will help remove dead code under the assumption this is an undefiend behavior:
  lazy val NoneGet = ir"""assert(false, "None.get").asInstanceOf[Nothing]"""
  
  rewrite {
      
    // Simplifications
    
    case ir"Some[$t]($v).get" => ir"$v"
    case ir"None.get" => NoneGet
    case ir"Some[$t]($v).getOrElse($d)" => ir"$v"
    case ir"None.getOrElse[$t]($d)" => ir"$d"
    //case ir"Some[$t]($_).isDefined" => ir"true" // FIXME allow `_` in patterns...
    case ir"Some[$t]($v).isDefined" => ir"true"
    case ir"None.isDefined" => ir"false"
      
    // Methods `isEmpty` and `get` are redefined in `Some` and `None` (ie Some.get is not the same symbol as Option.get),
    // so we need to handle the separate Option symbols here:
    case ir"(Some[$t]($v):Option[t]).isEmpty" => ir"false"
    case ir"(Some[$t]($v):Option[t]).get" => ir"$v"
    case ir"(None:Option[$t]).isEmpty" => ir"true"
    case ir"(None:Option[$t]).get" => NoneGet
      
      
    // Feature Streamlining
    
    case ir"Option.empty[$t]" => ir"None"
    case ir"Option[$t]($v)" => ir"if ($v == null) None else Some($v)"
    case ir"($opt:Option[$t]).nonEmpty" => ir"$opt.isDefined"
    case ir"($opt:Option[$t]).isEmpty" => ir"!$opt.isDefined"
    case ir"($opt:Option[$t]).fold[$s]($dflt)($thn)" => ir"if ($opt.isDefined) $thn($opt.get) else $dflt"
    case ir"($opt:Option[$t]).filter($f)" => ir"if ($opt.isDefined && $f($opt.get)) $opt else None"
    case ir"($opt:Option[$t]).map[$mt]($f)" => ir"if ($opt.isDefined) Some($f($opt.get)) else None"
    case ir"($opt:Option[$t]).flatMap[$mt]($f)" => ir"if ($opt.isDefined) $f($opt.get) else None"
    case ir"($opt:Option[$t]).orElse[t]($other)" => ir"if ($opt.isDefined) $opt else $other" // TODO test with different type params
    case ir"($opt:Option[$t]).foreach[$r]($f)" => ir"if ($opt.isDefined) {$f($opt.get);()} else ()"
    // TODO handle other features...
    
    // Commuting with IF
    
    case ir"(if ($c) $th else $el : Option[$t]).get" => ir"if ($c) $th.get else $el.get"
    case ir"(if ($c) $th else $el : Option[$t]).getOrElse($d)" => ir"if ($c) $th.getOrElse($d) else $el.getOrElse($d)"
    case ir"(if ($c) $th else $el : Option[$t]).isDefined" => ir"if ($c) $th.isDefined else $el.isDefined"
    
  }
  
}

trait TupleNormalizer extends SimpleRuleBasedTransformer { self =>
  val base: InspectableBase with ScalaCore
  import base.Predef._
  import self.base.InspectableIROps
  import self.base.IntermediateIROps
  
  rewrite {
      
    case ir"($a:$ta) -> ($b:$tb)" => ir"($a,$b)"
    case ir"($a:$ta,$b:$tb)._1" => ir"$a"
    case ir"($a:$ta,$b:$tb)._2" => ir"$b"
    case ir"($a:$ta,$b:$tb).swap" => ir"($b,$a)"
    case ir"($a:$ta,$b:$tb).copy($va:$tc,$vb:$td)" => ir"($va,$vb)"
      
  }
  
}

trait FunctionNormalizer extends SimpleRuleBasedTransformer { self =>
  val base: InspectableBase with ScalaCore
  import base.Predef._
  import self.base.InspectableIROps
  import self.base.IntermediateIROps
  
  rewrite {
      
    case ir"($f:$ta=>$tb).andThen[$tc]($g)" => ir"(x:$ta) => $g($f(x))"
      
  }
  
}

trait IdiomsNormalizer extends SimpleRuleBasedTransformer { self =>
  val base: InspectableBase with ScalaCore
  import base.Predef._
  import self.base.InspectableIROps
  import self.base.IntermediateIROps
  
  rewrite {
      
    case ir"($str:String).toString" => ir"$str"
      
    //case ir"($x:$t).asInstanceOf[t]" => ir"$x:$t" // FIXME... access to messed up "static" type path
    case ir"(($x:$t):Any).asInstanceOf[t]" => ir"$x:$t"
      
  }
  
}

