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
package functional

// TODO port OtherRep?

object PartiallyEvaluatedStructures // to avoid SBT 1 warning

/*
import lang._
import quasi._
import ir._
import utils._


class Dict[A](val entries: Map[Symbol, A])
object Dict {
  def apply[A](entries: (Symbol, A)*) = new Dict[A](Map(entries: _*))
}

class PartiallyEvaluatedStructures extends MyFunSuite(PartiallyEvaluatedStructures.DSL) {
  import DSL._
  
  test("Matching Dict/PEDict") {
    
    intercept[EmbeddingException](dsl"Dict('a -> 42, ('hehe, 1))") // does not infer info from Tuple2.apply
    
    val Dyn = dsl"if (readBoolean) 0 else 1"
    
    val dict = dsl"Dict('a -> 42, 'b -> $Dyn)"
    
    dict match {
      //case dsl"Dict('a -> 42, 'b -> $Dyn)" => fail // Warning:(26, 12) Type inferred for hole 'Dyn' was Nothing. Ascribe the hole explicitly to remove this warning.
      case dsl"Dict('a -> 42, 'b -> ($Dyn:Nothing))" => fail 
      case dsl"Dict('a -> 42, 'b -> ($Dyn:Int))" => 
    }
    
    intercept[EmbeddingException](dict match { case dsl"Dict($entries*)" => println(entries) }) // scp.quasi.EmbeddingException: Use PEDict.unapplySeq instead!
    
    dict match {
      case PEDict(("a", dsl"42"), ("b", Dyn)) => 
    }
    dict match {
      case PEDict(entries @ _*) => assert(entries.toMap.mapValues(_.rep) == dict.rep.asInstanceOf[PEDictRep].fields)
    }
    
  }
  
}

object PartiallyEvaluatedStructures {
  object DSL extends AST with MyDSL with ScalaTyping with QuasiBase[Any] { dsl =>
    
    case class PEDictRep(fields: Map[String, Rep], typ: TypeRep) extends OtherRep {
      def extractRep(that: Rep): Option[Extract] = that match {
        case PEDictRep(fields2, typ2) =>
          if (fields.keySet != fields2.keySet) return None
          mergeAll(fields.map {case (name, v) => v extract fields2(name)})
        case _ => None
      }
      def getExtractedBy(that: Rep): Option[Extract] = ???
      def transform(f: Rep => Rep): Rep = PEDictRep(fields mapValues (dsl.transform(_)(f)), typ)
      def print(p: RepPrinter): PrintResult = toString -> p.maxPrecedence
    }
    object PEDict {
      def unapplySeq[A,C](x: Q[Dict[A],C]): Option[Seq[(String, Quoted[A,C])]] = x.rep match {
        case PEDictRep(fields, typ) => Some(fields.iterator map {case (name, rep) => name -> Quoted[A,C](rep)} toSeq)
        case _ => None
      }
    }
    
    private val DictApply = loadSymbol(true, "scp.functional.Dict", "apply")
    
    private object Entries {
      def unapply(x: ArgList): Option[Seq[(String, Rep)]] = x match {
        case ArgList(as @ _*) => Some(as map (Quoted[(String,_),?](_).erase) map {
          case dsl"Symbol(${Constant(str)}) -> ($value: $vt)" => str -> value.rep  // Note: won't match a tuple constructed with Tuple2.apply!
          case _ => return None
        })
        case _ => None
      }
    }
    
    override def methodApp(self: Rep, mtd: DSLSymbol, targs: scala.List[TypeRep], argss: scala.List[ArgList], tp: TypeRep): Rep = {
      if (mtd != DictApply) return super.methodApp(self, mtd, targs, argss, tp)
      argss.head match {
        case Entries(es) => PEDictRep(es.toMap, tp)
        case ArgsVarargSpliced(Args(), SplicedHole(name)) =>  // matches extractors like:  case dsl"Dict($entries*)" =>
          throw EmbeddingException("Use PEDict.unapplySeq instead!")
        case _ => throw EmbeddingException("Cannot infer static names from Dict creation: "+(super.methodApp(self, mtd, targs, argss, tp)))
      }
    }
    
  }
}
*/








