package scp.paper


/**
  * Created by lptk on 11/03/16.
  */
object OpTermAlgo extends App {
  import scala.collection.mutable
  //import scala.collection.mutable.{MultiMap,HashMap,Set}
  
  import scala.reflect.runtime.universe._
  
  //trait Name
  //trait Type
  //trait Tree { val tpe: Int }
  def typecheck(t: Tree): Tree = ???
  def typecheckType(t: Tree): Tree = ???
  def inferImplicitValue(t: Type, silent: Boolean): Tree = ???
  
  object Spliced {
    def unapply(x: Tree): Option[(Type,List[Type],List[(String, Type)])] = ???
  }
  object FreeVar {
    def unapply(x: Tree): Option[(Name,Type)] = ???
  }
  
  //def MultiMap[K,V](): mutable.Map[K,V] = ???
  class MultiMap[K,V] extends mutable.Map[K,Set[V]] {
    override def +=(kv: (K, Set[V])): MultiMap.this.type = ???
    override def -=(key: K): MultiMap.this.type = ???
    override def get(key: K): Option[Set[V]] = ???
    override def iterator: Iterator[(K, Set[V])] = ???
  }
  
  def quotedType(t: Tree) = {
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
  
  
  
}

























