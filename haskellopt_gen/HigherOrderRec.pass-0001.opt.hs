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

main_sub = ((:) 1)
main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) rec7Test2) (main_sub (main_sub (main_sub [])))) of {False -> System.Exit.exitFailure; True -> System.Exit.exitSuccess}))

rec7Test2 = (((GHC.Base.$) (GHC.List.take (GHC.Types.I# 3#))) (_7(# (), (_5(# _11, _11 #)) #)))

rec0 = (\f -> (_0(# f #)))

_0(# f'5 #) = (f'5 (\x'2 -> ((_0(# f'5 #)) x'2)))

rec1 = (\f' -> (\x'7 -> (_1(# f' #))))

_1(# f'6 #) = (f'6 (_1(# f'6 #)))

rec2 = (\f'2 -> (\x -> (_2(# f'2, x #))))

_2(# f'7, x'3 #) = (((:) x'3) (f'7 (_2(# f'7, x'3 #))))

rec3 = (\f'3 -> (\x' -> (\y -> (_3(# f'3, x', y #)))))

_3(# f'9, x'4, y' #) = let
    _14 = (_13(# f'9 #))
  in (((:) (_14 x'4)) (_3(# f'9, (_14 y'), x'4 #)))

rec7 = (\f'4 -> (_4(# {-A-}\(ds'3) -> (_5(# f'4, f'4 #)) #)))

_4(# f'13 #) = (\ds'3 -> (_7(# ds'3, {-P-}(f'13(ds'3)) #)))

_5(# f'17, f'18 #) = (f'18 (_15(# f'17, f'17 #)))

rec7Test0_sub = ((GHC.Num.+) 1)
rec7Test0 = (_4(# {-A-}\(ds'3) -> (_5(# rec7Test0_sub, rec7Test0_sub #)) #))

rec7Test1_sub = (\ds' -> (_6(# ds' #)))
rec7Test1 = (_4(# {-A-}\(ds'3) -> (_6(# (_7(# _8, (_9(# (_10(# rec7Test1_sub #)), rec7Test1_sub #)) #)) #)) #))

_6(# ds'4 #) = (((GHC.Num.*) ds'4) ds)

_7(# ds'2, f'12 #) = (case ds'2 of {() -> f'12})

_8 = ()

_9(# f'19, f'20 #) = (f'20 (_15(# f'19, f'19 #)))

_10(# f'16 #) = f'16

_11 = ((:) 1)

rec8 = (\eta -> (\eta' -> (_12(# eta, eta' #))))

_12(# f'15, x'5 #) = let
    _17 = (sx(# x'5 #))
    _18 = (_16(# f'15 #))
  in (((GHC.Num.-) _17) (((GHC.Num.*) (_18 (_12(# f'15, _17 #)))) (_18 (_12(# f'15, _17 #)))))

_13(# f'8 #) = f'8

_15(# f'10, f'11 #) = (_7(# _8, (_9(# (_10(# f'11 #)), f'10 #)) #))

_16(# f'14 #) = f'14

sx(# x'6 #) = (((GHC.Num.+) x'6) (GHC.Num.fromInteger 1))
