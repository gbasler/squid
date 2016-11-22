package squid.paper


/**
  * Created by lptk on 11/03/16.
  */
object OpTermAlgo {
  import scala.collection.mutable
  //import scala.collection.mutable.{MultiMap,HashMap,Set}
  
  import scala.reflect.runtime.universe._
  
  //trait Name
  //trait Type
  //trait Tree { val tpe: Int }
  def typecheck(t: Tree): Tree = ???
  def typecheckType(t: Tree): Tree = ???
  def inferImplicitValue(t: Type, silent: Boolean): Tree = ???
  def checkValidRefinement(t: Type, vars: scala.collection.Set[String]): Unit = ???
  def error(str: String) = ???
  
  object Spliced {
    def unapply(x: Tree): Option[(Type,List[Type],List[(String, Type)])] = ???
  }
  object FreeVar {
    def unapply(x: Tree): Option[(String,Type)] = ???
  }
  
  //def MultiMap[K,V](): mutable.Map[K,V] = ???
  class MultiMap[K,V] extends mutable.Map[K,Set[V]] {
    override def +=(kv: (K, Set[V])): MultiMap.this.type = ???
    override def -=(key: K): MultiMap.this.type = ???
    override def get(key: K): Option[Set[V]] = ???
    override def iterator: Iterator[(K, Set[V])] = ???
  }
  
  import mutable.HashSet
  
  def quotedTypeOf(t: Tree) = {
    val tt = typecheck(t)
    val freeVars = new MultiMap[String, Type]
    var abstractContexts = List(typeOf[AnyRef])
    def walk(t: Tree, ctx: Map[String, Type]): Unit = {
      t match {
        case FreeVar(name, typ) => freeVars += (name -> Set(typ))
        // PAPER: case q"${vdef @ q"val $name: $tpe = $v"}; $body" =>
        case q"${vdef @ q"val ${name: TermName}: $tpe = $v"}; $body" =>
          walk(v, ctx); walk(t, ctx)
          walk(body, ctx + (name.toString -> vdef.tpe))
        case Spliced(tpe, absCtx, conCtx) =>
          for ((fvName,fvTyp) <- conCtx) ctx.get(fvName) match {
            case Some(typ) => if (!(typ <:< fvTyp))
              error(s"Captured variable $fvName has wrong type")
            case None      => freeVars += (fvName -> Set(fvTyp))
          }
          abstractContexts ++= absCtx
        case q"if ($cond) $thn else $els" =>
          walk(cond, ctx); walk(thn, ctx); walk(els, ctx)
        // ... similar cases elided ...
      }
    }
    walk(tt, ctx = Map())
    checkValidRefinement(tt.tpe, freeVars.keySet)
    val freeVarTrees = freeVars map {case (name,types) =>
      q"val ${TermName(name)}: ${lub(types.toList)}"}
    // Building the final refinement type:
    val ctxType = tq"${lub(abstractContexts)}{ ..$freeVarTrees }"
    tq"Quoted[${tt.tpe}, $ctxType]"
  }
  
  
  
  /*
  
  
  
  
  
  def quotedTypeOf(t: Tree) = {
    val tt = typecheck(t)
    val freeVars = new MultiMap[String, Type]
    //val abstractContexts = mutable.Buffer[Type]()
    var abstractContexts = List(typeOf[AnyRef])
    def walk(t: Tree, ctx: Set[Symbol]): Unit = {
      t match {
        case FreeVar(name, typ) => freeVars += (name -> Set(typ))
        // PAPER: case q"${vdef @ q"val $name: $tpe = $v"}; $body" =>
        case q"${vdef @ q"val ${name: TermName}: $tpe = $v"}; $body" =>
          walk(v, ctx); walk(t, ctx)
          walk(body, ctx + vdef.symbol)
        case Spliced(tpe, absCtx, conCtx) =>
          val (captured,free) = conCtx partition {
            case(name,typ) => ctx exists (_.name == name) }
          //for ((name,typ) <- captured) ctx(name) ...
          for ((name,typ) <- free) freeVars += (name -> Set(typ))
          //for (c <- absCtx) notIns += (c -> ???)
          abstractContexts ++= absCtx
        case q"if ($cond) $thn else $els" =>
          walk(cond, ctx); walk(thn, ctx); walk(els, ctx)
        // ... more similar cases elided ...
      }
    }
    walk(tt, ctx = Set())
    checkValidRefinement(tt.tpe, freeVars.keySet)
    //val freeVarsCtx = tq"{ ..${freeVars map {case (name,typs) => q"val ${TermName(name)}: ${lub(typs.toList)}"} } }"
    val freeVarTrees = freeVars map {case (name,types) =>
      q"val ${TermName(name)}: ${lub(types.toList)}"}
    // Building the final refinement type:
    val ctxType = tq"${lub(abstractContexts)}{ ..$freeVarTrees }"
    tq"Quoted[${tt.tpe}, $ctxType]"
  }
  
  
  
  
  
  def quotedTypeOf(t: Tree) = {
    val tt = typecheck(t)
    //val freeVars = new HashMap[String, Set[Type]]
    //               with MultiMap[String, Type]
    //val notIns = new HashMap[Type, Set[String]]
    //             with MultiMap[Type, String]
    val freeVars = new MultiMap[String, Type]
    val notIns =   new MultiMap[Type, Name]
    def walk(t: Tree, ctx: Map[Symbol, Name]): Unit = {
      t match {
        //case q"${vdef @ q"val $name: $tpe = $v"}; $body" => // PAPER
        case q"${vdef @ q"val ${name: TermName}: $tpe = $v"}; $body" =>
          walk(v, ctx)
          walk(body, ctx + (vdef.symbol -> name))
        //case q"($name: $tpe) => $body" =>
        //  walk(body, ctx + (vdef.symbol -> name))
        case Spliced(tpe, absCtx, conCtx) =>
          val captured = ???
          val escaping = ???
          // TODO
        case FreeVar(name, tpe) =>
          // TODO
        case q"if ($cond) $thn else $els" => // TODO
        // ... more similar cases elided ...
      }
    }
    walk(tt, ctx = Map())
    // TODO
    //val reqEvs = notIns map {
    notIns foreach { case (tpe, names) =>
      val vars = tq"{ ..${names map (n => q"def ${n.toTermName}")} }"
      // checks there is an implicit proving 'vars NotIn tpe':
      inferImplicitValue(typecheckType(tq"$vars NotIn $tpe").tpe, silent = false)
    }
    val fvCtx = ??? : Tree
    tq"Quoted[${tt.tpe}, $fvCtx]"
  }
  
  */
  
  
  
}

























