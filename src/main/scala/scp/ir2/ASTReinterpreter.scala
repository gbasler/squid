package scp
package ir2

import lang2._
import scp.quasi2.MetaBases
import utils._
import utils.meta.{RuntimeUniverseHelpers => ruh}
import ruh.sru

import scala.collection.mutable

trait ASTReinterpreter { ast: AST =>
  
  
  /* --- --- --- Node Reinterpretation --- --- --- */
  
  
  def reinterpret(r: Rep, newBase: Base)(extrudedHandle: (BoundVal => newBase.Rep) = DefaultExtrudedHandler): newBase.Rep =
    Reinterpreter(newBase)((r, f) => f(dfn(r)), extrudedHandle)(r)
  
  
  /** TODO cache symbols (use forwarder?) */
  trait Reinterpreter {
    val newBase: Base
    def apply(r: Rep): newBase.Rep
    
    val extrudedHandle: BoundVal => newBase.Rep = bv => wth(s"Extruded variable: $bv")
    
    //def getClassName(cls: sru.ClassSymbol) = ruh.srum.runtimeClass(cls).getName
    
    protected val bound = mutable.Map[BoundVal, newBase.BoundVal]()
    
    protected def apply(d: Def): newBase.Rep = d match {
        
      case cnst @ Constant(v) => newBase.const(v)
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
      case ModuleObject(fullName, isPackage) => newBase.moduleObject(fullName, isPackage)
      case Module(pre, name, typ) => newBase.module(apply(pre), name, rect(typ))
      case NewObject(tp) => newBase.newObject(rect(tp))
      case bv @ BoundVal(name) => bound andThen newBase.readVal applyOrElse (bv, extrudedHandle)
      case Ascribe(r,t) => newBase.ascribe(apply(r), rect(t))
      case h @ Hole(n) =>
        newBase.hole(n, rect(h.typ))
        
      //case RecordGet(RepDef(bv), name, tp) =>
      //  //???
        
    }
    protected def recv(bv: BoundVal) = newBase.bindVal(bv.name, rect(bv.typ), bv.annots map {case(tp,ass) => rect(tp) -> ass.map(_.map(newBase)(a => apply(a)))}) and (bound += bv -> _)
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
  
  override def scalaTreeIn(MBM: MetaBases)(SRB: MBM.ScalaReflectionBase, rep: Rep, ExtrudedHandle: (BoundVal => MBM.u.Tree)): MBM.u.Tree = {
    import MBM.u._
    
    lazy val NothingType = Predef.implicitType[Nothing]
    
    new Reinterpreter {
      val newBase: SRB.type = SRB
      def apply(r: Rep) = apply(dfn(r))
      override val extrudedHandle = ExtrudedHandle
      
      /** Remembers mutable variable bindings */
      val boundVars = collection.mutable.Map[BoundVal, TermName]()
      
      import Quasiquotes.QuasiContext
      
      override def apply(d: Def) = {
        
        object ScalaVar {
          def unapply(r: IR[lib.Var[_],_]): Option[Ident] = r match {
            case ir"($v: scp.lib.Var[$tv])" => apply(v.rep) match {
              case id @ Ident(_) => Some(id)
              case v =>
                // TODO handle gracefully? (by implementing the expected semantics?)
                throw IRException(s"Cannot de-virtualize usage of Var not associated with a local identifier: $d")
            }}}
        
        `internal IR`[Any,Nothing](ast.rep(d)) match {
    
          //case MethodApp(se, sy, ts, ArgList(effs @ _*) :: Args(res) :: Nil, tp) if sy === ImpSym =>
          //  lazy val r = apply(res)
          //  if (effs isEmpty) r
          //  else q"..${effs map apply}; $r"
          case ir"scp.lib.Imperative(${effs @ __*})($res: $t)" =>
            lazy val r = apply(res.rep)
            if (effs isEmpty) r
            else q"..${effs map (e => apply(e.rep))}; $r"
    
          case ir"if ($cond) $thn else $els : $t" => q"if (${apply(cond rep)}) ${apply(thn rep)} else ${apply(els rep)}"
    
          case ir"while ($cond) $body" => q"while(${apply(cond rep)}) ${apply(body rep)}"
    
          case IR(RepDef(h: Hole)) =>
            h.originalSymbol flatMap (os => boundVars get os) map (n => Ident(n)) getOrElse super.apply(d)
    
          case ir"(${ScalaVar(id)}: scp.lib.Var[$tv]) := $value" => q"$id = ${apply(value.rep)}"
          case ir"(${ScalaVar(id)}: scp.lib.Var[$tv]) !" => id
          case ir"$v: scp.lib.Var[$tv]" if !(v.typ <:< NothingType) =>
            System.err.println(s"Virtualized variable `${v rep}` escapes its defining scope!")
            val r = super.apply(dfn(v rep))
            //System.err.println("Note: "+showCode(r))
            r
            
          //case ir"var $v: $tv = $init; $body: $tb" =>
          case ir"var ${v @ IR(RepDef(bv: BoundVal))}: $tv = $init; $body: $tb" =>
            val varName = MBM.freshName(bv.name)
            
            //q"var $varName: ${rect(tv rep)} = ${apply(init.rep)}; ${body subs 'v -> q"$varName"}"
            /* ^ this does not work because `subs` wants to substitute something or IR type of the current base, not a Scala tree */
            
            q"var $varName: ${rect(tv.rep/*.tpe.typeArgs.head*/)} = ${apply(init.rep)}; ..${
              boundVars += bv -> varName
              apply(body.rep)}" // Note: if subs took a partial function, we could avoid the need for a `boundVars` context
            
          case ir"((${p @ IR(RepDef(bv: BoundVal))}: $ta) => $body: $tb)($arg)" =>
            newBase.letin(recv(bv), apply(arg rep), apply(body subs 'p -> p rep), rect(tb rep))
            
          /** The following is not necessary, as Reinterpreter already handles multi-param lambdas... but why doesn't it match?? */
          //case ir"((${p @ IR(RepDef(bv: BoundVal))}: $ta, q: $tb) => $body: $tr)($a, $b)" =>
          //  newBase.letin(recv(bv), apply(a rep), apply(ir"((q: $tb) => $body)($b)" subs 'p -> p rep), rect(tb rep))
          case ir"((p: $ta, q: $tb) => $body: $tr)($a, $b)" => ??? // FIXME this never matches!
          case ir"((p: $ta, q: $tb) => $body: $tr)" => ??? // FIXME
            
          case ir"($f: $ta => $tb)($arg)" =>
            q"${apply(f rep)}(${apply(arg rep)})"
            
          case IR(RepDef(RecordGet(RepDef(bv), name, tp))) =>
            Ident(TermName(s"<access to param $name>"))
            // ^ It can be useful to visualize these nodes when temporarily extruded from their context,
            // although in a full program we should never see them!
            
          case _ => super.apply(d)
        }
      }
      
      //lazy val ImpSym = loadMtdSymbol(loadTypSymbol("scp.lib.package$"), "Imperative", None)
      
    } apply rep
  }
  
  
}
