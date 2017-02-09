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
  val x_5 = scala.Option.empty[scala.Int];
  var v_6: scala.Option[scala.Int] = x_5;
  var taken_7: scala.Int = 0;
  var cur_8: scala.Int = 0;
  val x_9 = curIsLhs_4;
  val x_170 = if (x_9)
    {
      var cont_10: scala.Boolean = true;
      val lhsEnded_23 = {
        val k_22 = ((l_11: scala.Int) => {
          val x_12 = taken_7;
          val x_13 = x_12.<(100);
          val x_21 = x_13.&&({
            val x_14 = taken_7;
            val x_15 = x_14.+(1);
            taken_7 = x_15;
            val x_19 = {
              val a_16 = l_11;
              val x_17 = cur_8;
              val x_18 = x_17.+(a_16);
              cur_8 = x_18;
              true
            };
            x_19.&&({
              val x_20 = taken_7;
              x_20.<(100)
            })
          });
          cont_10 = x_21;
          cont_10
        });
        k_22(2);
        true
      };
      if (lhsEnded_23)
        {
          curIsLhs_4 = false;
          val k_87 = ((a_24: scala.Int) => {
            val x_25 = a_24.toDouble;
            val x_26 = scala.math.`package`.sqrt(x_25);
            val x_27 = scala.Predef.doubleWrapper(x_26);
            val x_28 = x_27.ceil;
            val x_29 = x_28.toInt;
            var cur_30: scala.Int = 1;
            var curIsLhs_31: scala.Boolean = true;
            val x_32 = scala.Option.empty[scala.Int];
            var v_33: scala.Option[scala.Int] = x_32;
            var stop_34: scala.Boolean = false;
            val x_35 = curIsLhs_31;
            val x_72 = if (x_35)
              {
                var cont_36: scala.Boolean = true;
                val lhsEnded_42 = {
                  val k_41 = ((l_37: scala.Int) => {
                    val x_38 = l_37.<=(x_29);
                    val x_40 = if (x_38)
                      {
                        val x_39 = a_24.%(l_37);
                        x_39.!=(0)
                      }
                    else
                      {
                        stop_34 = true;
                        false
                      };
                    cont_36 = x_40;
                    cont_36
                  });
                  k_41(2);
                  true
                };
                if (lhsEnded_42)
                  {
                    curIsLhs_31 = false;
                    val k_51 = ((a_43: scala.Int) => {
                      val x_44 = a_43.*(2);
                      val x_45 = x_44.+(1);
                      val x_46 = cont_36;
                      if (x_46)
                        {
                          val x_47 = x_45.<=(x_29);
                          val x_49 = if (x_47)
                            {
                              val x_48 = a_24.%(x_45);
                              x_48.!=(0)
                            }
                          else
                            {
                              stop_34 = true;
                              false
                            };
                          cont_36 = x_49
                        }
                      else
                        {
                          val x_50 = scala.Some.apply[scala.Int](x_45);
                          v_33 = x_50
                        };
                      cont_36
                    });
                    while ({
                      val x_52 = cur_30;
                      val x_53 = k_51(x_52);
                      x_53.&&({
                        val x_54 = cur_30;
                        val x_55 = x_54.+(1);
                        cur_30 = x_55;
                        true
                      })
                    }) 
                      ()
                    ;
                    false
                  }
                else
                  false
              }
            else
              {
                val x_56 = v_33;
                val x_57 = x_56.isEmpty;
                if (x_57)
                  {
                    val k_63 = ((a_58: scala.Int) => {
                      val x_59 = a_58.*(2);
                      val x_60 = x_59.+(1);
                      val x_61 = x_60.<=(x_29);
                      if (x_61)
                        {
                          val x_62 = a_24.%(x_60);
                          x_62.!=(0)
                        }
                      else
                        {
                          stop_34 = true;
                          false
                        }
                    });
                    while ({
                      val x_64 = cur_30;
                      val x_65 = k_63(x_64);
                      x_65.&&({
                        val x_66 = cur_30;
                        val x_67 = x_66.+(1);
                        cur_30 = x_67;
                        true
                      })
                    }) 
                      ()
                    ;
                    false
                  }
                else
                  {
                    v_33 = scala.None;
                    val x_68 = v_33;
                    val x_69 = x_68.get;
                    val x_70 = x_69.<=(x_29);
                    if (x_70)
                      {
                        val x_71 = a_24.%(x_69);
                        x_71.!=(0)
                      }
                    else
                      {
                        stop_34 = true;
                        false
                      }
                  }
              };
            val x_73 = x_72.||(stop_34);
            val x_74 = x_73.`unary_!`;
            x_74.||({
              val x_75 = cont_10;
              if (x_75)
                {
                  val x_76 = taken_7;
                  val x_77 = x_76.<(100);
                  val x_85 = x_77.&&({
                    val x_78 = taken_7;
                    val x_79 = x_78.+(1);
                    taken_7 = x_79;
                    val x_83 = {
                      val a_80 = a_24;
                      val x_81 = cur_8;
                      val x_82 = x_81.+(a_80);
                      cur_8 = x_82;
                      true
                    };
                    x_83.&&({
                      val x_84 = taken_7;
                      x_84.<(100)
                    })
                  });
                  cont_10 = x_85
                }
              else
                {
                  val x_86 = scala.Some.apply[scala.Int](a_24);
                  v_6 = x_86
                };
              cont_10
            })
          });
          while ({
            val x_88 = cur_3;
            val x_89 = k_87(x_88);
            x_89.&&({
              val x_90 = cur_3;
              val x_91 = x_90.+(1);
              cur_3 = x_91;
              true
            })
          }) 
            ()
          ;
          false
        }
      else
        false
    }
  else
    {
      val x_92 = v_6;
      val x_93 = x_92.isEmpty;
      if (x_93)
        {
          val k_154 = ((a_94: scala.Int) => {
            val x_95 = a_94.toDouble;
            val x_96 = scala.math.`package`.sqrt(x_95);
            val x_97 = scala.Predef.doubleWrapper(x_96);
            val x_98 = x_97.ceil;
            val x_99 = x_98.toInt;
            var cur_100: scala.Int = 1;
            var curIsLhs_101: scala.Boolean = true;
            val x_102 = scala.Option.empty[scala.Int];
            var v_103: scala.Option[scala.Int] = x_102;
            var stop_104: scala.Boolean = false;
            val x_105 = curIsLhs_101;
            val x_142 = if (x_105)
              {
                var cont_106: scala.Boolean = true;
                val lhsEnded_112 = {
                  val k_111 = ((l_107: scala.Int) => {
                    val x_108 = l_107.<=(x_99);
                    val x_110 = if (x_108)
                      {
                        val x_109 = a_94.%(l_107);
                        x_109.!=(0)
                      }
                    else
                      {
                        stop_104 = true;
                        false
                      };
                    cont_106 = x_110;
                    cont_106
                  });
                  k_111(2);
                  true
                };
                if (lhsEnded_112)
                  {
                    curIsLhs_101 = false;
                    val k_121 = ((a_113: scala.Int) => {
                      val x_114 = a_113.*(2);
                      val x_115 = x_114.+(1);
                      val x_116 = cont_106;
                      if (x_116)
                        {
                          val x_117 = x_115.<=(x_99);
                          val x_119 = if (x_117)
                            {
                              val x_118 = a_94.%(x_115);
                              x_118.!=(0)
                            }
                          else
                            {
                              stop_104 = true;
                              false
                            };
                          cont_106 = x_119
                        }
                      else
                        {
                          val x_120 = scala.Some.apply[scala.Int](x_115);
                          v_103 = x_120
                        };
                      cont_106
                    });
                    while ({
                      val x_122 = cur_100;
                      val x_123 = k_121(x_122);
                      x_123.&&({
                        val x_124 = cur_100;
                        val x_125 = x_124.+(1);
                        cur_100 = x_125;
                        true
                      })
                    }) 
                      ()
                    ;
                    false
                  }
                else
                  false
              }
            else
              {
                val x_126 = v_103;
                val x_127 = x_126.isEmpty;
                if (x_127)
                  {
                    val k_133 = ((a_128: scala.Int) => {
                      val x_129 = a_128.*(2);
                      val x_130 = x_129.+(1);
                      val x_131 = x_130.<=(x_99);
                      if (x_131)
                        {
                          val x_132 = a_94.%(x_130);
                          x_132.!=(0)
                        }
                      else
                        {
                          stop_104 = true;
                          false
                        }
                    });
                    while ({
                      val x_134 = cur_100;
                      val x_135 = k_133(x_134);
                      x_135.&&({
                        val x_136 = cur_100;
                        val x_137 = x_136.+(1);
                        cur_100 = x_137;
                        true
                      })
                    }) 
                      ()
                    ;
                    false
                  }
                else
                  {
                    v_103 = scala.None;
                    val x_138 = v_103;
                    val x_139 = x_138.get;
                    val x_140 = x_139.<=(x_99);
                    if (x_140)
                      {
                        val x_141 = a_94.%(x_139);
                        x_141.!=(0)
                      }
                    else
                      {
                        stop_104 = true;
                        false
                      }
                  }
              };
            val x_143 = x_142.||(stop_104);
            val x_144 = x_143.`unary_!`;
            x_144.||({
              val x_145 = taken_7;
              val x_146 = x_145.<(100);
              x_146.&&({
                val x_147 = taken_7;
                val x_148 = x_147.+(1);
                taken_7 = x_148;
                val x_152 = {
                  val a_149 = a_94;
                  val x_150 = cur_8;
                  val x_151 = x_150.+(a_149);
                  cur_8 = x_151;
                  true
                };
                x_152.&&({
                  val x_153 = taken_7;
                  x_153.<(100)
                })
              })
            })
          });
          while ({
            val x_155 = cur_3;
            val x_156 = k_154(x_155);
            x_156.&&({
              val x_157 = cur_3;
              val x_158 = x_157.+(1);
              cur_3 = x_158;
              true
            })
          }) 
            ()
          ;
          false
        }
      else
        {
          v_6 = scala.None;
          val x_159 = v_6;
          val x_160 = x_159.get;
          val x_161 = taken_7;
          val x_162 = x_161.<(100);
          x_162.&&({
            val x_163 = taken_7;
            val x_164 = x_163.+(1);
            taken_7 = x_164;
            val x_168 = {
              val a_165 = x_160;
              val x_166 = cur_8;
              val x_167 = x_166.+(a_165);
              cur_8 = x_167;
              true
            };
            x_168.&&({
              val x_169 = taken_7;
              x_169.<(100)
            })
          })
        }
    };
  x_170.||({
    val x_171 = taken_7;
    x_171.==(100)
  });
  cur_8
}
