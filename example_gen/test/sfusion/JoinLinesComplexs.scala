// Automatically-generated code

// === Init ===

// Transfo time: 0ms  Stringifying time: 39ms

((x$2_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesComplex$default$2;
  val x_2 = scala.`package`.Right.apply[Nothing, scala.Boolean](false);
  val x_4 = new sfusion.Sequence[java.lang.String]((() => {
    val x_3 = x$2_0.iterator;
    sfusion.impl.`package`.fromIterator[java.lang.String](x_3)
  }), x_2);
  val x_14 = x_4.flatMap[scala.Char](((str_5: java.lang.String) => {
    val x_6 = scala.Predef.wrapString(x_1);
    val x_7 = ((x_6): scala.collection.IndexedSeq[scala.Char]).size;
    val x_8 = scala.`package`.Left.apply[scala.Int, Nothing](x_7);
    val x_9 = new sfusion.Sequence[scala.Char]((() => sfusion.impl.`package`.fromIndexed[scala.Char](((x_6): scala.collection.IndexedSeq[scala.Char]))), x_8);
    val x_10 = scala.Predef.wrapString(str_5);
    val x_11 = ((x_10): scala.collection.IndexedSeq[scala.Char]).size;
    val x_12 = scala.`package`.Left.apply[scala.Int, Nothing](x_11);
    val x_13 = new sfusion.Sequence[scala.Char]((() => sfusion.impl.`package`.fromIndexed[scala.Char](((x_10): scala.collection.IndexedSeq[scala.Char]))), x_12);
    x_9.++[scala.Char](x_13)
  }));
  val x_15 = x_14.drop(x_1.length());
  x_15.fold[java.lang.String]("")(((x$10_16: java.lang.String, x$11_17: scala.Char) => {
    val x_18 = scala.Predef.augmentString(x$10_16);
    val x_19 = scala.Predef.StringCanBuildFrom;
    x_18.:+[scala.Char, java.lang.String](x$11_17)(x_19)
  }))
})

// === Impl ===

// Transfo time: 16ms  Stringifying time: 55ms

((x$2_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesComplex$default$2;
  val x_2 = scala.`package`.Right.apply[Nothing, scala.Boolean](false);
  val x_4 = new sfusion.Sequence[java.lang.String]((() => {
    val x_3 = x$2_0.iterator;
    sfusion.impl.`package`.fromIterator[java.lang.String](x_3)
  }), x_2);
  val x_5 = scala.`package`.Right.apply[Nothing, scala.Boolean](true);
  val x_26 = new sfusion.Sequence[scala.Char]((() => {
    val x_6 = x_4.under;
    val x_7 = x_6.apply();
    sfusion.impl.`package`.flatMap[java.lang.String, scala.Char](x_7)(((x_8: java.lang.String) => {
      val x_9 = scala.Predef.wrapString(x_1);
      val x_10 = ((x_9): scala.collection.IndexedSeq[scala.Char]).size;
      val x_11 = scala.`package`.Left.apply[scala.Int, Nothing](x_10);
      val x_12 = new sfusion.Sequence[scala.Char]((() => sfusion.impl.`package`.fromIndexed[scala.Char](((x_9): scala.collection.IndexedSeq[scala.Char]))), x_11);
      val x_13 = scala.Predef.wrapString(x_8);
      val x_14 = ((x_13): scala.collection.IndexedSeq[scala.Char]).size;
      val x_15 = scala.`package`.Left.apply[scala.Int, Nothing](x_14);
      val x_16 = new sfusion.Sequence[scala.Char]((() => sfusion.impl.`package`.fromIndexed[scala.Char](((x_13): scala.collection.IndexedSeq[scala.Char]))), x_15);
      val x_17 = x_12.size;
      val x_18 = x_16.size;
      val x_19 = sfusion.`package`.addSizes(x_17, x_18);
      val x_24 = new sfusion.Sequence[scala.Char]((() => {
        val x_20 = x_12.under;
        val x_21 = x_20.apply();
        val x_22 = x_16.under;
        val x_23 = x_22.apply();
        sfusion.impl.`package`.concat[scala.Char](x_21, x_23)
      }), x_19);
      val x_25 = x_24.under;
      x_25.apply()
    }))
  }), x_5);
  val x_27 = x_26.size;
  val x_28 = scala.`package`.Left.apply[scala.Int, Nothing](x_1.length());
  val x_29 = sfusion.`package`.minSize(x_27, x_28);
  val x_32 = new sfusion.Sequence[scala.Char]((() => {
    val x_30 = x_26.under;
    val x_31 = x_30.apply();
    sfusion.impl.`package`.drop[scala.Char](x_31)(x_1.length())
  }), x_29);
  val x_33 = x_32.under;
  val x_34 = x_33.apply();
  sfusion.impl.`package`.fold[scala.Char, java.lang.String](x_34)("")(((x$10_35: java.lang.String, x$11_36: scala.Char) => {
    val x_37 = scala.Predef.augmentString(x$10_35);
    val x_38 = scala.Predef.StringCanBuildFrom;
    x_37.:+[scala.Char, java.lang.String](x$11_36)(x_38)
  }))
})

// === CtorInline ===

// Transfo time: 9ms  Stringifying time: 24ms

((x$2_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesComplex$default$2;
  val x_2 = scala.`package`.Right.apply[Nothing, scala.Boolean](false);
  val x_3 = scala.`package`.Right.apply[Nothing, scala.Boolean](true);
  val x_4 = scala.`package`.Left.apply[scala.Int, Nothing](x_1.length());
  val x_5 = sfusion.`package`.minSize(((x_3): scala.util.Either[scala.Int, scala.Boolean]), x_4);
  val x_6 = x$2_0.iterator;
  val x_7 = sfusion.impl.`package`.fromIterator[java.lang.String](x_6);
  val x_18 = sfusion.impl.`package`.flatMap[java.lang.String, scala.Char](x_7)(((x_8: java.lang.String) => {
    val x_9 = scala.Predef.wrapString(x_1);
    val x_10 = ((x_9): scala.collection.IndexedSeq[scala.Char]).size;
    val x_11 = scala.`package`.Left.apply[scala.Int, Nothing](x_10);
    val x_12 = scala.Predef.wrapString(x_8);
    val x_13 = ((x_12): scala.collection.IndexedSeq[scala.Char]).size;
    val x_14 = scala.`package`.Left.apply[scala.Int, Nothing](x_13);
    val x_15 = sfusion.`package`.addSizes(((x_11): scala.util.Either[scala.Int, scala.Boolean]), ((x_14): scala.util.Either[scala.Int, scala.Boolean]));
    val x_16 = sfusion.impl.`package`.fromIndexed[scala.Char](((x_9): scala.collection.IndexedSeq[scala.Char]));
    val x_17 = sfusion.impl.`package`.fromIndexed[scala.Char](((x_12): scala.collection.IndexedSeq[scala.Char]));
    sfusion.impl.`package`.concat[scala.Char](x_16, x_17)
  }));
  val x_19 = sfusion.impl.`package`.drop[scala.Char](x_18)(x_1.length());
  sfusion.impl.`package`.fold[scala.Char, java.lang.String](x_19)("")(((x$10_20: java.lang.String, x$11_21: scala.Char) => {
    val x_22 = scala.Predef.augmentString(x$10_20);
    val x_23 = scala.Predef.StringCanBuildFrom;
    x_22.:+[scala.Char, java.lang.String](x$11_21)(x_23)
  }))
})

// === ImplOptim ===

// Transfo time: 0ms  Stringifying time: 6ms

// Same as above.

// === Imperative ===

// Transfo time: 32ms  Stringifying time: 86ms

((x$2_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesComplex$default$2;
  val x_2 = scala.`package`.Right.apply[Nothing, scala.Boolean](false);
  val x_3 = scala.`package`.Right.apply[Nothing, scala.Boolean](true);
  val x_4 = scala.`package`.Left.apply[scala.Int, Nothing](x_1.length());
  val x_5 = sfusion.`package`.minSize(((x_3): scala.util.Either[scala.Int, scala.Boolean]), x_4);
  val x_6 = x$2_0.iterator;
  var cur_s_7: scala.Function1[scala.Function1[scala.Char, scala.Boolean], scala.Boolean] = null;
  var dropped_8: scala.Int = 0;
  var cur_9: java.lang.String = "";
  var completed_10: scala.Boolean = false;
  var continue_11: scala.Boolean = false;
  while ({
    val x_12 = cur_s_7;
    val x_13 = x_12.==(null);
    if (x_13)
      {
        while ({
          val x_14 = x_6.hasNext;
          x_14.&&({
            val x_15 = x_6.next();
            val x_16 = scala.Predef.wrapString(x_1);
            val x_17 = ((x_16): scala.collection.IndexedSeq[scala.Char]).size;
            val x_18 = scala.`package`.Left.apply[scala.Int, Nothing](x_17);
            val x_19 = scala.Predef.wrapString(x_15);
            val x_20 = ((x_19): scala.collection.IndexedSeq[scala.Char]).size;
            val x_21 = scala.`package`.Left.apply[scala.Int, Nothing](x_20);
            val x_22 = sfusion.`package`.addSizes(((x_18): scala.util.Either[scala.Int, scala.Boolean]), ((x_21): scala.util.Either[scala.Int, scala.Boolean]));
            val x_23 = ((x_16): scala.collection.IndexedSeq[scala.Char]).length;
            var i_24: scala.Int = 0;
            val x_25 = ((x_19): scala.collection.IndexedSeq[scala.Char]).length;
            var i_26: scala.Int = 0;
            var curIsLhs_27: scala.Boolean = true;
            cur_s_7 = ((k_28: scala.Function1[scala.Char, scala.Boolean]) => {
              var cont_29: scala.Boolean = true;
              var finished_30: scala.Boolean = false;
              while ({
                val x_31 = cont_29;
                x_31.&&({
                  val x_32 = finished_30;
                  x_32.`unary_!`
                })
              }) 
                {
                  var next_33: scala.Option[scala.Char] = scala.None;
                  val x_34 = curIsLhs_27;
                  if (x_34)
                    {
                      while ({
                        val x_35 = i_24;
                        val x_36 = x_35.<(x_23);
                        x_36.&&({
                          val x_37 = i_24;
                          val x_38 = ((x_16): scala.collection.IndexedSeq[scala.Char]).apply(x_37);
                          val x_39 = i_24;
                          val x_40 = x_39.+(1);
                          i_24 = x_40;
                          next_33 = scala.Some.apply[scala.Char](x_38);
                          false
                        })
                      }) 
                        ()
                      ;
                      val x_41 = i_24;
                      val x_42 = x_41.==(x_23);
                      if (x_42)
                        curIsLhs_27 = false
                      else
                        ()
                    }
                  else
                    ();
                  val x_43 = next_33;
                  val x_44 = x_43.isDefined.`unary_!`;
                  if (x_44)
                    {
                      while ({
                        val x_45 = i_26;
                        val x_46 = x_45.<(x_25);
                        x_46.&&({
                          val x_47 = i_26;
                          val x_48 = ((x_19): scala.collection.IndexedSeq[scala.Char]).apply(x_47);
                          val x_49 = i_26;
                          val x_50 = x_49.+(1);
                          i_26 = x_50;
                          next_33 = scala.Some.apply[scala.Char](x_48);
                          false
                        })
                      }) 
                        ()
                      ;
                      val x_51 = i_26;
                      val x_52 = x_51.==(x_25);
                      if (x_52)
                        finished_30 = true
                      else
                        ()
                    }
                  else
                    ();
                  val x_53 = next_33;
                  if (x_53.isDefined)
                    {
                      val x_54 = k_28(x_53.get);
                      cont_29 = x_54
                    }
                  else
                    finished_30 = true
                }
              ;
              finished_30
            });
            false
          })
        }) 
          ()
        ;
        val x_55 = x_6.hasNext;
        x_55.`unary_!`
      }
    else
      ();
    val x_56 = cur_s_7;
    val x_57 = x_56.==(null);
    if (x_57)
      completed_10 = true
    else
      {
        val x_58 = cur_s_7;
        val x_69 = x_58(((b_59: scala.Char) => {
          val x_60 = dropped_8;
          val x_61 = x_60.<(x_1.length());
          val x_68 = if (x_61)
            {
              val x_62 = dropped_8;
              val x_63 = x_62.+(1);
              dropped_8 = x_63;
              true
            }
          else
            {
              val x_64 = cur_9;
              val x_65 = scala.Predef.augmentString(x_64);
              val x_66 = scala.Predef.StringCanBuildFrom;
              val x_67 = x_65.:+[scala.Char, java.lang.String](b_59)(x_66);
              cur_9 = x_67;
              true
            };
          continue_11 = x_68;
          continue_11
        }));
        if (x_69)
          cur_s_7 = null
        else
          ()
      };
    val x_70 = completed_10;
    val x_71 = x_70.`unary_!`;
    x_71.&&(continue_11)
  }) 
    ()
  ;
  completed_10;
  cur_9
})

// === Low-Level Norm ===

// Transfo time: 43ms  Stringifying time: 131ms

((x$2_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesComplex$default$2;
  val x_2 = scala.`package`.Right.apply[Nothing, scala.Boolean](false);
  val x_3 = scala.`package`.Right.apply[Nothing, scala.Boolean](true);
  val x_4 = scala.`package`.Left.apply[scala.Int, Nothing](x_1.length());
  val x_5 = sfusion.`package`.minSize(((x_3): scala.util.Either[scala.Int, scala.Boolean]), x_4);
  val x_6 = x$2_0.iterator;
  var cur_s_7: scala.Function1[scala.Function1[scala.Char, scala.Boolean], scala.Boolean] = null;
  var dropped_8: scala.Int = 0;
  var cur_9: java.lang.String = "";
  var completed_10: scala.Boolean = false;
  var continue_11: scala.Boolean = false;
  while ({
    val x_12 = cur_s_7;
    val x_13 = x_12.==(null);
    if (x_13)
      {
        val x_14 = x_6.hasNext;
        if (x_14)
          {
            val x_15 = x_6.next();
            val x_16 = scala.Predef.wrapString(x_1);
            val x_17 = ((x_16): scala.collection.IndexedSeq[scala.Char]).size;
            val x_18 = scala.`package`.Left.apply[scala.Int, Nothing](x_17);
            val x_19 = scala.Predef.wrapString(x_15);
            val x_20 = ((x_19): scala.collection.IndexedSeq[scala.Char]).size;
            val x_21 = scala.`package`.Left.apply[scala.Int, Nothing](x_20);
            val x_22 = sfusion.`package`.addSizes(((x_18): scala.util.Either[scala.Int, scala.Boolean]), ((x_21): scala.util.Either[scala.Int, scala.Boolean]));
            val x_23 = ((x_16): scala.collection.IndexedSeq[scala.Char]).length;
            var i_24: scala.Int = 0;
            val x_25 = ((x_19): scala.collection.IndexedSeq[scala.Char]).length;
            var i_26: scala.Int = 0;
            var curIsLhs_27: scala.Boolean = true;
            cur_s_7 = ((k_28: scala.Function1[scala.Char, scala.Boolean]) => {
              var cont_29: scala.Boolean = true;
              var finished_30: scala.Boolean = false;
              while ({
                val x_31 = cont_29;
                x_31.&&({
                  val x_32 = finished_30;
                  x_32.`unary_!`
                })
              }) 
                {
                  var isDefined_33: scala.Boolean = false;
                  var optVal_34: scala.Char = '\00';
                  val x_35 = curIsLhs_27;
                  if (x_35)
                    {
                      val x_36 = i_24;
                      val x_37 = x_36.<(x_23);
                      if (x_37)
                        {
                          val x_38 = i_24;
                          val x_39 = ((x_16): scala.collection.IndexedSeq[scala.Char]).apply(x_38);
                          val x_40 = i_24;
                          val x_41 = x_40.+(1);
                          i_24 = x_41;
                          optVal_34 = x_39;
                          isDefined_33 = true;
                          false
                        }
                      else
                        ();
                      val x_42 = i_24;
                      val x_43 = x_42.==(x_23);
                      if (x_43)
                        curIsLhs_27 = false
                      else
                        ()
                    }
                  else
                    ();
                  val x_44 = isDefined_33;
                  if (x_44)
                    ()
                  else
                    {
                      val x_45 = i_26;
                      val x_46 = x_45.<(x_25);
                      if (x_46)
                        {
                          val x_47 = i_26;
                          val x_48 = ((x_19): scala.collection.IndexedSeq[scala.Char]).apply(x_47);
                          val x_49 = i_26;
                          val x_50 = x_49.+(1);
                          i_26 = x_50;
                          optVal_34 = x_48;
                          isDefined_33 = true;
                          false
                        }
                      else
                        ();
                      val x_51 = i_26;
                      val x_52 = x_51.==(x_25);
                      if (x_52)
                        finished_30 = true
                      else
                        ()
                    };
                  val x_53 = isDefined_33;
                  if (x_53)
                    {
                      val x_54 = optVal_34;
                      val x_55 = k_28(x_54);
                      cont_29 = x_55
                    }
                  else
                    finished_30 = true
                }
              ;
              finished_30
            });
            false
          }
        else
          ();
        val x_56 = x_6.hasNext;
        x_56.`unary_!`
      }
    else
      ();
    val x_57 = cur_s_7;
    val x_58 = x_57.==(null);
    if (x_58)
      completed_10 = true
    else
      {
        val x_59 = cur_s_7;
        val x_70 = x_59(((b_60: scala.Char) => {
          val x_61 = dropped_8;
          val x_62 = x_61.<(x_1.length());
          val x_69 = if (x_62)
            {
              val x_63 = dropped_8;
              val x_64 = x_63.+(1);
              dropped_8 = x_64;
              true
            }
          else
            {
              val x_65 = cur_9;
              val x_66 = scala.Predef.augmentString(x_65);
              val x_67 = scala.Predef.StringCanBuildFrom;
              val x_68 = x_66.:+[scala.Char, java.lang.String](b_60)(x_67);
              cur_9 = x_68;
              true
            };
          continue_11 = x_69;
          continue_11
        }));
        if (x_70)
          cur_s_7 = null
        else
          ()
      };
    val x_71 = completed_10;
    val x_72 = x_71.`unary_!`;
    x_72.&&(continue_11)
  }) 
    ()
  ;
  completed_10;
  cur_9
})

// === ReNorm (should be the same) ===

// Transfo time: 23ms  Stringifying time: 49ms

// Same as above.
