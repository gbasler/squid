package squid
package quasi

sealed class EmbeddingException(val msg: String) extends Exception(msg)
object EmbeddingException {
  def apply(msg: String) = new EmbeddingException(msg)
  def unapply(ee: EmbeddingException) = Some(ee.msg)
  case class Typing(typeError: String) extends EmbeddingException("Quoted expression does not type check: "+typeError)
  case class Unsupported(featureName: String) extends EmbeddingException(s"Unsupported feature: $featureName")
}





