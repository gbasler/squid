// Automatically-generated code

// === Init ===

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

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val x_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]).size;
  val x_3 = scala.`package`.Left.apply[scala.Int, Nothing](x_2);
  val x_4 = new sfusion.Sequence[scala.Int]((() => sfusion.impl.`package`.fromIndexed[scala.Int](((x_1): scala.collection.IndexedSeq[scala.Int]))), x_3);
  val x_5 = x_4.show$default$1;
  val x_6 = scala.StringContext.apply("Sequence(", ")");
  val sb_7 = new scala.collection.mutable.StringBuilder();
  val x_8 = x_4.under;
  val x_9 = x_8.apply();
  val x_10 = sfusion.impl.`package`.take[scala.Int](x_9)(x_5);
  sfusion.impl.`package`.foreach[scala.Int](x_10)(((e_11: scala.Int) => {
    val x_12 = sb_7.nonEmpty;
    if (x_12)
      sb_7.+=(',')
    else
      ();
    val x_13 = scala.StringContext.apply("", "");
    val x_14 = x_13.s(e_11);
    sb_7.++=(x_14);
    ()
  }));
  var shorten_15: scala.Boolean = false;
  x_9(((x$1_16: scala.Int) => {
    shorten_15 = true;
    false
  }));
  val x_17 = shorten_15;
  if (x_17)
    sb_7.++=(",...")
  else
    ();
  val x_18 = sb_7.result();
  x_6.s(x_18)
}

// === CtorInline ===

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val x_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]).size;
  val x_3 = scala.`package`.Left.apply[scala.Int, Nothing](x_2);
  val x_4 = scala.StringContext.apply("Sequence(", ")");
  val sb_5 = new scala.collection.mutable.StringBuilder();
  val x_6 = sfusion.impl.`package`.fromIndexed[scala.Int](((x_1): scala.collection.IndexedSeq[scala.Int]));
  val x_7 = sfusion.impl.`package`.take[scala.Int](x_6)(10);
  sfusion.impl.`package`.foreach[scala.Int](x_7)(((e_8: scala.Int) => {
    val x_9 = sb_5.nonEmpty;
    if (x_9)
      sb_5.+=(',')
    else
      ();
    val x_10 = scala.StringContext.apply("", "");
    val x_11 = x_10.s(e_8);
    sb_5.++=(x_11);
    ()
  }));
  var shorten_12: scala.Boolean = false;
  x_6(((x$1_13: scala.Int) => {
    shorten_12 = true;
    false
  }));
  val x_14 = shorten_12;
  if (x_14)
    sb_5.++=(",...")
  else
    ();
  val x_15 = sb_5.result();
  x_4.s(x_15)
}

// === Imperative ===

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val x_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]).size;
  val x_3 = scala.`package`.Left.apply[scala.Int, Nothing](x_2);
  val x_4 = scala.StringContext.apply("Sequence(", ")");
  val sb_5 = new scala.collection.mutable.StringBuilder();
  var i_6: scala.Int = 0;
  val len_7 = ((x_1): scala.collection.IndexedSeq[scala.Int]).length;
  var taken_8: scala.Int = 0;
  while ({
    val x_9 = i_6;
    val x_10 = x_9.<(len_7);
    x_10.&&({
      val x_11 = i_6;
      val x_12 = ((x_1): scala.collection.IndexedSeq[scala.Int]).apply(x_11);
      val x_13 = i_6;
      val x_14 = x_13.+(1);
      i_6 = x_14;
      val x_15 = taken_8;
      val x_16 = x_15.<(10);
      x_16.&&({
        val x_17 = taken_8;
        val x_18 = x_17.+(1);
        taken_8 = x_18;
        val x_19 = sb_5.nonEmpty;
        if (x_19)
          sb_5.+=(',')
        else
          ();
        val x_20 = scala.StringContext.apply("", "");
        val x_21 = x_20.s(x_12);
        sb_5.++=(x_21);
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
  val x_24 = x_23.==(len_7);
  x_24.||({
    val x_25 = taken_8;
    x_25.==(10)
  });
  var shorten_26: scala.Boolean = false;
  while ({
    val x_27 = i_6;
    val x_28 = x_27.<(len_7);
    x_28.&&({
      val x_29 = i_6;
      val x_30 = ((x_1): scala.collection.IndexedSeq[scala.Int]).apply(x_29);
      val x_31 = i_6;
      val x_32 = x_31.+(1);
      i_6 = x_32;
      shorten_26 = true;
      false
    })
  }) 
    ()
  ;
  val x_33 = i_6;
  x_33.==(len_7);
  val x_34 = shorten_26;
  if (x_34)
    sb_5.++=(",...")
  else
    ();
  val x_35 = sb_5.result();
  x_4.s(x_35)
}

// === Low-Level Norm ===

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val x_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]).size;
  val x_3 = scala.`package`.Left.apply[scala.Int, Nothing](x_2);
  val x_4 = scala.StringContext.apply("Sequence(", ")");
  val sb_5 = new scala.collection.mutable.StringBuilder();
  var i_6: scala.Int = 0;
  val len_7 = ((x_1): scala.collection.IndexedSeq[scala.Int]).length;
  var taken_8: scala.Int = 0;
  while ({
    val x_9 = i_6;
    val x_10 = x_9.<(len_7);
    x_10.&&({
      val x_11 = i_6;
      val x_12 = ((x_1): scala.collection.IndexedSeq[scala.Int]).apply(x_11);
      val x_13 = i_6;
      val x_14 = x_13.+(1);
      i_6 = x_14;
      val x_15 = taken_8;
      val x_16 = x_15.<(10);
      x_16.&&({
        val x_17 = taken_8;
        val x_18 = x_17.+(1);
        taken_8 = x_18;
        val x_19 = sb_5.nonEmpty;
        if (x_19)
          sb_5.+=(',')
        else
          ();
        val x_20 = scala.StringContext.apply("", "");
        val x_21 = x_20.s(x_12);
        sb_5.++=(x_21);
        val x_22 = taken_8;
        x_22.<(10)
      })
    })
  }) 
    ()
  ;
  val x_23 = i_6;
  val x_24 = x_23.==(len_7);
  x_24.||({
    val x_25 = taken_8;
    x_25.==(10)
  });
  var shorten_26: scala.Boolean = false;
  val x_27 = i_6;
  val x_28 = x_27.<(len_7);
  if (x_28)
    {
      val x_29 = i_6;
      val x_30 = ((x_1): scala.collection.IndexedSeq[scala.Int]).apply(x_29);
      val x_31 = i_6;
      val x_32 = x_31.+(1);
      i_6 = x_32;
      shorten_26 = true;
      false
    }
  else
    ();
  val x_33 = i_6;
  x_33.==(len_7);
  val x_34 = shorten_26;
  if (x_34)
    sb_5.++=(",...")
  else
    ();
  val x_35 = sb_5.result();
  x_4.s(x_35)
}

// === ReNorm (should be the same) ===

{
  val x_0 = scala.Predef.intWrapper(1);
  val x_1 = x_0.to(10);
  val x_2 = ((x_1): scala.collection.IndexedSeq[scala.Int]).size;
  val x_3 = scala.`package`.Left.apply[scala.Int, Nothing](x_2);
  val x_4 = scala.StringContext.apply("Sequence(", ")");
  val sb_5 = new scala.collection.mutable.StringBuilder();
  var i_6: scala.Int = 0;
  val len_7 = ((x_1): scala.collection.IndexedSeq[scala.Int]).length;
  var taken_8: scala.Int = 0;
  while ({
    val x_9 = i_6;
    val x_10 = x_9.<(len_7);
    x_10.&&({
      val x_11 = i_6;
      val x_12 = ((x_1): scala.collection.IndexedSeq[scala.Int]).apply(x_11);
      val x_13 = i_6;
      val x_14 = x_13.+(1);
      i_6 = x_14;
      val x_15 = taken_8;
      val x_16 = x_15.<(10);
      x_16.&&({
        val x_17 = taken_8;
        val x_18 = x_17.+(1);
        taken_8 = x_18;
        val x_19 = sb_5.nonEmpty;
        if (x_19)
          sb_5.+=(',')
        else
          ();
        val x_20 = scala.StringContext.apply("", "");
        val x_21 = x_20.s(x_12);
        sb_5.++=(x_21);
        val x_22 = taken_8;
        x_22.<(10)
      })
    })
  }) 
    ()
  ;
  val x_23 = i_6;
  val x_24 = x_23.==(len_7);
  x_24.||({
    val x_25 = taken_8;
    x_25.==(10)
  });
  var shorten_26: scala.Boolean = false;
  val x_27 = i_6;
  val x_28 = x_27.<(len_7);
  if (x_28)
    {
      val x_29 = i_6;
      val x_30 = ((x_1): scala.collection.IndexedSeq[scala.Int]).apply(x_29);
      val x_31 = i_6;
      val x_32 = x_31.+(1);
      i_6 = x_32;
      shorten_26 = true;
      false
    }
  else
    ();
  val x_33 = i_6;
  x_33.==(len_7);
  val x_34 = shorten_26;
  if (x_34)
    sb_5.++=(",...")
  else
    ();
  val x_35 = sb_5.result();
  x_4.s(x_35)
}
