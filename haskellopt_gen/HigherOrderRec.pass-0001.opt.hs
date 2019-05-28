-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 180; Boxes: 50; Branches: 22
-- Apps: 38; Lams: 14; Unreduced Redexes: 1

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

_0(# f'9 #) = (f'9 (\x'2 -> ((_0(# f'9 #)) x'2)))

rec1 = (\f' -> (\x'7 -> (_1(# f' #))))

_1(# f'10 #) = (f'10 (_1(# f'10 #)))

rec2 = (\f'2 -> (\x -> (_2(# f'2, x #))))

_2(# f'11, x'3 #) = (((:) x'3) (f'11 (_2(# f'11, x'3 #))))

rec3 = (\f'3 -> (\x' -> (\y -> (_3(# f'3, x', y #)))))

_3(# f'13, x'4, y' #) = (((:) ((_14(# f'13 #)) x'4)) (_3(# f'13, ((_14(# f'13 #)) y'), x'4 #)))

rec7 = (\f'4 -> (_4(# {-A-}\(ds'3) -> (_5(# f'4, f'4 #)) #)))

_4(# f'17 #) = (\ds'3 -> (_7(# ds'3, {-P-}(f'17(ds'3)) #)))

_5(# f'5, f'6 #) = (f'6 (_13(# f'5, f'5 #)))

rec7Test0 = (_4(# {-A-}\(ds'3) -> (_5(# ((GHC.Num.+) 1), ((GHC.Num.+) 1) #)) #))

rec7Test1 = (_4(# {-A-}\(ds'3) -> (_6(# (_7(# _8, (_9(# (_10(# (\ds' -> (_6(# ds' #))) #)), (\ds' -> (_6(# ds' #))) #)) #)) #)) #))

_6(# ds'4 #) = (((GHC.Num.*) ds'4) ds)

_7(# ds'2, f'16 #) = (case ds'2 of {() -> f'16})

_8 = ()

_9(# f'7, f'8 #) = (f'8 (_13(# f'7, f'7 #)))

_10(# f'20 #) = f'20

_11 = ((:) 1)

rec8 = (\eta -> (\eta' -> (_12(# eta, eta' #))))

_12(# f'19, x'5 #) = (((GHC.Num.-) (sx(# x'5 #))) (((GHC.Num.*) ((_15(# f'19 #)) (_12(# f'19, (sx(# x'5 #)) #)))) ((_15(# f'19 #)) (_12(# f'19, (sx(# x'5 #)) #)))))

_13(# f'14, f'15 #) = (_7(# _8, (_9(# (_10(# f'15 #)), f'14 #)) #))

_14(# f'12 #) = f'12

_15(# f'18 #) = f'18

sx(# x'6 #) = (((GHC.Num.+) x'6) (GHC.Num.fromInteger 1))
