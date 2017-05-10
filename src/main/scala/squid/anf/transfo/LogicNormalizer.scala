package squid
package anf.transfo

import utils._
import ir._
import utils.Debug.show

/**
  * Created by lptk on 01/02/17.
  * TODO? move to anf.transfo.StandardNormalizer
  */
/* 
  TODO: have specialized treatment of booleans and logic in the IR
    Some things like `x>0 || ... || !(x>0) || ...` are not syntactic enough to optimize with these rules
    In general, we need to allow extension to, eg, make special support for numeric types and make `x==0` equivalent to `x>=0 && x<=0`
  ... actually can do this with a better analysing pass – see LogicFlowNormalizer
  
  Note: IF-THEN-ELSE returning Bool is normalized using `&&` and `!`
  
  TODO: normalize `false` to `!true`?
  
  TODO: normalize `while` loops to put all code in the condition? (it's more general than everything in the body)
  
*/
trait LogicNormalizer extends SimpleRuleBasedTransformer { self =>
  
  val base: anf.analysis.BlockHelpers
  import base.Predef._
  import base.{AsBlock,WithResult}
  
  rewrite {
    
    case ir"!true" => ir"false"
    case ir"!false" => ir"true"
    case ir"!(!($x:Bool))" => ir"$x"
      
    // Streamlining, so we don't have to deal with || but only &&
    case ir"($x:Bool) || $y" => ir"!(!$x && !$y)"
      
    case ir"($x:Bool) && false" => ir"false"
    case ir"($x:Bool) && true" => ir"$x"
    case ir"false && ($x:Bool)" => ir"false"
    case ir"true && ($x:Bool)" => ir"$x"
      
    // Generalization of the above (first half) for blocks. The above are kept because they do not generate useless if-statements.
    case ir"($x:Bool) && ${AsBlock(WithResult(b, ir"false"))}" => ir"if ($x) ${b.statements()}; false"
    case ir"($x:Bool) && ${AsBlock(WithResult(b, ir"true"))}" => ir"if ($x) ${b.statements()}; $x"
    // ^ Note: not just writing `ir"if ($x) ${b.original}; $x"` because that would make an IF-THEN-ELSE with result of
    // type Any, which makes the 'then' branch artificially look like it's using its result; here we use b.statements(), 
    // of return type Unit.
      
    case ir"if (true) $x else $y : $t" => ir"$x"
    case ir"if (false) $x else $y : $t" => ir"$y"
    case ir"if (!($cond:Bool)) $x else $y : $t" => ir"if ($cond) $y else $x"
      
    case ir"if ($cond) $thn else thn : $t" => ir"$thn" // this one might be expensive as it compares the two branches for equivalence as expressions
    //// more general, but too complex (would need crazy bindings manip):
    //case ir"if ($cond) ${Block(b0)} else ${Block(b1)} : $t" if b0.res == b1.res  =>  ir"if ($cond) ${b0.statements()} else ${b1.statements()}; ???"
      
    //case ir"if ($cond) $thn else $els : Boolean" => 
    //  ir"val c = $cond; c && $thn || !c && $els" // TODO rm usage of ||
      
    // TODO later?:
    //case ir"($x:Bool) && ${AsBlock(WithResult(b, ir"false"))}" => ir"if ($x) ${b.statements()}; false"
      
    // these ones might be expensive as they compare the two branches for equivalence as expressions
    case ir"($x0:Bool) && ${AsBlock(b)}" if x0 =~= b.res  =>  ir"${b.statements()}; $x0"
    case ir"($x0:Bool) && ${AsBlock(WithResult(b, ir"!($x1:Boolean)"))}" if x0 =~= x1  =>  ir"false"
      
    case ir"assert(true)" => ir"()"
    case ir"require(true)" => ir"()"
    case ir"($x:$xt) ensuring true" => ir"$x"
      
    // Equivalent to `case ir"while(${Block(b)}){$body}" if b.ret =~= ir"true" => ...`
    case ir"while(${AsBlock(WithResult(b, ir"true" ))}) $body" => ir"while(true) { ${b.statements()}; $body }"
    case ir"while(${AsBlock(WithResult(b, ir"false"))}) $body" => b.statements()
      
    // Associatign on the left
    case ir"($x:Bool) && (($y:Bool) && $z)" => ir"$x && $y && $z"
      
  }
  
}

/** Traverses a program and its expressions, accumulating what is known to be true or false if the current code is being
  * executed. For example, will rewrite {{{ n > 0 && {assert(m != 0); (n > 0)} && m != 0 }}} to {{{ n > 0 && {assert(m != 0); true} && true }}}.
  * Best used along with LogicNormalizer because this only deals with `. && .` and `!.` but not with `. || .`.
  * Only supposed to work in ANF or any other IR where by-value conditions (eg to an IF-THEN-ELSE) are guaranteed to be pure. */
trait LogicFlowNormalizer extends IRTransformer { self =>
  
  val base: anf.analysis.BlockHelpers
  import base.Predef._
  import base.InspectableIROps
  import base.{AsBlock,WithResult,MethodApplication}
  
  object Tab extends squid.utils.algo.Tableaux {
    type Symbol = IR[Bool,Nothing]
    override def equal(s0: Symbol, s1: Symbol): Bool = s0 =~= s1
  }
  import Tab.{Formula,Formulas,Atom,Trivial}
  
  def transform[T0,C0](code: IR[T0,C0]): IR[T0,C0] = {
    
    // Returns the transformed code along with what the code implies as true (for example because of a call to `assert`), 
    // and what this code implies as true if it returns true (in case it's of type Bool).
    // For example, `q = code"{assert(n > 0); !(m > 0)}"` should return `q -> (code"n > 0" -> ¬code"m > 0")`
    def rec[T,C](code: IR[T,C])(known: Formula): IR[T,C] -> (Formula -> Formula) = code match {
        
      case ir"!($c:Bool)" =>
        val (c0,(s1,e1)) = rec(c)(known)
        ir"!$c0".asInstanceOf[IR[T,C]] -> (s1,!e1)
        
      case ir"($c:Bool) && $d" =>
        val (c0,(s1,e1)) = rec(c)(known)
        val k2 = known ∧ s1 ∧ e1
        val (d0,(s2,e2)) = rec(d)(k2)
        ir"$c0 && $d0".asInstanceOf[IR[T,C]] -> (s1 ∧ s2, e1 ∧ e2)
        
      case AsBlock(b) if b.stmts.nonEmpty => // TODO move down
        //println("Block with "+known)
        var ks: Formula = Trivial
        var ke: Formula = Trivial
        val b0 = b.rebuild(
        new base.SelfTransformer with IRTransformer {
          def transform[T1,C1](code: IR[T1,C1]): IR[T1,C1] = {
            val (c,(s,e)) = rec(code)(known ∧ ks)
            ks = ks ∧ s
            c
          }
        }, 
        new base.SelfTransformer with IRTransformer {
          def transform[T1,C1](code: IR[T1,C1]): IR[T1,C1] = {
            val (c,(s,e)) = rec(code)(known ∧ ks)
            ks = ks ∧ s
            ke = e
            c
          }
        })
        b0 -> (ks -> ke)
        
      case ir"assert($e)" => // TODO generalize
        //println(s"Assert: $e")
        code -> ((Atom(e), Trivial))
        
      case ir"if ($cond) $thn else $els : $t" => // TODO generalize, use Cflow.Xor
        //println(s"ITE($cond) with "+known)
        val (cond0,(s0,e0)) = rec(cond)(known)
        val (thn0,(s01,e01)) = rec(thn)(known ∧ s0 ∧ e0)
        val (els0,(s02,e02)) = rec(els)(known ∧ s0 ∧ !e0)
        ir"if ($cond0) $thn0 else $els0".asInstanceOf[IR[T,C]] -> ((s0 ∧ (s01 ∨ s02), e01 ∨ e02))
        
      case ir"$e:Bool" =>
        //println(s"Bool($e) with "+known)
        val a = Atom(e)
        if (!Formulas.isSatisfiable(!a ∧ known)) ir"true".asInstanceOf[IR[T,C]] -> (Trivial -> Trivial)
        else if (Formulas.isValid(!(a ∧ known))) ir"false".asInstanceOf[IR[T,C]] -> (Trivial -> Trivial)
        else {
          //println(s"Assume: $e")
          code -> ((Trivial, Atom(e)))
        }
        
      case MethodApplication(ma) =>
        //println(s"MethodApplication(${ma.symbol}) with "+known)
        var ks: Formula = Trivial
        ma.rebuild(new base.SelfIRTransformer { // TODO extract and make common with above
          def transform[T1,C1](code: IR[T1,C1]): IR[T1,C1] = {
            val (c,(s,e)) = rec(code)(known ∧ ks)
            ks = ks ∧ s
            c
          }
        }) -> ((ks,Trivial))
        
      // FIXME add case for traversing lambdas...
      // TODO add special case for while
        
      case e =>
        //println(s"Default: $e")
        e -> (Trivial -> Trivial)
      
    }
    
    rec(code)(Trivial)._1
  }
  
}
