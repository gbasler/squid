-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 135; Boxes: 55; Branches: 27
-- Apps: 93; Lams: 14; Unreduced Redexes: 1

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (rec7,rec3,rec2,rec8,rec7Test1,rec7Test2,main,ds,rec7Test0,rec0,rec1) where

import GHC.Base
import GHC.Classes
import GHC.List
import GHC.Num
import GHC.TopHandler
import GHC.Types
import System.Exit

ds = 2

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) rec7Test2) (((:) 1) (((:) 1) (((:) 1) [])))) of {False -> System.Exit.exitFailure; True -> System.Exit.exitSuccess}))

rec7Test2 = (((GHC.Base.$) (GHC.List.take (GHC.Types.I# 3#))) (_7(# (), (_5(# _11, _11 #)) #)))

rec0 = (\f -> (_0(# f #)))

_0(# f'12 #) = (f'12 (\x'2 -> ((_0(# f'12 #)) x'2)))

rec1 = (\f' -> (\x'8 -> (_1(# f' #))))

_1(# f'13 #) = (f'13 (_1(# f'13 #)))

rec2 = (\f'2 -> (\x -> (_2(# f'2, x #))))

_2(# f'14, x'3 #) = (((:) x'3) (f'14 (_2(# f'14, x'3 #))))

rec3 = (\f'3 -> (\x' -> (\y -> (_3(# x', x', f'3, y #)))))

_3(# x'4, x'5, f'17, y'2 #) = (((:) ((_17(# f'17 #)) x'5)) (_3(# (_18(# y'2, f'17 #)), (_18(# y'2, f'17 #)), f'17, x'4 #)))

rec7 = (\f'4 -> (_4(# {-A-}\(ds'3) -> (_5(# f'4, f'4 #)) #)))

_4(# f'21 #) = (\ds'3 -> (_7(# ds'3, {-P-}(f'21(ds'3)) #)))

_5(# f'8, f'9 #) = (f'9 (_16(# f'8, f'8 #)))

rec7Test0 = (_4(# {-A-}\(ds'3) -> (_5(# rec7Test0', rec7Test0' #)) #))

rec7Test0' = ((GHC.Num.+) 1)

rec7Test1 = (_4(# {-A-}\(ds'3) -> (_6(# (_7(# _8, (_9(# (_10(# (\ds' -> (_6(# ds' #))) #)), (\ds' -> (_6(# ds' #))) #)) #)) #)) #))

_6(# ds'4 #) = (((GHC.Num.*) ds'4) ds)

_7(# ds'2, f'20 #) = (case ds'2 of {() -> f'20})

_8 = ()

_9(# f'10, f'11 #) = (f'11 (_16(# f'10, f'10 #)))

_10(# f'25 #) = f'25

_11 = ((:) 1)

rec8 = (\eta -> (\eta' -> (_12(# eta, eta', eta, eta #))))

_12(# f'22, x'6, f'23, f'24 #) = (((GHC.Num.-) (sx(# x'6 #))) (((GHC.Num.*) (f'23 (_12(# (_14(# f'22 #)), (sx(# x'6 #)), (_13(# f'22 #)), (_15(# f'22 #)) #)))) (f'23 (_12(# (_14(# f'24 #)), (sx(# x'6 #)), (_13(# f'24 #)), (_15(# f'24 #)) #)))))

_13(# f'5 #) = f'5

_14(# f'6 #) = f'6

_15(# f'7 #) = f'7

_16(# f'18, f'19 #) = (_7(# _8, (_9(# (_10(# f'19 #)), f'18 #)) #))

_17(# f'15 #) = f'15

_18(# y', f'16 #) = ((_17(# f'16 #)) y')

sx(# x'7 #) = (((GHC.Num.+) x'7) (GHC.Num.fromInteger 1))
