// Automatically-generated code

// === Init ===

// Transfo time: 0ms  Stringifying time: 151ms

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val x_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]).size;
  val x_3 = new sfusion.Sequence[scala.Int]((() => sfusion.impl.`package`.fromIndexed[scala.Int](((x_1): scala.collection.IndexedSeq[scala.Int]))), scala.`package`.Left.apply[scala.Int, Nothing](x_2));
  val x_4 = x_3.show$default$1;
  x_3.show(x_4)
}

// === Impl ===

// Transfo time: 44ms  Stringifying time: 177ms

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
  val withSep_15 = sfusion.impl.`package`.mapHeadTail[scala.Int, java.lang.String](x_9)(((a_10: scala.Int) => {
    val sch_11 = "";
    val x_12 = scala.StringContext.apply(sch_11, sch_11);
    x_12.s(a_10)
  }))(((a_13: scala.Int) => {
    val x_14 = scala.StringContext.apply(",", "");
    x_14.s(a_13)
  }));
  val withTrunc_16 = sfusion.impl.`package`.take[java.lang.String](withSep_15)(x_4);
  val flat_19 = sfusion.impl.`package`.fold[java.lang.String, java.lang.String](withTrunc_16)("")(((x$4_17: java.lang.String, x$5_18: java.lang.String) => x$4_17.+(x$5_18)));
  val x_20 = truncated_6;
  x_5.s(if (x_20)
    flat_19.+(",...")
  else
    flat_19)
}

// === CtorInline ===

// Transfo time: 36ms  Stringifying time: 85ms

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

// Transfo time: 82ms  Stringifying time: 94ms

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

// Transfo time: 81ms  Stringifying time: 151ms

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

// Transfo time: 0ms  Stringifying time: 57ms

// Same as above.

// === LateImperative ===

// Transfo time: 0ms  Stringifying time: 48ms

// Same as above.

// === VarFlattening ===

// Transfo time: 3ms  Stringifying time: 75ms

// Same as above.

// === Low-Level Norm ===

// Transfo time: 61ms  Stringifying time: 134ms

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

// Transfo time: 40ms  Stringifying time: 49ms

// Same as above.
