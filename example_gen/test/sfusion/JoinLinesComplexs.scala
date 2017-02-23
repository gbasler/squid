// Automatically-generated code

// === Init ===

// Transfo time: 0ms  Stringifying time: 21ms

((x$2_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesComplex$default$2;
  val x_3 = new sfusion.Sequence[java.lang.String]((() => {
    val x_2 = x$2_0.iterator;
    sfusion.impl.`package`.fromIterator[java.lang.String](x_2)
  }), scala.`package`.Right.apply[Nothing, scala.Boolean](false));
  val x_11 = x_3.flatMap[scala.Char](((str_4: java.lang.String) => {
    val x_5 = scala.Predef.wrapString(x_1);
    val x_6 = ((x_5): scala.collection.IndexedSeq[scala.Char]).size;
    val x_7 = new sfusion.Sequence[scala.Char]((() => sfusion.impl.`package`.fromIndexed[scala.Char](((x_5): scala.collection.IndexedSeq[scala.Char]))), scala.`package`.Left.apply[scala.Int, Nothing](x_6));
    val x_8 = scala.Predef.wrapString(str_4);
    val x_9 = ((x_8): scala.collection.IndexedSeq[scala.Char]).size;
    val x_10 = new sfusion.Sequence[scala.Char]((() => sfusion.impl.`package`.fromIndexed[scala.Char](((x_8): scala.collection.IndexedSeq[scala.Char]))), scala.`package`.Left.apply[scala.Int, Nothing](x_9));
    x_7.++[scala.Char](x_10)
  }));
  val x_12 = x_11.drop(x_1.length());
  x_12.fold[java.lang.String]("")(((x$10_13: java.lang.String, x$11_14: scala.Char) => {
    val x_15 = scala.Predef.augmentString(x$10_13);
    val x_16 = scala.Predef.StringCanBuildFrom;
    x_15.:+[scala.Char, java.lang.String](x$11_14)(x_16)
  }))
})

// === Impl ===

// Transfo time: 12ms  Stringifying time: 34ms

((x$2_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesComplex$default$2;
  val x_3 = new sfusion.Sequence[java.lang.String]((() => {
    val x_2 = x$2_0.iterator;
    sfusion.impl.`package`.fromIterator[java.lang.String](x_2)
  }), scala.`package`.Right.apply[Nothing, scala.Boolean](false));
  val x_21 = new sfusion.Sequence[scala.Char]((() => {
    val x_4 = x_3.under;
    val x_5 = x_4.apply();
    sfusion.impl.`package`.flatMap[java.lang.String, scala.Char](x_5)(((x_6: java.lang.String) => {
      val x_7 = scala.Predef.wrapString(x_1);
      val x_8 = ((x_7): scala.collection.IndexedSeq[scala.Char]).size;
      val x_9 = new sfusion.Sequence[scala.Char]((() => sfusion.impl.`package`.fromIndexed[scala.Char](((x_7): scala.collection.IndexedSeq[scala.Char]))), scala.`package`.Left.apply[scala.Int, Nothing](x_8));
      val x_10 = scala.Predef.wrapString(x_6);
      val x_11 = ((x_10): scala.collection.IndexedSeq[scala.Char]).size;
      val x_12 = new sfusion.Sequence[scala.Char]((() => sfusion.impl.`package`.fromIndexed[scala.Char](((x_10): scala.collection.IndexedSeq[scala.Char]))), scala.`package`.Left.apply[scala.Int, Nothing](x_11));
      val x_13 = x_9.size;
      val x_14 = x_12.size;
      val x_19 = new sfusion.Sequence[scala.Char]((() => {
        val x_15 = x_9.under;
        val x_16 = x_15.apply();
        val x_17 = x_12.under;
        val x_18 = x_17.apply();
        sfusion.impl.`package`.concat[scala.Char](x_16, x_18)
      }), sfusion.`package`.addSizes(x_13, x_14));
      val x_20 = x_19.under;
      x_20.apply()
    }))
  }), scala.`package`.Right.apply[Nothing, scala.Boolean](true));
  val x_22 = x_21.size;
  val x_25 = new sfusion.Sequence[scala.Char]((() => {
    val x_23 = x_21.under;
    val x_24 = x_23.apply();
    sfusion.impl.`package`.drop[scala.Char](x_24)(x_1.length())
  }), sfusion.`package`.minSize(x_22, scala.`package`.Left.apply[scala.Int, Nothing](x_1.length())));
  val x_26 = x_25.under;
  val x_27 = x_26.apply();
  sfusion.impl.`package`.fold[scala.Char, java.lang.String](x_27)("")(((x$10_28: java.lang.String, x$11_29: scala.Char) => {
    val x_30 = scala.Predef.augmentString(x$10_28);
    val x_31 = scala.Predef.StringCanBuildFrom;
    x_30.:+[scala.Char, java.lang.String](x$11_29)(x_31)
  }))
})

// === CtorInline ===

// Transfo time: 8ms  Stringifying time: 14ms

((x$2_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesComplex$default$2;
  val x_2 = x$2_0.iterator;
  val x_3 = sfusion.impl.`package`.fromIterator[java.lang.String](x_2);
  val x_11 = sfusion.impl.`package`.flatMap[java.lang.String, scala.Char](x_3)(((x_4: java.lang.String) => {
    val x_5 = scala.Predef.wrapString(x_1);
    val x_6 = ((x_5): scala.collection.IndexedSeq[scala.Char]).size;
    val x_7 = scala.Predef.wrapString(x_4);
    val x_8 = ((x_7): scala.collection.IndexedSeq[scala.Char]).size;
    val x_9 = sfusion.impl.`package`.fromIndexed[scala.Char](((x_5): scala.collection.IndexedSeq[scala.Char]));
    val x_10 = sfusion.impl.`package`.fromIndexed[scala.Char](((x_7): scala.collection.IndexedSeq[scala.Char]));
    sfusion.impl.`package`.concat[scala.Char](x_9, x_10)
  }));
  val x_12 = sfusion.impl.`package`.drop[scala.Char](x_11)(x_1.length());
  sfusion.impl.`package`.fold[scala.Char, java.lang.String](x_12)("")(((x$10_13: java.lang.String, x$11_14: scala.Char) => {
    val x_15 = scala.Predef.augmentString(x$10_13);
    val x_16 = scala.Predef.StringCanBuildFrom;
    x_15.:+[scala.Char, java.lang.String](x$11_14)(x_16)
  }))
})

// === ImplOptim ===

// Transfo time: 0ms  Stringifying time: 3ms

// Same as above.

// === Imperative ===

// Transfo time: 34ms  Stringifying time: 84ms

((x$2_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesComplex$default$2;
  val x_2 = x$2_0.iterator;
  var cur_s_3: scala.Function1[scala.Function1[scala.Char, scala.Boolean], scala.Boolean] = null;
  var dropped_4: scala.Int = 0;
  var cur_5: java.lang.String = "";
  var completed_6: scala.Boolean = false;
  var continue_7: scala.Boolean = false;
  while ({
    val x_8 = cur_s_3;
    if (x_8.==(null))
      {
        while ({
          val x_9 = x_2.hasNext;
          x_9.&&({
            val x_10 = x_2.next();
            val x_11 = scala.Predef.wrapString(x_1);
            val x_12 = ((x_11): scala.collection.IndexedSeq[scala.Char]).size;
            val x_13 = scala.Predef.wrapString(x_10);
            val x_14 = ((x_13): scala.collection.IndexedSeq[scala.Char]).size;
            val x_15 = ((x_11): scala.collection.IndexedSeq[scala.Char]).length;
            var i_16: scala.Int = 0;
            val x_17 = ((x_13): scala.collection.IndexedSeq[scala.Char]).length;
            var i_18: scala.Int = 0;
            var curIsLhs_19: scala.Boolean = true;
            cur_s_3 = ((k_20: scala.Function1[scala.Char, scala.Boolean]) => {
              var cont_21: scala.Boolean = true;
              var finished_22: scala.Boolean = false;
              while ({
                val x_23 = cont_21;
                x_23.&&({
                  val x_24 = finished_22;
                  x_24.`unary_!`
                })
              }) 
                {
                  var next_25: scala.Option[scala.Char] = scala.None;
                  val x_26 = curIsLhs_19;
                  if (x_26)
                    {
                      while ({
                        val x_27 = i_16;
                        x_27.<(x_15).&&({
                          val x_28 = i_16;
                          val x_29 = ((x_11): scala.collection.IndexedSeq[scala.Char]).apply(x_28);
                          val x_30 = i_16;
                          i_16 = x_30.+(1);
                          next_25 = scala.Some.apply[scala.Char](x_29);
                          false
                        })
                      }) 
                        ()
                      ;
                      val x_31 = i_16;
                      if (x_31.==(x_15))
                        curIsLhs_19 = false
                      else
                        ()
                    }
                  else
                    ();
                  val x_32 = next_25;
                  if (x_32.isDefined.`unary_!`)
                    {
                      while ({
                        val x_33 = i_18;
                        x_33.<(x_17).&&({
                          val x_34 = i_18;
                          val x_35 = ((x_13): scala.collection.IndexedSeq[scala.Char]).apply(x_34);
                          val x_36 = i_18;
                          i_18 = x_36.+(1);
                          next_25 = scala.Some.apply[scala.Char](x_35);
                          false
                        })
                      }) 
                        ()
                      ;
                      val x_37 = i_18;
                      if (x_37.==(x_17))
                        finished_22 = true
                      else
                        ()
                    }
                  else
                    ();
                  val x_38 = next_25;
                  if (x_38.isDefined)
                    {
                      val x_39 = k_20(x_38.get);
                      cont_21 = x_39
                    }
                  else
                    finished_22 = true
                }
              ;
              finished_22
            });
            false
          })
        }) 
          ()
        ;
        val x_40 = x_2.hasNext;
        x_40.`unary_!`
      }
    else
      ();
    val x_41 = cur_s_3;
    if (x_41.==(null))
      completed_6 = true
    else
      {
        val x_42 = cur_s_3;
        val x_51 = x_42(((b_43: scala.Char) => {
          val x_44 = dropped_4;
          val x_50 = if (x_44.<(x_1.length()))
            {
              val x_45 = dropped_4;
              dropped_4 = x_45.+(1);
              true
            }
          else
            {
              val x_46 = cur_5;
              val x_47 = scala.Predef.augmentString(x_46);
              val x_48 = scala.Predef.StringCanBuildFrom;
              val x_49 = x_47.:+[scala.Char, java.lang.String](b_43)(x_48);
              cur_5 = x_49;
              true
            };
          continue_7 = x_50;
          continue_7
        }));
        if (x_51)
          cur_s_3 = null
        else
          ()
      };
    val x_52 = completed_6;
    x_52.`unary_!`.&&(continue_7)
  }) 
    ()
  ;
  completed_6;
  cur_5
})

// === Low-Level Norm ===

// Transfo time: 52ms  Stringifying time: 113ms

((x$2_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesComplex$default$2;
  val x_2 = x$2_0.iterator;
  var cur_s_3: scala.Function1[scala.Function1[scala.Char, scala.Boolean], scala.Boolean] = null;
  var dropped_4: scala.Int = 0;
  var cur_5: java.lang.String = "";
  var completed_6: scala.Boolean = false;
  var continue_7: scala.Boolean = false;
  while ({
    val x_8 = cur_s_3;
    if (x_8.==(null))
      {
        val x_9 = x_2.hasNext;
        if (x_9)
          {
            val x_10 = x_2.next();
            val x_11 = scala.Predef.wrapString(x_1);
            val x_12 = ((x_11): scala.collection.IndexedSeq[scala.Char]).size;
            val x_13 = scala.Predef.wrapString(x_10);
            val x_14 = ((x_13): scala.collection.IndexedSeq[scala.Char]).size;
            val x_15 = ((x_11): scala.collection.IndexedSeq[scala.Char]).length;
            var i_16: scala.Int = 0;
            val x_17 = ((x_13): scala.collection.IndexedSeq[scala.Char]).length;
            var i_18: scala.Int = 0;
            var curIsLhs_19: scala.Boolean = true;
            cur_s_3 = ((k_20: scala.Function1[scala.Char, scala.Boolean]) => {
              var cont_21: scala.Boolean = true;
              var finished_22: scala.Boolean = false;
              while ({
                val x_23 = cont_21;
                x_23.&&({
                  val x_24 = finished_22;
                  x_24.`unary_!`
                })
              }) 
                {
                  var isDefined_25: scala.Boolean = false;
                  var optVal_26: scala.Char = '\00';
                  val x_27 = curIsLhs_19;
                  if (x_27)
                    {
                      val x_28 = i_16;
                      if (x_28.<(x_15))
                        {
                          val x_29 = i_16;
                          val x_30 = ((x_11): scala.collection.IndexedSeq[scala.Char]).apply(x_29);
                          val x_31 = i_16;
                          i_16 = x_31.+(1);
                          optVal_26 = x_30;
                          isDefined_25 = true;
                          false
                        }
                      else
                        ();
                      val x_32 = i_16;
                      if (x_32.==(x_15))
                        curIsLhs_19 = false
                      else
                        ()
                    }
                  else
                    ();
                  val x_33 = isDefined_25;
                  if (x_33)
                    ()
                  else
                    {
                      val x_34 = i_18;
                      if (x_34.<(x_17))
                        {
                          val x_35 = i_18;
                          val x_36 = ((x_13): scala.collection.IndexedSeq[scala.Char]).apply(x_35);
                          val x_37 = i_18;
                          i_18 = x_37.+(1);
                          optVal_26 = x_36;
                          isDefined_25 = true;
                          false
                        }
                      else
                        ();
                      val x_38 = i_18;
                      if (x_38.==(x_17))
                        finished_22 = true
                      else
                        ()
                    };
                  val x_39 = isDefined_25;
                  if (x_39)
                    {
                      val x_40 = optVal_26;
                      val x_41 = k_20(x_40);
                      cont_21 = x_41
                    }
                  else
                    finished_22 = true
                }
              ;
              finished_22
            });
            false
          }
        else
          ();
        val x_42 = x_2.hasNext;
        x_42.`unary_!`
      }
    else
      ();
    val x_43 = cur_s_3;
    if (x_43.==(null))
      completed_6 = true
    else
      {
        val x_44 = cur_s_3;
        val x_53 = x_44(((b_45: scala.Char) => {
          val x_46 = dropped_4;
          val x_52 = if (x_46.<(x_1.length()))
            {
              val x_47 = dropped_4;
              dropped_4 = x_47.+(1);
              true
            }
          else
            {
              val x_48 = cur_5;
              val x_49 = scala.Predef.augmentString(x_48);
              val x_50 = scala.Predef.StringCanBuildFrom;
              val x_51 = x_49.:+[scala.Char, java.lang.String](b_45)(x_50);
              cur_5 = x_51;
              true
            };
          continue_7 = x_52;
          continue_7
        }));
        if (x_53)
          cur_s_3 = null
        else
          ()
      };
    val x_54 = completed_6;
    x_54.`unary_!`.&&(continue_7)
  }) 
    ()
  ;
  completed_6;
  cur_5
})

// === ReNorm (should be the same) ===

// Transfo time: 33ms  Stringifying time: 83ms

// Same as above.
