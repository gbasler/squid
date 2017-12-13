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
package ir
/*
import scp.lang.Precedence

trait PatternAlternation extends AST {
  
  object AltOpMatcher {
    val Symbol = loadSymbol(true, "scp.ir.PatternAlternation", "AltOp")
    def unapply(r: Rep) = r match {
      case MethodApp(_, Symbol, _::Nil, Args(self)::Nil, _) => Some(self)
      case _ => None
    }
  }
  object Alt {
    val Symbol = loadSymbol(false, "scp.ir.PatternAlternation.AltOp", "$bar")
  }
  
  case class AltPat(lhs: Rep, rhs: Rep) extends OtherRep {
    val typ: TypeRep = lhs.typ
    assert(typ =:= rhs.typ)
    
    def transform(f: (Rep) => Rep): Rep = ???
  
    def extractRep(that: Rep): Option[Extract] = lhs extract that orElse (rhs extract that)
    
    def getExtractedBy(that: Rep): Option[Extract] = ???
    
    def print(printer: RepPrinter): (String, Precedence) = ???
  }
  
  override def methodApp(self: Rep, mtd: DSLSymbol, targs: List[TypeRep], argss: List[ArgList], tp: TypeRep): Rep = mtd match {
    case Alt.Symbol =>
      val trueSelf = self match { case AltOpMatcher(self) => self }
      if (isInExtraction) AltPat(trueSelf, argss match { case Args(a)::Nil => a })
      else throw new IllegalArgumentException("Pattern Alternation is not supposed to be used in construction mode!")
    case _ => super.methodApp(self, mtd, targs, argss, tp)
  }
  
  
}

object PatternAlternation {
  implicit class AltOp[A](self: A) {
    def | (that: A) = throw new IllegalAccessError("This method is not supposed to be called directly.")
  }
}
*/

