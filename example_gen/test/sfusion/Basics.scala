// Automatically-generated code

// === Init ===

// Transfo time: 0ms  Stringifying time: 79ms

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val x_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]).size;
  val x_3 = new sfusion.Sequence[scala.Int]((() => sfusion.impl.`package`.fromIndexed[scala.Int](((x_1): scala.collection.IndexedSeq[scala.Int]))), scala.`package`.Left.apply[scala.Int, Nothing](x_2));
  val x_4 = x_3.show$default$1;
  x_3.show(x_4)
}

// === Impl ===

// Transfo time: 22ms  Stringifying time: 58ms

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val x_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]).size;
  val x_3 = new sfusion.Sequence[scala.Int]((() => sfusion.impl.`package`.fromIndexed[scala.Int](((x_1): scala.collection.IndexedSeq[scala.Int]))), scala.`package`.Left.apply[scala.Int, Nothing](x_2));
  val x_4 = x_3.show$default$1;
  val x_5 = scala.StringContext.apply("Sequence(", ")");
  var truncated_6: scala.Boolean = true;
  val x_7 = x_3.under;
  val x_8 = x_7.apply();
  val x_9 = sfusion.impl.`package`.onFinish[scala.Int](x_8)((() => truncated_6 = false));
  val withSep_14 = sfusion.impl.`package`.mapHeadTail[scala.Int, java.lang.String](x_9)(((a_10: scala.Int) => {
    val x_11 = scala.StringContext.apply("", "");
    x_11.s(a_10)
  }))(((a_12: scala.Int) => {
    val x_13 = scala.StringContext.apply(",", "");
    x_13.s(a_12)
  }));
  val withTrunc_15 = sfusion.impl.`package`.take[java.lang.String](withSep_14)(x_4);
  val flat_18 = sfusion.impl.`package`.fold[java.lang.String, java.lang.String](withTrunc_15)("")(((x$4_16: java.lang.String, x$5_17: java.lang.String) => x$4_16.+(x$5_17)));
  val x_19 = truncated_6;
  x_5.s(if (x_19)
    flat_18.+(",...")
  else
    flat_18)
}

// === CtorInline ===

// Transfo time: 21ms  Stringifying time: 35ms

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val x_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]).size;
  val x_3 = scala.StringContext.apply("Sequence(", ")");
  var truncated_4: scala.Boolean = true;
  val x_5 = sfusion.impl.`package`.fromIndexed[scala.Int](((x_1): scala.collection.IndexedSeq[scala.Int]));
  val x_6 = sfusion.impl.`package`.onFinish[scala.Int](x_5)((() => truncated_4 = false));
  val withSep_11 = sfusion.impl.`package`.mapHeadTail[scala.Int, java.lang.String](x_6)(((a_7: scala.Int) => {
    val x_8 = scala.StringContext.apply("", "");
    x_8.s(a_7)
  }))(((a_9: scala.Int) => {
    val x_10 = scala.StringContext.apply(",", "");
    x_10.s(a_9)
  }));
  val withTrunc_12 = sfusion.impl.`package`.take[java.lang.String](withSep_11)(10);
  val flat_15 = sfusion.impl.`package`.fold[java.lang.String, java.lang.String](withTrunc_12)("")(((x$4_13: java.lang.String, x$5_14: java.lang.String) => x$4_13.+(x$5_14)));
  val x_16 = truncated_4;
  x_3.s(if (x_16)
    flat_15.+(",...")
  else
    flat_15)
}

// === ImplOptim ===

// Transfo time: 26ms  Stringifying time: 42ms

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val x_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]).size;
  val x_3 = scala.StringContext.apply("Sequence(", ")");
  var truncated_4: scala.Boolean = true;
  val x_5 = sfusion.impl.`package`.fromIndexed[scala.Int](((x_1): scala.collection.IndexedSeq[scala.Int]));
  val x_6 = sfusion.impl.`package`.onFinish[scala.Int](x_5)((() => truncated_4 = false));
  val withSep_11 = sfusion.impl.`package`.mapHeadTail[scala.Int, java.lang.String](x_6)(((a_7: scala.Int) => {
    val x_8 = scala.StringContext.apply("", "");
    x_8.s(a_7)
  }))(((a_9: scala.Int) => {
    val x_10 = scala.StringContext.apply(",", "");
    x_10.s(a_9)
  }));
  val withTrunc_12 = sfusion.impl.`package`.take[java.lang.String](withSep_11)(10);
  val strAcc_13 = new scala.collection.mutable.StringBuilder();
  sfusion.impl.`package`.foreach[java.lang.String](withTrunc_12)(((s_14: java.lang.String) => {
    val x_15 = s_14.toString();
    strAcc_13.++=(x_15);
    ()
  }));
  val x_16 = strAcc_13.result();
  val x_17 = truncated_4;
  x_3.s(if (x_17)
    x_16.+(",...")
  else
    x_16)
}

// === Imperative ===

// Transfo time: 49ms  Stringifying time: 60ms

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val x_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]).size;
  val x_3 = scala.StringContext.apply("Sequence(", ")");
  var truncated_4: scala.Boolean = true;
  val x_5 = ((x_1): scala.collection.IndexedSeq[scala.Int]).length;
  var i_6: scala.Int = 0;
  var first_7: scala.Boolean = true;
  var taken_8: scala.Int = 0;
  val strAcc_9 = new scala.collection.mutable.StringBuilder();
  while ({
    val x_10 = i_6;
    x_10.<(x_5).&&({
      val x_11 = i_6;
      val x_12 = ((x_1): scala.collection.IndexedSeq[scala.Int]).apply(x_11);
      val x_13 = i_6;
      i_6 = x_13.+(1);
      val x_14 = first_7;
      val x_17 = if (x_14)
        {
          first_7 = false;
          val x_15 = scala.StringContext.apply("", "");
          x_15.s(x_12)
        }
      else
        {
          val x_16 = scala.StringContext.apply(",", "");
          x_16.s(x_12)
        };
      val x_18 = taken_8;
      x_18.<(10).&&({
        val x_19 = taken_8;
        taken_8 = x_19.+(1);
        val x_20 = x_17.toString();
        strAcc_9.++=(x_20);
        true.&&({
          val x_21 = taken_8;
          x_21.<(10)
        })
      })
    })
  }) 
    ()
  ;
  val x_22 = i_6;
  if (x_22.==(x_5))
    truncated_4 = false
  else
    ();
  x_22.==(x_5).||({
    val x_23 = taken_8;
    x_23.==(10)
  });
  val x_24 = strAcc_9.result();
  val x_25 = truncated_4;
  x_3.s(if (x_25)
    x_24.+(",...")
  else
    x_24)
}

// === Low-Level Norm ===

// Transfo time: 24ms  Stringifying time: 63ms

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val x_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]).size;
  val x_3 = scala.StringContext.apply("Sequence(", ")");
  var truncated_4: scala.Boolean = true;
  val x_5 = ((x_1): scala.collection.IndexedSeq[scala.Int]).length;
  var i_6: scala.Int = 0;
  var first_7: scala.Boolean = true;
  var taken_8: scala.Int = 0;
  val strAcc_9 = new scala.collection.mutable.StringBuilder();
  while ({
    val x_10 = i_6;
    x_10.<(x_5).&&({
      val x_11 = i_6;
      val x_12 = ((x_1): scala.collection.IndexedSeq[scala.Int]).apply(x_11);
      val x_13 = i_6;
      i_6 = x_13.+(1);
      val x_14 = first_7;
      val x_17 = if (x_14)
        {
          first_7 = false;
          val x_15 = scala.StringContext.apply("", "");
          x_15.s(x_12)
        }
      else
        {
          val x_16 = scala.StringContext.apply(",", "");
          x_16.s(x_12)
        };
      val x_18 = taken_8;
      x_18.<(10).&&({
        val x_19 = taken_8;
        taken_8 = x_19.+(1);
        val x_20 = x_17.toString();
        strAcc_9.++=(x_20);
        val x_21 = taken_8;
        x_21.<(10)
      })
    })
  }) 
    ()
  ;
  val x_22 = i_6;
  if (x_22.==(x_5))
    truncated_4 = false
  else
    ();
  x_22.==(x_5).||({
    val x_23 = taken_8;
    x_23.==(10)
  });
  val x_24 = strAcc_9.result();
  val x_25 = truncated_4;
  x_3.s(if (x_25)
    x_24.+(",...")
  else
    x_24)
}

// === ReNorm (should be the same) ===

// Transfo time: 18ms  Stringifying time: 32ms

// Same as above.
