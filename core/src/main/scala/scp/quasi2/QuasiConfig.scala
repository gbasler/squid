package scp
package quasi2

import utils._
import lang2._
import scala.reflect.macros.whitebox

/** Helper used for the dependent method type */
abstract class BaseUser[C <: whitebox.Context](val macroContext: C) {
  def apply(b: Base)(insert: (macroContext.Tree, Map[String, b.BoundVal]) => b.Rep): b.Rep
}

abstract class QuasiConfig {
  
  def embed(c: whitebox.Context)(baseTree: c.Tree, user: BaseUser[c.type]): c.Tree
  
}

class DefaultQuasiConfig extends QuasiConfig {
  
  def embed(c: whitebox.Context)(baseTree: c.Tree, user: BaseUser[c.type]) = {
    import c.universe._
    
    object Meta extends MetaBases {
      val u: c.universe.type = c.universe
      def freshName(hint: String) = c.freshName(u.TermName(hint))
    }
    object base extends Meta.MirrorBase(baseTree)
    
    val code = user(base) {
      case (tr, bind) => base.substitute(q"$tr.rep", bind mapValues base.readVal)
    }
    
    q"..${base.mkSymbolDefs}; $code"
  }
  
  
}











