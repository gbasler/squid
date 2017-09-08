/*
package stagerwr2
package compiler


import squid.lib.{Var,uncheckedNullValue}
import Embedding.Predef._
import Embedding.SimplePredef.{Rep=>Code,_}

object NeatClosure {
  
  def mkFun[S,T](body: IR[T,{val x:S}]) = (x: Code[S]) => body subs 'x -> x
  
  //def rec[T:IRType](t: Code[T], reset: Code[() => Unit]): Code[T] = {
  def rec[T:IRType,R:IRType](t: Code[T], reset: Code[() => Unit], term_reset: (Code[T], Code[() => Unit]) => Code[R]): Option[Code[R]] = {
  //def rec[T:IRType,R:IRType,C](t: Code[T], reset: Code[() => Unit], term_reset: (Code[T], Code[() => Unit]) => IR[R,C]): IR[R,C] = {
    println(s"REC $t $reset")
    t match {
      //def rec[T:IRType](t: Code[T], reset: Code[Unit => Unit]): NeatClosure[T,Any] = t match {
    
      case ir"val x = Var[$xt]($init); $body: T" =>
      
        val bodyFun = mkFun(body)
        //println("BODY "+body)
        //println("BODY' "+bodyFun(ir"???"))
        
        //ir"val x = Var($init); ${(x:Code[Var[xt.Typ]]) =>
        //ir"val x = Var[$xt](uncheckedNullValue); ${(x:Code[Var[xt.Typ]]) =>
        Some(ir"val mut_env = Var(uncheckedNullValue[$xt]); ${(x:Code[Var[xt.Typ]]) =>
          //val b = bodyFun(ir"$x")
          val b = bodyFun(x)
          rec(b, ir"() => { $reset(); $x := $init }", term_reset) getOrElse (return None)
        }(mut_env)")
        
        
      case ir"val x: $xt = $init; $body: T" =>
      
        val bodyFun = mkFun(body)
        //val bodyFun = (x: IR[xt.Typ,C]) => body subs 'x -> x
      
        //ir"var x: $xt = $init; ${bodyFun andThen { b =>
        //  //unapply(b).get.term
        //  rec(b,reset)
        //}}(x)"
        //ir"val x = Var($init); ${(x:Code[Var[xt.Typ]]) =>
        //ir"val x = Var[$xt](uncheckedNullValue); ${(x:Code[Var[xt.Typ]]) =>
        Some(ir"val env = Var(uncheckedNullValue[$xt]); ${ (x:Code[Var[xt.Typ]]) =>
          ///+
          val b = bodyFun(ir"$x.!")  // UNSAFE: the var access is let-bound, and rec ends up in an infinite loop!
          rec(b, ir"() => { $reset(); $x := $init }", term_reset) getOrElse (return None)
          //+/
          /*
          // Does not work: `acc` ends up being bound in the context, and is thus not up to date
          ir"val acc = $x.!; ${ (a:Code[xt.Typ]) =>
            val b = bodyFun(a)
            rec(b, ir"() => { $reset(); $x := $init }", term_reset)
          }(acc)"
          */
          //???
        }(env)")
        
      //???
        
    
      case _ =>
        //println(reset)
        //???
        //new NeatClosure[T,Any] {
        //}
        Some(term_reset(t,reset))
        
      //case _ => Some(new NeatClosure[T,C] {
      //  type C0 = C
      //  type Env = Unit
      //  val term = x
      //  val reinit = (_:Env) => ir"()"
      //  def make[R](code: (Env => IR[R,C])): IR[R,C] = code(())
      //})
    }
  }
  def unapply[T:IRType,C](x: IR[T,C]): Some[NeatClosure[T,C]] = {
    Some(new NeatClosure[T,C] {
      def make[R:IRType](term_reset: (Code[T], Code[() => Unit]) => Code[R]): Option[Code[R]] = 
        rec(x, ir"() => ()", term_reset)
    })
  }
}

abstract class NeatClosure[T,C] {
  def make[R:IRType](term_reset: (Code[T], Code[() => Unit]) => Code[R]): Option[Code[R]]
}

*/
