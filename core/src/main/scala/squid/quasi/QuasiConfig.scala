package squid
package quasi

import utils._
import squid.lang.Base

import scala.reflect.macros.whitebox

/** Helper used for the dependent method type */
abstract class BaseUser[C <: whitebox.Context](val macroContext: C) {
  def apply(b: Base)(insert: (macroContext.Tree, Map[String, b.BoundVal]) => b.Rep): b.Rep
}

/** This abstraction is probably going to disappear or be simplified because it complicates the implementation with little return. */
abstract class QuasiConfig {
  
  def embed(c: whitebox.Context)(baseTree: c.Tree, user: BaseUser[c.type]): c.Tree
  
}

class DefaultQuasiConfig extends QuasiConfig {
  
  def embed(c: whitebox.Context)(baseTree: c.Tree, user: BaseUser[c.type]) = {
    import c.universe._
    
    object Meta extends MetaBases {
      private var cnt = 0
      val u: c.universe.type = c.universe
      //def freshName(hint: String) = c.freshName(u.TermName(hint)) // Creates problems with the Scala compiler when the produced code is processed by another macro
      def freshName(hint: String) = TermName(s"_${cnt}_$hint") oh_and (cnt += 1)
    }
    object base extends Meta.MirrorBase(baseTree)
    
    val code = user(base) {
      case (q"$tr: _*", bind) => q"$tr map (__$$ir => ${base.substitute(q"__$$ir.rep", bind mapValues base.readVal)}): _*"
      case (tr, bind) => base.substitute(q"$tr.rep", bind mapValues base.readVal)
    }
    
    q"..${base.mkSymbolDefs}; $code"
  }
  
  
}











