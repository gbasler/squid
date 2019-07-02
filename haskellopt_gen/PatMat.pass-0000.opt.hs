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

f0 = (\ds -> (case ds of {Nothing -> _0; Just arg0 -> (_1(# arg0 #))}))

_0 = (GHC.Maybe.Just 0)

_1(# ds' #) = (GHC.Maybe.Just (_2(# ds' #)))

f0'0 = (_1(# 2 #))

f0'1 = _0

f0'2 = (_1(# (_2(# 3 #)) #))

_2(# ds'2 #) = (((GHC.Num.+) ds'2) 1)

f0'3 = (_1(# 0 #))

f1 = (\x -> (_3(# x #)))

_3(# x' #) = (case (((GHC.Classes.>) x') 0) of {False -> GHC.Maybe.Nothing; True -> (GHC.Maybe.Just x')})

f1'0 = (_3(# 4 #))

f1'1 = (case (_3(# 5 #)) of {Nothing -> GHC.Types.False; Just arg0' -> GHC.Types.True})

f1'2 = (_4(# (_3(# 5 #)) #))

_4(# ds'3 #) = (case ds'3 of {Nothing -> 0; Just arg0'2 -> arg0'2})

orZero = (\ds'4 -> (_4(# ds'4 #)))
