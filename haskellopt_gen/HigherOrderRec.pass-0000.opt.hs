-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrderRec (rec0,rec1,rec2,rec3) where



_0(# _1, _2 #) = (_2 (_0(# _2, _1 #)))

_3(# _4, _5, _6 #) = (((:) _4) (_5 (_3(# _4, _6, _5 #))))

_7(# _8, _9, _10, _11 #) = (((:) (_10 _11)) (_7(# _11, _10, _9, (_10 _8) #)))

_12(# _13 #) = (_13 (\x_a -> ((_12(# _13 #)) x_a)))

rec0 = (\f_a -> (_12(# f_a #)))

rec1 = (\f_a' -> (\x_a' -> (_0(# f_a', f_a' #))))

rec2 = (\f_a'2 -> (\x_a'2 -> (_3(# x_a'2, f_a'2, f_a'2 #))))

rec3 = (\f_a'3 -> (\x_a'3 -> (\y_a -> (_7(# y_a, f_a'3, f_a'3, x_a'3 #)))))
