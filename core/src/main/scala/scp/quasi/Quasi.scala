package scp
package quasi

import annotation.StaticAnnotation
import collection.mutable
import scala.language.experimental.macros
import reflect.macros.TypecheckException
import reflect.macros.whitebox.Context

import lang._
import utils.MacroUtils._

/**
  * TODO: fix the interpretation of holes -- names like ??? are seen as beginning with a $ sign! (use .decodedName?...)
  */
trait Quasi[B <: Base, L] {
  val base: B
  import base._
  
  implicit class QuasiContext(private val ctx: StringContext) {
    
    object dsl {
      def apply(t: Any*): Any = macro QuasiMacro.applyImpl[L]
      @QuasiMacro.Ext def unapply(t: Any): Any = macro QuasiMacro.unapplyImpl[L]
    }
    
    object dbgdsl {
      @MacroSetting(debug = true) def apply(t: Any*): Any = macro QuasiMacro.applyImpl[L]
      @MacroSetting(debug = true) @QuasiMacro.Ext def unapply(t: Any): Any = macro QuasiMacro.unapplyImpl[L]
    }
    
    object test {
      @MacroSetting(debug = true) @QuasiMacro.Ext def unapply[A,S](t: Q[A,S]): Any = macro QuasiMacro.unapplyImpl[L]
      //@MacroSetting(debug = true) @QuasiMacro.Ext def unapply(t: Q[Int,{}]): Option[Q[Int,{}]] = ???
    }
    
  }
}
object Quasi {
  
}
trait QuasiBase[L] extends Quasi[Base, L] { self: Base =>
  override val base: this.type = this
}

object QuasiMacro {
  
  private[scp] class Ext extends StaticAnnotation
  
  val dslInterpolators = Set("dsl", "dbgdsl", "exp", "dbgexp")
  
}
class QuasiMacro(val c: Context) extends utils.MacroShared {
  import QuasiMacro._
  
  type Ctx = c.type
  val Ctx: Ctx = c
  
  def unapplyImpl[L: c.WeakTypeTag](t: c.Tree) = {
    import c.universe._
    
    /** [INV:Quasi:reptyp]: we only match Quoted[_,_] types */
    val sym = c.typecheck(tq"Quoted[Any,Any]", c.TYPEmode).tpe.typeSymbol
    if (sym.fullName != t.tpe.typeSymbol.fullName) {
      throw EmbeddingException(s"Cannot match type '${t.tpe.typeSymbol.fullName}', which is different from Quoted type '${sym.fullName}[_,_]'."
      +"\nTry matching { case x: Rep[_] => ... } first.")
    }
    
    applyImpl[L](t)
  }
  
  def applyImpl[L: c.WeakTypeTag](t: c.Tree*) = {
    import c.universe._
    
    val mc = MacroDebugger[c.type](c)
    val debug = { mc[MacroDebug] }
    
  try {
    
    val unapply = c.macroApplication.symbol.annotations.exists(_.tree.tpe <:< typeOf[quasi.QuasiMacro.Ext])
    
    //debug(c.macroApplication)
    val quasiBase = c.macroApplication match {
      //case x @ q"$base.QuasiContext(scala.StringContext.apply(..${ Literal(Constant(head)) :: args }).${id @ TermName(name)}(..$args2)"
      case x @ q"$base.QuasiContext(scala.StringContext.apply(..$_)).${id @ TermName(name)}.apply(..$_)"
      if dslInterpolators(name)
      =>
        debug("Found ction base: "+base)
        base
      case x @ q"$base.QuasiContext(scala.StringContext.apply(..$_)).${id @ TermName(name)}.unapply($_)"
      if dslInterpolators(name)
      =>
        debug("Found xtion base: "+base)
        base
      //case _ => null
    }
    
    val base = c.typecheck(q"$quasiBase.base")
    
    //val base = internal.setType(q"$quasiBase.base", quasiBase.tpe.member(TermName("base")).typeSignature)
    // ^ gets weird types like:  => scp.quasi.QuasiBase.<refinement>.type  which break everything 
    
    
    val builder = new PgrmBuilder[c.type](c)(unapply)
    
    //debug("Built:", showCode(builder.tree))
    
    var holes: List[Either[TermName,TypeName]] = Nil // Left: value hole; Right: type hole
    val splicedHoles = mutable.Set[TermName]()
    
    var unquotedTypes = List[(TypeName, Type, Tree)]() // fresh name; type; type rep tree
    
    lazy val QTSym = symbolOf[Base#QuotedType[_]]
    lazy val VarargSym = symbolOf[Base].typeSignature.member(TypeName("__*").encodedName)
    //lazy val VarargSym = symbolOf[BaseDefs#__*] // nope: gets 'trait Seq'
    
    def unquoteType(name: TypeName, tp: Type, tree: Tree) = tp.baseType(QTSym) match {
      case TypeRef(tpbase,QTSym,tp::Nil) if tpbase =:= base.tpe =>
        unquotedTypes ::= ((name.toTypeName, tp, tree))
        tq"$tp"
      case TypeRef(_,_,_) => throw EmbeddingException(s"Cannot unquote type '$tp': it is not from base $base.")
      case _ => throw EmbeddingException(s"Cannot unquote type '$tp': it is not a QuotedType[_].")
    }
    
    val pgrm = builder.tree transform { // TODO move this into SimpleEmbedding?
        
      //case Assign(Ident(name), value) if quasi.holes.contains(name) =>
      //  val h = quasi.holes(name)
      //  val tpe = h.tree.tpe match {
      //    case tpe @ TypeRef(pre, name, args) if tpe <:< typeOf[PardisVar[Any]] =>
      //      holeTrees += h.tree
      //      args.head
      //    case _ => c.abort(c.enclosingPosition, s"Assignment to a non-Var[_], in `$t`")
      //  }
      //  q"""$anf.outsideVar.update[${TypeTree(tpe)}](${holeTrees.size-1}, ${transform(value)})"""
        
      case Ident(name: TermName) if builder.holes.contains(name) =>
        val h = builder.holes(name)
        
        //debug("HOLE: "+h)
        
        if (unapply) {
          //q"holeExtract()"
          //val n = h.name.filter(_.toString != "_").map(n => q"$n").getOrElse(h.tree)
          val n = h.name.filter(_.toString != "_") map (_.toTermName) getOrElse // hygiene? (can taking the name as specified by the user make collisions?)
            c.freshName(TermName("ANON_HOLE"))
          holes ::= Left(n)
          if (h.vararg) splicedHoles += n
          else h.tree match { case pq"$p @ __*" => splicedHoles += n   case _ => }
          q"$n"
          
        } else {
          h.tree match {
            //case t @ q"$_: _*" => q"spliceVarargs($t): _*"  // not actually useful...
            case q"$t: _*" => q"spliceVararg($t): _*"
            case q"($t: $tp)" if (tp.tpe match {
              case TypeRef(btp, sym, Nil) => (btp =:= base.tpe) && sym == VarargSym
              case _ => false
            }) => q"spliceVararg($t): _*"
            case t if h.vararg => q"spliceVararg($t): _*"
            case t => q"unquote($t)"
          }
        }
        
      case Ident(name: TypeName) if builder.holes.contains(name.toTermName) => // in case we have a hole in type position ('name' is a TypeName but 'holes' only uses TermNames)
        val hole = builder.holes(name.toTermName)
        if (hole.vararg) throw EmbeddingException(s"Varargs are not supported in type position.${showPosition(hole.tree.pos)}") // (for hole '${hole.tree}').")
        if (unapply) {
          val n = hole.name.filter(_.toString != "_")
            .getOrElse(throw EmbeddingException("All extracted types should be named.")).toTypeName // TODO B/E // TODO relax? // TODO catch!
          holes ::= Right(n)
          tq"$n"
        }
        else { // apply
          unquoteType(name.toTypeName, hole.tree.tpe, hole.tree)
        }
        
      //case t @ q"$$(..$args)" if unapply => throw EmbeddingException(s"Unsupported alternative unquoting syntax in unapply position: '$t'")
      case t @ q"$$(..$args)" => // alternative unquote syntax
        q"(..${args map (a => q"unquote($a)")})"
        
      case t @ Ident(name: TermName) if name.decodedName.toString.startsWith("$") => // escaped unquote syntax
        //q"open(${name.toString})"
        val bareName = name.toString.tail
        if (bareName.isEmpty) throw EmbeddingException(s"Empty escaped unquote name: '$$$t'")
        if (unapply) {
          q"unquote(${TermName(bareName)})" // escaped unquote in unapply mode does a normal unquote
        } else { // apply
          holes ::= Left(TermName(bareName)) // holes in apply mode are interpreted as free variables
          q"${TermName(bareName)}"
        }
        
      case t @ Ident(name: TypeName) if name.decodedName.toString.startsWith("$") => // escaped type unquote syntax
        val bareName = name.toString.tail
        if (unapply) {
          // Note: here we want a type unquote, not a type hole!
          
          val typedTypeRef = c.typecheck(Ident(TermName(bareName)))
          unquoteType(TypeName(bareName), typedTypeRef.tpe, c.untypecheck(typedTypeRef))
          
        } else {
          throw EmbeddingException(s"Free type variables are not supported: '$$$t'")
        }
        
    }
    
    if (debug.debugOptionEnabled) debug("Built:", showCode(pgrm)) // not usually useful to see; so only if `debugOptionEnabled`
    
    //holes.map(_.fold(identity,identity)).groupBy(identity).values.filter(_.size > 1) foreach {
    //  case name +: _ => c.warning(c.enclosingPosition, s"Hole '$name' is defined several times")
    //}
    //val dups = holes.map(_.fold(identity,identity)).groupBy(identity).values.filter(_.size > 1)
    //debug(dups)
    //debug(holes)
    
    holes.map(_.fold(identity,identity)).groupBy(identity) foreach {
      case (name, ns) if ns.size > 1 =>
        c.warning(c.enclosingPosition, s"Hole '$name' is defined several times") // TODO raise an error?
      case _ =>
    }
    
    
    /* // Moved to ScalaTypingMacros's typeEvImplicitImpl/typeRep
    // Automatic Type Splicing:
    // Looks into the current scope to find if we have values of type `TypeRep[x]`, such as those one would extract from an `ext` pattern
    val typesInScope = { ... }
    unquotedTypes :::= typesInScope
    */
    
    if (unquotedTypes nonEmpty) debug(s"Spliced types: ${unquotedTypes map {case (n,tp,tr) => s"$n: $tp ($tr)" }}")
    
    //debug(s"Unquotes: $holes")
    //debug(s"Spliced unquotes: $splicedHoles")
    
    
    val embed = new Embedding[c.type](c)
    
    val tree = embed(base, pgrm, holes.reverse, splicedHoles, unquotedTypes, if (unapply) Some(t.head) else None)
    
    if (debug.debugOptionEnabled)
      //debug("Generated: "+showCode(tree))
      debug("Generated:\n"+showCode(tree))
      //debug("Genreated: "+debug.showCodeOpen(showCode(tree)))//+debug.showCodeOpen(tree))
      //println("Generated:\n"+showCode(tree))
    
    if (debug.debugOptionEnabled) try {
      val tc = c.typecheck(tree)
      debug("Type-checked: "+showCode(tc))
    } catch {
      case e: TypecheckException =>
        throw EmbeddingException(s"[IN DEEP]\n${showPosition(e.pos)}${e.msg}") // TODO better reporting
    }
    
    tree: c.Tree
    
  } catch {
    case e @ EmbeddingException(msg) if !debug.debugOptionEnabled =>
      c.abort(c.enclosingPosition, "Embedding Error: "+msg)
  }}

}
















