package scp.quasi

import scala.reflect.macros.ParseException
import scala.reflect.macros.whitebox.Context

class PgrmBuilder[Ctx <: Context](val c: Ctx)(unapply: Boolean) { // TODO simplify interaction with Quasi
  val VARARG_REPEAT_STR = "*"

  import c.universe._

  case class Hole(tree: c.Tree, vararg: Boolean, name: Option[c.Name]) {
    def this(tree: c.Tree, vararg: Boolean) = this(tree, vararg, tree match {
      case Bind(name: Name, _) => Some(name)
      case Ident(name: Name)   => Some(name)
      case q"$_.${name: Name}"   => Some(name) // equivalent to `Select(_, name: TermName)`
      case _                       => None
    })
  }

  val (holes, tree, hasVarargs) = {
    var hasVarargs = false

    val (args, partsPos) = c.macroApplication match {
      case Apply(internal.reificationSupport.SyntacticTypeApplied(Select(Select(Apply(Select(universe0, _), List(Apply(_, parts0))), interpolator0), method0), _), args0) =>
        val parts1 = parts0.map {
          case lit @ Literal(Constant(s: String)) => s -> lit.pos
          case part                               => c.abort(part.pos, "Quasiquotes can only be used with literal strings")
        }
        (if (unapply) internal.subpatterns(args0.head).get else args0, parts1)
    }

    val (holes, codeParts) = (args zip partsPos.tail map {
      case (tree, (part, pos)) if part startsWith VARARG_REPEAT_STR =>
        hasVarargs = true; new Hole(tree, true) -> part.tail
      case (tree, (part, pos)) => new Hole(tree, false) -> part
    } map {
      case (hole, part) =>
        //val freshName = hole.name map (c.freshName(_)) getOrElse TermName("ANON_HOLE")
        val freshName = c.freshName(hole.name getOrElse TermName("")) //TermName("ANON_HOLE"))
        // Note: s"($freshName)$part" will not work, as it breaks things like "$x = 42"
        freshName -> (if (unapply) Hole(Bind(freshName, hole.tree), hole.vararg, hole.name) else hole) -> s"$freshName$part" // TODO simplify (unnecessary stuff in `if (unapply)`)
    }).unzip

    //println("Parsing: "+(partsPos.head._1 :: codeParts).mkString)
    
    val codeTree = try c.parse((partsPos.head._1 :: codeParts).mkString) catch {
      case ParseException(pos, msg) =>
        val varargInfo = if (hasVarargs) "\nNote: the problem may be caused by a vararg matcher (syntax `$name*`)" else ""
        c.abort(c.enclosingPosition,
          s"""|Failed to parse DSL code: $msg$varargInfo
              |In macro application:""".stripMargin)
    }

    (holes.toMap, codeTree, hasVarargs)
  }

}

