import scp._
import examples._
import TestDSL._

def power[C](n: Int)(q: Q[Double,C]): Q[Double,C] =
  if (n == 0) dsl"1.0" else dsl"$q * ${power(n-1)(q)}"

val p3f = dsl"(x: Double) => ${power(3)(dsl"$$x:Double")}"

//p3f.run

//dsl"List(1.0, 2.0).sum" // FIXME
//dsl"List(1.0, 2.0) map ((x: Double) => x+1)"
dsl"List(1.0, 2.0)"

val mls = dsl"List(1.0, 2.0) map $p3f"
//val f = mls match { case dsl"($ls: List[Double]) map $f" => f } // FIXME
//mls match { case dbgdsl"($ls: List[Double]).map($f)($cbf)" => f } // FIXME: not found: type CanBuildFrom


dsl"42.toDouble" : Quoted[Double, {}]


/*
val fun = dsl"(x: Int) => x + 1"
val body = fun match {
  case dsl"(y: Int) => $b: Int" => b
}
val fun2 = dsl"(y: Int) => $body"
*/


dsl"(x: Int) => x + 42 toDouble"


