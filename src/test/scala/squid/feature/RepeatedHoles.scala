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
  
}

