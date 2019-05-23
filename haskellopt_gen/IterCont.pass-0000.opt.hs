-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module IterCont (loop,dont) where

import GHC.Num

_0(# f #) = (\new_state -> {-P-}(f(new_state)))

_1(# s #) = (((GHC.Num.+) s) 1)

_2 = (\k -> (\s' -> (_1(# s' #))))

_3(# new_state #) = new_state

_5(# f'2 #) = f'2

_7(# state #) = state

_8(# f'3, f'4, f'5 #) = (((_5(# f'3 #)) (_0(# {-A-}\(new_state) -> (_8(# f'4, f'5, (_5(# f'3 #)) #)) #))) (_3(# new_state #)))

dont = (_1(# 0 #))

loop = (\f' -> let { _6 = f'; _4 = f' } in (\state' -> ((_4 (_0(# {-A-}\(new_state) -> ((_6 (_0(# {-A-}\(new_state) -> (_8(# _4, _6, (_5(# _4 #)) #)) #))) (_3(# new_state #))) #))) state')))
