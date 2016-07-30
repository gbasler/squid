package scp
package lang

import utils._

case class DSLDef(fullName: String, info: String, module: Boolean) { // TODO rename DSLMethod
  override def toString = s"${prefix} $fullName: $info"
  val segments = fullName.splitSane('.')
  def path = segments.init
  def shortName = segments.last
  
  def prefix = if (module) "mod " else "def "
  
  /** The full name incorporating type info, to avoid overloading ambiguities */
  def mangledName = fullName+"::"+info
  
  /** The name the associated deep-embedding method should have */
  def deepName = prefix+mangledName
}



