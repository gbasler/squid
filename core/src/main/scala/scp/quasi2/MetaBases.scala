package scp
package quasi2

import utils._
import lang2._
import scp.utils.meta.{RuntimeUniverseHelpers => ruh}
import ruh.sru

trait MetaBases {
  type U = u.type
  val u: scala.reflect.api.Universe
  import u._
  
  object Helpers extends {val uni: u.type = u} with meta.UniverseHelpers[u.type]
  import Helpers.TreeOps
  
  def freshName(hint: String): TermName
  def curatedFreshName(hint: String): TermName = freshName(hint replace('$', '_'))
  
  /** Base that generates the Scala code necessary to construct the DSL program.
    * This class let-binds type symbols, method symbols, type applications and modules: the corresponding definitions
    * are stored in the `symbols` mutable buffer.
    * Note: these are let-bound but not cached – caching is assumed to be performed by the caller (eg: ModularEmbedding). */
  class MirrorBase(Base: Tree) extends Base {
    /* TODO: more clever let-binding so defs used once won't be bound? -- that would require changing `type Rep = Tree` to something like `type Rep = () => Tree` */
    import scala.collection.mutable
    
    /** For legibility of the gen'd code, calls `mapp` instead of `methodApp`.
      * Note: maybe it will make the gen'd pgrm slower (cf: more Seq/List ction?) */
    val shortAppSyntax = true
    //val shortAppSyntax = false
    
    val symbols = mutable.Buffer[(TermName, Tree)]()
    def mkSymbolDefs = symbols map { case(n, t) => q"val $n = $t" }
    
    
    type Rep = Tree
    /** (originalName, valName[in gen'd code], type) */
    type BoundVal = (String, TermName, TypeRep, List[Annot])
    type TypeRep = Tree
    
    type MtdSymbol = Tree
    type TypSymbol = (String, Tree) // (nameHint, exprTree)
    
    
    def repEq(a: Rep, b: Rep): Boolean = a == b
    def typLeq(a: TypeRep, b: TypeRep): Boolean = ???
    
    
    def loadTypSymbol(fullName: String): TypSymbol = {
      val nameHint = fullName.drop(fullName.lastIndexOf('.')+1)
      val n = curatedFreshName(nameHint+"Sym")
      val s = q"$Base.loadTypSymbol($fullName)"
      symbols += n -> s
      nameHint -> q"$n"
    }
    
    def loadMtdSymbol(typ: TypSymbol, symName: String, index: Option[Int], static: Boolean = false): MtdSymbol = {
      val n = curatedFreshName(symName)
      val s = q"$Base.loadMtdSymbol(${typ._2}, $symName, $index, $static)"
      symbols += n -> s
      q"$n"
    }
    
    def bindVal(name: String, typ: TypeRep, annots: List[Annot]): BoundVal =
      //TermName(name) -> typ // We assume it's find without a fresh name, since the binding structure should reflect that of the encoded program
      (name, TermName("_$"+name), typ, annots) // Assumption: nobody else uses names of this form... (?)
    def readVal(v: BoundVal): Rep = q"$Base.readVal(${v._2})"
    
    def const(value: Any): Rep = q"$Base.const(${Constant(value)})"
    
    def annot(a: Annot) = q"${a._1} -> ${mkNeatList(a._2 map argList)}"
    
    def lambda(params: List[BoundVal], body: => Rep): Rep = q"""
      ..${params map { case (on, vn, vt, anns) => q"val $vn = $Base.bindVal($on, $vt, ${mkNeatList(anns map annot)})" }}
      $Base.lambda(${mkNeatList(params map (_._2) map Ident.apply)}, $body)
    """
    
    override def letin(bound: BoundVal, value: Rep, body: => Rep, bodyType: TypeRep): Rep = q"""
      val ${bound._2} = $Base.bindVal(${bound._1}, ${bound._3}, ${mkNeatList(bound._4 map annot)})
      $Base.letin(${bound._2}, $value, $body, $bodyType)
    """
    
    
    def newObject(tp: TypeRep): Rep = q"$Base.newObject($tp)"
    
    def moduleObject(fullName: String, isPackage: Boolean): Rep = {
      val n = curatedFreshName(fullName.drop(fullName.lastIndexOf('.')+1))
      symbols += n -> q"$Base.moduleObject($fullName, $isPackage)"
      q"$n"
    }
    def staticModule(fullName: String): Rep = {
      val n = curatedFreshName(fullName.drop(fullName.lastIndexOf('.')+1))
      symbols += n -> q"$Base.staticModule($fullName)"
      q"$n"
    }
    def module(prefix: Rep, name: String, typ: TypeRep): Rep = q"$Base.module($prefix, $name, $typ)"
    
    
    def argList(argl: ArgList): Tree = argl match {
      case Args(reps @ _*) => q"$Base.Args(..$reps)"
      case ArgsVarargs(args, varargs) => q"$Base.ArgsVarargs(${argList(args)}, ${argList(varargs)})"
      case ArgsVarargSpliced(args, vararg) => q"$Base.ArgsVarargSpliced(${argList(args)}, $vararg)"
    }
    
    def methodApp(self: Rep, mtd: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep = {
      val as = argss map argList
      if (shortAppSyntax) q"$Base.mapp($self, $mtd, $tp)(..$targs)(..$as)"
      else q"$Base.methodApp($self, $mtd, ${mkNeatList(targs)}, ${mkNeatList(as)}, $tp)"
    }
    
    def byName(arg: => Rep) = q"$Base.byName($arg)"
    
    
    //def uninterpretedType[A: sru.TypeTag]: TypeRep = q"$Base.uninterpretedType[${typeOf[A]}]"
    def uninterpretedType[A: sru.TypeTag]: TypeRep = { // Type tags can be very hairy, so we let-bind them
      val n = curatedFreshName(typeOf[A].typeSymbol.name.toString)
      symbols += n -> q"$Base.uninterpretedType[${typeOf[A]}]"
      q"$n"
    }
    
    
    def typeApp(self: TypeRep, typ: TypSymbol, targs: List[TypeRep]): TypeRep = {
      val n = curatedFreshName(typ._1)
      symbols += n -> q"$Base.typeApp($self, ${typ._2}, ${mkNeatList(targs)})"
      q"$n"
    }
    def staticTypeApp(typ: TypSymbol, targs: List[TypeRep]): TypeRep = {
      val n = curatedFreshName(typ._1)
      symbols += n -> q"$Base.staticTypeApp(${typ._2}, ${mkNeatList(targs)})"
      q"$n"
    }
    
    def recordType(fields: List[(String, TypeRep)]): TypeRep = ???
    
    def constType(value: Any, underlying: TypeRep): TypeRep = q"$Base.constType(${Constant(value)}, $underlying)"
    
    
    
    
    def mkNeatList(xs: Seq[Tree]) = xs match {
      case Seq() => q"scala.Nil"
      case _ => q"scala.List(..$xs)"
    }
    
    
    
    def hole(name: String, typ: TypeRep): Rep = q"$Base.hole($name, $typ)"
    def splicedHole(name: String, typ: TypeRep): Rep = q"$Base.splicedHole($name, $typ)"
    
    def substitute(r: Rep, defs: Map[String, Rep]): Rep = if (defs isEmpty) r else
      q"$Base.substitute($r, ..${defs map {case (name, rep) => q"$name -> $rep"}})"
    /* 
    /** Note: We could implement the method above by actually doing substitution in the Scala tree as below,
      * but that would break the semantics of `MirrorBase`, which is really to forward operations instead of applying
      * them eagerly. So we generate actual substitution code instead. */
    def substitute(r: Rep, defs: Map[String, Rep]): Rep = r transform {
      case h @ q"$Base.hole(${Literal(Constant(name: String))}, $typ)" =>
        defs.getOrElse(name, h)
    }
    */
    
    def typeHole(name: String): TypeRep = q"$Base.typeHole($name)"
    
    
    override def ascribe(self: Rep, typ: TypeRep): Rep = q"$Base.ascribe($self, $typ)"
    
    object Const extends ConstAPI {
      def unapply[T: sru.TypeTag](ir: IR[T,_]): Option[T] = ir match {
        case q"$b.const[$typ](${Literal(Constant(value))})" if sru.typeOf[T] <:< typ.tpe.asInstanceOf[sru.Type] => // TODO check base?
          Some(value.asInstanceOf[T])
        case _ => None
      }
    }
    
  }
  
  
  
  
  
  /** Base that simply outputs the Scala tree representation of the DSL program.
    * It does not add types to the trees, although it could (to some extent). */
  class ScalaReflectionBase extends Base {
    
    val startPathsFromRoot = false
    
    type Rep = Tree
    type BoundVal = (TermName, TypeRep)
    type TypeRep = Tree
    
    type MtdSymbol = TermName
    type TypSymbol = () => sru.TypeSymbol // to delay computation, since we won't need most of them! (only needed in typeApp)
    
    
    def repEq(a: Rep, b: Rep): Boolean = a == b
    def typLeq(a: TypeRep, b: TypeRep): Boolean = ???
    
    
    def loadTypSymbol(fullName: String): TypSymbol = () => ir2.RuntimeSymbols.loadTypSymbol(fullName)
    
    def loadMtdSymbol(typ: TypSymbol, symName: String, index: Option[Int], static: Boolean = false): MtdSymbol =
      TermName(symName)
    
    def bindVal(name: String, typ: TypeRep, annots: List[Annot]): BoundVal = TermName(name) -> typ  // Assumption: it will be fine to use the unaltered name here
    //def bindVal(name: String, typ: TypeRep): BoundVal = TermName("_$"+name) -> typ
    
    def readVal(v: BoundVal): Rep = q"${v._1}"
    def const(value: Any): Rep = value match {
      case cls: Class[_] => q"_root_.scala.Predef.classOf[${ruh.srum.classSymbol(cls).asInstanceOf[u.Symbol]}]" // FIXME
      case _ => Literal(Constant(value))
    }
    def lambda(params: List[BoundVal], body: => Rep): Rep = q"""
      (..${params map { case (vn, vt) => q"val $vn: $vt" }}) => $body
    """
    
    override def letin(bound: BoundVal, value: Rep, body: => Rep, bodyType: TypeRep): Rep = q"""
      val ${bound._1}: ${bound._2} = $value
      ..$body
    """
    
    def newObject(tp: TypeRep): Rep = q"new $tp"
    def moduleObject(fullName: String, isPackage: Boolean): Rep = {
      val path = fullName.splitSane('.').toList
      path.tail.foldLeft(q"${TermName(path.head)}":Tree)((acc,n) => q"$acc.${TermName(n)}")
    }
    def staticModule(fullName: String): Rep = {
      val path = fullName.splitSane('.').toList
      path.tail.foldLeft(q"${TermName(path.head)}":Tree)((acc,n) => q"$acc.${TermName(n)}")
    }
    def module(prefix: Rep, name: String, typ: TypeRep): Rep = q"$prefix.${TermName(name)}"
    def methodApp(self: Rep, mtd: MtdSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep =
      q"$self.${mtd}[..$targs](...${
        def quote(argl: ArgList): Seq[Tree] = argl match {
          case Args(reps @ _*) => reps
          case ArgsVarargs(args, varargs) => quote(args) ++ quote(varargs)
          case ArgsVarargSpliced(args, vararg) => quote(args) :+ q"$vararg: _*"
        }
        argss map quote})" // Note: could also call  internal.setType ...?
    
    def byName(arg: => Rep): Rep = arg
    
    def uninterpretedType[A: sru.TypeTag]: TypeRep = tq"${typeOf[A]}"
    
    def typeApp(self: TypeRep, typ: TypSymbol, targs: List[TypeRep]): TypeRep = {
      val tp = typ()
      val pre = self match {
        case tq"$t.type" => t
        case _ => self
      }
      if (tp.isModuleClass) {
        assert(targs.isEmpty, s"Non-empty targs $targs")
        tq"$pre.${TermName(tp.name.toString)}.type"
      }
      else tq"$pre.${TypeName(tp.name.toString)}[..$targs]"
    }
    def staticTypeApp(typ: TypSymbol, targs: List[TypeRep]): TypeRep = {
      val tp = typ()
      var nameChain = ((Iterator iterate tp.owner)(_.owner) takeWhile (s => s.name.toString != "<root>")
        map (s => TermName(s.name.toString)) foldLeft List.empty[TermName]) (_.::(_))
      if (startPathsFromRoot) nameChain ::= TermName("_root_")
      val path = nameChain.tail.foldLeft(Ident(nameChain.head):Tree){ (p, n) => q"$p.$n" }
      typeApp(path, () => tp, targs)
    }
    
    def recordType(fields: List[(String, TypeRep)]): TypeRep = ??? // TODO
    
    def repType(r: Rep): TypeRep = ??? // TODO impl (store types using internal)
    
    
    def hole(name: String, typ: TypeRep): Rep = q"${TermName(name)}" // TODO ensure hygiene... (this should not clash!)
    def splicedHole(name: String, typ: TypeRep): Rep = q"${TermName(name)}: _*"
    
    def substitute(r: Rep, defs: Map[String, Rep]): Rep = r transform {
      //case h @ q"${TermName(name)}" =>  // Weird! this does not seem to match...
      case h @ Ident(TermName(name)) =>
        defs.getOrElse(name, h)
    }
    
    def typeHole(name: String): TypeRep = tq"${TypeName(name)}"
    
    def constType(value: Any, underlying: TypeRep): TypeRep = tq"$underlying"
    
    
    
    override def ascribe(self: Rep, typ: TypeRep): Rep = q"$self: $typ"
    
    object Const extends ConstAPI {
      def unapply[T: sru.TypeTag](ir: IR[T,_]): Option[T] = ir match {
        case Literal(Constant(v)) => Some(v.asInstanceOf[T]) // TODO check type?
        case _ => None
      }
    }
    
    
  }
}

object MetaBases {
  
  object Runtime extends MetaBases {
    val u: sru.type = sru
    
    def freshName(hint: String) = sru.TermName(hint+"$__")
    
    object ScalaReflectionBase extends Runtime.ScalaReflectionBase
    
  }
  
}










