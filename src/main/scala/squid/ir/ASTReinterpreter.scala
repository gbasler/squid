package squid
package ir

import squid.quasi.MetaBases
import utils._
import utils.meta.{RuntimeUniverseHelpers => ruh}
import ruh.sru
import squid.lang.Base

import scala.collection.mutable

trait ASTReinterpreter { ast: AST =>
  
  
  /* --- --- --- Node Reinterpretation --- --- --- */
  
  
  def reinterpret(r: Rep, newBase: Base)(extrudedHandle: (BoundVal => newBase.Rep) = DefaultExtrudedHandler): newBase.Rep =
    Reinterpreter(newBase)((r, f) => f(dfn(r)), extrudedHandle)(r)
  
  
  /** TODO cache symbols (use forwarder?) */
  trait Reinterpreter {
    val newBase: Base
    def apply(r: Rep): newBase.Rep
    
    val ascribeBoundValsWhenNull = false  // also applies to bindings with value of type Nothing since Nothing <: Null
    
    val extrudedHandle: BoundVal => newBase.Rep = bv => wth(s"Extruded variable: $bv")
    
    //def getClassName(cls: sru.ClassSymbol) = ruh.srum.runtimeClass(cls).getName
    
    protected val bound = mutable.Map[BoundVal, newBase.BoundVal]()
    
    protected def apply(d: Def): newBase.Rep = d match {
        
      case cnst @ Constant(v) => newBase.const(v)
      case LetIn(bv, vl, body) =>
        val v = apply(vl)
        val av = if (ascribeBoundValsWhenNull && vl.typ <:< ruh.Null) newBase.ascribe(v, rect(vl.typ)) else v
        newBase.letin(bv |> recv, av, apply(body), rect(body.typ))
      case Abs(bv, body) if bv.name == "$BYNAME$" => newBase.byName(apply(body))
      case Abs(bv, body) =>
        bv.typ.tpe match {
          case RecordType(fields @ _*) =>
            val params = fields map {case(n,t) => n -> bindVal(n, t, bv.annots)} toMap;
            val adaptedBody = bottomUpPartial(body) {
              case RepDef(RecordGet(RepDef(`bv`), name, _)) => readVal(params(name))
            }
            newBase.lambda(params.valuesIterator map recv toList, apply(adaptedBody))
          case _ =>
            newBase.lambda({ recv(bv)::Nil }, apply(body))
        }
      case MethodApp(self, mtd, targs, argss, tp) =>
        val typ = newBase.loadTypSymbol(ruh.encodedTypeSymbol(mtd.owner.asType))
        val alts = mtd.owner.typeSignature.member(mtd.name).alternatives
        val newMtd = newBase.loadMtdSymbol(typ, mtd.name.toString, if (alts.isEmpty) None else Some(alts.indexOf(mtd)), mtd.isStatic)
        newBase.methodApp(
          apply(self),
          newMtd,
          targs map (t => rect(t)),
          argss map (_.map(newBase)(a => apply(a))),
          rect(tp))
      case StaticModule(fullName) => newBase.staticModule(fullName)
      case Module(pre, name, typ) => newBase.module(apply(pre), name, rect(typ))
      case NewObject(tp) => newBase.newObject(rect(tp))
      case bv @ BoundVal(name) => bound andThen newBase.readVal applyOrElse (bv, extrudedHandle)
      case Ascribe(r,t) => newBase.ascribe(apply(r), rect(t))
      case h @ Hole(n) =>
        newBase.hole(n, rect(h.typ))
        
      //case RecordGet(RepDef(bv), name, tp) =>
      //  //???
        
    }
    //protected def recv(bv: BoundVal) = newBase.bindVal(bv.name, rect(bv.typ), bv.annots map {case(tp,ass) => rect(tp) -> ass.map(_.map(newBase)(a => apply(a)))}) and (bound += bv -> _)
    protected def recv(bv: BoundVal) = newBase.bindVal(
      bv.name IfNot (_ startsWith "$") Else "x",
      rect(bv.typ),
      bv.annots map {case(tp,ass) => rect(tp) -> ass.map(_.map(newBase)(a => apply(a)))}
    ) and (bound += bv -> _)
    
    def rect(r: TypeRep): newBase.TypeRep = reinterpretType(r, newBase)
    
  }
  object Reinterpreter {
    def apply(NewBase: Base)(app: (Rep, Def => NewBase.Rep) => NewBase.Rep, ExtrudedHandle: (BoundVal => NewBase.Rep)) =
      new Reinterpreter {
        val newBase: NewBase.type = NewBase
        def apply(r: Rep) = app(r, apply)
        override val extrudedHandle = ExtrudedHandle
      }
  }
  
  
  /* --- --- --- Specialized Scala reinterpretation, reverting virtualized constructs --- --- --- */
  
  override def scalaTreeIn(MBM: MetaBases)(SRB: MBM.ScalaReflectionBase, rep: Rep, ExtrudedHandle: (BoundVal => MBM.u.Tree)): MBM.u.Tree = muteFor {
    new ReinterpreterToScala {
      val MetaBases: MBM.type = MBM
      val newBase: SRB.type = SRB
      override val extrudedHandle = ExtrudedHandle
    } apply rep
  }
  abstract class ReinterpreterToScala extends Reinterpreter {
      val MetaBases: MetaBases
      import MetaBases.u._
      val newBase: MetaBases.ScalaReflectionBase
      def apply(r: Rep) = apply(dfn(r))
      
      override val ascribeBoundValsWhenNull = true
      
      //protected lazy val NothingType = Predef.implicitType[Nothing]
      protected lazy val NullType = Predef.implicitType[Null] // Note: `irTypeOf[Null]` doesn't work because the implicit in Predef is not imported
      protected lazy val AnyRef = Predef.implicitType[AnyRef]
      
      /** Remembers mutable variable bindings */
      val boundVars = collection.mutable.Map[BoundVal, TermName]()
      
      import Quasiquotes.QuasiContext
      
      //override def rect(r: TypeRep): newBase.TypeRep = {
      //  //println("> "+r)
      //  super.rect(r) //and (println)
      //}
      
      override def apply(d: Def) = {
        
        object BV { def unapply(x: IR[_,_]) = x match {
          case IR(RepDef(bv: BoundVal)) => Some(bv)
          case _ => None }}
        
        object ScalaVar { def unapply(r: IR[lib.Var[_],_]): Option[Ident] = r match {
          case ir"($v: squid.lib.Var[$tv])" => apply(v.rep) match {
            case id @ Ident(_) => Some(id)
            case v =>
              // TODO handle gracefully? (by implementing the expected semantics?)
              throw IRException(s"Cannot de-virtualize usage of Var not associated with a local identifier: $d")
          }
          case _ => None }}
        
        //`internal IR`[Any,Nothing](ast.rep(d)) match {
        `internal IR`[Any,Nothing](ast.simpleRep(d)) match {
    
          //case MethodApp(se, sy, ts, ArgList(effs @ _*) :: Args(res) :: Nil, tp) if sy === ImpSym =>
          //  lazy val r = apply(res)
          //  if (effs isEmpty) r
          //  else q"..${effs map apply}; $r"
          case ir"squid.lib.Imperative(${effs @ __*})($res: $t)" =>
            lazy val r = apply(res.rep)
            if (effs isEmpty) r
            else q"..${effs map (e => apply(e.rep))}; $r"
    
          case ir"if ($cond) $thn else $els : $t" => q"if (${apply(cond rep)}) ${apply(thn rep)} else ${apply(els rep)}"
    
          case ir"while ($cond) $body" => q"while(${apply(cond rep)}) ${apply(body rep)}"
    
          case IR(RepDef(h: Hole)) =>
            h.originalSymbol flatMap (os => boundVars get os) map (n => Ident(n)) getOrElse super.apply(d)
    
          case ir"(${ScalaVar(id)}: squid.lib.Var[$tv]) := $value" => q"$id = ${apply(value.rep)}"
          case ir"(${ScalaVar(id)}: squid.lib.Var[$tv]) !" => id
          case ir"$v: squid.lib.Var[$tv]" if !(v.typ <:< NullType) =>
            System.err.println(s"Virtualized variable `${v rep}` of type `${tv}` escapes its defining scope!")
            val r = super.apply(dfn(v rep))
            //System.err.println("Note: "+showCode(r))
            r
            
          //case ir"var $v: $tv = $init; $body: $tb" =>
          case ir"var ${v @ BV(bv)}: $tv = $init; $body: $tb" =>
            val varName = newBase.freshName(bv.name IfNot (_ startsWith "$") Else "v")
            
            //q"var $varName: ${rect(tv rep)} = ${apply(init.rep)}; ${body subs 'v -> q"$varName"}"
            /* ^ this does not work because `subs` wants to substitute with something of IR type of the current base, not a Scala tree */
            
            q"var $varName: ${rect(tv.rep/*.tpe.typeArgs.head*/)} = ${apply(init.rep)}; ..${
              boundVars += bv -> varName
              apply(body.rep)}" // Note: if subs took a partial function, we could avoid the need for a `boundVars` context
            
          // The rule above will not match if `tv` is not a subtype of AnyRef: value null will extract type Null that will not merge successfully
          case ir"var ${v @ BV(bv)}: $tv = null; $body: $tb" =>
            assert(!(tv <:< AnyRef))
            System.err.println(s"Warning: variable `${v rep}` of type `${tv}` (not a subtype of `AnyRef`) is assigned `null`.")
            val varName = newBase.freshName(bv.name IfNot (_ startsWith "$") Else "v")
            q"var $varName: ${rect(tv.rep)} = null; ..${
              boundVars += bv -> varName
              apply(body.rep)}"
            
            
          /** Converts redexes to value bindings, nicer to the eye.
            * Note: we don't do it for multi-parameter lambdas, assuming they are already normalized (cf: `BindingNormalizer`) */
          case ir"((${p @ BV(bv)}: $ta) => $body: $tb)($arg)" =>
            val a = apply(arg rep)
            val av = if (ascribeBoundValsWhenNull && arg.rep.typ <:< ruh.Null) newBase.ascribe(a, rect(bv.typ)) else a
            newBase.letin(recv(bv), av, apply(body subs 'p -> p rep), rect(tb rep))
            
            
          /** We explicitly convert multi-parameter lambdas, as they may be encoded (cf: `CurryEncoding`): */
            
          case ir"() => $body: $tb" =>
            newBase.lambda(Nil, apply(body rep))
            
          case ir"((${p @ BV(bp)}: $tp, ${q @ BV(bq)}: $tq) => $body: $tb)" =>
            newBase.lambda(recv(bp) :: recv(bq) :: Nil, apply(body  subs 'p -> p subs 'q -> q rep))
            
          case ir"((${p @ BV(bp)}: $tp, ${q @ BV(bq)}: $tq, ${r @ BV(br)}: $tr) => $body: $tb)" =>
            newBase.lambda(recv(bp) :: recv(bq) :: recv(br) :: Nil, apply(body  subs 'p -> p subs 'q -> q subs 'r -> r rep))
            
          case ir"((${p @ BV(bp)}: $tp, ${q @ BV(bq)}: $tq, ${r @ BV(br)}: $tr, ${s @ BV(bs)}: $ts) => $body: $tb)" =>
            newBase.lambda(recv(bp) :: recv(bq) :: recv(br) :: recv(bs) :: Nil, apply(body  subs 'p -> p subs 'q -> q subs 'q -> q subs 's -> s rep))

          case ir"((${p @ BV(bp)}: $tp, ${q @ BV(bq)}: $tq, ${r @ BV(br)}: $tr, ${s @ BV(bs)}: $ts, ${t @ BV(bt)}: $tt) => $body: $tb)" =>
            newBase.lambda(recv(bp) :: recv(bq) :: recv(br) :: recv(bs) :: recv(bt) :: Nil, apply(body  subs 'p -> p subs 'q -> q subs 'q -> q subs 's -> s subs 't -> t rep))
            
            
          case ir"($f: $ta => $tb)($arg)" =>
            q"${apply(f rep)}(${apply(arg rep)})"
            
          case IR(RepDef(RecordGet(RepDef(bv), name, tp))) =>
            Ident(TermName(s"<access to param $name>"))
            // ^ It can be useful to visualize these nodes when temporarily extruded from their context,
            // although in a full program we should never see them!
            
          case _ => super.apply(d)
        }
    }
      
  }
  
  
}
