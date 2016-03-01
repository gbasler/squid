package scp
package quasi

import annotation.StaticAnnotation
import collection.mutable
import scala.language.experimental.macros
import reflect.macros.TypecheckException
import reflect.macros.whitebox.Context

import lang._
import utils.MacroUtils._

//trait Quasi[L <: Base] {
trait Quasi[L] {
  
  implicit class QuasiContext(private val ctx: StringContext) {
    
    object dsl {
      def apply(t: Any*): Any = macro QuasiMacro.applyImpl[L]
      @QuasiMacro.Ext def unapply(t: Any): Any = macro QuasiMacro.unapplyImpl[L]
    }
    
    object dbgdsl {
      @MacroSetting(debug = true) def apply(t: Any*): Any = macro QuasiMacro.applyImpl[L]
      @MacroSetting(debug = true) @QuasiMacro.Ext def unapply(t: Any): Any = macro QuasiMacro.unapplyImpl[L]
    }
    
  }
  
}

object QuasiMacro {
  
  private[scp] class Ext extends StaticAnnotation
  
}
class QuasiMacro(val c: Context) extends utils.MacroShared {
  
  type Ctx = c.type
  val Ctx: Ctx = c
  
  def unapplyImpl[L: c.WeakTypeTag](t: c.Tree) = {
    import c.universe._
    
    /** [INV:Quasi:reptyp]: we only match Rep[_] types */
    val sym = c.typecheck(tq"Rep[Any]", c.TYPEmode).tpe.typeSymbol
    if (sym.fullName != t.tpe.typeSymbol.fullName) {
      throw EmbeddingException(s"Cannot match type '${t.tpe.typeSymbol.fullName}', which is different from Rep type '${sym.fullName}[_]'."
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
    
    val builder = new PgrmBuilder[c.type](c)(unapply)
    
    //debug("Built:", showCode(builder.tree))
    
    var holes: List[Either[TermName,TypeName]] = Nil // Left: value hole; Right: type hole
    
    var splicedTypes = List[(TypeName, Type, Tree)]() // fresh name; type; type rep tree
    
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
          val n = h.name.filter(_.toString != "_").map(_.toTermName)getOrElse(c.freshName(TermName("ANON_HOLE"))) // FIXME hygiene?
          holes ::= Left(n)
          q"$n"
        } else {
          q"splice(${h.tree})"
        }
        
      case Ident(name) if builder.holes.contains(name.toTermName) => // in case we have a hole in type position
        if (unapply) {
          val n = builder.holes(name.toTermName).name.filter(_.toString != "_")
            .getOrElse(throw EmbeddingException("All extracted types should be named.")).toTypeName // TODO B/E // TODO relax? // TODO catch!
          holes ::= Right(n)
          tq"$n"
        }
        else {
          
          val hole = builder.holes(name.toTermName)
          
          hole.tree.tpe match {
            case TypeRef(tpbase,sym,args) =>
              
              // TODO hygiene: use real TypeRep, extracted from implicit base object
              if (sym.name.toString != "TypeRep") throw EmbeddingException(s"Cannot splice type '$sym': it is not a TypeRep.")
              
              val tp = args.head
              
              splicedTypes ::= ((name.toTypeName, tp, hole.tree))
              
              tq"${tp}"
          }
          
        }
        
      case Ident(name) if name.toString.startsWith("$") =>
        q"open(${name.toString})"
        
    } 
    
    if (debug.debugOptionEnabled) debug("Built:", showCode(pgrm)) // not usually useful to see; so only if `debugOptionEnabled`
    
    
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
        case (sym, TypeRef(pre, tsym, List(arg))) if tsym.name.toString == "TypeRep" => // TODO better check!
          Some(sym.name.toTypeName, arg, q"$sym")
        case _ => None
      }
      
      //debug(treps)
      
      treps
    }
    
    splicedTypes :::= typesInScope
    
    splicedTypes = splicedTypes filter {
      case (name, typ, tree) =>
        val impl = c.inferImplicitValue(c.typecheck(tq"TypeEv[$typ]", c.TYPEmode).tpe)
        
        //debug(tp, impl)
        impl == EmptyTree // only consider the spliced type if an implicit for it is not already in scope
        
    }
    
    
    
    val embed = new SimpleEmbedding[c.type](c)
    
    val tree = try embed(pgrm, holes.reverse, splicedTypes, if (unapply) Some(t.head) else None) catch {
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
















