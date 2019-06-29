// Copyright 2019 EPFL DATA Lab (data.epfl.ch)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package squid
package haskellopt

import squid.utils._
import squid.ir.graph3._

/* TODO this could be implemented in a much cleaner and clearer way */
abstract class HaskellGraphInterpreter extends HaskellGraph {
  
  object HaskellInterpDebug extends PublicTraceDebug
  import HaskellInterpDebug.{debug => Idebug}
  
  type Top = AnyRef
  
  def interpretHaskell(r: Rep): Top = {
    Idebug(s"Interpreting...")
    
    def L[A](x: => A) = Lazy.mk(x.asInstanceOf[AnyRef & A], computeWhenShow = false)
    def get[A](x: Any): A = x match {
      case l: Lazy[_] => get(l.value)
      //case x: (() => _) => get(x()) // Note: this raises a very strange Scalac error
      case x: (() => Any) => get(x()) // Not sure why this was needed
      case x => x.asInstanceOf[A]
    }
    def conv(b: Bool): Top = Data(if (b) "True" else "False", Nil)
    
    type CBNFun = Lazy[Top] => Top
    def CBNFun[A](f: (=> A) => Any): Lazy[CBNFun] = L(new Function1[Lazy[Top], Top]{
      override def apply(x: Lazy[Top]) =  L(f(get[A](x)))
    })
    case class IO(f: CBNFun)
    type I[A] = Unit => A
    case class Data(ctor: String, args: List[Top])
    
    type CaseCtx = Map[(Rep,String), List[Lazy[Top]]]
    
    def rec(r: Rep)(implicit cctx: CCtx, vctx: Map[Val,Lazy[Top]], enclosingCases: CaseCtx): Lazy[Top] = HaskellInterpDebug.nestDbg(r.node match {
      case Box(ctrl,res) => rec(res)(withCtrl_!(ctrl),vctx,enclosingCases)
      case Branch(ctrl,cid,thn,els) =>
        if (hasCid_!(ctrl,cid)) rec(thn) else rec(els)
      case cn@ConcreteNode(d) => d match {
        case ByName(b) => die
        case Abs(p,b) =>
          L((a: Lazy[Top]) => rec(b)(
            cctx push DummyCallId, // we should take the 'else' of the next branches since we are traversing a lambda!
            vctx+(p->a),
            enclosingCases,
          ).value)
        case v: Val => vctx(v)
        case Ascribe(r, tp) => rec(r)
        case StaticModule(modName) =>
          Idebug(s"Module $modName")
          modName match {
            case "GHC.TopHandler.runMainIO" => CBNFun[Top](get[Top](_))
            case "GHC.Base.id" => CBNFun[Top](e => e)
            case "(GHC.Base.>>)" => CBNFun[Top](e => {get(e);id})
            case "(GHC.Classes.==)" => CBNFun((x:Top) => CBNFun((y:Top) => {
              val lhs = get[Top](x)
              val rhs = get[Top](y)
              Idebug(s"$lhs==$rhs ${lhs==rhs}") thenReturn (lhs == rhs |> conv)
            }))
            case "GHC.List.take" => CBNFun[Int](n => CBNFun[Stream[Top]](xs => xs.take(n)))
            case "GHC.List.length" => CBNFun[Stream[Top]](x => x.length)
            case "GHC.List.head" => CBNFun[Stream[Top]](x => x.head)
            case "GHC.Types.I#" => CBNFun[Top](id)
            case "(GHC.Num.+)" => CBNFun[Int](x => CBNFun[Int](x + _))
            case "(GHC.Num.*)" => CBNFun[Int](x => CBNFun[Int](x * _))
            case "(GHC.Num.-)" => CBNFun[Int](x => CBNFun[Int](x - _))
            case "GHC.Num.fromInteger" => CBNFun[Top](id)
            case "(GHC.Real.^)" => CBNFun[Int](x => CBNFun[Int](y => Math.pow(x,y).toInt))
            case "GHC.Classes.not" => CBNFun[Data](x => conv(x.ctor === "False"))
            case "(GHC.Classes.>)" => CBNFun[Int](x => CBNFun[Int](y => conv(x > y)))
            case "(GHC.Classes.<)" => CBNFun[Int](x => CBNFun[Int](y => conv(x < y)))
            case "(GHC.Classes.&&)" => CBNFun[Data](x => CBNFun[Data](y => Data((x.ctor,y.ctor) match {
              case ("True", "True") => "True"
              case ("True", "False") | ("False", "True") | ("False", "False") => "False"
            }, Nil)))
            case "(:)" => CBNFun[Top](x => CBNFun[Stream[Top]](xs => x #:: xs))
            case "[]" => L(Stream.empty)
            case "(,)" => CBNFun[Top](x => CBNFun[Top](y => Data("(,)", x :: y :: Nil)))
            case "()" => L(Data("()", Nil))
            case "(GHC.Base.$)" => CBNFun[Top](id)
            case "GHC.Types.True" => L(conv(true))
            case "GHC.Types.False" => L(conv(false))
            case "GHC.Maybe.Just" => CBNFun[Top](x => Data("Just", x::Nil))
            case "GHC.Maybe.Nothing" => L(Data("Nothing", Nil))
            case "System.IO.print" => CBNFun[Top](x => Idebug(s"System.IO.print(${x match {
              case s: Stream[_] => s.toList
              case _ => x
            }})"))
            case "System.Exit.exitFailure" => L(scala.util.Failure(new Exception))
            case "System.Exit.exitSuccess" => L(scala.util.Success(()))
          }
        case Apply(f, arg) => L {
            val fe = get[Top => Top](rec(f))
            fe(rec(arg))
          }
        case MethodApp(scrut, CaseMtd, Nil, ArgsVarargs(Args(), Args(alts @ _*))::Nil, _) =>
          val Data(c,es) = get[Data](rec(scrut))
          alts.iterator.map {
            case Rep(ConcreteNode(MethodApp(_,Tuple2.ApplySymbol,Nil,Args(lhs,rhs)::Nil,_))) =>
              lhs.node |>! { case ConcreteNode(StaticModule(con)) => (con,rhs) }
          }.collectFirst{ case (`c`, r2) => rec(r2) }.get
        case MethodApp(scrut,GetMtd,Nil,Args(con,idx)::Nil,_) =>
          val Data(c,es) = get[Data](rec(scrut))
          val Rep(ConcreteNode(StaticModule(conStr))) = con
          require(conStr === c)
          val Rep(ConcreteNode(Constant(i:Int))) = idx
          val v = es(i)
          L(v)
        case c: ConstantLike => L(c.value match {
          case b: Bool => conv(b)
          case v => v.asInstanceOf[Top]
        })
      }
    })
    
    val res = rec(r)(CCtx.empty,Map.empty,Map.empty)
    get(res)
  }
  
}
