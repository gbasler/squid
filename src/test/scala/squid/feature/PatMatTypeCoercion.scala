package squid
package feature

import squid.utils.meta.RuntimeUniverseHelpers
import utils.Debug._

//class PatMatTypeCoercion extends MyFunSuite(anf.SimpleANFTests.DSL) {  // <-- already does beta reduction
class PatMatTypeCoercion extends MyFunSuite {
  import DSL.Predef._
  //import DSL.{LeafCode}
  type Code[T] = IR[T,Any]
  
  test("Simple Type Coercion") {
    
    import quasi.SuppressWarning.`scrutinee type mismatch`
    
    // TODO: a way to remove the warning... without actually throwing the type info (which is being used)
    def foo[T:IRType](x : IR[List[T],Any]) = x match {
      case ir"List[$t0]($x)" => ir"$x:T" // compiles
    }
    
    assertDoesNotCompile("""
    def foo[T:IRType](x : IR[List[T],Any]) = x.erase match {  // erasure prevents subtyping knowledge to be obtained
      case ir"List[$t0]($x)" => ir"$x:T"
    }
    """)
    
    eqt(foo(ir"List(1)").typ, irTypeOf[Int])
    
  }
  
  test("GADT-like Pattern Matching") {
    
    def beta[T:IRType](x:Code[T]): Code[T] = x match {
      case ir"((p: $t0) => $body: T)($a: t0)" => body subs ('p -> beta(a))
      case ir"($f: ($t0 => T))($a)" => val (f0, a0) = (beta(f), beta(a)); ir"$f($a)":Code[T]
      case ir"(p: $t0) => $body: $t1" =>
        val bodyf = (x:Code[t0.Typ]) => body subs 'p -> x
        ir"{(p: $t0) => ${(x:Code[t0.Typ]) => beta(bodyf(x))}(p)} : T" // coercion
      case ir"($a:Int) + ($b:Int)" => ir"${beta(a)} + ${beta(b)} : T" // coercion
      
      //case LeafCode(_) => x
        
      case Const(n) => Const(n)
      case base.IR((base.RepDef(h:base.Hole))) => base.IR((base.rep(h)))
      case base.IR((base.RepDef(bv:base.BoundVal))) => base.IR((base.rep(bv)))
      // ^ Should use LeafCode, but LeafCode is currently only defined for SimpleANF
    }
    
    assertCompiles("""
      def foo[T:IRType](x:Code[T]): Code[T] = x match {
        case ir"$x:$t2" => ir"$x:T" // compiles, good
        case ir"$x:Int" => ir"$x:T" // compiles, good
      }
    """)
    
    assertDoesNotCompile("""
      def foo[T:IRType](x:Code[T]): Code[T] = x match {
        case ir"println($x:$t2)" => ir"$x:T" // rejected (good) -- t2 is a different type
      }
    """)
      
    assertDoesNotCompile("""
      def foo[T:IRType](x:Code[T]): Code[T] = x match {
        case ir"$x:Int" => ir"$x:T"
        case ir"println($x:Int)" => ir"$x:T" // should reject; assumption in previous case (Int<:T) should not bleed through here!
      }
    """)
    
    ir"((x:Int) => (y:Int) => x+y)(42)"        neqt ir"(z:Int) => 42 + z"
    ir"((x:Int) => (y:Int) => x+y)(42)" |> beta eqt ir"(z:Int) => 42 + z"
    
  }
  
}