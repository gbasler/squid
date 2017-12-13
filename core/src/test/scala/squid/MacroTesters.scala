// Copyright 2017 EPFL DATA Lab (data.epfl.ch)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package squid

import scala.language.experimental.macros
import reflect.macros.whitebox.Context
import ir._
import quasi._
import squid.lang.Base
import squid.lang.IntermediateBase
import squid.utils.MacroUtils._
import utils.meta.RuntimeUniverseHelpers._

object MacroTesters {
  
  def embed(base: Base)(code: Any): base.Rep = macro embedImpl
  def embedImpl(c: Context)(base: c.Tree)(code: c.Tree) = {
    import c.universe._
    
    //val debug = { val mc = MacroDebugger(c); mc[NoDebug] }
    //debug("INPUT", showCode(code))
    
    val baseName = TermName("__b")
    
    object MBM extends MetaBases {
      val u: c.universe.type = c.universe
      def freshName(hint: String) = c.freshName(TermName(hint))
    }
    val MB = new MBM.MirrorBase( q"$baseName" )
    
    object ME extends ModularEmbedding[c.universe.type, MBM.MirrorBase](c.universe, MB,
      //str => debug(str)
      str => ()
    )
    
    val newCode = ME(code)
    
    val defs = q"val $baseName = $base" +: MB.mkSymbolDefs
    
    q"..$defs; $newCode"
  }
  
  
  def shallowAndDeep[A](base: Base)(code: A): (base.Rep, A) = macro shallowAndDeepImpl
  def shallowAndDeepImpl(c: Context)(base: c.Tree)(code: c.Tree) = {
    import c.universe._
    //val debug = { val mc = MacroDebugger(c); mc[NoDebug] }
    //debug("INPUT", showCode(code))
    q"squid.MacroTesters.embed($base)($code) -> $code"
  }
  
  
  def staticExecAsConst[Base <: IntermediateBase](code: Any): Any = macro staticExecAsConstImpl[Base]
  def staticExecAsConstImpl[AST: c.WeakTypeTag](c: Context)(code: c.Tree) = {
    import c.universe._
    
    //val debug = { val mc = MacroDebugger(c); mc[NoDebug] }
    val debug = { val mc = MacroDebugger(c); mc[ApplyDebug] } // will cut dbg unless MacroUtils.DebugLevel <: ApplyDebug
    
    val m: sru.Mirror = reflect.runtime.currentMirror
    val imp = scala.reflect.runtime.universe.internal.createImporter(c.universe)
    
    val AST = Class.forName(weakTypeOf[AST].typeSymbol.asClass.fullName).newInstance().asInstanceOf[IntermediateBase]
    object ME extends ModularEmbedding[c.universe.type, AST.type](c.universe, AST, str => debug(str))
    
    val newCode = ME(code)
    debug("AST: "+newCode)
    
    val EB = new BaseInterpreter
    
    val res = //EB.debugFor
      { AST.reinterpret(newCode, EB)() }
    debug("EXEC", res)
    
    Literal(Constant(res))
    
  }
  
  def staticExecAndSource[Base](code: Any): (Any, Any) = macro staticExecWorksImpl[Base]
  def staticExecWorksImpl[AST: c.WeakTypeTag](c: Context)(code: c.Tree) = {
    import c.universe._
    q"${staticExecAsConstImpl[AST](c)(code)} -> $code"
  }
  
  
  
  
}
