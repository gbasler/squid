-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrder (hoH,hoHTest3,hoHTest4,hoHTest5) where

import GHC.Num
import GHC.Types

_0 = (GHC.Types.I# 3#)

_1(# _2, _3 #) = (((GHC.Num.*) _3) _2)

_4(# _5 #) = (((GHC.Num.+) _5) (GHC.Types.I# 1#))

_6(# _7 #) = (((GHC.Num.*) _7) (GHC.Types.I# 2#))

_8(# _9, _10 #) = (((GHC.Num.-) _9) _10)

_11(# _9 #) = (((GHC.Num.*) _9) (_1(# (_8(# _9, _0 #)), (_8(# _9, _12 #)) #)))

_13 = ((GHC.Num.+) (GHC.Types.I# 1#))

_14 = ((GHC.Num.*) (GHC.Types.I# 2#))

_12 = (GHC.Types.I# 2#)

hoH = (\f_a -> (_1(# (f_a _0), (f_a _12) #)))

hoHTest3 = (((GHC.Num.+) (_1(# (_13 _0), (_13 _12) #))) (_1(# (_14 _0), (_14 _12) #)))

hoHTest4 = (((GHC.Num.+) (_1(# (_4(# _0 #)), (_4(# _12 #)) #))) (_1(# (_6(# _0 #)), (_6(# _12 #)) #)))

hoHTest5 = (_1(# (_11(# _0 #)), (_11(# _12 #)) #))
