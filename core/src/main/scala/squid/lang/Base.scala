package squid.lang

import squid.quasi

trait Base extends TypingBase with quasi.QuasiBase {
  
  type Rep
  type BoundVal
  
  type Annot = (TypeRep, List[ArgList])
  
  def bindVal(name: String, typ: TypeRep, annots: List[Annot]): BoundVal
  
  def readVal(v: BoundVal): Rep
  def const(value: Any): Rep // Note: not taking a sru.Constant as argument, because `const` also receives things like () -- Unit "literal"
  def lambda(params: List[BoundVal], body: => Rep): Rep
  
  def staticModule(fullName: String): Rep
  def module(prefix: Rep, name: String, typ: TypeRep): Rep
  def newObject(tp: TypeRep): Rep
  def methodApp(self: Rep, mtd: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep
  
  def byName(arg: => Rep): Rep
  
  
  type MtdSymbol
  
  /** Parameter `static` shpuld be true only for truly static methods (in the Java sense)
    * Note: index should be None when the symbol is not overloaded, to allow for more efficient caching */
  def loadMtdSymbol(typ: TypSymbol, symName: String, index: Option[Int] = None, static: Boolean = false): MtdSymbol
  
  
  
  val Const: ConstAPI
  trait ConstAPI {
    def apply[T: IRType](v: T): IR[T,{}] = `internal IR`(const(v))
  }
  
  
  def repEq(a: Rep, b: Rep): Boolean
  
  
  
  implicit class RepOps(private val self: Rep) {
    def =~= (that: Rep) = repEq(self, that)
    
    /** Useful for inspection with quasiquotes; the Nothing context prevents it from being used as is (which would be unsafe). */
    def asIR = `internal IR`[Any,Nothing](self)
    
    def show = showRep(self)
  }
  
  
  def showRep(r: Rep) = r.toString
  
  
  
  // Helpers:
  
  /** Just a shortcut for methodApp */
  final def mapp(self: Rep, mtd: MtdSymbol, tp: TypeRep)(targs: TypeRep*)(argss: ArgList*): Rep =
    methodApp(self, mtd, targs.toList, argss.toList, tp)
  
  
  protected lazy val Function1ApplySymbol = loadMtdSymbol(loadTypSymbol("scala.Function1"), "apply")
  def app(fun: Rep, arg: Rep)(retTp: TypeRep): Rep = methodApp(fun, Function1ApplySymbol, Nil, Args(arg)::Nil, retTp)
  def letin(bound: BoundVal, value: Rep, body: => Rep, bodyType: TypeRep): Rep = {
    app(lambda(bound::Nil, body), value)(bodyType)
  }
  
  // TODO move all these into a Builtin object and remove `lazy`
  protected lazy val squidLib = staticModule("squid.lib.package")
  protected lazy val squidLibSym = loadTypSymbol("squid.lib.package$")
  protected lazy val BooleanType = staticTypeApp(loadTypSymbol("scala.Boolean"),Nil)
  protected lazy val BooleanAndSymbol = loadMtdSymbol(squidLibSym, "And")
  protected lazy val BooleanOrSymbol = loadMtdSymbol(squidLibSym, "Or")
  def and(lhs: Rep, rhs: => Rep) = methodApp(squidLib,BooleanAndSymbol,Nil,Args(lhs,rhs)::Nil,BooleanType)
  def or(lhs: Rep, rhs: => Rep) = methodApp(squidLib,BooleanOrSymbol,Nil,Args(lhs,rhs)::Nil,BooleanType)
  
  /** Override to give special meaning */
  def ascribe(self: Rep, typ: TypeRep): Rep = self
  
  
  
  sealed trait ArgList extends Product with Serializable {
    def repsIt: Iterator[Rep] = reps.iterator
    def reps: Seq[Rep]
    //def extract(al: ArgList): Option[Extract] = (this, al) match {
    //  case (a0: Args, a1: Args) => a0 extract a1
    //  case (ArgsVarargs(a0, va0), ArgsVarargs(a1, va1)) => for {
    //    a <- a0 extract a1
    //    va <- va0 extractRelaxed va1
    //    m <- merge(a, va)
    //  } yield m
    //  case (ArgsVarargSpliced(a0, va0), ArgsVarargSpliced(a1, va1)) => for {
    //    a <- a0 extract a1
    //    va <- baseSelf.extract(va0, va1)
    //    m <- merge(a, va)
    //  } yield m
    //  case (ArgsVarargSpliced(a0, va0), ArgsVarargs(a1, vas1)) => for { // case dsl"List($xs*)" can extract dsl"List(1,2,3)"
    //    a <- a0 extract a1
    //    va <- baseSelf.spliceExtract(va0, vas1)
    //    m <- merge(a, va)
    //  } yield m
    //  case _ => None
    //}
    override def toString = show(_ toString)
    def show(rec: Rep => String, forceParens: Boolean = true) = {
      val strs = this match {
        case Args(as @ _*) => as map rec
        case ArgsVarargs(as, vas) => as.reps ++ vas.reps map rec
        case ArgsVarargSpliced(as, va) => as.reps.map(rec) :+ s"${rec(va)}: _*"
      }
      if (forceParens || strs.size != 1) strs mkString ("(",",",")") else strs mkString ","
    }
    def map(b: Base)(f: Rep => b.Rep): b.ArgList
  }
  case class Args(reps: Rep*) extends ArgList {
    def apply(vreps: Rep*) = ArgsVarargs(this, Args(vreps: _*))
    def splice(vrep: Rep) = ArgsVarargSpliced(this, vrep)
    
    //def extract(that: Args): Option[Extract] = {
    //  require(reps.size == that.reps.size)
    //  extractRelaxed(that)
    //}
    //def extractRelaxed(that: Args): Option[Extract] = {
    //  if (reps.size != that.reps.size) return None
    //  val args = (reps zip that.reps) map { case (a,b) => baseSelf.extract(a, b) }
    //  (Some(EmptyExtract) +: args).reduce[Option[Extract]] { // `reduce` on non-empty; cf. `Some(EmptyExtract)`
    //    case (acc, a) => for (acc <- acc; a <- a; m <- merge(acc, a)) yield m }
    //}
    def map(b: Base)(f: Rep => b.Rep): b.Args = b.Args(reps map f: _*)
  }
  object ArgList {
    def unapplySeq(x: ArgList) = x match {
      case Args(as @ _*) => Some(as)
      case ArgsVarargs(as, vas) => Some(as.reps ++ vas.reps)
      case ArgsVarargSpliced(_, _) => None
    }
  }
  case class ArgsVarargs(args: Args, varargs: Args) extends ArgList {
    val reps = args.reps ++ varargs.reps
    def map(b: Base)(f: Rep => b.Rep): b.ArgsVarargs = b.ArgsVarargs(args.map(b)(f), varargs.map(b)(f))
  }
  case class ArgsVarargSpliced(args: Args, vararg: Rep) extends ArgList {
    val reps = args.reps :+ vararg
    def map(b: Base)(f: Rep => b.Rep): b.ArgsVarargSpliced = b.ArgsVarargSpliced(args.map(b)(f), f(vararg))
  }
  
}
