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
    debug(d)
    val res = c.enclosingClass match {
      case ModuleDef(mods, name, Template(parents, self, defs)) =>
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
          catch { case _: scala.reflect.internal.Symbols#CyclicReference => false }) =>
            //q"(code{(??? : ${name}.type).${vd.name}},code{???})"
            println(vd.symbol,vd.symbol.typeSignature)
            q"mkField(code{${Ident(name)}.${vd.name}},None)"
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
    res
    //???
  }
  
  
  
  
}
