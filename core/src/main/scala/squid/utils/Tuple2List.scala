package squid
package utils

import scala.language.experimental.macros

/** Takes anything that looks like a tuple and makes it a List of the appropriate type.
  * `asList` also acts on "pseudo-tuples" of 0 and 1 field: () and (x) */
object Tuple2List {
  
  implicit def productAsList(tup: Product): List[Any] = macro asListImpl
  
  implicit def asList(tup: Any): List[Any] = macro asListImpl
  
  
  import scala.reflect.macros.whitebox
  
  def asListImpl(c: whitebox.Context)(tup: c.Tree): c.Tree = {
    import c.universe._
    
    // Used to be:
    /*
    tup match {
      case q"(..$defs)" => q"_root_.scala.List(..$defs)"
      case _ => null
    }
    */
    
    // Converts, eg, `a -> b -> c` to `a :: b :: c :: Nil`
    def rec(x: Tree): Tree = x match {
      case q"scala.this.Predef.ArrowAssoc[$_]($a).->[$_]($b)" =>
        val r = rec(b)
        if (r == null) q"$a :: $b :: Nil" else q"$a :: $r"
      case q"(..$defs)" => q"_root_.scala.List(..$defs)"
      case _ => null
    }
    //c.warning(c.enclosingPosition, showCode(tup))
    rec(tup) //and (x=>c.warning(c.enclosingPosition, showCode(x)))
  }
}
