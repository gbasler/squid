-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 118; Boxes: 34; Branches: 24
-- Apps: 18; Lams: 5; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module IterCont (ds,count,nats) where

import GHC.Num

count = (case ds of {(,) arg0 arg1' -> arg0})

ds = (((,) (case ds' of {(,) arg _ -> arg})) (case ds' of {(,) _ arg' -> arg'}))

ds' = (((,) (let{-rec-} _6 = (_3(# (\k' -> (\s'3 -> (k' (_0(# s'3 #))))), {-A-}\(new_state') -> _6, (_4(# (_0(# (let{-rec-} _7 = (_4(# (_0(# _7 #)) #)) in _7) #)) #)) #)) in _6)) (_2(# (_4(# (let{-rec-} _8 = (_1(# (_5(# (_4(# _8 #)) #)) #)) in _8) #)), (_2(# (_5(# (_4(# (let{-rec-} _9 = (_1(# (_5(# (_4(# _9 #)) #)) #)) in _9) #)) #)), (let{-rec-} _10 = (_3(# (\k'2 -> (\s'4 -> (_2(# s'4, (k'2 (_1(# s'4 #))) #)))), {-A-}\(new_state') -> _10, (_4(# (let{-rec-} _11 = (_1(# (_5(# (_4(# _11 #)) #)) #)) in _11) #)) #)) in _10) #)) #)))

nats = (case ds of {(,) arg0' arg1 -> arg1})

_0(# s #) = (((GHC.Num.+) s) 1)

_1(# s' #) = (((GHC.Num.+) s') 1)

_2(# s'2, k #) = (((:) s'2) k)

_3(# f, f', state #) = ((f (\new_state' -> {-P-}(f'(new_state')))) state)

_4(# new_state #) = new_state

_5(# state' #) = state'
