-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = InitialPhase [Gentle],
--                          inline,
--                          rules,
--                          eta-expand,
--                          no case-of-case}
-- Total nodes: 47; Boxes: 3; Branches: 0
-- Apps: 12; Lams: 4; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module ListFusion (f0) where

import GHC.Base
import GHC.Prim
import GHC.Types

f0 = (\ls -> (GHC.Base.build (\c -> (\n -> (((GHC.Base.foldr ((GHC.Base.mapFB c) (\x -> (case (case x of {I# arg0 -> (GHC.Types.I# (((GHC.Prim.*#) 2#) arg0))}) of {I# arg0' -> (GHC.Types.I# (((GHC.Prim.+#) 1#) arg0'))})))) n) ls)))))
