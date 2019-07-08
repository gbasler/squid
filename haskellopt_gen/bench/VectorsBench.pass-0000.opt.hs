-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 2450; Boxes: 332; Branches: 150
-- Apps: 862; Lams: 50; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Main (prod_12,test_12,test_8,prod_20,test_4,prod_6,test_18,main,prod_16,prod_2,prod_18,test_16,test_6,test_10,prod_14,test_2,prod_8,test_20,prod_4,test_14,prod_10) where

import Criterion.Main
import Criterion.Measurement.Types
import Data.Foldable
import GHC.Base
import GHC.CString
import GHC.Enum
import GHC.Num
import GHC.TopHandler
import GHC.Types

main = (let sh = (GHC.Types.I# 1000#) in (GHC.TopHandler.runMainIO (Criterion.Main.defaultMain (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "2"#))) ((Criterion.Measurement.Types.whnf test_2) sh))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "4"#))) ((Criterion.Measurement.Types.whnf test_4) sh))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "6"#))) ((Criterion.Measurement.Types.whnf test_6) sh))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "8"#))) ((Criterion.Measurement.Types.whnf test_8) sh))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "10"#))) ((Criterion.Measurement.Types.whnf test_10) sh))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "12"#))) ((Criterion.Measurement.Types.whnf test_12) sh))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "14"#))) ((Criterion.Measurement.Types.whnf test_14) sh))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "16"#))) ((Criterion.Measurement.Types.whnf test_16) sh))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "18"#))) ((Criterion.Measurement.Types.whnf test_18) sh))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "20"#))) ((Criterion.Measurement.Types.whnf test_20) sh))) [])))))))))))))

test_2 = (\n -> (Data.Foldable.sum (GHC.Base.build (\c -> (\n' -> let sh'2 = (GHC.Types.I# 0#) in (((GHC.Base.foldr (((GHC.Base..) c) (\i -> let sh' = ((GHC.Num.+) i) in (_0 (sh' sh'2) (sh' (GHC.Types.I# 1#)))))) n') ((GHC.Enum.enumFromTo sh'2) n)))))))

test_4 = (\n'2 -> (Data.Foldable.sum (GHC.Base.build (\c' -> (\n'3 -> let sh'4 = (GHC.Types.I# 0#) in (((GHC.Base.foldr (((GHC.Base..) c') (\i' -> let sh'3 = ((GHC.Num.+) i') in (_1 (sh'3 sh'4) (sh'3 (GHC.Types.I# 1#)) (sh'3 (GHC.Types.I# 2#)) (sh'3 (GHC.Types.I# 3#)))))) n'3) ((GHC.Enum.enumFromTo sh'4) n'2)))))))

test_6 = (\n'4 -> (Data.Foldable.sum (GHC.Base.build (\c'2 -> (\n'5 -> let sh'6 = (GHC.Types.I# 0#) in (((GHC.Base.foldr (((GHC.Base..) c'2) (\i'2 -> let sh'5 = ((GHC.Num.+) i'2) in (_2 (sh'5 sh'6) (sh'5 (GHC.Types.I# 1#)) (sh'5 (GHC.Types.I# 2#)) (sh'5 (GHC.Types.I# 3#)) (sh'5 (GHC.Types.I# 4#)) (sh'5 (GHC.Types.I# 5#)))))) n'5) ((GHC.Enum.enumFromTo sh'6) n'4)))))))

test_8 = (\n'6 -> (Data.Foldable.sum (GHC.Base.build (\c'3 -> (\n'7 -> let sh'8 = (GHC.Types.I# 0#) in (((GHC.Base.foldr (((GHC.Base..) c'3) (\i'3 -> let sh'7 = ((GHC.Num.+) i'3) in (_3 (sh'7 sh'8) (sh'7 (GHC.Types.I# 1#)) (sh'7 (GHC.Types.I# 2#)) (sh'7 (GHC.Types.I# 3#)) (sh'7 (GHC.Types.I# 4#)) (sh'7 (GHC.Types.I# 5#)) (sh'7 (GHC.Types.I# 6#)) (sh'7 (GHC.Types.I# 7#)))))) n'7) ((GHC.Enum.enumFromTo sh'8) n'6)))))))

test_10 = (\n'8 -> (Data.Foldable.sum (GHC.Base.build (\c'4 -> (\n'9 -> let sh'10 = (GHC.Types.I# 0#) in (((GHC.Base.foldr (((GHC.Base..) c'4) (\i'4 -> let sh'9 = ((GHC.Num.+) i'4) in (_4 (sh'9 sh'10) (sh'9 (GHC.Types.I# 1#)) (sh'9 (GHC.Types.I# 2#)) (sh'9 (GHC.Types.I# 3#)) (sh'9 (GHC.Types.I# 4#)) (sh'9 (GHC.Types.I# 5#)) (sh'9 (GHC.Types.I# 6#)) (sh'9 (GHC.Types.I# 7#)) (sh'9 (GHC.Types.I# 8#)) (sh'9 (GHC.Types.I# 9#)))))) n'9) ((GHC.Enum.enumFromTo sh'10) n'8)))))))

test_12 = (\n'10 -> (Data.Foldable.sum (GHC.Base.build (\c'5 -> (\n'11 -> let sh'12 = (GHC.Types.I# 0#) in (((GHC.Base.foldr (((GHC.Base..) c'5) (\i'5 -> let sh'11 = ((GHC.Num.+) i'5) in (_5 (sh'11 sh'12) (sh'11 (GHC.Types.I# 1#)) (sh'11 (GHC.Types.I# 2#)) (sh'11 (GHC.Types.I# 3#)) (sh'11 (GHC.Types.I# 4#)) (sh'11 (GHC.Types.I# 5#)) (sh'11 (GHC.Types.I# 6#)) (sh'11 (GHC.Types.I# 7#)) (sh'11 (GHC.Types.I# 8#)) (sh'11 (GHC.Types.I# 9#)) (sh'11 (GHC.Types.I# 10#)) (sh'11 (GHC.Types.I# 11#)))))) n'11) ((GHC.Enum.enumFromTo sh'12) n'10)))))))

test_14 = (\n'12 -> (Data.Foldable.sum (GHC.Base.build (\c'6 -> (\n'13 -> let sh'14 = (GHC.Types.I# 0#) in (((GHC.Base.foldr (((GHC.Base..) c'6) (\i'6 -> let sh'13 = ((GHC.Num.+) i'6) in (_6 (sh'13 sh'14) (sh'13 (GHC.Types.I# 1#)) (sh'13 (GHC.Types.I# 2#)) (sh'13 (GHC.Types.I# 3#)) (sh'13 (GHC.Types.I# 4#)) (sh'13 (GHC.Types.I# 5#)) (sh'13 (GHC.Types.I# 6#)) (sh'13 (GHC.Types.I# 7#)) (sh'13 (GHC.Types.I# 8#)) (sh'13 (GHC.Types.I# 9#)) (sh'13 (GHC.Types.I# 10#)) (sh'13 (GHC.Types.I# 11#)) (sh'13 (GHC.Types.I# 12#)) (sh'13 (GHC.Types.I# 13#)))))) n'13) ((GHC.Enum.enumFromTo sh'14) n'12)))))))

test_16 = (\n'14 -> (Data.Foldable.sum (GHC.Base.build (\c'7 -> (\n'15 -> let sh'16 = (GHC.Types.I# 0#) in (((GHC.Base.foldr (((GHC.Base..) c'7) (\i'7 -> let sh'15 = ((GHC.Num.+) i'7) in (_7 (sh'15 sh'16) (sh'15 (GHC.Types.I# 1#)) (sh'15 (GHC.Types.I# 2#)) (sh'15 (GHC.Types.I# 3#)) (sh'15 (GHC.Types.I# 4#)) (sh'15 (GHC.Types.I# 5#)) (sh'15 (GHC.Types.I# 6#)) (sh'15 (GHC.Types.I# 7#)) (sh'15 (GHC.Types.I# 8#)) (sh'15 (GHC.Types.I# 9#)) (sh'15 (GHC.Types.I# 10#)) (sh'15 (GHC.Types.I# 11#)) (sh'15 (GHC.Types.I# 12#)) (sh'15 (GHC.Types.I# 13#)) (sh'15 (GHC.Types.I# 14#)) (sh'15 (GHC.Types.I# 15#)))))) n'15) ((GHC.Enum.enumFromTo sh'16) n'14)))))))

test_18 = (\n'16 -> (Data.Foldable.sum (GHC.Base.build (\c'8 -> (\n'17 -> let sh'18 = (GHC.Types.I# 0#) in (((GHC.Base.foldr (((GHC.Base..) c'8) (\i'8 -> let sh'17 = ((GHC.Num.+) i'8) in (_8 (sh'17 sh'18) (sh'17 (GHC.Types.I# 1#)) (sh'17 (GHC.Types.I# 2#)) (sh'17 (GHC.Types.I# 3#)) (sh'17 (GHC.Types.I# 4#)) (sh'17 (GHC.Types.I# 5#)) (sh'17 (GHC.Types.I# 6#)) (sh'17 (GHC.Types.I# 7#)) (sh'17 (GHC.Types.I# 8#)) (sh'17 (GHC.Types.I# 9#)) (sh'17 (GHC.Types.I# 10#)) (sh'17 (GHC.Types.I# 11#)) (sh'17 (GHC.Types.I# 12#)) (sh'17 (GHC.Types.I# 13#)) (sh'17 (GHC.Types.I# 14#)) (sh'17 (GHC.Types.I# 15#)) (sh'17 (GHC.Types.I# 16#)) (sh'17 (GHC.Types.I# 17#)))))) n'17) ((GHC.Enum.enumFromTo sh'18) n'16)))))))

test_20 = (\n'18 -> (Data.Foldable.sum (GHC.Base.build (\c'9 -> (\n'19 -> let sh'20 = (GHC.Types.I# 0#) in (((GHC.Base.foldr (((GHC.Base..) c'9) (\i'9 -> let sh'19 = ((GHC.Num.+) i'9) in (_9 (sh'19 sh'20) (sh'19 (GHC.Types.I# 1#)) (sh'19 (GHC.Types.I# 2#)) (sh'19 (GHC.Types.I# 3#)) (sh'19 (GHC.Types.I# 4#)) (sh'19 (GHC.Types.I# 5#)) (sh'19 (GHC.Types.I# 6#)) (sh'19 (GHC.Types.I# 7#)) (sh'19 (GHC.Types.I# 8#)) (sh'19 (GHC.Types.I# 9#)) (sh'19 (GHC.Types.I# 10#)) (sh'19 (GHC.Types.I# 11#)) (sh'19 (GHC.Types.I# 12#)) (sh'19 (GHC.Types.I# 13#)) (sh'19 (GHC.Types.I# 14#)) (sh'19 (GHC.Types.I# 15#)) (sh'19 (GHC.Types.I# 16#)) (sh'19 (GHC.Types.I# 17#)) (sh'19 (GHC.Types.I# 18#)) (sh'19 (GHC.Types.I# 19#)))))) n'19) ((GHC.Enum.enumFromTo sh'20) n'18)))))))

prod_10 = (\ds -> (case ds of {(,,,,,,,,,) arg0 arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 -> (_4 arg0 arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9)}))

_4 ds' ds'2 ds'3 ds'4 ds'5 ds'6 ds'7 ds'8 ds'9 ds'10 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (GHC.Types.I# 0#)) ds')) ds'2)) ds'3)) ds'4)) ds'5)) ds'6)) ds'7)) ds'8)) ds'9)) ds'10)

prod_12 = (\ds'11 -> (case ds'11 of {(,,,,,,,,,,,) arg0' arg1' arg2' arg3' arg4' arg5' arg6' arg7' arg8' arg9' arg10 arg11 -> (_5 arg0' arg1' arg2' arg3' arg4' arg5' arg6' arg7' arg8' arg9' arg10 arg11)}))

_5 ds'12 ds'13 ds'14 ds'15 ds'16 ds'17 ds'18 ds'19 ds'20 ds'21 ds'22 ds'23 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (GHC.Types.I# 0#)) ds'12)) ds'13)) ds'14)) ds'15)) ds'16)) ds'17)) ds'18)) ds'19)) ds'20)) ds'21)) ds'22)) ds'23)

prod_14 = (\ds'24 -> (case ds'24 of {(,,,,,,,,,,,,,) arg0'2 arg1'2 arg2'2 arg3'2 arg4'2 arg5'2 arg6'2 arg7'2 arg8'2 arg9'2 arg10' arg11' arg12 arg13 -> (_6 arg0'2 arg1'2 arg2'2 arg3'2 arg4'2 arg5'2 arg6'2 arg7'2 arg8'2 arg9'2 arg10' arg11' arg12 arg13)}))

_6 ds'25 ds'26 ds'27 ds'28 ds'29 ds'30 ds'31 ds'32 ds'33 ds'34 ds'35 ds'36 ds'37 ds'38 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (GHC.Types.I# 0#)) ds'25)) ds'26)) ds'27)) ds'28)) ds'29)) ds'30)) ds'31)) ds'32)) ds'33)) ds'34)) ds'35)) ds'36)) ds'37)) ds'38)

prod_16 = (\ds'39 -> (case ds'39 of {(,,,,,,,,,,,,,,,) arg0'3 arg1'3 arg2'3 arg3'3 arg4'3 arg5'3 arg6'3 arg7'3 arg8'3 arg9'3 arg10'2 arg11'2 arg12' arg13' arg14 arg15 -> (_7 arg0'3 arg1'3 arg2'3 arg3'3 arg4'3 arg5'3 arg6'3 arg7'3 arg8'3 arg9'3 arg10'2 arg11'2 arg12' arg13' arg14 arg15)}))

_7 ds'40 ds'41 ds'42 ds'43 ds'44 ds'45 ds'46 ds'47 ds'48 ds'49 ds'50 ds'51 ds'52 ds'53 ds'54 ds'55 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (GHC.Types.I# 0#)) ds'40)) ds'41)) ds'42)) ds'43)) ds'44)) ds'45)) ds'46)) ds'47)) ds'48)) ds'49)) ds'50)) ds'51)) ds'52)) ds'53)) ds'54)) ds'55)

prod_18 = (\ds'56 -> (case ds'56 of {(,,,,,,,,,,,,,,,,,) arg0'4 arg1'4 arg2'4 arg3'4 arg4'4 arg5'4 arg6'4 arg7'4 arg8'4 arg9'4 arg10'3 arg11'3 arg12'2 arg13'2 arg14' arg15' arg16 arg17 -> (_8 arg0'4 arg1'4 arg2'4 arg3'4 arg4'4 arg5'4 arg6'4 arg7'4 arg8'4 arg9'4 arg10'3 arg11'3 arg12'2 arg13'2 arg14' arg15' arg16 arg17)}))

_8 ds'57 ds'58 ds'59 ds'60 ds'61 ds'62 ds'63 ds'64 ds'65 ds'66 ds'67 ds'68 ds'69 ds'70 ds'71 ds'72 ds'73 ds'74 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (GHC.Types.I# 0#)) ds'57)) ds'58)) ds'59)) ds'60)) ds'61)) ds'62)) ds'63)) ds'64)) ds'65)) ds'66)) ds'67)) ds'68)) ds'69)) ds'70)) ds'71)) ds'72)) ds'73)) ds'74)

prod_2 = (\ds'75 -> (case ds'75 of {(,) arg0'5 arg1'5 -> (_0 arg0'5 arg1'5)}))

_0 ds'76 ds'77 = (((GHC.Num.+) (((GHC.Num.+) (GHC.Types.I# 0#)) ds'76)) ds'77)

prod_20 = (\ds'78 -> (case ds'78 of {(,,,,,,,,,,,,,,,,,,,) arg0'6 arg1'6 arg2'5 arg3'5 arg4'5 arg5'5 arg6'5 arg7'5 arg8'5 arg9'5 arg10'4 arg11'4 arg12'3 arg13'3 arg14'2 arg15'2 arg16' arg17' arg18 arg19 -> (_9 arg0'6 arg1'6 arg2'5 arg3'5 arg4'5 arg5'5 arg6'5 arg7'5 arg8'5 arg9'5 arg10'4 arg11'4 arg12'3 arg13'3 arg14'2 arg15'2 arg16' arg17' arg18 arg19)}))

_9 ds'79 ds'80 ds'81 ds'82 ds'83 ds'84 ds'85 ds'86 ds'87 ds'88 ds'89 ds'90 ds'91 ds'92 ds'93 ds'94 ds'95 ds'96 ds'97 ds'98 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (GHC.Types.I# 0#)) ds'79)) ds'80)) ds'81)) ds'82)) ds'83)) ds'84)) ds'85)) ds'86)) ds'87)) ds'88)) ds'89)) ds'90)) ds'91)) ds'92)) ds'93)) ds'94)) ds'95)) ds'96)) ds'97)) ds'98)

prod_4 = (\ds'99 -> (case ds'99 of {(,,,) arg0'7 arg1'7 arg2'6 arg3'6 -> (_1 arg0'7 arg1'7 arg2'6 arg3'6)}))

_1 ds'100 ds'101 ds'102 ds'103 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (GHC.Types.I# 0#)) ds'100)) ds'101)) ds'102)) ds'103)

prod_6 = (\ds'104 -> (case ds'104 of {(,,,,,) arg0'8 arg1'8 arg2'7 arg3'7 arg4'6 arg5'6 -> (_2 arg0'8 arg1'8 arg2'7 arg3'7 arg4'6 arg5'6)}))

_2 ds'105 ds'106 ds'107 ds'108 ds'109 ds'110 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (GHC.Types.I# 0#)) ds'105)) ds'106)) ds'107)) ds'108)) ds'109)) ds'110)

prod_8 = (\ds'111 -> (case ds'111 of {(,,,,,,,) arg0'9 arg1'9 arg2'8 arg3'8 arg4'7 arg5'7 arg6'6 arg7'6 -> (_3 arg0'9 arg1'9 arg2'8 arg3'8 arg4'7 arg5'7 arg6'6 arg7'6)}))

_3 ds'112 ds'113 ds'114 ds'115 ds'116 ds'117 ds'118 ds'119 = (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (((GHC.Num.+) (GHC.Types.I# 0#)) ds'112)) ds'113)) ds'114)) ds'115)) ds'116)) ds'117)) ds'118)) ds'119)
