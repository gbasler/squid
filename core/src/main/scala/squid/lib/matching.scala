// Copyright 2018 EPFL DATA Lab (data.epfl.ch)
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

package squid.lib

import squid.utils._
import scala.reflect.ClassTag

object matching {
  
  type None = None.type
  
  // These can be used to give a dummy class to true and to false when interpreted as ADT cases
  object `true`
  object `false`
  
  abstract class Case[-A,+R] { def apply(a: A): Option[R] }
  
  class CaseBuilder[T] {
    @transparencyPropagating
    def apply[A>:T,R](body: T => R)(implicit cls: ClassTag[T]): A Case R = new Case[A,R] {
      def apply(a: A): Option[R] = body(a.asInstanceOf[T]) optionIf (a.getClass == scala.reflect.classTag[T].runtimeClass)
    }
  }
  
  @transparencyPropagating
  def Case[T] = new CaseBuilder[T]
  
  @transparencyPropagating
  def Match[A,R](scrut: A)(cases: Case[A,R]*): R =
    cases.iterator.map(_(scrut)).collectFirst{case Some(r)=>r}.getOrElse(throw new scala.MatchError(scrut))
  
}
