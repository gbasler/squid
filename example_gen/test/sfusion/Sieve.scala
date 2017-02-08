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
  var nextFromRhs_5: scala.Option[scala.Int] = scala.Option.empty[scala.Int];
  var taken_6: scala.Int = 0;
  var cur_7: scala.Int = 0;
  val x_8 = curIsLhs_4;
  val x_171 = if (x_8)
    {
      var cont_9: scala.Boolean = true;
      val lhsEnded_23 = {
        val k_22 = ((l_10: scala.Int) => {
          val x_11 = taken_6;
          val x_12 = x_11.<(100);
          val x_21 = if (x_12)
            {
              val x_13 = taken_6;
              val x_14 = x_13.+(1);
              taken_6 = x_14;
              val x_18 = {
                val a_15 = l_10;
                val x_16 = cur_7;
                val x_17 = x_16.+(a_15);
                cur_7 = x_17;
                true
              };
              val x_19 = taken_6;
              val x_20 = x_19.<(100);
              x_18.&&(x_20)
            }
          else
            false;
          cont_9 = x_21;
          cont_9
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
            var nextFromRhs_32: scala.Option[scala.Int] = scala.Option.empty[scala.Int];
            var stop_33: scala.Boolean = false;
            val x_34 = curIsLhs_31;
            val x_71 = if (x_34)
              {
                var cont_35: scala.Boolean = true;
                val lhsEnded_41 = {
                  val k_40 = ((l_36: scala.Int) => {
                    val x_37 = l_36.<=(x_29);
                    val x_39 = if (x_37)
                      {
                        val x_38 = a_24.%(l_36);
                        x_38.!=(0)
                      }
                    else
                      {
                        stop_33 = true;
                        false
                      };
                    cont_35 = x_39;
                    cont_35
                  });
                  k_40(2);
                  true
                };
                if (lhsEnded_41)
                  {
                    curIsLhs_31 = false;
                    val k_50 = ((a_42: scala.Int) => {
                      val x_43 = a_42.*(2);
                      val x_44 = x_43.+(1);
                      val x_45 = cont_35;
                      if (x_45)
                        {
                          val x_46 = x_44.<=(x_29);
                          val x_48 = if (x_46)
                            {
                              val x_47 = a_24.%(x_44);
                              x_47.!=(0)
                            }
                          else
                            {
                              stop_33 = true;
                              false
                            };
                          cont_35 = x_48
                        }
                      else
                        {
                          val x_49 = scala.Some.apply[scala.Int](x_44);
                          nextFromRhs_32 = x_49
                        };
                      cont_35
                    });
                    while ({
                      val x_51 = cur_30;
                      val x_52 = k_50(x_51);
                      if (x_52)
                        {
                          val x_53 = cur_30;
                          val x_54 = x_53.+(1);
                          cur_30 = x_54;
                          true
                        }
                      else
                        false
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
                val x_55 = nextFromRhs_32;
                val x_56 = x_55.isEmpty;
                if (x_56)
                  {
                    val k_62 = ((a_57: scala.Int) => {
                      val x_58 = a_57.*(2);
                      val x_59 = x_58.+(1);
                      val x_60 = x_59.<=(x_29);
                      if (x_60)
                        {
                          val x_61 = a_24.%(x_59);
                          x_61.!=(0)
                        }
                      else
                        {
                          stop_33 = true;
                          false
                        }
                    });
                    while ({
                      val x_63 = cur_30;
                      val x_64 = k_62(x_63);
                      if (x_64)
                        {
                          val x_65 = cur_30;
                          val x_66 = x_65.+(1);
                          cur_30 = x_66;
                          true
                        }
                      else
                        false
                    }) 
                      ()
                    ;
                    false
                  }
                else
                  {
                    nextFromRhs_32 = scala.None;
                    val x_67 = nextFromRhs_32;
                    val x_68 = x_67.get;
                    val x_69 = x_68.<=(x_29);
                    if (x_69)
                      {
                        val x_70 = a_24.%(x_68);
                        x_70.!=(0)
                      }
                    else
                      {
                        stop_33 = true;
                        false
                      }
                  }
              };
            val x_72 = stop_33;
            val x_73 = x_71.||(x_72);
            if (x_73)
              {
                val x_74 = cont_9;
                if (x_74)
                  {
                    val x_75 = taken_6;
                    val x_76 = x_75.<(100);
                    val x_85 = if (x_76)
                      {
                        val x_77 = taken_6;
                        val x_78 = x_77.+(1);
                        taken_6 = x_78;
                        val x_82 = {
                          val a_79 = a_24;
                          val x_80 = cur_7;
                          val x_81 = x_80.+(a_79);
                          cur_7 = x_81;
                          true
                        };
                        val x_83 = taken_6;
                        val x_84 = x_83.<(100);
                        x_82.&&(x_84)
                      }
                    else
                      false;
                    cont_9 = x_85
                  }
                else
                  {
                    val x_86 = scala.Some.apply[scala.Int](a_24);
                    nextFromRhs_5 = x_86
                  };
                cont_9
              }
            else
              true
          });
          while ({
            val x_88 = cur_3;
            val x_89 = k_87(x_88);
            if (x_89)
              {
                val x_90 = cur_3;
                val x_91 = x_90.+(1);
                cur_3 = x_91;
                true
              }
            else
              false
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
      val x_92 = nextFromRhs_5;
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
            var nextFromRhs_102: scala.Option[scala.Int] = scala.Option.empty[scala.Int];
            var stop_103: scala.Boolean = false;
            val x_104 = curIsLhs_101;
            val x_141 = if (x_104)
              {
                var cont_105: scala.Boolean = true;
                val lhsEnded_111 = {
                  val k_110 = ((l_106: scala.Int) => {
                    val x_107 = l_106.<=(x_99);
                    val x_109 = if (x_107)
                      {
                        val x_108 = a_94.%(l_106);
                        x_108.!=(0)
                      }
                    else
                      {
                        stop_103 = true;
                        false
                      };
                    cont_105 = x_109;
                    cont_105
                  });
                  k_110(2);
                  true
                };
                if (lhsEnded_111)
                  {
                    curIsLhs_101 = false;
                    val k_120 = ((a_112: scala.Int) => {
                      val x_113 = a_112.*(2);
                      val x_114 = x_113.+(1);
                      val x_115 = cont_105;
                      if (x_115)
                        {
                          val x_116 = x_114.<=(x_99);
                          val x_118 = if (x_116)
                            {
                              val x_117 = a_94.%(x_114);
                              x_117.!=(0)
                            }
                          else
                            {
                              stop_103 = true;
                              false
                            };
                          cont_105 = x_118
                        }
                      else
                        {
                          val x_119 = scala.Some.apply[scala.Int](x_114);
                          nextFromRhs_102 = x_119
                        };
                      cont_105
                    });
                    while ({
                      val x_121 = cur_100;
                      val x_122 = k_120(x_121);
                      if (x_122)
                        {
                          val x_123 = cur_100;
                          val x_124 = x_123.+(1);
                          cur_100 = x_124;
                          true
                        }
                      else
                        false
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
                val x_125 = nextFromRhs_102;
                val x_126 = x_125.isEmpty;
                if (x_126)
                  {
                    val k_132 = ((a_127: scala.Int) => {
                      val x_128 = a_127.*(2);
                      val x_129 = x_128.+(1);
                      val x_130 = x_129.<=(x_99);
                      if (x_130)
                        {
                          val x_131 = a_94.%(x_129);
                          x_131.!=(0)
                        }
                      else
                        {
                          stop_103 = true;
                          false
                        }
                    });
                    while ({
                      val x_133 = cur_100;
                      val x_134 = k_132(x_133);
                      if (x_134)
                        {
                          val x_135 = cur_100;
                          val x_136 = x_135.+(1);
                          cur_100 = x_136;
                          true
                        }
                      else
                        false
                    }) 
                      ()
                    ;
                    false
                  }
                else
                  {
                    nextFromRhs_102 = scala.None;
                    val x_137 = nextFromRhs_102;
                    val x_138 = x_137.get;
                    val x_139 = x_138.<=(x_99);
                    if (x_139)
                      {
                        val x_140 = a_94.%(x_138);
                        x_140.!=(0)
                      }
                    else
                      {
                        stop_103 = true;
                        false
                      }
                  }
              };
            val x_142 = stop_103;
            val x_143 = x_141.||(x_142);
            if (x_143)
              {
                val x_144 = taken_6;
                val x_145 = x_144.<(100);
                if (x_145)
                  {
                    val x_146 = taken_6;
                    val x_147 = x_146.+(1);
                    taken_6 = x_147;
                    val x_151 = {
                      val a_148 = a_94;
                      val x_149 = cur_7;
                      val x_150 = x_149.+(a_148);
                      cur_7 = x_150;
                      true
                    };
                    val x_152 = taken_6;
                    val x_153 = x_152.<(100);
                    x_151.&&(x_153)
                  }
                else
                  false
              }
            else
              true
          });
          while ({
            val x_155 = cur_3;
            val x_156 = k_154(x_155);
            if (x_156)
              {
                val x_157 = cur_3;
                val x_158 = x_157.+(1);
                cur_3 = x_158;
                true
              }
            else
              false
          }) 
            ()
          ;
          false
        }
      else
        {
          nextFromRhs_5 = scala.None;
          val x_159 = nextFromRhs_5;
          val x_160 = x_159.get;
          val x_161 = taken_6;
          val x_162 = x_161.<(100);
          if (x_162)
            {
              val x_163 = taken_6;
              val x_164 = x_163.+(1);
              taken_6 = x_164;
              val x_168 = {
                val a_165 = x_160;
                val x_166 = cur_7;
                val x_167 = x_166.+(a_165);
                cur_7 = x_167;
                true
              };
              val x_169 = taken_6;
              val x_170 = x_169.<(100);
              x_168.&&(x_170)
            }
          else
            false
        }
    };
  val x_172 = taken_6;
  val x_173 = x_172.==(100);
  x_171.||(x_173);
  cur_7
}
