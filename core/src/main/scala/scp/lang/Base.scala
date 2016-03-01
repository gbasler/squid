package scp.lang

/** Type class to describe scopes */
trait Scope[S]

/*
    Do we really need the type parameters in operations?
    they are no more inferred corectly; cf: dslMethodApp[Nothing,Nothing]
    
 */
/** Main language trait, encoding second order lambda calculus with records, let-bindings and ADTs */
trait Base {
  
  //type Rep[Typ, -Scp]
  //type TypeRep[A]
  type Rep
  type TypeRep
  
  def const[A: TypeEv](value: A): Rep
  def abs[A: TypeEv, B: TypeEv](fun: Rep => Rep): Rep
  def app[A: TypeEv, B: TypeEv](fun: Rep, arg: Rep): Rep
  
  //def dslMethodApp[A,S](self: Option[SomeRep], mtd: DSLDef, targs: List[SomeTypeRep], args: List[List[SomeRep]], tp: TypeRep[A], run: Any): Rep[A,S]
  def dslMethodApp(self: Option[Rep], mtd: DSLDef, targs: List[TypeRep], argss: List[List[Rep]], tp: TypeRep): Rep
  
  
  def repEq(a: Rep, b: Rep): Boolean
  def typEq(a: TypeRep, b: TypeRep): Boolean
  
  implicit def funType[A: TypeEv, B: TypeEv]: TypeEv[A => B]
  
  
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Provided Definitions:
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  sealed case class Quoted[Typ, -Scp](rep: Rep)(implicit val typ: TypeEv[Typ], scp: Scope[Scp])
  type Q[T,-S] = Quoted[T, S] // shortcut
  
  @annotation.implicitNotFound(msg = "Could not find type representation evidence for ${A} (implicit of type TypeEv[${A}])")
  final case class TypeEv[A](rep: TypeRep)
  
  def typeEv[A: TypeEv] = implicitly[TypeEv[A]]
  def typeRepOf[A: TypeEv]: TypeRep = typeEv[A].rep
  
  
  def letin[A: TypeEv, B: TypeEv](value: Rep, body: Rep => Rep): Rep = app[A,B](abs[A,B](body), value)
  def ascribe[A: TypeEv](value: Rep): Rep = value
  
  
  def open(name: String): Nothing = ???
  
  def splice[A: Lift](x: A): A = ??? // TODO better error
  def splice[A,S:Scope](x: Q[A,S]): A = ??? // TODO better error
  //implicit def spliceDeep[A: Lift](x: A): Rep[A] = implicitly[Lift[A]].apply(x)
  //implicit def spliceDeep[A](x: Rep[A]): Rep[A] = x
  implicit def spliceDeep(x: Rep): Rep = x
  
  trait Lift[A] { def apply(x: A): Rep }
  implicit def liftConst[A: TypeEv] = new Lift[A] { def apply(x: A) = const(x) }
  
  
  implicit class RepHelp(private val self: Rep) { // extends AnyVal { // Error: value class may not be a member of another class
    def === (that: Rep): Boolean = repEq(self, that)
  }
  implicit class TypeRepHelp(private val self: TypeRep) { // extends AnyVal { // Error: value class may not be a member of another class
    def =:= (that: TypeRep): Boolean = typEq(self, that)
  }
  
  
  
  //type `type scala.Function1` // TODO possibility not to mix it in
  //def `def scala.Function1.apply::(v1: T1)R`[A: TypeEv, B: TypeEv](self: Rep[A -> B])(arg: Rep[A]) = app(self, arg)
  
}
