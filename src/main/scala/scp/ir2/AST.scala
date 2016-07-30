package scp
package ir2

import scala.collection.mutable
import lang2._
import utils.meta.RuntimeUniverseHelpers
import utils._

import scala.reflect.runtime.{universe => sru}
//import ScalaTyping.{Contravariant, Covariant, Variance}
import scp.quasi.EmbeddingException

import scala.reflect.runtime.universe.TypeTag


class AST extends Base with RuntimeSymbols {
  
  
  
  
  def uninterpretedType[A: TypeTag]: TypeRep =
    UninterpretedType[A](sru.typeTag[A])
  
  def typeApp(self: Rep, typ: TypSymbol, targs: List[TypeRep]): TypeRep = TypeApp(self, typ, targs)
  
  def recordType(fields: List[(String, TypeRep)]): TypeRep = ??? // TODO
  
  sealed trait TypeRep
  case class TypeApp(self: Rep, typ: TypSymbol, targs: List[TypeRep]) extends TypeRep
  case class UninterpretedType[A]
    (val tag: TypeTag[A]) extends TypeRep
    //(implicit val tag: TypeTag[A]) extends TypeRep
  
  
  
  
  
  
  
  
  sealed trait Rep { // Q: why not make it a class with typ as param?
    val typ: TypeRep
    def isPure = true
  }
  
  def readVal(v: BoundVal): Rep = v
  
  def const[A: TypeTag](value: A): Rep = Const(value)
  def bindVal(name: String, typ: TypeRep) = BoundVal(name)(typ)
  def freshBoundVal(typ: TypeRep) = BoundVal("val$"+varCount)(typ) oh_and (varCount += 1)
  private var varCount = 0
  
  def lambda(params: List[BoundVal], body: => Rep): Rep = {
    if (params.size == 1) Abs(params.head, body)
    else ???
  }
  
  //override def ascribe[A: TypeEv](value: Rep): Rep =
  //  Ascribe[A](value)
  
  def newObject(tp: TypeRep): Rep = NewObject(tp)
  def moduleObject(fullName: String, isPackage: Boolean): Rep = {
    ModuleObject(fullName: String, isPackage)
  }
  def methodApp(self: Rep, mtd: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep =
    MethodApp(self, mtd, targs, argss, tp) //and println
  
  def byName(arg: => Rep): Rep =
    lambda(bindVal("$BYNAME$", uninterpretedType[Unit]) :: Nil, arg) // FIXME proper impl
  
  // We encode thunks (by-name parameters) as functions from some dummy 'ThunkParam' to the result
  //def byName(arg: => Rep): Rep = dsl"(_: lib.ThunkParam) => ${Quote[A](arg)}".rep
  //def byName(arg: => Rep): Rep = ??? //lambda(Seq(freshBoundVal(typeRepOf[lib.ThunkParam])), arg)
  
  def recordGet(self: Rep, name: String, typ: TypeRep) = RecordGet(self, name, typ)
  
  /*
  def hole[A: TypeEv](name: String) = Hole[A](name)
  //def hole[A: TypeEv](name: String) = Var(name)(typeRepOf[A])
  
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
  
  case class BoundVal(name: String)(val typ: TypeRep) extends Rep {
    //def toHole = Hole(name)(TypeEv(typ))
    override def equals(that: Any) = that match { case that: AnyRef => this eq that  case _ => false }
    //override def hashCode(): Int = name.hashCode // should be inherited
  }
  
  
  case class Const[A: TypeTag](value: A) extends Rep {
    lazy val typ = uninterpretedType(sru.typeTag[A]) // FIXME //typeEv[A].rep
  }
  case class Abs(param: BoundVal, body: Rep) extends Rep {
    def ptyp = param.typ
    val typ = body.typ//FIXME funType(ptyp, body.typ)
    
    //def fun(r: Rep) = transformPartial(body) { case `param` => r }
    
    //def inline(arg: Rep): Rep = fun(arg) //body withSymbol (param -> arg)
    
    override def toString = s"Abs($param, $body)"
  }
  
  
  case class NewObject(typ: TypeRep) extends Rep
  
  //case class ModuleObject(fullName: String, typ: TypeRep) extends Rep
  case class ModuleObject(fullName: String, isPackage: Boolean) extends Rep {
    //println(s"Loading package $fullName")
    val m = sru.runtimeMirror(getClass.getClassLoader)
    val modSym = if (isPackage) m.staticPackage(fullName) else m.staticModule(fullName)
    ensureDefined(s"module $fullName", modSym)
    val typ: TypeRep = UninterpretedType(RuntimeUniverseHelpers.mkTag(modSym.typeSignature))
  }
  
  case class MethodApp(self: Rep, sym: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], typ: TypeRep) extends Rep {
    override def isPure: Bool = false
    override def toString: String =
      s"$self.${sym.name}<${sym.typeSignature}>[${targs mkString ","}]${argss mkString ""}"
      //s"$self.${sym.name}[${targs mkString ","}]${argss mkString ""}"
  }
  
  //case class Record(fields: List[(String, Rep)]) extends Rep { // TODO
  //  val typ = RecordType
  //}
  case class RecordGet(self: Rep, name: String, typ: TypeRep) extends Rep
  
  def repType(r: Rep): TypeRep = r.typ
  
  
  def getClassName(cls: sru.ClassSymbol) = RuntimeUniverseHelpers.srum.runtimeClass(cls).getName
  
  /**
    * FIXME handle encoding of multi-param lambdas
    */
  override def reinterpret(r: Rep, newBase: Base): newBase.Rep = {
    val bound = mutable.Map[BoundVal, newBase.BoundVal]()
    //def rec(r: Rep) = reinterpret(r, newBase)
    //def rect(typ: TypeRep) = reinterpret(typ, newBase)
    def recv(bv: BoundVal) = newBase.bindVal(bv.name, rect(bv.typ)) and (bound += bv -> _) //reinterpret(bv.typ, newBase))
    //import newBase.{methodApp, _}
    def rec(r: Rep): newBase.Rep = r match {
      case cnst @ Const(v) => newBase.const(v) //.asInstanceOf[newBase.Rep] // ???
      case MethodApp(self, mtd, targs, argss, tp) =>
        //val typ = reinterpret(tp, newBase)
        //val typ = newBase.loadTypSymbol(reflect.runtime.currentMirror.runtimeClass(mtd.owner.asClass).getName)
        val typ = newBase.loadTypSymbol(getClassName(mtd.owner.asClass))
        val alts = mtd.owner.typeSignature.member(mtd.name).alternatives
        val newMtd = newBase.loadMtdSymbol(typ, mtd.name.toString, if (alts.isEmpty) None else Some(alts.indexOf(mtd)), mtd.isStatic)
        newBase.methodApp(
          rec(self),
          newMtd,
          //targs map (t => reinterpret(t, newBase)),
          targs map (t => rect(t)),
          //argss map (_ .map (a => reinterpret(a, newBase).asInstanceOf[Rep]).asInstanceOf[newBase.ArgList]),
          argss map (_.map(newBase)(a => rec(a))),
          //reinterpret(tp, newBase))
          rect(tp))
          //typ)
      case ModuleObject(fullName, isPackage) => newBase.moduleObject(fullName, isPackage)
      case Abs(bv, body) if bv.name == "$BYNAME$" => newBase.byName(rec(body))
      case Abs(bv, body) => newBase.lambda({
        //println(bound)
        recv(bv)::Nil /*oh_and
        println(bound)*/
      }, rec(body))
      //case Abs(bv, body) => newBase.lambda(rec(bv).asInstanceOf[BoundVal]::Nil, rec(body))
      //case bv @ BoundVal(name) => newBase.boundVal(name, rect(bv.typ))
      //case bv @ BoundVal(name) => newBase.readVal(recv(bv))
      case bv @ BoundVal(name) => //println("LOL "+bound)
        newBase.readVal(bound(bv))
    }
    
  //}
  //def reinterpret(r: TypeRep, newBase: Base): newBase.TypeRep = {
    def rect(r: TypeRep): newBase.TypeRep = //reinterpret(r, newBase)
    //newBase.uninterpretedType[Any] // FIXME
    r match {
      case TypeApp(self, typ, targs) => newBase.typeApp(rec(self), newBase.loadTypSymbol(getClassName(typ.asClass)), // TODO B/E?
        targs map (t => rect(t)))
      //case ModuleType(fullName) => newBase.moduleType(fullName)
      case UninterpretedType(tag) => newBase.uninterpretedType(tag)
    }
    
    rec(r)
  }
  
  
  
  
  
}
























