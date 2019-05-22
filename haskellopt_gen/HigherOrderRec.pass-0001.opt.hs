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

_0(# _1, _2, _3 #) = (_2 (_4(# _1, _3 #)))

_5(# _6 #) = (_6 (\x_a -> ((_5(# _6 #)) x_a)))

_7(# _8, _9 #) = (_9 (_7(# _9, _8 #)))

_10(# _11, _12, _13 #) = (((:) _11) (_12 (_10(# _11, _13, _12 #))))

_14(# _15, _16, _17, _18 #) = (((:) (_17 _18)) (_14(# _18, _17, _16, (_17 _15) #)))

_4(# _19, _20 #) = (_21(# (), ((_22(# _20 #)) (_4(# (_22(# _20 #)), _19 #))) #))

_21(# _23, _24 #) = (case _23 of {() -> _24})

_25(# _26 #) = (\ds_d -> (_21(# ds_d, (_26(ds_d)) #)))

_27(# _28 #) = (((GHC.Num.*) _28) 2)

_29 = ((:) 1)

_30(# _31, _32 #) = (((GHC.Num.-) (sx_a(# _31 #))) (((GHC.Num.*) (_32 (_30(# (sx_a(# _31 #)), _32 #)))) (_32 (_30(# (sx_a(# _31 #)), _32 #)))))

_22(# _33 #) = _33

ds = 2

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) rec7Test2) (((:) 1) (((:) 1) (((:) 1) [])))) of {False -> System.Exit.exitFailure; True -> System.Exit.exitSuccess}))

rec0 = (\f_a -> (_5(# f_a #)))

rec1 = (\f_a' -> (\x_a' -> (_7(# f_a', f_a' #))))

rec2 = (\f_a'2 -> (\x_a'2 -> (_10(# x_a'2, f_a'2, f_a'2 #))))

rec3 = (\f_a'3 -> (\x_a'3 -> (\y_a -> (_14(# y_a, f_a'3, f_a'3, x_a'3 #)))))

rec7 = (\f_a'4 -> (_25(# \(ds_d) -> (_0(# f_a'4, f_a'4, f_a'4 #)) #)))

rec7Test0 = (_25(# \(ds_d) -> (_0(# rec7Test0', rec7Test0', rec7Test0' #)) #))

rec7Test0' = ((GHC.Num.+) 1)

rec7Test1' = (\ds_d' -> (_27(# ds_d' #)))

rec7Test1 = (_25(# \(ds_d) -> (_27(# (_4(# rec7Test1', rec7Test1' #)) #)) #))

rec7Test2 = ((GHC.List.take (GHC.Types.I# 3#)) (_21(# (), (_0(# _29, _29, _29 #)) #)))

rec8 = (\eta_B -> (\eta_B' -> (_30(# eta_B', eta_B #))))

sx_a(# _34 #) = (((GHC.Num.+) _34) (GHC.Num.fromInteger 1))
