-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 96; Boxes: 36; Branches: 28
-- Apps: 13; Lams: 7; Unreduced Redexes: 4

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (rec0_1,main,rec0_0,rec0_0_,rec0) where

import GHC.Base
import GHC.Num
import GHC.TopHandler

main = (GHC.TopHandler.runMainIO (GHC.Base.return ()))

rec0 = (\f -> (_0(# f, {-A-}\(x) -> ((f (_1(# {-A-}\(x) -> ((f (let{-rec-} _2 = (_1(# {-A-}\(x) -> ((f _2) x) #)) in _2)) x) #))) x) #)))

_0(# f'2, f'3 #) = (f'2 (_1(# f'3 #)))

_1(# f' #) = (\x -> {-P-}(f'(x)))

rec0_0 = GHC.Base.id

rec0_0_ = (_0(# (\k -> (\s -> s)), {-A-}\(x) -> x #))

rec0_1 = (\s' -> (((GHC.Num.+) s') 1))
