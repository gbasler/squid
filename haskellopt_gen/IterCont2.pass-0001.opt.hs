-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 48; Boxes: 14; Branches: 9
-- Apps: 9; Lams: 5; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module IterCont (loop,nats) where

import GHC.Num

loop = (\f -> (\state -> ((f (_0(# {-A-}\(new_state) -> f #))) state)))

_0(# f' #) = (\new_state -> (_2(# (_3(# new_state #)), {-P-}(f'(new_state)), {-A-}\(new_state) -> (_5(# {-P-}(f'(new_state)) #)) #)))

nats_sub = (\k -> (\s -> (_1(# (k (_4(# s #))), s #))))
nats = (_1(# (_2(# (_3(# (_4(# 0 #)) #)), nats_sub, {-A-}\(new_state) -> (_5(# nats_sub #)) #)), 0 #))

_1(# k', s'2 #) = (((:) s'2) k')

_4(# s' #) = (((GHC.Num.+) s') 1)

_2(# state', f'3, f'4 #) = ((f'3 (_0(# f'4 #))) state')

_3(# new_state' #) = new_state'

_5(# f'2 #) = f'2
