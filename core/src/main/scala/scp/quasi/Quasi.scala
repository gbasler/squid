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
//trait Quasi[L <: Base] {
trait Quasi[L] { self: Base =>
  
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
    
    val unapply = c.macroApplication.symbol.annotations.filter(
      _.tree.tpe <:< typeOf[QuasiMacro.Ext]
    ).headOption.map(_.tree.children.tail).nonEmpty
    
    //debug(c.macroApplication)
    val base = c.macroApplication match {
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
    
    val builder = new PgrmBuilder[c.type](c)(unapply)
    
    //debug("Built:", showCode(builder.tree))
    
    var holes: List[Either[TermName,TypeName]] = Nil // Left: value hole; Right: type hole
    
    var splicedTypes = List[(TypeName, Type, Tree)]() // fresh name; type; type rep tree
    
    lazy val QTSym = symbolOf[Base#QuotedType[_]]
    
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
        
      case Ident(name) if builder.holes.contains(name) =>
        val h = builder.holes(name)
        if (unapply) {
          //q"holeExtract()"
          //val n = h.name.filter(_.toString != "_").map(n => q"$n").getOrElse(h.tree)
          val n = h.name.filter(_.toString != "_") map (_.toTermName) getOrElse // hygiene? (can taking the name as specified by the user make collisions?)
            c.freshName(TermName("ANON_HOLE"))
          holes ::= Left(n)
          if (h.vararg) q"$n: _*"
          else q"$n"
        } else {
          //if (h.vararg) q"splice(${h.tree}): _*"
          if (h.vararg) q"spliceVararg(${h.tree}): _*"
          else q"splice(${h.tree})"
        }
        
      case Ident(name) if builder.holes.contains(name.toTermName) => // in case we have a hole in type position ('name' is a TypeName but 'holes' only uses TermNames)
        val hole = builder.holes(name.toTermName)
        if (hole.vararg) throw EmbeddingException(s"Varargs are not supported in type position.${showPosition(hole.tree.pos)}") // (for hole '${hole.tree}').")
        if (unapply) {
          val n = hole.name.filter(_.toString != "_")
            .getOrElse(throw EmbeddingException("All extracted types should be named.")).toTypeName // TODO B/E // TODO relax? // TODO catch!
          holes ::= Right(n)
          tq"$n"
        }
        else { // apply
          
          hole.tree.tpe.baseType(QTSym) match {
            case TypeRef(tpbase,QTSym,tp::Nil) if tpbase =:= base.tpe =>
              splicedTypes ::= ((name.toTypeName, tp, hole.tree))
              tq"${tp}"
            case TypeRef(_,_,_) => throw EmbeddingException(s"Cannot splice type '${hole.tree.tpe}': it is not from base $base.")
            case _ => throw EmbeddingException(s"Cannot splice type '${hole.tree.tpe}': it is not a QuotedType[_].")
          }
          
        }
        
      case t @ q"$$(..$args)" if unapply => throw EmbeddingException(s"Unsupported alternative splicing syntax in unapply position: '$$$t'")
      case t @ q"$$(..$args)" =>
        q"(..${args map (a => q"splice($a)")})"
        
      case t @ Ident(name: TermName) if name.toString.startsWith("$") => // alternative splicing syntax
        //q"open(${name.toString})"
        val bareName = name.toString.tail
        if (bareName.isEmpty) throw EmbeddingException(s"Empty alternative splicing name: '$$$t'")
        if (unapply) {
          q"splice(${TermName(bareName)})" // alternative splicing in unapply mode does a normal splicing
        } else { // apply
          holes ::= Left(bareName) // holes in apply mode are interpreted as free variables
          q"${TermName(bareName)}"
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
    
    
    
    // Automatic Type Splicing:
    // Looks into the current scope to find if we have values of type `TypeRep[x]`, such as those one would extract from an `ext` pattern
    val typesInScope = {
      val vals = c.asInstanceOf[reflect.macros.runtime.Context].callsiteTyper.context.enclosingContextChain.flatMap {
        _.scope collect {
          case sym if sym.isVal
            && sym.isInitialized // If we look into the type of value being constructed (eg `val x = exp"42"`),
                                 // it will trigger a 'recursive value needs type' error
          =>
            //sym.name.toTermName -> sym.tpe
            //debug(sym, sym.isInitialized)
            sym -> sym.tpe
        }
      }.asInstanceOf[List[(TermSymbol, Type)]]
      
      //debug(vals)
      
      val treps = vals flatMap {
        case (sym, TypeRef(tpbase, QTSym, tp::Nil)) if tpbase =:= base.tpe =>
          Some(sym.name.toTypeName, tp, q"$sym")
        case _ => None
      }
      
      //debug(treps)
      
      treps
    }
    
    debug(s"Found types in scope: ${typesInScope map {case (n,tp,tr) => s"$n: $tp ($tr)" }}")
    
    splicedTypes :::= typesInScope
    
    // FIXME: not safe to remove types in scope, because they may be used in the expr to typecheck...
    splicedTypes = splicedTypes filter {
      case (name, typ, tree) =>
        val impl = c.inferImplicitValue(c.typecheck(tq"TypeEv[$typ]", c.TYPEmode).tpe)
        
        //debug(tp, impl)
        impl == EmptyTree // only consider the spliced type if an implicit for it is not already in scope
        
    }
    
    debug(s"Spliced types: ${splicedTypes map {case (n,tp,tr) => s"$n: $tp ($tr)" }}")
    
    
    
    val embed = new SimpleEmbedding[c.type](c)
    
    val tree = try embed(base, pgrm, holes.reverse, splicedTypes, if (unapply) Some(t.head) else None) catch {
      case e @ EmbeddingException(msg) if !debug.debugOptionEnabled =>
        c.abort(c.enclosingPosition, "Embedding Error: "+msg)
    }
    
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
  }

}
















