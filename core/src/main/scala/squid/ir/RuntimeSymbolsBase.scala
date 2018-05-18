package squid
package ir

/** Intercept runtime Scala reflection problems and rethrow them as proper XSymbolLoadingException errors. */
trait RuntimeSymbolsBase extends lang.Base { self: RuntimeSymbols =>
  
  abstract override def loadTypSymbol(fullName: String): TypSymbol = {
    try super.loadTypSymbol(fullName)
    catch {
      case e @ ScalaReflectionException(msg) =>
        throw TypSymbolLoadingException(fullName, e)
    }
  }
  abstract override def loadMtdSymbol(typ: TypSymbol, symName: String, index: Option[Int], static: Boolean = false): MtdSymbol = {
    try super.loadMtdSymbol(typ, symName, index, static)
    catch {
      case e @ ScalaReflectionException(msg) =>
        throw MtdSymbolLoadingException(typ, symName, index, e)
    }
  }
  
}
