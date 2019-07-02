-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 36; Boxes: 8; Branches: 4
-- Apps: 2; Lams: 1; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Main (rec,main) where

import GHC.Classes
import GHC.TopHandler
import GHC.Types
import System.Exit

main = (GHC.TopHandler.runMainIO (case _0 of {False -> System.Exit.exitSuccess; True -> System.Exit.exitFailure}))

_0 = (_1(# (GHC.Classes.not GHC.Types.True) #))

rec = (\st0 -> (_1(# st0 #)))

_1(# st0' #) = (case st0' of {False -> GHC.Types.False; True -> _0})
