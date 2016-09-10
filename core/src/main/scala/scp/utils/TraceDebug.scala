package scp.utils

trait TraceDebug {
  
  private var debugEnabled = false
  private var indent: Int = 0
  
  protected def isDebugEnabled = debugEnabled
  
  protected def debug(x: => Any) = if (debugEnabled) println(s"${Debug.GREY}| ${Console.RESET}" * indent + x)
  
  @inline final def setDebugFor[T](enabled: Boolean)(x: => T): T = {
    val old = debugEnabled
    debugEnabled = enabled
    try x finally debugEnabled = old
  }
  def debugFor[T](x: => T): T = setDebugFor(true)(x)
  def muteFor[T](x: => T): T = setDebugFor(false)(x)
  
  //@inline final protected def nestDbg[T](x: T) = x // to enable in release
  protected def nestDbg[T](x: => T) = (indent += 1) before (try x finally { indent -=1 })
  
  protected def dbg(xs: => List[Any]) = debug(xs mkString " ")
  protected def dbgs(x: => Any, xs: Any*) = debug((x +: xs) mkString " ")
  
}
trait PublicTraceDebug extends TraceDebug {
  
  override def debug(x: => Any) = super.debug(x)
  override def nestDbg[T](x: => T) = super.nestDbg(x)
  
}
