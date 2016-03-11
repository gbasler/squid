package scp

import lang._
import quasi._
import ir._

trait MyDSL
object TestDSL extends AST with MyDSL with ScalaTyping with Quasi[MyDSL]


