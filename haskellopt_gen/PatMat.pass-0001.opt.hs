-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 454; Boxes: 102; Branches: 29
-- Apps: 88; Lams: 21; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module PatMat (tf,x,f0'1,bat,e0'2,f0'0,e1,f1'0,f1,e0'1,slt0,f0'2,e0,orZero,f0,f1'2,tlsf,ds,tls,e1'1,f0'3,e0'0,f1'1,slt1,usumls,usum,e1'0,e0'3) where

import Control.Exception.Base
import Data.Foldable
import GHC.Base
import GHC.Classes
import GHC.Maybe
import GHC.Num
import GHC.Prim
import GHC.Types

bat = (\ds' -> (case ds' of {(,) arg0 arg1 -> (_0(# arg0, arg1 #))}))

_0(# ds'2, ds'3 #) = (let sh = (r(# ds'2, ds'3 #)) in (((GHC.Num.+) (((GHC.Num.*) sh) sh)) (GHC.Num.fromInteger 1)))

ds = 0

e0 = (GHC.Maybe.Just 2)

e0'0 = (((GHC.Num.+) 2) 1)

e0'1 = (((GHC.Num.+) 2) 1)

e0'2 = (((GHC.Num.+) 2) 1)

e0'3 = 1

e1 = GHC.Maybe.Nothing

e1'0 = 0

e1'1 = 0

f0 = (\ds'4 -> (case ds'4 of {Nothing -> (GHC.Maybe.Just 0); Just arg0' -> (GHC.Maybe.Just (((GHC.Num.+) arg0') 1))}))

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

_2(# ds'5 #) = (case ds'5 of {Nothing -> 0; Just arg0'3 -> arg0'3})

orZero = (\ds'6 -> (_2(# ds'6 #)))

slt0 = (\x'3 -> (((,) (_0(# Data.Foldable.sum, x'3 #))) (_0(# (((GHC.Base..) Data.Foldable.sum) (\xs -> (GHC.Base.build (\c -> (\n -> (((GHC.Base.foldr (((GHC.Base..) c) (\x'4 -> (((GHC.Num.*) x'4) 2)))) n) xs)))))), (((GHC.Num.+) x'3) 1) #))))

slt1 = (\ls -> (GHC.Base.build (\c' -> (\n' -> (((GHC.Base.foldr (((GHC.Base..) (((GHC.Base..) c') (\c'2 -> 0))) (\c'3 -> 0))) n') ls)))))

tf = (\ds'7 -> (_3(# ds'7 #)))

_3(# ds'8 #) = (case ds'8 of {[] -> _4; (:) arg0'4 arg1' -> (case arg1' of {[] -> _4; (:) arg0'5 arg1'2 -> (case arg1'2 of {[] -> _4; (:) arg0'6 arg1'3 -> (case arg1'3 of {[] -> _4; (:) arg0'7 arg1'4 -> (case arg1'4 of {[] -> (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) arg0'4) arg0'5)) arg0'6)) arg0'7); (:) arg0'8 arg1'5 -> _4})})})})})

tls = tls'

tls' = (((:) 1) (((:) 2) (((:) 3) (((:) 4) []))))

tlsf = (_3(# tls' #))

usum = (\eta -> (_5(# eta #)))

_5(# ds'9 #) = (case ds'9 of {[] -> (GHC.Num.fromInteger 0); (:) arg0'9 arg1'6 -> (((GHC.Num.+) arg0'9) (_5(# arg1'6 #)))})

usumls = (_5(# tls' #))

r(# ds'10, ds'11 #) = (ds'10 (GHC.Base.build (\c'4 -> (\n'2 -> (((GHC.Base.foldr (((GHC.Base..) c'4) (\c'5 -> (((GHC.Num.+) c'5) ds'11)))) n'2) [])))))

_4 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/PatMat.hs:48:1-22|function tf"#)
