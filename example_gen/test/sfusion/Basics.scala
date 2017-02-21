// Automatically-generated code

// === Init ===

// Transfo time: 0ms  Stringifying time: 87ms

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val x_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]).size;
  val x_3 = scala.`package`.Left.apply[scala.Int, Nothing](x_2);
  val x_4 = new sfusion.Sequence[scala.Int]((() => sfusion.impl.`package`.fromIndexed[scala.Int](((x_1): scala.collection.IndexedSeq[scala.Int]))), x_3);
  val x_5 = x_4.show$default$1;
  x_4.show(x_5)
}

// === Impl ===

// Transfo time: 34ms  Stringifying time: 64ms

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val x_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]).size;
  val x_3 = scala.`package`.Left.apply[scala.Int, Nothing](x_2);
  val x_4 = new sfusion.Sequence[scala.Int]((() => sfusion.impl.`package`.fromIndexed[scala.Int](((x_1): scala.collection.IndexedSeq[scala.Int]))), x_3);
  val x_5 = x_4.show$default$1;
  val x_6 = scala.StringContext.apply("Sequence(", ")");
  var truncated_7: scala.Boolean = true;
  val x_8 = x_4.under;
  val x_9 = x_8.apply();
  val x_10 = sfusion.impl.`package`.onFinish[scala.Int](x_9)((() => truncated_7 = false));
  val withSep_15 = sfusion.impl.`package`.mapHeadTail[scala.Int, java.lang.String](x_10)(((a_11: scala.Int) => {
    val x_12 = scala.StringContext.apply("", "");
    x_12.s(a_11)
  }))(((a_13: scala.Int) => {
    val x_14 = scala.StringContext.apply(",", "");
    x_14.s(a_13)
  }));
  val withTrunc_16 = sfusion.impl.`package`.take[java.lang.String](withSep_15)(x_5);
  val flat_19 = sfusion.impl.`package`.fold[java.lang.String, java.lang.String](withTrunc_16)("")(((x$4_17: java.lang.String, x$5_18: java.lang.String) => x$4_17.+(x$5_18)));
  val x_20 = truncated_7;
  val x_21 = if (x_20)
    flat_19.+(",...")
  else
    flat_19;
  x_6.s(x_21)
}

// === CtorInline ===

// Transfo time: 23ms  Stringifying time: 39ms

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val x_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]).size;
  val x_3 = scala.`package`.Left.apply[scala.Int, Nothing](x_2);
  val x_4 = scala.StringContext.apply("Sequence(", ")");
  var truncated_5: scala.Boolean = true;
  val x_6 = sfusion.impl.`package`.fromIndexed[scala.Int](((x_1): scala.collection.IndexedSeq[scala.Int]));
  val x_7 = sfusion.impl.`package`.onFinish[scala.Int](x_6)((() => truncated_5 = false));
  val withSep_12 = sfusion.impl.`package`.mapHeadTail[scala.Int, java.lang.String](x_7)(((a_8: scala.Int) => {
    val x_9 = scala.StringContext.apply("", "");
    x_9.s(a_8)
  }))(((a_10: scala.Int) => {
    val x_11 = scala.StringContext.apply(",", "");
    x_11.s(a_10)
  }));
  val withTrunc_13 = sfusion.impl.`package`.take[java.lang.String](withSep_12)(10);
  val flat_16 = sfusion.impl.`package`.fold[java.lang.String, java.lang.String](withTrunc_13)("")(((x$4_14: java.lang.String, x$5_15: java.lang.String) => x$4_14.+(x$5_15)));
  val x_17 = truncated_5;
  val x_18 = if (x_17)
    flat_16.+(",...")
  else
    flat_16;
  x_4.s(x_18)
}

// === ImplOptim ===

// Transfo time: 15ms  Stringifying time: 46ms

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val x_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]).size;
  val x_3 = scala.`package`.Left.apply[scala.Int, Nothing](x_2);
  val x_4 = scala.StringContext.apply("Sequence(", ")");
  var truncated_5: scala.Boolean = true;
  val x_6 = sfusion.impl.`package`.fromIndexed[scala.Int](((x_1): scala.collection.IndexedSeq[scala.Int]));
  val x_7 = sfusion.impl.`package`.onFinish[scala.Int](x_6)((() => truncated_5 = false));
  val withSep_12 = sfusion.impl.`package`.mapHeadTail[scala.Int, java.lang.String](x_7)(((a_8: scala.Int) => {
    val x_9 = scala.StringContext.apply("", "");
    x_9.s(a_8)
  }))(((a_10: scala.Int) => {
    val x_11 = scala.StringContext.apply(",", "");
    x_11.s(a_10)
  }));
  val withTrunc_13 = sfusion.impl.`package`.take[java.lang.String](withSep_12)(10);
  val strAcc_14 = new scala.collection.mutable.StringBuilder();
  sfusion.impl.`package`.foreach[java.lang.String](withTrunc_13)(((s_15: java.lang.String) => {
    val x_16 = s_15.toString();
    strAcc_14.++=(x_16);
    ()
  }));
  val x_17 = strAcc_14.result();
  val x_18 = truncated_5;
  val x_19 = if (x_18)
    x_17.+(",...")
  else
    x_17;
  x_4.s(x_19)
}

// === Imperative ===

// Transfo time: 48ms  Stringifying time: 76ms

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val x_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]).size;
  val x_3 = scala.`package`.Left.apply[scala.Int, Nothing](x_2);
  val x_4 = scala.StringContext.apply("Sequence(", ")");
  var truncated_5: scala.Boolean = true;
  val x_6 = ((x_1): scala.collection.IndexedSeq[scala.Int]).length;
  var i_7: scala.Int = 0;
  var first_8: scala.Boolean = true;
  var taken_9: scala.Int = 0;
  val strAcc_10 = new scala.collection.mutable.StringBuilder();
  while ({
    val x_11 = i_7;
    val x_12 = x_11.<(x_6);
    x_12.&&({
      val x_13 = i_7;
      val x_14 = ((x_1): scala.collection.IndexedSeq[scala.Int]).apply(x_13);
      val x_15 = i_7;
      val x_16 = x_15.+(1);
      i_7 = x_16;
      val x_17 = first_8;
      val x_20 = if (x_17)
        {
          first_8 = false;
          val x_18 = scala.StringContext.apply("", "");
          x_18.s(x_14)
        }
      else
        {
          val x_19 = scala.StringContext.apply(",", "");
          x_19.s(x_14)
        };
      val x_21 = taken_9;
      val x_22 = x_21.<(10);
      x_22.&&({
        val x_23 = taken_9;
        val x_24 = x_23.+(1);
        taken_9 = x_24;
        val x_25 = x_20.toString();
        strAcc_10.++=(x_25);
        true.&&({
          val x_26 = taken_9;
          x_26.<(10)
        })
      })
    })
  }) 
    ()
  ;
  val x_27 = i_7;
  val x_28 = x_27.==(x_6);
  if (x_28)
    truncated_5 = false
  else
    ();
  x_28.||({
    val x_29 = taken_9;
    x_29.==(10)
  });
  val x_30 = strAcc_10.result();
  val x_31 = truncated_5;
  val x_32 = if (x_31)
    x_30.+(",...")
  else
    x_30;
  x_4.s(x_32)
}

// === Low-Level Norm ===

// Transfo time: 30ms  Stringifying time: 99ms

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val x_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]).size;
  val x_3 = scala.`package`.Left.apply[scala.Int, Nothing](x_2);
  val x_4 = scala.StringContext.apply("Sequence(", ")");
  var truncated_5: scala.Boolean = true;
  val x_6 = ((x_1): scala.collection.IndexedSeq[scala.Int]).length;
  var i_7: scala.Int = 0;
  var first_8: scala.Boolean = true;
  var taken_9: scala.Int = 0;
  val strAcc_10 = new scala.collection.mutable.StringBuilder();
  while ({
    val x_11 = i_7;
    val x_12 = x_11.<(x_6);
    x_12.&&({
      val x_13 = i_7;
      val x_14 = ((x_1): scala.collection.IndexedSeq[scala.Int]).apply(x_13);
      val x_15 = i_7;
      val x_16 = x_15.+(1);
      i_7 = x_16;
      val x_17 = first_8;
      val x_20 = if (x_17)
        {
          first_8 = false;
          val x_18 = scala.StringContext.apply("", "");
          x_18.s(x_14)
        }
      else
        {
          val x_19 = scala.StringContext.apply(",", "");
          x_19.s(x_14)
        };
      val x_21 = taken_9;
      val x_22 = x_21.<(10);
      x_22.&&({
        val x_23 = taken_9;
        val x_24 = x_23.+(1);
        taken_9 = x_24;
        val x_25 = x_20.toString();
        strAcc_10.++=(x_25);
        val x_26 = taken_9;
        x_26.<(10)
      })
    })
  }) 
    ()
  ;
  val x_27 = i_7;
  val x_28 = x_27.==(x_6);
  if (x_28)
    truncated_5 = false
  else
    ();
  x_28.||({
    val x_29 = taken_9;
    x_29.==(10)
  });
  val x_30 = strAcc_10.result();
  val x_31 = truncated_5;
  val x_32 = if (x_31)
    x_30.+(",...")
  else
    x_30;
  x_4.s(x_32)
}

// === ReNorm (should be the same) ===

// Transfo time: 18ms  Stringifying time: 31ms

// Same as above.
