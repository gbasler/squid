package graph4

import squid.utils._

abstract class GraphInterpreter extends GraphRewriting { self: GraphIR =>
  
  class Interpreter extends PublicTraceDebug {
    
    sealed abstract class Value {
      def app(arg: Thunk): Thunk = lastWords(s"not a known function: $this")
      def int: Int = asInstanceOf[Const].c.asInstanceOf[IntLit].value
      def bool: Bool = {
        val const = asInstanceOf[Const]
        val isTrue = const.c === ModuleRef("GHC.Types","True")
        assert(isTrue || const.c === ModuleRef("GHC.Types","False"))
        isTrue
      }
      def ctor: Ctor = this match {
        case ctor: Ctor => ctor
        case Const(mod: ModuleRef) if mod.isCtor =>
          // This case is for nullary constructors like GHC.Types.True;
          // it has false positives as it will return module definitions that are not constructors, which in principle should crash.
          Ctor(mod.defName, Nil)
        case _ => lastWords(s"Not a data constructor: $this")
      }
      def nf: this.type = {
        this match {
          case ctor: Ctor => ctor.fields.foreach(_.value.nf)
          case _: Const | _: Fun =>
        }
        this
      }
    }
    // haskellStr is used when we try to recover a Haskell term from a Value, in the TestHarness
    case class Fun(f: Thunk => Thunk, haskellStr: Opt[Str] = None) extends Value {
      override def app(arg: Thunk): Thunk = f(arg)
      override def toString = s"<fun>"
    }
    private object KnownUnaryCtor {
      val emptyStrSet = Set.empty[Str]
      val known = Map(
        "Data.Either" -> Set("Left", "Right"),
        "GHC.Maybe" -> Set("Just"),
      ).withDefaultValue(emptyStrSet)
      def unapply(arg: ModuleRef): Opt[Str] =
        arg.defName optionIf known(arg.modName)(arg.defName)
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
          case ModuleRef("GHC.Real","mod") => intBinOp(_ % _)
          case ModuleRef("GHC.Real","^") => intBinOp(scala.math.pow(_, _).toInt)
          case ModuleRef("GHC.Classes",">") => Fun(rhs => Bool(arg.value.int > rhs.value.int))
          case ModuleRef("GHC.Types","I#") => arg
          case ModuleRef("GHC.List","take") => Fun(rhs =>
            // I regret thinking it would be a good idea to implement functions like this...
            if (arg.value === Const(IntLit(true,0))) Ctor("[]", Nil)
            else rhs.value |>! {
              case Ctor(":", x :: xs :: Nil) => Ctor(":", x :: app(
                Const(ModuleRef("GHC.Num","-")).app(arg).value.app(Const(IntLit(true,1)))
              ).value.app(xs) :: Nil)
              case nil @ Ctor("[]", Nil) => nil
            })
          case ModuleRef("GHC.Base","++") => Fun(rhs => arg.value |>! {
              case Ctor(":", x :: xs :: Nil) =>
                Ctor(":", x :: Thunk(app(xs).value.app(rhs).value) :: Nil)
              case Ctor("[]", Nil) => rhs
            })
          case ModuleRef("GHC.Classes","&&") => Fun(rhs => Bool(arg.value.bool && rhs.value.bool))
          case ModuleRef("GHC.Classes","==") => Fun(rhs => Bool(arg.value.int === rhs.value.int))
          case ModuleRef("GHC.Classes","/=") => Fun(rhs => Bool(arg.value.int =/= rhs.value.int))
          case ModuleRef("GHC.Base","$") => arg
          case ModuleRef("GHC.Types",":") => Fun(rhs => Ctor(":", arg :: rhs :: Nil))
          case ModuleRef("GHC.Tuple","(,)") => Fun(rhs => Ctor("(,)", arg :: rhs :: Nil))
          case KnownUnaryCtor(nme) => Ctor(nme, arg :: Nil)
          case _ => super.app(arg)
        }
      }
      override def toString = c match {
        case ModuleRef(modName, defName) => s"$modName.$defName" // avoids hiding of known modules
        case _ => c.toString
      }
    }
    case class Ctor(name: String, fields: List[Thunk]) extends Value {
      override def toString = s"$name${fields.map("("+_.valueIfComputed.getOrElse("...")+")").mkString}"
    }
    type Thunk = Lazy[Value]
    implicit def Thunk(v: => Value): Thunk =
      Lazy.mk(v, computeWhenShow = false)
      //Lazy.mk(v, computeWhenShow = true)
    
    def Int(n: Int): Value = Const(IntLit(true,n))
    
    def Bool(b: Bool): Value =
      //Ctor(if (b) "True" else "False", Nil)
      // ^ For things like `true`, we don't use Ctor("True", Nil) because they will actually be represented as ModuleRef-s in the graph
      Const(ModuleRef("GHC.Types", if (b) "True" else "False"))
    
    def apply(r: Ref): Thunk = rec(r)(Id, Map.empty)
    
    // Note: vctx used to be a Map[Var, Thunk],
    //       but this would confuse different recursive instances of the same variables;
    //       in fact, we need to keep track of the Instr context of the variable to disambiguate.
    //       It's not actually necessary to keep the variable as a key, but I do it for easier debugging.
    def rec(r: Ref)(implicit ictx: Instr, vctx: Map[(Var,Instr), Thunk]): Thunk = Thunk {
      //debug(s"Eval [$ictx] $r {${vctx.mkString(", ")}}")
      debug(s"Eval [$ictx] ${r.showDef}\n${Debug.GREY}{${vctx.mkString(", ")}}${Console.RESET}")
      val res = nestDbg { r.node match {
        case Control(i, b) => rec(b)(ictx `;` i, vctx).value
        case Branch(c, t, e) =>
          if (Condition.test_!(c, ictx)) rec(t).value else rec(e).value
        case c: ConstantNode => Const(c)
        case App(f,a) =>
          rec(f).value.app(rec(a)).value
        case l @ Lam(pr, b) =>
          val newCtx = ictx push DummyCallId
          Fun(x => rec(b)(newCtx,
            //assert(!vctx.contains((l.param,newCtx)),(newCtx,l.param,vctx)) thenReturn
            // ^ Nope, since sometimes we pop all the way to the top and then shadow...
            vctx + ((l.param,newCtx) -> x)))
        case v: Var =>
          vctx.getOrElse((v,ictx), lastWords(s"[$ictx]$v not in ${vctx}")).value
        case Case(scrut, arms) =>
          val ctor = rec(scrut).value.ctor
          val armBody = arms.collectFirst {
            case (ctorName, ari, body) if ctorName === ctor.name || ctorName === "_" => body
          }.getOrElse(lastWords(s"Value $ctor does not match any of: ${arms.map(_._1).mkString(", ")}"))
          rec(armBody).value
        case CtorField(scrut, ctorName, arity, idx) =>
          val ctor = rec(scrut).value.ctor
          assert(ctor.name === ctorName, (ctor, ctorName))
          assert(ctor.fields.size === arity, (ctor, arity))
          ctor.fields(idx).value
      }}
      debug(s"= "+res)
      res
    }
    
    /** Convert a Scala value into a value understood by the interpreter */
    def lift(value: Any): Value = value match {
      case n: Int => Int(n)
      case 'S => Fun(x => Int(x.value.int + 1), haskellStr = Some("(\\x->x+1)"))
      case b: Bool => Bool(b)
      case None => Const(ModuleRef("GHC.Maybe", "Nothing"))
      case Some(v) => Ctor("Just", lift(v) :: Nil)
      case ls: List[_] =>
        ls.foldRight[Value](Ctor("[]",Nil)) { case (x, acc) => Ctor(":", Thunk(lift(x)) :: Thunk(acc) :: Nil) }
      case _ => lastWords(s"don't know how to lift $value")
    }
    
  }
  
}
