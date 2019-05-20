-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrderRec (rec0,rec1,rec2,rec3) where



_0(# _1 #) = (_1 (\x_a -> ((_0(# _1 #)) x_a)))

_2(# _3, _4 #) = (_4 (_2(# _4, _3 #)))

_5(# _6, _7, _8 #) = (((:) _6) (_7 (_5(# _6, _8, _7 #))))

_9(# _10, _11, _12, _13 #) = (((:) (_12 _13)) (_9(# _13, _12, _11, (_12 _10) #)))

rec0 = (\f_a -> (_0(# f_a #)))

rec1 = (\f_a' -> (\x_a' -> (_2(# f_a', f_a' #))))

rec2 = (\f_a'2 -> (\x_a'2 -> (_5(# x_a'2, f_a'2, f_a'2 #))))

rec3 = (\f_a'3 -> (\x_a'3 -> (\y_a -> (_9(# y_a, f_a'3, f_a'3, x_a'3 #)))))
