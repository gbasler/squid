-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 43; Boxes: 13; Branches: 7
-- Apps: 8; Lams: 1; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (main,nats) where

import GHC.Num
import GHC.TopHandler
import System.Exit

main = (GHC.TopHandler.runMainIO System.Exit.exitSuccess)

nats = (let{-rec-} _1 = (_0(# _1, 0 #)) in _1)

_0(# f, st #) = (((:) st) f)
