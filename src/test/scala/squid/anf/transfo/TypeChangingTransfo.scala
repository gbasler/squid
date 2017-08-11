package squid
package anf

class TypeChangingTransfo extends MyFunSuite(SimpleANFTests.DSLWithEffects) {
  import DSL.Predef._
  import DSL.Quasicodes._
  import DSL.{ANFTypeChangingIRTransformer}
  
  object Tr extends DSL.SelfTransformer with ANFTypeChangingIRTransformer {
    override def transformChangingType[T,C](code: IR[T,C]): IR[_,C] = code match {
      case ir"Symbol($str)" => str
      case ir"println($x)" => ir"println(${transformChangingType(x)})"
      case _ => super.transformChangingType(code)
    }
  }
  
  test("Basic") {
    
    ir"println('ok)" transformWith Tr eqt ir"""println("ok")"""
    
  }
  
  test("Lambdas") {
    
    ir"(s:String) => Symbol(s)" transformWith Tr eqt ir"(s:String) => s"
    
    // Note: omitting the `:Any` ascription here breaks the transformation, because the default recursive call that 
    // transforms the prefix to `.toString` sees the term changing type and gives up.
    ir"(n:Int) => ('ok:Any).toString * n" transformWith Tr eqt 
      ir"(n:Int) => augmentString(${ ir""""ok"""":IR[Any,Any] }.toString) * n"
    // ^ this is a bit tricky: the `.toString` symbol in the transformed code is that of Ant, BUT the object to which it
    // is applied is "ok", which is known to be a 'pure' type and thus the whole thing is not let-bound...
    
  }
  
}
