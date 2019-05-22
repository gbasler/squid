-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (rec7,rec3,rec2,rec8,rec7Test1,rec7Test2,main,ds,rec7Test0,rec0,rec1) where

import GHC.Classes
import GHC.List
import GHC.Num
import GHC.TopHandler
import GHC.Types
import System.Exit

_0(# f, f', f'2 #) = (f' (_1(# f, f'2 #)))

_2(# f'3 #) = (f'3 (\x'4 -> ((_2(# f'3 #)) x'4)))

_3(# f'4, f'5 #) = (f'5 (_3(# f'5, f'4 #)))

_4(# x, f'6, f'7 #) = (((:) x) (f'6 (_4(# x, f'7, f'6 #))))

_5(# y, f'8, f'9, x' #) = (((:) (f'9 x')) (_5(# x', f'9, f'8, (f'9 y) #)))

_1(# f'10, f'11 #) = (_6(# (), ((_7(# f'11 #)) (_1(# (_7(# f'11 #)), f'10 #))) #))

_6(# ds', f'12 #) = (case ds' of {() -> f'12})

_8(# f'13 #) = (\ds'2 -> (_6(# ds'2, (f'13(ds'2)) #)))

_9(# ds'3 #) = (((GHC.Num.*) ds'3) 2)

_10 = ((:) 1)

_11(# x'2, f'14 #) = (((GHC.Num.-) (sx(# x'2 #))) (((GHC.Num.*) (f'14 (_11(# (sx(# x'2 #)), f'14 #)))) (f'14 (_11(# (sx(# x'2 #)), f'14 #)))))

_7(# f'15 #) = f'15

ds = 2

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) rec7Test2) (((:) 1) (((:) 1) (((:) 1) [])))) of {False -> System.Exit.exitFailure; True -> System.Exit.exitSuccess}))

rec0 = (\f'16 -> (_2(# f'16 #)))

rec1 = (\f'17 -> (\x'5 -> (_3(# f'17, f'17 #))))

rec2 = (\f'18 -> (\x'6 -> (_4(# x'6, f'18, f'18 #))))

rec3 = (\f'19 -> (\x'7 -> (\y' -> (_5(# y', f'19, f'19, x'7 #)))))

rec7 = (\f'20 -> (_8(# \(ds'2) -> (_0(# f'20, f'20, f'20 #)) #)))

rec7Test0 = (_8(# \(ds'2) -> (_0(# rec7Test0', rec7Test0', rec7Test0' #)) #))

rec7Test0' = ((GHC.Num.+) 1)

rec7Test1' = (\ds'4 -> (_9(# ds'4 #)))

rec7Test1 = (_8(# \(ds'2) -> (_9(# (_1(# rec7Test1', rec7Test1' #)) #)) #))

rec7Test2 = ((GHC.List.take (GHC.Types.I# 3#)) (_6(# (), (_0(# _10, _10, _10 #)) #)))

rec8 = (\eta -> (\eta' -> (_11(# eta', eta #))))

sx(# x'3 #) = (((GHC.Num.+) x'3) (GHC.Num.fromInteger 1))
