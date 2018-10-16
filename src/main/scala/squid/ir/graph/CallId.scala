package squid.ir.graph

import squid.utils._

object CallId {
  private var curId = 0; def reset(): Unit = curId = 0
}
class CallId(val name: String = "") {
  val uid: Int = CallId.curId alsoDo (CallId.curId += 1)
  def uidstr: String = s"$name$uid"
  override def toString: String = uidstr
}
