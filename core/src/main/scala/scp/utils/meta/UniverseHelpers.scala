package scp
package utils
package meta

/** Generic helpers for reflection on Universe. */
trait UniverseHelpers[U <: scala.reflect.api.Universe] {
  val uni: U
  import uni._
  
  val Any = typeOf[Any]
  val AnyRef = typeOf[AnyRef]
  val Nothing = typeOf[Nothing]
  val Unit = typeOf[Unit]
  val Boolean = typeOf[Boolean]
  // Can't name pattern variables with a cap letter :-/
  //lazy val List(Any, AnyRef, Nothing, Unit, Boolean) =
  //  List(typeOf[Any], typeOf[AnyRef], typeOf[Nothing], typeOf[Unit], typeOf[Boolean])
  
  
  val sru: scala.reflect.runtime.universe.type = scala.reflect.runtime.universe
  lazy val srum = sru.runtimeMirror(getClass.getClassLoader)
  
  
  lazy val imp = scala.reflect.runtime.universe.internal.createImporter(uni)
  
  
  def mkTag[A](tpe: Type) = {
    import reflect.api
    scala.reflect.runtime.universe.TypeTag[A](srum, new scala.reflect.api.TypeCreator {
      def apply[U <: api.Universe with Singleton](m: api.Mirror[U]): U#Type = tpe.asInstanceOf[U#Type]
    })
  }
  
  
  lazy val ByNameParamClass = uni.definitions.ByNameParamClass
  lazy val RepeatedParamClass = uni.definitions.RepeatedParamClass // JavaRepeatedParamClass
  
  def typeIfNotNothing(tp: Type) = {
    assert(tp != NoType)
    if (tp <:< Nothing) None else Some(tp)
  }
  
  object SelectMember {
    private def getQual(t: Tree): Option[Tree] = t match {
      case q"$a.$m" => Some(a)
      case _ => None
    }
    def unapply(t: Tree): Option[(Tree,Tree)] = (t match {
      case q"$a[..$ts]" if ts.nonEmpty  => getQual(a) map ((_, t))
      case Apply(a, _) => unapply(a)
      case a => getQual(a) map ((_, t))
    }) ensuring(_.map(_._2.symbol != null) getOrElse true, s"[Internal Error] Extracted null symbol for $t")
  }
  
  object MultipleTypeApply {

    def apply(lhs: Tree, targs: List[Tree], argss: List[List[Tree]]): Tree = {
      val tpeApply = if (targs.isEmpty) lhs else TypeApply(lhs, targs)
      argss.foldLeft(tpeApply)((agg, args) => Apply(agg, args))
    }

    def unapply(value: Tree): Option[(Tree, List[Tree], List[List[Tree]])] = value match {
      case Apply(x, y) =>
        Some(x match {
          case MultipleTypeApply(lhs, targs, argss) =>
            (lhs, targs, argss :+ y) // note: inefficient List appension
          case TypeApply(lhs, targs) =>
            (lhs, targs, Nil)
          case _ =>
            (x, Nil, y :: Nil)
        })

      case TypeApply(lhs, targs) =>
        Some((lhs, targs, Nil))

      case _ => None
    }
  }
  
  object FunctionType {
    val Fun0Sym = symbolOf[() => _]
    val FunSym = symbolOf[_ => _]
    val Fun2Sym = symbolOf[(_, _) => _]
    val Fun3Sym = symbolOf[(_, _, _) => _]
    def unapply(t: Type): Option[Type] = Option(t match {
      case TypeRef(_, Fun0Sym, ret::Nil) => ret
      case TypeRef(_, FunSym, _::ret::Nil) => ret
      case TypeRef(_, Fun2Sym, _::_::ret::Nil) => ret
      case TypeRef(_, Fun3Sym, _::_::_::ret::Nil) => ret
      case _ => null
    })
  }
  
  
  implicit class TreeOps(private val self: Tree) {
    def transform(pf: PartialFunction[Tree, Tree]) =  {
      new Transformer {
        override def transform(x: Tree) =
          if (pf isDefinedAt x) pf(x)
          else super.transform(x)
      } transform self
    }
    def transformRec(rec_pf: (Tree => Tree) => PartialFunction[Tree, Tree]) = transformer(rec_pf)(self)
  }
  
  def transformer(rec_pf: (Tree => Tree) => PartialFunction[Tree, Tree]) = {
    new Transformer {
      val pf: PartialFunction[Tree, Tree] = rec_pf(transform)
      override def transform(x: Tree) =
        if (pf isDefinedAt x) pf(x)
        else super.transform(x)
    } transform _
  }
  
  
}

abstract class UniverseHelpersClass[U <: scala.reflect.api.Universe](val uni: U) extends UniverseHelpers[U]

object RuntimeUniverseHelpers extends UniverseHelpersClass[scala.reflect.runtime.universe.type](scala.reflect.runtime.universe)



