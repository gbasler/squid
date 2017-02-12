package squid
package anf.transfo

import utils._
import ir._
import utils.Debug.show

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
  import base.Predef._
  import self.base.InspectableIROps
  import self.base.IntermediateIROps
  
  rewrite {
      
    // Simplifications
    
    case ir"Some[$t]($v).get" => ir"$v"
    //case ir"None.get" => ir"???" // TODO generate explicit assertion failure/contract violation?
    case ir"Some[$t]($v).getOrElse($d)" => ir"$v"
    case ir"None.getOrElse[$t]($d)" => ir"$d"
    //case ir"Some[$t]($_).isDefined" => ir"true" // FIXME allow `_` in patterns...
    case ir"Some[$t]($v).isDefined" => ir"true"
    case ir"None.isDefined" => ir"false"
      
    // Feature Streamlining
    
    case ir"Option.empty[$t]" => ir"None"
    case ir"Option[$t]($v)" => ir"if ($v == null) None else Some($v)"
    case ir"($opt:Option[$t]).nonEmpty" => ir"$opt.isDefined"
    case ir"($opt:Option[$t]).isEmpty" => ir"!$opt.isDefined"
    case ir"($opt:Option[$t]).fold[$s]($dflt)($thn)" => ir"if ($opt.isDefined) $thn($opt.get) else $dflt"
    case ir"($opt:Option[$t]).filter($f)" => ir"if ($opt.isDefined && $f($opt.get)) $opt else None"
    case ir"($opt:Option[$t]).map[$mt]($f)" => ir"if ($opt.isDefined) Some($f($opt.get)) else None"
    
    // TODO other features... e.g. flatMap
    
  }
  
}
