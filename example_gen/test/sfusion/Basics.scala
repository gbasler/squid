// Automatically-generated code

// === Init ===

// Transfo time: 0ms  Stringifying time: 98ms

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val sch_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]);
  val x_3 = sch_2.size;
  val x_4 = new sfusion.Sequence[scala.Int]((() => sfusion.impl.`package`.fromIndexed[scala.Int](sch_2)), scala.`package`.Left.apply[scala.Int, scala.Nothing](x_3));
  x_4.show(10)
}

// === HL ===

// Transfo time: 2ms  Stringifying time: 18ms

// Same as above.

// === Impl ===

// Transfo time: 52ms  Stringifying time: 100ms

{
  val sch_0 = "";
  val x_1 = scala.Predef.intWrapper(1);
  val x_2 = x_1.to(10);
  val sch_3 = ((x_2): scala.collection.IndexedSeq[scala.Int]);
  val x_4 = sch_3.size;
  val x_5 = new sfusion.Sequence[scala.Int]((() => sfusion.impl.`package`.fromIndexed[scala.Int](sch_3)), scala.`package`.Left.apply[scala.Int, scala.Nothing](x_4));
  val x_6 = scala.StringContext.apply("Sequence(", ")");
  var truncated_7: scala.Boolean = true;
  val x_8 = x_5.under;
  val x_9 = x_8.apply();
  val x_10 = sfusion.impl.`package`.onFinish[scala.Int](x_9)((() => truncated_7 = false));
  val withSep_15 = sfusion.impl.`package`.mapHeadTail[scala.Int, java.lang.String](x_10)(((a_11: scala.Int) => {
    val x_12 = scala.StringContext.apply(sch_0, sch_0);
    x_12.s(a_11)
  }))(((a_13: scala.Int) => {
    val x_14 = scala.StringContext.apply(",", sch_0);
    x_14.s(a_13)
  }));
  val withTrunc_16 = sfusion.impl.`package`.take[java.lang.String](withSep_15)(10);
  val flat_19 = sfusion.impl.`package`.fold[java.lang.String, java.lang.String](withTrunc_16)(sch_0)(((x$4_17: java.lang.String, x$5_18: java.lang.String) => x$4_17.+(x$5_18)));
  val x_20 = truncated_7;
  x_6.s(if (x_20)
    flat_19.+(",...")
  else
    flat_19)
}

// === CtorInline ===

// Transfo time: 29ms  Stringifying time: 75ms

{
  val sch_0 = "";
  val x_1 = scala.Predef.intWrapper(1);
  val x_2 = x_1.to(10);
  val sch_3 = ((x_2): scala.collection.IndexedSeq[scala.Int]);
  val x_4 = sch_3.size;
  val x_5 = scala.StringContext.apply("Sequence(", ")");
  var truncated_6: scala.Boolean = true;
  val x_7 = sfusion.impl.`package`.fromIndexed[scala.Int](sch_3);
  val x_8 = sfusion.impl.`package`.onFinish[scala.Int](x_7)((() => truncated_6 = false));
  val withSep_13 = sfusion.impl.`package`.mapHeadTail[scala.Int, java.lang.String](x_8)(((a_9: scala.Int) => {
    val x_10 = scala.StringContext.apply(sch_0, sch_0);
    x_10.s(a_9)
  }))(((a_11: scala.Int) => {
    val x_12 = scala.StringContext.apply(",", sch_0);
    x_12.s(a_11)
  }));
  val withTrunc_14 = sfusion.impl.`package`.take[java.lang.String](withSep_13)(10);
  val flat_17 = sfusion.impl.`package`.fold[java.lang.String, java.lang.String](withTrunc_14)(sch_0)(((x$4_15: java.lang.String, x$5_16: java.lang.String) => x$4_15.+(x$5_16)));
  val x_18 = truncated_6;
  x_5.s(if (x_18)
    flat_17.+(",...")
  else
    flat_17)
}

// === ImplOptim ===

// Transfo time: 26ms  Stringifying time: 76ms

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
    strAcc_15.++=(s_16.toString());
    ()
  }));
  val x_17 = strAcc_15.result();
  val x_18 = truncated_5;
  x_4.s(if (x_18)
    x_17.+(",...")
  else
    x_17)
}

// === Imperative ===

// Transfo time: 114ms  Stringifying time: 106ms

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val sch_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]);
  val x_3 = sch_2.size;
  val x_4 = scala.StringContext.apply("Sequence(", ")");
  var truncated_5: scala.Boolean = true;
  val x_6 = sch_2.length;
  var i_7: scala.Int = 0;
  var first_8: scala.Boolean = true;
  var taken_9: scala.Int = 0;
  val strAcc_10 = new scala.collection.mutable.StringBuilder();
  while ({
    val x_11 = i_7;
    x_11.<(x_6).&&({
      val x_12 = i_7;
      val x_13 = sch_2.apply(x_12);
      val x_14 = i_7;
      i_7 = x_14.+(1);
      val x_15 = first_8;
      val x_19 = if (x_15)
        {
          val sch_16 = "";
          first_8 = false;
          val x_17 = scala.StringContext.apply(sch_16, sch_16);
          x_17.s(x_13)
        }
      else
        {
          val x_18 = scala.StringContext.apply(",", "");
          x_18.s(x_13)
        };
      val x_20 = taken_9;
      x_20.<(10).&&({
        val x_21 = taken_9;
        taken_9 = x_21.+(1);
        strAcc_10.++=(x_19.toString());
        true.&&({
          val x_22 = taken_9;
          x_22.<(10)
        })
      })
    })
  }) 
    ()
  ;
  val x_23 = i_7;
  val sch_24 = x_23.==(x_6);
  if (sch_24)
    truncated_5 = false
  else
    ();
  sch_24.||({
    val x_25 = taken_9;
    x_25.==(10)
  });
  val x_26 = strAcc_10.result();
  val x_27 = truncated_5;
  x_4.s(if (x_27)
    x_26.+(",...")
  else
    x_26)
}

// === FlatMapFusion ===

// Transfo time: 2ms  Stringifying time: 93ms

// Same as above.

// === LateImperative ===

// Transfo time: 0ms  Stringifying time: 99ms

// Same as above.

// === VarFlattening ===

// Transfo time: 2ms  Stringifying time: 99ms

// Same as above.

// === Low-Level Norm ===

// Transfo time: 92ms  Stringifying time: 131ms

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val sch_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]);
  val x_3 = sch_2.size;
  val x_4 = scala.StringContext.apply("Sequence(", ")");
  var truncated_5: scala.Boolean = true;
  val x_6 = sch_2.length;
  var i_7: scala.Int = 0;
  var first_8: scala.Boolean = true;
  var taken_9: scala.Int = 0;
  val strAcc_10 = new scala.collection.mutable.StringBuilder();
  while ({
    val x_11 = i_7;
    x_11.<(x_6).&&({
      val x_12 = i_7;
      val x_13 = sch_2.apply(x_12);
      val x_14 = i_7;
      i_7 = x_14.+(1);
      val x_15 = first_8;
      val x_19 = if (x_15)
        {
          val sch_16 = "";
          first_8 = false;
          val x_17 = scala.StringContext.apply(sch_16, sch_16);
          x_17.s(x_13)
        }
      else
        {
          val x_18 = scala.StringContext.apply(",", "");
          x_18.s(x_13)
        };
      val x_20 = taken_9;
      x_20.<(10).&&({
        val x_21 = taken_9;
        taken_9 = x_21.+(1);
        strAcc_10.++=(x_19.toString());
        val x_22 = taken_9;
        x_22.<(10)
      })
    })
  }) 
    ()
  ;
  val x_23 = i_7;
  val sch_24 = x_23.==(x_6);
  if (sch_24)
    truncated_5 = false
  else
    ();
  val x_26 = sch_24.`unary_!`.&&({
    val x_25 = taken_9;
    x_25.==(10).`unary_!`
  });
  val x_27 = strAcc_10.result();
  val x_28 = truncated_5;
  x_4.s(if (x_28)
    x_27.+(",...")
  else
    x_27)
}

// === ReNorm (should be the same) ===

// Transfo time: 55ms  Stringifying time: 99ms

// Same as above.
