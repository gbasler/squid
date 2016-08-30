package scp.utils

trait TraceDebug {
  
  private var debugEnabled = false
  private var indent: Int = 0
  
  protected def isDebugEnabled = debugEnabled
  
  protected def debug(x: => Any) = if (debugEnabled) println("| " * indent + x)
  
  def debugFor[T](x: => T): T = {
    debugEnabled = true
    try x
    finally debugEnabled = false
  }
  
  //@inline final protected def nestDbg[T](x: T) = x // to enable in release
  protected def nestDbg[T](x: => T) = (indent += 1) before (try x finally { indent -=1 })
  
  protected def dbg(xs: => List[Any]) = debug(xs mkString " ")
  protected def dbgs(x: => Any, xs: Any*) = debug((x +: xs) mkString " ")
  
}
trait PublicTraceDebug extends TraceDebug {
  
  override def debug(x: => Any) = super.debug(x)
  //def debug(x: => Any)
  
}
