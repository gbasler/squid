
// This super useful annotation does not exit in Scala 2.11
package scala.annotation { class showAsInfix extends StaticAnnotation }

package squid.utils {
  package object shims { implicit class OptOps[A](private val self: Some[A]) extends AnyVal { def value = self.x } }
}
