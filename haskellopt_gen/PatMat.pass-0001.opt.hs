-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 242; Boxes: 53; Branches: 15
-- Apps: 54; Lams: 9; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module PatMat (x,f0'1,bat,e0'2,f0'0,e1,f1'0,f1,e0'1,slt0,f0'2,e0,orZero,f0,f1'2,e1'1,f0'3,e0'0,f1'1,slt1,arg,e1'0,e0'3) where

import Control.Exception.Base
import Data.Foldable
import GHC.Base
import GHC.Classes
import GHC.Maybe
import GHC.Num
import GHC.Types

arg = 0

bat = (\ds -> (case ds of {(,) arg0 arg1 -> (_0(# arg0, arg1 #))}))

_0(# ds', ds'2 #) = (let sh = (r(# ds', ds'2 #)) in (((GHC.Num.+) (((GHC.Num.*) sh) sh)) (GHC.Num.fromInteger 1)))

e0 = (GHC.Maybe.Just 2)

e0'0 = (((GHC.Num.+) 2) 1)

e0'1 = (((GHC.Num.+) 2) 1)

e0'2 = (((GHC.Num.+) 2) 1)

e0'3 = 1

e1 = GHC.Maybe.Nothing

e1'0 = 0

e1'1 = 0

f0 = (\ds'3 -> (case ds'3 of {Nothing -> (GHC.Maybe.Just 0); Just arg0' -> (GHC.Maybe.Just (((GHC.Num.+) arg0') 1))}))

f0'0 = (GHC.Maybe.Just (((GHC.Num.+) 2) 1))

f0'1 = (GHC.Maybe.Just 0)

f0'2 = (GHC.Maybe.Just (((GHC.Num.+) (((GHC.Num.+) 3) 1)) 1))

f0'3 = (GHC.Maybe.Just (((GHC.Num.+) 0) 1))

x = 0

f1 = (\x' -> (_1(# x' #)))

_1(# x'2 #) = (case (((GHC.Classes.>) x'2) 0) of {False -> GHC.Maybe.Nothing; True -> (GHC.Maybe.Just x'2)})

f1'0 = (_1(# 4 #))

f1'1 = (case (_1(# 5 #)) of {Nothing -> GHC.Types.False; Just arg0'2 -> GHC.Types.True})

f1'2 = (_2(# (_1(# 5 #)) #))

_2(# ds'4 #) = (case ds'4 of {Nothing -> 0; Just arg0'3 -> arg0'3})

orZero = (\ds'5 -> (_2(# ds'5 #)))

slt0 = (\x'3 -> (((,) (_0(# Data.Foldable.sum, x'3 #))) (_0(# (((GHC.Base..) Data.Foldable.sum) (GHC.Base.map (\x'4 -> (((GHC.Num.*) x'4) 2)))), (((GHC.Num.+) x'3) 1) #))))

slt1 = (((GHC.Base..) (GHC.Base.id (GHC.Base.map (\c -> 0)))) (GHC.Base.map (\c' -> 0)))

r(# ds'6, ds'7 #) = (ds'6 ((GHC.Base.map (\c'2 -> (((GHC.Num.+) c'2) ds'7))) []))
