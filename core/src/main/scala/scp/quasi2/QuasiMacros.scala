package scp
package quasi2

import utils._
import lang2._
import MacroUtils._
import utils.CollectionUtils._
import quasi.EmbeddingException

import collection.mutable
import scala.reflect.macros.whitebox
import scala.reflect.macros.blackbox


object QuasiMacros {
  val dslInterpolators = Set("ir", "dbg_ir")
}
class QuasiMacros(val c: whitebox.Context) {
  import c.universe._
  import QuasiMacros._
  
  //val debug = { val mc = MacroDebugger(c); mc[NoDebug] } // always debug
  val debug = { val mc = MacroDebugger(c); mc[MacroDebug] } // will cut dbg unless MacroUtils.DebugLevel <: MacroDebug or the macro application symbol has @MacroSetting(debug = true)
  
  object Helpers extends {val uni: c.universe.type = c.universe} with meta.UniverseHelpers[c.universe.type]
  import Helpers._
  
  def mkConfig(Config: Type): QuasiConfig = {
    val ctor = Config.member(termNames.CONSTRUCTOR)
    
    if (NoSymbol === ctor)
      throw QuasiException(s"Could not find constructor on type $Config")
    
    if (ctor.alternatives.size =/= 1)
      throw QuasiException(s"More than one constructor on type $Config")
    
    // TODO check that ctor has unique param of 'whitebox.Context'
    
    val cm = srum.reflectClass(Config.typeSymbol.asClass.asInstanceOf[sru.ClassSymbol])
    val mm = cm.reflectConstructor(ctor.asMethod.asInstanceOf[sru.MethodSymbol])
    
    mm(c).asInstanceOf[QuasiConfig]
  }
  
  /** Generates a proper macro abort/error if a quasi or embedding exception is raised,
    * unless debugging is on (in which case it is often useful to see the stack trace) */
  def wrapError(code: => Tree): Tree = try code catch {
    case e: Throwable =>
      val (err, report) = e match {
        case QuasiException(msg) => "Quasiquote Error: "+msg -> true
        case EmbeddingException(msg) => "Embedding Error: "+msg -> true
        case e => e.getMessage -> false
      }
      if (debug.debugOptionEnabled) {
        debug("Macro failed with: "+e)
        //debug(e.getStackTrace mkString "\n")
        throw e
      }
      else if (report) c.abort(c.enclosingPosition, err)
      else {
        c.warning(c.enclosingPosition, "Macro failed with: "+e)
        throw e
      }
  }
  
  
  def forward$(q: Tree*): Tree = c.macroApplication match {
    case q"$qc.$$[$t,$c](..$code)" => q"$qc.qcbase.$$[$t,$c](..$code)"
  }
  def forward$$(name: Tree): Tree = c.macroApplication match {
    case q"$qc.$$$$[$t]($n)" => q"$qc.qcbase.$$$$[$t]($n)"
  }
  
  
  lazy val SubstituteVarargSym = typeOf[QuasiBase].member(TypeName("__*").encodedName)
  //lazy val SubstituteVarargSym = symbolOf[QuasiBase#__*] // nope: gets 'trait Seq' (dealias) ...
  
  
  def quasicodeImpl[Config: c.WeakTypeTag](tree: c.Tree) = wrapError {
    
    debug(s"Typed[${tree.tpe}]: "+showCode(tree))
    
    val quasiBase = c.macroApplication match {
      case x @ q"$qc.${id @ TermName(name)}[$tp]($code)" if dslInterpolators(name) =>
        debug("Found quasicode base: "+qc)
        qc
    }
    val base = c.typecheck(q"$quasiBase.qcbase")
    
    val config = mkConfig(weakTypeOf[Config])
    
    val code = tree //transform { case q"$qc.$$[$typ,$ctx]($t)" =>  }
    //val code = tree transform { case q"$qc.$$[$typ,$ctx](..$xs)" if xs.size != 1 => ??? }
    
    object Embedder extends QuasiEmbedder[c.type](c)
    val res = Embedder(base, code, Nil,
       config,
      None, Map(), Set(), Seq(), Set(), Set(), Set(), code, code.tpe, Nil)
    
    debug("Generated:\n"+showCode(res))
    
    res: c.Tree
    //c.parse(showCode(res))
  }
  
  
  lazy val IRTSym = symbolOf[QuasiBase#IRType[_]]
  lazy val IRSym = symbolOf[QuasiBase#IR[_,_]]
  
  
  def unapplyImpl[L: c.WeakTypeTag](scrutinee: c.Tree) = wrapError {
    import c.universe._
    
    val quasiBase = c.macroApplication match {
      case x @ q"$base.QuasiContext(scala.StringContext.apply(..$_)).${id @ TermName(name)}.unapply($_)"
      if dslInterpolators(name)
      =>
        debug("Found xtion base: "+base)
        base
    }
    
    val base = c.typecheck(q"$quasiBase.base")
    
    /** [INV:Quasi:reptyp]: we only match IR[_,_] types */
    scrutinee.tpe.baseType(IRSym) match {
      case TypeRef(baseType, IRSym, typ :: ctx :: Nil) if base.tpe =:= baseType =>
      case _ => throw EmbeddingException(s"Cannot match type `${scrutinee.tpe}`, which is not a subtype of `$base.${IRSym.name}[_,_]`"
        +"\n\tTry matching { case x: Rep[_] => ... } first.")
    }
    
    quasiquoteImpl[L](base, Some(scrutinee))
  }
  
  
  def applyImpl[Config: c.WeakTypeTag](inserted: c.Tree*) = wrapError {
    
    val quasiBase = c.macroApplication match {
      case x @ q"$base.QuasiContext(scala.StringContext.apply(..$_)).${id @ TermName(name)}.apply(..$_)"
      if dslInterpolators(name)
      =>
        debug("Found ction base: "+base)
        base
    }
    
    val base = c.typecheck(q"$quasiBase.base")
    
    quasiquoteImpl[Config](base, None)
  }
  
  
  def quasiquoteImpl[Config: c.WeakTypeTag](base: Tree, scrutinee: Option[Tree]) = {
    
    val isUnapply = scrutinee.isDefined
    
    val config = mkConfig(weakTypeOf[Config])
    
    val builder = new quasi.PgrmBuilder[c.type](c)(isUnapply)
    
    var holes: List[Either[TermName,TypeName]] = Nil // Left: value hole; Right: type hole
    val splicedHoles = mutable.Set[TermName]()
    
    
    var unquotedTypes = List[(TypeName, Type, Tree)]() // fresh name; type; type rep tree
    
    def unquoteType(name: TypeName, tp: Type, tree: Tree) = tp.baseType(IRTSym) match {
      case TypeRef(tpbase,IRTSym,tp::Nil) if tpbase =:= base.tpe =>
        unquotedTypes ::= ((name.toTypeName, tp, tree))
        tq"$tp"
      case TypeRef(_,_,_) => throw EmbeddingException(s"Cannot unquote type '$tp': it is not from base $base.")
      case _ => throw EmbeddingException(s"Cannot unquote type '$tp': it is not an IRType[_].")
    }
    
    def mkTermHole(name: TermName, followedBySplice: Boolean) = {
      val h = builder.holes(name)
      
      //debug("HOLE: "+h)
      
      if (isUnapply) {
        
        val n = h.name filter (_.toString != "_") getOrElse c.freshName(TermName("ANON_HOLE")) toTermName;
        holes ::= Left(n)
        
        if (followedBySplice) {
          // This is to help Scala typecheck the spliced hole; 
          // if we don't and call $$ instead of $$_*, typechecking usually silently fails and makes <error> types, for some reason...
          q"$base.$$$$_*(${Symbol(n toString)}): _*"
        }
        else  {
          if (h.vararg) splicedHoles += n
          else h.tree match { case pq"$p @ __*" => splicedHoles += n   case _ => }
          if (splicedHoles(n)) q"$base.$$$$_*(${Symbol(n toString)}): _*"
          else
          q"$base.$$$$(${Symbol(n toString)})"
        }
        
      } else {
        h.tree match {
          //case t @ q"$_: _*" => q"$base.$$($t: _*)"  // not actually useful...
          case q"$t: _*" => q"$base.$$($t: _*)"
          //case q"$t: _*" => q"$base.$$($t: _*): _*" // Adding the _* so that typing does not think of this term as a Seq[T] (see vararg $'s type) -- EDIT: won't work, as ModEmb will interpret it as an object-language splice (which we don't want)
          case q"($t: $tp)" if (tp.tpe match { /** This is specifically for handling the {{{xs : __*}}} syntax (which is there as a complementary of the {{{xs @ __*}}} pattern) */
            case TypeRef(btp, sym, Nil) => (btp =:= base.tpe) && sym == SubstituteVarargSym
            case _ => false
          }) => q"$base.$$($t: _*)"
          case t if h.vararg => q"$base.$$($t: _*)"
          case t => q"$base.$$($t)"
        }
      }
    }
    
    val code = (builder.tree: Tree) transform {
      
      // FIXME: these does not seem to work:
      //case q"(_ : $_) => $_" if isUnapply => 
      case ValDef(_, TermName("_"), _, _) if isUnapply => throw QuasiException("All extracted bindings should be named.")
        
      // This is to help Scala typecheck the spliced hole; if we don't and call $$ instead of $$_*, typechecking usually silently fails and makes <error> types, for some reason...
      case q"${Ident(name: TermName)}: _*" if isUnapply && builder.holes.contains(name) =>
        mkTermHole(name, true)
        
      case Ident(name: TermName) if builder.holes.contains(name) =>
        mkTermHole(name, false)
        
      case Ident(name: TypeName) if builder.holes.contains(name.toTermName) => // in case we have a hole in type position ('name' is a TypeName but 'holes' only uses TermNames)
        val hole = builder.holes(name.toTermName)
        if (hole.vararg) throw EmbeddingException(s"Varargs are not supported in type position.${showPosition(hole.tree.pos)}") // (for hole '${hole.tree}').")
        
        if (isUnapply) {
          val n = hole.name.filter(_.toString != "_")
            .getOrElse(throw QuasiException("All extracted types should be named.")).toTypeName // TODO B/E // TODO relax?
          holes ::= Right(n)
          tq"$n"
        }
        else { // !isUnapply
          unquoteType(name.toTypeName, hole.tree.tpe, hole.tree)
        }
        
      //case t @ q"$$(..$args)" if unapply => throw EmbeddingException(s"Unsupported alternative unquoting syntax in unapply position: '$t'")
      case t @ q"$$(..$args)" => // alternative unquote syntax
        q"$base.$$(..$args)"
        
        
      case t @ Ident(name: TermName) if name.decodedName.toString.startsWith("$") => // escaped unquote syntax
        //q"open(${name.toString})"
        val bareName = name.toString.tail
        if (bareName.isEmpty) throw EmbeddingException(s"Empty escaped unquote name: '$$$t'")
        
        if (isUnapply) {
          q"$base.$$(${TermName(bareName)})" // escaped unquote in unapply mode does a normal unquote
        }
        else { // !isUnapply
          holes ::= Left(TermName(bareName)) // holes in apply mode are interpreted as free variables
          q"$base.$$$$(${Symbol(bareName)})"
        }
        
      case t @ Ident(name: TypeName) if name.decodedName.toString.startsWith("$") => // escaped type unquote syntax
        val bareName = name.toString.tail
        if (isUnapply) {
          // Note: here we want a type unquote, not a type hole!
          
          val typedTypeRef = c.typecheck(Ident(TermName(bareName)))
          unquoteType(TypeName(bareName), typedTypeRef.tpe, c.untypecheck(typedTypeRef))
          
        } else {
          throw EmbeddingException(s"Free type variables are not supported: '$$$t'")
        }
        
    }
    
    
    if (isUnapply && holes.size != builder.holes.size) {
      //println(builder.holes, holes.map(_.fold(identity, _ |> hol))) // TODO
      val missing = builder.holes -- holes.map(_.fold(identity, _.toTermName)) map (h => h._2.name map ("$"+_) getOrElse s"$${${showCode(h._2.tree)}}")
      throw QuasiException(s"Illegal hole position${if (missing.size > 1) "s" else ""} for: "+missing.mkString(", "))
    }
    
    object Embedder extends QuasiEmbedder[c.type](c)
    val res = Embedder.applyQQ(base, code, holes.reverse, splicedHoles, unquotedTypes, scrutinee, config)
    
    debug("Generated:\n"+showCode(res))
    
    res: c.Tree
    //c.parse(showCode(res)): c.Tree
  }
  
  
  
  def implicitTypeImpl[Config: c.WeakTypeTag, T: c.WeakTypeTag] = wrapError {
    val T = weakTypeOf[T]
    
    debug("Implicit for "+T)
    
    val config = mkConfig(weakTypeOf[Config])
    
    val quasiBase = c.macroApplication match {
      case q"$qc.dbg.implicitType[$tp]" =>
        debug("Found implicitType base: "+qc)
        qc
      case q"$qc.implicitType[$tp]" =>
        debug("Found implicitType base: "+qc)
        qc
    }
    val myBaseTree = c.typecheck(q"$quasiBase.base")
    
    val codeTree = config.embed(c)(myBaseTree, new BaseUser[c.type](c) {
      def apply(b: Base)(insert: (macroContext.Tree, Map[String, b.BoundVal]) => b.Rep): b.Rep = {
        object QTE extends QuasiTypeEmbedder[macroContext.type, b.type](macroContext, b, str => debug(str)) {
          val helper = QuasiMacros.this.Helpers
          val baseTree = myBaseTree
        }
        object ME extends QTE.Impl
        ME.liftType(T).asInstanceOf[b.Rep] // TODO proper way to do that!
      }
    })
    
    val res = q"$myBaseTree.`internal IRType`[$T]($codeTree)"
    
    debug("Generated: "+res)
    //if (debug.debugOptionEnabled) debug("Of Type: "+c.typecheck(res).tpe) // Makes a StackOverflow when type evidence macro stuff happen
    
    //codeTree: c.Tree
    res
  }
  
  
  
  
}

class QuasiBlackboxMacros(val ctx: blackbox.Context) extends QuasiMacros(ctx.asInstanceOf[whitebox.Context])



















