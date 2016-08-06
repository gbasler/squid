package scp
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
    tup match {
      case q"(..$defs)" => q"_root_.scala.List(..$defs)"
      case _ => null
    }
    
  }
}
