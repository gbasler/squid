-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 107; Boxes: 24; Branches: 25
-- Apps: 9; Lams: 1; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Motiv (isJust,pgrm) where

import GHC.Maybe
import GHC.Num
import GHC.Types

isJust = (\ds -> (case ds of {Nothing -> GHC.Types.False; Just arg0 -> GHC.Types.True}))

pgrm = (((GHC.Num.+) (((GHC.Num.*) 2) 1)) (((GHC.Num.+) 0) 1))
