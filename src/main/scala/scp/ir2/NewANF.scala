package scp
package ir2

import utils._
import utils.Debug.show

import scala.collection.mutable
import scala.{collection => gen}

import utils.meta.{RuntimeUniverseHelpers => ruh}
import ruh.sru

/**
  * Created by lptk on 27/09/16.
  * 
  * Non-effectful bindings should be removed...
  * 
  * Idea: Block(effs: Seq[BoundVal], res: Def)(defs: gen.Map[BoundVal, Def])
  *   everything is associated with a BoundVal, even pure non-trivial expressions (which include lambdas), and their definitions are in defs
  *     (trivial expressions are type ascriptions, module access, etc.)
  * 
  * Extracted reps are Blocks which pull in the necessary effects/defs
  * 
  * 
  * Q: should defs include those of enclosing blocks? how does this work??
  *   3 cases to consdier
  *     extraction / rewriteRep
  *     online transfo / transformRep
  *     offline transfo / transformRep
  * 
  * 
  * 
  * should we really pull in all defs when doing an extract
  *   they're needed to be able to, eg, 'run' the xted term
  * what if some come from effects in outer scopes?
  * 
  * 
  * 
  * Idea:
  *   have a xtion context (would be better to have it in a class like for Reinterpreter)
  *   xtion can look up defs in current context to match subexprs
  *   but holes extract bv's, don't try to find out defn
  *   block matching:
  *     holes vacuum effects in linear order
  *     all holes xted in a block are 'augmented' with the block's defs (to make sure all locally defined symbols are saved, cf they may not be available in outer context)
  * 
  * 
  * TODO don't add effects for bindings to trivial defs like 'Val'
  * 
  * 
  */
class NewANF extends ir2.AST with CurryEncoding with NewANFHelpers {
  
  object ANFDebug extends PublicTraceDebug
  
  sealed trait Rep {
    val typ: TypeRep
    def processed: ProcessedRep // TODO rm & add toBlock
    def processedOption: Option[ProcessedRep]
    override def toString = prettyPrint(processed)
  }
  case class Delayed(ast: Def) extends Rep {
    val typ = ast.typ
    //lazy val processed = terraForm(ast)
    private var isProcessed = false
    def processedOption = if (!isProcessed) None else Some(processed)
    lazy val processed = terraForm(this) oh_and (isProcessed = true)
  }
  sealed trait ProcessedRep extends Rep { val processed = this; lazy val processedOption = Some(this) }
  //case class Block(effs: Seq[(BoundVal, Def)], res: Def)(defs: gen.Map[BoundVal, Def]) extends ProcessedRep {
  case class Block(effs: Seq[BoundVal], res: Def, defs: gen.Map[BoundVal, Def]) extends ProcessedRep {
    assert(effs forall defs.keySet) // comment assert once tested
    val typ = res.typ
    lazy val normDefs = defs // TODO DCE
    lazy val (pureDefs, impureDefs) = normDefs.partition(_._2 isPureDef) // TODO add assertions to check things are right
    def effects = effs
  }
  case class SimpleRep(dfn: Def) extends ProcessedRep {
    val typ = dfn.typ
    /*
    override def equals(that: Any): Bool = dfn -> that match {
      case (v0: Val) -> SimpleRep(v1: Val) => v0 == v1
      case _ => super.equals(that)
    }
    */
  }
  class EffectRep(dfn: Def) extends SimpleRep(dfn)
  
  
  def repType(r: Rep): TypeRep = r.typ
  
  
  def rep(dfn: Def): Rep = dfn match {
    case Abs(p, b) => SimpleRep(Abs(p, b.processed)(dfn.typ))
    case _ => Delayed(dfn)
  }
  
  override def repEq(a: Rep, b: Rep): Boolean = a -> b match {
    case SimpleRep(v0: Val) -> SimpleRep(v1: Val) => v0 == v1
    case _ =>
      val r = super.repEq(a,b)
      r
  }
  
  
  override def wrapConstruct(r: => Rep): Rep = super.wrapConstruct(r.processed)
  override def wrapExtract(r: => Rep): Rep = super.wrapExtract(r.processed)
  
  
  
  protected def getValDef(v: Val) =
    contexts.iterator map (_ get v) collectFirst {case Some(a)=>a}   //and (r => println(s"$v = $r"))
  protected def getValDefOrDie(v: Val) = getValDef(v).get
  
  def dfn(r: Rep) = r match {
    case dl @ Delayed(d) => 
      //???
      d
      //dl.processed
    case SimpleRep(bv: BoundVal) =>
      //contexts collectFirst (bv)
      //val f: Option[Def] => Option[Def] = Some.unapply[Def] _
      //contexts.iterator map (_ get bv) collectFirst {case Some(a)=>a} Else
      getValDef(bv) Else
        //bv
        //{debug("not found: "+bv);???; bv}
        {debug(s"${Console.BOLD+Console.RED}not found: $bv ${Console.RESET}"); bv}
      //*/
      
      
    case SimpleRep(d) => d
    case b: Block =>
      wtf // TODO?
      //b.res
  }
  
  // Not acting at the right level/time
  //override def readVal(v: BoundVal): Rep = if (v.typ =:= sru.typeOf[Unit]) () |> const else super.readVal(v)
  
  
  override protected def freshNameImpl(n: Int) = "#"+n
  
  protected type Context = gen.Map[BoundVal, Def]
  protected val contexts = mutable.Stack[Context]()
  protected def inContext[A](defs: Context)(x: => A) =
    contexts push defs before (try x finally contexts.pop)
  
  
  
  ///*
  //def terraForm(ast: Def) = transformRep()
  protected def terraForm(rep: Rep): Block = {
    //val effs = mutable.Buffer[(BoundVal, Def)]()
    val effs = mutable.Buffer[BoundVal]()
    val defs = mutable.Map[BoundVal, Def]()
    
    // TODO detect if already bound
    def bind(bv: BoundVal, dfn: Def): Unit = if (!(defs isDefinedAt bv)) { // TODO only add to effs if impure
      defs += bv -> dfn
      if (!dfn.isPureDef) effs += bv
    } //oh_and (println(s"Bind $bv = $dfn"))
    /*
    def rebindEffect(v_d: Val -> Def) = {
      val k = v_d._1
      defs get k match {
        case Some(d) =>
          if (v_d._2 =/= d)
            println(s"/!\\ $k = $d is already bound as ${v_d._2}!")
        case None => 
          defs += v_d
      }
    }
    */
    def rebindIfNeeded(v_d: Val -> Def) = {
      val k = v_d._1
      defs get k match {
        case Some(d) =>
          if (v_d._2 =/= d)
            println(s"/!\\ $k = $d is already bound as ${v_d._2}!")
        case None => 
          defs += v_d
      }
    }
    
    def orUnit(d: Def) = if (d.typ =:= sru.typeOf[Unit]) Constant( () ) else d
    
    def bindRep(bv: BoundVal, dfn: Def): SimpleRep = bind(bv, dfn) !> SimpleRep(bv |> orUnit)
    //def bindRep(bv: BoundVal, dfn: Def): SimpleRep = {
    //  assert(bv.typ =:= dfn.typ)
    //  if (bv.typ =:= sru.typeOf[Unit])
    //    //() |> const
    //    SimpleRep(Constant( () ))
    //  else bind(bv, dfn) !> SimpleRep(bv)
    //}
    
    //transformRep(rep)({
    //  case Delayed(ast) => SimpleRep(ast)
    //  case r => r3
    //}, {r => r})
    
    //def rec(d: Def): Def = d match {
    //  case MethodApp(self, Imperative.Symbol, _::Nil, ArgList(effs @ _*)::Args(res)::Nil, typ) =>
    //    ???
    //}
    
    //def rec(r: Rep): SimpleRep = (r.processedOption getOrElse r) match {
    def rec(r: Rep): SimpleRep = r.processedOption Else r match {
        
      case Delayed(ast) => ast match {
          
        //case Apply(RepDef(Abs(p,b)), a) if isConstructing || (!hasHoles(b) && !p.isExtractedBinder) =>
        //  //effs += p -> (a |> rec).dfn
        //  bind(p |> renewVal, (a |> rec).dfn)
        //  //b |> rec // TODO properly renew!!
        //  b.asInstanceOf[Block] |> renew |> rec // TODO properly renew!!
        case Typed(Apply(f, a),t) =>
        //case r @ Apply(f, a) =>
          f |> rec match {
            case SimpleRep(Abs(p,b))  if isConstructing || (!hasHoles(b) && !p.isExtractedBinder)  =>
              
              //show(p, a)
              //show(b, hasHoles(b))
              
              //bind(p, (a |> rec).dfn)
              val a2 = a match {
                case sr: SimpleRep => sr.dfn  // not very useful (?)
                case _ => (a |> rec).dfn
              }
              bind(p, a2)
              b |> rec
              
            //case SimpleRep(Abs(p,b)) =>
            // TODO also inline calls to bound lambdas (but properly renew) ?
            case f => bindRep(freshBoundVal(t), Apply(f, a |> rec, t))
            //case f => bindRep(freshBoundVal(r.typ), Apply(f, a, r.typ))
          }
          
        case Typed(h @ (Hole(_) | SplicedHole(_)), typ) => bindRep(freshBoundVal(typ), h)
          
        case MethodApp(self, Imperative.Symbol, _::Nil, ArgList(effs @ _*)::Args(res)::Nil, typ) =>
          effs foreach rec
          res |> rec
        case MethodApp(self, sym, targs, argss, typ) =>
          val newSelf = self |> rec
          val newArgss = argss map (_.map(this)(rec))
          val d = MethodApp(newSelf, sym, targs, newArgss, typ)
          val bv = freshBoundVal(d.typ)
          //effs += bv -> d
          bindRep(bv, d)
          //SimpleRep(bv)
          
        case d => SimpleRep(d)
      }
      case Block(es, r, ds) =>
        //println("Inlining block "+r)
        //effs ++= es
        //es foreach (effs += _)
        effs ++= (es filterNot defs.keySet)
        //es foreach rebindEffect
        
        //ds foreach (bind _).tupled
        //defs ++= ds
        ds foreach rebindIfNeeded
        
        SimpleRep(r)
      case sr: SimpleRep => sr
    }
    
    val res = rec(rep)
    //val res = {
    //  val res = rec(rep)
    //  if (res.typ =:= sru.typeOf[Unit]) 
    //}
    //Block(effs, res.dfn)(defs)
    //println(res, res.dfn, res.dfn.typ)
    Block(effs, res.dfn |> orUnit/*necessary?*/, defs toMap)
  }
  
  def renewVal(bv: BoundVal) = bindVal(bv.name + freshName, bv.typ, Nil)
  def renew(bl: Block) = ??? //Block(bl.effs, bl.res)
  //*/
  
  //protected def terraForm(rep: Rep): Block = {
  //  transformRep(rep)(identity, {
  //    case Delayed(ast) => SimpleRep(ast)
  //    case r => r
  //  })
  //}
  
  override def transformRep(r: Rep)(pre: Rep => Rep, post: Rep => Rep = identity): Rep =
    ANFDebug.debug(Console.BOLD+"--- NEW TRANSFO ---"+Console.RESET) before
    (new ANFRepTransformer(pre,post))(r)

  class ANFRepTransformer(pre: Rep => Rep, post: Rep => Rep) extends super.RepTransformer(pre, post) {
    val cache = mutable.Map[Int, Rep]()
    
    override def apply(r: Rep): Rep = post {
      //cache.getOrElseUpdate(r.uniqueId, {
      //  val r2 = pre(r)
      //}
      pre(r).processed match {
        case r @ SimpleRep(d) =>
          val d2 = apply(d)
          if (d2 eq d) r else SimpleRep(d2)
        case Block(effs, res, defs) =>
          //contexts push defs
          inContext(defs) {
            Block(effs, res, ???)
          }
      }
    }
    
  }
  
  /** TODO put effects/return in a Rep? (to avoid reboxing them all the time...) */
  override def traverse(f: Rep => Boolean)(r: Rep): Unit = r match {
    //case b: Block => 
    case Block(es,r,ds) => inContext(ds) {
      //val traversed = mutable.HashSet[Val]()
        es foreach (ds andThen (new EffectRep(_)) andThen traverse(f))
        SimpleRep(r) |> traverse(f)
      }
    case _ => super.traverse(f)(r)
  }
  
  
  
  
  
  
  
  def isHole(dfn: Def) = dfn match { case Hole(_) | SplicedHole(_) => true  case _ => false }
  
  
  override def extractRep(xtor: Rep, xtee: Rep): Option[Extract] = super.extractRep(xtor, xtee) map filterANFExtract
  /** TODO also handle vararg reps! */
  protected def filterANFExtract(maps: Extract) = (maps._1 filterKeys (k => !(k startsWith "#")), maps._2, maps._3)
  
  override protected def extract(xtor: Rep, xtee: Rep): Extract_? = {
    
    def withBinding(v0: BoundVal, v1: BoundVal): Extract_? => Extract_? =
      _ flatMap (merge(_, repExtract(""+v0.name -> SimpleRep(v1))))
    
    //xtor.processed -> xtee.processed match {
    xtor -> xtee.processed match { // FIXME needs to be processed?
      case SimpleRep(v0: BoundVal) -> SimpleRep(v1: BoundVal) =>
        //???
        //Some(Map(v0.name -> xtee),Map(),Map())
        //super.extract(xtor, xtee) flatMap (e => merge(e, (Map(v0.name -> xtee),Map(),Map())))
        //super.extract(xtor, xtee) flatMap (merge(_, repExtract(v0.name -> xtee)))
        
        //super.extract(xtor, xtee) |> withBinding(v0, v1)
        //EmptyExtract |> some |> withBinding(v0, v1)
        extractType(v0.typ, v1.typ, Covariant) |> withBinding(v0, v1)
        
      case SimpleRep(_) -> SimpleRep(_) => 
        //???
        super.extract(xtor, xtee)
      case (b0 @ Block(es0,r0,d0)) -> (b1 @ Block(es1,r1,d1)) => inContext(d1) {
        debug("Matching blocks:")
        debug(b0.toString.indent)
        debug(b1.toString.indent)
        
        /*
        //val defs0 = es0 :+ r0
        //val defs1 = es1 :+ r1
        val defs0 = es0 map d0
        println(defs0, defs0 indexWhere isHole)
        defs0 indexWhere isHole match {
          case -1 =>
            ???
          case i =>
            //println(i)
            defs0 lastIndexWhere isHole match {
              
            }
        }
        */
        
        /*
        //def rec(left: List[Def], right: List[Def], holes: List[Def]): Option[Extract] = left -> right match {
        def rec(left: List[Def], right: List[Def], passedHole: Boolean): Option[Extract] = left -> right match {
          //case (l :: ls) -> _ if l |> isHole => rec(left, right, holes)
          case (l :: ls) -> _ if l |> isHole => rec(ls, right, true)
          case (l :: ls) -> (r :: rs) => l extractImpl r match {
            //case Some(ex) => mergeOpt()
            case s: Some[_] => mergeOpt(s, rec(ls, rs, true))
            case None if passedHole => rec(left, rs, true)
            case None => None
          }
          case _ => None
        }
        */
        
        // TODO inContext xtee
        // TODO aggregate bv xtion bindings?
        
        //def pairWith[A,B](x: A)(f: A => B) = x -> f(x)
        
        
        type Binding = BoundVal -> Def
        
        //val holeMatchedDefs = new mutable.HashMap[Def,Def] with mutable.DefaultEntry[Def,Def]
        // TODOne use an acc
        //val holeMatchedDefs = mutable.HashMap[Def,List[Def]]() withDefaultValue Nil
        //def rec(left: List[Def], right: List[Def]): Option[Extract] = left -> right match {
        /*
        def rec(matchedHoles: List[BoundVal -> List[BoundVal]])(left: List[Binding], right: List[Binding]): Option[Extract] = left -> right match {
          //case ((lbv, ld) :: ls) -> (r :: rs) if ld |> isHole => rec(ls, right)
          case ((lbv, ld) :: ls) -> _ if ld |> isHole => rec((lbv -> Nil) :: matchedHoles)(ls, right)
          //case (l :: ls) -> (r :: rs) => mergeOpt(d0(l) extractImpl d1(r), rec(ls, rs)) match {
          case (l :: ls) -> (r :: rs) => mergeOpt(l._2 extractImpl SimpleRep(r._1), rec(matchedHoles)(ls, rs)) |> withBinding(l._1, r._1) match {
          //case (l :: ls) -> (r :: rs) => mergeOpt(extract(l, r), rec(ls, rs)) match {
          //  case Some(ex) => mergeOpt()
            case None => None
          }
          case Nil -> Nil => Some(???)
          case _ => None
        }
        //rec(r0 :: es0.reverseIterator.toList, r1 :: es1.reverseIterator.toList, false)
        //rec(r0 :: es0.reverseIterator.toList, r1 :: es1.reverseIterator.toList, Nil)
        //val res = rec(Nil)(r0 :: es0.reverseIterator.toList, r1 :: es1.reverseIterator.toList)
        val res = rec(Nil)(freshBoundVal(r0.typ) -> r0 :: es0.reverseIterator.map(v => v -> d0(v)).toList,
          //freshBoundVal(r1.typ) -> r1 :: es1.map(v => v -> d1(v)).reverseIterator.toList)
          freshBoundVal(r1.typ) -> r1 :: es1.map(pairWith(d1)).reverseIterator.toList)
        */
        
        //def rec(matchedHoles: List[BoundVal -> List[BoundVal]])(left: List[BoundVal], right: List[BoundVal]): /*List[BoundVal -> List[BoundVal]] ->*/ Option[Extract] = left -> right match {
        def rec(matchedHoles: List[BoundVal -> List[BoundVal]])(left: List[BoundVal], right: List[BoundVal]): Option[List[BoundVal -> List[BoundVal]] -> Extract] = left -> right match {
          case (l :: ls) -> _ if d0(l) |> isHole => rec((l -> Nil) :: matchedHoles)(ls, right)
            /*
          case (l :: ls) -> _ =>
            //val hs -> ex = 
            (matchedHoles -> right match {
              case ((h -> vs) :: hs) -> (r :: rs) => rec((h -> (r :: vs)) :: hs)(left, rs)
              case Nil -> _ => None
            //}) orElse mergeOpt(d0(l) extractImpl SimpleRep(r), rec(matchedHoles)(ls, rs)) |> withBinding(l, r)
            //}) orElse (for {hs -> ex <- d0(l) extractImpl SimpleRep(r); r <- rec(matchedHoles)(ls, rs)} yield r) |> withBinding(l, r)
            }) orElse (for {(r,rs) <- right.headOption map (_ -> right.tail); ex <- d0(l) extractImpl SimpleRep(r); hs -> ex2 <- rec(matchedHoles)(ls, rs); m <- merge(ex, ex2) |> withBinding(l, r)} yield hs -> m)
            */
          case _ -> (r :: rs) =>
            (matchedHoles match {
              case ((h -> vs) :: hs) => rec((h -> (r :: vs)) :: hs)(left, rs)
              case Nil => None
            }) orElse (for {
              (l,ls) <- left.headOption map (_ -> left.tail)
              ex <- d0(l) extractImpl new EffectRep(r)
              hs -> ex2 <- rec(matchedHoles)(ls, rs)
              m <- merge(ex, ex2) |> withBinding(l, r)
            } yield hs -> m)
            
          //case Nil -> (r :: rs) => 
          case Nil -> Nil => matchedHoles -> EmptyExtract |> some
          case _ => None
        }
        val res = rec(Nil)(es0.reverseIterator.toList, es1.reverseIterator.toList)
        //show(res)
        
        // ^ Note: the following should really be nested inside the previous rec, which should be tailrec (having a currentExtr param) EDIT: can't be tailrec cf backtracking
        
        val extr = res flatMap {
          //case hs -> ex => (r0 extractImpl SimpleRep(r1)) flatMap (merge(_, ex)) flatMap { ex =>
          case hs -> ex => (SimpleRep(r0) extract SimpleRep(r1)) flatMap (merge(_, ex)) flatMap { ex =>
            
            val newHolesExtract = hs map { case h -> xs => d0(h) match {
              case Hole(name)/* -> xs*/ =>
                //name -> terraForm(xs)
                //val r = ex._1 get name match {
                val r = ex._1 get h.name match {
                  case Some(Block(_,_,_)) => ??? // TODO?
                  //case Some(SimpleRep(bv: BoundVal)) => bv
                  case Some(SimpleRep(d)) => d
                  case Some(_) => ??? // TODO?
                  case None =>
                    //assert(Unit <:< h.typ) // TODO
                    //() |> const
                    () |> Constant
                }
                //terraForm(r) match {
                //  case Block(e,r)
                //}
                //Block(xs, r, Map())
                repExtract(name -> Block(xs, r, d1))
              case SplicedHole(name)/* -> xs*/ =>
                //mkExtract()()(name -> xs) // TODO put in Block with defs? TODO search ex
                ???
            }}
            //newHoles:Int
            //merge(newHolesExtract, ex)
            
            //newHolesExtract.map(some).foldLeft(Option(ex))(mergeOpt)
            newHolesExtract.foldLeft(Option(ex)) {
              case (o, e) => o flatMap (merge(_, e))
            }
            
          }
        }
        
        // TODO check pure symbols
        
        //show(extr)
        
        //System exit 0
        //???
        
        //extr.flatten
        extr
      }
        
      case _ => None // ?
    }
    
    
  }
  
  
  
  
  
  
  
  //override def letin(bound: BoundVal, value: Rep, body: => Rep, bodyType: TypeRep): Rep = super.letin(bound, value, body, bodyType) // TODO
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
}
