package squid
package ir.fastanf

import utils._
import lang.{InspectableBase, Base}
import squid.ir.CurryEncoding
import squid.lang.ScalaCore

class FastANF extends InspectableBase with CurryEncoding with ScalaCore {
  private[this] implicit val base = this
  
  
  // * --- * --- * --- *  Basic Definitions * --- * --- * --- *
  
  type Rep = ir.fastanf.Rep
  type TypeRep = ir.fastanf.TypeRep
  type BoundVal = ir.fastanf.Symbol
  type TypSymbol = TypeSymbol
  type MtdSymbol = MethodSymbol
  
  
  // * --- * --- * --- *  Reification  * --- * --- * --- *
  
  var scopes: List[ReificationContext] = Nil
  
  @inline final def wrap(r: => Rep, inXtor: Bool): Rep = {
    val scp = new ReificationContext(inXtor)
    scopes ::= scp
    try scp.finalize(r)
    finally scopes = scopes.tail
  }
  @inline final def wrapNest(r: => Rep): Rep = {
    wrap(r, currentScope.inExtractor)
  }
  override final def wrapConstruct(r: => Rep): Rep = wrap(super.wrapConstruct(r), false)
  override final def wrapExtract(r: => Rep): Rep = wrap(super.wrapExtract(r), true)
  
  @inline final def currentScope = scopes.head
  
  // TODO make squid.lang.Base and QQs agnostic in the argument list impl to avoid this conversion!
  def toArgumentLists(argss: List[ArgList]): ArgumentLists = {
    argss match {
      case Nil => NoArgumentLists
      case Args(a) :: Nil => a
    }
  }
  
  
  // * --- * --- * --- *  Implementations of `Base` methods  * --- * --- * --- *
  
  def bindVal(name: String, typ: TypeRep, annots: List[Annot]): BoundVal = new UnboundSymbol(name,typ)
  def readVal(bv: BoundVal): Rep = bv
  def const(value: Any): Rep = Constant(value)
  
  // Note: method `lambda(params: List[BoundVal], body: => Rep): Rep` is implemented by CurryEncoding
  def abs(param: BoundVal, mkBody: => Rep): Rep = {
    val body = wrapNest(mkBody)
    new Lambda(param.name, param, param.typ, body).alsoApply(param rebind _) |> letbind
  }
  def funType(paramTyp: TypeRep, ret: TypeRep): TypeRep = lambdaType(paramTyp :: Nil, ret)
  def lambdaType(paramTyps: List[TypeRep], ret: TypeRep): TypeRep = DummyTypeRep
  
  def staticModule(fullName: String): Rep = StaticModule(fullName)
  def module(prefix: Rep, name: String, typ: TypeRep): Rep = unsupported
  def newObject(tp: TypeRep): Rep = unsupported
  def methodApp(self: Rep, mtd: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep = {
    MethodApp(self, mtd, targs, argss |> toArgumentLists, tp) |> letbind
  }
  def byName(mkArg: => Rep): Rep = wrapNest(mkArg)
  
  def letbind(d: Def): Rep = currentScope += d
  
  override def ascribe(self: Rep, typ: TypeRep): Rep = if (self.typ =:= typ) self else self match {
    case Ascribe(trueSelf, _) => Ascribe(trueSelf, typ) // Hopefully Scala's subtyping is transitive!
    case _ => Ascribe(self, typ)
  }
  
  def loadMtdSymbol(typ: TypSymbol, symName: String, index: Option[Int] = None, static: Boolean = false): MtdSymbol = new MethodSymbol(symName) // TODO
  
  object Const extends ConstAPI {
    def unapply[T: IRType](ir: IR[T,_]): Option[T] = ir.rep match {
      case cst @ Constant(v) if typLeq(cst.typ, irTypeOf[T].rep) => Some(v.asInstanceOf[T])
      case _ => None
    }
  }
  
  def repEq(a: Rep, b: Rep): Boolean = a === b // FIXME impl term equivalence
  
  
  // * --- * --- * --- *  Implementations of `IntermediateBase` methods  * --- * --- * --- *
  
  def nullValue[T: IRType]: IR[T,{}] = unsupported
  def reinterpret(r: Rep, newBase: Base)(extrudedHandle: BoundVal => newBase.Rep): newBase.Rep = unsupported
  def repType(r: Rep): TypeRep = r.typ
  def boundValType(bv: BoundVal) = bv.typ
  
  
  // * --- * --- * --- *  Implementations of `InspectableBase` methods  * --- * --- * --- *
  
  def extractType(xtor: TypeRep, xtee: TypeRep,va: squid.ir.Variance): Option[Extract] = unsupported
  def bottomUp(r: Rep)(f: Rep => Rep): Rep = transformRep(r)(identity, f)
  def topDown(r: Rep)(f: Rep => Rep): Rep = transformRep(r)(f)
  def transformRep(r: Rep)(pre: Rep => Rep, post: Rep => Rep = identity): Rep = unsupported
  protected def extract(xtor: Rep, xtee: Rep): Option[Extract] = unsupported
  protected def spliceExtract(xtor: Rep, args: Args): Option[Extract] = unsupported
  
  
  // * --- * --- * --- *  Implementations of `QuasiBase` methods  * --- * --- * --- *
  
  def hole(name: String, typ: TypeRep) = unsupported
  def splicedHole(name: String, typ: TypeRep): Rep = unsupported
  def typeHole(name: String): TypeRep = unsupported
  def hopHole(name: String, typ: TypeRep, yes: List[List[BoundVal]], no: List[BoundVal]) = unsupported
  
  def substitute(r: => Rep, defs: Map[String, Rep]): Rep = unsupported
  
  
  // * --- * --- * --- *  Implementations of `TypingBase` methods  * --- * --- * --- *
  
  import scala.reflect.runtime.universe.TypeTag // TODO do without this
  
  def uninterpretedType[A: TypeTag]: TypeRep = unsupported
  def typeApp(self: TypeRep, typ: TypSymbol, targs: List[TypeRep]): TypeRep = unsupported
  def staticTypeApp(typ: TypSymbol, targs: List[TypeRep]): TypeRep = DummyTypeRep //unsupported 
  def recordType(fields: List[(String, TypeRep)]): TypeRep = unsupported
  def constType(value: Any, underlying: TypeRep): TypeRep = unsupported
  
  def typLeq(a: TypeRep, b: TypeRep): Boolean = unsupported
  
  def loadTypSymbol(fullName: String): TypSymbol = new TypeSymbol // TODO
  
  
  // * --- * --- * --- *  Misc  * --- * --- * --- *
  
  def unsupported = lastWords("This part of the IR is not yet implemented/supported")
  
  override def showRep(r: Rep) = r.toString // TODO impl pretty-printing
  
  val FunApp = `scala.Function1`.`method apply`.value
  
}

class ReificationContext(val inExtractor: Bool) { reif =>
  var firstLet: FlatOpt[LetBinding] = Non
  var curLet: FlatOpt[LetBinding] = Non
  def += (lb: LetBinding): Unit = {
    curLet match {
      case Non => firstLet = lb.som
      case Som(cl) => cl.body = lb
    }
    curLet = lb.som
  }
  def += (d: Def): Symbol = new Symbol {
    protected var _parent: SymbolParent = new LetBinding("tmp", this, d, this) alsoApply (reif += _)
  }
  def finalize(r: Rep) = {
    firstLet match {
      case Non => 
        assert(curLet.isEmpty)
        r
      case Som(fl) =>
        curLet.get.body = r
        fl
    }
  }
}

