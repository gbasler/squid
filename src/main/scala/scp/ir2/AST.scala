package scp
package ir2

import scala.collection.mutable
import lang2._
import utils.meta.RuntimeUniverseHelpers
import RuntimeUniverseHelpers.sru
import utils._

import scala.reflect.runtime.universe.TypeTag


trait AST extends Base with ScalaTyping with RuntimeSymbols { self: IntermediateBase =>
  
  
  def rep(dfn: Def): Rep 
  
  sealed trait Def { // Q: why not make it a class with typ as param?
    val typ: TypeRep
    def isPure = true
  }
  
  def readVal(v: BoundVal): Rep = rep(v)
  
  def const[A: TypeTag](value: A): Rep = rep(Const(value))
  def bindVal(name: String, typ: TypeRep) = BoundVal(name)(typ)
  def freshBoundVal(typ: TypeRep) = rep(BoundVal("val$"+varCount)(typ) oh_and (varCount += 1))
  private var varCount = 0
  
  def lambda(params: List[BoundVal], body: => Rep): Rep = rep({
    if (params.size == 1) Abs(params.head, body)
    else ??? // TODO
  })
  
  override def ascribe(self: Rep, typ: TypeRep): Rep =
    rep(Ascribe(self, typ))
  
  def newObject(tp: TypeRep): Rep = rep(NewObject(tp))
  def moduleObject(fullName: String, isPackage: Boolean): Rep = rep({
    ModuleObject(fullName: String, isPackage)
  })
  def methodApp(self: Rep, mtd: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep =
    rep(MethodApp(self, mtd, targs, argss, tp)) //and println
  
  def byName(arg: => Rep): Rep =
    lambda(bindVal("$BYNAME$", uninterpretedType[Unit]) :: Nil, arg) // FIXME proper impl
  
  // TODO
  // We encode thunks (by-name parameters) as functions from some dummy 'ThunkParam' to the result
  //def byName(arg: => Rep): Rep = dsl"(_: lib.ThunkParam) => ${Quote[A](arg)}".rep
  //def byName(arg: => Rep): Rep = ??? //lambda(Seq(freshBoundVal(typeRepOf[lib.ThunkParam])), arg)
  
  def recordGet(self: Rep, name: String, typ: TypeRep) = RecordGet(self, name, typ)
  
  
  /* TODO
  
  def hole[A: TypeEv](name: String) = Hole[A](name)
  def splicedHole[A: TypeEv](name: String): Rep = SplicedHole(name)
  
  /**
    * In xtion, represents an extraction hole
    * In ction, represents a free variable
    */
  case class Hole[+A: TypeEv](name: String) extends Rep {
    val typ = typeEv[A].rep
    override def isPure: Bool = false
  }
  case class SplicedHole[+A: TypeEv](name: String) extends Rep {
    val typ = typeEv[A].rep
    override def isPure: Bool = false
  }
  */
  
  
  
  /* --- --- --- NODES --- --- --- */
  
  case class BoundVal(name: String)(val typ: TypeRep) extends Def {
    //def toHole = Hole(name)(TypeEv(typ))
    override def equals(that: Any) = that match { case that: AnyRef => this eq that  case _ => false }
    //override def hashCode(): Int = name.hashCode // should be inherited
  }
  
  
  case class Const[A: TypeTag](value: A) extends Def {
    lazy val typ = uninterpretedType(sru.typeTag[A]) // FIXME //typeEv[A].rep
  }
  case class Abs(param: BoundVal, body: Rep) extends Def {
    def ptyp = param.typ
    //val typ = ??? //body.typ//FIXME funType(ptyp, body.typ)
    val typ = repType(body) //FIXME funType(ptyp, body.typ)
    
    //def fun(r: Rep) = transformPartial(body) { case `param` => r }
    
    //def inline(arg: Rep): Rep = fun(arg) //body withSymbol (param -> arg)
    
    override def toString = s"Abs($param, $body)"
  }
  
  case class Ascribe(self: Rep, typ: TypeRep) extends Def
  
  case class NewObject(typ: TypeRep) extends Def
  
  //case class ModuleObject(fullName: String, typ: TypeRep) extends Def
  case class ModuleObject(fullName: String, isPackage: Boolean) extends Def {
    //println(s"Loading package $fullName")
    val m = sru.runtimeMirror(getClass.getClassLoader)
    val modSym = if (isPackage) m.staticPackage(fullName) else m.staticModule(fullName)
    ensureDefined(s"module $fullName", modSym)
    val typ: TypeRep = uninterpretedType(RuntimeUniverseHelpers.mkTag(modSym.typeSignature))
  }
  
  case class MethodApp(self: Rep, sym: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], typ: TypeRep) extends Def {
    override def isPure: Bool = false
    override def toString: String =
      s"$self.${sym.name}<${sym.typeSignature}>[${targs mkString ","}]${argss mkString ""}"
      //s"$self.${sym.name}[${targs mkString ","}]${argss mkString ""}"
  }
  
  //case class Record(fields: List[(String, Rep)]) extends Def { // TODO
  //  val typ = RecordType
  //}
  case class RecordGet(self: Rep, name: String, typ: TypeRep) extends Def
  
  
  
  def getClassName(cls: sru.ClassSymbol) = RuntimeUniverseHelpers.srum.runtimeClass(cls).getName
  
  /**
    * FIXME handle encoding of multi-param lambdas
    */
  trait Reinterpreter {
    val newBase: Base
    def apply(r: Rep): newBase.Rep
    
    protected val bound = mutable.Map[BoundVal, newBase.BoundVal]()
    
    protected def apply(d: Def): newBase.Rep = d match {
      case cnst @ Const(v) => newBase.const(v)
      case Abs(bv, body) if bv.name == "$BYNAME$" => newBase.byName(apply(body))
      case Abs(bv, body) => newBase.lambda({ recv(bv)::Nil }, apply(body))
      case MethodApp(self, mtd, targs, argss, tp) =>
        val typ = newBase.loadTypSymbol(getClassName(mtd.owner.asClass))
        val alts = mtd.owner.typeSignature.member(mtd.name).alternatives
        val newMtd = newBase.loadMtdSymbol(typ, mtd.name.toString, if (alts.isEmpty) None else Some(alts.indexOf(mtd)), mtd.isStatic)
        newBase.methodApp(
          apply(self),
          newMtd,
          targs map (t => rect(t)),
          argss map (_.map(newBase)(a => apply(a))),
          rect(tp))
      case ModuleObject(fullName, isPackage) => newBase.moduleObject(fullName, isPackage)
      case bv @ BoundVal(name) => newBase.readVal(bound(bv))
      case Ascribe(r,t) => newBase.ascribe(apply(r), rect(t))
    }
    protected def recv(bv: BoundVal) = newBase.bindVal(bv.name, rect(bv.typ)) and (bound += bv -> _)
    def rect(r: TypeRep): newBase.TypeRep = r match {
      case TypeApp(self, typ, targs) => newBase.typeApp(apply(self), newBase.loadTypSymbol(getClassName(typ.asClass)), // TODO B/E?
        targs map (t => rect(t)))
      case UninterpretedType(tag) => newBase.uninterpretedType(tag)
    }
    
  }
  object Reinterpreter {
    def apply(NewBase: Base)(app: (Rep, Def => NewBase.Rep) => NewBase.Rep) =
      new Reinterpreter { val newBase: NewBase.type = NewBase; def apply(r: Rep) = app(r, apply) }
  }
  
  
  
  
  
}
























