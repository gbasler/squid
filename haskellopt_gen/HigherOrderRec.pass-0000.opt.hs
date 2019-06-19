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

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) rec7Test2) (((:) 1) (((:) 1) (((:) 1) [])))) of {False -> System.Exit.exitFailure; True -> System.Exit.exitSuccess}))

rec7Test2 = (((GHC.Base.$) (GHC.List.take (GHC.Types.I# 3#))) (_1(# (), (_0(# ((:) 1) #)) #)))

rec0 = (\f -> (_2(# f #)))

_2(# f' #) = (f' (\x -> ((_2(# f' #)) x)))

rec1 = (\f'2 -> (\x' -> (_3(# f'2 #))))

_3(# f'3 #) = (f'3 (_3(# f'3 #)))

rec2 = (\f'4 -> (\x'2 -> (_4(# f'4, x'2 #))))

_4(# f'5, x'3 #) = (((:) x'3) (f'5 (_4(# f'5, x'3 #))))

rec3 = (\f'6 -> (\x'4 -> (\y -> (_5(# f'6, x'4, y #)))))

_5(# f'7, x'5, y' #) = (((:) ((_6(# f'7 #)) x'5)) (_5(# f'7, ((_6(# f'7 #)) y'), x'5 #)))

rec7 = (\f'8 -> (_7(# (_0(# f'8 #)) #)))

_7(# f'9 #) = (\ds -> (_1(# ds, f'9 #)))

_0(# f'10 #) = (f'10 (_1(# _8, (_0(# (_9(# f'10 #)) #)) #)))

rec7Test0 = (_7(# (_0(# ((GHC.Num.+) 1) #)) #))

rec7Test1 = (_7(# (_10(# (_1(# _8, (_0(# (_9(# (\ds' -> (_10(# ds' #))) #)) #)) #)) #)) #))

_10(# ds'2 #) = (((GHC.Num.*) ds'2) 2)

_1(# ds'3, f'11 #) = (case ds'3 of {() -> f'11})

_8 = ()

_9(# f'12 #) = f'12

rec8 = (\f'13 -> (\x'6 -> (_11(# f'13, x'6 #))))

_11(# f'14, x'7 #) = (((GHC.Num.-) (sx(# x'7 #))) (((GHC.Num.*) ((_12(# f'14 #)) (_11(# f'14, (sx(# x'7 #)) #)))) ((_12(# f'14 #)) (_11(# f'14, (sx(# x'7 #)) #)))))

_6(# f'15 #) = f'15

_12(# f'16 #) = f'16

sx(# x'8 #) = (((GHC.Num.+) x'8) (GHC.Num.fromInteger 1))
