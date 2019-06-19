-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 51; Boxes: 16; Branches: 10
-- Apps: 8; Lams: 3; Unreduced Redexes: 1

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (main,nats) where

import GHC.Num
import GHC.TopHandler
import System.Exit

main = (GHC.TopHandler.runMainIO System.Exit.exitSuccess)

nats = (let{-rec-} _1 = (_0(# _1, 0 #)) in _1)

_0(# f, st #) = (((:) st) f)
