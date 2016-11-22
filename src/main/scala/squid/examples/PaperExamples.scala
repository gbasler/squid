//import scp._
//import examples._
//
//import utils.Debug._
//
///**
//  * Created by lptk on 21/03/16.
//  */
//object PaperExamples extends App {
//  import TestDSL._
//  
//  object PowerOptim extends TestDSL.Transformer {
//  new Rewrite[Double] { def apply[C] = {
//    case dsl"math.pow($x, ${ConstQ(n)}:Double)"
//      if n.isValidInt && (0 <= n && n <= 16) =>
//        show(x) // good!
//        show(n) // good!
//        ???
//        //power(n.toInt)(x) 
//  }}}
//  
//}
//
//
//
//
