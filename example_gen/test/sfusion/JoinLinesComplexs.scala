// Automatically-generated code

// === Init ===

// Transfo time: 0ms  Stringifying time: 28ms

((x$2_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesComplex$default$2;
  val x_3 = new sfusion.Sequence[java.lang.String]((() => {
    val x_2 = x$2_0.iterator;
    sfusion.impl.`package`.fromIterator[java.lang.String](x_2)
  }), scala.`package`.Right.apply[Nothing, scala.Boolean](false));
  val x_12 = x_3.flatMap[scala.Char](((str_4: java.lang.String) => {
    val sch_5 = scala.`package`.Left;
    val x_6 = scala.Predef.wrapString(x_1);
    val x_7 = ((x_6): scala.collection.IndexedSeq[scala.Char]).size;
    val x_8 = new sfusion.Sequence[scala.Char]((() => sfusion.impl.`package`.fromIndexed[scala.Char](((x_6): scala.collection.IndexedSeq[scala.Char]))), sch_5.apply[scala.Int, Nothing](x_7));
    val x_9 = scala.Predef.wrapString(str_4);
    val x_10 = ((x_9): scala.collection.IndexedSeq[scala.Char]).size;
    val x_11 = new sfusion.Sequence[scala.Char]((() => sfusion.impl.`package`.fromIndexed[scala.Char](((x_9): scala.collection.IndexedSeq[scala.Char]))), sch_5.apply[scala.Int, Nothing](x_10));
    x_8.++[scala.Char](x_11)
  }));
  val x_13 = x_12.drop(x_1.length());
  x_13.fold[java.lang.String]("")(((x$10_14: java.lang.String, x$11_15: scala.Char) => {
    val x_16 = scala.Predef.augmentString(x$10_14);
    val x_17 = scala.Predef.StringCanBuildFrom;
    x_16.:+[scala.Char, java.lang.String](x$11_15)(x_17)
  }))
})

// === Impl ===

// Transfo time: 15ms  Stringifying time: 60ms

((x$2_0: scala.collection.Iterable[java.lang.String]) => {
  val sch_1 = scala.`package`.Right;
  val x_2 = sfusion.algo.`package`.joinLinesComplex$default$2;
  val x_4 = new sfusion.Sequence[java.lang.String]((() => {
    val x_3 = x$2_0.iterator;
    sfusion.impl.`package`.fromIterator[java.lang.String](x_3)
  }), sch_1.apply[Nothing, scala.Boolean](false));
  val x_23 = new sfusion.Sequence[scala.Char]((() => {
    val x_5 = x_4.under;
    val x_6 = x_5.apply();
    sfusion.impl.`package`.flatMap[java.lang.String, scala.Char](x_6)(((x_7: java.lang.String) => {
      val sch_8 = scala.`package`.Left;
      val x_9 = scala.Predef.wrapString(x_2);
      val x_10 = ((x_9): scala.collection.IndexedSeq[scala.Char]).size;
      val x_11 = new sfusion.Sequence[scala.Char]((() => sfusion.impl.`package`.fromIndexed[scala.Char](((x_9): scala.collection.IndexedSeq[scala.Char]))), sch_8.apply[scala.Int, Nothing](x_10));
      val x_12 = scala.Predef.wrapString(x_7);
      val x_13 = ((x_12): scala.collection.IndexedSeq[scala.Char]).size;
      val x_14 = new sfusion.Sequence[scala.Char]((() => sfusion.impl.`package`.fromIndexed[scala.Char](((x_12): scala.collection.IndexedSeq[scala.Char]))), sch_8.apply[scala.Int, Nothing](x_13));
      val x_15 = x_11.size;
      val x_16 = x_14.size;
      val x_21 = new sfusion.Sequence[scala.Char]((() => {
        val x_17 = x_11.under;
        val x_18 = x_17.apply();
        val x_19 = x_14.under;
        val x_20 = x_19.apply();
        sfusion.impl.`package`.concat[scala.Char](x_18, x_20)
      }), sfusion.`package`.addSizes(x_15, x_16));
      val x_22 = x_21.under;
      x_22.apply()
    }))
  }), sch_1.apply[Nothing, scala.Boolean](true));
  val x_24 = x_23.size;
  val x_27 = new sfusion.Sequence[scala.Char]((() => {
    val x_25 = x_23.under;
    val x_26 = x_25.apply();
    sfusion.impl.`package`.drop[scala.Char](x_26)(x_2.length())
  }), sfusion.`package`.minSize(x_24, scala.`package`.Left.apply[scala.Int, Nothing](x_2.length())));
  val x_28 = x_27.under;
  val x_29 = x_28.apply();
  sfusion.impl.`package`.fold[scala.Char, java.lang.String](x_29)("")(((x$10_30: java.lang.String, x$11_31: scala.Char) => {
    val x_32 = scala.Predef.augmentString(x$10_30);
    val x_33 = scala.Predef.StringCanBuildFrom;
    x_32.:+[scala.Char, java.lang.String](x$11_31)(x_33)
  }))
})

// === CtorInline ===

// Transfo time: 11ms  Stringifying time: 28ms

((x$2_0: scala.collection.Iterable[java.lang.String]) => {
  val x_1 = sfusion.algo.`package`.joinLinesComplex$default$2;
  val x_2 = x$2_0.iterator;
  val x_3 = sfusion.impl.`package`.fromIterator[java.lang.String](x_2);
  val x_13 = sfusion.impl.`package`.flatMap[java.lang.String, scala.Char](x_3)(((x_4: java.lang.String) => {
    val x_5 = scala.Predef.wrapString(x_1);
    val sch_6 = ((x_5): scala.collection.IndexedSeq[scala.Char]);
    val x_7 = sch_6.size;
    val x_8 = scala.Predef.wrapString(x_4);
    val sch_9 = ((x_8): scala.collection.IndexedSeq[scala.Char]);
    val x_10 = sch_9.size;
    val x_11 = sfusion.impl.`package`.fromIndexed[scala.Char](sch_6);
    val x_12 = sfusion.impl.`package`.fromIndexed[scala.Char](sch_9);
    sfusion.impl.`package`.concat[scala.Char](x_11, x_12)
  }));
  val x_14 = sfusion.impl.`package`.drop[scala.Char](x_13)(x_1.length());
  sfusion.impl.`package`.fold[scala.Char, java.lang.String](x_14)("")(((x$10_15: java.lang.String, x$11_16: scala.Char) => {
    val x_17 = scala.Predef.augmentString(x$10_15);
    val x_18 = scala.Predef.StringCanBuildFrom;
    x_17.:+[scala.Char, java.lang.String](x$11_16)(x_18)
  }))
})

// === ImplOptim ===

// Transfo time: 0ms  Stringifying time: 5ms

// Same as above.

// === Imperative ===

// Transfo time: 21ms  Stringifying time: 121ms

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

// Transfo time: 149ms  Stringifying time: 223ms

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
            val sch_12 = ((ClosureVar_11): scala.collection.IndexedSeq[scala.Char]);
            val ClosureVar_13 = sch_12.size;
            val ClosureVar_14 = scala.Predef.wrapString(x_10);
            val sch_15 = ((ClosureVar_14): scala.collection.IndexedSeq[scala.Char]);
            val ClosureVar_16 = sch_15.size;
            val ClosureVar_17 = sch_12.length;
            var ClosureVar_18: scala.Int = 0;
            val ClosureVar_19 = sch_15.length;
            var ClosureVar_20: scala.Int = 0;
            var v_21: scala.Boolean = true;
            val x_22 = scala.Some.apply[scala.Tuple2[scala.collection.immutable.WrappedString, scala.Tuple2[scala.Int, scala.Tuple2[scala.collection.immutable.WrappedString, scala.Tuple2[scala.Int, scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], squid.lib.`package`.Var[scala.Boolean]]]]]]]]]](scala.Tuple2.apply[scala.collection.immutable.WrappedString, scala.Tuple2[scala.Int, scala.Tuple2[scala.collection.immutable.WrappedString, scala.Tuple2[scala.Int, scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], squid.lib.`package`.Var[scala.Boolean]]]]]]]]](ClosureVar_11, scala.Tuple2.apply[scala.Int, scala.Tuple2[scala.collection.immutable.WrappedString, scala.Tuple2[scala.Int, scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], squid.lib.`package`.Var[scala.Boolean]]]]]]]](ClosureVar_13, scala.Tuple2.apply[scala.collection.immutable.WrappedString, scala.Tuple2[scala.Int, scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], squid.lib.`package`.Var[scala.Boolean]]]]]]](ClosureVar_14, scala.Tuple2.apply[scala.Int, scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], squid.lib.`package`.Var[scala.Boolean]]]]]](ClosureVar_16, scala.Tuple2.apply[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], squid.lib.`package`.Var[scala.Boolean]]]]](ClosureVar_17, scala.Tuple2.apply[squid.lib.`package`.Var[scala.Int], scala.Tuple2[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], squid.lib.`package`.Var[scala.Boolean]]]](new squid.lib.VarProxy[scala.Int](ClosureVar_18, ((a) => ClosureVar_18 = a)), scala.Tuple2.apply[scala.Int, scala.Tuple2[squid.lib.`package`.Var[scala.Int], squid.lib.`package`.Var[scala.Boolean]]](ClosureVar_19, scala.Tuple2.apply[squid.lib.`package`.Var[scala.Int], squid.lib.`package`.Var[scala.Boolean]](new squid.lib.VarProxy[scala.Int](ClosureVar_20, ((a) => ClosureVar_20 = a)), new squid.lib.VarProxy[scala.Boolean](v_21, ((a) => v_21 = a)))))))))));
            envVar_3 = x_22;
            false
          })
        }) 
          ()
        ;
        val x_23 = x_2.hasNext;
        x_23.`unary_!`
      }
    else
      ();
    val x_24 = envVar_3;
    if (x_24.isDefined.`unary_!`)
      completed_6 = true
    else
      {
        val x_25 = envVar_3;
        var cont_26: scala.Boolean = true;
        var finished_27: scala.Boolean = false;
        while ({
          val x_28 = cont_26;
          x_28.&&({
            val x_29 = finished_27;
            x_29.`unary_!`
          })
        }) 
          {
            var next_30: scala.Option[scala.Char] = scala.None;
            val x_31 = x_25.get._2._2._2._2._2._2._2._2.!;
            if (x_31)
              {
                while ({
                  val x_32 = x_25.get._2._2._2._2._2._1.!;
                  x_32.<(x_25.get._2._2._2._2._1).&&({
                    val sch_33 = x_25.get;
                    val sch_34 = sch_33._2;
                    val sch_35 = sch_34._2;
                    val sch_36 = sch_35._2;
                    val sch_37 = sch_36._2;
                    val sch_38 = sch_37._2;
                    val sch_39 = sch_38._1;
                    val x_40 = sch_39.!;
                    val x_41 = ((sch_33._1): scala.collection.IndexedSeq[scala.Char]).apply(x_40);
                    val x_42 = sch_39.!;
                    sch_39.:=(x_42.+(1));
                    next_30 = scala.Some.apply[scala.Char](x_41);
                    false
                  })
                }) 
                  ()
                ;
                val x_43 = x_25.get._2._2._2._2._2._1.!;
                if (x_43.==(x_25.get._2._2._2._2._1))
                  x_25.get._2._2._2._2._2._2._2._2.:=(false)
                else
                  ()
              }
            else
              ();
            val x_44 = next_30;
            if (x_44.isDefined.`unary_!`)
              {
                while ({
                  val x_45 = x_25.get._2._2._2._2._2._2._2._1.!;
                  x_45.<(x_25.get._2._2._2._2._2._2._1).&&({
                    val sch_46 = x_25.get;
                    val sch_47 = sch_46._2;
                    val sch_48 = sch_47._2;
                    val sch_49 = sch_48._2;
                    val sch_50 = sch_49._2;
                    val sch_51 = sch_50._2;
                    val sch_52 = sch_51._2;
                    val sch_53 = sch_52._2;
                    val sch_54 = sch_53._1;
                    val x_55 = sch_54.!;
                    val x_56 = ((sch_48._1): scala.collection.IndexedSeq[scala.Char]).apply(x_55);
                    val x_57 = sch_54.!;
                    sch_54.:=(x_57.+(1));
                    next_30 = scala.Some.apply[scala.Char](x_56);
                    false
                  })
                }) 
                  ()
                ;
                val sch_58 = x_25.get;
                val sch_59 = sch_58._2;
                val sch_60 = sch_59._2;
                val sch_61 = sch_60._2;
                val sch_62 = sch_61._2;
                val sch_63 = sch_62._2;
                val sch_64 = sch_63._2;
                val x_65 = sch_64._2._1.!;
                if (x_65.==(sch_64._1))
                  finished_27 = true
                else
                  ()
              }
            else
              ();
            val x_66 = next_30;
            if (x_66.isDefined)
              {
                val x_67 = dropped_4;
                val x_73 = if (x_67.<(x_1.length()))
                  {
                    val x_68 = dropped_4;
                    dropped_4 = x_68.+(1);
                    true
                  }
                else
                  {
                    val x_69 = cur_5;
                    val x_70 = scala.Predef.augmentString(x_69);
                    val x_71 = scala.Predef.StringCanBuildFrom;
                    val x_72 = x_70.:+[scala.Char, java.lang.String](x_66.get)(x_71);
                    cur_5 = x_72;
                    true
                  };
                continue_7 = x_73;
                val x_74 = continue_7;
                cont_26 = x_74
              }
            else
              finished_27 = true
          }
        ;
        val x_75 = finished_27;
        if (x_75)
          envVar_3 = scala.None
        else
          ()
      };
    val x_76 = completed_6;
    x_76.`unary_!`.&&(continue_7)
  }) 
    ()
  ;
  completed_6;
  cur_5
})

// === LateImperative ===

// Transfo time: 0ms  Stringifying time: 100ms

// Same as above.

// === VarFlattening ===

// Transfo time: 362ms  Stringifying time: 164ms

((x$2_0: scala.collection.Iterable[java.lang.String]) => {
  val sch_1 = squid.lib.`package`.uncheckedNullValue[scala.Int];
  val sch_2 = squid.lib.`package`.uncheckedNullValue[scala.collection.immutable.WrappedString];
  val x_3 = sfusion.algo.`package`.joinLinesComplex$default$2;
  val x_4 = x$2_0.iterator;
  var isDefined_5: scala.Boolean = false;
  var lhs_6: scala.collection.immutable.WrappedString = sch_2;
  var lhs_7: scala.Int = sch_1;
  var lhs_8: scala.collection.immutable.WrappedString = sch_2;
  var lhs_9: scala.Int = sch_1;
  var lhs_10: scala.Int = sch_1;
  var flatVar_11: scala.Int = sch_1;
  var lhs_12: scala.Int = sch_1;
  var flatVar_13: scala.Int = sch_1;
  var flatVar_14: scala.Boolean = squid.lib.`package`.uncheckedNullValue[scala.Boolean];
  var dropped_15: scala.Int = 0;
  var cur_16: java.lang.String = "";
  var completed_17: scala.Boolean = false;
  var continue_18: scala.Boolean = false;
  while ({
    val x_19 = isDefined_5;
    if (x_19.`unary_!`)
      {
        while ({
          val x_20 = x_4.hasNext;
          x_20.&&({
            val x_21 = x_4.next();
            val ClosureVar_22 = scala.Predef.wrapString(x_3);
            val sch_23 = ((ClosureVar_22): scala.collection.IndexedSeq[scala.Char]);
            val ClosureVar_24 = sch_23.size;
            val ClosureVar_25 = scala.Predef.wrapString(x_21);
            val sch_26 = ((ClosureVar_25): scala.collection.IndexedSeq[scala.Char]);
            val ClosureVar_27 = sch_26.size;
            val ClosureVar_28 = sch_23.length;
            var ClosureVar_29: scala.Int = 0;
            val ClosureVar_30 = sch_26.length;
            var ClosureVar_31: scala.Int = 0;
            var v_32: scala.Boolean = true;
            lhs_6 = ClosureVar_22;
            lhs_7 = ClosureVar_24;
            lhs_8 = ClosureVar_25;
            lhs_9 = ClosureVar_27;
            lhs_10 = ClosureVar_28;
            val x_33 = ClosureVar_29;
            flatVar_11 = x_33;
            lhs_12 = ClosureVar_30;
            val x_34 = ClosureVar_31;
            flatVar_13 = x_34;
            val x_35 = v_32;
            flatVar_14 = x_35;
            isDefined_5 = true;
            false
          })
        }) 
          ()
        ;
        val x_36 = x_4.hasNext;
        x_36.`unary_!`
      }
    else
      ();
    val x_37 = isDefined_5;
    if (x_37.`unary_!`)
      completed_17 = true
    else
      {
        var cont_38: scala.Boolean = true;
        var finished_39: scala.Boolean = false;
        while ({
          val x_40 = cont_38;
          x_40.&&({
            val x_41 = finished_39;
            x_41.`unary_!`
          })
        }) 
          {
            var isDefined_42: scala.Boolean = false;
            var optVal_43: scala.Char = squid.lib.`package`.uncheckedNullValue[scala.Char];
            val x_44 = flatVar_14;
            if (x_44)
              {
                while ({
                  val x_45 = flatVar_11;
                  val x_46 = lhs_10;
                  x_45.<(x_46).&&({
                    val x_47 = flatVar_11;
                    val x_48 = lhs_6;
                    val x_49 = x_48.apply(x_47);
                    val x_50 = flatVar_11;
                    flatVar_11 = x_50.+(1);
                    optVal_43 = x_49;
                    isDefined_42 = true;
                    false
                  })
                }) 
                  ()
                ;
                val x_51 = flatVar_11;
                val x_52 = lhs_10;
                if (x_51.==(x_52))
                  flatVar_14 = false
                else
                  ()
              }
            else
              ();
            val x_53 = isDefined_42;
            if (x_53.`unary_!`)
              {
                while ({
                  val x_54 = flatVar_13;
                  val x_55 = lhs_12;
                  x_54.<(x_55).&&({
                    val x_56 = flatVar_13;
                    val x_57 = lhs_8;
                    val x_58 = x_57.apply(x_56);
                    val x_59 = flatVar_13;
                    flatVar_13 = x_59.+(1);
                    optVal_43 = x_58;
                    isDefined_42 = true;
                    false
                  })
                }) 
                  ()
                ;
                val x_60 = flatVar_13;
                val x_61 = lhs_12;
                if (x_60.==(x_61))
                  finished_39 = true
                else
                  ()
              }
            else
              ();
            val x_62 = isDefined_42;
            if (x_62)
              {
                val x_63 = dropped_15;
                val x_70 = if (x_63.<(x_3.length()))
                  {
                    val x_64 = dropped_15;
                    dropped_15 = x_64.+(1);
                    true
                  }
                else
                  {
                    val x_65 = cur_16;
                    val x_66 = scala.Predef.augmentString(x_65);
                    val x_67 = scala.Predef.StringCanBuildFrom;
                    val x_68 = optVal_43;
                    val x_69 = x_66.:+[scala.Char, java.lang.String](x_68)(x_67);
                    cur_16 = x_69;
                    true
                  };
                continue_18 = x_70;
                val x_71 = continue_18;
                cont_38 = x_71
              }
            else
              finished_39 = true
          }
        ;
        val x_72 = finished_39;
        if (x_72)
          isDefined_5 = false
        else
          ()
      };
    val x_73 = completed_17;
    x_73.`unary_!`.&&(continue_18)
  }) 
    ()
  ;
  completed_17;
  cur_16
})

// === Low-Level Norm ===

// Transfo time: 71ms  Stringifying time: 187ms

((x$2_0: scala.collection.Iterable[java.lang.String]) => {
  val sch_1 = ((null): scala.collection.immutable.WrappedString);
  val x_2 = sfusion.algo.`package`.joinLinesComplex$default$2;
  val x_3 = x$2_0.iterator;
  var isDefined_4: scala.Boolean = false;
  var lhs_5: scala.collection.immutable.WrappedString = null;
  var lhs_6: scala.Int = 0;
  var lhs_7: scala.collection.immutable.WrappedString = null;
  var lhs_8: scala.Int = 0;
  var lhs_9: scala.Int = 0;
  var flatVar_10: scala.Int = 0;
  var lhs_11: scala.Int = 0;
  var flatVar_12: scala.Int = 0;
  var flatVar_13: scala.Boolean = false;
  var dropped_14: scala.Int = 0;
  var cur_15: java.lang.String = "";
  var completed_16: scala.Boolean = false;
  var continue_17: scala.Boolean = false;
  while ({
    val x_18 = isDefined_4;
    if (x_18)
      ()
    else
      {
        val x_19 = x_3.hasNext;
        if (x_19)
          {
            val x_20 = x_3.next();
            val ClosureVar_21 = scala.Predef.wrapString(x_2);
            val sch_22 = ((ClosureVar_21): scala.collection.IndexedSeq[scala.Char]);
            val ClosureVar_23 = sch_22.size;
            val ClosureVar_24 = scala.Predef.wrapString(x_20);
            val sch_25 = ((ClosureVar_24): scala.collection.IndexedSeq[scala.Char]);
            val ClosureVar_26 = sch_25.size;
            val ClosureVar_27 = sch_22.length;
            var ClosureVar_28: scala.Int = 0;
            val ClosureVar_29 = sch_25.length;
            var ClosureVar_30: scala.Int = 0;
            var v_31: scala.Boolean = true;
            lhs_5 = ClosureVar_21;
            lhs_6 = ClosureVar_23;
            lhs_7 = ClosureVar_24;
            lhs_8 = ClosureVar_26;
            lhs_9 = ClosureVar_27;
            val x_32 = ClosureVar_28;
            flatVar_10 = x_32;
            lhs_11 = ClosureVar_29;
            val x_33 = ClosureVar_30;
            flatVar_12 = x_33;
            val x_34 = v_31;
            flatVar_13 = x_34;
            isDefined_4 = true
          }
        else
          ();
        val x_35 = x_3.hasNext;
        x_35.`unary_!`
      };
    val x_36 = isDefined_4;
    if (x_36)
      {
        var cont_37: scala.Boolean = true;
        var finished_38: scala.Boolean = false;
        while ({
          val x_39 = cont_37;
          x_39.&&({
            val x_40 = finished_38;
            x_40.`unary_!`
          })
        }) 
          {
            var isDefined_41: scala.Boolean = false;
            var optVal_42: scala.Char = '\00';
            val x_43 = flatVar_13;
            if (x_43)
              {
                val x_44 = flatVar_10;
                val x_45 = lhs_9;
                if (x_44.<(x_45))
                  {
                    val x_46 = flatVar_10;
                    val x_47 = lhs_5;
                    val x_48 = x_47.apply(x_46);
                    val x_49 = flatVar_10;
                    flatVar_10 = x_49.+(1);
                    optVal_42 = x_48;
                    isDefined_41 = true
                  }
                else
                  ();
                val x_50 = flatVar_10;
                val x_51 = lhs_9;
                if (x_50.==(x_51))
                  flatVar_13 = false
                else
                  ()
              }
            else
              ();
            val x_52 = isDefined_41;
            if (x_52)
              ()
            else
              {
                val x_53 = flatVar_12;
                val x_54 = lhs_11;
                if (x_53.<(x_54))
                  {
                    val x_55 = flatVar_12;
                    val x_56 = lhs_7;
                    val x_57 = x_56.apply(x_55);
                    val x_58 = flatVar_12;
                    flatVar_12 = x_58.+(1);
                    optVal_42 = x_57;
                    isDefined_41 = true
                  }
                else
                  ();
                val x_59 = flatVar_12;
                val x_60 = lhs_11;
                if (x_59.==(x_60))
                  finished_38 = true
                else
                  ()
              };
            val x_61 = isDefined_41;
            if (x_61)
              {
                val x_62 = dropped_14;
                if (x_62.<(x_2.length()))
                  {
                    val x_63 = dropped_14;
                    dropped_14 = x_63.+(1)
                  }
                else
                  ();
                val x_71 = x_62.<(x_2.length()).`unary_!`.&&({
                  val sch_64 = x_2.length();
                  val sch_65 = x_62.<(sch_64);
                  if (sch_65)
                    ()
                  else
                    {
                      val x_66 = cur_15;
                      val x_67 = scala.Predef.augmentString(x_66);
                      val x_68 = scala.Predef.StringCanBuildFrom;
                      val x_69 = optVal_42;
                      val x_70 = x_67.:+[scala.Char, java.lang.String](x_69)(x_68);
                      cur_15 = x_70
                    };
                  sch_65
                });
                continue_17 = x_71.`unary_!`;
                val x_72 = continue_17;
                cont_37 = x_72
              }
            else
              finished_38 = true
          }
        ;
        val x_73 = finished_38;
        if (x_73)
          isDefined_4 = false
        else
          ()
      }
    else
      completed_16 = true;
    val x_74 = completed_16;
    x_74.`unary_!`.&&(continue_17)
  }) 
    ()
  ;
  completed_16;
  cur_15
})

// === ReNorm (should be the same) ===

// Transfo time: 39ms  Stringifying time: 101ms

// Same as above.
