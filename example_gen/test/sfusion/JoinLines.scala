// Automatically-generated code

// === Init ===

// Transfo time: 0ms  Stringifying time: 10ms

((x$1_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesSimple$default$2;
  val x_2 = x$1_0.isEmpty;
  if (x_2)
    ""
  else
    {
      val x_3 = x$1_0.tail;
      val x_4 = scala.`package`.Right.apply[Nothing, scala.Boolean](false);
      val x_6 = new sfusion.Sequence[java.lang.String]((() => {
        val x_5 = x_3.iterator;
        sfusion.impl.`package`.fromIterator[java.lang.String](x_5)
      }), x_4);
      val x_7 = x$1_0.head;
      x_6.fold[java.lang.String](x_7)(((x$8_8: java.lang.String, x$9_9: java.lang.String) => x$8_8.+(x_1).+(x$9_9)))
    }
})

// === Impl ===

// Transfo time: 1ms  Stringifying time: 13ms

((x$1_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesSimple$default$2;
  val x_2 = x$1_0.isEmpty;
  if (x_2)
    ""
  else
    {
      val x_3 = x$1_0.tail;
      val x_4 = scala.`package`.Right.apply[Nothing, scala.Boolean](false);
      val x_6 = new sfusion.Sequence[java.lang.String]((() => {
        val x_5 = x_3.iterator;
        sfusion.impl.`package`.fromIterator[java.lang.String](x_5)
      }), x_4);
      val x_7 = x$1_0.head;
      val x_8 = x_6.under;
      val x_9 = x_8.apply();
      sfusion.impl.`package`.fold[java.lang.String, java.lang.String](x_9)(x_7)(((x$8_10: java.lang.String, x$9_11: java.lang.String) => x$8_10.+(x_1).+(x$9_11)))
    }
})

// === CtorInline ===

// Transfo time: 2ms  Stringifying time: 10ms

((x$1_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesSimple$default$2;
  val x_2 = x$1_0.isEmpty;
  if (x_2)
    ""
  else
    {
      val x_3 = x$1_0.tail;
      val x_4 = scala.`package`.Right.apply[Nothing, scala.Boolean](false);
      val x_5 = x$1_0.head;
      val x_6 = x_3.iterator;
      val x_7 = sfusion.impl.`package`.fromIterator[java.lang.String](x_6);
      sfusion.impl.`package`.fold[java.lang.String, java.lang.String](x_7)(x_5)(((x$8_8: java.lang.String, x$9_9: java.lang.String) => x$8_8.+(x_1).+(x$9_9)))
    }
})

// === ImplOptim ===

// Transfo time: 9ms  Stringifying time: 14ms

((x$1_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesSimple$default$2;
  val x_2 = x$1_0.isEmpty;
  if (x_2)
    ""
  else
    {
      val x_3 = x$1_0.tail;
      val x_4 = scala.`package`.Right.apply[Nothing, scala.Boolean](false);
      val x_5 = x$1_0.head;
      val x_6 = x_3.iterator;
      val x_7 = sfusion.impl.`package`.fromIterator[java.lang.String](x_6);
      val strAcc_8 = new scala.collection.mutable.StringBuilder();
      strAcc_8.++=(x_5);
      sfusion.impl.`package`.foreach[java.lang.String](x_7)(((s_9: java.lang.String) => {
        val x_10 = x_1.toString();
        val x_11 = x_10.+(s_9).toString();
        strAcc_8.++=(x_11);
        ()
      }));
      strAcc_8.result()
    }
})

// === Imperative ===

// Transfo time: 6ms  Stringifying time: 24ms

((x$1_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesSimple$default$2;
  val x_2 = x$1_0.isEmpty;
  if (x_2)
    ""
  else
    {
      val x_3 = x$1_0.tail;
      val x_4 = scala.`package`.Right.apply[Nothing, scala.Boolean](false);
      val x_5 = x$1_0.head;
      val x_6 = x_3.iterator;
      val strAcc_7 = new scala.collection.mutable.StringBuilder();
      strAcc_7.++=(x_5);
      while ({
        val x_8 = x_6.hasNext;
        x_8.&&({
          val x_9 = x_6.next();
          val x_10 = x_1.toString();
          val x_11 = x_10.+(x_9).toString();
          strAcc_7.++=(x_11);
          true
        })
      }) 
        ()
      ;
      val x_12 = x_6.hasNext;
      x_12.`unary_!`;
      strAcc_7.result()
    }
})

// === Low-Level Norm ===

// Transfo time: 11ms  Stringifying time: 34ms

((x$1_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesSimple$default$2;
  val x_2 = x$1_0.isEmpty;
  if (x_2)
    ""
  else
    {
      val x_3 = x$1_0.tail;
      val x_4 = scala.`package`.Right.apply[Nothing, scala.Boolean](false);
      val x_5 = x$1_0.head;
      val x_6 = x_3.iterator;
      val strAcc_7 = new scala.collection.mutable.StringBuilder();
      strAcc_7.++=(x_5);
      while ({
        val x_8 = x_6.hasNext;
        if (x_8)
          {
            val x_9 = x_6.next();
            val x_10 = x_1.toString();
            val x_11 = x_10.+(x_9).toString();
            strAcc_7.++=(x_11);
            true
          }
        else
          ();
        x_8
      }) 
        ()
      ;
      val x_12 = x_6.hasNext;
      x_12.`unary_!`;
      strAcc_7.result()
    }
})

// === ReNorm (should be the same) ===

// Transfo time: 7ms  Stringifying time: 12ms

// Same as above.
