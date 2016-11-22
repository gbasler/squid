package scp
package ir2


case class IRException(msg: String, cause: Option[Throwable] = None) extends Exception(msg) { cause foreach initCause }


