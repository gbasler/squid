-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 511; Boxes: 115; Branches: 77
-- Apps: 82; Lams: 22; Unreduced Redexes: 1

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module PatMat (tf,f0'1,e0'2,f0'0,e1,f1'0,f1,e0'1,slt0,f0'2,e0,orZero,f0,f1'2,tlsf,tls,e1'1,f0'3,e0'0,f1'1,slt1,usumls,usum,e1'0,e0'3) where

import Control.Exception.Base
import Data.Foldable
import GHC.Base
import GHC.Classes
import GHC.Maybe
import GHC.Num
import GHC.Prim
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

slt0 = (\x'2 -> (((,) (_5(# Data.Foldable.sum, x'2 #))) (_5(# (((GHC.Base..) Data.Foldable.sum) (\xs -> (GHC.Base.build (\c -> (\n -> (((GHC.Base.foldr (((GHC.Base..) c) (\x'3 -> (((GHC.Num.*) x'3) 2)))) n) xs)))))), (((GHC.Num.+) x'2) 1) #))))

_5(# ds'5, ds'6 #) = (let sh = (r(# ds'5, ds'6 #)) in (((GHC.Num.+) (((GHC.Num.*) sh) sh)) (GHC.Num.fromInteger 1)))

slt1 = (\ls -> (GHC.Base.build (\c' -> (\n' -> (((GHC.Base.foldr (((GHC.Base..) (((GHC.Base..) c') (\c'2 -> 0))) (\c'3 -> 0))) n') ls)))))

tf = (\ds'7 -> (case ds'7 of {(:) arg0'3 arg1 -> (case arg1 of {(:) arg0'4 arg1' -> (case arg1' of {(:) arg0'5 arg1'2 -> (case arg1'2 of {(:) arg0'6 arg1'3 -> (case arg1'3 of {[] -> (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) arg0'3) arg0'4)) arg0'5)) arg0'6); (_) -> _6}); (_) -> _6}); (_) -> _6}); (_) -> _6}); (_) -> _6}))

_6 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/PatMat.hs:48:1-22|function tf"#)

tls = tls'

tls' = (((:) 1) (((:) 2) (((:) 3) (((:) 4) []))))

tlsf = _6

usum = (\ds'8 -> (_7(# ds'8 #)))

_7(# ds'9 #) = (case ds'9 of {[] -> (GHC.Num.fromInteger 0); (:) arg0'7 arg1'4 -> (((GHC.Num.+) arg0'7) (_7(# arg1'4 #)))})

usumls = (_7(# tls' #))

r(# ds'10, ds'11 #) = (ds'10 (GHC.Base.build (\c'4 -> (\n'2 -> (((GHC.Base.foldr (((GHC.Base..) c'4) (\c'5 -> (((GHC.Num.+) c'5) ds'11)))) n'2) [])))))
