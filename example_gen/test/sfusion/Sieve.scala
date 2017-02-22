// Automatically-generated code

// === Init ===

// Transfo time: 0ms  Stringifying time: 14ms

{
  val posNats_1 = new sfusion.Sequence[scala.Int]((() => sfusion.impl.`package`.iterate[scala.Int](1)(((x$1_0: scala.Int) => x$1_0.+(1)))), scala.`package`.Right.apply[Nothing, scala.Boolean](false));
  val odds_3 = posNats_1.map[scala.Int](((x$2_2: scala.Int) => x$2_2.*(2).+(1)));
  val divs_4 = odds_3.+:[scala.Int](2);
  val x_13 = posNats_1.filter(((n_5: scala.Int) => {
    val x_6 = scala.math.`package`.sqrt(n_5.toDouble);
    val x_7 = scala.Predef.doubleWrapper(x_6);
    val x_8 = x_7.ceil;
    val x_9 = x_8.toInt;
    val x_11 = divs_4.takeWhile(((x$4_10: scala.Int) => x$4_10.<=(x_9)));
    x_11.forall(((d_12: scala.Int) => n_5.%(d_12).!=(0)))
  }));
  val x_14 = x_13.+:[scala.Int](2);
  val x_15 = x_14.take(100);
  x_15.fold[scala.Int](0)(((x$6_16: scala.Int, x$7_17: scala.Int) => x$6_16.+(x$7_17)))
}

// === Impl ===

// Transfo time: 19ms  Stringifying time: 42ms

{
  val posNats_1 = new sfusion.Sequence[scala.Int]((() => sfusion.impl.`package`.iterate[scala.Int](1)(((x$1_0: scala.Int) => x$1_0.+(1)))), scala.`package`.Right.apply[Nothing, scala.Boolean](false));
  val x_2 = posNats_1.size;
  val x_6 = new sfusion.Sequence[scala.Int]((() => {
    val x_3 = posNats_1.under;
    val x_4 = x_3.apply();
    sfusion.impl.`package`.map[scala.Int, scala.Int](x_4)(((x$2_5: scala.Int) => x$2_5.*(2).+(1)))
  }), x_2);
  val x_7 = x_6.size;
  val x_11 = new sfusion.Sequence[scala.Int]((() => {
    val x_8 = sfusion.impl.`package`.single[scala.Int](2);
    val x_9 = x_6.under;
    val x_10 = x_9.apply();
    sfusion.impl.`package`.concat[scala.Int](x_8, x_10)
  }), sfusion.`package`.addToSize(x_7, 1));
  val x_12 = posNats_1.size;
  val x_28 = new sfusion.Sequence[scala.Int]((() => {
    val x_13 = posNats_1.under;
    val x_14 = x_13.apply();
    sfusion.impl.`package`.filter[scala.Int](x_14)(((n_15: scala.Int) => {
      val x_16 = scala.math.`package`.sqrt(n_15.toDouble);
      val x_17 = scala.Predef.doubleWrapper(x_16);
      val x_18 = x_17.ceil;
      val x_19 = x_18.toInt;
      val x_20 = x_11.size;
      val x_24 = new sfusion.Sequence[scala.Int]((() => {
        val x_21 = x_11.under;
        val x_22 = x_21.apply();
        sfusion.impl.`package`.takeWhile[scala.Int](x_22)(((x$4_23: scala.Int) => x$4_23.<=(x_19)))
      }), x_20);
      val x_25 = x_24.under;
      val x_26 = x_25.apply();
      sfusion.impl.`package`.all[scala.Int](x_26)(((d_27: scala.Int) => n_15.%(d_27).!=(0)))
    }))
  }), x_12);
  val x_29 = x_28.size;
  val x_33 = new sfusion.Sequence[scala.Int]((() => {
    val x_30 = sfusion.impl.`package`.single[scala.Int](2);
    val x_31 = x_28.under;
    val x_32 = x_31.apply();
    sfusion.impl.`package`.concat[scala.Int](x_30, x_32)
  }), sfusion.`package`.addToSize(x_29, 1));
  val x_34 = x_33.size;
  val x_37 = new sfusion.Sequence[scala.Int]((() => {
    val x_35 = x_33.under;
    val x_36 = x_35.apply();
    sfusion.impl.`package`.take[scala.Int](x_36)(100)
  }), sfusion.`package`.minSize(x_34, scala.`package`.Left.apply[scala.Int, Nothing](100)));
  val x_38 = x_37.under;
  val x_39 = x_38.apply();
  sfusion.impl.`package`.fold[scala.Int, scala.Int](x_39)(0)(((x$6_40: scala.Int, x$7_41: scala.Int) => x$6_40.+(x$7_41)))
}

// === CtorInline ===

// Transfo time: 21ms  Stringifying time: 16ms

{
  val x_0 = sfusion.impl.`package`.single[scala.Int](2);
  val x_2 = sfusion.impl.`package`.iterate[scala.Int](1)(((x$1$73_1: scala.Int) => x$1$73_1.+(1)));
  val x_17 = sfusion.impl.`package`.filter[scala.Int](x_2)(((n_3: scala.Int) => {
    val x_4 = scala.math.`package`.sqrt(n_3.toDouble);
    val x_5 = scala.Predef.doubleWrapper(x_4);
    val x_6 = x_5.ceil;
    val x_7 = x_6.toInt;
    val x_8 = sfusion.impl.`package`.single[scala.Int](2);
    val x_10 = sfusion.impl.`package`.iterate[scala.Int](1)(((x$1_9: scala.Int) => x$1_9.+(1)));
    val x_12 = sfusion.impl.`package`.map[scala.Int, scala.Int](x_10)(((x$2_11: scala.Int) => x$2_11.*(2).+(1)));
    val x_13 = sfusion.impl.`package`.concat[scala.Int](x_8, x_12);
    val x_15 = sfusion.impl.`package`.takeWhile[scala.Int](x_13)(((x$4_14: scala.Int) => x$4_14.<=(x_7)));
    sfusion.impl.`package`.all[scala.Int](x_15)(((d_16: scala.Int) => n_3.%(d_16).!=(0)))
  }));
  val x_18 = sfusion.impl.`package`.concat[scala.Int](x_0, x_17);
  val x_19 = sfusion.impl.`package`.take[scala.Int](x_18)(100);
  sfusion.impl.`package`.fold[scala.Int, scala.Int](x_19)(0)(((x$6_20: scala.Int, x$7_21: scala.Int) => x$6_20.+(x$7_21)))
}

// === ImplOptim ===

// Transfo time: 0ms  Stringifying time: 5ms

// Same as above.

// === Imperative ===

// Transfo time: 75ms  Stringifying time: 81ms

{
  var cur_0: scala.Int = 1;
  var curIsLhs_1: scala.Boolean = true;
  var taken_2: scala.Int = 0;
  var cur_3: scala.Int = 0;
  var cont_4: scala.Boolean = true;
  var finished_5: scala.Boolean = false;
  while ({
    val x_6 = cont_4;
    x_6.&&({
      val x_7 = finished_5;
      x_7.`unary_!`
    })
  }) 
    {
      var next_8: scala.Option[scala.Int] = scala.None;
      val x_9 = curIsLhs_1;
      if (x_9)
        {
          next_8 = scala.Some.apply[scala.Int](2);
          if (true)
            curIsLhs_1 = false
          else
            ()
        }
      else
        ();
      val x_10 = next_8;
      if (x_10.isDefined.`unary_!`)
        {
          while ({
            val x_11 = cur_0;
            val x_12 = scala.math.`package`.sqrt(x_11.toDouble);
            val x_13 = scala.Predef.doubleWrapper(x_12);
            val x_14 = x_13.ceil;
            val x_15 = x_14.toInt;
            var cur_16: scala.Int = 1;
            var curIsLhs_17: scala.Boolean = true;
            var stop_18: scala.Boolean = false;
            var cont_19: scala.Boolean = true;
            var finished_20: scala.Boolean = false;
            while ({
              val x_21 = cont_19;
              x_21.&&({
                val x_22 = finished_20;
                x_22.`unary_!`
              })
            }) 
              {
                var next_23: scala.Option[scala.Int] = scala.None;
                val x_24 = curIsLhs_17;
                if (x_24)
                  {
                    next_23 = scala.Some.apply[scala.Int](2);
                    if (true)
                      curIsLhs_17 = false
                    else
                      ()
                  }
                else
                  ();
                val x_25 = next_23;
                if (x_25.isDefined.`unary_!`)
                  {
                    while ({
                      val x_26 = cur_16;
                      next_23 = scala.Some.apply[scala.Int](x_26.*(2).+(1));
                      false.&&({
                        val x_27 = cur_16;
                        cur_16 = x_27.+(1);
                        true
                      })
                    }) 
                      ()
                    ;
                    if (false)
                      finished_20 = true
                    else
                      ()
                  }
                else
                  ();
                val x_28 = next_23;
                if (x_28.isDefined)
                  {
                    val x_29 = if (x_28.get.<=(x_15))
                      x_11.%(x_28.get).!=(0)
                    else
                      {
                        stop_18 = true;
                        false
                      };
                    cont_19 = x_29
                  }
                else
                  finished_20 = true
              }
            ;
            val x_30 = finished_20;
            val x_31 = x_30.||(stop_18);
            val x_32 = x_31.`unary_!`.||({
              next_8 = scala.Some.apply[scala.Int](x_11);
              false
            });
            x_32.&&({
              val x_33 = cur_0;
              cur_0 = x_33.+(1);
              true
            })
          }) 
            ()
          ;
          if (false)
            finished_5 = true
          else
            ()
        }
      else
        ();
      val x_34 = next_8;
      if (x_34.isDefined)
        {
          val x_35 = taken_2;
          val x_39 = x_35.<(100).&&({
            val x_36 = taken_2;
            taken_2 = x_36.+(1);
            val x_37 = cur_3;
            cur_3 = x_37.+(x_34.get);
            true.&&({
              val x_38 = taken_2;
              x_38.<(100)
            })
          });
          cont_4 = x_39
        }
      else
        finished_5 = true
    }
  ;
  val x_40 = finished_5;
  x_40.||({
    val x_41 = taken_2;
    x_41.==(100)
  });
  cur_3
}

// === Low-Level Norm ===

// Transfo time: 63ms  Stringifying time: 87ms

{
  var cur_0: scala.Int = 1;
  var curIsLhs_1: scala.Boolean = true;
  var taken_2: scala.Int = 0;
  var cur_3: scala.Int = 0;
  var cont_4: scala.Boolean = true;
  var finished_5: scala.Boolean = false;
  while ({
    val x_6 = cont_4;
    x_6.&&({
      val x_7 = finished_5;
      x_7.`unary_!`
    })
  }) 
    {
      var isDefined_8: scala.Boolean = false;
      var optVal_9: scala.Int = 0;
      val x_10 = curIsLhs_1;
      if (x_10)
        {
          optVal_9 = 2;
          isDefined_8 = true;
          curIsLhs_1 = false
        }
      else
        ();
      val x_11 = isDefined_8;
      if (x_11)
        ()
      else
        while ({
          val x_12 = cur_0;
          val x_13 = scala.math.`package`.sqrt(x_12.toDouble);
          val x_14 = scala.Predef.doubleWrapper(x_13);
          val x_15 = x_14.ceil;
          val x_16 = x_15.toInt;
          var cur_17: scala.Int = 1;
          var curIsLhs_18: scala.Boolean = true;
          var stop_19: scala.Boolean = false;
          var cont_20: scala.Boolean = true;
          var finished_21: scala.Boolean = false;
          while ({
            val x_22 = cont_20;
            x_22.&&({
              val x_23 = finished_21;
              x_23.`unary_!`
            })
          }) 
            {
              var isDefined_24: scala.Boolean = false;
              var optVal_25: scala.Int = 0;
              val x_26 = curIsLhs_18;
              if (x_26)
                {
                  optVal_25 = 2;
                  isDefined_24 = true;
                  curIsLhs_18 = false
                }
              else
                ();
              val x_27 = isDefined_24;
              if (x_27)
                ()
              else
                {
                  val x_28 = cur_17;
                  optVal_25 = x_28.*(2).+(1);
                  isDefined_24 = true
                };
              val x_29 = isDefined_24;
              if (x_29)
                {
                  val x_30 = optVal_25;
                  val x_32 = if (x_30.<=(x_16))
                    {
                      val x_31 = optVal_25;
                      x_12.%(x_31).!=(0)
                    }
                  else
                    {
                      stop_19 = true;
                      false
                    };
                  cont_20 = x_32
                }
              else
                finished_21 = true
            }
          ;
          val x_33 = finished_21;
          val x_34 = x_33.||(stop_19);
          if (x_34.`unary_!`.`unary_!`)
            {
              optVal_9 = x_12;
              isDefined_8 = true;
              false
            }
          else
            ();
          if (x_34.`unary_!`)
            {
              val x_35 = cur_0;
              cur_0 = x_35.+(1);
              true
            }
          else
            ();
          x_34.`unary_!`
        }) 
          ()
        ;
      val x_36 = isDefined_8;
      if (x_36)
        {
          val x_37 = taken_2;
          val x_42 = x_37.<(100).&&({
            val x_38 = taken_2;
            taken_2 = x_38.+(1);
            val x_39 = cur_3;
            val x_40 = optVal_9;
            cur_3 = x_39.+(x_40);
            val x_41 = taken_2;
            x_41.<(100)
          });
          cont_4 = x_42
        }
      else
        finished_5 = true
    }
  ;
  val x_43 = finished_5;
  x_43.||({
    val x_44 = taken_2;
    x_44.==(100)
  });
  cur_3
}

// === ReNorm (should be the same) ===

// Transfo time: 30ms  Stringifying time: 37ms

// Same as above.
