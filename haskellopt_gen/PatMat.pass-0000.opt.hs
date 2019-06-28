-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 175; Boxes: 46; Branches: 45
-- Apps: 16; Lams: 3; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module PatMat (f0'1,e0'2,f0'0,e1,f1'0,f1,e0'1,f0'2,e0,orZero,f0,f1'2,e1'1,f0'3,e0'0,f1'1,e1'0,e0'3) where

import Control.Exception.Base
import GHC.Classes
import GHC.Maybe
import GHC.Num
import GHC.Types

e0 = (GHC.Maybe.Just 2)

e0'0 = (((GHC.Num.+) 2) 1)

e0'1 = (((GHC.Num.+) 2) 1)

e0'2 = (((GHC.Num.+) 2) 1)

e0'3 = 1

e1 = GHC.Maybe.Nothing

e1'0 = 0

e1'1 = 0

f0 = (\ds -> (_9(# (_8(# (_7(# (_6(# (_5(# (case ds of {Nothing -> _0; Just arg0 -> (_4(# (_3(# (_2(# (_1(# arg0 #)) #)) #)) #))}) #)) #)) #)) #)) #)))

_9(# ds' #) = ds'

_8(# ds'2 #) = ds'2

_7(# ds'3 #) = ds'3

_6(# ds'4 #) = ds'4

_5(# ds'5 #) = ds'5

_0 = (GHC.Maybe.Just 0)

_4(# ds'6 #) = (GHC.Maybe.Just (_10(# ds'6 #)))

_3(# ds'7 #) = ds'7

_2(# ds'8 #) = ds'8

_1(# ds'9 #) = ds'9

f0'0 = (_9(# (_4(# 2 #)) #))

f0'1 = _0

f0'2 = (_9(# (_8(# (_7(# (_4(# (_3(# (_2(# (_10(# (_3(# 3 #)) #)) #)) #)) #)) #)) #)) #))

_10(# ds'10 #) = (((GHC.Num.+) ds'10) 1)

f0'3 = (_9(# (_8(# (_7(# (_6(# (_5(# (_4(# (_3(# (_2(# (_1(# 0 #)) #)) #)) #)) #)) #)) #)) #)) #))

f1 = (\x -> (_11(# x #)))

_11(# x' #) = (case (((GHC.Classes.>) x') 0) of {False -> GHC.Maybe.Nothing; True -> (GHC.Maybe.Just x')})

f1'0 = (_11(# 4 #))

f1'1 = (case (_11(# 5 #)) of {Nothing -> GHC.Types.False; Just arg0' -> GHC.Types.True})

f1'2 = (_12(# (_11(# 5 #)) #))

_12(# ds'11 #) = (case ds'11 of {Nothing -> 0; Just arg0'2 -> arg0'2})

orZero = (\ds'12 -> (_12(# ds'12 #)))
