package graph4

import squid.utils._

abstract class GraphInterpreter extends GraphScheduler { self: GraphIR =>
  
  class Interpreter extends PublicTraceDebug {
    
    sealed abstract class Value {
      def app(arg: Thunk): Thunk = lastWords(s"not a known function: $this")
      def int = asInstanceOf[Const].c.asInstanceOf[IntLit].value
    }
    case class Fun(f: Thunk => Thunk) extends Value {
      override def app(arg: Thunk): Thunk = f(arg)
      override def toString = s"<fun>"
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
          case ModuleRef("GHC.List","take") => Fun(rhs =>
            // I regret thinking it would be a good idea to implement functions like this...
            if (arg.value === Const(IntLit(true,0))) Ctor("[]", Nil)
            else rhs.value |>! {
              case Ctor(":", x :: xs :: Nil) => Ctor(":", x :: Const(ModuleRef("GHC.List","take")).app(
                Const(ModuleRef("GHC.Num","-")).app(arg).value.app(Const(IntLit(true,1)))
              ).value.app(xs) :: Nil)
              case nil @ Ctor("[]", Nil) => nil
            })
          case ModuleRef("GHC.Base","$") => arg
          case ModuleRef("GHC.Types",":") => Fun(rhs => Ctor(":", arg :: rhs :: Nil))
          case _ => super.app(arg)
        }
      }
      override def toString = c match {
        case ModuleRef(modName, defName) => s"$modName.$defName" // avoids hiding of known modules
        case _ => c.toString
      }
    }
    case class Ctor(name: String, fields: List[Thunk]) extends Value {
      override def toString = s"$name${fields.map("("+_.value+")").mkString}"
    }
    type Thunk = Lazy[Value]
    implicit def toThunk(v: => Value): Thunk = Lazy(v)
    
    def Int(n: Int): Value = Const(IntLit(true,n))
    
    def apply(r: Ref): Thunk = rec(r)(Id, Map.empty)
    
    def rec(r: Ref)(implicit ictx: Instr, vctx: Map[Var, Thunk]): Thunk = Lazy { nestDbg {
      debug(s"Eval [$ictx] $r {${vctx.mkString(", ")}}")
      r.node match {
        case Control(i, b) => rec(b)(ictx `;` i, vctx).value
        case Branch(c, t, e) =>
          if (Condition.test_!(c, ictx)) rec(t).value else rec(e).value
        case c: ConstantNode => Const(c)
        case App(f,a) =>
          rec(f).value.app(rec(a)).value
        case l @ Lam(pr, b) =>
          Fun(x => rec(b)(ictx push DummyCallId, vctx + (l.param -> x)))
        case v: Var =>
          vctx(v).value
      }
    }}
    
    /** Convert a Scala value into a value understood by the interpreter */
    def lift(value: Any): Value = value match {
      case n: Int => Int(n)
      case 'S => Fun(x => Int(x.value.int + 1))
      case ls: List[_] =>
        ls.foldRight[Value](Ctor("[]",Nil)) { case (x, acc) => Ctor(":", Lazy(lift(x)) :: Lazy(acc) :: Nil) }
      case _ => lastWords(s"don't know how to lift $value")
    }
    
  }
  
}
