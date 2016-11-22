package scala.reflect
package runtime

import squid._
import utils._
import meta.RuntimeUniverseHelpers._

/** The Stuff of Nightmares */
object ScalaReflectSurgeon {
  
  lazy val cache = {
    val cls = Class.forName("scala.reflect.runtime.JavaMirrors$JavaMirror")
    val mtd = cls.getDeclaredMethod("classCache")
    mtd.setAccessible(true)
    mtd.invoke(srum).asInstanceOf[TwoWayCaches#TwoWayCache[Class[_], sru.ClassSymbol]]
  }
  
  private val inl = sru.asInstanceOf[reflect.internal.Internals with reflect.macros.Universe]
  /** Open-heart surgery */
  def changeType_omgwtfbbq(sym: sru.Symbol, tpe: sru.Type): Unit = {
    inl.internal.setInfo(sym.asInstanceOf[inl.Symbol], tpe.asInstanceOf[inl.Type])
  }
  
}
