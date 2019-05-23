-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

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

_0(# f, f', f'2 #) = (f' (_1(# f, f'2 #)))

_2(# f'3 #) = (f'3 (\x -> ((_2(# f'3 #)) x)))

_3(# f'4, f'5 #) = (f'5 (_3(# f'5, f'4 #)))

_4(# x', f'6, f'7 #) = (((:) x') (f'6 (_4(# x', f'7, f'6 #))))

_5(# y, f'8, f'9, x'2 #) = (((:) (f'9 x'2)) (_5(# x'2, f'9, f'8, (f'9 y) #)))

_1(# f'10, f'11 #) = (_6(# (), ((_7(# f'11 #)) (_1(# (_7(# f'11 #)), f'10 #))) #))

_6(# ds, f'12 #) = (case ds of {() -> f'12})

_8(# f'13 #) = (\ds' -> (_6(# ds', {-P-}(f'13(ds')) #)))

_9 = ((GHC.Num.+) 1)

_10(# ds'2 #) = (((GHC.Num.*) ds'2) 2)

_11 = (\ds'3 -> (_10(# ds'3 #)))

_12 = ((:) 1)

_13(# x'3, f'14, f'15 #) = (((GHC.Num.-) (sx(# x'3 #))) (((GHC.Num.*) (f'14 (_13(# (sx(# x'3 #)), f'15, f'14 #)))) (f'14 (_13(# (sx(# x'3 #)), f'14, f'14 #)))))

_7(# f'16 #) = f'16

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) rec7Test2) (((:) 1) (((:) 1) (((:) 1) [])))) of {False -> System.Exit.exitFailure; True -> System.Exit.exitSuccess}))

rec0 = (\f'17 -> (_2(# f'17 #)))

rec1 = (\f'18 -> (\x'8 -> (_3(# f'18, f'18 #))))

rec2 = (\f'19 -> (\x'4 -> (_4(# x'4, f'19, f'19 #))))

rec3 = (\f'20 -> (\x'5 -> (\y' -> (_5(# y', f'20, f'20, x'5 #)))))

rec7 = (\f'21 -> (_8(# {-A-}\(ds') -> (_0(# f'21, f'21, f'21 #)) #)))

rec7Test0 = (_8(# {-A-}\(ds') -> (_0(# _9, _9, _9 #)) #))

rec7Test1 = (_8(# {-A-}\(ds') -> (_10(# (_1(# _11, _11 #)) #)) #))

rec7Test2 = (((GHC.Base.$) (GHC.List.take (GHC.Types.I# 3#))) (_6(# (), (_0(# _12, _12, _12 #)) #)))

rec8 = (\f'22 -> (\x'6 -> (_13(# x'6, f'22, f'22 #))))

sx(# x'7 #) = (((GHC.Num.+) x'7) (GHC.Num.fromInteger 1))
