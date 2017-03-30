package squid
package anf.analysis

import squid.ir.SimpleANF
import squid.lang.InspectableBase
import utils._

/**
  * Created by lptk on 10/02/17.
  */
trait BlockHelpers extends SimpleANF {
  
  trait Block[T,C] extends IR[T,C] {
    type C0 <: C
    val stmts: List[IR[_,C0]]
    val res: IR[T,C0]
    
    // TODO methods for hygienically and safely performing statements manipulations, such as filtering statements based
    // on whether they depend on some context, etc.
    //def splitDependent[D:Ctx]: (List[_,D],List[_,C0]) -- or ((Block[Unit,C] => Block[Unit,C]) => Block[T,D]) 
  }
  
  object Block {
    def unapply[T,C](q: IR[T,C]): Option[Block[T,C]] = unapplyBlock[T,C](q)
  }
  def unapplyBlock[T,C](q: IR[T,C]): Option[Block[T,C]] = {
    val bl = q.rep.asBlock
    // Q: is it okay to extract single expressions with this extractor?
    /*if (bl._1.isEmpty) None
    else*/ Some(new WrappingIR[T,C](q.rep) with Block[T,C]{
      val stmts: List[IR[_,C0]] = bl._1 map (_.fold(_._2, identity) |> IR.apply[T,C0])
      val res: IR[T,C0] = IR(bl._2)
    })
  }
  
  object WithResult {
    def unapply[T,C](b: Block[T,C]): Some[Block[T,C] -> IR[T,b.C0]] = Some(b->b.res)
  }
  
  
  /** A thing of beauty: no unsafe casts, @unchecked patterns or low-level hacks; just plain typeticool Squid quasiquotes. */
  object Closure {
    import Predef.QuasiContext
    import Predef.implicitType
    import squid.anf.analysis.BlockHelpers.placeHolder
    
    private var uid = 0
    
    //def unapply[A,C](x: IR[A,C]): Option[Closure[A,C with AnyRef]] = {
    def unapply[A:IRType,C](term: IR[A,C]): Option[Closure[A, C]] = {
    // ^ `C with AnyRef` necessary because we do `C{x:xt} – {x} == C{} == C with AnyRef`...
      
      //println("CLOSREC: "+x.rep)
      
      term match {
          
        case ir"(x: $xt) => ($body:$bt)" => // Note: here `bt` is NOT `A` -- in fact `A =:= (xt => bt)`
          Some(new ClosureImpl[A,C,Unit](ir"()", ir"(_:Unit) => $term"))
          
        case ir"val ClosureVar: $xt = $v; $body: A" =>
          
          // Cannot do the following, as it would confuse the matched bindings `x` -- TODO why does it not make a compile error? (probably because of unchecked patmat below)
          //val rec = unapply(body)
          
          // TODO a fixpoint combinator to help recursive binder extrusion
          
          // Note: the trick will become unneeded once proper context polymorphism hygiene is implemented (see `doc/internal/design/future/Hygienic Context Polymorphism.md`)
          
          val curid = uid oh_and (uid += 1)
          val closedBody = body subs 'ClosureVar -> ir"placeHolder[$xt](${Const(curid)})"
          val rec = unapply(closedBody)
          def reopen[T,C](q: IR[T,C]): IR[T,C{val ClosureVar:xt.Typ}] = q rewrite {
            //case dbg_ir"squid.lib.placeHolder[$$xt](${Const(Curid)})" => ir"x?:$xt"  // FIXME `Curid` pat-mat...
            case ir"placeHolder[$$xt](${Const(id)})" if id == curid => ir"ClosureVar?:$xt"
          }
          
          rec match {
              
            case Some(cls) if cls.env =~= ir"()" =>
              import cls.{typA => _, _}
              Some(new ClosureImpl(v, ir"(ClosureVar:$xt) => ${reopen(fun)}(${reopen(env)})")) // type: ClosureImpl[A,C with AnyRef,xt.Typ]
              
            case Some(cls) =>
              import cls.{typA => _, _}
              Some(new ClosureImpl(  // type: ClosureImpl[A, C with AnyRef, (xt.Typ,cls.E)]
                ir"val ClosureVar = $v; (ClosureVar,${reopen(env)})",
                ir"{ (env:($xt,${typE})) => val ClosureVar = env._1; ${reopen(fun)}(env._2) }"))
              
            case None => None
          }
        case _ =>
          None
      }
    }
    
  }
  abstract class Closure[A,-C] {
    type E
    val env: IR[E,C]
    val fun: IR[E => A,C]
    implicit val typA: IRType[A]
    implicit val typE: IRType[E]
  }
  private case class ClosureImpl[A,C,TE](env: IR[TE,C], fun: IR[TE => A,C])(implicit val typA: IRType[A], val typE: IRType[TE])
    extends Closure[A,C] { type E = TE }
  
  
}
object BlockHelpers {
  import squid.lib.transparencyPropagating
  /** Making it `private` ensures that an end program which would still (erroneously) contain it would not be able to compile.
    * Note: using an implicit-based scheme could even give a more helpful compile-time error message. */
  @transparencyPropagating private def placeHolder[T](id: Int): T = squid.lib.placeHolder("Closure")
}

