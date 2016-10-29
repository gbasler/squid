package scp
package scback

import ch.epfl.data.sc._
import ch.epfl.data.sc.pardis.ir.{Expression, PardisReadVal}
import ch.epfl.data.sc.pardis.prettyprinter.ScalaCodeGenerator
import ch.epfl.data.sc.pardis.types.PardisTypeImplicits
import pardis._
import scp.utils._

import scala.collection.mutable
import lang2._
import meta.{RuntimeUniverseHelpers => ruh}
import meta.RuntimeUniverseHelpers.sru
import scp.ir2.IRException

import scala.reflect.runtime.universe.TypeTag


/** IR Base that uses SC as a backend. It constructs PardisType's via reflective invocation of methods found in `ir`
  * Note: For convenience, we use Scala MethodSymbol and TypeSymbol to identify methods and types, but we could do with
  *   a simpler representation. */
abstract class PardisIR(val ir: pardis.ir.Base) extends Base with ir2.RuntimeSymbols with IntermediateBase { self =>
  
  
  type Rep = ir.Rep[_] // TODO make it [Any]
  type BoundVal = ir.Sym[_]
  type TypeRep = ir.TypeRep[_]
  
  type R[+A] = ir.Rep[A]
  type TR[A] = ir.TypeRep[A]
  
  type TypSymbol = ScalaTypeSymbol
  
  
  case class New[A](_tp: TR[A]) extends Expression[A]()(_tp)
  case class TopLevelBlock[A](b: ir.Block[A]) extends Expression[A]()(b.tp)
  
  
  
  // * --- * --- * --- *  Implementations of `Base` methods  * --- * --- * --- *
  
  
  def bindVal(name: String, typ: TypeRep, annots: List[Annot]): BoundVal = ir.freshNamed(name)(typ)
  def readVal(v: BoundVal): Rep = v
  def const(value: Any): Rep = {
    import types.PardisTypeImplicits._
    value match {
      case value: Unit => ir.unit(value)
      case value: Boolean => ir.unit(value)
      case value: Int => ir.unit(value)
      case value: Double => ir.unit(value)
      case value: String => ir.unit(value)
      case _ =>
        println("Unsupported constant value: "+value)
        ??? // TODO
    }
  }
  def lambda(params: List[BoundVal], body: => Rep): Rep = params match {
    case p :: Nil =>
      val ir.Block(s,r) = ir.reifyBlock(body.asInstanceOf[ir.Rep[Any]])(types.AnyType)
      val typedBlock = ir.Block(s,r)(r.tp)
      val d = ir.Lambda[Any,Any]((x: Rep) => ??? /*TODO*/ , p, typedBlock)(p.tp/*.asInstanceOf[TR[Any]]*/, r.tp)
      ir.toAtom(d)(types.PardisTypeImplicits.typeLambda1(p.tp, r.tp))
    case _ => ??? // TODO
  }
  override def letin(bound: BoundVal, value: Rep, body: => Rep, bodyType: TypeRep): Rep = {
    //ir.reflectStm(ir.Stm[Any](bound, value.correspondingNode)(bound.tp)) // duplicates the Def (not good!)
    ir.reflectStm(ir.Stm[Any](bound, PardisReadVal(value.asInstanceOf[R[Any]])(bound.tp))(bound.tp))
    body
  }
  def newObject(tp: TypeRep): Rep = New(tp)
  def staticModule(fullName: String): Rep = null
  def module(prefix: Rep, name: String, typ: TypeRep): Rep = ???
  def byName(arg: => Rep): Rep = TopLevelBlock(typedBlock(arg))
  
  object Const extends ConstAPI {
    def unapply[T: sru.TypeTag](ir: IR[T, _]): Option[T] = ???
  }
  
  def repEq(a: Rep, b: Rep): Boolean = a == b
  
  
  // Reimplementations
  
  override def showRep(r: Rep) = {
    import pardis.deep.scalalib.ArrayScalaCodeGen
    import ch.epfl.data.sc.pardis.prettyprinter.ASTCodeGenerator
    import ch.epfl.data.sc.pardis.utils.document.toDocumentContext
    import ch.epfl.data.sc.pardis.ir.{Constant}
    r match {
      //case ir.Def(b: ir.Block[_]) => new ScalaCodeGenerator{}.blockToDocument(b).toString
      case TopLevelBlock(b) => new ScalaCodeGenerator with ASTCodeGenerator[ir.type] with ArrayScalaCodeGen {
        val IR: ir.type = ir
        override def expToDocument(exp: Expression[_]) = exp match {
          case Constant(b: Boolean) => doc"${b.toString}"
          case _                    => super.expToDocument(exp)
        }
      }.blockToDocument(b).toString
      case cn =>
        //println(cn)
        r.toString
    }
  }
  
  
  // Helpers
  
  def typedBlock(body: => Rep): ir.Block[_] = {
    val ir.Block(s,r) = ir.reifyBlock(body.asInstanceOf[ir.Rep[Any]])(types.AnyType)
    ir.Block(s,r)(r.tp)
  }
  def toAtom(r: ir.Def[_]) = ir.toAtom[Any](r)(r.tp.asInstanceOf[TR[Any]])
  
  
  
  // * --- * --- * --- *  Implementations of `QuasiBase` methods  * --- * --- * --- *
  
  
  def substitute(r: Rep, defs: Map[String, Rep]): Rep = r // TODO the subs
  
  def hole(name: String, typ: TypeRep): Rep = ???
  def splicedHole(name: String, typ: TypeRep): Rep = ???
  def typeHole(name: String): TypeRep = ???
  
  
  
  // * --- * --- * --- *  Implementations of `TypingBase` methods  * --- * --- * --- *
  
  
  def uninterpretedType[A: TypeTag]: TypeRep = {
    //println("Unint: "+implicitly[TypeTag[A]])
    implicitly[TypeTag[A]].tpe match {
      case ruh.Any => types.AnyType
      case ruh.Nothing => types.NothingType
      case typ =>
        throw new IRException(s"Unsupported uninterpreted type: `$typ`")
    }
  }
  def typeApp(self: TypeRep, typ: TypSymbol, targs: List[TypeRep]): TypeRep = ???
  def staticTypeApp(typ: TypSymbol, targs: List[TypeRep]): TypeRep = {
    val (obj,tname) = typ match {
      case _ if targs.nonEmpty && typ === ruh.FunctionType.symbol(targs.size-1) => ir.Predef -> ("typeLambda"+(targs.size-1))
      case _ => ir -> ("type"+typ.name.toString)
    }
    try {
      val rec -> m = try obj -> obj.getClass.getMethod(tname, targs map (_ => classOf[TypeRep]): _*)
      catch { case e: NoSuchMethodException =>
        PardisTypeImplicits -> PardisTypeImplicits.getClass.getMethod(tname, targs map (_ => classOf[TypeRep]): _*)
      }
      val r = m.invoke(rec, targs: _*)
      r.asInstanceOf[TypeRep]
    } catch {
      case e: NoSuchMethodException =>
        throw new IRException(s"Could not find a deep type representation for type symbol `$typ` (tried name ${'"'+tname+'"'}); perhaps it is absent from the DSL cake.", Some(e))
    }
  }
  def recordType(fields: List[(String, TypeRep)]): TypeRep = ???
  def constType(value: Any, underlying: TypeRep): TypeRep = underlying
  
  def typLeq(a: TypeRep, b: TypeRep): Boolean = ???
  
  
  
  // * --- * --- * --- *  Implementations of `IntermediateBase` methods  * --- * --- * --- *
  
  
  def repType(r: Rep): TypeRep = r.tp
  def boundValType(bv: BoundVal): TypeRep = bv.tp
  def reinterpret(r: Rep, newBase: Base)(extrudedHandle: (BoundVal => newBase.Rep) = DefaultExtrudedHandler): newBase.Rep = ???
  
  
  
  // * --- * --- * --- *  Other Implementations * --- * --- * --- *
  
  
  def block[T:IRType,C](q: => IR[T,C]) = `internal IR`[T,C](TopLevelBlock[T](pardisBlock(q)))
  def pardisBlock[T:IRType,C](q: => IR[T,C]) = ir.reifyBlock[T] { q.rep.asInstanceOf[ir.Rep[T]] }
  
  
  implicit def typeRepFromIRType[A:IRType]: ir.TypeRep[A] = implicitly[IRType[A]].rep.asInstanceOf[ir.TypeRep[A]]
  
}



