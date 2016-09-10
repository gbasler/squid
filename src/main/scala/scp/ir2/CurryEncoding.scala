package scp
package ir2

import lang2._
import utils._
import utils.meta.{RuntimeUniverseHelpers => ruh}
import ruh.sru

/** Encodes multi-parameter lambdas as curried functions and calls to `uncurry` */
trait CurryEncoding extends InspectableBase 
  with ScalaTyping /*with RuntimeSymbols*/ { // TODO rm
  
  def abs(param: BoundVal, body: => Rep): Rep
  
  def lambda(params: List[BoundVal], bodyThunk: => Rep): Rep = {
    
    lazy val body = bodyThunk // Don't evaluate this too early to preserve execution order!
    
    params match {
        
      //case Nil => import Predef._; `internal IR`(body).erase match { case ir"$body: $t" => ir"scp.lib.asFunction0($body)".rep }
        /* ^ Works, but that case is now handled in the default case */
        
      case p :: Nil => abs(p, body)
        
      case _ =>
        
        val curried = if (params isEmpty) byName(body)
          else (params foldRight (() => body)){ case (p, acc) => () => abs(p, acc()) }()
        
        val typ = lambdaType(params map boundValType, repType(body))
        
        val arity = params.size
        if (arity > 5) throw IRException(s"Unsupported lambda arity: $arity (no associated `uncurried` function)")
        
        val mtd = loadMtdSymbol(loadTypSymbol(ruh.encodedTypeSymbol(sru.typeOf[scp.lib.`package`.type].typeSymbol.asType)), s"uncurried$arity", None)
        
        methodApp(staticModule("scp.lib.package"), mtd, (params map boundValType) :+ body.typ, Args(curried) :: Nil, typ)
        
    }
  }
  
}

