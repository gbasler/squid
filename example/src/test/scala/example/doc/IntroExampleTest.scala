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

package example.doc

import squid.StaticOptimizer

object IntroExampleTest extends App {
  
  object Stopt extends StaticOptimizer[IntroExample.TestOptimizer]
  import Stopt._
  
  // use `dbg_optimize` instead of `optimize` to print some debug info and make sure the optimization happens
  optimize{ println(Test.foo(1 :: 2 :: 3 :: Nil) + 1) } // expands into just `println(2)`
  
}
