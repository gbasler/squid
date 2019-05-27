-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 38; Boxes: 18; Branches: 16
-- Apps: 26; Lams: 5; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module IterCont (loop,dont) where

import GHC.Num

dont = (_0(# 0 #))

_0(# s #) = (((GHC.Num.+) s) 1)

loop = (\f' -> let { _3 = f'; _1 = f' } in (\state -> ((_1 (_2(# {-A-}\(new_state) -> ((_3 (_2(# {-A-}\(new_state) -> (_4(# _1, _3, (_5(# _1 #)) #)) #))) (_6(# new_state #))) #))) state)))

_2(# f #) = (\new_state -> {-P-}(f(new_state)))

_4(# f'3, f'4, f'5 #) = (((_5(# f'3 #)) (_2(# {-A-}\(new_state) -> (_4(# f'4, f'5, (_5(# f'3 #)) #)) #))) (_6(# new_state #)))

_5(# f'2 #) = f'2

_6(# new_state #) = new_state

_7 = (\k -> (\s' -> (_0(# s' #))))

_8(# state' #) = state'
