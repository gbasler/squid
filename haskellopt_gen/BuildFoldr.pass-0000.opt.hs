-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module BuildFoldr (foldr',build) where



_0(# ds, n, c, c', n' #) = (case ds of {[] -> n; (:) arg0 arg1 -> ((c arg0) (_0(# arg1, n', c', c, n #)))})

build = (\g -> ((g (:)) []))

foldr' = (\c'2 -> (\n'2 -> (\ds' -> (_0(# ds', n'2, c'2, c'2, n'2 #)))))
