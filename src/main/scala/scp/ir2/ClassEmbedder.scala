package scp
package ir2

import scp.utils.meta.RuntimeUniverseHelpers
import utils._
import RuntimeUniverseHelpers.sru

import collection.mutable

/** TODO handle varargs */
trait ClassEmbedder { baseSelf: lang2.InspectableBase =>
  
  protected[scp] var isEmbedding = false
  protected val embeddedClasses = mutable.Buffer[EmbeddedClass[baseSelf.type]]() // useful? rm?
  def embed(cls: EmbeddedableClass*): Unit = cls map embed
  def embed(cls: EmbeddedableClass) = {
    assert(!isEmbedding)
    isEmbedding = true
    try {
      val ecls = cls.embedIn(baseSelf)
      embeddedClasses += ecls
      methods ++= ecls.defs
      paramMethods ++= ecls.parametrizedDefs mapValues (new ParamMethod(_))
    } finally isEmbedding = false
  }
  protected var methods = Map.empty[sru.MethodSymbol, Lazy[SomeIR]]
  protected var paramMethods = Map.empty[sru.MethodSymbol, ParamMethod]
  
  import ClassEmbedder.Errors._
  
  def methodDef(mtd: sru.MethodSymbol, targs: List[TypeRep]): Either[Symbol, SomeIR] = {
    if (targs isEmpty) methods get mtd match { case Some(m) => if (m.isComputing) Left(Recursive) else Right(m.value)  case None => Left(Missing) }
    else paramMethods get mtd match { case Some(m) => m(targs)  case None => Left(Missing) }
  }
  
  protected class ParamMethod(f: List[TypeRep] => SomeIR) {
    private var computing = false
    def isComputing = computing
    def apply(targs: List[TypeRep]): Either[Symbol, SomeIR] = {
      if (computing) Left(Recursive)
      else {
        computing = true
        try Right(f(targs))
        finally computing = false
      }
    }
  }
  
}
object ClassEmbedder {
  object Errors {
    val Recursive = 'Recursive
    val Missing = 'Missing
  }
}

