-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 38; Boxes: 11; Branches: 9
-- Apps: 4; Lams: 5; Unreduced Redexes: 1

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module IterCont (loop,dont) where

import GHC.Num

dont = (((GHC.Num.+) 0) 1)

loop = (\f -> (\state -> (_0(# state, (_1(# f #)) #))))

_0(# state', f'2 #) = (f'2 state')

_1(# f' #) = (f' (\new_state -> (_0(# new_state, (_1(# f' #)) #))))
