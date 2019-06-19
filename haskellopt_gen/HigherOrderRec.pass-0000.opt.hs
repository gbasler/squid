-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 171; Boxes: 46; Branches: 20
-- Apps: 37; Lams: 14; Unreduced Redexes: 1

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

main_sub = ((:) 1)
main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) rec7Test2) (main_sub (main_sub (main_sub [])))) of {False -> System.Exit.exitFailure; True -> System.Exit.exitSuccess}))

rec7Test2 = (((GHC.Base.$) (GHC.List.take (GHC.Types.I# 3#))) (_7(# (), (_5(# ((:) 1) #)) #)))

rec0 = (\f -> (_0(# f #)))

_0(# f'6 #) = (f'6 (\x'3 -> ((_0(# f'6 #)) x'3)))

rec1 = (\f' -> (\x'8 -> (_1(# f' #))))

_1(# f'7 #) = (f'7 (_1(# f'7 #)))

rec2 = (\f'2 -> (\x -> (_2(# f'2, x #))))

_2(# f'8, x'4 #) = (((:) x'4) (f'8 (_2(# f'8, x'4 #))))

rec3 = (\f'3 -> (\x' -> (\y -> (_3(# f'3, x', y #)))))

_3(# f'10, x'5, y' #) = let
    _12 = (_11(# f'10 #))
  in (((:) (_12 x'5)) (_3(# f'10, (_12 y'), x'5 #)))

rec7 = (\f'4 -> (_4(# {-A-}\(ds'2) -> (_5(# f'4 #)) #)))

_4(# f'12 #) = (\ds'2 -> (_7(# ds'2, {-P-}(f'12(ds'2)) #)))

_5(# f'16 #) = (f'16 (_7(# _8, (_5(# (_9(# f'16 #)) #)) #)))

rec7Test0 = (_4(# {-A-}\(ds'2) -> (_5(# ((GHC.Num.+) 1) #)) #))

rec7Test1 = (_4(# {-A-}\(ds'2) -> (_6(# (_7(# _8, (_5(# (_9(# (\ds -> (_6(# ds #))) #)) #)) #)) #)) #))

_6(# ds'3 #) = (((GHC.Num.*) ds'3) 2)

_7(# ds', f'11 #) = (case ds' of {() -> f'11})

_8 = ()

_9(# f'15 #) = f'15

rec8 = (\f'5 -> (\x'2 -> (_10(# f'5, x'2 #))))

_10(# f'14, x'6 #) = let
    _14 = (sx(# x'6 #))
    _15 = (_13(# f'14 #))
  in (((GHC.Num.-) _14) (((GHC.Num.*) (_15 (_10(# f'14, _14 #)))) (_15 (_10(# f'14, _14 #)))))

_11(# f'9 #) = f'9

_13(# f'13 #) = f'13

sx(# x'7 #) = (((GHC.Num.+) x'7) (GHC.Num.fromInteger 1))
