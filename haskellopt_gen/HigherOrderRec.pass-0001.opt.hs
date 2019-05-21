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

module Main (rec7,rec3,rec2,rec7Test1,rec7Test2,main,ds,rec7Test0,rec0,rec1) where

import GHC.Classes
import GHC.List
import GHC.Num
import GHC.TopHandler
import GHC.Types
import System.Exit

_0(# _1 #) = (_1 (\x_a -> ((_0(# _1 #)) x_a)))

_2(# _3, _4 #) = (_4 (_2(# _4, _3 #)))

_5(# _6, _7, _8 #) = (((:) _7) (_8 (_5(# _8, _7, _6 #))))

_9(# _10, _11, _12, _13 #) = (((:) (_13 _12)) (_9(# _13, _12, (_13 _11), _10 #)))

_14(# _15, _16 #) = (_17(# ((_18(# _15 #)) (_14(# _16, (_18(# _15 #)) #))), () #))

_17(# _19, _20 #) = (case _20 of {() -> _19})

_21(# _22 #) = (\ds_d -> (_17(# (_22(ds_d)), ds_d #)))

_23(# _24 #) = (((GHC.Num.*) _24) 2)

_25 = ((:) 1)

_18(# _26 #) = _26

_27(# _28, _29, _30 #) = (_29 (_14(# _30, _28 #)))

ds = 2

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) rec7Test2) (((:) 1) (((:) 1) (((:) 1) [])))) of {False -> System.Exit.exitFailure; True -> System.Exit.exitSuccess}))

rec0 = (\f_a -> (_0(# f_a #)))

rec1 = (\f_a' -> (\x_a' -> (_2(# f_a', f_a' #))))

rec2 = (\f_a'2 -> (\x_a'2 -> (_5(# f_a'2, x_a'2, f_a'2 #))))

rec3 = (\f_a'3 -> (\x_a'3 -> (\y_a -> (_9(# f_a'3, y_a, x_a'3, f_a'3 #)))))

rec7 = (\f_a'4 -> (_21(# \(ds_d) -> (_27(# f_a'4, f_a'4, f_a'4 #)) #)))

rec7Test0 = (_21(# \(ds_d) -> (_27(# rec7Test0', rec7Test0', rec7Test0' #)) #))

rec7Test0' = ((GHC.Num.+) 1)

rec7Test1' = (\ds_d' -> (_23(# ds_d' #)))

rec7Test1 = (_21(# \(ds_d) -> (_23(# (_14(# rec7Test1', rec7Test1' #)) #)) #))

rec7Test2 = ((GHC.List.take (GHC.Types.I# 3#)) (_17(# (_27(# _25, _25, _25 #)), () #)))
