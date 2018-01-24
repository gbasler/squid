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
package feature

class RepeatedHoles extends MyFunSuite {
  import TestDSL.Predef._
  
  val e = code"HPair(HPair(1,1), HPair(1,1)).second".erase
  
  test("Repeated Term Holes") {
    
    e match {
      case code"HPair[$tp]($p,p).second" =>
        eqt(tp.rep, typeRepOf[HPair[Int]])
        eqt(p, code"HPair(1,1)")
    }
    e match {
      case code"HPair[$tp](p,$p).second" =>
        eqt(tp.rep, typeRepOf[HPair[Int]])
        eqt(p, code"HPair(1,1)")
    }
  }
  
  test("Repeated Term and Type Holes") {
    
    e match {
      case code"HPair(HPair[$tn](n,n),HPair[tn]($n,n)).second" =>
        eqt(tn.rep, typeRepOf[Int])
        eqt(n, code"1")
    }
    e match {
      case code"HPair($a:$t, $_).second" =>
        eqt(t.rep, typeRepOf[HPair[Int]])
    }
    
  }
  
  test("Repeated Term Holes in Different Contexts") {
    // TODO remove the need to annotate the type of each occurrence of a repeated hole...
    
    val p0 = code"val ls = List(1,2,3); ls ++ List(1,2,3)"
    
    p0 matches {
      case code"val x: List[Int] = $xs; ($e:List[Int]) ++ (xs:List[Int])" =>
        (xs : Code[List[Int],{}]) eqt code"List(1,2,3)"
    } and {
      case code"val x: List[Int] = xs; ($e:List[Int]) ++ ($xs:List[Int])" =>
        (xs : Code[List[Int],{}]) eqt code"List(1,2,3)"
    }
    
    val p1 = code"((x:Int) => (x+1,x:AnyVal), (y:Double) => (y.toInt+1,y:AnyVal))"

    p1 match {
        
      case code"((a:Int) => (($_:Int)+($body:Int), $_: AnyVal), (a:Double) => (($_:Int)+(body:Int), $_: AnyVal))" =>
        eqt(body : Code[Int,{val a: AnyVal}], code"1")
        //  ^ `body` appears equal to itself in each context, so the LUB of the contexts should be used!
        
      /* The following doesn't match because the current IR doesn't merge two variables of incompatible types
         However, it could conceivably do so (provided it has some procedure to compute the least upper bound of two types) */
      //case code"((a:Int) => ($_:Int, $body:AnyVal), (a:Double) => ($_:Int, body:AnyVal))" =>
      //case code"((a:Int) => (($_:Int)+($body0:Int),$body1:AnyVal), (a:Double) => (($_:Int)+(body0:Int),body1:AnyVal))" =>
      //  println(body0,body1)
        
    }
    
  }
  
}

