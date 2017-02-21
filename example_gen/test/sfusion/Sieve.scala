// Automatically-generated code

// === Init ===

// Transfo time: 0ms  Stringifying time: 22ms

{
  val x_0 = scala.`package`.Right.apply[Nothing, scala.Boolean](false);
  val x_2 = new sfusion.Sequence[scala.Int]((() => sfusion.impl.`package`.iterate[scala.Int](1)(((x$1_1: scala.Int) => x$1_1.+(1)))), x_0);
  val odds_5 = x_2.map[scala.Int](((x$2_3: scala.Int) => {
    val x_4 = x$2_3.*(2);
    x_4.+(1)
  }));
  val divs_6 = odds_5.+:[scala.Int](2);
  val x_17 = x_2.filter(((n_7: scala.Int) => {
    val x_8 = n_7.toDouble;
    val x_9 = scala.math.`package`.sqrt(x_8);
    val x_10 = scala.Predef.doubleWrapper(x_9);
    val x_11 = x_10.ceil;
    val x_12 = x_11.toInt;
    val x_14 = divs_6.takeWhile(((x$4_13: scala.Int) => x$4_13.<=(x_12)));
    x_14.forall(((d_15: scala.Int) => {
      val x_16 = n_7.%(d_15);
      x_16.!=(0)
    }))
  }));
  val x_18 = x_17.+:[scala.Int](2);
  val x_19 = x_18.take(100);
  x_19.fold[scala.Int](0)(((x$6_20: scala.Int, x$7_21: scala.Int) => x$6_20.+(x$7_21)))
}

// === Impl ===

// Transfo time: 24ms  Stringifying time: 62ms

{
  val x_0 = scala.`package`.Right.apply[Nothing, scala.Boolean](false);
  val x_2 = new sfusion.Sequence[scala.Int]((() => sfusion.impl.`package`.iterate[scala.Int](1)(((x$1_1: scala.Int) => x$1_1.+(1)))), x_0);
  val x_3 = x_2.size;
  val x_8 = new sfusion.Sequence[scala.Int]((() => {
    val x_4 = x_2.under;
    val x_5 = x_4.apply();
    sfusion.impl.`package`.map[scala.Int, scala.Int](x_5)(((x$2_6: scala.Int) => {
      val x_7 = x$2_6.*(2);
      x_7.+(1)
    }))
  }), x_3);
  val x_9 = x_8.size;
  val x_10 = sfusion.`package`.addToSize(x_9, 1);
  val x_14 = new sfusion.Sequence[scala.Int]((() => {
    val x_11 = sfusion.impl.`package`.single[scala.Int](2);
    val x_12 = x_8.under;
    val x_13 = x_12.apply();
    sfusion.impl.`package`.concat[scala.Int](x_11, x_13)
  }), x_10);
  val x_15 = x_2.size;
  val x_33 = new sfusion.Sequence[scala.Int]((() => {
    val x_16 = x_2.under;
    val x_17 = x_16.apply();
    sfusion.impl.`package`.filter[scala.Int](x_17)(((n_18: scala.Int) => {
      val x_19 = n_18.toDouble;
      val x_20 = scala.math.`package`.sqrt(x_19);
      val x_21 = scala.Predef.doubleWrapper(x_20);
      val x_22 = x_21.ceil;
      val x_23 = x_22.toInt;
      val x_24 = x_14.size;
      val x_28 = new sfusion.Sequence[scala.Int]((() => {
        val x_25 = x_14.under;
        val x_26 = x_25.apply();
        sfusion.impl.`package`.takeWhile[scala.Int](x_26)(((x$4_27: scala.Int) => x$4_27.<=(x_23)))
      }), x_24);
      val x_29 = x_28.under;
      val x_30 = x_29.apply();
      sfusion.impl.`package`.all[scala.Int](x_30)(((d_31: scala.Int) => {
        val x_32 = n_18.%(d_31);
        x_32.!=(0)
      }))
    }))
  }), x_15);
  val x_34 = x_33.size;
  val x_35 = sfusion.`package`.addToSize(x_34, 1);
  val x_39 = new sfusion.Sequence[scala.Int]((() => {
    val x_36 = sfusion.impl.`package`.single[scala.Int](2);
    val x_37 = x_33.under;
    val x_38 = x_37.apply();
    sfusion.impl.`package`.concat[scala.Int](x_36, x_38)
  }), x_35);
  val x_40 = x_39.size;
  val x_41 = scala.`package`.Left.apply[scala.Int, Nothing](100);
  val x_42 = sfusion.`package`.minSize(x_40, x_41);
  val x_45 = new sfusion.Sequence[scala.Int]((() => {
    val x_43 = x_39.under;
    val x_44 = x_43.apply();
    sfusion.impl.`package`.take[scala.Int](x_44)(100)
  }), x_42);
  val x_46 = x_45.under;
  val x_47 = x_46.apply();
  sfusion.impl.`package`.fold[scala.Int, scala.Int](x_47)(0)(((x$6_48: scala.Int, x$7_49: scala.Int) => x$6_48.+(x$7_49)))
}

// === CtorInline ===

// Transfo time: 36ms  Stringifying time: 41ms

{
  val x_0 = scala.`package`.Right.apply[Nothing, scala.Boolean](false);
  val x_1 = sfusion.`package`.addToSize(((x_0): scala.util.Either[scala.Int, scala.Boolean]), 1);
  val x_2 = sfusion.`package`.addToSize(((x_0): scala.util.Either[scala.Int, scala.Boolean]), 1);
  val x_3 = scala.`package`.Left.apply[scala.Int, Nothing](100);
  val x_4 = sfusion.`package`.minSize(x_2, x_3);
  val x_5 = sfusion.impl.`package`.single[scala.Int](2);
  val x_7 = sfusion.impl.`package`.iterate[scala.Int](1)(((x$1$87_6: scala.Int) => x$1$87_6.+(1)));
  val x_25 = sfusion.impl.`package`.filter[scala.Int](x_7)(((n_8: scala.Int) => {
    val x_9 = n_8.toDouble;
    val x_10 = scala.math.`package`.sqrt(x_9);
    val x_11 = scala.Predef.doubleWrapper(x_10);
    val x_12 = x_11.ceil;
    val x_13 = x_12.toInt;
    val x_14 = sfusion.impl.`package`.single[scala.Int](2);
    val x_16 = sfusion.impl.`package`.iterate[scala.Int](1)(((x$1_15: scala.Int) => x$1_15.+(1)));
    val x_19 = sfusion.impl.`package`.map[scala.Int, scala.Int](x_16)(((x$2_17: scala.Int) => {
      val x_18 = x$2_17.*(2);
      x_18.+(1)
    }));
    val x_20 = sfusion.impl.`package`.concat[scala.Int](x_14, x_19);
    val x_22 = sfusion.impl.`package`.takeWhile[scala.Int](x_20)(((x$4_21: scala.Int) => x$4_21.<=(x_13)));
    sfusion.impl.`package`.all[scala.Int](x_22)(((d_23: scala.Int) => {
      val x_24 = n_8.%(d_23);
      x_24.!=(0)
    }))
  }));
  val x_26 = sfusion.impl.`package`.concat[scala.Int](x_5, x_25);
  val x_27 = sfusion.impl.`package`.take[scala.Int](x_26)(100);
  sfusion.impl.`package`.fold[scala.Int, scala.Int](x_27)(0)(((x$6_28: scala.Int, x$7_29: scala.Int) => x$6_28.+(x$7_29)))
}

// === ImplOptim ===

// Transfo time: 0ms  Stringifying time: 17ms

// Same as above.

// === Imperative ===

// Transfo time: 127ms  Stringifying time: 174ms

{
  val x_0 = scala.`package`.Right.apply[Nothing, scala.Boolean](false);
  val x_1 = sfusion.`package`.addToSize(((x_0): scala.util.Either[scala.Int, scala.Boolean]), 1);
  val x_2 = sfusion.`package`.addToSize(((x_0): scala.util.Either[scala.Int, scala.Boolean]), 1);
  val x_3 = scala.`package`.Left.apply[scala.Int, Nothing](100);
  val x_4 = sfusion.`package`.minSize(x_2, x_3);
  var cur_5: scala.Int = 1;
  var curIsLhs_6: scala.Boolean = true;
  var taken_7: scala.Int = 0;
  var cur_8: scala.Int = 0;
  var cont_9: scala.Boolean = true;
  var finished_10: scala.Boolean = false;
  while ({
    val x_11 = cont_9;
    x_11.&&({
      val x_12 = finished_10;
      x_12.`unary_!`
    })
  }) 
    {
      var next_13: scala.Option[scala.Int] = scala.None;
      val x_14 = curIsLhs_6;
      if (x_14)
        {
          next_13 = scala.Some.apply[scala.Int](2);
          if (true)
            curIsLhs_6 = false
          else
            ()
        }
      else
        ();
      val x_15 = next_13;
      val x_16 = x_15.isDefined.`unary_!`;
      if (x_16)
        {
          while ({
            val x_17 = cur_5;
            val x_18 = x_17.toDouble;
            val x_19 = scala.math.`package`.sqrt(x_18);
            val x_20 = scala.Predef.doubleWrapper(x_19);
            val x_21 = x_20.ceil;
            val x_22 = x_21.toInt;
            var cur_23: scala.Int = 1;
            var curIsLhs_24: scala.Boolean = true;
            var stop_25: scala.Boolean = false;
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
                var next_30: scala.Option[scala.Int] = scala.None;
                val x_31 = curIsLhs_24;
                if (x_31)
                  {
                    next_30 = scala.Some.apply[scala.Int](2);
                    if (true)
                      curIsLhs_24 = false
                    else
                      ()
                  }
                else
                  ();
                val x_32 = next_30;
                val x_33 = x_32.isDefined.`unary_!`;
                if (x_33)
                  {
                    while ({
                      val x_34 = cur_23;
                      val x_35 = x_34.*(2);
                      val x_36 = x_35.+(1);
                      next_30 = scala.Some.apply[scala.Int](x_36);
                      false.&&({
                        val x_37 = cur_23;
                        val x_38 = x_37.+(1);
                        cur_23 = x_38;
                        true
                      })
                    }) 
                      ()
                    ;
                    if (false)
                      finished_27 = true
                    else
                      ()
                  }
                else
                  ();
                val x_39 = next_30;
                if (x_39.isDefined)
                  {
                    val x_40 = x_39.get.<=(x_22);
                    val x_42 = if (x_40)
                      {
                        val x_41 = x_17.%(x_39.get);
                        x_41.!=(0)
                      }
                    else
                      {
                        stop_25 = true;
                        false
                      };
                    cont_26 = x_42
                  }
                else
                  finished_27 = true
              }
            ;
            val x_43 = finished_27;
            val x_44 = x_43.||(stop_25);
            val x_45 = x_44.`unary_!`;
            val x_46 = x_45.||({
              next_13 = scala.Some.apply[scala.Int](x_17);
              false
            });
            x_46.&&({
              val x_47 = cur_5;
              val x_48 = x_47.+(1);
              cur_5 = x_48;
              true
            })
          }) 
            ()
          ;
          if (false)
            finished_10 = true
          else
            ()
        }
      else
        ();
      val x_49 = next_13;
      if (x_49.isDefined)
        {
          val x_50 = taken_7;
          val x_51 = x_50.<(100);
          val x_57 = x_51.&&({
            val x_52 = taken_7;
            val x_53 = x_52.+(1);
            taken_7 = x_53;
            val x_54 = cur_8;
            val x_55 = x_54.+(x_49.get);
            cur_8 = x_55;
            true.&&({
              val x_56 = taken_7;
              x_56.<(100)
            })
          });
          cont_9 = x_57
        }
      else
        finished_10 = true
    }
  ;
  val x_58 = finished_10;
  x_58.||({
    val x_59 = taken_7;
    x_59.==(100)
  });
  cur_8
}

// === Low-Level Norm ===

// Transfo time: 109ms  Stringifying time: 112ms

{
  val x_0 = scala.`package`.Right.apply[Nothing, scala.Boolean](false);
  val x_1 = sfusion.`package`.addToSize(((x_0): scala.util.Either[scala.Int, scala.Boolean]), 1);
  val x_2 = sfusion.`package`.addToSize(((x_0): scala.util.Either[scala.Int, scala.Boolean]), 1);
  val x_3 = scala.`package`.Left.apply[scala.Int, Nothing](100);
  val x_4 = sfusion.`package`.minSize(x_2, x_3);
  var cur_5: scala.Int = 1;
  var curIsLhs_6: scala.Boolean = true;
  var taken_7: scala.Int = 0;
  var cur_8: scala.Int = 0;
  var cont_9: scala.Boolean = true;
  var finished_10: scala.Boolean = false;
  while ({
    val x_11 = cont_9;
    x_11.&&({
      val x_12 = finished_10;
      x_12.`unary_!`
    })
  }) 
    {
      var isDefined_13: scala.Boolean = false;
      var optVal_14: scala.Int = 0;
      val x_15 = curIsLhs_6;
      if (x_15)
        {
          optVal_14 = 2;
          isDefined_13 = true;
          curIsLhs_6 = false
        }
      else
        ();
      val x_16 = isDefined_13;
      if (x_16)
        ()
      else
        while ({
          val x_17 = cur_5;
          val x_18 = x_17.toDouble;
          val x_19 = scala.math.`package`.sqrt(x_18);
          val x_20 = scala.Predef.doubleWrapper(x_19);
          val x_21 = x_20.ceil;
          val x_22 = x_21.toInt;
          var cur_23: scala.Int = 1;
          var curIsLhs_24: scala.Boolean = true;
          var stop_25: scala.Boolean = false;
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
              var isDefined_30: scala.Boolean = false;
              var optVal_31: scala.Int = 0;
              val x_32 = curIsLhs_24;
              if (x_32)
                {
                  optVal_31 = 2;
                  isDefined_30 = true;
                  curIsLhs_24 = false
                }
              else
                ();
              val x_33 = isDefined_30;
              if (x_33)
                ()
              else
                {
                  val x_34 = cur_23;
                  val x_35 = x_34.*(2);
                  val x_36 = x_35.+(1);
                  optVal_31 = x_36;
                  isDefined_30 = true
                };
              val x_37 = isDefined_30;
              if (x_37)
                {
                  val x_38 = optVal_31;
                  val x_39 = x_38.<=(x_22);
                  val x_42 = if (x_39)
                    {
                      val x_40 = optVal_31;
                      val x_41 = x_17.%(x_40);
                      x_41.!=(0)
                    }
                  else
                    {
                      stop_25 = true;
                      false
                    };
                  cont_26 = x_42
                }
              else
                finished_27 = true
            }
          ;
          val x_43 = finished_27;
          val x_44 = x_43.||(stop_25);
          val x_45 = x_44.`unary_!`;
          if (x_45)
            ()
          else
            {
              optVal_14 = x_17;
              isDefined_13 = true;
              false
            };
          if (x_45)
            {
              val x_46 = cur_5;
              val x_47 = x_46.+(1);
              cur_5 = x_47;
              true
            }
          else
            ();
          x_45
        }) 
          ()
        ;
      val x_48 = isDefined_13;
      if (x_48)
        {
          val x_49 = taken_7;
          val x_50 = x_49.<(100);
          val x_57 = x_50.&&({
            val x_51 = taken_7;
            val x_52 = x_51.+(1);
            taken_7 = x_52;
            val x_53 = cur_8;
            val x_54 = optVal_14;
            val x_55 = x_53.+(x_54);
            cur_8 = x_55;
            val x_56 = taken_7;
            x_56.<(100)
          });
          cont_9 = x_57
        }
      else
        finished_10 = true
    }
  ;
  val x_58 = finished_10;
  x_58.||({
    val x_59 = taken_7;
    x_59.==(100)
  });
  cur_8
}

// === ReNorm (should be the same) ===

// Transfo time: 23ms  Stringifying time: 33ms

// Same as above.
