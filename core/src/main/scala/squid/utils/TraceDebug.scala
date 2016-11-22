package squid.utils

trait TraceDebug {
  
  private var debugEnabled = false
  private var indent: Int = 0
  
  protected def isDebugEnabled = debugEnabled
  
  protected def debug(x: => Any) = if (debugEnabled) {
    val lines = x.toString.splitSane('\n')
    val pre = "| " * indent
    println(lines map ((if (pre nonEmpty) Debug.GREY + pre + Console.RESET else "") + _) mkString "\n")
  }
  protected def debugVisible(x: => Any) = if (debugEnabled) {
    val lines = s"$x".splitSane('\n')
    println(lines map (Console.RED + "| " * indent + ">> " + Console.RESET + Console.BOLD + _) mkString s"${Console.RESET}\n")
  }
  
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
