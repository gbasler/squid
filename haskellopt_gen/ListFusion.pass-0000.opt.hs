-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 21; Boxes: 1; Branches: 0
-- Apps: 8; Lams: 1; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module ListFusion (f0) where

import GHC.Base
import GHC.Num
import GHC.Types

f0 = (\ls -> ((GHC.Base.map ((GHC.Num.+) (GHC.Types.I# 1#))) ((GHC.Base.map ((GHC.Num.*) (GHC.Types.I# 2#))) ls)))
