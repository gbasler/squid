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
        val x_23 = {
          val a_19 = x_12;
          val x_20 = sb_5.nonEmpty;
          if (x_20)
            sb_5.+=(',')
          else
            ();
          val x_21 = scala.StringContext.apply("", "");
          val x_22 = x_21.s(a_19);
          sb_5.++=(x_22);
          true
        };
        x_23.&&({
          val x_24 = taken_8;
          x_24.<(10)
        })
      })
    })
  }) 
    ()
  ;
  val x_25 = i_6;
  val x_26 = x_25.==(len_7);
  x_26.||({
    val x_27 = taken_8;
    x_27.==(10)
  });
  var shorten_28: scala.Boolean = false;
  while ({
    val x_29 = i_6;
    val x_30 = x_29.<(len_7);
    x_30.&&({
      val x_31 = i_6;
      val x_32 = ((x_1): scala.collection.IndexedSeq[scala.Int]).apply(x_31);
      val x_33 = i_6;
      val x_34 = x_33.+(1);
      i_6 = x_34;
      val x$1_35 = x_32;
      shorten_28 = true;
      false
    })
  }) 
    ()
  ;
  val x_36 = i_6;
  x_36.==(len_7);
  val x_37 = shorten_28;
  if (x_37)
    sb_5.++=(",...")
  else
    ();
  val x_38 = sb_5.result();
  x_4.s(x_38)
}
