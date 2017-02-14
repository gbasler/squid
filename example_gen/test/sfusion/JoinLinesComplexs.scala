// Automatically-generated code

// === Init ===

// Transfo time: 0ms  Stringifying time: 16ms

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

// Transfo time: 10ms  Stringifying time: 29ms

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
    sfusion.impl.`package`.flatMap[java.lang.String, scala.Char](x_7)(((str_8: java.lang.String) => {
  val x_9 = scala.Predef.wrapString(x_1);
  val x_10 = ((x_9): scala.collection.IndexedSeq[scala.Char]).size;
  val x_11 = scala.`package`.Left.apply[scala.Int, Nothing](x_10);
  val x_12 = new sfusion.Sequence[scala.Char]((() => sfusion.impl.`package`.fromIndexed[scala.Char](((x_9): scala.collection.IndexedSeq[scala.Char]))), x_11);
  val x_13 = scala.Predef.wrapString(str_8);
  val x_14 = ((x_13): scala.collection.IndexedSeq[scala.Char]).size;
  val x_15 = scala.`package`.Left.apply[scala.Int, Nothing](x_14);
  val x_16 = new sfusion.Sequence[scala.Char]((() => sfusion.impl.`package`.fromIndexed[scala.Char](((x_13): scala.collection.IndexedSeq[scala.Char]))), x_15);
  val x_17 = x_12.size;
  val x_18 = x_16.size;
  val x_19 = sfusion.`package`.addSizes(x_17, x_18);
  new sfusion.Sequence[scala.Char]((() => {
    val x_20 = x_12.under;
    val x_21 = x_20.apply();
    val x_22 = x_16.under;
    val x_23 = x_22.apply();
    sfusion.impl.`package`.concat[scala.Char](x_21, x_23)
  }), x_19)
}).andThen[scala.Function1[scala.Function1[scala.Char, scala.Boolean], scala.Boolean]](((x$1_24: sfusion.Sequence[scala.Char]) => {
      val x_25 = x$1_24.under;
      x_25.apply()
    })))
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

// Transfo time: 7ms  Stringifying time: 19ms

((x$2_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesComplex$default$2;
  val x_2 = scala.`package`.Right.apply[Nothing, scala.Boolean](false);
  val x_3 = scala.`package`.Right.apply[Nothing, scala.Boolean](true);
  val x_4 = scala.`package`.Left.apply[scala.Int, Nothing](x_1.length());
  val x_5 = sfusion.`package`.minSize(((x_3): scala.util.Either[scala.Int, scala.Boolean]), x_4);
  val x_6 = x$2_0.iterator;
  val x_7 = sfusion.impl.`package`.fromIterator[java.lang.String](x_6);
  val x_20 = sfusion.impl.`package`.flatMap[java.lang.String, scala.Char](x_7)(((str_8: java.lang.String) => {
  val x_9 = scala.Predef.wrapString(x_1);
  val x_10 = ((x_9): scala.collection.IndexedSeq[scala.Char]).size;
  val x_11 = scala.`package`.Left.apply[scala.Int, Nothing](x_10);
  val x_12 = scala.Predef.wrapString(str_8);
  val x_13 = ((x_12): scala.collection.IndexedSeq[scala.Char]).size;
  val x_14 = scala.`package`.Left.apply[scala.Int, Nothing](x_13);
  val x_15 = sfusion.`package`.addSizes(((x_11): scala.util.Either[scala.Int, scala.Boolean]), ((x_14): scala.util.Either[scala.Int, scala.Boolean]));
  new sfusion.Sequence[scala.Char]((() => {
    val x_16 = sfusion.impl.`package`.fromIndexed[scala.Char](((x_9): scala.collection.IndexedSeq[scala.Char]));
    val x_17 = sfusion.impl.`package`.fromIndexed[scala.Char](((x_12): scala.collection.IndexedSeq[scala.Char]));
    sfusion.impl.`package`.concat[scala.Char](x_16, x_17)
  }), x_15)
}).andThen[scala.Function1[scala.Function1[scala.Char, scala.Boolean], scala.Boolean]](((x$1_18: sfusion.Sequence[scala.Char]) => {
    val x_19 = x$1_18.under;
    x_19.apply()
  })));
  val x_21 = sfusion.impl.`package`.drop[scala.Char](x_20)(x_1.length());
  sfusion.impl.`package`.fold[scala.Char, java.lang.String](x_21)("")(((x$10_22: java.lang.String, x$11_23: scala.Char) => {
    val x_24 = scala.Predef.augmentString(x$10_22);
    val x_25 = scala.Predef.StringCanBuildFrom;
    x_24.:+[scala.Char, java.lang.String](x$11_23)(x_25)
  }))
})

// === ImplOptim ===

// Transfo time: 0ms  Stringifying time: 5ms

// Same as above.

// === Imperative ===

// Transfo time: 34ms  Stringifying time: 69ms

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
            val x_58 = ((str_16: java.lang.String) => {
  val x_17 = scala.Predef.wrapString(x_1);
  val x_18 = ((x_17): scala.collection.IndexedSeq[scala.Char]).size;
  val x_19 = scala.`package`.Left.apply[scala.Int, Nothing](x_18);
  val x_20 = scala.Predef.wrapString(str_16);
  val x_21 = ((x_20): scala.collection.IndexedSeq[scala.Char]).size;
  val x_22 = scala.`package`.Left.apply[scala.Int, Nothing](x_21);
  val x_23 = sfusion.`package`.addSizes(((x_19): scala.util.Either[scala.Int, scala.Boolean]), ((x_22): scala.util.Either[scala.Int, scala.Boolean]));
  new sfusion.Sequence[scala.Char]((() => {
    var i_24: scala.Int = 0;
    val len_25 = ((x_17): scala.collection.IndexedSeq[scala.Char]).length;
    var i_26: scala.Int = 0;
    val len_27 = ((x_20): scala.collection.IndexedSeq[scala.Char]).length;
    var curIsLhs_28: scala.Boolean = true;
    ((k_29: scala.Function1[scala.Char, scala.Boolean]) => {
      var cont_30: scala.Boolean = true;
      var finished_31: scala.Boolean = false;
      while ({
        val x_32 = cont_30;
        x_32.&&({
          val x_33 = finished_31;
          x_33.`unary_!`
        })
      }) 
        {
          var next_34: scala.Option[scala.Char] = scala.None;
          val x_35 = curIsLhs_28;
          if (x_35)
            {
              while ({
                val x_36 = i_24;
                val x_37 = x_36.<(len_25);
                x_37.&&({
                  val x_38 = i_24;
                  val x_39 = ((x_17): scala.collection.IndexedSeq[scala.Char]).apply(x_38);
                  val x_40 = i_24;
                  val x_41 = x_40.+(1);
                  i_24 = x_41;
                  next_34 = scala.Some.apply[scala.Char](x_39);
                  false
                })
              }) 
                ()
              ;
              val x_42 = i_24;
              val x_43 = x_42.==(len_25);
              if (x_43)
                curIsLhs_28 = false
              else
                ()
            }
          else
            ();
          val x_44 = next_34;
          val x_45 = x_44.isDefined.`unary_!`;
          if (x_45)
            {
              while ({
                val x_46 = i_26;
                val x_47 = x_46.<(len_27);
                x_47.&&({
                  val x_48 = i_26;
                  val x_49 = ((x_20): scala.collection.IndexedSeq[scala.Char]).apply(x_48);
                  val x_50 = i_26;
                  val x_51 = x_50.+(1);
                  i_26 = x_51;
                  next_34 = scala.Some.apply[scala.Char](x_49);
                  false
                })
              }) 
                ()
              ;
              val x_52 = i_26;
              val x_53 = x_52.==(len_27);
              if (x_53)
                finished_31 = true
              else
                ()
            }
          else
            ();
          val x_54 = next_34;
          if (x_54.isDefined)
            {
              val x_55 = k_29(x_54.get);
              cont_30 = x_55
            }
          else
            finished_31 = true
        }
      ;
      finished_31
    })
  }), x_23)
}).andThen[scala.Function1[scala.Function1[scala.Char, scala.Boolean], scala.Boolean]](((x$1_56: sfusion.Sequence[scala.Char]) => {
              val x_57 = x$1_56.under;
              x_57.apply()
            }))(x_15);
            cur_s_7 = x_58;
            false
          })
        }) 
          ()
        ;
        val x_59 = x_6.hasNext;
        x_59.`unary_!`
      }
    else
      ();
    val x_60 = cur_s_7;
    val x_61 = x_60.==(null);
    if (x_61)
      completed_10 = true
    else
      {
        val x_62 = cur_s_7;
        val x_73 = x_62(((b_63: scala.Char) => {
          val x_64 = dropped_8;
          val x_65 = x_64.<(x_1.length());
          val x_72 = if (x_65)
            {
              val x_66 = dropped_8;
              val x_67 = x_66.+(1);
              dropped_8 = x_67;
              true
            }
          else
            {
              val x_68 = cur_9;
              val x_69 = scala.Predef.augmentString(x_68);
              val x_70 = scala.Predef.StringCanBuildFrom;
              val x_71 = x_69.:+[scala.Char, java.lang.String](b_63)(x_70);
              cur_9 = x_71;
              true
            };
          continue_11 = x_72;
          continue_11
        }));
        if (x_73)
          cur_s_7 = null
        else
          ()
      };
    val x_74 = completed_10;
    val x_75 = x_74.`unary_!`;
    x_75.&&(continue_11)
  }) 
    ()
  ;
  completed_10;
  cur_9
})

// === Low-Level Norm ===

// Transfo time: 17ms  Stringifying time: 93ms

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
            val x_59 = ((str_16: java.lang.String) => {
  val x_17 = scala.Predef.wrapString(x_1);
  val x_18 = ((x_17): scala.collection.IndexedSeq[scala.Char]).size;
  val x_19 = scala.`package`.Left.apply[scala.Int, Nothing](x_18);
  val x_20 = scala.Predef.wrapString(str_16);
  val x_21 = ((x_20): scala.collection.IndexedSeq[scala.Char]).size;
  val x_22 = scala.`package`.Left.apply[scala.Int, Nothing](x_21);
  val x_23 = sfusion.`package`.addSizes(((x_19): scala.util.Either[scala.Int, scala.Boolean]), ((x_22): scala.util.Either[scala.Int, scala.Boolean]));
  new sfusion.Sequence[scala.Char]((() => {
    var i_24: scala.Int = 0;
    val len_25 = ((x_17): scala.collection.IndexedSeq[scala.Char]).length;
    var i_26: scala.Int = 0;
    val len_27 = ((x_20): scala.collection.IndexedSeq[scala.Char]).length;
    var curIsLhs_28: scala.Boolean = true;
    ((k_29: scala.Function1[scala.Char, scala.Boolean]) => {
      var cont_30: scala.Boolean = true;
      var finished_31: scala.Boolean = false;
      while ({
        val x_32 = cont_30;
        x_32.&&({
          val x_33 = finished_31;
          x_33.`unary_!`
        })
      }) 
        {
          var isDefined_34: scala.Boolean = false;
          var optVal_35: scala.Char = '\00';
          val x_36 = curIsLhs_28;
          if (x_36)
            {
              val x_37 = i_24;
              val x_38 = x_37.<(len_25);
              if (x_38)
                {
                  val x_39 = i_24;
                  val x_40 = ((x_17): scala.collection.IndexedSeq[scala.Char]).apply(x_39);
                  val x_41 = i_24;
                  val x_42 = x_41.+(1);
                  i_24 = x_42;
                  optVal_35 = x_40;
                  isDefined_34 = true;
                  false
                }
              else
                ();
              val x_43 = i_24;
              val x_44 = x_43.==(len_25);
              if (x_44)
                curIsLhs_28 = false
              else
                ()
            }
          else
            ();
          val x_45 = isDefined_34;
          if (x_45)
            ()
          else
            {
              val x_46 = i_26;
              val x_47 = x_46.<(len_27);
              if (x_47)
                {
                  val x_48 = i_26;
                  val x_49 = ((x_20): scala.collection.IndexedSeq[scala.Char]).apply(x_48);
                  val x_50 = i_26;
                  val x_51 = x_50.+(1);
                  i_26 = x_51;
                  optVal_35 = x_49;
                  isDefined_34 = true;
                  false
                }
              else
                ();
              val x_52 = i_26;
              val x_53 = x_52.==(len_27);
              if (x_53)
                finished_31 = true
              else
                ()
            };
          val x_54 = isDefined_34;
          if (x_54)
            {
              val x_55 = optVal_35;
              val x_56 = k_29(x_55);
              cont_30 = x_56
            }
          else
            finished_31 = true
        }
      ;
      finished_31
    })
  }), x_23)
}).andThen[scala.Function1[scala.Function1[scala.Char, scala.Boolean], scala.Boolean]](((x$1_57: sfusion.Sequence[scala.Char]) => {
              val x_58 = x$1_57.under;
              x_58.apply()
            }))(x_15);
            cur_s_7 = x_59;
            false
          }
        else
          ();
        val x_60 = x_6.hasNext;
        x_60.`unary_!`
      }
    else
      ();
    val x_61 = cur_s_7;
    val x_62 = x_61.==(null);
    if (x_62)
      completed_10 = true
    else
      {
        val x_63 = cur_s_7;
        val x_74 = x_63(((b_64: scala.Char) => {
          val x_65 = dropped_8;
          val x_66 = x_65.<(x_1.length());
          val x_73 = if (x_66)
            {
              val x_67 = dropped_8;
              val x_68 = x_67.+(1);
              dropped_8 = x_68;
              true
            }
          else
            {
              val x_69 = cur_9;
              val x_70 = scala.Predef.augmentString(x_69);
              val x_71 = scala.Predef.StringCanBuildFrom;
              val x_72 = x_70.:+[scala.Char, java.lang.String](b_64)(x_71);
              cur_9 = x_72;
              true
            };
          continue_11 = x_73;
          continue_11
        }));
        if (x_74)
          cur_s_7 = null
        else
          ()
      };
    val x_75 = completed_10;
    val x_76 = x_75.`unary_!`;
    x_76.&&(continue_11)
  }) 
    ()
  ;
  completed_10;
  cur_9
})

// === ReNorm (should be the same) ===

// Transfo time: 18ms  Stringifying time: 32ms

// Same as above.
