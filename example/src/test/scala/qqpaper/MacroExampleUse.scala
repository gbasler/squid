package qqpaper

/**
  * Created by lptk on 21/06/17.
  */
object MacroExampleUse extends App {
  import MacroExample._
  
  
  println(power(.5,3))
  //println(dbg_power(.5,3))
  
  //println(dbg_power(readDouble,3))
  
  //println(dbg_power(readDouble,readInt))
  
  
  // FIXME:
  //val x = 0.5
  //println(power(x,3))
  
  // TODO:
  //{
  //  val x = 0.5
  //  println(power(x,3))
  //}
  
  println("Done.")
}
