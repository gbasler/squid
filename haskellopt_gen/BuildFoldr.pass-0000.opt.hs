-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 39; Boxes: 8; Branches: 3
-- Apps: 4; Lams: 4; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module BuildFoldr (foldr',build) where



build = (\g -> ((g (:)) []))

foldr' = (\c -> (\n -> (\ds -> (_0(# ds, c, n #)))))

_0(# ds', c', n' #) = (case ds' of {[] -> n'; (:) arg0 arg1 -> ((c' arg0) (_0(# arg1, c', n' #)))})
