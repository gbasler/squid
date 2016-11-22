package squid.paper

import scala.reflect.runtime.universe.{MethodSymbol => Mtd}

/*

/**
  * Created by lptk on 21/03/16.
  */
abstract
class AST extends Base with DirectStyle { self: Typing =>
  
  //trait Rep[+A] { val typ: TypeRep[A] }
  class Rep[+A](val typ: TypeRep[A])
  
  //case class const[A:TypeRep](value: A) extends Rep[A]
  //case class const[A: AST.this.TypeRep](value: A) extends Rep[A]
  
  def abs[A:TypEv,B:TypEv](name: String, fun: Rep[A] => Rep[B]):Rep[A=>B] = Abs(name, fun)
  case class Abs[A:TypEv,B:TypEv](name: String, fun: Rep[A] => Rep[B])
    extends Rep[A => B](typeRepOf[A => B])
  
  //def app[A:TypeRep,B:TypeRep](fun: Rep[A → B],
  //                             arg: Rep[A]): Rep[B]
  //def const[A:TypeRep](value: A): Rep[A]
  //def unapply_const[A:TypeRep](x: Rep[A]): Option[A]
  //
  //def dslMethodApp[A:TypeRep](self: Option[Rep[_]], mtd: MethodSymbol,
  //  targs: List[TypeRep[_]], argss: List[List[Rep[_]]]): Rep[A]
  //
  //
  //case class Hole[+A: TypeEv](name: String) extends Rep[A] {
  //  val typ = typeEv[A].rep
  //  override def toString = s"($$$$$name: $typ)"
  //}
  
  case class Constant[A: TypEv](value: A) extends Rep(typeRepOf[A])
  
  case class App[A:TypEv,B:TypEv](fun: Rep[A => B], arg: Rep[A]) extends Rep(typeRepOf[B])
  
  
  def freeVar[A: TypEv](name: String) = Hole[A](name)
  def hole[A: TypEv](name: String) = Hole[A](name)
  case class Hole[A:TypEv](name: String) extends Rep(typeRepOf[A])
  case class BoundVar[A:TypEv](name: String) extends Rep(typeRepOf[A])
  
val EmptyExtract: Extract = (Map(), Map())
def extract(xtor: Rep[_], t: Rep[_]): Option[Extract] =
(xtor, t) match {
  case (Constant(v1), Constant(v2)) =>
    if (v1 == v2) Some(EmptyExtract) else None
  case (App(f1,a1), App(f2,a2)) => for {e1 <- extract(f1, f2)
    e2 <- extract(a1, a2); m <- merge(e1, e2) } yield m
  case (a0: Abs[x0,y0], a1: Abs[x1,y1]) => for {
      typExtr <- extractType(a0.typ, a1.typ)
      bodyExtr <- extract(a0.fun(BoundVar[x0](a0.name)),
                          a1.fun(Hole[x1](a0.name)))
      m <- merge(typExtr, bodyExtr) } yield m
  case (Hole(name), _) => Some(Map((name, t)), Map())
  case (BoundVar(n1), Hole(n2)) =>
    if (n1 == n2) Some(EmptyExtract) else None
  case (_, Hole(_)) => None
  // ...
}
  
  // OLD:
  /*
  
  
  val EmptyExtract: Extract = Map() -> Map()
  def extract(xtor: Rep[_], t: Rep[_]): Option[Extract] = (xtor, t) match {
    case (Constant(v1), Constant(v2)) => if (v1 == v2) Some(EmptyExtract) else None
    case (App(f1,a1), App(f2,a2)) => for {e1 <- extract(f1, f2); e2 <- extract(a1, a2); m <- merge(e1, e2)} yield m
    case (a0: Abs[x0,y0], a1: Abs[x1,y1]) =>
      val typExtr = extractType(a0.typ, a1.typ) getOrElse (return None)
      for {bodyExtr <- extract(a0.fun(BoundVar[x0](a0.name)), a1.fun(Hole[x1](a0.name)))
        m <- merge(typExtr, bodyExtr)} yield m
    case (Hole(name), _) => Some(Map(name -> t) -> Map())
    case (BoundVar(n1), Hole(n2)) if n1 == n2 => Some(EmptyExtract) // FIXME safe?
    case (_, Hole(_)) => None
    // ...
  }
      
      
      
    //case (a0: Abs[x,y], a1: Abs[_,_]) =>
    //  //val p = new Symbol[Nothing]("match") // could use a hole here
    //  //val p: Rep[_] = ???
    //  val typExtr = extractType(a0.typ, a1.typ) getOrElse (return None)
    //  val p = BoundVar[x](a0.name)
    //  extract(a0.fun(p), a1.asInstanceOf[Abs[x,y]].fun(Hole[x](a0.name)))
    //  ???
      
    //case (a1: Abs, a2: Abs) =>
    //    //a1.body.extract(a2.fun(a1.param))
    //    a1.body.extract(a2.fun(a1.param.toHole))
  
  */
  
  def dslMethodApp[A:TypEv](self: Option[Rep[_]], mtd: Mtd,
                            targs: List[TypEv[_]], argss: List[List[Rep[_]]]): Rep[A] = ???
  
}

import scala.collection.mutable








abstract
class ANF extends AST { self: Typing =>
  class Symbol[A:TypEv](t: Rep[A]) extends Rep[A](typeRepOf[A])
  case class AbsLets[A:TypEv,B:TypEv]
    (param: BoundVar[A], lets: Seq[Symbol[_]], body: Rep[B])
    extends Rep[A => B](typeRepOf[A => B])
  var scopes = new mutable.Stack[mutable.Buffer[Symbol[_]]]
  override def abs[A:TypEv,B:TypEv](name: String,
                                    fun: Rep[A] => Rep[B]) = {
    val param = new BoundVar[A](name)
    scopes.push(mutable.Buffer[Symbol[_]]())
    try AbsLets(param, body = fun(param), lets = scopes.last)
    finally scopes.pop }
  override def dslMethodApp[A:TypEv](self: Option[Rep[_]],
    mtd: Mtd, targs: List[TypEv[_]], argss: List[List[Rep[_]]])
  = { val t = super.dslMethodApp[A](self, mtd, targs, argss)
    if (scopes.isEmpty) t else { val sym = new Symbol[A](t)
      scopes.last += sym; sym }}
  // ... other defs elided ...
}
*/



// OLD
/*



class ANF extends AST { self: Typing =>
  class Symbol[A:TypEv](t: Rep[A]) extends Rep[A](typeRepOf[A])
  case class Let[A](sym: Symbol[A], value: Rep[A])
  case class AbsLets[A:TypEv,B:TypEv]
    (param: BoundVar[A], lets: Seq[Let[_]], body: Rep[B])
    extends Rep[A => B](typeRepOf[A => B])
  
  var scopes = new mutable.Stack[mutable.Buffer[Let[_]]]
  
  override def abs[A:TypEv,B:TypEv](name: String,
                                    fun: Rep[A] => Rep[B]) = {
    val param = new BoundVar[A](name)
    scopes.push(mutable.Buffer[Let[_]]())
    try AbsLets(param, body = fun(param), lets = scopes.last)
    finally scopes.pop
  }
  override def dslMethodApp[A:TypEv](self: Option[Rep[_]],
    mtd: Mtd, targs: List[TypEv[_]], argss: List[List[Rep[_]]])
  = { val t = super.dslMethodApp[A](self, mtd, targs, argss)
    if (scopes.isEmpty) t else { val sym = new Symbol[A](t)
      scopes.last += Let(sym, t); sym }}
  // ... other defs elided ...
}



abstract
class ANF extends AST { self: Typing =>
  class Symbol[A:TypEv](name: Option[String])
    extends Rep[A](typeRepOf[A])
  case class Let[A:TypEv](sym: Symbol[A], value: Rep[A])
  case class AbsLets[A:TypEv,B:TypEv]
    (param: Symbol[A], lets: Seq[Let[_]], body: Rep[B])
    extends Rep[A => B](typeRepOf[A => B])
  
  var scopes = new mutable.Stack[mutable.Buffer[Let[_]]]
  
  override def abs[A:TypEv,B:TypEv](name: String,
                                    fun: Rep[A] => Rep[B]) = {
    val param = new Symbol[A](Some(name))
    scopes.push(mutable.Buffer[Let[_]]())
    try AbsLets(param, body = fun(param), lets = scopes.last)
    finally scopes.pop
  }
  override def dslMethodApp[A:TypEv](self: Option[Rep[_]],
    mtd: Mtd, targs: List[TypEv[_]], argss: List[List[Rep[_]]])
  = { val t = super.dslMethodApp[A](self, mtd, targs, argss)
    if (scopes.isEmpty) t else { val sym = new Symbol[A](None)
      scopes.last += Let(sym, t); sym }}
  // ... other defs elided ...
}



abstract
class ANF extends AST { self: Typing =>
  class Symbol[A:TypEv](name: Option[String])
    extends Rep[A](typeRepOf[A])
  case class LetBinding[A:TypEv](sym: Symbol[A], value: Rep[A])
  case class AbsDefs[A:TypEv,B:TypEv]
    (param: Symbol[A], lets: Seq[LetBinding[_]], body: Rep[B])
      extends Rep[A => B](typeRepOf[A => B])
  
  var scopes = new mutable.Stack[mutable.Buffer[LetBinding[_]]]
  
  override def abs[A:TypEv,B:TypEv](name: String,
                                    fun: Rep[A] => Rep[B]) = {
    val param = new Symbol[A](Some(name))
    scopes.push(mutable.Buffer[LetBinding[_]]())
    try AbsDefs(param, body = fun(param), lets = scopes.last)
    finally scopes.pop
  }
  override def dslMethodApp[A:TypEv](self: Option[Rep[_]],
    mtd: Mtd, targs: List[TypEv[_]], argss: List[List[Rep[_]]])
  = { val t = super.dslMethodApp[A](self, mtd, targs, argss)
    if (scopes.isEmpty) t else {
      val sym = new Symbol[A](None)
      scopes.last += LetBinding(sym, t)
      sym }}
  // ...
}




abstract
//class ANF extends Base with DirectStyle { self: Typing =>
class ANF extends AST { self: Typing =>
  //class Rep[+A](val typ: TypeRep[A])
  
  class Symbol[A:TypEv](name: Option[String]) extends Rep[A](typeRepOf[A])
  
  //var scopes = mutable.Stack(new mutable.ArrayBuffer[LetBinding[_]])
  var scopes = new mutable.Stack[mutable.ArrayBuffer[LetBinding[_]]]
  
  override def abs[A:TypEv,B:TypEv](name: String, fun: Rep[A] => Rep[B]) = {
    val param = new Symbol[A](Some(name))
    scopes.push(new mutable.ArrayBuffer[LetBinding[_]])
    //val body = 
    try AbsDefs(param, body = fun(param), lets = scopes.last) finally scopes.pop
  }
  
  case class LetBinding[A:TypEv](sym: Symbol[A], value: Rep[A])
  
  case class AbsDefs[A:TypEv,B:TypEv](param: Symbol[A], lets: Seq[LetBinding[_]], body: Rep[B])
  extends Rep[A => B](typeRepOf[A => B])
  //{
  //  val body = {
  //    try fun(param)
  //    finally ???
  //  }
  //}
  
  override def dslMethodApp[A:TypEv](self: Option[Rep[_]], mtd: MethodSymbol,
                            targs: List[TypEv[_]], argss: List[List[Rep[_]]]): Rep[A] = {
    val t = super.dslMethodApp[A](self, mtd, targs, argss)
    if (scopes.isEmpty) t
    else {
      val sym = new Symbol[A](None)
      scopes.last += LetBinding(sym, t)
      sym
    }
  }
  
}
*/



























