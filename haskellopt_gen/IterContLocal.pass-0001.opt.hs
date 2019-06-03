-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 93; Boxes: 32; Branches: 24
-- Apps: 14; Lams: 7; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module IterCont (loop,count,ds,nats) where

import GHC.Num

count = (let{-rec-} _1 = (_0(# {-A-}\(new_state') -> _1, (\k -> (\s -> (k (_2(# s #))))), (_3(# (_2(# (let{-rec-} _4 = (_3(# (_2(# _4 #)) #)) in _4) #)) #)) #)) in _1)

_0(# f'3, f'4, state'2 #) = ((f'4 (_10(# f'3 #))) state'2)

_2(# s' #) = (((GHC.Num.+) s') 1)

_3(# new_state #) = new_state

ds = (_5(# (_6(# (let{-rec-} _7 = (_0(# {-A-}\(new_state') -> _7, _8, _9 #)) in _7), _9 #)), _9 #))

_5(# f'5, state'3 #) = (_13(# state'3, f'5 #))

_6(# f'2, state' #) = (_13(# state', f'2 #))

_8 = (\k'2 -> (\s'3 -> (_13(# s'3, (k'2 (((GHC.Num.+) s'3) 1)) #))))

_9 = 0

loop = (\f -> (\state -> ((f (let{-rec-} _11 = (_10(# {-A-}\(new_state') -> ((f _11) state) #)) in _11)) state)))

_10(# f' #) = (\new_state' -> {-P-}(f'(new_state')))

nats = (_5(# (_6(# (let{-rec-} _12 = (_0(# {-A-}\(new_state') -> _12, _8, _9 #)) in _12), _9 #)), _9 #))

_13(# s'2, k' #) = (((:) s'2) k')
