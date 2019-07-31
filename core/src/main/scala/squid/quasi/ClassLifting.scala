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
  @MacroSetting(debug = true) def thisClass(d: squid.lang.Definitions): d.TopLevel.ClassOrObject[_] = macro ClassLifting.classLiftImpl
}

class ClassLifting(override val c: whitebox.Context) extends QuasiMacros(c) {
  import c.universe._
  
  def liftAnnotImpl(annottees: c.Tree*): c.Tree = wrapError {
    annottees match {
      case (cls: ClassDef) :: rest =>
        q"""..${ClassDef(cls.mods, cls.name, cls.tparams, Template(cls.impl.parents, cls.impl.self, 
          //q"${ Modifiers(NoFlags, TermName(""), ) } def foo = 0"::
            cls.impl.body)) :: rest}"""
        //???
    }
  }
  
  def classLiftImpl(d: c.Tree): c.Tree = wrapError {
    
    object MBM extends MetaBases {
      val u: c.universe.type = c.universe
      def freshName(hint: String) = c.freshName(TermName(hint))
    }
    val td = c.typecheck(q"d:d.type")
    internal.setType(d, td.tpe)
    val Base = new MBM.MirrorBase(d, Some(td.tpe))
    
    class MEBase extends ModularEmbedding[c.universe.type, Base.type](c.universe, Base, str => debug(str)) {
      // TOOD handle `this` references
    }
    
    //println(c.enclosingClass.symbol)
    ////println(c.enclosingClass.symbol.asType)
    //println(c.enclosingClass.symbol.typeSignature.typeSymbol)
    //println(Helpers.encodedTypeSymbol(c.enclosingClass.symbol.companion.asType))
    val obj = c.enclosingClass.asInstanceOf[ModuleDef]
    
    val chain = c.asInstanceOf[reflect.macros.runtime.Context].callsiteTyper.context.enclosingContextChain
    /*
    debug(s">>> "+chain.map(_.scope.collectFirst{
      case cls: ClassDef if cls.symbol.companion === obj.symbol => cls
    }))
    //debug(s">>> "+chain.map(_.scope.collect{case d:ClassDef=>d.symbol}))
    //debug(s">>> "+chain.map(_.scope.collect{case d:ModuleDef=>d.symbol}))
    debug(s">>> "+chain.map(_.scope.iterator.find(_.name.toString === obj.symbol.name.toString)))
    debug(s">>> "+chain.map(_.scope.iterator.find(_ == obj.symbol.companion)))
    ???
    */
    val comp = chain.flatMap(_.scope.iterator.find(_ == obj.symbol.companion)).headOption
    debug(s"Companion: $comp")
    //debug()
    
    ////debug(c.enclosingUnit.body)
    //debug(c.enclosingPackage)
    //???
    val cls = c.enclosingPackage.children.find(_.symbol == obj.symbol.companion)
      .map(_.asInstanceOf[ClassDef])
    debug(s"Companion tree: $cls")
    
    
    
    //val tsymStr = Helpers.encodedTypeSymbol(cls.symbol.typeSignature.typeSymbol.asType)
    
    def liftTemplate(t: Template) = ???
    
    
    val res = obj match {
      case mod @ ModuleDef(mods, name, Template(parents, self, defs)) =>
        //println(defs)
        val fields = defs.collect {
          case vd: ValDef if (try vd.symbol.typeSignature != null
          catch { case _: scala.reflect.internal.Symbols#CyclicReference => false })
            && vd.symbol.asTerm.isVal || vd.symbol.asTerm.isVar
          =>
            object ME extends MEBase
            //println(vd.symbol.asTerm.isGetter) // false
            val s = c.typecheck(q"${Ident(name)}.${vd.name}").symbol
            assert(s.asTerm.isGetter, s)
            val t = c.typecheck(q"${Ident(name)}.${vd.name} = ???").symbol
            assert(t.asTerm.isSetter, t)
            q"mkField(${vd.name.toString},${ME.getMtd(s.asMethod)},Some(${ME.getMtd(t.asMethod)}),${ME(vd.rhs, Some(vd.symbol.typeSignature))})(${ME.liftType(vd.rhs.tpe)})"
        }
        val methods = defs.collect {
          case _md: DefDef if (_md.name.toString =/= "<init>") && (_md.name.toString =/= "reflect") =>
            _md.symbol.typeSignature // force type checking!
            val md = c.typecheck(_md).asInstanceOf[DefDef]
            // FIXME cache symbols!
            object ME extends MEBase {
              lazy val tparams = md.symbol.typeSignature.typeParams.map{tp =>
                assert(tp.asType.typeParams.isEmpty)
                tp -> Base.typeParam(tp.name.toString)}
                //tp -> Base.staticTypeApp(Base.typeParam(tp.name.toString),Nil)}
              lazy val vparams = md.symbol.typeSignature.paramLists.map(vps =>
                vps.map{vp =>
                  vp -> Base.bindVal(vp.name.toString, ME.liftType(vp.typeSignature), Nil)})
                  //Base.New(vp.name.toString, vp.name.toTermName, ME.liftType(vp.typeSignature), Nil)))
              override def unknownTypefallBack(tp: Type): base.TypeRep = {
                val tsym = tp.typeSymbol.asType
                if (tsym.isParameter) {
                  debug(s"P ${tsym.fullName} ${tsym.owner.isType}")
                  assert(tsym.typeParams.isEmpty)
                  Base.staticTypeApp(
                    tparams.find(_._1.name.toString === tsym.name.toString).get._2, Nil) // FIXME hygiene
                } else super.unknownTypefallBack(tp)
              }
              override def unknownFeatureFallback(x: Tree, parent: Tree) = x match {
                  
                case Ident(TermName(name)) =>
                  //debug(s"IDENT $name: ${x.tpe.widen}")
                  vparams.iterator.flatten.find(_._1.name.toString === x.symbol.name.toString).get._2 |> Base.readVal // FIXME hygiene
                  
                case _ =>
                  super.unknownFeatureFallback(x, parent)
                  
              }
            }
            val resTpe = md.symbol.typeSignature.finalResultType
            ME.setType(md.rhs, resTpe)
            val macroUni = ME.uni.asInstanceOf[scala.reflect.macros.Universe]
            macroUni.internal.setSymbol(md.rhs.asInstanceOf[macroUni.Tree], md.symbol.asInstanceOf[macroUni.Symbol])
            val res = ME.apply(md.rhs, Some(md.symbol.typeSignature))
            val sym = ME.getMtd(md.symbol.asMethod)
            //q"Method[Unit](null.asInstanceOf[d.MtdSymbol],d.Code($res))"
            q"..${
              //ME.vparams.flatMap(_.map(vp => ValDef(vp._2.valName, q"")))
              ME.vparams.flatMap(_.map(vp => vp._2.toValDef))
            }; new Method[Any,Scp]($sym,${
              //ME.tparams.map(tp => q"$td.CodeType(${tp._2})")
              //ME.tparams.map(tp => Base.staticTypeApp(tp._2,Nil))
              ME.tparams.map(tp => tp._2._2)
            },${
              //ME.vparams.map(_.map(_._2.tree))},d.Code($res))"
              ME.vparams.map(_.map(tv => q"$td.Variable.mk(${tv._2.tree},${tv._2.typRep})"))},d.Code($res))($td.CodeType(${ME.liftType(md.rhs.tpe)}))"
            //???
        }
        val dv = c.freshName(d.symbol.name).toTermName
        q"""
        val $dv: d.type = d
        ..${Base.mkSymbolDefs}
        class Test
        //implicit val tt = 
        new $dv.TopLevel.Object[$name.type](${name.toString}){
          import $dv.Predef._
          import $dv.Quasicodes._
          //println(codeTypeOf[Test])
          val fields: List[Field[_]] = $fields
          val methods: List[Method[_,Scp]] = $methods
          val companion = None
        }"""
    }
    debug(s"Generated: ${showCode(res)}")
    //debug(Base.symbols)
    //debug(c.enclosingClass.asInstanceOf[ModuleDef].impl.body)
    res
    //???
  }
  
  
  
  
}
