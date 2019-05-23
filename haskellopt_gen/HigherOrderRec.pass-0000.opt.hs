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

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) rec7Test2) (((:) 1) (((:) 1) (((:) 1) [])))) of {False -> System.Exit.exitFailure; True -> System.Exit.exitSuccess}))

rec7Test2 = (((GHC.Base.$) (GHC.List.take (GHC.Types.I# 3#))) (_10(# (), (_5(# _11, _11, _11 #)) #)))

rec0 = (\f -> (_0(# f #)))

_0(# f'9 #) = (f'9 (\x'3 -> ((_0(# f'9 #)) x'3)))

rec1 = (\f' -> (\x'8 -> (_1(# f', f' #))))

_1(# f'10, f'11 #) = (f'11 (_1(# f'11, f'10 #)))

rec2 = (\f'2 -> (\x -> (_2(# x, f'2, f'2 #))))

_2(# x'4, f'12, f'13 #) = (((:) x'4) (f'12 (_2(# x'4, f'13, f'12 #))))

rec3 = (\f'3 -> (\x' -> (\y -> (_3(# y, f'3, f'3, x' #)))))

_3(# y', f'14, f'15, x'5 #) = (((:) (f'15 x'5)) (_3(# x'5, f'15, f'14, (f'15 y') #)))

rec7 = (\f'4 -> (_4(# {-A-}\(ds') -> (_5(# f'4, f'4, f'4 #)) #)))

_4(# f'19 #) = (\ds' -> (_10(# ds', {-P-}(f'19(ds')) #)))

_5(# f'6, f'7, f'8 #) = (f'7 (_8(# f'6, f'8 #)))

rec7Test0 = (_4(# {-A-}\(ds') -> (_5(# _6, _6, _6 #)) #))

_6 = ((GHC.Num.+) 1)

rec7Test1 = (_4(# {-A-}\(ds') -> (_7(# (_8(# _9, _9 #)) #)) #))

_7(# ds'2 #) = (((GHC.Num.*) ds'2) 2)

_8(# f'16, f'17 #) = (_10(# (), ((_13(# f'17 #)) (_8(# (_13(# f'17 #)), f'16 #))) #))

_9 = (\ds'3 -> (_7(# ds'3 #)))

_10(# ds, f'18 #) = (case ds of {() -> f'18})

_11 = ((:) 1)

rec8 = (\f'5 -> (\x'2 -> (_12(# x'2, f'5, f'5 #))))

_12(# x'6, f'20, f'21 #) = (((GHC.Num.-) (sx(# x'6 #))) (((GHC.Num.*) (f'20 (_12(# (sx(# x'6 #)), f'21, f'20 #)))) (f'20 (_12(# (sx(# x'6 #)), f'20, f'20 #)))))

_13(# f'22 #) = f'22

sx(# x'7 #) = (((GHC.Num.+) x'7) (GHC.Num.fromInteger 1))
