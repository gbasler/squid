-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 142; Boxes: 34; Branches: 4
-- Apps: 26; Lams: 3; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module PatMat (x,f0'1,e0'2,f0'0,e1,f1'0,f1,e0'1,f0'2,e0,orZero,f0,f1'2,e1'1,f0'3,e0'0,f1'1,e1'0,e0'3) where

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

f0 = (\ds -> (case ds of {Nothing -> (GHC.Maybe.Just 0); Just arg0 -> (GHC.Maybe.Just (((GHC.Num.+) arg0) 1))}))

f0'0 = (GHC.Maybe.Just (((GHC.Num.+) 2) 1))

f0'1 = (GHC.Maybe.Just 0)

f0'2 = (GHC.Maybe.Just (((GHC.Num.+) (((GHC.Num.+) 3) 1)) 1))

f0'3 = (GHC.Maybe.Just (((GHC.Num.+) 0) 1))

x = 0

f1 = (\x' -> (_0(# x' #)))

_0(# x'2 #) = (case (((GHC.Classes.>) x'2) 0) of {False -> GHC.Maybe.Nothing; True -> (GHC.Maybe.Just x'2)})

f1'0 = (_0(# 4 #))

f1'1 = (case (_0(# 5 #)) of {Nothing -> GHC.Types.False; Just arg0' -> GHC.Types.True})

f1'2 = (_1(# (_0(# 5 #)) #))

_1(# ds' #) = (case ds' of {Nothing -> 0; Just arg0'2 -> arg0'2})

orZero = (\ds'2 -> (_1(# ds'2 #)))
