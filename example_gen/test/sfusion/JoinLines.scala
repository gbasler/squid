// Automatically-generated code

// === Init ===

// Transfo time: 0ms  Stringifying time: 8ms

((x$1_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesSimple$default$2;
  val x_2 = x$1_0.isEmpty;
  if (x_2)
    ""
  else
    {
      val x_3 = x$1_0.tail;
      val x_5 = new sfusion.Sequence[java.lang.String]((() => {
        val x_4 = x_3.iterator;
        sfusion.impl.`package`.fromIterator[java.lang.String](x_4)
      }), scala.`package`.Right.apply[Nothing, scala.Boolean](false));
      val x_6 = x$1_0.head;
      x_5.fold[java.lang.String](x_6)(((x$8_7: java.lang.String, x$9_8: java.lang.String) => x$8_7.+(x_1).+(x$9_8)))
    }
})

// === Impl ===

// Transfo time: 0ms  Stringifying time: 10ms

((x$1_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesSimple$default$2;
  val x_2 = x$1_0.isEmpty;
  if (x_2)
    ""
  else
    {
      val x_3 = x$1_0.tail;
      val x_5 = new sfusion.Sequence[java.lang.String]((() => {
        val x_4 = x_3.iterator;
        sfusion.impl.`package`.fromIterator[java.lang.String](x_4)
      }), scala.`package`.Right.apply[Nothing, scala.Boolean](false));
      val x_6 = x$1_0.head;
      val x_7 = x_5.under;
      val x_8 = x_7.apply();
      sfusion.impl.`package`.fold[java.lang.String, java.lang.String](x_8)(x_6)(((x$8_9: java.lang.String, x$9_10: java.lang.String) => x$8_9.+(x_1).+(x$9_10)))
    }
})

// === CtorInline ===

// Transfo time: 1ms  Stringifying time: 6ms

((x$1_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesSimple$default$2;
  val x_2 = x$1_0.isEmpty;
  if (x_2)
    ""
  else
    {
      val x_3 = x$1_0.tail;
      val x_4 = x$1_0.head;
      val x_5 = x_3.iterator;
      val x_6 = sfusion.impl.`package`.fromIterator[java.lang.String](x_5);
      sfusion.impl.`package`.fold[java.lang.String, java.lang.String](x_6)(x_4)(((x$8_7: java.lang.String, x$9_8: java.lang.String) => x$8_7.+(x_1).+(x$9_8)))
    }
})

// === ImplOptim ===

// Transfo time: 4ms  Stringifying time: 10ms

((x$1_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesSimple$default$2;
  val x_2 = x$1_0.isEmpty;
  if (x_2)
    ""
  else
    {
      val x_3 = x$1_0.tail;
      val x_4 = x$1_0.head;
      val x_5 = x_3.iterator;
      val x_6 = sfusion.impl.`package`.fromIterator[java.lang.String](x_5);
      val strAcc_7 = new scala.collection.mutable.StringBuilder();
      strAcc_7.++=(x_4);
      sfusion.impl.`package`.foreach[java.lang.String](x_6)(((s_8: java.lang.String) => {
        val x_9 = x_1.toString();
        val x_10 = x_9.+(s_8).toString();
        strAcc_7.++=(x_10);
        ()
      }));
      strAcc_7.result()
    }
})

// === Imperative ===

// Transfo time: 4ms  Stringifying time: 11ms

((x$1_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesSimple$default$2;
  val x_2 = x$1_0.isEmpty;
  if (x_2)
    ""
  else
    {
      val x_3 = x$1_0.tail;
      val x_4 = x$1_0.head;
      val x_5 = x_3.iterator;
      val strAcc_6 = new scala.collection.mutable.StringBuilder();
      strAcc_6.++=(x_4);
      while ({
        val x_7 = x_5.hasNext;
        x_7.&&({
          val x_8 = x_5.next();
          val x_9 = x_1.toString();
          val x_10 = x_9.+(x_8).toString();
          strAcc_6.++=(x_10);
          true
        })
      }) 
        ()
      ;
      val x_11 = x_5.hasNext;
      strAcc_6.result()
    }
})

// === Low-Level Norm ===

// Transfo time: 2ms  Stringifying time: 11ms

((x$1_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesSimple$default$2;
  val x_2 = x$1_0.isEmpty;
  if (x_2)
    ""
  else
    {
      val x_3 = x$1_0.tail;
      val x_4 = x$1_0.head;
      val x_5 = x_3.iterator;
      val strAcc_6 = new scala.collection.mutable.StringBuilder();
      strAcc_6.++=(x_4);
      while ({
        val x_7 = x_5.hasNext;
        if (x_7)
          {
            val x_8 = x_5.next();
            val x_9 = x_1.toString();
            val x_10 = x_9.+(x_8).toString();
            strAcc_6.++=(x_10);
            true
          }
        else
          ();
        x_7
      }) 
        ()
      ;
      val x_11 = x_5.hasNext;
      strAcc_6.result()
    }
})

// === ReNorm (should be the same) ===

// Transfo time: 3ms  Stringifying time: 3ms

// Same as above.
