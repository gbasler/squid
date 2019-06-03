-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 92; Boxes: 31; Branches: 24
-- Apps: 14; Lams: 7; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module IterCont (loop,count,nats) where

import GHC.Num

count = (let{-rec-} _2 = (_0(# (\k -> (\s -> (k (_1(# s #))))), {-A-}\(new_state') -> _2, (_3(# (_1(# (let{-rec-} _4 = (_3(# (_1(# _4 #)) #)) in _4) #)) #)) #)) in _2)

_0(# f'2, f'3, state' #) = ((f'2 (_5(# f'3 #))) state')

_1(# s'2 #) = (((GHC.Num.+) s'2) 1)

_3(# new_state #) = new_state

loop = (\f -> (\state -> ((f (let{-rec-} _6 = (_5(# {-A-}\(new_state') -> ((f _6) state) #)) in _6)) state)))

_5(# f' #) = (\new_state' -> {-P-}(f'(new_state')))

nats = (_7(# (_7(# (let{-rec-} _8 = (_0(# (\k' -> (\s' -> (_7(# (k' (((GHC.Num.+) s') 1)), s' #)))), {-A-}\(new_state') -> _8, 0 #)) in _8), 0 #)), 0 #))

_7(# k'2, s'3 #) = (((:) s'3) k'2)
