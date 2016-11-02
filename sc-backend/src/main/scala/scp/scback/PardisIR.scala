package scp
package scback

import ch.epfl.data.sc._
import ch.epfl.data.sc.pardis.ir.{Base => _, _}
import ch.epfl.data.sc.pardis.prettyprinter.ScalaCodeGenerator
import ch.epfl.data.sc.pardis.types.{PardisType, PardisTypeImplicits}
import pardis._
import pardis.{ir => pir}
import scp.utils._
import CollectionUtils.TraversableOnceHelper
import ch.epfl.data.sc.pardis.utils.document.Document

import scala.collection.mutable
import lang2._
import meta.{RuntimeUniverseHelpers => ruh}
import meta.RuntimeUniverseHelpers.sru
import scp.ir2.IRException
import ir2.{Covariant, Variance}

import scala.language.existentials
import scala.reflect.runtime.universe.TypeTag


/** IR Base that uses SC as a backend. It constructs PardisType's via reflective invocation of methods found in `ir`
  * Note: For convenience, we use Scala MethodSymbol and TypeSymbol to identify methods and types, but we could do with
  *   a simpler representation. */
abstract class PardisIR(val ir: pardis.ir.Base) extends Base with ir2.RuntimeSymbols with InspectableBase { self =>
  import ir.Def
  
  type Rep = ir.Rep[Any]
  type BoundVal = ir.Sym[_]
  type TypeRep = ir.TypeRep[Any]
  type Block = ir.Block[Any]
  type Sym = ir.Sym[_]
  type Stm = ir.Stm[_]
  
  type R[+A] = ir.Rep[A]
  type TR[A] = ir.TypeRep[A]
  
  type TypSymbol = ScalaTypeSymbol
  
  
  case class New[A](_tp: TR[A]) extends Expression[A]()(_tp)
  case class TopLevelBlock[A](b: ir.Block[A]) extends Expression[A]()(b.tp)
  
  case class Hole[A](name: String, _tp: TR[A]) extends Expression[A]()(_tp)
  
  
  case class TypeHole[A](name: String) extends PardisType[A] {
    def rebuild(newArguments: TR[_]*): TR[_] = this
    val typeArguments: List[TR[_]] = Nil
  }
  
  
  
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
    //ir.reflectStm(ir.Stm[Any](bound, value.correspondingNode)(bound.tp))  // duplicates the Def (not good!)
    ir.reflectStm(ir.Stm[Any](bound, PardisReadVal(value.asInstanceOf[R[Any]])(bound.tp))(bound.tp))  // duplicates the binding
    body
  }
  def newObject(tp: TypeRep): Rep = New(tp)
  def staticModule(fullName: String): Rep = null
  def module(prefix: Rep, name: String, typ: TypeRep): Rep = ???
  def byName(arg: => Rep): Rep = TopLevelBlock(typedBlock(arg))
  
  object Const extends ConstAPI {
    def unapply[T: IRType](ir: IR[T, _]): Option[T] = ir.rep match {
      case cst @ pir.Constant(v) if cst.typ <:< irTypeOf[T].rep => Some(v.asInstanceOf[T])
      case _ => none
    }
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
          case r: TopLevelBlock[_] => showRep(r) // FIXME should not happen
          case _                    => super.expToDocument(exp)
        }
        override def stmtToDocument(stmt: Statement[_]) = super.stmtToDocument(stmt) match {
          case d if d === Document.empty => d
          case d => d :: ";"
        }
      }.blockToDocument(b).toString
      case cn =>
        //println(cn)
        r.toString
    }
  }
  
  
  // Helpers
  
  protected def typedBlock(body: => Rep): Block = {
    val ir.Block(s,r) = ir.reifyBlock(body.asInstanceOf[ir.Rep[Any]])(types.AnyType)
    ir.Block(s,r)(r.tp)
  }
  def toAtom(r: ir.Def[_]) = ir.toAtom[Any](r)(r.tp.asInstanceOf[TR[Any]])
  
  
  
  // * --- * --- * --- *  Implementations of `QuasiBase` methods  * --- * --- * --- *
  
  
  def substitute(r: Rep, defs: Map[String, Rep]): Rep = r // TODO the subs
  
  def hole(name: String, typ: TypeRep): Rep = Hole(name, typ)
  def splicedHole(name: String, typ: TypeRep): Rep = ???
  def typeHole(name: String): TypeRep = TypeHole(name)
  
  
  
  // * --- * --- * --- *  Implementations of `TypingBase` methods  * --- * --- * --- *
  
  
  def uninterpretedType[A: TypeTag]: TypeRep = {
    //println("Unint: "+implicitly[TypeTag[A]])
    implicitly[TypeTag[A]].tpe match {
      case ruh.Any => types.AnyType
      case ruh.Nothing => types.NothingType.asInstanceOf[TypeRep]
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
  
  // TODO refine? Basic subtyping like Int <: Any?
  def typLeq(a: TypeRep, b: TypeRep): Boolean =
    (a.name === b.name) && ((a.typeArguments zipAnd b.typeArguments)((typLeq _).asInstanceOf[(TR[_],TR[_])=>Bool]) forall identity)
  
  
  
  // * --- * --- * --- *  Implementations of `IntermediateBase` methods  * --- * --- * --- *
  
  
  def repType(r: Rep): TypeRep = r.tp
  def boundValType(bv: BoundVal): TypeRep = bv.tp
  def reinterpret(r: Rep, newBase: Base)(extrudedHandle: (BoundVal => newBase.Rep) = DefaultExtrudedHandler): newBase.Rep = ???
  
  
  
  // * --- * --- * --- *  Implementations of `InspectableBase` methods  * --- * --- * --- *
  
  
  override def bottomUpPartial(r: Rep)(f: PartialFunction[Rep, Rep]): Rep = ??? // TODO
  def bottomUp(r: Rep)(f: Rep => Rep): Rep = transformRep(r)(identity, f)
  def topDown(r: Rep)(f: Rep => Rep): Rep = transformRep(r)(f)
  def transformRep(r: Rep)(pre: Rep => Rep, post: Rep => Rep = identity): Rep = (new RepTransformer(pre,post))(r)
  
  
  // FIXME: doesn't properly work: have to reify statements again (cf: global symbol map)
  class RepTransformer(pre: Rep => Rep, post: Rep => Rep) {
    
    protected val subst = collection.mutable.Map.empty[Rep, Rep]
    
    def apply(r: Rep): Rep = post(pre(r) match {
      case cst: pir.Constant[_] => cst
      case ex: ExpressionSymbol[_] => ex
      case TopLevelBlock(b) => TopLevelBlock(b |> apply)
      // New, Hole
    })
    def apply(fa: PardisFunArg): PardisFunArg = fa match {
      case r: Rep => apply(r)
      case PardisVarArg(und) =>
        println(und)
        //PardisVarArg(und |> apply)
        ir.__varArg(und |> apply)
    }
    // TODO copy other cases from SC's Transformer.scala:162
    def apply(d: Def[_]): Def[_] = d match {
      case b: ir.Block[_] => apply(b)
      //case n: pir.PardisNode[_] => n.rebuild(n.funArgs map apply : _*)
      case _ => d.rebuild(d.funArgs.map(transformFunArg): _*)
    }
    protected def transformFunArg(funArg: PardisFunArg): PardisFunArg = funArg match {
      case d: Def[_]       => apply(d.asInstanceOf[Def[Any]]).asInstanceOf[PardisFunArg]
      case e: Rep       => apply(e)
      case PardisVarArg(v) => transformFunArg(v)
    }
    
    def apply(b: Block): Block = {
      ir.reifyBlock[Any] {
        b.stmts foreach apply
        b.res
      }(b.tp)
    }
    def transformType(tp: TypeRep): TypeRep = tp
    def apply(st: Stm): Stm = {
      val ir.Stm(sym, rhs) = st
      val transformedSym = if (ir.IRReifier.findDefinition(sym).nonEmpty) {
        // creates a symbol with a new name in order not to conflict with any existing symbol
        ir.fresh(transformType(sym.tp))
      } else {
        //newSym(sym.asInstanceOf[Sym[Any]])
        ir.fresh(transformType(sym.tp)).copyFrom(sym)(sym.tp)
      }

      subst += sym -> transformedSym
      val newdef = apply(rhs)

      val stmt = ir.Stm(transformedSym, newdef)(transformedSym.tp)
      
      ir.reflectStm(stmt)
      stmt
    }
    
  }
  
  protected def failExtrWith(msg: => String) = none oh_and debug(s"${Console.RED}$msg${Console.RESET}")
  
  override def merge(a: Extract, b: Extract): Option[Extract] =
    super.merge(a,b) >>? { case None => failExtrWith(s"Cannot merge: $a and $b") }  
  
  
  protected def extract(xtor: Rep, xtee: Rep): Option[Extract] = debug(s"${"Extr." |> bold} $xtor << $xtee") before nestDbg(xtor -> xtee match {
      
    case Hole(name, typ) -> _ => 
      typ extract (xtee.typ, Covariant) flatMap { merge(_, (Map(name -> xtee), Map(), Map())) }
      
    case (Constant(v1), Constant(v2)) =>
      mergeOpt(extractType(xtor.tp, xtee.tp, Covariant),
        if (v1 === v2) Some(EmptyExtract) else failExtrWith(s"Different constant values: $v1 =/= $v2"))
      
    // TODO proper impl of extraction
    case TopLevelBlock(ir.Block(s0, r0)) -> TopLevelBlock(ir.Block(s1, r1)) => ???
    case TopLevelBlock(ir.Block(Nil, r0)) -> _ =>
      r0 extract xtee
      
    case Def(f0: pir.FunctionNode[_]) -> Def(f1: pir.FunctionNode[_]) if pure(f0) && pure(f1) => //if f0.isPure && f1.isPure =>
      extractDef(f0, f1)
      
    case (es0: ExpressionSymbol[_]) -> (es1: ExpressionSymbol[_]) if es0 == es1 => SomeEmptyExtract
    
    //case (es0: ExpressionSymbol[_]) -> (es1: ExpressionSymbol[_]) if rwrCtx.applyOrElse(es1) === es0 =>
    case (es0: ExpressionSymbol[_]) -> (es1: ExpressionSymbol[_]) if es1 |>? rwrCtx contains es0 => SomeEmptyExtract // Q: handle xted bindings here?
      
    //case _ => None
    case _ =>
      failExtrWith(s"No match.")
  })
  
  protected def extractDef(xtor: Def[_], xtee: Def[_]): Option[Extract] = {
    xtor -> xtee match {
      case (f0: pir.FunctionNode[_]) -> (f1: pir.FunctionNode[_]) =>
        if (f0.name =/= f1.name) failExtrWith(s"Different names: ${f0.name} =/= ${f1.name}")
        else for {
          e <- f0.caller -> f1.caller match {
            case Some(c0) -> Some(c1) => c0 extract c1
            case None -> None => SomeEmptyExtract
            case c0 -> c1 => throw IRException(s"Inconsistent callers: $c0 and $c1")
          }
          () = assert(f0.typeParams.size === f1.typeParams.size)
          ts <- mergeAll( (f0.typeParams zip f1.typeParams) map { case (a,b) => weakenTypeRep(a) extract (b, Covariant) } )
          e <- merge(e, ts)
          ass <- mergeAll( (f0.funArgs zip f1.funArgs) map {
            case (a:ir.Rep[_],b:ir.Rep[_]) => extract(a,b)
            case (a:ir.Block[_],b:ir.Block[_]) => extract(TopLevelBlock(a),TopLevelBlock(b))
            // TODO lambdaDef, vararg
          })
          e <- merge(e, ass)
        } yield e
      case PardisReadVal(b0) -> PardisReadVal(b1) => b0 extract b1
      case _ -> PardisReadVal(b1) => failExtrWith(s"No Def match [1]") // TODO better?
      case PardisReadVal(b0) -> _ => failExtrWith(s"No Def match [2]") // TODO better?
    }
  }
  
  protected def extractBlock(xtor: Block, xtee: Block): Option[Extract] = {
    /*
    //val left = xtor.stmts
    //def rec(ex: Extract, matchedVals: List[Sym])(xy: Block -> Block): Option[Rep] = /*println(s"rec $xy") before*/ (xy match {
    def rec(matchedHoles: List[BoundVal -> List[Stm]])(left: List[Stm], right: List[Stm]): Option[List[BoundVal -> List[Stm]] -> Extract] = left -> right match {
      //case (l :: ls) -> _ if d0(l) |> isHole => rec((l -> Nil) :: matchedHoles)(ls, right)
      //case (l :: ls) -> (r :: rs) => 
      case Nil -> Nil => matchedHoles -> EmptyExtract |> some
      case _ => ???
    }
    rec(Nil)(xtor.stmts, xtee.stmts)
    */
    ???
  }
  
  // TODO proper impl w/ cleanup
  // the xtor symbol a symbol has been matched with
  // extraction should make the corresponding xtor Stm match the Stm
  // substitution should apply based on associated Stm name
  type RwContext = PartialFunction[Sym, Sym]
  //def rwrCtx: RwContext = ???
  val rwrCtx = mutable.Map[Sym, Sym]()
  
  
  def bold(str: String) = {
    import Console.{BOLD, RESET}
    s"${BOLD}$str$RESET"
  }
  
  def pure(d: Def[_]) = d match {
    case d: PardisReadVal[_] => true
    case _ => d isPure
  }
  
  override def rewriteRep(xtor: Rep, xtee: Rep, code: Extract => Option[Rep]): Option[Rep] = /*ANFDebug muteFor*/ {
    debug(s"${"Rewriting" |> bold} $xtee ${"with" |> bold} $xtor")
    
    def codeBlock(ex: Extract): Option[Block] = typedBlock(code(ex) getOrElse (return None)) |> some
    
    type Val = Sym
    type ListBlock = List[Stm] -> Rep
    
    def toHole(from: Sym, to: Sym): Extract = {
      debug(s"TODO Should bind $from -> $to")
      // TODO handle xted bindings!
      // TODO update rwr context
      
      rwrCtx += from -> to // TODO cleanup
      
      EmptyExtract
    }
    
    /** Tries to apply a RwR on a list of statements by trying to match the xtor (itself a list of statements),
      * then applying the associated `code` function and checking removed statements are not used later on in the list.
      * @return pure statements encountered and skipped, rewritten block output by `code`, rest of the statements */
    def rec(ex: Extract, matchedVals: List[Val], pureStms: List[Stm])(xy: ListBlock -> ListBlock): Option[(List[Stm], Block, ListBlock)] =  // TODO rm `matchedVals`?
    debug(s"rec ${xy._1}\n<<  ${xy._2}") before nestDbg(xy match {
      case (Nil -> r0, Nil -> r1) =>
        for {
          e <- extract(r0, r1)
          //() = println(e)
          m <- merge(e, ex)
          //() = println(m)
          () = debug(s"Constructing rewritten code with $m")
          b <- codeBlock(m)
        } yield (pureStms, b, Nil -> r1) // TODO update symbols!?
      
      case (x, ((ps @ ir.Stm(_, e1)) :: es1) -> r1) if e1 |> pure =>
        rec(ex, matchedVals, ps :: pureStms)(x, es1 -> r1)
        
        
      case ((ir.Stm(b0, e0) :: es0) -> r0, (ir.Stm(b1, e1) :: es1) -> r1) =>
        //println("e0: "+e0)
        //println("e1: "+e1)
        for {
          e <- extractDef(e0, e1)
          e <- merge(e, ex)
          hExtr = toHole(b1, b0)
          //() = println(hExtr,h)
          e <- merge(e, hExtr)
          //() = println(s"Extracting binder $b1: $hr")
          es1i = es1
          r1i = r1
          ///*bl -> ls <-*/ ( rec(e, b1 :: matchedVals)(Nil -> r0, Nil -> b1) If (es0 isEmpty) Else None
          //r <- ( (if (es0 isEmpty) rec(e, b1 :: matchedVals, pureStms)(Nil -> r0, Nil -> b1) map {case bl->(ls->r) => bl->((ls++es1i)->r1i) } else None)
          r <- ( (if (es0 isEmpty) rec(e, b1 :: matchedVals, pureStms)(Nil -> r0, Nil -> b1) map {case (ps,bl,(ls->r)) => (ps,bl,(ls++es1i)->r1i) } else None)
          orElse rec(e, b1 :: matchedVals, pureStms)(es0 -> r0, es1i -> r1i) )
          
        } yield r
        //} yield bl -> ls
        //???
        
      case (Nil -> r0, (ir.Stm(b, v) :: es) -> r1) =>
        println(r0, matchedVals)
        //for {
        //  e <- extract(r0, v)
        //  e <- merge(e, ex)
        //  c <- code(e)
        //  r = constructBlock((Left(b -> c) :: es) -> r1)
        //  if !(originalVals(r) exists matchedVals.toSet) // abort if one of the Vals matched so far is still used in the result of the rewriting
        //} yield r
        ???
      
      case _ =>
        debug("Rec reached None")
        None
        
    })
    
    def toBlock(x: Rep) = x match {
      case TopLevelBlock(ir.Block(sts, r)) => sts -> r
      case r => Nil -> r
    }
    
    def process(xtor: ListBlock, xtee: ListBlock): List[Stm \/ Block] = {
      rec(EmptyExtract, Nil, Nil)(xtor, xtee) match {
        case Some((ps, b, lb)) => ((ps map Left.apply) :+ Right(b)) ++ process(xtor, lb)
        case None => xtee match {
          case (st :: sts) -> r => Left(st) :: process(xtor, sts -> r)  // FIXME no need to pass around the return?
          case Nil -> r => Nil
        }
      }
    }
    val xteeBlock = xtee |> toBlock
    val processed = process(xtor |> toBlock, xteeBlock)
    
    if (processed forall (_ isLeft)) none  // No RwR have kicked in
    else TopLevelBlock(ir.reifyBlock {
      processed foreach {
        case Left(s: Stm) => ir.reflectStm(s)//(s.)
        case Right(bl) =>
          bl.stmts foreach (s => ir.reflectStm(s))
      }
      xteeBlock._2
    }(xtee.tp)) |> some
    
    //???
  }
  
  
  
  protected def spliceExtract(xtor: Rep, t: Args): Option[Extract] = ???

  // TODO refine? Basic subtyping like Int <: Any?
  def extractType(xtor: TypeRep, xtee: TypeRep, va: Variance): Option[Extract] = debug(s"$va ${s"TypExtr." |> bold} $xtor << $xtee") before nestDbg(xtor match {
    case TypeHole(name) => mkExtract()(name -> xtee)() |> some
    case _ =>
      //if (xtor.name =/= xtee.name) failExtrWith(s"Different type names: ${xtor.name} =/= ${xtee.name}")
      val xtorNameBase = xtor.name takeWhile (_ =/= '[')
      val xteeNameBase = xtee.name takeWhile (_ =/= '[')
      if (xtorNameBase =/= xteeNameBase) failExtrWith(s"Different type names: ${xtorNameBase} =/= ${xteeNameBase}")
      else {
        assert(xtor.typeArguments.size === xtee.typeArguments.size)
        mergeAll((xtor.typeArguments zipAnd xtee.typeArguments)(extractType(_,_,va))) // TODO proper handling of va
      }
  })
  
  
  /** Extraction nodes need to be wrapped in a Block, because it makes no sense to let statements escape the pattern. */
  override def wrapExtract(r: => Rep): Rep = TopLevelBlock(typedBlock(r))
  
  
  
  
  
  // * --- * --- * --- *  Other Implementations * --- * --- * --- *
  
  
  def block[T:IRType,C](q: => IR[T,C]) = `internal IR`[T,C](TopLevelBlock[T](pardisBlock(q)))
  def pardisBlock[T:IRType,C](q: => IR[T,C]) = ir.reifyBlock[T] { q.rep.asInstanceOf[ir.Rep[T]] }
  
  
  implicit def typeRepFromIRType[A:IRType]: ir.TypeRep[A] = implicitly[IRType[A]].rep.asInstanceOf[ir.TypeRep[A]]
  
  implicit def weakenTypeRep(tr: TR[_]): TypeRep = tr.asInstanceOf[TypeRep]
  
  
}



