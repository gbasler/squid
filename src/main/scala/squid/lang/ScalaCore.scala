package squid
package lang

import squid.quasi.mirror

trait ScalaCore
  extends Base
  // ---
  with UnitMirror
  with NullMirror
  with BooleanMirror
  with IntMirror
  with DoubleMirror
  with StringMirror
  // ---
  with OptionMirror
  with EitherMirror
  with FunctionMirror
  // ---
  with Tuple2Mirror
  with Tuple3Mirror
  with Tuple4Mirror
  with Tuple5Mirror
  // ---
  with SquidLibMirror
  with VarMirror
  with PredefMirror

@mirror[Unit] trait UnitMirror
@mirror[Null] trait NullMirror
@mirror[Boolean] trait BooleanMirror
@mirror[Int] trait IntMirror
@mirror[Double] trait DoubleMirror
@mirror[String] trait StringMirror

@mirror[Option[_]] trait OptionMirror
@mirror[Either[_,_]] trait EitherMirror
@mirror[Function[_,_]] trait FunctionMirror

@mirror[Tuple2[_,_]] trait Tuple2Mirror
@mirror[Tuple3[_,_,_]] trait Tuple3Mirror
@mirror[Tuple4[_,_,_,_]] trait Tuple4Mirror
@mirror[Tuple5[_,_,_,_,_]] trait Tuple5Mirror

@mirror[squid.lib.`package`.type] trait SquidLibMirror
@mirror[squid.lib.Var[_]] trait VarMirror
@mirror[Predef.type] trait PredefMirror

