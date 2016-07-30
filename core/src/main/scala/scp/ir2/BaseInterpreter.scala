package scp
package ir2

import utils._
import lang2._

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import scala.reflect.runtime.{universe => sru}

/**
  * Created by lptk on 28/07/16.
  * 
  * TODO: roll my own reflection lib, because of blocker bugs: class symbol cache problem, by-name parameters problem
  */
class BaseInterpreter extends Base with RuntimeSymbols with TraceDebug {
  
  val m = runtimeMirror(getClass.getClassLoader)
  
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
  def byName(arg: => Rep): Rep = arg
  //def byName(arg: => Rep): Rep = () => arg
  //def byName(arg: => Rep): Rep = () => {
  //  val x = arg
  //  println("Reading byname: "+x)
  //  x
  //}
  
  def newObject(tp: TypeRep): Rep = ??? // TODO
  
  def moduleObject(fullName: String, isPackage: Boolean): Rep = {
    
    if (isPackage) return null // TODO improve
    
    val sym = if (isPackage) m.staticPackage(fullName) else m.staticModule(fullName)
    
    if (sym.isJava) {
      return m.runtimeClass(sym.companion.asType.asClass)
    }
    
    val mm = m.reflectModule(sym)
    mm.instance
    
  }
  
  def methodApp(self: Rep, mtd: MtdSymbol, targs: List[TypeRep], argss_raw: List[ArgList], tp: TypeRep): Rep = {
    //println(s"$mtd <${mtd.typeSignature}>")
    
    val argss = argss_raw/*.map(_.map(this) {
      case bn: ByName => bn.get
      case a => a
    })*/
    
    //println("Int "+m.asInstanceOf[JavaMirror])
    //println("Int "+m.asInstanceOf[scala.reflect.runtime.JavaMirror].classToJava)
    //println("Int "+m.asInstanceOf[internal.JavaMirror].classToJava)
    
    ///*//
    //println("Self: '"+self+"' "+self.getClass)
    //println("Mtd: "+mtd.fullName+" "+mtd.isJava)
    debug(//Math.random()+
      s"\nSelf: '$self' ${self.getClass}\nMtd: "+mtd.fullName+" "+mtd.isJava)
    //*///
    
    if (mtd.isJava /*|| true*/) { // TODO test varargs
      assert(/*!mtd.isJava ||*/ mtd.paramLists.size == 1, "Java methods should have exactly one parameter list.")
      
      //println(classOf[Int])
      //println(0.getClass)
      //val IntType = m.runtimeClass(symbolOf[Int].asClass)
      
      /*
      println(m.runtimeClass(typeOf[Int]))
      println(m.runtimeClass(typeOf[Int].typeSymbol.asClass))
      //m.runtimeClass(typeOf[Int]).
      println(42.getClass)
      */
      
      val args = argss.flatMap(_.reps).asInstanceOf[List[Object]]
      //println("Args: "+args)
      //println("Args: "+args.map(_.getClass))
      //println("Args: "+(args.map(_.getClass) map toJavaCls))
      
      val params = 
        //mtd.paramLists.head.map(_.typeSignature.erasure) map m.runtimeClass map toJavaCls
        mtd.paramLists.head.map(_.typeSignature.erasure) map toJavaCls //map m.runtimeClass
        //(args.map(_.getClass) map toJavaCls)
      
      //val params = argss.head.reps.map(_.getClass)
      //println(argss.head.reps.map(_.getClass.isPrimitive))
      
      //self.getClass.getMethod(mtd.name, mtd.paramLists match { case () => .flatten })
      //m.runtimeClass()
      debug("Params erasure: "+params)
      
      val r = if (mtd.isStatic && mtd.isJava) {
        val javaMtd = self.asInstanceOf[Class[_]].getMethod(mtd.name.toString, params: _*)
        //println(javaMtd)
        //println(argss.map(_.reps).flatten.asInstanceOf[List[Object]])
        javaMtd.invoke(null, args: _*)
        //???
      } else {
        //val javaMtd = self.getClass.getMethod(mtd.name.toString, params: _*)
        val javaMtd = self.getClass.getMethod(mtd.name.toString, params: _*)
        //println("???")
        //Thread.sleep(1000)
        javaMtd.invoke(self, args: _*)
      }
      return r
    }
    
    if (mtd.fullName == "java.lang.String.$plus") { // string concatenation is an exception: does not have proper Java method
      return self + argss.head.reps.head.toString
    }
    
    
    m.classSymbol(classOf[Int]) // ??? made a crashing program run normally... it used to get scala.Int instead of int for javaClass(Int)
    
    //println("OPS "+ m.classSymbol(classOf[collection.immutable.StringOps]).toType.member(TermName("take")) )
    
    //val method = typeOf[List.type].member(TermName("apply")).asMethod
    //implicitly[ClassTag[Int]]
    //val im = m.reflect(self)
    //println(self.getClass, ClassTag(self.getClass))
    //println(reflect.ClassTag(42.getClass))
    //val im = m.reflect(self)(ClassTag(self.getClass))
    val tag = (self match {
      //case n: Int => ClassTag(n.getClass)
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
    val im = m.reflect(self)(tag)
    //val im = m.reflect(self)(ClassTag.Int.asInstanceOf[ClassTag[Any]])
    //println(im)
    val mm = im.reflectMethod(mtd)
    val args = argss flatMap {
      case Args(reps @ _*) => reps //Seq(reps: _*)
      case ArgsVarargs(Args(reps @ _*), Args(vreps @ _*)) => reps :+ vreps //Seq(reps: _*)
      case ArgsVarargSpliced(Args(reps @ _*), vrep) => reps :+ vrep//.asInstanceOf[Seq[_]] //Seq(reps: _*)
    }
    //println(args)
    mm(args: _*)
  }
  
  def uninterpretedType[A: sru.TypeTag]: TypeRep = ()
  def moduleType(fullName: String): TypeRep = ()
  def typeApp(self: Rep, typ: TypSymbol, targs: List[TypeRep]): TypeRep = ()
  def recordType(fields: List[(String, TypeRep)]): TypeRep = ???
  
  def repType(r: Rep): TypeRep = ()
  
  /*
  private val ByteType = m.runtimeClass(symbolOf[Byte].asClass)
  private val ByteCls = classOf[Byte]
  private val CharType = m.runtimeClass(symbolOf[Char].asClass)
  private val CharCls = classOf[Char]
  private val ShortType = m.runtimeClass(symbolOf[Short].asClass)
  private val ShortCls = classOf[Short]
  private val IntType = m.runtimeClass(symbolOf[Int].asClass)
  private val IntCls = classOf[Int]
  private val LongType = m.runtimeClass(symbolOf[Long].asClass)
  private val LongCls = classOf[Long]
  private val FloatType = m.runtimeClass(symbolOf[Float].asClass)
  private val FloatCls = classOf[Float]
  private val DoubleType = m.runtimeClass(symbolOf[Double].asClass)
  private val DoubleCls = classOf[Double]
  private val BoolType = m.runtimeClass(symbolOf[Boolean].asClass)
  private val BoolCls = classOf[Boolean]
  private val VoidType = m.runtimeClass(symbolOf[Unit].asClass)
  private val VoidCls = classOf[Unit]
  */
  // FIXME not sure uf useful; at runtime (at least):  m.runtimeClass(symbolOf[Int].asClass) == classOf[Int] == int  !
  //println("AAAAAH "+m.runtimeClass(symbolOf[Int].asClass))
  //lazy val IntType2 = m.runtimeClass(symbolOf[Int].asClass)
  
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
    case cls => m.runtimeClass(cls)
  }
  
  
  
}










