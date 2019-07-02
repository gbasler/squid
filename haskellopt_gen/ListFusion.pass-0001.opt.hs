-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 29; Boxes: 1; Branches: 0
-- Apps: 12; Lams: 1; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module ListFusion (f0) where

import GHC.Base
import GHC.Num
import GHC.Types

f0 = (\ls -> let sh = (GHC.Base.map ((GHC.Num.+) (GHC.Types.I# 1#))) in (sh (sh ((GHC.Base.map ((GHC.Num.*) (GHC.Types.I# 2#))) ls))))
