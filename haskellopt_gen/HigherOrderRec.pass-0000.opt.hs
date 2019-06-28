-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 176; Boxes: 48; Branches: 23
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

rec7Test2 = (((GHC.Base.$) (GHC.List.take (GHC.Types.I# 3#))) (_1(# (_0(# ((:) 1) #)) #)))

rec0 = (\f -> (_2(# f #)))

_2(# f' #) = (f' (\x -> ((_2(# f' #)) x)))

rec1 = (\f'2 -> (\x' -> (_3(# f'2 #))))

_3(# f'3 #) = (f'3 (_3(# f'3 #)))

rec2 = (\f'4 -> (\x'2 -> (_4(# f'4, x'2 #))))

_4(# f'5, x'3 #) = (((:) x'3) (f'5 (_4(# f'5, x'3 #))))

rec3 = (\f'6 -> (\x'4 -> (\y -> (_5(# f'6, x'4, y #)))))

_5(# f'7, x'5, y' #) = (((:) ((_6(# f'7 #)) x'5)) (_5(# f'7, ((_6(# f'7 #)) y'), x'5 #)))

rec7 = (\f'8 -> (_7(# (_0(# f'8 #)) #)))

_7(# f'9 #) = (\ds -> (_1(# (case ds of {() -> f'9}) #)))

_0(# f'10 #) = (f'10 (_0(# (_8(# f'10 #)) #)))

rec7Test0 = (_7(# (_0(# ((GHC.Num.+) 1) #)) #))

rec7Test1 = (_7(# (_9(# (_0(# (_8(# (\ds' -> (_9(# ds' #))) #)) #)) #)) #))

_9(# ds'2 #) = (((GHC.Num.*) ds'2) 2)

_8(# f'11 #) = f'11

_1(# ds'3 #) = ds'3

rec8 = (\f'12 -> (\x'6 -> (_10(# f'12, x'6 #))))

_10(# f'13, x'7 #) = (((GHC.Num.-) (sx(# x'7 #))) (((GHC.Num.*) ((_11(# f'13 #)) (_10(# f'13, (sx(# x'7 #)) #)))) ((_11(# f'13 #)) (_10(# f'13, (sx(# x'7 #)) #)))))

_6(# f'14 #) = f'14

_11(# f'15 #) = f'15

sx(# x'8 #) = (((GHC.Num.+) x'8) (GHC.Num.fromInteger 1))
