// Copyright 2019 EPFL DATA Lab (data.epfl.ch)
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
package quasi

import squid.utils._
import squid.lang._
import squid.quasi._
import squid.ir._
import squid.utils.MacroUtils.MacroSetting
//import squid.utils.meta.{RuntimeUniverseHelpers => ruh}

import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly
import scala.reflect.macros.whitebox
import scala.language.experimental.macros

@compileTimeOnly("Enable macro paradise to expand macro annotations.")
class lift[B <: Base] extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ClassLifting.liftAnnotImpl
}
object lift {
  def thisClass(d: squid.lang.Definitions): Any = macro ClassLifting.classLiftImpl
}
@compileTimeOnly("Enable macro paradise to expand macro annotations.")
class dbg_lift[B <: Base] extends StaticAnnotation {
  @MacroSetting(debug = true) def macroTransform(annottees: Any*): Any = macro ClassLifting.liftAnnotImpl
}
object dbg_lift {
  @MacroSetting(debug = true) def thisClass(d: squid.lang.Definitions): Any = macro ClassLifting.classLiftImpl
}

class ClassLifting(override val c: whitebox.Context) extends QuasiMacros(c) {
  import c.universe._
  
  def liftAnnotImpl(annottees: c.Tree*): c.Tree = wrapError {
    ???
  }
  
  def classLiftImpl(d: c.Tree): c.Tree = wrapError {
    //debug(c.reifyEnclosingRuntimeClass)
    //debug(c.enclosingClass)
    //debug(c.enclosingMethod)
    
    //object MB extends MetaBases
    //val Base = new BaseInterpreter
    
    object MBM extends MetaBases {
      val u: c.universe.type = c.universe
      def freshName(hint: String) = c.freshName(TermName(hint))
    }
    //val Base = new MBM.ScalaReflectionBase
    val Base = new MBM.MirrorBase(d, None)
    object ME extends ModularEmbedding[c.universe.type, Base.type](c.universe, Base, str => debug(str))
    
    //println(c.enclosingClass.symbol)
    ////println(c.enclosingClass.symbol.asType)
    //println(c.enclosingClass.symbol.typeSignature.typeSymbol)
    //println(Helpers.encodedTypeSymbol(c.enclosingClass.symbol.companion.asType))
    val tsymStr = Helpers.encodedTypeSymbol(c.enclosingClass.symbol.typeSignature.typeSymbol.asType)
    println(tsymStr)
    println(ME.loadTypSymbol(tsymStr))
    println(Base.loadTypSymbol(tsymStr))
    //println(ME.loadTypSymbol(tsymStr)())
    //???
    
    debug(d)
    val res = c.enclosingClass match {
      case ModuleDef(mods, name, Template(parents, self, defs)) =>
        println(defs)
        //defs.foreach{d =>
        //  println(d)
        //  try println(": "+d.symbol.typeSignature)
        //  catch {
        //    //_: internal.CyclicReference =>
        //    case _: scala.reflect.internal.Symbols#CyclicReference =>
        //      println(": ?")
        //  }
        //}
        val fields = defs.collect {
          case vd: ValDef if (try vd.symbol.typeSignature != null
          catch { case _: scala.reflect.internal.Symbols#CyclicReference => false })
            && vd.symbol.asTerm.isVal || vd.symbol.asTerm.isVar
          =>
            //println(vd.symbol.asTerm.isGetter) // false
            //q"(code{(??? : ${name}.type).${vd.name}},code{???})"
            //println(vd.symbol,vd.symbol.typeSignature)
            val s = c.typecheck(q"${Ident(name)}.${vd.name}").symbol
            println(s,s.asTerm.isGetter)
            val t = c.typecheck(q"${Ident(name)}.${vd.name} = ???").symbol
            println(t,t.asTerm.isSetter)
            println(t == s)
            println(ME.getMtd(s.asMethod))
            println(ME.getMtd(t.asMethod))
            println(ME.apply(vd.rhs, Some(vd.symbol.typeSignature)))
            q"mkField(code{${Ident(name)}.${vd.name}},${
              //if (vd.mods.flags | Flag.MUTABLE)
              if (vd.symbol.asTerm.isVar) q"Some(code{${Ident(name)}.${vd.name} = ???})" else q"None"
            })"
            //q"mkField(code{this.${vd.name}},code{???})"
        }
        val dv = c.freshName(d.symbol.name).toTermName
        q"""
        val $dv: d.type = d
        new $dv.Class(${name.toString}){
          import $dv.Predef._
          import $dv.Quasicodes._
          val fields: List[Field[_]] = $fields
          val methods: List[Method[_]] = Nil
        }"""
    }
    debug(s"Generated: ${showCode(res)}")
    debug(Base.symbols)
    //debug(c.enclosingClass.asInstanceOf[ModuleDef].impl.body)
    res
    ???
  }
  
  
  
  
}
