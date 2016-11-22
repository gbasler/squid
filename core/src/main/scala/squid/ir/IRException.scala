package squid
package ir


case class IRException(msg: String, cause: Option[Throwable] = None) extends Exception(msg) { cause foreach initCause }


