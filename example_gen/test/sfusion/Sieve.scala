// Automatically-generated code

// === Init ===

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
  val x_13 = new sfusion.Sequence[scala.Int]((() => {
    val x_10 = sfusion.impl.`package`.single[scala.Int](2);
    val x_11 = x_8.under;
    val x_12 = x_11.apply();
    sfusion.impl.`package`.concat[scala.Int](x_10, x_12)
  }), x_9);
  val x_14 = x_2.size;
  val x_32 = new sfusion.Sequence[scala.Int]((() => {
    val x_15 = x_2.under;
    val x_16 = x_15.apply();
    sfusion.impl.`package`.filter[scala.Int](x_16)(((n_17: scala.Int) => {
      val x_18 = n_17.toDouble;
      val x_19 = scala.math.`package`.sqrt(x_18);
      val x_20 = scala.Predef.doubleWrapper(x_19);
      val x_21 = x_20.ceil;
      val x_22 = x_21.toInt;
      val x_23 = x_13.size;
      val x_27 = new sfusion.Sequence[scala.Int]((() => {
        val x_24 = x_13.under;
        val x_25 = x_24.apply();
        sfusion.impl.`package`.takeWhile[scala.Int](x_25)(((x$4_26: scala.Int) => x$4_26.<=(x_22)))
      }), x_23);
      val x_28 = x_27.under;
      val x_29 = x_28.apply();
      sfusion.impl.`package`.all[scala.Int](x_29)(((d_30: scala.Int) => {
        val x_31 = n_17.%(d_30);
        x_31.!=(0)
      }))
    }))
  }), x_14);
  val x_33 = x_32.size;
  val x_37 = new sfusion.Sequence[scala.Int]((() => {
    val x_34 = sfusion.impl.`package`.single[scala.Int](2);
    val x_35 = x_32.under;
    val x_36 = x_35.apply();
    sfusion.impl.`package`.concat[scala.Int](x_34, x_36)
  }), x_33);
  val x_38 = x_37.size;
  val x_39 = scala.`package`.Left.apply[scala.Int, Nothing](100);
  val x_40 = sfusion.`package`.minSize(x_38, x_39);
  val x_43 = new sfusion.Sequence[scala.Int]((() => {
    val x_41 = x_37.under;
    val x_42 = x_41.apply();
    sfusion.impl.`package`.take[scala.Int](x_42)(100)
  }), x_40);
  val x_44 = x_43.under;
  val x_45 = x_44.apply();
  sfusion.impl.`package`.fold[scala.Int, scala.Int](x_45)(0)(((x$6_46: scala.Int, x$7_47: scala.Int) => x$6_46.+(x$7_47)))
}

// === CtorInline ===

{
  val x_0 = scala.`package`.Right.apply[Nothing, scala.Boolean](false);
  val x_1 = scala.`package`.Left.apply[scala.Int, Nothing](100);
  val x_2 = sfusion.`package`.minSize(((x_0): scala.util.Either[scala.Int, scala.Boolean]), x_1);
  val x_3 = sfusion.impl.`package`.single[scala.Int](2);
  val x_5 = sfusion.impl.`package`.iterate[scala.Int](1)(((x$1_4: scala.Int) => x$1_4.+(1)));
  val x_23 = sfusion.impl.`package`.filter[scala.Int](x_5)(((n_6: scala.Int) => {
    val x_7 = n_6.toDouble;
    val x_8 = scala.math.`package`.sqrt(x_7);
    val x_9 = scala.Predef.doubleWrapper(x_8);
    val x_10 = x_9.ceil;
    val x_11 = x_10.toInt;
    val x_12 = sfusion.impl.`package`.single[scala.Int](2);
    val x_14 = sfusion.impl.`package`.iterate[scala.Int](1)(((x$1_13: scala.Int) => x$1_13.+(1)));
    val x_17 = sfusion.impl.`package`.map[scala.Int, scala.Int](x_14)(((x$2_15: scala.Int) => {
      val x_16 = x$2_15.*(2);
      x_16.+(1)
    }));
    val x_18 = sfusion.impl.`package`.concat[scala.Int](x_12, x_17);
    val x_20 = sfusion.impl.`package`.takeWhile[scala.Int](x_18)(((x$4_19: scala.Int) => x$4_19.<=(x_11)));
    sfusion.impl.`package`.all[scala.Int](x_20)(((d_21: scala.Int) => {
      val x_22 = n_6.%(d_21);
      x_22.!=(0)
    }))
  }));
  val x_24 = sfusion.impl.`package`.concat[scala.Int](x_3, x_23);
  val x_25 = sfusion.impl.`package`.take[scala.Int](x_24)(100);
  sfusion.impl.`package`.fold[scala.Int, scala.Int](x_25)(0)(((x$6_26: scala.Int, x$7_27: scala.Int) => x$6_26.+(x$7_27)))
}

// === Imperative ===

{
  val x_0 = scala.`package`.Right.apply[Nothing, scala.Boolean](false);
  val x_1 = scala.`package`.Left.apply[scala.Int, Nothing](100);
  val x_2 = sfusion.`package`.minSize(((x_0): scala.util.Either[scala.Int, scala.Boolean]), x_1);
  var cur_3: scala.Int = 1;
  var curIsLhs_4: scala.Boolean = true;
  var taken_5: scala.Int = 0;
  var cur_6: scala.Int = 0;
  var cont_7: scala.Boolean = true;
  var finished_8: scala.Boolean = false;
  while ({
    val x_9 = cont_7;
    x_9.&&({
      val x_10 = finished_8;
      x_10.`unary_!`
    })
  }) 
    {
      var next_11: scala.Option[scala.Int] = scala.None;
      val x_12 = curIsLhs_4;
      if (x_12)
        {
          val x_13 = scala.Some.apply[scala.Int](2);
          next_11 = x_13;
          if (true)
            curIsLhs_4 = false
          else
            ()
        }
      else
        ();
      val x_14 = next_11;
      val x_15 = x_14.isEmpty;
      if (x_15)
        {
          while ({
            val x_16 = cur_3;
            val x_17 = x_16.toDouble;
            val x_18 = scala.math.`package`.sqrt(x_17);
            val x_19 = scala.Predef.doubleWrapper(x_18);
            val x_20 = x_19.ceil;
            val x_21 = x_20.toInt;
            var cur_22: scala.Int = 1;
            var curIsLhs_23: scala.Boolean = true;
            var stop_24: scala.Boolean = false;
            var cont_25: scala.Boolean = true;
            var finished_26: scala.Boolean = false;
            while ({
              val x_27 = cont_25;
              x_27.&&({
                val x_28 = finished_26;
                x_28.`unary_!`
              })
            }) 
              {
                var next_29: scala.Option[scala.Int] = scala.None;
                val x_30 = curIsLhs_23;
                if (x_30)
                  {
                    val x_31 = scala.Some.apply[scala.Int](2);
                    next_29 = x_31;
                    if (true)
                      curIsLhs_23 = false
                    else
                      ()
                  }
                else
                  ();
                val x_32 = next_29;
                val x_33 = x_32.isEmpty;
                if (x_33)
                  {
                    while ({
                      val x_34 = cur_22;
                      val x_35 = x_34.*(2);
                      val x_36 = x_35.+(1);
                      val x_37 = scala.Some.apply[scala.Int](x_36);
                      next_29 = x_37;
                      false.&&({
                        val x_38 = cur_22;
                        val x_39 = x_38.+(1);
                        cur_22 = x_39;
                        true
                      })
                    }) 
                      ()
                    ;
                    if (false)
                      finished_26 = true
                    else
                      ()
                  }
                else
                  ();
                val x_40 = next_29;
                x_40.fold[scala.Unit](finished_26 = true)(((x_41: scala.Int) => {
                  val x_42 = x_41.<=(x_21);
                  val x_44 = if (x_42)
                    {
                      val x_43 = x_16.%(x_41);
                      x_43.!=(0)
                    }
                  else
                    {
                      stop_24 = true;
                      false
                    };
                  cont_25 = x_44
                }))
              }
            ;
            val x_45 = finished_26;
            val x_46 = x_45.||(stop_24);
            val x_47 = x_46.`unary_!`;
            val x_49 = x_47.||({
              val x_48 = scala.Some.apply[scala.Int](x_16);
              next_11 = x_48;
              false
            });
            x_49.&&({
              val x_50 = cur_3;
              val x_51 = x_50.+(1);
              cur_3 = x_51;
              true
            })
          }) 
            ()
          ;
          if (false)
            finished_8 = true
          else
            ()
        }
      else
        ();
      val x_52 = next_11;
      x_52.fold[scala.Unit](finished_8 = true)(((x_53: scala.Int) => {
        val x_54 = taken_5;
        val x_55 = x_54.<(100);
        val x_61 = x_55.&&({
          val x_56 = taken_5;
          val x_57 = x_56.+(1);
          taken_5 = x_57;
          val x_58 = cur_6;
          val x_59 = x_58.+(x_53);
          cur_6 = x_59;
          true.&&({
            val x_60 = taken_5;
            x_60.<(100)
          })
        });
        cont_7 = x_61
      }))
    }
  ;
  val x_62 = finished_8;
  x_62.||({
    val x_63 = taken_5;
    x_63.==(100)
  });
  cur_6
}

// === Low-Level Norm ===

{
  val x_0 = scala.`package`.Right.apply[Nothing, scala.Boolean](false);
  val x_1 = scala.`package`.Left.apply[scala.Int, Nothing](100);
  val x_2 = sfusion.`package`.minSize(((x_0): scala.util.Either[scala.Int, scala.Boolean]), x_1);
  var cur_3: scala.Int = 1;
  var curIsLhs_4: scala.Boolean = true;
  var taken_5: scala.Int = 0;
  var cur_6: scala.Int = 0;
  var cont_7: scala.Boolean = true;
  var finished_8: scala.Boolean = false;
  while ({
    val x_9 = cont_7;
    x_9.&&({
      val x_10 = finished_8;
      x_10.`unary_!`
    })
  }) 
    {
      var next_11: scala.Option[scala.Int] = scala.None;
      val x_12 = curIsLhs_4;
      if (x_12)
        {
          val x_13 = scala.Some.apply[scala.Int](2);
          next_11 = x_13;
          curIsLhs_4 = false
        }
      else
        ();
      val x_14 = next_11;
      val x_15 = x_14.isEmpty;
      if (x_15)
        while ({
          val x_16 = cur_3;
          val x_17 = x_16.toDouble;
          val x_18 = scala.math.`package`.sqrt(x_17);
          val x_19 = scala.Predef.doubleWrapper(x_18);
          val x_20 = x_19.ceil;
          val x_21 = x_20.toInt;
          var cur_22: scala.Int = 1;
          var curIsLhs_23: scala.Boolean = true;
          var stop_24: scala.Boolean = false;
          var cont_25: scala.Boolean = true;
          var finished_26: scala.Boolean = false;
          while ({
            val x_27 = cont_25;
            x_27.&&({
              val x_28 = finished_26;
              x_28.`unary_!`
            })
          }) 
            {
              var next_29: scala.Option[scala.Int] = scala.None;
              val x_30 = curIsLhs_23;
              if (x_30)
                {
                  val x_31 = scala.Some.apply[scala.Int](2);
                  next_29 = x_31;
                  curIsLhs_23 = false
                }
              else
                ();
              val x_32 = next_29;
              val x_33 = x_32.isEmpty;
              if (x_33)
                while ({
                  val x_34 = cur_22;
                  val x_35 = x_34.*(2);
                  val x_36 = x_35.+(1);
                  val x_37 = scala.Some.apply[scala.Int](x_36);
                  next_29 = x_37;
                  false
                }) 
                  ()
                
              else
                ();
              val x_38 = next_29;
              x_38.fold[scala.Unit](finished_26 = true)(((x_39: scala.Int) => {
                val x_40 = x_39.<=(x_21);
                val x_42 = if (x_40)
                  {
                    val x_41 = x_16.%(x_39);
                    x_41.!=(0)
                  }
                else
                  {
                    stop_24 = true;
                    false
                  };
                cont_25 = x_42
              }))
            }
          ;
          val x_43 = finished_26;
          val x_44 = x_43.||(stop_24);
          val x_45 = x_44.`unary_!`;
          val x_47 = x_45.||({
            val x_46 = scala.Some.apply[scala.Int](x_16);
            next_11 = x_46;
            false
          });
          x_47.&&({
            val x_48 = cur_3;
            val x_49 = x_48.+(1);
            cur_3 = x_49;
            true
          })
        }) 
          ()
        
      else
        ();
      val x_50 = next_11;
      x_50.fold[scala.Unit](finished_8 = true)(((x_51: scala.Int) => {
        val x_52 = taken_5;
        val x_53 = x_52.<(100);
        val x_59 = x_53.&&({
          val x_54 = taken_5;
          val x_55 = x_54.+(1);
          taken_5 = x_55;
          val x_56 = cur_6;
          val x_57 = x_56.+(x_51);
          cur_6 = x_57;
          val x_58 = taken_5;
          x_58.<(100)
        });
        cont_7 = x_59
      }))
    }
  ;
  val x_60 = finished_8;
  x_60.||({
    val x_61 = taken_5;
    x_61.==(100)
  });
  cur_6
}

// === ReNorm (should be the same) ===

// Same as above.
