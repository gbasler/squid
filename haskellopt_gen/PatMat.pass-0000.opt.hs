-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 940; Boxes: 235; Branches: 182
-- Apps: 124; Lams: 27; Unreduced Redexes: 1

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module PatMat (t3'0,f0'1,t1'1,t0,e0'2,usum'0,f0'0,e1,f1'0,f2'0,f1,e0'1,t3,slt0,f0'2,e0,orZero,f0,t0'0,f1'2,e1'1,t2,f0'3,e0'0,t1'0,f1'1,slt1,t1,t'ls,usum,e1'0,e0'3,f2) where

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

f2 = (\ds'4 -> (case ds'4 of {(,,) arg0'3 arg1 arg2 -> (_5(# arg0'3, arg1, arg2 #))}))

_5(# ds'5, ds'6, ds'7 #) = (((GHC.Num.+) (((GHC.Num.+) ds'5) ds'6)) ds'7)

f2'0 = (_5(# 1, 2, 3 #))

orZero = (\ds'8 -> (_4(# ds'8 #)))

slt0 = (\x'2 -> (((,) (_6(# Data.Foldable.sum, x'2 #))) (_6(# (((GHC.Base..) Data.Foldable.sum) (\xs -> (GHC.Base.build (\c -> (\n -> (((GHC.Base.foldr (((GHC.Base..) c) (\x'3 -> (((GHC.Num.*) x'3) 2)))) n) xs)))))), (((GHC.Num.+) x'2) 1) #))))

_6(# ds'9, ds'10 #) = (let sh = (r(# ds'9, ds'10 #)) in (((GHC.Num.+) (((GHC.Num.*) sh) sh)) (GHC.Num.fromInteger 1)))

slt1 = (\ls -> (GHC.Base.build (\c' -> (\n' -> (((GHC.Base.foldr (((GHC.Base..) (((GHC.Base..) c') (\c'2 -> 0))) (\c'3 -> 0))) n') ls)))))

t'ls = (((:) 1) (((:) 2) (((:) 3) (((:) 4) []))))

t0 = (\ds'11 -> (case ds'11 of {(:) arg0'4 arg1' -> (case arg1' of {(:) arg0'5 arg1'2 -> (case arg1'2 of {(:) arg0'6 arg1'3 -> (case arg1'3 of {(:) arg0'7 arg1'4 -> (case arg1'4 of {[] -> (_7(# arg0'4, arg0'5, arg0'6, arg0'7 #)); (_) -> _8}); (_) -> _8}); (_) -> _8}); (_) -> _8}); (_) -> _8}))

_8 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/PatMat.hs:51:1-22|function t0"#)

_7(# ds'12, ds'13, ds'14, ds'15 #) = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) ds'12) ds'13)) ds'14)) ds'15)

t0'0 = (_7(# 1, 2, 3, 4 #))

t1 = (\ds'16 -> (case ds'16 of {(:) arg0'8 arg1'5 -> (case arg1'5 of {(:) arg0'9 arg1'6 -> (_9(# arg1'6, (_10(# (case arg1'6 of (:) _ arg -> arg), (_11(# (case (case arg1'6 of (:) _ arg -> arg) of (:) _ arg -> arg), arg0'8, arg0'9, (case arg1'6 of (:) arg _ -> arg), (case (case arg1'6 of (:) _ arg -> arg) of (:) arg _ -> arg) #)) #)) #)); (_) -> _12}); (_) -> _12}))

_12 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/PatMat.hs:55:1-22|function t1"#)

_9(# ds'17, ds'18 #) = (case ds'17 of {(:) arg0'10 arg1'7 -> ds'18; (_) -> _12})

_10(# ds'19, ds'20 #) = (case ds'19 of {(:) arg0'11 arg1'8 -> ds'20; (_) -> _12})

_11(# ds'21, ds'22, ds'23, ds'24, ds'25 #) = (case ds'21 of {[] -> (_13(# ds'22, ds'23, ds'24, ds'25 #)); (_) -> _12})

t1'0 = (_13(# 1, 2, 3, 4 #))

_13(# ds'26, ds'27, ds'28, ds'29 #) = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) ds'26) ds'27)) ds'28)) ds'29)

t1'1 = (\xs' -> let sh' = (case xs' of (:) _ arg -> arg) in (_9(# xs', (_10(# sh', (_11(# (case sh' of (:) _ arg -> arg), (GHC.Num.fromInteger 5), (GHC.Num.fromInteger 6), (case xs' of (:) arg _ -> arg), (case sh' of (:) arg _ -> arg) #)) #)) #)))

t2 = (\ds'30 -> (case ds'30 of {(:) arg0'12 arg1'9 -> (case arg1'9 of {(:) arg0'13 arg1'10 -> (case arg1'10 of {[] -> (_14(# arg0'12, arg0'13 #)); (_) -> _15}); (_) -> _15}); (_) -> _15}))

_15 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/PatMat.hs:60:1-29|function t2"#)

_14(# ds'31, ds'32 #) = (((:) (((GHC.Num.+) ds'31) ds'32)) (_14(# ds'32, ds'31 #)))

t3 = (\ds'33 -> (case ds'33 of {(:) arg0'14 arg1'11 -> (case arg1'11 of {(:) arg0'15 arg1'12 -> (case arg1'12 of {[] -> (_16(# arg0'14, arg0'15 #)); (_) -> _17}); (_) -> _17}); (_) -> _17}))

_17 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/PatMat.hs:62:1-29|function t3"#)

_16(# ds'34, ds'35 #) = (((:) (((GHC.Num.+) ds'34) ds'35)) (_16(# ds'35, ds'34 #)))

t3'0 = (_16(# 11, 22 #))

usum = (\ds'36 -> (_18(# ds'36 #)))

_18(# ds'37 #) = (case ds'37 of {[] -> _19; (:) arg0'16 arg1'13 -> (((GHC.Num.+) arg0'16) (_18(# arg1'13 #)))})

usum'0 = _19

_19 = (GHC.Num.fromInteger 0)

r(# ds'38, ds'39 #) = (ds'38 (GHC.Base.build (\c'4 -> (\n'2 -> (((GHC.Base.foldr (((GHC.Base..) c'4) (\c'5 -> (((GHC.Num.+) c'5) ds'39)))) n'2) [])))))
