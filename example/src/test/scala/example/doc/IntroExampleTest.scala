package example.doc

import squid.StaticOptimizer

object IntroExampleTest extends App {
  
  object Stopt extends StaticOptimizer[IntroExample.TestOptimizer]
  import Stopt._
  
  // use `dbg_optimize` instead of `optimize` to print some debug info and make sure the optimization happens
  optimize{ println(Test.foo(1 :: 2 :: 3 :: Nil) + 1) } // expands into just `println(2)`
  
}
