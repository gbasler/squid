// Automatically-generated code

// === Init ===

// Transfo time: 0ms  Stringifying time: 19ms

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

// Transfo time: 12ms  Stringifying time: 32ms

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

// Transfo time: 9ms  Stringifying time: 13ms

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

// Transfo time: 0ms  Stringifying time: 4ms

// Same as above.

// === Imperative ===

// Transfo time: 32ms  Stringifying time: 62ms

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
    val x_9 = x_8.==(null);
    if (x_9)
      {
        while ({
          val x_10 = x_2.hasNext;
          x_10.&&({
            val x_11 = x_2.next();
            val x_12 = scala.Predef.wrapString(x_1);
            val x_13 = ((x_12): scala.collection.IndexedSeq[scala.Char]).size;
            val x_14 = scala.Predef.wrapString(x_11);
            val x_15 = ((x_14): scala.collection.IndexedSeq[scala.Char]).size;
            val x_16 = ((x_12): scala.collection.IndexedSeq[scala.Char]).length;
            var i_17: scala.Int = 0;
            val x_18 = ((x_14): scala.collection.IndexedSeq[scala.Char]).length;
            var i_19: scala.Int = 0;
            var curIsLhs_20: scala.Boolean = true;
            cur_s_3 = ((k_21: scala.Function1[scala.Char, scala.Boolean]) => {
              var cont_22: scala.Boolean = true;
              var finished_23: scala.Boolean = false;
              while ({
                val x_24 = cont_22;
                x_24.&&({
                  val x_25 = finished_23;
                  x_25.`unary_!`
                })
              }) 
                {
                  var next_26: scala.Option[scala.Char] = scala.None;
                  val x_27 = curIsLhs_20;
                  if (x_27)
                    {
                      while ({
                        val x_28 = i_17;
                        x_28.<(x_16).&&({
                          val x_29 = i_17;
                          val x_30 = ((x_12): scala.collection.IndexedSeq[scala.Char]).apply(x_29);
                          val x_31 = i_17;
                          i_17 = x_31.+(1);
                          next_26 = scala.Some.apply[scala.Char](x_30);
                          false
                        })
                      }) 
                        ()
                      ;
                      val x_32 = i_17;
                      if (x_32.==(x_16))
                        curIsLhs_20 = false
                      else
                        ()
                    }
                  else
                    ();
                  val x_33 = next_26;
                  if (x_33.isDefined.`unary_!`)
                    {
                      while ({
                        val x_34 = i_19;
                        x_34.<(x_18).&&({
                          val x_35 = i_19;
                          val x_36 = ((x_14): scala.collection.IndexedSeq[scala.Char]).apply(x_35);
                          val x_37 = i_19;
                          i_19 = x_37.+(1);
                          next_26 = scala.Some.apply[scala.Char](x_36);
                          false
                        })
                      }) 
                        ()
                      ;
                      val x_38 = i_19;
                      if (x_38.==(x_18))
                        finished_23 = true
                      else
                        ()
                    }
                  else
                    ();
                  val x_39 = next_26;
                  if (x_39.isDefined)
                    {
                      val x_40 = k_21(x_39.get);
                      cont_22 = x_40
                    }
                  else
                    finished_23 = true
                }
              ;
              finished_23
            });
            false
          })
        }) 
          ()
        ;
        val x_41 = x_2.hasNext;
        x_41.`unary_!`
      }
    else
      ();
    val x_42 = cur_s_3;
    val x_43 = x_42.==(null);
    if (x_43)
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

// === Low-Level Norm ===

// Transfo time: 41ms  Stringifying time: 90ms

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
    val x_9 = x_8.==(null);
    if (x_9)
      {
        val x_10 = x_2.hasNext;
        if (x_10)
          {
            val x_11 = x_2.next();
            val x_12 = scala.Predef.wrapString(x_1);
            val x_13 = ((x_12): scala.collection.IndexedSeq[scala.Char]).size;
            val x_14 = scala.Predef.wrapString(x_11);
            val x_15 = ((x_14): scala.collection.IndexedSeq[scala.Char]).size;
            val x_16 = ((x_12): scala.collection.IndexedSeq[scala.Char]).length;
            var i_17: scala.Int = 0;
            val x_18 = ((x_14): scala.collection.IndexedSeq[scala.Char]).length;
            var i_19: scala.Int = 0;
            var curIsLhs_20: scala.Boolean = true;
            cur_s_3 = ((k_21: scala.Function1[scala.Char, scala.Boolean]) => {
              var cont_22: scala.Boolean = true;
              var finished_23: scala.Boolean = false;
              while ({
                val x_24 = cont_22;
                x_24.&&({
                  val x_25 = finished_23;
                  x_25.`unary_!`
                })
              }) 
                {
                  var isDefined_26: scala.Boolean = false;
                  var optVal_27: scala.Char = '\00';
                  val x_28 = curIsLhs_20;
                  if (x_28)
                    {
                      val x_29 = i_17;
                      if (x_29.<(x_16))
                        {
                          val x_30 = i_17;
                          val x_31 = ((x_12): scala.collection.IndexedSeq[scala.Char]).apply(x_30);
                          val x_32 = i_17;
                          i_17 = x_32.+(1);
                          optVal_27 = x_31;
                          isDefined_26 = true;
                          false
                        }
                      else
                        ();
                      val x_33 = i_17;
                      if (x_33.==(x_16))
                        curIsLhs_20 = false
                      else
                        ()
                    }
                  else
                    ();
                  val x_34 = isDefined_26;
                  if (x_34)
                    ()
                  else
                    {
                      val x_35 = i_19;
                      if (x_35.<(x_18))
                        {
                          val x_36 = i_19;
                          val x_37 = ((x_14): scala.collection.IndexedSeq[scala.Char]).apply(x_36);
                          val x_38 = i_19;
                          i_19 = x_38.+(1);
                          optVal_27 = x_37;
                          isDefined_26 = true;
                          false
                        }
                      else
                        ();
                      val x_39 = i_19;
                      if (x_39.==(x_18))
                        finished_23 = true
                      else
                        ()
                    };
                  val x_40 = isDefined_26;
                  if (x_40)
                    {
                      val x_41 = optVal_27;
                      val x_42 = k_21(x_41);
                      cont_22 = x_42
                    }
                  else
                    finished_23 = true
                }
              ;
              finished_23
            });
            false
          }
        else
          ();
        val x_43 = x_2.hasNext;
        x_43.`unary_!`
      }
    else
      ();
    val x_44 = cur_s_3;
    val x_45 = x_44.==(null);
    if (x_45)
      completed_6 = true
    else
      {
        val x_46 = cur_s_3;
        val x_55 = x_46(((b_47: scala.Char) => {
          val x_48 = dropped_4;
          val x_54 = if (x_48.<(x_1.length()))
            {
              val x_49 = dropped_4;
              dropped_4 = x_49.+(1);
              true
            }
          else
            {
              val x_50 = cur_5;
              val x_51 = scala.Predef.augmentString(x_50);
              val x_52 = scala.Predef.StringCanBuildFrom;
              val x_53 = x_51.:+[scala.Char, java.lang.String](b_47)(x_52);
              cur_5 = x_53;
              true
            };
          continue_7 = x_54;
          continue_7
        }));
        if (x_55)
          cur_s_3 = null
        else
          ()
      };
    val x_56 = completed_6;
    x_56.`unary_!`.&&(continue_7)
  }) 
    ()
  ;
  completed_6;
  cur_5
})

// === ReNorm (should be the same) ===

// Transfo time: 18ms  Stringifying time: 35ms

// Same as above.
