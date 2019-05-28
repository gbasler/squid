-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 179; Boxes: 50; Branches: 23
-- Apps: 38; Lams: 14; Unreduced Redexes: 1

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (rec7,rec3,rec2,rec8,rec7Test1,rec7Test2,main,rec7Test0,rec0,rec1) where

import GHC.Base
import GHC.Classes
import GHC.List
import GHC.Num
import GHC.TopHandler
import GHC.Types
import System.Exit

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) rec7Test2) (((:) 1) (((:) 1) (((:) 1) [])))) of {False -> System.Exit.exitFailure; True -> System.Exit.exitSuccess}))

rec7Test2 = (((GHC.Base.$) (GHC.List.take (GHC.Types.I# 3#))) (_8(# (), (_5(# _12, _12 #)) #)))

rec0 = (\f -> (_0(# f #)))

_0(# f'8 #) = (f'8 (\x'3 -> ((_0(# f'8 #)) x'3)))

rec1 = (\f' -> (\x'9 -> (_1(# f' #))))

_1(# f'9 #) = (f'9 (_1(# f'9 #)))

rec2 = (\f'2 -> (\x -> (_2(# f'2, x #))))

_2(# f'10, x'4 #) = (((:) x'4) (f'10 (_2(# f'10, x'4 #))))

rec3 = (\f'3 -> (\x' -> (\y -> (_3(# x', x', f'3, y #)))))

_3(# x'5, x'6, f'13, y'2 #) = (((:) ((_15(# f'13 #)) x'6)) (_3(# (_16(# y'2, f'13 #)), (_16(# y'2, f'13 #)), f'13, x'5 #)))

rec7 = (\f'4 -> (_4(# {-A-}\(ds'2) -> (_5(# f'4, f'4 #)) #)))

_4(# f'17 #) = (\ds'2 -> (_8(# ds'2, {-P-}(f'17(ds'2)) #)))

_5(# f'21, f'22 #) = (f'22 (_14(# f'21, f'21 #)))

rec7Test0 = (_4(# {-A-}\(ds'2) -> (_5(# _6, _6 #)) #))

_6 = ((GHC.Num.+) 1)

rec7Test1 = (_4(# {-A-}\(ds'2) -> (_7(# (_8(# _9, (_10(# (_11(# (\ds -> (_7(# ds #))) #)), (\ds -> (_7(# ds #))) #)) #)) #)) #))

_7(# ds'3 #) = (((GHC.Num.*) ds'3) 2)

_8(# ds', f'16 #) = (case ds' of {() -> f'16})

_9 = ()

_10(# f'6, f'7 #) = (f'7 (_14(# f'6, f'6 #)))

_11(# f'20 #) = f'20

_12 = ((:) 1)

rec8 = (\f'5 -> (\x'2 -> (_13(# f'5, x'2 #))))

_13(# f'19, x'7 #) = (((GHC.Num.-) (sx(# x'7 #))) (((GHC.Num.*) ((_17(# f'19 #)) (_13(# f'19, (sx(# x'7 #)) #)))) ((_17(# f'19 #)) (_13(# f'19, (sx(# x'7 #)) #)))))

_14(# f'14, f'15 #) = (_8(# _9, (_10(# (_11(# f'15 #)), f'14 #)) #))

_15(# f'11 #) = f'11

_16(# y', f'12 #) = ((_15(# f'12 #)) y')

_17(# f'18 #) = f'18

sx(# x'8 #) = (((GHC.Num.+) x'8) (GHC.Num.fromInteger 1))
