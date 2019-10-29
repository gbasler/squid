package graph4

import squid.utils._

abstract class GraphInterpreter extends GraphScheduler { self: GraphIR =>
  
  class Interpreter extends PublicTraceDebug {
    
    sealed abstract class Value {
      def app(arg: Thunk): Thunk
      def int = asInstanceOf[Const].c.asInstanceOf[IntLit].value
    }
    case class Fun(f: Thunk => Thunk) extends Value {
      override def app(arg: Thunk): Thunk = f(arg)
    }
    case class Const(c: ConstantNode) extends Value {
      override def app(arg: Thunk): Thunk = {
        def intBinOp(f: (Int, Int) => Int): Thunk =
          Fun(rhs => Int(f(arg.value.int, rhs.value.int)))
        c match {
          case ModuleRef("GHC.Base","id") => arg
          case ModuleRef("GHC.Num","+") => intBinOp(_ + _)
          case ModuleRef("GHC.Num","-") => intBinOp(_ - _)
          case ModuleRef("GHC.Num","*") => intBinOp(_ * _)
          case ModuleRef("GHC.Real","^") => intBinOp(scala.math.pow(_, _).toInt)
          case ModuleRef("GHC.Types","I#") => arg
          case _ => lastWords(s"not a known function: $this")
        }
      }
    }
    type Thunk = Lazy[Value]
    implicit def toThunk(v: => Value): Thunk = Lazy(v)
    
    def Int(n: Int): Value = Const(IntLit(true,n))
    
    def apply(r: Ref): Thunk = rec(r)(Id, Map.empty)
    
    def rec(r: Ref)(implicit ictx: Instr, vctx: Map[Var, Thunk]): Thunk = {
      r.node match {
        case Control(i, b) => rec(b)(ictx `;` i, vctx)
        case Branch(c, t, e) =>
          if (Condition.test_!(c, ictx)) rec(t) else rec(e)
        case c: ConstantNode => Lazy(Const(c))
        case App(f,a) =>
          rec(f).value.app(rec(a))
        case l @ Lam(pr, b) =>
          Fun(x => rec(b)(ictx push DummyCallId, vctx + (l.param -> x)))
        case v: Var =>
          vctx(v)
      }
    }
    
    /** Convert a Scala value into a value understood by the interpreter */
    def lift(value: Any): Value = value match {
      case n: Int => Int(n)
      case 'S => Fun(x => Int(x.value.int + 1))
      case _ => lastWords(s"don't know how to lift $value")
    }
    
  }
  
}
