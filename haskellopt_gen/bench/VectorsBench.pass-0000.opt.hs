-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 5410; Boxes: 992; Branches: 465
-- Apps: 1247; Lams: 75; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Main (prod_12,test_12,test_8,prod_20,test_3,prod_5,test_4,prod_1,test_7,prod_6,test_18,main,prod_16,test_5,prod_2,prod_7,prod_18,test_16,prod_3,test_1,test_6,test_10,prod_14,test_2,test_9,prod_8,test_20,prod_9,prod_4,test_14,prod_10) where

import Control.Exception.Base
import Criterion.Main
import Criterion.Measurement.Types
import Data.Foldable
import GHC.Base
import GHC.CString
import GHC.Enum
import GHC.Num
import GHC.Prim
import GHC.TopHandler

main = (GHC.TopHandler.runMainIO (Criterion.Main.defaultMain (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "1"#))) ((Criterion.Measurement.Types.whnf test_1) 1000))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "2"#))) ((Criterion.Measurement.Types.whnf test_2) 1000))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "3"#))) ((Criterion.Measurement.Types.whnf test_3) 1000))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "4"#))) ((Criterion.Measurement.Types.whnf test_4) 1000))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "5"#))) ((Criterion.Measurement.Types.whnf test_5) 1000))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "6"#))) ((Criterion.Measurement.Types.whnf test_6) 1000))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "7"#))) ((Criterion.Measurement.Types.whnf test_7) 1000))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "8"#))) ((Criterion.Measurement.Types.whnf test_8) 1000))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "9"#))) ((Criterion.Measurement.Types.whnf test_9) 1000))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "10"#))) ((Criterion.Measurement.Types.whnf test_10) 1000))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "12"#))) ((Criterion.Measurement.Types.whnf test_12) 1000))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "14"#))) ((Criterion.Measurement.Types.whnf test_14) 1000))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "16"#))) ((Criterion.Measurement.Types.whnf test_16) 1000))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "18"#))) ((Criterion.Measurement.Types.whnf test_18) 1000))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "20"#))) ((Criterion.Measurement.Types.whnf test_20) 1000))) [])))))))))))))))))

test_1 = (\n -> (Data.Foldable.sum (GHC.Base.build (\c -> (\n' -> let sh = (GHC.Num.fromInteger 0) in (((GHC.Base.foldr (((GHC.Base..) c) (\i -> (_0 (((GHC.Num.+) i) sh))))) n') ((GHC.Enum.enumFromTo sh) n)))))))

test_2 = (\n'2 -> (Data.Foldable.sum (GHC.Base.build (\c' -> (\n'3 -> let sh'2 = (GHC.Num.fromInteger 0) in (((GHC.Base.foldr (((GHC.Base..) c') (\i' -> let sh' = ((GHC.Num.+) i') in (_1 (sh' sh'2) (sh' (GHC.Num.fromInteger 1)))))) n'3) ((GHC.Enum.enumFromTo sh'2) n'2)))))))

test_3 = (\n'4 -> (Data.Foldable.sum (GHC.Base.build (\c'2 -> (\n'5 -> let sh'4 = (GHC.Num.fromInteger 0) in (((GHC.Base.foldr (((GHC.Base..) c'2) (\i'2 -> let sh'3 = ((GHC.Num.+) i'2) in (_2 (sh'3 sh'4) (sh'3 (GHC.Num.fromInteger 1)) (sh'3 (GHC.Num.fromInteger 2)))))) n'5) ((GHC.Enum.enumFromTo sh'4) n'4)))))))

test_4 = (\n'6 -> (Data.Foldable.sum (GHC.Base.build (\c'3 -> (\n'7 -> let sh'6 = (GHC.Num.fromInteger 0) in (((GHC.Base.foldr (((GHC.Base..) c'3) (\i'3 -> let sh'5 = ((GHC.Num.+) i'3) in (_3 (sh'5 sh'6) (sh'5 (GHC.Num.fromInteger 1)) (sh'5 (GHC.Num.fromInteger 2)) (sh'5 (GHC.Num.fromInteger 3)))))) n'7) ((GHC.Enum.enumFromTo sh'6) n'6)))))))

test_5 = (\n'8 -> (Data.Foldable.sum (GHC.Base.build (\c'4 -> (\n'9 -> let sh'8 = (GHC.Num.fromInteger 0) in (((GHC.Base.foldr (((GHC.Base..) c'4) (\i'4 -> let sh'7 = ((GHC.Num.+) i'4) in (_4 (sh'7 sh'8) (sh'7 (GHC.Num.fromInteger 1)) (sh'7 (GHC.Num.fromInteger 2)) (sh'7 (GHC.Num.fromInteger 3)) (sh'7 (GHC.Num.fromInteger 4)))))) n'9) ((GHC.Enum.enumFromTo sh'8) n'8)))))))

test_6 = (\n'10 -> (Data.Foldable.sum (GHC.Base.build (\c'5 -> (\n'11 -> let sh'10 = (GHC.Num.fromInteger 0) in (((GHC.Base.foldr (((GHC.Base..) c'5) (\i'5 -> let sh'9 = ((GHC.Num.+) i'5) in (_5 (sh'9 sh'10) (sh'9 (GHC.Num.fromInteger 1)) (sh'9 (GHC.Num.fromInteger 2)) (sh'9 (GHC.Num.fromInteger 3)) (sh'9 (GHC.Num.fromInteger 4)) (sh'9 (GHC.Num.fromInteger 5)))))) n'11) ((GHC.Enum.enumFromTo sh'10) n'10)))))))

test_7 = (\n'12 -> (Data.Foldable.sum (GHC.Base.build (\c'6 -> (\n'13 -> let sh'12 = (GHC.Num.fromInteger 0) in (((GHC.Base.foldr (((GHC.Base..) c'6) (\i'6 -> let sh'11 = ((GHC.Num.+) i'6) in (_6 (sh'11 sh'12) (sh'11 (GHC.Num.fromInteger 1)) (sh'11 (GHC.Num.fromInteger 2)) (sh'11 (GHC.Num.fromInteger 3)) (sh'11 (GHC.Num.fromInteger 4)) (sh'11 (GHC.Num.fromInteger 5)) (sh'11 (GHC.Num.fromInteger 6)))))) n'13) ((GHC.Enum.enumFromTo sh'12) n'12)))))))

test_8 = (\n'14 -> (Data.Foldable.sum (GHC.Base.build (\c'7 -> (\n'15 -> let sh'14 = (GHC.Num.fromInteger 0) in (((GHC.Base.foldr (((GHC.Base..) c'7) (\i'7 -> let sh'13 = ((GHC.Num.+) i'7) in (_7 (sh'13 sh'14) (sh'13 (GHC.Num.fromInteger 1)) (sh'13 (GHC.Num.fromInteger 2)) (sh'13 (GHC.Num.fromInteger 3)) (sh'13 (GHC.Num.fromInteger 4)) (sh'13 (GHC.Num.fromInteger 5)) (sh'13 (GHC.Num.fromInteger 6)) (sh'13 (GHC.Num.fromInteger 7)))))) n'15) ((GHC.Enum.enumFromTo sh'14) n'14)))))))

test_9 = (\n'16 -> (Data.Foldable.sum (GHC.Base.build (\c'8 -> (\n'17 -> let sh'16 = (GHC.Num.fromInteger 0) in (((GHC.Base.foldr (((GHC.Base..) c'8) (\i'8 -> let sh'15 = ((GHC.Num.+) i'8) in (_8 (sh'15 sh'16) (sh'15 (GHC.Num.fromInteger 1)) (sh'15 (GHC.Num.fromInteger 2)) (sh'15 (GHC.Num.fromInteger 3)) (sh'15 (GHC.Num.fromInteger 4)) (sh'15 (GHC.Num.fromInteger 5)) (sh'15 (GHC.Num.fromInteger 6)) (sh'15 (GHC.Num.fromInteger 7)) (sh'15 (GHC.Num.fromInteger 8)))))) n'17) ((GHC.Enum.enumFromTo sh'16) n'16)))))))

test_10 = (\n'18 -> (Data.Foldable.sum (GHC.Base.build (\c'9 -> (\n'19 -> let sh'18 = (GHC.Num.fromInteger 0) in (((GHC.Base.foldr (((GHC.Base..) c'9) (\i'9 -> let sh'17 = ((GHC.Num.+) i'9) in (_9 (sh'17 sh'18) (sh'17 (GHC.Num.fromInteger 1)) (sh'17 (GHC.Num.fromInteger 2)) (sh'17 (GHC.Num.fromInteger 3)) (sh'17 (GHC.Num.fromInteger 4)) (sh'17 (GHC.Num.fromInteger 5)) (sh'17 (GHC.Num.fromInteger 6)) (sh'17 (GHC.Num.fromInteger 7)) (sh'17 (GHC.Num.fromInteger 8)) (sh'17 (GHC.Num.fromInteger 9)))))) n'19) ((GHC.Enum.enumFromTo sh'18) n'18)))))))

test_12 = (\n'20 -> (Data.Foldable.sum (GHC.Base.build (\c'10 -> (\n'21 -> let sh'20 = (GHC.Num.fromInteger 0) in (((GHC.Base.foldr (((GHC.Base..) c'10) (\i'10 -> let sh'19 = ((GHC.Num.+) i'10) in (_10 (sh'19 sh'20) (sh'19 (GHC.Num.fromInteger 1)) (sh'19 (GHC.Num.fromInteger 2)) (sh'19 (GHC.Num.fromInteger 3)) (sh'19 (GHC.Num.fromInteger 4)) (sh'19 (GHC.Num.fromInteger 5)) (sh'19 (GHC.Num.fromInteger 6)) (sh'19 (GHC.Num.fromInteger 7)) (sh'19 (GHC.Num.fromInteger 8)) (sh'19 (GHC.Num.fromInteger 9)) (sh'19 (GHC.Num.fromInteger 10)) (sh'19 (GHC.Num.fromInteger 11)))))) n'21) ((GHC.Enum.enumFromTo sh'20) n'20)))))))

test_14 = (\n'22 -> (Data.Foldable.sum (GHC.Base.build (\c'11 -> (\n'23 -> let sh'22 = (GHC.Num.fromInteger 0) in (((GHC.Base.foldr (((GHC.Base..) c'11) (\i'11 -> let sh'21 = ((GHC.Num.+) i'11) in (_11 (sh'21 sh'22) (sh'21 (GHC.Num.fromInteger 1)) (sh'21 (GHC.Num.fromInteger 2)) (sh'21 (GHC.Num.fromInteger 3)) (sh'21 (GHC.Num.fromInteger 4)) (sh'21 (GHC.Num.fromInteger 5)) (sh'21 (GHC.Num.fromInteger 6)) (sh'21 (GHC.Num.fromInteger 7)) (sh'21 (GHC.Num.fromInteger 8)) (sh'21 (GHC.Num.fromInteger 9)) (sh'21 (GHC.Num.fromInteger 10)) (sh'21 (GHC.Num.fromInteger 11)) (sh'21 (GHC.Num.fromInteger 12)) (sh'21 (GHC.Num.fromInteger 13)))))) n'23) ((GHC.Enum.enumFromTo sh'22) n'22)))))))

test_16 = (\n'24 -> (Data.Foldable.sum (GHC.Base.build (\c'12 -> (\n'25 -> let sh'24 = (GHC.Num.fromInteger 0) in (((GHC.Base.foldr (((GHC.Base..) c'12) (\i'12 -> let sh'23 = ((GHC.Num.+) i'12) in (_12 (sh'23 sh'24) (sh'23 (GHC.Num.fromInteger 1)) (sh'23 (GHC.Num.fromInteger 2)) (sh'23 (GHC.Num.fromInteger 3)) (sh'23 (GHC.Num.fromInteger 4)) (sh'23 (GHC.Num.fromInteger 5)) (sh'23 (GHC.Num.fromInteger 6)) (sh'23 (GHC.Num.fromInteger 7)) (sh'23 (GHC.Num.fromInteger 8)) (sh'23 (GHC.Num.fromInteger 9)) (sh'23 (GHC.Num.fromInteger 10)) (sh'23 (GHC.Num.fromInteger 11)) (sh'23 (GHC.Num.fromInteger 12)) (sh'23 (GHC.Num.fromInteger 13)) (sh'23 (GHC.Num.fromInteger 14)) (sh'23 (GHC.Num.fromInteger 15)))))) n'25) ((GHC.Enum.enumFromTo sh'24) n'24)))))))

test_18 = (\n'26 -> (Data.Foldable.sum (GHC.Base.build (\c'13 -> (\n'27 -> let sh'26 = (GHC.Num.fromInteger 0) in (((GHC.Base.foldr (((GHC.Base..) c'13) (\i'13 -> let sh'25 = ((GHC.Num.+) i'13) in (_13 (sh'25 sh'26) (sh'25 (GHC.Num.fromInteger 1)) (sh'25 (GHC.Num.fromInteger 2)) (sh'25 (GHC.Num.fromInteger 3)) (sh'25 (GHC.Num.fromInteger 4)) (sh'25 (GHC.Num.fromInteger 5)) (sh'25 (GHC.Num.fromInteger 6)) (sh'25 (GHC.Num.fromInteger 7)) (sh'25 (GHC.Num.fromInteger 8)) (sh'25 (GHC.Num.fromInteger 9)) (sh'25 (GHC.Num.fromInteger 10)) (sh'25 (GHC.Num.fromInteger 11)) (sh'25 (GHC.Num.fromInteger 12)) (sh'25 (GHC.Num.fromInteger 13)) (sh'25 (GHC.Num.fromInteger 14)) (sh'25 (GHC.Num.fromInteger 15)) (sh'25 (GHC.Num.fromInteger 16)) (sh'25 (GHC.Num.fromInteger 17)))))) n'27) ((GHC.Enum.enumFromTo sh'26) n'26)))))))

test_20 = (\n'28 -> (Data.Foldable.sum (GHC.Base.build (\c'14 -> (\n'29 -> let sh'28 = (GHC.Num.fromInteger 0) in (((GHC.Base.foldr (((GHC.Base..) c'14) (\i'14 -> let sh'27 = ((GHC.Num.+) i'14) in (_14 (sh'27 sh'28) (sh'27 (GHC.Num.fromInteger 1)) (sh'27 (GHC.Num.fromInteger 2)) (sh'27 (GHC.Num.fromInteger 3)) (sh'27 (GHC.Num.fromInteger 4)) (sh'27 (GHC.Num.fromInteger 5)) (sh'27 (GHC.Num.fromInteger 6)) (sh'27 (GHC.Num.fromInteger 7)) (sh'27 (GHC.Num.fromInteger 8)) (sh'27 (GHC.Num.fromInteger 9)) (sh'27 (GHC.Num.fromInteger 10)) (sh'27 (GHC.Num.fromInteger 11)) (sh'27 (GHC.Num.fromInteger 12)) (sh'27 (GHC.Num.fromInteger 13)) (sh'27 (GHC.Num.fromInteger 14)) (sh'27 (GHC.Num.fromInteger 15)) (sh'27 (GHC.Num.fromInteger 16)) (sh'27 (GHC.Num.fromInteger 17)) (sh'27 (GHC.Num.fromInteger 18)) (sh'27 (GHC.Num.fromInteger 19)))))) n'29) ((GHC.Enum.enumFromTo sh'28) n'28)))))))

prod_1 = (\ds -> (case ds of {(:) arg0 arg1 -> (case arg1 of {[] -> (_0 arg0); (_) -> _15}); (_) -> _15}))

_15 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/VectorsBench.hs:7:1-20|function prod_1"#)

_0 ds' = (((GHC.Num.+) (GHC.Num.fromInteger 0)) ds')

prod_10 = (\ds'2 -> (case ds'2 of {(:) arg0' arg1' -> (case arg1' of {(:) arg0'2 arg1'2 -> (case arg1'2 of {(:) arg0'3 arg1'3 -> (case arg1'3 of {(:) arg0'4 arg1'4 -> (case arg1'4 of {(:) arg0'5 arg1'5 -> (case arg1'5 of {(:) arg0'6 arg1'6 -> (case arg1'6 of {(:) arg0'7 arg1'7 -> (case arg1'7 of {(:) arg0'8 arg1'8 -> (case arg1'8 of {(:) arg0'9 arg1'9 -> (case arg1'9 of {(:) arg0'10 arg1'10 -> (case arg1'10 of {[] -> (_9 arg0' arg0'2 arg0'3 arg0'4 arg0'5 arg0'6 arg0'7 arg0'8 arg0'9 arg0'10); (_) -> _16}); (_) -> _16}); (_) -> _16}); (_) -> _16}); (_) -> _16}); (_) -> _16}); (_) -> _16}); (_) -> _16}); (_) -> _16}); (_) -> _16}); (_) -> _16}))

_16 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/VectorsBench.hs:34:1-93|function prod_10"#)

_9 ds'3 ds'4 ds'5 ds'6 ds'7 ds'8 ds'9 ds'10 ds'11 ds'12 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (GHC.Num.fromInteger 0)) ds'3)) ds'4)) ds'5)) ds'6)) ds'7)) ds'8)) ds'9)) ds'10)) ds'11)) ds'12)

prod_12 = (\ds'13 -> (case ds'13 of {(:) arg0'11 arg1'11 -> (case arg1'11 of {(:) arg0'12 arg1'12 -> (case arg1'12 of {(:) arg0'13 arg1'13 -> (case arg1'13 of {(:) arg0'14 arg1'14 -> (case arg1'14 of {(:) arg0'15 arg1'15 -> (case arg1'15 of {(:) arg0'16 arg1'16 -> (case arg1'16 of {(:) arg0'17 arg1'17 -> (case arg1'17 of {(:) arg0'18 arg1'18 -> (case arg1'18 of {(:) arg0'19 arg1'19 -> (case arg1'19 of {(:) arg0'20 arg1'20 -> (case arg1'20 of {(:) arg0'21 arg1'21 -> (case arg1'21 of {(:) arg0'22 arg1'22 -> (case arg1'22 of {[] -> (_10 arg0'11 arg0'12 arg0'13 arg0'14 arg0'15 arg0'16 arg0'17 arg0'18 arg0'19 arg0'20 arg0'21 arg0'22); (_) -> _17}); (_) -> _17}); (_) -> _17}); (_) -> _17}); (_) -> _17}); (_) -> _17}); (_) -> _17}); (_) -> _17}); (_) -> _17}); (_) -> _17}); (_) -> _17}); (_) -> _17}); (_) -> _17}))

_17 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/VectorsBench.hs:37:1-113|function prod_12"#)

_10 ds'14 ds'15 ds'16 ds'17 ds'18 ds'19 ds'20 ds'21 ds'22 ds'23 ds'24 ds'25 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (GHC.Num.fromInteger 0)) ds'14)) ds'15)) ds'16)) ds'17)) ds'18)) ds'19)) ds'20)) ds'21)) ds'22)) ds'23)) ds'24)) ds'25)

prod_14 = (\ds'26 -> (case ds'26 of {(:) arg0'23 arg1'23 -> (case arg1'23 of {(:) arg0'24 arg1'24 -> (case arg1'24 of {(:) arg0'25 arg1'25 -> (case arg1'25 of {(:) arg0'26 arg1'26 -> (case arg1'26 of {(:) arg0'27 arg1'27 -> (case arg1'27 of {(:) arg0'28 arg1'28 -> (case arg1'28 of {(:) arg0'29 arg1'29 -> (case arg1'29 of {(:) arg0'30 arg1'30 -> (case arg1'30 of {(:) arg0'31 arg1'31 -> (case arg1'31 of {(:) arg0'32 arg1'32 -> (case arg1'32 of {(:) arg0'33 arg1'33 -> (case arg1'33 of {(:) arg0'34 arg1'34 -> (case arg1'34 of {(:) arg0'35 arg1'35 -> (case arg1'35 of {(:) arg0'36 arg1'36 -> (case arg1'36 of {[] -> (_11 arg0'23 arg0'24 arg0'25 arg0'26 arg0'27 arg0'28 arg0'29 arg0'30 arg0'31 arg0'32 arg0'33 arg0'34 arg0'35 arg0'36); (_) -> _18}); (_) -> _18}); (_) -> _18}); (_) -> _18}); (_) -> _18}); (_) -> _18}); (_) -> _18}); (_) -> _18}); (_) -> _18}); (_) -> _18}); (_) -> _18}); (_) -> _18}); (_) -> _18}); (_) -> _18}); (_) -> _18}))

_18 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/VectorsBench.hs:40:1-133|function prod_14"#)

_11 ds'27 ds'28 ds'29 ds'30 ds'31 ds'32 ds'33 ds'34 ds'35 ds'36 ds'37 ds'38 ds'39 ds'40 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (GHC.Num.fromInteger 0)) ds'27)) ds'28)) ds'29)) ds'30)) ds'31)) ds'32)) ds'33)) ds'34)) ds'35)) ds'36)) ds'37)) ds'38)) ds'39)) ds'40)

prod_16 = (\ds'41 -> (case ds'41 of {(:) arg0'37 arg1'37 -> (case arg1'37 of {(:) arg0'38 arg1'38 -> (case arg1'38 of {(:) arg0'39 arg1'39 -> (case arg1'39 of {(:) arg0'40 arg1'40 -> (case arg1'40 of {(:) arg0'41 arg1'41 -> (case arg1'41 of {(:) arg0'42 arg1'42 -> (case arg1'42 of {(:) arg0'43 arg1'43 -> (case arg1'43 of {(:) arg0'44 arg1'44 -> (case arg1'44 of {(:) arg0'45 arg1'45 -> (case arg1'45 of {(:) arg0'46 arg1'46 -> (case arg1'46 of {(:) arg0'47 arg1'47 -> (case arg1'47 of {(:) arg0'48 arg1'48 -> (case arg1'48 of {(:) arg0'49 arg1'49 -> (case arg1'49 of {(:) arg0'50 arg1'50 -> (case arg1'50 of {(:) arg0'51 arg1'51 -> (case arg1'51 of {(:) arg0'52 arg1'52 -> (case arg1'52 of {[] -> (_12 arg0'37 arg0'38 arg0'39 arg0'40 arg0'41 arg0'42 arg0'43 arg0'44 arg0'45 arg0'46 arg0'47 arg0'48 arg0'49 arg0'50 arg0'51 arg0'52); (_) -> _19}); (_) -> _19}); (_) -> _19}); (_) -> _19}); (_) -> _19}); (_) -> _19}); (_) -> _19}); (_) -> _19}); (_) -> _19}); (_) -> _19}); (_) -> _19}); (_) -> _19}); (_) -> _19}); (_) -> _19}); (_) -> _19}); (_) -> _19}); (_) -> _19}))

_19 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/VectorsBench.hs:43:1-153|function prod_16"#)

_12 ds'42 ds'43 ds'44 ds'45 ds'46 ds'47 ds'48 ds'49 ds'50 ds'51 ds'52 ds'53 ds'54 ds'55 ds'56 ds'57 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (GHC.Num.fromInteger 0)) ds'42)) ds'43)) ds'44)) ds'45)) ds'46)) ds'47)) ds'48)) ds'49)) ds'50)) ds'51)) ds'52)) ds'53)) ds'54)) ds'55)) ds'56)) ds'57)

prod_18 = (\ds'58 -> (case ds'58 of {(:) arg0'53 arg1'53 -> (case arg1'53 of {(:) arg0'54 arg1'54 -> (case arg1'54 of {(:) arg0'55 arg1'55 -> (case arg1'55 of {(:) arg0'56 arg1'56 -> (case arg1'56 of {(:) arg0'57 arg1'57 -> (case arg1'57 of {(:) arg0'58 arg1'58 -> (case arg1'58 of {(:) arg0'59 arg1'59 -> (case arg1'59 of {(:) arg0'60 arg1'60 -> (case arg1'60 of {(:) arg0'61 arg1'61 -> (case arg1'61 of {(:) arg0'62 arg1'62 -> (case arg1'62 of {(:) arg0'63 arg1'63 -> (case arg1'63 of {(:) arg0'64 arg1'64 -> (case arg1'64 of {(:) arg0'65 arg1'65 -> (case arg1'65 of {(:) arg0'66 arg1'66 -> (case arg1'66 of {(:) arg0'67 arg1'67 -> (case arg1'67 of {(:) arg0'68 arg1'68 -> (case arg1'68 of {(:) arg0'69 arg1'69 -> (case arg1'69 of {(:) arg0'70 arg1'70 -> (case arg1'70 of {[] -> (_13 arg0'53 arg0'54 arg0'55 arg0'56 arg0'57 arg0'58 arg0'59 arg0'60 arg0'61 arg0'62 arg0'63 arg0'64 arg0'65 arg0'66 arg0'67 arg0'68 arg0'69 arg0'70); (_) -> _20}); (_) -> _20}); (_) -> _20}); (_) -> _20}); (_) -> _20}); (_) -> _20}); (_) -> _20}); (_) -> _20}); (_) -> _20}); (_) -> _20}); (_) -> _20}); (_) -> _20}); (_) -> _20}); (_) -> _20}); (_) -> _20}); (_) -> _20}); (_) -> _20}); (_) -> _20}); (_) -> _20}))

_20 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/VectorsBench.hs:46:1-173|function prod_18"#)

_13 ds'59 ds'60 ds'61 ds'62 ds'63 ds'64 ds'65 ds'66 ds'67 ds'68 ds'69 ds'70 ds'71 ds'72 ds'73 ds'74 ds'75 ds'76 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (GHC.Num.fromInteger 0)) ds'59)) ds'60)) ds'61)) ds'62)) ds'63)) ds'64)) ds'65)) ds'66)) ds'67)) ds'68)) ds'69)) ds'70)) ds'71)) ds'72)) ds'73)) ds'74)) ds'75)) ds'76)

prod_2 = (\ds'77 -> (case ds'77 of {(:) arg0'71 arg1'71 -> (case arg1'71 of {(:) arg0'72 arg1'72 -> (case arg1'72 of {[] -> (_1 arg0'71 arg0'72); (_) -> _21}); (_) -> _21}); (_) -> _21}))

_21 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/VectorsBench.hs:10:1-28|function prod_2"#)

_1 ds'78 ds'79 = (((GHC.Num.+) (((GHC.Num.+) (GHC.Num.fromInteger 0)) ds'78)) ds'79)

prod_20 = (\ds'80 -> (case ds'80 of {(:) arg0'73 arg1'73 -> (case arg1'73 of {(:) arg0'74 arg1'74 -> (case arg1'74 of {(:) arg0'75 arg1'75 -> (case arg1'75 of {(:) arg0'76 arg1'76 -> (case arg1'76 of {(:) arg0'77 arg1'77 -> (case arg1'77 of {(:) arg0'78 arg1'78 -> (case arg1'78 of {(:) arg0'79 arg1'79 -> (case arg1'79 of {(:) arg0'80 arg1'80 -> (case arg1'80 of {(:) arg0'81 arg1'81 -> (case arg1'81 of {(:) arg0'82 arg1'82 -> (case arg1'82 of {(:) arg0'83 arg1'83 -> (case arg1'83 of {(:) arg0'84 arg1'84 -> (case arg1'84 of {(:) arg0'85 arg1'85 -> (case arg1'85 of {(:) arg0'86 arg1'86 -> (case arg1'86 of {(:) arg0'87 arg1'87 -> (case arg1'87 of {(:) arg0'88 arg1'88 -> (case arg1'88 of {(:) arg0'89 arg1'89 -> (case arg1'89 of {(:) arg0'90 arg1'90 -> (case arg1'90 of {(:) arg0'91 arg1'91 -> (case arg1'91 of {(:) arg0'92 arg1'92 -> (case arg1'92 of {[] -> (_14 arg0'73 arg0'74 arg0'75 arg0'76 arg0'77 arg0'78 arg0'79 arg0'80 arg0'81 arg0'82 arg0'83 arg0'84 arg0'85 arg0'86 arg0'87 arg0'88 arg0'89 arg0'90 arg0'91 arg0'92); (_) -> _22}); (_) -> _22}); (_) -> _22}); (_) -> _22}); (_) -> _22}); (_) -> _22}); (_) -> _22}); (_) -> _22}); (_) -> _22}); (_) -> _22}); (_) -> _22}); (_) -> _22}); (_) -> _22}); (_) -> _22}); (_) -> _22}); (_) -> _22}); (_) -> _22}); (_) -> _22}); (_) -> _22}); (_) -> _22}); (_) -> _22}))

_22 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/VectorsBench.hs:49:1-193|function prod_20"#)

_14 ds'81 ds'82 ds'83 ds'84 ds'85 ds'86 ds'87 ds'88 ds'89 ds'90 ds'91 ds'92 ds'93 ds'94 ds'95 ds'96 ds'97 ds'98 ds'99 ds'100 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (GHC.Num.fromInteger 0)) ds'81)) ds'82)) ds'83)) ds'84)) ds'85)) ds'86)) ds'87)) ds'88)) ds'89)) ds'90)) ds'91)) ds'92)) ds'93)) ds'94)) ds'95)) ds'96)) ds'97)) ds'98)) ds'99)) ds'100)

prod_3 = (\ds'101 -> (case ds'101 of {(:) arg0'93 arg1'93 -> (case arg1'93 of {(:) arg0'94 arg1'94 -> (case arg1'94 of {(:) arg0'95 arg1'95 -> (case arg1'95 of {[] -> (_2 arg0'93 arg0'94 arg0'95); (_) -> _23}); (_) -> _23}); (_) -> _23}); (_) -> _23}))

_23 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/VectorsBench.hs:13:1-36|function prod_3"#)

_2 ds'102 ds'103 ds'104 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (GHC.Num.fromInteger 0)) ds'102)) ds'103)) ds'104)

prod_4 = (\ds'105 -> (case ds'105 of {(:) arg0'96 arg1'96 -> (case arg1'96 of {(:) arg0'97 arg1'97 -> (case arg1'97 of {(:) arg0'98 arg1'98 -> (case arg1'98 of {(:) arg0'99 arg1'99 -> (case arg1'99 of {[] -> (_3 arg0'96 arg0'97 arg0'98 arg0'99); (_) -> _24}); (_) -> _24}); (_) -> _24}); (_) -> _24}); (_) -> _24}))

_24 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/VectorsBench.hs:16:1-44|function prod_4"#)

_3 ds'106 ds'107 ds'108 ds'109 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (GHC.Num.fromInteger 0)) ds'106)) ds'107)) ds'108)) ds'109)

prod_5 = (\ds'110 -> (case ds'110 of {(:) arg0'100 arg1'100 -> (case arg1'100 of {(:) arg0'101 arg1'101 -> (case arg1'101 of {(:) arg0'102 arg1'102 -> (case arg1'102 of {(:) arg0'103 arg1'103 -> (case arg1'103 of {(:) arg0'104 arg1'104 -> (case arg1'104 of {[] -> (_4 arg0'100 arg0'101 arg0'102 arg0'103 arg0'104); (_) -> _25}); (_) -> _25}); (_) -> _25}); (_) -> _25}); (_) -> _25}); (_) -> _25}))

_25 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/VectorsBench.hs:19:1-52|function prod_5"#)

_4 ds'111 ds'112 ds'113 ds'114 ds'115 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (GHC.Num.fromInteger 0)) ds'111)) ds'112)) ds'113)) ds'114)) ds'115)

prod_6 = (\ds'116 -> (case ds'116 of {(:) arg0'105 arg1'105 -> (case arg1'105 of {(:) arg0'106 arg1'106 -> (case arg1'106 of {(:) arg0'107 arg1'107 -> (case arg1'107 of {(:) arg0'108 arg1'108 -> (case arg1'108 of {(:) arg0'109 arg1'109 -> (case arg1'109 of {(:) arg0'110 arg1'110 -> (case arg1'110 of {[] -> (_5 arg0'105 arg0'106 arg0'107 arg0'108 arg0'109 arg0'110); (_) -> _26}); (_) -> _26}); (_) -> _26}); (_) -> _26}); (_) -> _26}); (_) -> _26}); (_) -> _26}))

_26 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/VectorsBench.hs:22:1-60|function prod_6"#)

_5 ds'117 ds'118 ds'119 ds'120 ds'121 ds'122 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (GHC.Num.fromInteger 0)) ds'117)) ds'118)) ds'119)) ds'120)) ds'121)) ds'122)

prod_7 = (\ds'123 -> (case ds'123 of {(:) arg0'111 arg1'111 -> (case arg1'111 of {(:) arg0'112 arg1'112 -> (case arg1'112 of {(:) arg0'113 arg1'113 -> (case arg1'113 of {(:) arg0'114 arg1'114 -> (case arg1'114 of {(:) arg0'115 arg1'115 -> (case arg1'115 of {(:) arg0'116 arg1'116 -> (case arg1'116 of {(:) arg0'117 arg1'117 -> (case arg1'117 of {[] -> (_6 arg0'111 arg0'112 arg0'113 arg0'114 arg0'115 arg0'116 arg0'117); (_) -> _27}); (_) -> _27}); (_) -> _27}); (_) -> _27}); (_) -> _27}); (_) -> _27}); (_) -> _27}); (_) -> _27}))

_27 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/VectorsBench.hs:25:1-68|function prod_7"#)

_6 ds'124 ds'125 ds'126 ds'127 ds'128 ds'129 ds'130 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (GHC.Num.fromInteger 0)) ds'124)) ds'125)) ds'126)) ds'127)) ds'128)) ds'129)) ds'130)

prod_8 = (\ds'131 -> (case ds'131 of {(:) arg0'118 arg1'118 -> (case arg1'118 of {(:) arg0'119 arg1'119 -> (case arg1'119 of {(:) arg0'120 arg1'120 -> (case arg1'120 of {(:) arg0'121 arg1'121 -> (case arg1'121 of {(:) arg0'122 arg1'122 -> (case arg1'122 of {(:) arg0'123 arg1'123 -> (case arg1'123 of {(:) arg0'124 arg1'124 -> (case arg1'124 of {(:) arg0'125 arg1'125 -> (case arg1'125 of {[] -> (_7 arg0'118 arg0'119 arg0'120 arg0'121 arg0'122 arg0'123 arg0'124 arg0'125); (_) -> _28}); (_) -> _28}); (_) -> _28}); (_) -> _28}); (_) -> _28}); (_) -> _28}); (_) -> _28}); (_) -> _28}); (_) -> _28}))

_28 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/VectorsBench.hs:28:1-76|function prod_8"#)

_7 ds'132 ds'133 ds'134 ds'135 ds'136 ds'137 ds'138 ds'139 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (GHC.Num.fromInteger 0)) ds'132)) ds'133)) ds'134)) ds'135)) ds'136)) ds'137)) ds'138)) ds'139)

prod_9 = (\ds'140 -> (case ds'140 of {(:) arg0'126 arg1'126 -> (case arg1'126 of {(:) arg0'127 arg1'127 -> (case arg1'127 of {(:) arg0'128 arg1'128 -> (case arg1'128 of {(:) arg0'129 arg1'129 -> (case arg1'129 of {(:) arg0'130 arg1'130 -> (case arg1'130 of {(:) arg0'131 arg1'131 -> (case arg1'131 of {(:) arg0'132 arg1'132 -> (case arg1'132 of {(:) arg0'133 arg1'133 -> (case arg1'133 of {(:) arg0'134 arg1'134 -> (case arg1'134 of {[] -> (_8 arg0'126 arg0'127 arg0'128 arg0'129 arg0'130 arg0'131 arg0'132 arg0'133 arg0'134); (_) -> _29}); (_) -> _29}); (_) -> _29}); (_) -> _29}); (_) -> _29}); (_) -> _29}); (_) -> _29}); (_) -> _29}); (_) -> _29}); (_) -> _29}))

_29 = (Control.Exception.Base.patError "/Volumes/Macintosh HD/Users/lptk/work/EPFL/DATA/git/alt/Squid-1.0/haskellopt/src/test/haskell/VectorsBench.hs:31:1-84|function prod_9"#)

_8 ds'141 ds'142 ds'143 ds'144 ds'145 ds'146 ds'147 ds'148 ds'149 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (GHC.Num.fromInteger 0)) ds'141)) ds'142)) ds'143)) ds'144)) ds'145)) ds'146)) ds'147)) ds'148)) ds'149)
