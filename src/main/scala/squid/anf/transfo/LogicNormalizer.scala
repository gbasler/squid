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
  
  val base: anf.analysis.BlockHelpers with lang.ScalaCore
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
      
      
    /*
    // Putting if-then-else in CPS
    case ir"val x: $xt = if ($cnd) $thn else $els; $body: $bt" =>
      ir"val k = (x:$xt) => $body; if ($cnd) k($thn) else k($els)"
    */
      
    // ^ this would be good to have, but currently ANF inlines too aggressively, so this would end up duplicating lots of code...
    // Also, it seems to create a stack overflow when the one below is also enabled:
      
    ///*
    // Note: this is let-binds the condition and uses the bound variable twice;
    // in ANF the expression (if pure) will be duplicated, but in SchedulingANF it will be re-bound in the scheduling phase
    case ir"if ($cond) $thn else $els : Boolean" =>
      ir"val c = $cond; c && $thn || !c && $els" // TODO rm usage of ||
    //*/
      
      
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
      
      
      
    case ir"if ($c0) { if ($c1) $thn else $els } else els : $t" => ir"if ($c0 && $c1) $thn else $els"
    //case ir"if ($c0) $thn else { if ($c1) thn else $els } : $t" => // TODO
      
      
      
  }
  
}

/** Types like Int have their own == symbol, different from Any.==, which is very annoying when wanting to treat 
  * equality uniformly. So this replaces all == and != method calls by calls to `Any.equals`, for consistency.
  * This also normalizes if-then-else.equals and partially evaluates constant equality tests. */
trait EqualityNormalizer extends SimpleRuleBasedTransformer { self =>
  
  val base: anf.analysis.BlockHelpers with lang.ScalaCore
  import base.Predef._
  import base.{MethodApplication}
  
  rewrite {
  
    case ir"${MethodApplication(ma)}:Bool" if ma.symbol.name.toString == "$eq$eq" =>
      ir"${ma.args(0)(0)} equals ${ma.args(1)(0)}"
      
    case ir"${MethodApplication(ma)}:Bool" if ma.symbol.name.toString == "$bang$eq" =>
      ir"!(${ma.args(0)(0)} equals ${ma.args(1)(0)})"
    
    case ir"(if ($c) $t else $e : Any) equals $x" =>
      ir"if ($c) $t equals $x else $e equals $x"
      
    case ir"(${Const(x)}:Any) equals ${Const(y)}" => Const(x == y)
      
  }
  
}

/** Traverses a program and its expressions, accumulating what is known to be true or false if the current code is being
  * executed. For example, will rewrite {{{ n > 0 && {assert(m != 0); (n > 0)} && m != 0 }}} to {{{ n > 0 && {assert(m != 0); true} && true }}}.
  * Best used along with LogicNormalizer because this only deals with `. && .` and `!.` but not with `. || .`.
  * Only supposed to work in ANF or any other IR where by-value conditions (eg to an IF-THEN-ELSE) are guaranteed to be pure. */
trait LogicFlowNormalizer extends IRTransformer { self =>
  /*
  
  FIXME: track state dependencies and invalidate formulae on mutation.
    At the very least, consider anything not pure can change value and either:
     - don't record it, or
     - invalidate it as soon as anything impure is executed
  
  */
  
  val base: anf.analysis.BlockHelpers
  import base.Predef._
  import base.InspectableIROps
  import base.{Block,WithResult,MethodApplication,LeafCode,Lambda}
  
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
        
      case Block(b) => // TODO move down
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
        
      //case ir"(x: $xt) => $body : $bt" => // <- doing this would cause problems unless we have safe context polymorphism in place 
      case Lambda(lam) =>
        // Note: this assumes the lambda gets executed here, which is obviously unsound:
        //val (c,(s,e)) = rec(lam.body)(known)
        //lam.rebuild(c) -> (s -> e)
        val (c,(s,e)) = rec(lam.body)(Trivial)
        lam.rebuild(c) -> (Trivial -> Trivial)
        // ^ to do better than this, we could:
        // - use a heuristic for when the lambda actually gets executed on the spot (for example in List.map(lam), but not in Stream.map(lam))
        // - otherwise only pass those formulae in `known` that constant (won't become false later on due to mutability)
        
      // Note: no need to special case let-bindings, as they are handled by the Block case above
      
      // FIXME add case for traversing module accesses on non-static paths (not a leaft node)
      // TODO add special case for while
      
      case LeafCode(_) => code -> (Trivial -> Trivial)
        
      case e =>
        System.err.println(s"Warning: case was not handled by LogicFlowNormalizer: $e")
        e -> (Trivial -> Trivial)
      
    }
    
    rec(code)(Trivial)._1
  }
  
}
