-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module IterCont (loop,dont) where

import GHC.Num

_0(# _1 #) = (\new_state_a -> (_1(new_state_a)))

_2(# _3 #) = (((GHC.Num.+) _3) 1)

_4 = (\k_a -> (\s_a -> (_2(# s_a #))))

_5(# new_state_a #) = new_state_a

_7(# _8 #) = _8

_10(# _11 #) = _11

_12(# _13, _14, _15 #) = (((_7(# _13 #)) (_0(# \(new_state_a) -> (_12(# _14, _15, (_7(# _13 #)) #)) #))) (_5(# new_state_a #)))

dont = (_2(# 0 #))

loop = (\f_a -> let { _9 = f_a; _6 = f_a } in (\state_a -> ((_6 (_0(# \(new_state_a) -> ((_9 (_0(# \(new_state_a) -> (_12(# _6, _9, (_7(# _6 #)) #)) #))) (_5(# new_state_a #))) #))) state_a)))
