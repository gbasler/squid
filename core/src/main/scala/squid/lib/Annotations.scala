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
package lib

import scala.annotation.StaticAnnotation

/** The method has no other effects than the latent effects of closures (or other objects) passed to it. */
class transparent extends StaticAnnotation

/** The method has the same latent effects as that of closures (or other objects) passed to it. */
class transparencyPropagating extends StaticAnnotation

/** The type maintains no state and all its methods are `@transparent`.
  * (This annotation is not yet taken into account by the effect system.) */
class immutable extends StaticAnnotation

