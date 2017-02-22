package squid
package lib

import scala.annotation.StaticAnnotation

/** The method has no other effects than the latent effects of closures (or other objects) passed to it. */
class transparent extends StaticAnnotation

/** The method has the same latent effects as that of closures (or other objects) passed to it. */
class transparencyPropagating extends StaticAnnotation

/** The type maintains no state and all its methods are `@transparent`. */
class immutable extends StaticAnnotation

