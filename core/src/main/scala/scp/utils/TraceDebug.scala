package scp.utils

trait TraceDebug {
  
  private var debugEnabled = false
  private[scp] def debug(x: => Any) = if (debugEnabled) println(x)
  def debugFor[T](x: => T): T = {
    debugEnabled = true
    try x
    finally debugEnabled = false
  }
  
}
