package scp
package ir

import scala.collection.mutable
import lang._

/** Main language trait, encoding second order lambda calculus with records, let-bindings and ADTs */
trait AST extends Base {
  
  sealed trait Rep {
    def subs(xs: (String, Rep)*): Rep = subs(xs.toMap) //if (xs.nonEmpty) subs(xs.toMap) else this
    def subs(xs: Map[String, Rep]): Rep = if (xs isEmpty) this else {
      this match {
        case Abs(p, b) => Abs(p, b.subs(xs - p.name))
        case App(f, a) => App(f.subs(xs), a.subs(xs))
        //case HoleExtract(name) => xs getOrElse (name, this) // xs get name getOrElse this //fold(this)(identity)
        case DSLMethodApp(self, mtd, targs, argss, tp) => DSLMethodApp(self map (_.subs(xs)), mtd, targs, argss map (_ map (_.subs(xs))), tp)
        case v @ Var(n) => xs getOrElse (n, v) //if (xs isDefinedAt n) xs(n) else v
        case Const(_) => this
        case HoleExtract(_) => ??? // TODO
      }
    }
    /** TODO check right type extrusion...
      * 
      * problem: what if 2 vars with same name?
      *   (x:Int) => (x:String) => ...
      * 
      * */
    def extract(t: Rep)(implicit binds: Map[Var, Var]): Option[Extract] = {
      //val reps = mutable.Buffer[(String, SomeRep)]
      //transform(this){
      //  case HoleExtract(name)
      //  case r => r
      //}
      (this, t) match {
        case (HoleExtract(name), _) => // Note: will also extract holes... is it ok?
          //Some(Map(name -> t) -> Map()) 
          // TODO replace extruded symbols
          //???
          //val dom = binds.values.toSet
          //val revBinds = binds.map(_.swap)
          //val extruded = transform(t){
          //  //case s: Symbol if dom(s) => HoleExtract(s.name)(TypeEv(s.tp)) // FIXME: that's not the right name!
          //  case s: Symbol if revBinds isDefinedAt s => HoleExtract(revBinds(s).name)(TypeEv(s.tp)) // FIXME: that's not the right name!
          //  case t => t
          //}
          //val extruded = t subs (revBinds mapValues(_.name))
          val extruded = t subs (binds.mapValues(_.name).map(_.swap))
          Some(Map(name -> extruded) -> Map())
        case (_, HoleExtract(_)) => None
          
        //case (s1: Symbol, s2: Symbol) =>
        //  assert(binds isDefinedAt s1) // safe?
        //  if (binds(s1) == s2) Some(EmptyExtract) else None
        case (v1: Var, v2: Var) =>
          assert(binds isDefinedAt v1) // safe?
          if (binds(v1) == v2) Some(EmptyExtract) else None
          
        //case (Const(v1), Const(v2)) if v1 == v2 => Some(EmptyExtract)
        case (Const(v1), Const(v2)) => if (v1 == v2) Some(EmptyExtract) else None
        case (App(f1,a1), Abs(f2,a2)) => for (e1 <- f1 extract f2; e2 <- a1 extract a2; m <- merge(e1, e2)) yield m
        case (Abs(v1,b1), Abs(v2,b2)) => b1.extract(b2)(binds + (v1 -> v2))
        case (DSLMethodApp(self1,mtd1,targs1,args1,tp1), DSLMethodApp(self2,mtd2,targs2,args2,tp2)) 
          if mtd1 == mtd2
        =>
          assert(args1.size == args2.size)
          assert(targs1.size == targs2.size)
          for {
            //s <- (self1, self2) match { //for (s1 <- self1; s2 <- self2) yield s1
            //  case (Some(s1), Some(s2)) => Some(s1 extract s2)
            //  case (None, None) => Some(None)
            //  case _ => None
            //}
            s <- (self1, self2) match {
              case (Some(s1), Some(s2)) => s1 extract s2
              case (None, None) => None
              case _ => Some(EmptyExtract)
            }
            // TODO check targs & tp
            
            //ao <- (args1 zip args2) flatMap { case (as,bs) => assert(as.size==bs.size); (as zip bs) map { case (a,b) =>  a extract b } }
            //a <- ao
            a <- {
              val args = (args1 zip args2) flatMap {
                case (as,bs) => assert(as.size==bs.size); (as zip bs) map { case (a,b) => a extract b } }
              //val fargs = args.flatten
              //if (fargs.size == args.size) fargs else None
              //args.foldLeft(EmptyExtract){case(acc,a) => merge(acc,a) getOrElse (return None)}
              (Some(EmptyExtract) :: args).reduce[Option[Extract]] { // `reduce` on non-empty; cf. `Some(EmptyExtract)`
                case (acc, a) => for (acc <- acc; a <- a; m <- merge(acc, a)) yield m }
            }
            m <- merge(s, a)
          } yield m
      }
    }
  }
  //sealed trait TypeRep
  
  def extract(xtor: Rep, t: Rep): Option[Extract] = xtor.extract(t)(Map())
  
  
  def const[A: TypeEv](value: A): Rep = Const(value)
  def abs[A: TypeEv, B: TypeEv](name: String, fun: Rep => Rep): Rep = {
    val v = Var(name)(typeRepOf[A])
    Abs(v, fun(v))
  }
  def app[A: TypeEv, B: TypeEv](fun: Rep, arg: Rep): Rep = App(fun, arg)
  
  //def dslMethodApp[A,S](self: Option[SomeRep], mtd: DSLDef, targs: List[SomeTypeRep], args: List[List[SomeRep]], tp: TypeRep[A], run: Any): Rep[A,S]
  def dslMethodApp(self: Option[Rep], mtd: DSLDef, targs: List[TypeRep], argss: List[List[Rep]], tp: TypeRep): Rep =
    DSLMethodApp(self, mtd, targs, argss, tp)
  
  def hole[A: TypeEv](name: String) = HoleExtract[A](name)
  //def hole[A: TypeEv](name: String) = Var(name)(typeRepOf[A])
  
  // TODO rename to Hole
  /**
    * In xtion, represents an extraction hole
    * In ction, represents a free variable
    */
  case class HoleExtract[+A: TypeEv](name: String) extends Rep {
    val tp = typeEv[A].rep
    override def toString = s"($$$$$name: $tp)"
  }
  
  
  case class Var(val name: String)(val tp: TypeRep) extends Rep {
    override def toString = s"($name: $tp)"
  }
  
  case class Const[A](value: A) extends Rep {
    override def toString = s"$value"
  }
  case class Abs(param: Var, body: Rep) extends Rep {
    def inline(arg: Rep): Rep = ??? //body withSymbol (param -> arg)
    override def toString = body match {
      case that: Abs => s"{ $param, ${that.print(false)} }"
      case _ => print(true)
    }
    def print(paren: Boolean) =
    if (paren) s"{ ($param) => $body }" else s"($param) => $body"
  }
  case class App[A,B](fun: Rep, arg: Rep) extends Rep {
    override def toString = s"$fun $arg"
  }
  
  case class DSLMethodApp(self: Option[Rep], mtd: DSLDef, targs: List[TypeRep], argss: List[List[Rep]], tp: TypeRep) extends Rep {
    override def toString = self.fold(mtd.path.last+".")(_.toString+".") + mtd.shortName +
      (targs.mkString("[",",","]")*(targs.size min 1)) +
      (argss map (_ mkString("(",",",")")) mkString)
  }
  
  
  
  def repEq(a: Rep, b: Rep): Boolean = ???
  //def typEq(a: TypeRep, b: TypeRep): Boolean = ???
  
  //implicit def funType[A: TypeEv, B: TypeEv]: TypeEv[A => B] = ???
  
  
  
  def transform(r: Rep)(f: Rep => Rep): Rep = {
    println(s"Traversing $r")
    val tr = (r: Rep) => transform(r)(f)
    val ret = f(r match {
      case Abs(p, b) => Abs(p, tr(b))
      case App(fun, a) => App(tr(fun), tr(a))
      case HoleExtract(name) => r //HoleExtract(name)
      case DSLMethodApp(self, mtd, targs, argss, tp) => DSLMethodApp(self map tr, mtd, targs, argss map (_ map tr), tp)
      //case s: Symbol => s
      case v: Var => v
      case Const(_) => r
    })
    //println(s"Traversing $r, getting $ret")
    println(s"=> $ret")
    ret
  }
  
  def typ(r: Rep): TypeRep = null.asInstanceOf[TypeRep] // TODO
  
  
  
}















