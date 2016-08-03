package scp
package ir2

import utils._
import lang2._
import meta.RuntimeUniverseHelpers._
import sru._

import scala.reflect.ClassTag
import scala.reflect.runtime.ScalaReflectSurgeon

class BaseInterpreter extends Base with RuntimeSymbols with TraceDebug {
  import BaseInterpreter._
  
  type Rep = Any
  class BoundVal(var value: Any = null)
  type TypeRep = Unit
  
  def bindVal(name: String, typ: TypeRep): BoundVal = new BoundVal()
  def readVal(v: BoundVal): Rep = v.value
  def const[A: sru.TypeTag](value: A): Rep = value
  
  /** Note: HOAS would be better, but we can't abstract over arity with HOAS so the rest of the code would suffer anyway */
  def lambda(params: List[BoundVal], body: => Rep): Rep = params match {
    case Nil => () => body
    case x0 :: Nil => (p0: Any) => x0.value = p0; body
    case x0 :: x1 :: Nil => (p0: Any, p1: Any) => x0.value = p0; x1.value = p1; body
    // TODO
    case _ => ???
  }
  
  def byName(arg: => Rep): Rep = () => arg
  
  
  def newObject(tp: TypeRep): Rep = ??? // TODO
  
  def moduleObject(fullName: String, isPackage: Boolean): Rep = {
    if (isPackage) return null // TODO improve
    val sym = if (isPackage) srum.staticPackage(fullName) else srum.staticModule(fullName)
    if (sym.isJava) return srum.runtimeClass(sym.companion.asType.asClass)
    
    //println(s"Mod $sym: "+srum.classSymbol(self.getClass))
    //println(s"Mod: "+srum.classSymbol(typeOf[scp.lib.Var.type].typeSymbol.asClass))
    //println(s"Mod: "+srum.classSymbol(classOf[scp.lib.Var.type]))
    
    val mm = srum.reflectModule(sym)
    
    mm.instance
  }
  
  /** These pesky primitive types have a tendency to change class symbol and break everything!
    * eg: Int -> scala.Int instead of Int -> int */
  private def watchPrimitives(): Unit = {
    val c = ScalaReflectSurgeon.cache
    c.enter(ByteCls, ByteType.typeSymbol.asClass)
    c.enter(CharCls, CharType.typeSymbol.asClass)
    c.enter(ShortCls, ShortType.typeSymbol.asClass)
    c.enter(IntCls, IntType.typeSymbol.asClass)
    c.enter(LongCls, LongType.typeSymbol.asClass)
    c.enter(FloatCls, FloatType.typeSymbol.asClass)
    c.enter(DoubleCls, DoubleType.typeSymbol.asClass)
    c.enter(BoolCls, BoolType.typeSymbol.asClass)
    c.enter(VoidCls, VoidType.typeSymbol.asClass)
  }
  
  def methodApp(self: Rep, _mtd: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep = {
    
    watchPrimitives()
    
    debug(s"\nSelf: '$self' ${self.getClass}\nMtd: "+_mtd.fullName+" "+_mtd.isJava)
    
    val cls = srum.classSymbol(self.getClass)
    if (cls.isJava && cls.isModuleClass) {
      reflect.runtime.ScalaReflectSurgeon.cache.enter(self.getClass, cls.companion.asClass)
    }
    
    debug(s"Cls $cls ${cls.isModuleClass}") // object Var true
    //debug(s"Members ${cls.toType.members.map("\n"+_).mkString}")
    //debug(s"Own ${mtd.owner} ${mtd.owner==cls}")
    
    /** A hack (again): try to retrieve the method from the `cls` symbol of `self`,
      * to avoid some cases where the reflection API complains that {{{_mtd}}} is not a member of `cls` (it tests its base classes)
      * Note: we may not actually find the method this way, eg for bytecode-less methods like those on primitives */
    
    //val mtd = _mtd
    val f1 = _mtd.paramLists.flatten
    val mtdO = cls.toType.member(_mtd.name).alternatives collectFirst {
      case s: MethodSymbol if {
        val f0 = s.paramLists.flatten
        f0.size == f1.size && (f0 zip f1 forall {
          case (ps, pm) => ps.typeSignature.typeSymbol.fullName.toString == pm.typeSignature.typeSymbol.fullName.toString
        })
      } => s
    }
    //if (mtdO.isDefined) println(s"Found method ${mtdO.get} in $cls") else println(s"Did not find method ${_mtd} in $cls")
    val mtd = mtdO getOrElse _mtd
    
    if (mtd.isJava && mtd.isStatic) {
      assert(mtd.paramLists.size == 1, "Java methods should have exactly one parameter list.")
      
      val params = mtd.paramLists.head.map(_.typeSignature.erasure) map toJavaCls
      debug("Params erasure: "+params)
      
      val javaMtd = self.asInstanceOf[Class[_]].getMethod(mtd.name.toString, params: _*)
      
      val args = argss.head.reps.asInstanceOf[Seq[Object]]
      
      javaMtd.invoke(null, args: _*)
      
    } else {
      
      //debug("Initial signature: "+mtd.typeSignature)
      
      val changes = mtd.paramLists.flatten.filter(_.typeSignature.typeSymbol == ByNameParamClass) map { x =>
        x -> (x.typeSignature -> sru.typeOf[() => Any])
      }
      BaseInterpreter.synchronized { try {
        changes foreach { case (s, (_, newTpe)) => ScalaReflectSurgeon.changeType_omgwtfbbq(s, newTpe) }
        //debug("Modified signature: "+mtd.typeSignature)
        
        val tag = (self match {
          case _: Byte => ClassTag.Byte
          case _: Char => ClassTag.Char
          case _: Short => ClassTag.Short
          case _: Int => ClassTag.Int
          case _: Long => ClassTag.Long
          case _: Float => ClassTag.Float
          case _: Double => ClassTag.Double
          case _: Boolean => ClassTag.Boolean
          case _: Unit => ClassTag.Unit
          case _ => ClassTag(self.getClass)
        }).asInstanceOf[ClassTag[Any]]
        
        val im = srum.reflect(self)(tag)
        val mm = im.reflectMethod(mtd)
        val args = argss flatMap {
          case Args(reps @ _*) => reps
          case ArgsVarargs(Args(reps @ _*), Args(vreps @ _*)) => reps :+ vreps
          case ArgsVarargSpliced(Args(reps @ _*), vrep) => reps :+ vrep
        }
        //println(args)
        
        mm(args: _*)  // oh_and debug("Final Sign: "+mtd.typeSignature)
      }
      finally {
        changes foreach { case (s, (oldTpe, _)) => ScalaReflectSurgeon.changeType_omgwtfbbq(s, oldTpe) }
      }}
      
    }
    
    
  }
  
  
  
  
  def uninterpretedType[A: sru.TypeTag]: TypeRep = ()
  def moduleType(fullName: String): TypeRep = ()
  def typeApp(self: Rep, typ: TypSymbol, targs: List[TypeRep]): TypeRep = ()
  def recordType(fields: List[(String, TypeRep)]): TypeRep = ???
   
  
  
  
}
object BaseInterpreter {
  private val ByteType = typeOf[Byte]
  private val ByteCls = classOf[Byte]
  private val CharType = typeOf[Char]
  private val CharCls = classOf[Char]
  private val ShortType = typeOf[Short]
  private val ShortCls = classOf[Short]
  private val IntType = typeOf[Int]
  private val IntCls = classOf[Int]
  private val LongType = typeOf[Long]
  private val LongCls = classOf[Long]
  private val FloatType = typeOf[Float]
  private val FloatCls = classOf[Float]
  private val DoubleType = typeOf[Double]
  private val DoubleCls = classOf[Double]
  private val BoolType = typeOf[Boolean]
  private val BoolCls = classOf[Boolean]
  private val VoidType = typeOf[Unit]
  private val VoidCls = classOf[Unit]
  
  //def toJavaCls(cls: Class[_]) = cls match { // TODO use classTag[T].runtimeClass instead; see JavaMirrors#preciseClass // TODO or use typeToJavaClass
  /** This is mainly to go around Scala's runtimeClass, which is broken as it returns inconsistent/wrong results depending on the state of its cache... */
  def toJavaCls(cls: Type): Class[_] = cls match {
    case ByteType => ByteCls
    case CharType => CharCls
    case ShortType => ShortCls
    case IntType => IntCls
    case LongType => LongCls
    case FloatType => FloatCls
    case DoubleType => DoubleCls
    case BoolType => BoolCls
    case VoidType => VoidCls
    //case cls => cls
    case cls => srum.runtimeClass(cls)
  }
  
}









