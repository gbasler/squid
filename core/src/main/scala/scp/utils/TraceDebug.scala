package scp.utils

trait TraceDebug {
  
  private var debugEnabled = false
  
  protected def debug(x: => Any) = if (debugEnabled) println(x)
  
  def debugFor[T](x: => T): T = {
    debugEnabled = true
    try x
    finally debugEnabled = false
  }
  
  protected def dbg(xs: List[Any]) = debug(xs mkString " ")
  
}
