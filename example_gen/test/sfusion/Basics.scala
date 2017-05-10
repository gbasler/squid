// Automatically-generated code

// === Init ===

// Transfo time: 0ms  Stringifying time: 90ms

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val x_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]).size;
  val x_3 = new sfusion.Sequence[scala.Int]((() => sfusion.impl.`package`.fromIndexed[scala.Int](((x_1): scala.collection.IndexedSeq[scala.Int]))), scala.`package`.Left.apply[scala.Int, Nothing](x_2));
  x_3.show(10)
}

// === Impl ===

// Transfo time: 31ms  Stringifying time: 73ms

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val x_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]).size;
  val x_3 = new sfusion.Sequence[scala.Int]((() => sfusion.impl.`package`.fromIndexed[scala.Int](((x_1): scala.collection.IndexedSeq[scala.Int]))), scala.`package`.Left.apply[scala.Int, Nothing](x_2));
  val x_4 = scala.StringContext.apply("Sequence(", ")");
  var truncated_5: scala.Boolean = true;
  val x_6 = x_3.under;
  val x_7 = x_6.apply();
  val x_8 = sfusion.impl.`package`.onFinish[scala.Int](x_7)((() => truncated_5 = false));
  val withSep_14 = sfusion.impl.`package`.mapHeadTail[scala.Int, java.lang.String](x_8)(((a_9: scala.Int) => {
    val sch_10 = "";
    val x_11 = scala.StringContext.apply(sch_10, sch_10);
    x_11.s(a_9)
  }))(((a_12: scala.Int) => {
    val x_13 = scala.StringContext.apply(",", "");
    x_13.s(a_12)
  }));
  val withTrunc_15 = sfusion.impl.`package`.take[java.lang.String](withSep_14)(10);
  val flat_18 = sfusion.impl.`package`.fold[java.lang.String, java.lang.String](withTrunc_15)("")(((x$4_16: java.lang.String, x$5_17: java.lang.String) => x$4_16.+(x$5_17)));
  val x_19 = truncated_5;
  x_4.s(if (x_19)
    flat_18.+(",...")
  else
    flat_18)
}

// === CtorInline ===

// Transfo time: 26ms  Stringifying time: 52ms

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val sch_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]);
  val x_3 = sch_2.size;
  val x_4 = scala.StringContext.apply("Sequence(", ")");
  var truncated_5: scala.Boolean = true;
  val x_6 = sfusion.impl.`package`.fromIndexed[scala.Int](sch_2);
  val x_7 = sfusion.impl.`package`.onFinish[scala.Int](x_6)((() => truncated_5 = false));
  val withSep_13 = sfusion.impl.`package`.mapHeadTail[scala.Int, java.lang.String](x_7)(((a_8: scala.Int) => {
    val sch_9 = "";
    val x_10 = scala.StringContext.apply(sch_9, sch_9);
    x_10.s(a_8)
  }))(((a_11: scala.Int) => {
    val x_12 = scala.StringContext.apply(",", "");
    x_12.s(a_11)
  }));
  val withTrunc_14 = sfusion.impl.`package`.take[java.lang.String](withSep_13)(10);
  val flat_17 = sfusion.impl.`package`.fold[java.lang.String, java.lang.String](withTrunc_14)("")(((x$4_15: java.lang.String, x$5_16: java.lang.String) => x$4_15.+(x$5_16)));
  val x_18 = truncated_5;
  x_4.s(if (x_18)
    flat_17.+(",...")
  else
    flat_17)
}

// === ImplOptim ===

// Transfo time: 30ms  Stringifying time: 64ms

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val sch_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]);
  val x_3 = sch_2.size;
  val x_4 = scala.StringContext.apply("Sequence(", ")");
  var truncated_5: scala.Boolean = true;
  val x_6 = sfusion.impl.`package`.fromIndexed[scala.Int](sch_2);
  val x_7 = sfusion.impl.`package`.onFinish[scala.Int](x_6)((() => truncated_5 = false));
  val withSep_13 = sfusion.impl.`package`.mapHeadTail[scala.Int, java.lang.String](x_7)(((a_8: scala.Int) => {
    val sch_9 = "";
    val x_10 = scala.StringContext.apply(sch_9, sch_9);
    x_10.s(a_8)
  }))(((a_11: scala.Int) => {
    val x_12 = scala.StringContext.apply(",", "");
    x_12.s(a_11)
  }));
  val withTrunc_14 = sfusion.impl.`package`.take[java.lang.String](withSep_13)(10);
  val strAcc_15 = new scala.collection.mutable.StringBuilder();
  sfusion.impl.`package`.foreach[java.lang.String](withTrunc_14)(((s_16: java.lang.String) => {
    val x_17 = s_16.toString();
    strAcc_15.++=(x_17);
    ()
  }));
  val x_18 = strAcc_15.result();
  val x_19 = truncated_5;
  x_4.s(if (x_19)
    x_18.+(",...")
  else
    x_18)
}

// === Imperative ===

// Transfo time: 66ms  Stringifying time: 96ms

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
      val x_18 = if (x_14)
        {
          val sch_15 = "";
          first_7 = false;
          val x_16 = scala.StringContext.apply(sch_15, sch_15);
          x_16.s(x_12)
        }
      else
        {
          val x_17 = scala.StringContext.apply(",", "");
          x_17.s(x_12)
        };
      val x_19 = taken_8;
      x_19.<(10).&&({
        val x_20 = taken_8;
        taken_8 = x_20.+(1);
        val x_21 = x_18.toString();
        strAcc_9.++=(x_21);
        true.&&({
          val x_22 = taken_8;
          x_22.<(10)
        })
      })
    })
  }) 
    ()
  ;
  val x_23 = i_6;
  val sch_24 = x_23.==(x_5);
  if (sch_24)
    truncated_4 = false
  else
    ();
  sch_24.||({
    val x_25 = taken_8;
    x_25.==(10)
  });
  val x_26 = strAcc_9.result();
  val x_27 = truncated_4;
  x_3.s(if (x_27)
    x_26.+(",...")
  else
    x_26)
}

// === FlatMapFusion ===

// Transfo time: 0ms  Stringifying time: 58ms

// Same as above.

// === LateImperative ===

// Transfo time: 0ms  Stringifying time: 32ms

// Same as above.

// === VarFlattening ===

// Transfo time: 3ms  Stringifying time: 54ms

// Same as above.

// === Low-Level Norm ===

// Transfo time: 52ms  Stringifying time: 101ms

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
      val x_18 = if (x_14)
        {
          val sch_15 = "";
          first_7 = false;
          val x_16 = scala.StringContext.apply(sch_15, sch_15);
          x_16.s(x_12)
        }
      else
        {
          val x_17 = scala.StringContext.apply(",", "");
          x_17.s(x_12)
        };
      val x_19 = taken_8;
      x_19.<(10).&&({
        val x_20 = taken_8;
        taken_8 = x_20.+(1);
        val x_21 = x_18.toString();
        strAcc_9.++=(x_21);
        val x_22 = taken_8;
        x_22.<(10)
      })
    })
  }) 
    ()
  ;
  val x_23 = i_6;
  val sch_24 = x_23.==(x_5);
  if (sch_24)
    truncated_4 = false
  else
    ();
  val x_26 = sch_24.`unary_!`.&&({
    val x_25 = taken_8;
    x_25.==(10).`unary_!`
  });
  val x_27 = strAcc_9.result();
  val x_28 = truncated_4;
  x_3.s(if (x_28)
    x_27.+(",...")
  else
    x_27)
}

// === ReNorm (should be the same) ===

// Transfo time: 27ms  Stringifying time: 43ms

// Same as above.
