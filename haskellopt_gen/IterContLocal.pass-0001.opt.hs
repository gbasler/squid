-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 23; Boxes: 6; Branches: 2
-- Apps: 5; Lams: 1; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (main,rec,nats) where

import GHC.Num
import GHC.TopHandler
import System.Exit

main = (GHC.TopHandler.runMainIO System.Exit.exitSuccess)

nats = (_0(# 0 #))

_0(# st #) = (((:) st) (_0(# (((GHC.Num.+) st) 1) #)))

rec = (\st' -> (_0(# st' #)))
