package scp
package ir

import scala.collection.mutable
import lang._

import scala.reflect.runtime.{universe => ru}

import ScalaTyping.Variance

/**
  * TODO: add a lock on the HashMap...
  * Also, we could share objects that are equal instead of cluttering the table (but that'd require a bidirectional map)
  */
//private // cannot make this private; needs access to `constants` from reflexive compilation
object AST {
  import scala.tools.reflect.ToolBox
  private lazy val toolBox = ru.runtimeMirror(getClass.getClassLoader).mkToolBox() // Q: necessary 'lazy'? (objects are already lazy)
  
  private var curConstantIndex = 0
  //val constants = new mutable.WeakHashMap[Int, Any] // cannot be weak; otherwise we may lose the object before accessing it...
  val constants = new mutable.HashMap[Int, Any]
}

/** Main language trait, encoding second order lambda calculus with records, let-bindings and ADTs
  * TODO: should have a Liftable trait for constants, with the default impl resulting in storage in glbal hash table (when Scala is the backend) */
trait AST extends Base with ScalaTyping { // TODO rm dep to ScalaTyping
  import AST._
  
  
  object ConstQ extends ConstAPI {
    override def unapply[A: ru.TypeTag, S](x: Q[A, S]): Option[A] = x.rep match {
      case Const(v) if ru.typeOf[A] <:< x.rep.typ.asInstanceOf[ScalaTyping#TypeRep].typ => Some(v.asInstanceOf[A])
      case _ => None
    }
  }
  
  
  
  // TODO move this to some other file
  protected def runRep(r: Rep): Any = {
    val t = toTree(r)
    System.err.println("Compiling tree: "+t)
    toolBox.eval(t)
  }
  protected def toTree(r: Rep): ru.Tree = { // TODO remember (lazy val in Rep)
    import ru._
    
    //println(s"To tree: $r")
    r match {
        
      //case Const(v) => v // we want a tree!
      //case Const(v) => ru.Literal(ru.Constant(v)) // TODOne check correct constant type -- otherwise it raises 'java.lang.Error: bad constant value'
        
      //case Const(v: Int) => q"$v"
      //case Const(v: Double) => q"$v"
      //case Const(v: String) => q"$v"
        
      // Types that can be lifted as literals:
      case Const(_: Int) | Const(_: Long) | Const(_: Float) | Const(_: Double) | Const(_: Byte)
           | Const(_: String) | Const(_: Symbol) | Const(())
           | Const(_: scala.reflect.api.Types#TypeApi) | Const(_: scala.reflect.api.Symbols#SymbolApi) // matching Types#Type is unchecked (erasure)
      => ru.Literal(ru.Constant(r.asInstanceOf[Const[_]].value)) // Note: if bad types passed, raises 'java.lang.Error: bad constant value'
        
      // Common types we provide a lifting for:
      case cst @ Const(vs: Seq[_]) =>
        val tp = cst.typ.asInstanceOf[ScalaTyping#ScalaTypeRep].targs.head
        q"_root_.scala.Seq(..${vs map (v => toTree(Const(v)(TypeEv(tp.asInstanceOf[TypeRep]))))})"
      case cst @ Const(vs: Set[_]) =>
        val tp = cst.typ.asInstanceOf[ScalaTyping#ScalaTypeRep].targs.head
        q"_root_.scala.Predef.Set(..${vs map (v => toTree(Const(v) (TypeEv(tp.asInstanceOf[TypeRep])) ))})"
      case cst @ Const(Some(v)) => 
        val tp = cst.typ.asInstanceOf[ScalaTyping#ScalaTypeRep].targs.head
        q"_root_.scala.Some(${toTree(Const(v) (TypeEv(tp.asInstanceOf[TypeRep])) )})"
      case Const(None) => q"_root_.scala.None"
        
      // Other types of constants are simply stored (forever) in a global hash table:
      case cst @ Const(v) =>
        constants(curConstantIndex) = v
        curConstantIndex += 1
        q"${symbolOf[AST].companion}.constants(${curConstantIndex-1}).asInstanceOf[${cst.typ.asInstanceOf[ScalaTyping#TypeRep].typ}]"
        
      //case Const(v: reflect.ClassTag[_]) => 
      //  println(v.runtimeClass.getName)
      //  q"_root_.scala.reflect.classTag[${v.runtimeClass.getName}]"
        
      //case Const(cl: Class[_]) =>
        //q"${TypeName(cl.getName)}.class" // not valid Scala syntax
        //q"_root_.scala.reflect.classTag[${TypeName(cl.getName)}].runtimeClass" // object classTag is not a member of package reflect
        //q"_root_.scala.reflect.classTag[String].runtimeClass" // object classTag is not a member of package reflect
        
      case Var(n) => q"${TermName(n)}"
      case App(f, a) => q"(${toTree(f)})(${toTree(a)})"
      case as @ Ascribe(v) =>
        //toTree(v)
        q"${toTree(v)}:${as.typ.asInstanceOf[ScalaTyping#TypeRep].typ}"
      case a: Abs =>
        val typ = a.ptyp.asInstanceOf[ScalaTyping#TypeRep].typ // TODO adapt API
        q"(${TermName(a.pname)}: $typ) => ${toTree(a.body)}"
      case dslm @ ModuleObject(fullName, tp) =>
        q"${reflect.runtime.currentMirror.staticModule(fullName)}"
      case dslm @ MethodApp(self, mtd, targs, argss, tp) =>
        val self2 = toTree(self)
        val argss2 = argss map (_ map toTree)
        q"$self2 ${mtd.name} ...$argss2"
      case HoleExtract(n) => throw new Exception(s"Trying to build an open term! Variable '$n' is free.")
    }
  }
  
  
  sealed trait Rep {
    val typ: TypeRep
    
    // TODO is this used?
    def subs(xs: (String, Rep)*): Rep = subs(xs.toMap)
    def subs(xs: Map[String, Rep]): Rep =
      if (xs isEmpty) this else transformPartial(this) { case r @ HoleExtract(n) => xs getOrElse (n, r) }
    
    /** TODO check right type extrusion...
      * 
      * problem: what if 2 vars with same name?
      *   (x:Int) => (x:String) => ...
      * 
      * */
    def extract(t: Rep): Option[Extract] = { // TODO check types
      //println(s"$this << $t")
      
      val r: Option[Extract] = (this, t) match {
        case (HoleExtract(_), Ascribe(v)) => extract(v) // Note: needed for term equivalence to ignore ascriptions
          
        case (HoleExtract(name), _) => // Note: will also extract holes... is it ok?
          // wontTODO replace extruded symbols: not necessary here since we use holes to represent free variables (see case for Abs)
          Some(Map(name -> t) -> Map())
          
        case (Var(n1), HoleExtract(n2)) if n1 == n2 => Some(EmptyExtract) // FIXME safe?
        case (_, HoleExtract(_)) => None
          
        case (v1: Var, v2: Var) =>
          if (v1.name == v2.name) Some(EmptyExtract) else None // TODO check they have the same type?!

        case (_, a @ Ascribe(v)) => extract(v) // TODO test type?
          
        //case (Const(v1), Const(v2)) if v1 == v2 => Some(EmptyExtract)
        case (Const(v1), Const(v2)) => if (v1 == v2) Some(EmptyExtract) else None
        case (App(f1,a1), App(f2,a2)) => for (e1 <- f1 extract f2; e2 <- a1 extract a2; m <- merge(e1, e2)) yield m
        //case (Abs(v1,b1), Abs(v2,b2)) => b1.extract(b2)(binds + (v1 -> v2))
        case (a1: Abs, a2: Abs) =>
          //a1.body.extract(a2.fun(a1.param))
          a1.body.extract(a2.fun(a1.param.toHole))
        case (ModuleObject(fullName1,tp1), ModuleObject(fullName2,tp2)) if fullName1 == fullName2 =>
          Some(EmptyExtract) // Note: not necessary to test the types, right?
        case (MethodApp(self1,mtd1,targs1,args1,tp1), MethodApp(self2,mtd2,targs2,args2,tp2)) // FIXME: is it necessary to test the ret types?
          if mtd1 == mtd2
        =>
          //println(s"$self1")
          assert(args1.size == args2.size)
          assert(targs1.size == targs2.size)
          
          //if (tp1 <:< tp2) // no, if tp1 contains type holes it won't be accepted!
          
          for {
            s <- self1 extract self2
            
            // TODOne check targs & tp
            t <- {
              val targs = (targs1 zip targs2 zip mtd1.typeParams) map { case ((a,b),p) => a extract (b, Variance of p.asType) }
              (Some(EmptyExtract) :: targs).reduce[Option[Extract]] { // `reduce` on non-empty; cf. `Some(EmptyExtract)`
                case (acc, a) => for (acc <- acc; a <- a; m <- merge(acc, a)) yield m }
            }
            
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
            m0 <- merge(s, t)
            m1 <- merge(m0, a)
          } yield m1
        case _ => None
      }
      //println(s">> $r")
      r
    }
    
    
    /* // FIXME creates stack overflow in tests
    override def equals(that: Any) = that match { // TODO override hashCode...
      case that: Rep =>
        //if (super.equals(that)) true else repEq(this, that)
        repEq(this, that)
      case _ => false
    }
    */
  }
  //sealed trait TypeRep
  
  def extract(xtor: Rep, t: Rep): Option[Extract] = xtor.extract(t)//(Map())
  
  
  def const[A: TypeEv](value: A): Rep = Const(value)
  //def abs[A: TypeEv, B: TypeEv](name: String, fun: Rep => Rep): Rep = {
  //  val v = Var(name)(typeRepOf[A])
  //  Abs(v, fun(v))
  //}
  def abs[A: TypeEv, B: TypeEv](name: String, fun: Rep => Rep): Rep = Abs(name, typeRepOf[A], fun)
  def app[A: TypeEv, B: TypeEv](fun: Rep, arg: Rep): Rep = App[A,B](fun, arg)
  
  override def ascribe[A: TypeEv](value: Rep): Rep = Ascribe[A](value)
  
  def moduleObject(fullName: String, tp: TypeRep): Rep = ModuleObject(fullName: String, tp: TypeRep)
  def methodApp(self: Rep, mtd: DSLSymbol, targs: List[TypeRep], argss: List[List[Rep]], tp: TypeRep): Rep =
    MethodApp(self, mtd, targs, argss, tp)
  
  def hole[A: TypeEv](name: String) = HoleExtract[A](name)
  //def hole[A: TypeEv](name: String) = Var(name)(typeRepOf[A])
  
  // TODO rename to Hole
  /**
    * In xtion, represents an extraction hole
    * In ction, represents a free variable
    */
  case class HoleExtract[+A: TypeEv](name: String) extends Rep {
    val typ = typeEv[A].rep
    override def toString = s"($$$$$name: $typ)"
  
    //override def equals(that: Any): Boolean = that match {
    //  case HoleExtract(n) => n == name // TODO check type
    //  case _ => false
    //}
  }
  
  
  case class Var(val name: String)(val typ: TypeRep) extends Rep {
    def toHole = HoleExtract(name)(TypeEv(typ))
    override def toString = s"($name: $typ)"
  }
  
  case class Const[A: TypeEv](value: A) extends Rep {
    val typ = typeEv[A].rep
    override def toString = s"$value"
  }
  //case class Abs(param: Var, body: Rep) extends Rep {
  //  def inline(arg: Rep): Rep = ??? //body withSymbol (param -> arg)
  //case class Abs[A:TypeEv,B](name: String, fun: Rep => Rep) extends Rep {
  //  val param = Var(name)
  case class Abs(pname: String, ptyp: TypeRep, fun: Rep => Rep) extends Rep {
    val param = Var(pname)(ptyp)
    val body = fun(param)
    
    //val typ = funType(TypeEv(ptyp), TypeEv(body.typ)).rep
    val typ = funType(ptyp, body.typ)
    
    def inline(arg: Rep): Rep = ??? //body withSymbol (param -> arg)
    override def toString = body match {
      case that: Abs => s"{ $param, ${that.print(false)} }"
      case _ => print(true)
    }
    def print(paren: Boolean) =
    if (paren) s"{ ($param) => $body }" else s"($param) => $body"
  }
  case class App[A,B: TypeEv](fun: Rep, arg: Rep) extends Rep {
    val typ = typeEv[B].rep
    override def toString = s"$fun $arg"
  }
  
  case class Ascribe[A: TypeEv](value: Rep) extends Rep {
    val typ = typeEv[A].rep
    override def toString = s"$value<:$typ" //s"($value: $typ)"
    
    override def extract(t: Rep): Option[Extract] = {
      val r0 = value.extract(t) getOrElse (return None)
      (typ extract (t.typ, ScalaTyping.Covariant)) flatMap (m => merge(r0, m))
    }
    
    //override def equals(that: Any) = value == that
  }
  
  case class ModuleObject(fullName: String, typ: TypeRep) extends Rep {
    override def toString = fullName
  }
  case class MethodApp(self: Rep, sym: DSLSymbol, targs: List[TypeRep], argss: List[List[Rep]], typ: TypeRep) extends Rep {
    lazy val mtd = DSLDef(sym.fullName, sym.info.toString, sym.isStatic)
    override def toString = s"$self.${mtd.shortName}" +
      (targs.mkString("[",",","]")*(targs.size min 1)) +
      (argss map (_ mkString("(",",",")")) mkString)
  }
  
  
  /**
    * Note: Works well with FVs (represented as holes),
    * since it checks that each extraction extracts exactly a hole with the same name
    */
  def repEq(a: Rep, b: Rep): Boolean = {
    (a extract b, b extract a) match {
      //case (Some((xs,xts)), Some((ys,yts))) => xs.keySet == ys.keySet && xts.keySet == yts.keySet
      case (Some((xs,xts)), Some((ys,yts))) =>
        val extractsHole: ((String, Rep)) => Boolean = {
          case (k: String, HoleExtract(name)) if k == name => true
          case _ => false
        }
        val extractsTypeHole: ((String, TypeRep)) => Boolean = {
          case (k: String, TypeHoleRep(name)) if k == name => true
          case _ => false
        }
        (xs forall extractsHole) && (ys forall extractsHole) && (xts forall extractsTypeHole) && (yts forall extractsTypeHole)
      case _ => false
    }
  }
  
  //def typEq(a: TypeRep, b: TypeRep): Boolean = ???
  
  //implicit def funType[A: TypeEv, B: TypeEv]: TypeEv[A => B] = ???
  
  
  
  def transformPartial(r: Rep)(f: PartialFunction[Rep, Rep]): Rep =
    //transform(r){ case r if f isDefinedAt r => f(r) case r => r}
    transform(r)(r => f applyOrElse (r, identity[Rep]))
  
  def transform(r: Rep)(f: Rep => Rep): Rep = {
    //println(s"Traversing $r")
    val tr = (r: Rep) => transform(r)(f)
    val ret = f(r match {
      //case Abs(p, b) => Abs(p, tr(b))
      case a: Abs => Abs(a.pname, a.ptyp, (x: Rep) => tr(a.fun(x)))
      case a: Ascribe[_] => Ascribe(tr(a.value))(TypeEv(a.typ))
      case ap @ App(fun, a) => App(tr(fun), tr(a))(TypeEv(ap.typ))
      case HoleExtract(name) => r //HoleExtract(name)
      case mo @ ModuleObject(fullName, tp) => mo
      case MethodApp(self, mtd, targs, argss, tp) => MethodApp(tr(self), mtd, targs, argss map (_ map tr), tp)
      //case s: Symbol => s
      case v: Var => v
      case Const(_) => r
    })
    //println(s"Traversing $r, getting $ret")
    //println(s"=> $ret")
    ret
  }
  
  def typ(r: Rep): TypeRep = null.asInstanceOf[TypeRep] // TODO
  
  
  
}















