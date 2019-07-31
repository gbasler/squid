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
package ir

import utils._
import meta.RuntimeUniverseHelpers._

import collection.mutable

object RuntimeSymbols extends RuntimeSymbols with PublicTraceDebug {
  
}
trait RuntimeSymbols {
  import RuntimeSymbols._
  
  
  private[this] val symbolCache = mutable.HashMap.empty[(ScalaTypeSymbol,String), MtdSymbol]
  private[this] val overloadedSymbolCache = mutable.HashMap.empty[(ScalaTypeSymbol,String,Int), MtdSymbol]
  private[this] val typSymbolCache = mutable.HashMap.empty[String, ScalaTypeSymbol]
  
  
  protected def ensureDefined(name: => String, sym: sru.Symbol) = sym match {
    case sru.NoSymbol => 
      throw new Exception(s"Could not find $name")
    case _ => sym
  }
  
  //type TypSymbol = sru.TypeSymbol
  type ScalaTypeSymbol = sru.TypeSymbol
  
  //type MtdSymbol = sru.MethodSymbol  // used to be this
  class MtdSymbol private(val asMethodSymbol: sru.MethodSymbol) {
    //assert(!mtdSymbolCache.contains(asMethodSymbol))
    def asMtdSymbol: this.type = this
    
    private var nextInSet: MtdSymbol = null
    
    asMethodSymbol.overrides.foreach(osym =>
      if (osym.isMethod) // Note: not unifying with overridden objects, but hopefully with overridden val accessors...
        union_!(MtdSymbol(osym.asMethod)))
    
    private def representative: MtdSymbol = if (nextInSet == null) this else {
      val res = nextInSet.representative
      assert(res.nextInSet == null)
      if (!(res eq nextInSet)) nextInSet = res
      res
    }
    
    private def union_!(that: MtdSymbol): Unit = {
      val thatRepr = that.representative
      if (thatRepr != representative) {
        assert(thatRepr.nextInSet == null)
        thatRepr.nextInSet = this
      }
    }
    
    override def hashCode() = asMethodSymbol.name.hashCode()
    override def equals(that: Any) = that match {
      case mtd: MtdSymbol => representative eq mtd.representative
      case _ => false
    }
    
    def infoString = s"${asMethodSymbol.owner.name}[~>${representative.asMethodSymbol.owner.name}].${asMethodSymbol.name}"
    override def toString = s"${asMethodSymbol.owner.name}.${asMethodSymbol.name}"
  }
  protected val mtdSymbolCache = mutable.HashMap.empty[sru.MethodSymbol, MtdSymbol]
  object MtdSymbol {
    def apply(sym: sru.MethodSymbol): MtdSymbol =
      mtdSymbolCache.getOrElseUpdate(sym, new MtdSymbol(sym))
  }
  implicit def asMethodSymbol(mtd: MtdSymbol): sru.MethodSymbol = mtd.asMethodSymbol
  implicit def asMtdSymbol(sym: sru.MethodSymbol): MtdSymbol = MtdSymbol(sym)
  
  def loadTypSymbol(fullName: String): ScalaTypeSymbol = {
    typSymbolCache getOrElseUpdate (fullName, loadTypSymbolImpl(fullName))
  }
  def loadTypSymbolImpl(fullName: String): ScalaTypeSymbol = {
    //debug(s"Loading type $fullName")
    val path = fullName.splitSane('#')
    val root = if (path.head endsWith "$") srum.staticModule(path.head.init) else srum.staticClass(path.head)
    // ^ [INV:RuntimeSymbols:loadclasses] can only runtime-load classes (or modules) as base paths
    val res = path.tail.foldLeft(root) {
      case (s, name) if name.endsWith("$") => ensureDefined(s"$name in $s", s.asType.toType.member(sru.TermName(name.init)))
      case (s, name) => ensureDefined(s"$name in $s",
        if (s.isType) s.asType.toType.member(sru.TypeName(name))
        else {
          assert(s.isModule, s"$s is not a module (while loading $fullName)")
          // If `s` is an object, look at its type signature...
          s.typeSignature.member(sru.TypeName(name))
        })
    }
    (if (res.isType) res.asType else res.typeSignature.typeSymbol.asType) alsoApply (x => debug(s"Loaded: $x"))
  }
  
  def loadMtdSymbol(typ: ScalaTypeSymbol, symName: String, index: Option[Int], static: Boolean = false): MtdSymbol = {
    // Q: is it okay not to include `static` in the caching key?!
    if (index.isDefined)
      overloadedSymbolCache getOrElseUpdate ((typ, symName, index.get), loadMtdSymbolImpl(typ, symName, index, static))
    else symbolCache getOrElseUpdate ((typ, symName), loadMtdSymbolImpl(typ, symName, index, static))
  }
  // Note: `static` as in "Java static"
  private def loadMtdSymbolImpl(typ: ScalaTypeSymbol, symName: String, index: Option[Int], static: Boolean): MtdSymbol = { // TODOne cache!!
    debug(s"Loading method $symName from $typ"+(if (static) " (static)" else ""))
    
    /* Because of a 2-ways caching problem in the impl of JavaMirror,
     * a Java class name like "java.lang.String" can return either 'object String' or 'class String'... */
    val tp = if (typ.isJava && (static ^ typ.isModuleClass))
      typ.companion.typeSignature else typ.toType
    
    val sym = ensureDefined(s"'$symName' in $typ", tp.member(sru.TermName(symName)))
    if (sym.alternatives.nonEmpty) debug("Alts: "+sym.alternatives.map(_.typeSignature).map("\n\t"+_))
    
    val idx = index getOrElse 0
    if (sym.alternatives.indices contains idx) sym.alternatives(idx).asMethod |> asMtdSymbol
    else throw IRException(s"Could not find overloading index $idx for method ${sym.fullName}; " +
      s"perhaps a quasiquote has not been recompiled atfer a change in the source of the quoted code?")
  }
  
  
  // Utilities:
  
  import reflect.runtime.universe.TypeTag
  
  def typeSymbol[T:TypeTag]: ScalaTypeSymbol = implicitly[TypeTag[T]].tpe.typeSymbol.asType
  def methodSymbol[T:TypeTag](name: String, index: Int = -1): MtdSymbol = {
    val tpe = implicitly[TypeTag[T]].tpe
    val alts = tpe.member(sru.TermName(name)).alternatives.filter(_.isMethod)
    val r = if (alts.isEmpty) throw new IllegalArgumentException(s"no $name method in $tpe")
      else if (alts.size == 1) alts.head
      else {
        require(index >= 0, s"overloaded method $name in $tpe")
        alts(index)
      }
    r.asMethod |> asMtdSymbol
  }
  
  
}
