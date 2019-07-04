-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 913; Boxes: 167; Branches: 504
-- Apps: 52; Lams: 8; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module PatMatRec (t1'1,t0,t1'2,t0'0,t0_,t2,t2'1,t0'1,t1_,t1'0,t1,t2'0,t2_) where

import Control.Exception.Base
import GHC.Num
import GHC.Prim

t0 = (\ds -> (case ds of {(:) arg0 arg1 -> (case arg1 of {[] -> _0; (_) -> _1}); (_) -> _1}))

_1 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/PatMatRec.hs:5:1-15|function t0"#)

_0 = _0

t0'0 = _0

t0'1 = _1

t0_ = (\ds' -> (case ds' of {(:) arg0' arg1' -> (case arg1' of {[] -> _0; (_) -> _2}); (_) -> _2}))

_2 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/PatMatRec.hs:4:1-16|function t0_"#)

t1 = (\ds'2 -> (_3(# (_4(# (case ds'2 of (:) arg _ -> arg), (case ds'2 of (:) _ arg -> arg) #)), ds'2 #)))

_3(# ds'3, ds'4 #) = (case ds'4 of {(:) arg0'2 arg1'2 -> ds'3; (_) -> _5})

_4(# ds'5, ds'6 #) = (case ds'6 of {[] -> (_6(# ds'5 #)); (_) -> _5})

t1'0 = (\xs -> (_3(# (_4(# (case xs of (:) arg _ -> arg), (case xs of (:) _ arg -> arg) #)), xs #)))

t1'1 = (\x -> (_6(# x #)))

_6(# ds'7 #) = (((:) ds'7) (_6(# (((GHC.Num.+) ds'7) (GHC.Num.fromInteger 1)) #)))

t1'2 = (_6(# 0 #))

t1_ = (\ds'8 -> (case ds'8 of {(:) arg0'3 arg1'3 -> (case arg1'3 of {[] -> (((:) arg0'3) (_6(# (((GHC.Num.+) arg0'3) (GHC.Num.fromInteger 1)) #))); (_) -> _7}); (_) -> _7}))

_7 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/PatMatRec.hs:9:1-22|function t1_"#)

t2 = (\ds'9 -> (case ds'9 of {(:) arg0'4 arg1'4 -> (case arg1'4 of {(:) arg0'5 arg1'5 -> (case arg1'5 of {[] -> (_8(# arg0'4, arg0'5 #)); (_) -> _9}); (_) -> _9}); (_) -> _9}))

_9 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/PatMatRec.hs:16:1-29|function t2"#)

_8(# ds'10, ds'11 #) = (((:) (((GHC.Num.-) ds'10) ds'11)) (_8(# ds'11, ds'10 #)))

t2'0 = (_8(# 0, 1 #))

t2'1 = (((,) (((GHC.Num.-) 0) 1)) (((GHC.Num.-) 1) 0))

t2_ = (\ds'12 -> (case ds'12 of {(:) arg0'6 arg1'6 -> (case arg1'6 of {(:) arg0'7 arg1'7 -> (case arg1'7 of {[] -> (((:) (((GHC.Num.-) arg0'6) arg0'7)) (_8(# arg0'7, arg0'6 #))); (_) -> _10}); (_) -> _10}); (_) -> _10}))

_10 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/PatMatRec.hs:15:1-30|function t2_"#)

_5 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/PatMatRec.hs:10:1-21|function t1"#)
