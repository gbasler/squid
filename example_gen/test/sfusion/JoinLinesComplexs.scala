// Automatically-generated code

// === Init ===

// Transfo time: 0ms  Stringifying time: 12ms

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

// Transfo time: 9ms  Stringifying time: 25ms

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

// Transfo time: 7ms  Stringifying time: 10ms

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

// Transfo time: 13ms  Stringifying time: 56ms

((x$2_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesComplex$default$2;
  val x_2 = x$2_0.iterator;
  val x_37 = sfusion.impl.`package`.flatMap[java.lang.String, scala.Char](((k_3: scala.Function1[java.lang.String, scala.Boolean]) => {
    while ({
      val x_4 = x_2.hasNext;
      x_4.&&({
        val x_5 = x_2.next();
        k_3(x_5)
      })
    }) 
      ()
    ;
    val x_6 = x_2.hasNext;
    x_6.`unary_!`
  }))(((x_7: java.lang.String) => {
    val x_8 = scala.Predef.wrapString(x_1);
    val x_9 = ((x_8): scala.collection.IndexedSeq[scala.Char]).size;
    val x_10 = scala.Predef.wrapString(x_7);
    val x_11 = ((x_10): scala.collection.IndexedSeq[scala.Char]).size;
    val x_12 = ((x_8): scala.collection.IndexedSeq[scala.Char]).length;
    var i_13: scala.Int = 0;
    val x_14 = ((x_10): scala.collection.IndexedSeq[scala.Char]).length;
    var i_15: scala.Int = 0;
    var curIsLhs_16: scala.Boolean = true;
    ((k_17: scala.Function1[scala.Char, scala.Boolean]) => {
      var cont_18: scala.Boolean = true;
      var finished_19: scala.Boolean = false;
      while ({
        val x_20 = cont_18;
        x_20.&&({
          val x_21 = finished_19;
          x_21.`unary_!`
        })
      }) 
        {
          var next_22: scala.Option[scala.Char] = scala.None;
          val x_23 = curIsLhs_16;
          if (x_23)
            {
              while ({
                val x_24 = i_13;
                x_24.<(x_12).&&({
                  val x_25 = i_13;
                  val x_26 = ((x_8): scala.collection.IndexedSeq[scala.Char]).apply(x_25);
                  val x_27 = i_13;
                  i_13 = x_27.+(1);
                  next_22 = scala.Some.apply[scala.Char](x_26);
                  false
                })
              }) 
                ()
              ;
              val x_28 = i_13;
              if (x_28.==(x_12))
                curIsLhs_16 = false
              else
                ()
            }
          else
            ();
          val x_29 = next_22;
          if (x_29.isDefined.`unary_!`)
            {
              while ({
                val x_30 = i_15;
                x_30.<(x_14).&&({
                  val x_31 = i_15;
                  val x_32 = ((x_10): scala.collection.IndexedSeq[scala.Char]).apply(x_31);
                  val x_33 = i_15;
                  i_15 = x_33.+(1);
                  next_22 = scala.Some.apply[scala.Char](x_32);
                  false
                })
              }) 
                ()
              ;
              val x_34 = i_15;
              if (x_34.==(x_14))
                finished_19 = true
              else
                ()
            }
          else
            ();
          val x_35 = next_22;
          if (x_35.isDefined)
            {
              val x_36 = k_17(x_35.get);
              cont_18 = x_36
            }
          else
            finished_19 = true
        }
      ;
      finished_19
    })
  }));
  var dropped_38: scala.Int = 0;
  var cur_39: java.lang.String = "";
  x_37(((a_40: scala.Char) => {
    val x_41 = dropped_38;
    if (x_41.<(x_1.length()))
      {
        val x_42 = dropped_38;
        dropped_38 = x_42.+(1);
        true
      }
    else
      {
        val x_43 = cur_39;
        val x_44 = scala.Predef.augmentString(x_43);
        val x_45 = scala.Predef.StringCanBuildFrom;
        val x_46 = x_44.:+[scala.Char, java.lang.String](a_40)(x_45);
        cur_39 = x_46;
        true
      }
  }));
  cur_39
})

// === FlatMapFusion ===

// Transfo time: 171ms  Stringifying time: 140ms

((x$2_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesComplex$default$2;
  val x_2 = x$2_0.iterator;
  var envVar_3: scala.Option[scala.Tuple2[scala.collection.immutable.WrappedString, scala.Tuple2[scala.Int, scala.Tuple2[scala.collection.immutable.WrappedString, scala.Tuple2[scala.Int, scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], squid.lib.`package`.Var[scala.Boolean]]]]]]]]]] = scala.None;
  var dropped_4: scala.Int = 0;
  var cur_5: java.lang.String = "";
  var completed_6: scala.Boolean = false;
  var continue_7: scala.Boolean = false;
  while ({
    val x_8 = envVar_3;
    if (x_8.isDefined.`unary_!`)
      {
        while ({
          val x_9 = x_2.hasNext;
          x_9.&&({
            val x_10 = x_2.next();
            val ClosureVar_11 = scala.Predef.wrapString(x_1);
            val ClosureVar_12 = ((ClosureVar_11): scala.collection.IndexedSeq[scala.Char]).size;
            val ClosureVar_13 = scala.Predef.wrapString(x_10);
            val ClosureVar_14 = ((ClosureVar_13): scala.collection.IndexedSeq[scala.Char]).size;
            val ClosureVar_15 = ((ClosureVar_11): scala.collection.IndexedSeq[scala.Char]).length;
            var ClosureVar_16: scala.Int = 0;
            val ClosureVar_17 = ((ClosureVar_13): scala.collection.IndexedSeq[scala.Char]).length;
            var ClosureVar_18: scala.Int = 0;
            var v_19: scala.Boolean = true;
            envVar_3 = scala.Some.apply[scala.Tuple2[scala.collection.immutable.WrappedString, scala.Tuple2[scala.Int, scala.Tuple2[scala.collection.immutable.WrappedString, scala.Tuple2[scala.Int, scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], squid.lib.`package`.Var[scala.Boolean]]]]]]]]]](scala.Tuple2.apply[scala.collection.immutable.WrappedString, scala.Tuple2[scala.Int, scala.Tuple2[scala.collection.immutable.WrappedString, scala.Tuple2[scala.Int, scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], squid.lib.`package`.Var[scala.Boolean]]]]]]]]](ClosureVar_11, scala.Tuple2.apply[scala.Int, scala.Tuple2[scala.collection.immutable.WrappedString, scala.Tuple2[scala.Int, scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], squid.lib.`package`.Var[scala.Boolean]]]]]]]](ClosureVar_12, scala.Tuple2.apply[scala.collection.immutable.WrappedString, scala.Tuple2[scala.Int, scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], squid.lib.`package`.Var[scala.Boolean]]]]]]](ClosureVar_13, scala.Tuple2.apply[scala.Int, scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], squid.lib.`package`.Var[scala.Boolean]]]]]](ClosureVar_14, scala.Tuple2.apply[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], squid.lib.`package`.Var[scala.Boolean]]]]](ClosureVar_15, scala.Tuple2.apply[squid.lib.`package`.Var[scala.Int], scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], squid.lib.`package`.Var[scala.Boolean]]]](new squid.lib.VarProxy[scala.Int](ClosureVar_16, ((a) => ClosureVar_16 = a)), scala.Tuple2.apply[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], squid.lib.`package`.Var[scala.Boolean]]](ClosureVar_17, scala.Tuple2.apply[squid.lib.`package`.Var[scala.Int], squid.lib.`package`.Var[scala.Boolean]](new squid.lib.VarProxy[scala.Int](ClosureVar_18, ((a) => ClosureVar_18 = a)), new squid.lib.VarProxy[scala.Boolean](v_19, ((a) => v_19 = a)))))))))));
            false
          })
        }) 
          ()
        ;
        val x_20 = x_2.hasNext;
        x_20.`unary_!`
      }
    else
      ();
    val x_21 = envVar_3;
    if (x_21.isDefined.`unary_!`)
      completed_6 = true
    else
      {
        val x_22 = envVar_3;
        var cont_23: scala.Boolean = true;
        var finished_24: scala.Boolean = false;
        while ({
          val x_25 = cont_23;
          x_25.&&({
            val x_26 = finished_24;
            x_26.`unary_!`
          })
        }) 
          {
            var next_27: scala.Option[scala.Char] = scala.None;
            val x_28 = x_22.get._2._2._2._2._2._2._2._2.!;
            if (x_28)
              {
                while ({
                  val x_29 = x_22.get._2._2._2._2._2._1.!;
                  x_29.<(x_22.get._2._2._2._2._1).&&({
                    val x_30 = x_22.get._2._2._2._2._2._1.!;
                    val x_31 = ((x_22.get._1): scala.collection.IndexedSeq[scala.Char]).apply(x_30);
                    val x_32 = x_22.get._2._2._2._2._2._1.!;
                    x_22.get._2._2._2._2._2._1.:=(x_32.+(1));
                    next_27 = scala.Some.apply[scala.Char](x_31);
                    false
                  })
                }) 
                  ()
                ;
                val x_33 = x_22.get._2._2._2._2._2._1.!;
                if (x_33.==(x_22.get._2._2._2._2._1))
                  x_22.get._2._2._2._2._2._2._2._2.:=(false)
                else
                  ()
              }
            else
              ();
            val x_34 = next_27;
            if (x_34.isDefined.`unary_!`)
              {
                while ({
                  val x_35 = x_22.get._2._2._2._2._2._2._2._1.!;
                  x_35.<(x_22.get._2._2._2._2._2._2._1).&&({
                    val x_36 = x_22.get._2._2._2._2._2._2._2._1.!;
                    val x_37 = ((x_22.get._2._2._1): scala.collection.IndexedSeq[scala.Char]).apply(x_36);
                    val x_38 = x_22.get._2._2._2._2._2._2._2._1.!;
                    x_22.get._2._2._2._2._2._2._2._1.:=(x_38.+(1));
                    next_27 = scala.Some.apply[scala.Char](x_37);
                    false
                  })
                }) 
                  ()
                ;
                val x_39 = x_22.get._2._2._2._2._2._2._2._1.!;
                if (x_39.==(x_22.get._2._2._2._2._2._2._1))
                  finished_24 = true
                else
                  ()
              }
            else
              ();
            val x_40 = next_27;
            if (x_40.isDefined)
              {
                val x_41 = dropped_4;
                val x_47 = if (x_41.<(x_1.length()))
                  {
                    val x_42 = dropped_4;
                    dropped_4 = x_42.+(1);
                    true
                  }
                else
                  {
                    val x_43 = cur_5;
                    val x_44 = scala.Predef.augmentString(x_43);
                    val x_45 = scala.Predef.StringCanBuildFrom;
                    val x_46 = x_44.:+[scala.Char, java.lang.String](x_40.get)(x_45);
                    cur_5 = x_46;
                    true
                  };
                continue_7 = x_47;
                val x_48 = continue_7;
                cont_23 = x_48
              }
            else
              finished_24 = true
          }
        ;
        val x_49 = finished_24;
        if (x_49)
          envVar_3 = scala.None
        else
          ()
      };
    val x_50 = completed_6;
    x_50.`unary_!`.&&(continue_7)
  }) 
    ()
  ;
  completed_6;
  cur_5
})

// === LateImperative ===

// Transfo time: 0ms  Stringifying time: 64ms

// Same as above.

// === VarFlattening ===

// Transfo time: 465ms  Stringifying time: 58ms

((x$2_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesComplex$default$2;
  val x_2 = x$2_0.iterator;
  var isDefined_3: scala.Boolean = false;
  var lhs_4: scala.collection.immutable.WrappedString = squid.lib.`package`.uncheckedNullValue[scala.collection.immutable.WrappedString];
  var lhs_5: scala.Int = squid.lib.`package`.uncheckedNullValue[scala.Int];
  var lhs_6: scala.collection.immutable.WrappedString = squid.lib.`package`.uncheckedNullValue[scala.collection.immutable.WrappedString];
  var lhs_7: scala.Int = squid.lib.`package`.uncheckedNullValue[scala.Int];
  var lhs_8: scala.Int = squid.lib.`package`.uncheckedNullValue[scala.Int];
  var flatVar_9: scala.Int = squid.lib.`package`.uncheckedNullValue[scala.Int];
  var lhs_10: scala.Int = squid.lib.`package`.uncheckedNullValue[scala.Int];
  var flatVar_11: scala.Int = squid.lib.`package`.uncheckedNullValue[scala.Int];
  var flatVar_12: scala.Boolean = squid.lib.`package`.uncheckedNullValue[scala.Boolean];
  var dropped_13: scala.Int = 0;
  var cur_14: java.lang.String = "";
  var completed_15: scala.Boolean = false;
  var continue_16: scala.Boolean = false;
  while ({
    val x_17 = isDefined_3;
    if (x_17.`unary_!`)
      {
        while ({
          val x_18 = x_2.hasNext;
          x_18.&&({
            val x_19 = x_2.next();
            val ClosureVar_20 = scala.Predef.wrapString(x_1);
            val ClosureVar_21 = ((ClosureVar_20): scala.collection.IndexedSeq[scala.Char]).size;
            val ClosureVar_22 = scala.Predef.wrapString(x_19);
            val ClosureVar_23 = ((ClosureVar_22): scala.collection.IndexedSeq[scala.Char]).size;
            val ClosureVar_24 = ((ClosureVar_20): scala.collection.IndexedSeq[scala.Char]).length;
            var ClosureVar_25: scala.Int = 0;
            val ClosureVar_26 = ((ClosureVar_22): scala.collection.IndexedSeq[scala.Char]).length;
            var ClosureVar_27: scala.Int = 0;
            var v_28: scala.Boolean = true;
            lhs_4 = ClosureVar_20;
            lhs_5 = ClosureVar_21;
            lhs_6 = ClosureVar_22;
            lhs_7 = ClosureVar_23;
            lhs_8 = ClosureVar_24;
            val x_29 = ClosureVar_25;
            flatVar_9 = x_29;
            lhs_10 = ClosureVar_26;
            val x_30 = ClosureVar_27;
            flatVar_11 = x_30;
            val x_31 = v_28;
            flatVar_12 = x_31;
            isDefined_3 = true;
            false
          })
        }) 
          ()
        ;
        val x_32 = x_2.hasNext;
        x_32.`unary_!`
      }
    else
      ();
    val x_33 = isDefined_3;
    if (x_33.`unary_!`)
      completed_15 = true
    else
      {
        var cont_34: scala.Boolean = true;
        var finished_35: scala.Boolean = false;
        while ({
          val x_36 = cont_34;
          x_36.&&({
            val x_37 = finished_35;
            x_37.`unary_!`
          })
        }) 
          {
            var isDefined_38: scala.Boolean = false;
            var optVal_39: scala.Char = squid.lib.`package`.uncheckedNullValue[scala.Char];
            val x_40 = flatVar_12;
            if (x_40)
              {
                while ({
                  val x_41 = flatVar_9;
                  val x_42 = lhs_8;
                  x_41.<(x_42).&&({
                    val x_43 = flatVar_9;
                    val x_44 = lhs_4;
                    val x_45 = x_44.apply(x_43);
                    val x_46 = flatVar_9;
                    flatVar_9 = x_46.+(1);
                    optVal_39 = x_45;
                    isDefined_38 = true;
                    false
                  })
                }) 
                  ()
                ;
                val x_47 = flatVar_9;
                val x_48 = lhs_8;
                if (x_47.==(x_48))
                  flatVar_12 = false
                else
                  ()
              }
            else
              ();
            val x_49 = isDefined_38;
            if (x_49.`unary_!`)
              {
                while ({
                  val x_50 = flatVar_11;
                  val x_51 = lhs_10;
                  x_50.<(x_51).&&({
                    val x_52 = flatVar_11;
                    val x_53 = lhs_6;
                    val x_54 = x_53.apply(x_52);
                    val x_55 = flatVar_11;
                    flatVar_11 = x_55.+(1);
                    optVal_39 = x_54;
                    isDefined_38 = true;
                    false
                  })
                }) 
                  ()
                ;
                val x_56 = flatVar_11;
                val x_57 = lhs_10;
                if (x_56.==(x_57))
                  finished_35 = true
                else
                  ()
              }
            else
              ();
            val x_58 = isDefined_38;
            if (x_58)
              {
                val x_59 = dropped_13;
                val x_66 = if (x_59.<(x_1.length()))
                  {
                    val x_60 = dropped_13;
                    dropped_13 = x_60.+(1);
                    true
                  }
                else
                  {
                    val x_61 = cur_14;
                    val x_62 = scala.Predef.augmentString(x_61);
                    val x_63 = scala.Predef.StringCanBuildFrom;
                    val x_64 = optVal_39;
                    val x_65 = x_62.:+[scala.Char, java.lang.String](x_64)(x_63);
                    cur_14 = x_65;
                    true
                  };
                continue_16 = x_66;
                val x_67 = continue_16;
                cont_34 = x_67
              }
            else
              finished_35 = true
          }
        ;
        val x_68 = finished_35;
        if (x_68)
          isDefined_3 = false
        else
          ()
      };
    val x_69 = completed_15;
    x_69.`unary_!`.&&(continue_16)
  }) 
    ()
  ;
  completed_15;
  cur_14
})

// === Low-Level Norm ===

// Transfo time: 20ms  Stringifying time: 71ms

((x$2_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesComplex$default$2;
  val x_2 = x$2_0.iterator;
  var isDefined_3: scala.Boolean = false;
  var lhs_4: scala.collection.immutable.WrappedString = null;
  var lhs_5: scala.Int = 0;
  var lhs_6: scala.collection.immutable.WrappedString = null;
  var lhs_7: scala.Int = 0;
  var lhs_8: scala.Int = 0;
  var flatVar_9: scala.Int = 0;
  var lhs_10: scala.Int = 0;
  var flatVar_11: scala.Int = 0;
  var flatVar_12: scala.Boolean = false;
  var dropped_13: scala.Int = 0;
  var cur_14: java.lang.String = "";
  var completed_15: scala.Boolean = false;
  var continue_16: scala.Boolean = false;
  while ({
    val x_17 = isDefined_3;
    if (x_17)
      ()
    else
      {
        val x_18 = x_2.hasNext;
        if (x_18)
          {
            val x_19 = x_2.next();
            val ClosureVar_20 = scala.Predef.wrapString(x_1);
            val ClosureVar_21 = ((ClosureVar_20): scala.collection.IndexedSeq[scala.Char]).size;
            val ClosureVar_22 = scala.Predef.wrapString(x_19);
            val ClosureVar_23 = ((ClosureVar_22): scala.collection.IndexedSeq[scala.Char]).size;
            val ClosureVar_24 = ((ClosureVar_20): scala.collection.IndexedSeq[scala.Char]).length;
            var ClosureVar_25: scala.Int = 0;
            val ClosureVar_26 = ((ClosureVar_22): scala.collection.IndexedSeq[scala.Char]).length;
            var ClosureVar_27: scala.Int = 0;
            var v_28: scala.Boolean = true;
            lhs_4 = ClosureVar_20;
            lhs_5 = ClosureVar_21;
            lhs_6 = ClosureVar_22;
            lhs_7 = ClosureVar_23;
            lhs_8 = ClosureVar_24;
            val x_29 = ClosureVar_25;
            flatVar_9 = x_29;
            lhs_10 = ClosureVar_26;
            val x_30 = ClosureVar_27;
            flatVar_11 = x_30;
            val x_31 = v_28;
            flatVar_12 = x_31;
            isDefined_3 = true;
            false
          }
        else
          ();
        val x_32 = x_2.hasNext;
        x_32.`unary_!`
      };
    val x_33 = isDefined_3;
    if (x_33)
      {
        var cont_34: scala.Boolean = true;
        var finished_35: scala.Boolean = false;
        while ({
          val x_36 = cont_34;
          x_36.&&({
            val x_37 = finished_35;
            x_37.`unary_!`
          })
        }) 
          {
            var isDefined_38: scala.Boolean = false;
            var optVal_39: scala.Char = '\00';
            val x_40 = flatVar_12;
            if (x_40)
              {
                val x_41 = flatVar_9;
                val x_42 = lhs_8;
                if (x_41.<(x_42))
                  {
                    val x_43 = flatVar_9;
                    val x_44 = lhs_4;
                    val x_45 = x_44.apply(x_43);
                    val x_46 = flatVar_9;
                    flatVar_9 = x_46.+(1);
                    optVal_39 = x_45;
                    isDefined_38 = true;
                    false
                  }
                else
                  ();
                val x_47 = flatVar_9;
                val x_48 = lhs_8;
                if (x_47.==(x_48))
                  flatVar_12 = false
                else
                  ()
              }
            else
              ();
            val x_49 = isDefined_38;
            if (x_49)
              ()
            else
              {
                val x_50 = flatVar_11;
                val x_51 = lhs_10;
                if (x_50.<(x_51))
                  {
                    val x_52 = flatVar_11;
                    val x_53 = lhs_6;
                    val x_54 = x_53.apply(x_52);
                    val x_55 = flatVar_11;
                    flatVar_11 = x_55.+(1);
                    optVal_39 = x_54;
                    isDefined_38 = true;
                    false
                  }
                else
                  ();
                val x_56 = flatVar_11;
                val x_57 = lhs_10;
                if (x_56.==(x_57))
                  finished_35 = true
                else
                  ()
              };
            val x_58 = isDefined_38;
            if (x_58)
              {
                val x_59 = dropped_13;
                val x_66 = if (x_59.<(x_1.length()))
                  {
                    val x_60 = dropped_13;
                    dropped_13 = x_60.+(1);
                    true
                  }
                else
                  {
                    val x_61 = cur_14;
                    val x_62 = scala.Predef.augmentString(x_61);
                    val x_63 = scala.Predef.StringCanBuildFrom;
                    val x_64 = optVal_39;
                    val x_65 = x_62.:+[scala.Char, java.lang.String](x_64)(x_63);
                    cur_14 = x_65;
                    true
                  };
                continue_16 = x_66;
                val x_67 = continue_16;
                cont_34 = x_67
              }
            else
              finished_35 = true
          }
        ;
        val x_68 = finished_35;
        if (x_68)
          isDefined_3 = false
        else
          ()
      }
    else
      completed_15 = true;
    val x_69 = completed_15;
    x_69.`unary_!`.&&(continue_16)
  }) 
    ()
  ;
  completed_15;
  cur_14
})

// === ReNorm (should be the same) ===

// Transfo time: 32ms  Stringifying time: 48ms

// Same as above.
