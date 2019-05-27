-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 137; Boxes: 59; Branches: 29
-- Apps: 97; Lams: 14; Unreduced Redexes: 2

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

rec7Test2 = (((GHC.Base.$) (GHC.List.take (GHC.Types.I# 3#))) (_8(# (), (_5(# _9, _9, _9 #)) #)))

rec0 = (\f -> (_0(# f #)))

_0(# f'8 #) = (f'8 (\x'2 -> ((_0(# f'8 #)) x'2)))

rec1 = (\f' -> (\x'7 -> (_1(# f', f' #))))

_1(# f'9, f'10 #) = (f'10 (_1(# f'10, f'9 #)))

rec2 = (\f'2 -> (\x -> (_2(# x, f'2, f'2 #))))

_2(# x'3, f'11, f'12 #) = (((:) x'3) (f'11 (_2(# x'3, f'12, f'11 #))))

rec3 = (\f'3 -> (\x' -> (\y -> (_3(# y, f'3, f'3, x' #)))))

_3(# y', f'13, f'14, x'4 #) = (((:) (f'14 x'4)) (_3(# x'4, f'14, f'13, (f'14 y') #)))

rec7 = (\f'4 -> (_4(# {-A-}\(ds'2) -> (_5(# f'4, f'4, f'4 #)) #)))

_4(# f'18 #) = (\ds'2 -> (_8(# ds'2, {-P-}(f'18(ds'2)) #)))

_5(# f'5, f'6, f'7 #) = (f'6 (_7(# f'5, f'7 #)))

rec7Test0 = (_4(# {-A-}\(ds'2) -> (_5(# rec7Test0', rec7Test0', rec7Test0' #)) #))

rec7Test0' = ((GHC.Num.+) 1)

rec7Test1 = (_4(# {-A-}\(ds'2) -> (_6(# (_7(# rec7Test1', rec7Test1' #)) #)) #))

_6(# ds'3 #) = (((GHC.Num.*) ds'3) ds)

_7(# f'15, f'16 #) = (_8(# (), ((_11(# f'16 #)) (_7(# (_11(# f'16 #)), f'15 #))) #))

rec7Test1' = (\ds'4 -> (_6(# ds'4 #)))

_8(# ds', f'17 #) = (case ds' of {() -> f'17})

_9 = ((:) 1)

rec8 = (\eta' -> (\eta -> (_10(# eta, eta' #))))

_10(# x'5, f'19 #) = (((GHC.Num.-) (sx(# x'5 #))) (((GHC.Num.*) (f'19 (_10(# (sx(# x'5 #)), f'19 #)))) (f'19 (_10(# (sx(# x'5 #)), f'19 #)))))

_11(# f'20 #) = f'20

sx(# x'6 #) = (((GHC.Num.+) x'6) (GHC.Num.fromInteger 1))
