-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module BuildFoldr (foldr',build) where



build = (\g -> ((g (:)) []))

foldr' = (\c -> (\n -> (\ds -> (_0(# ds, n, c, c, n #)))))

_0(# ds', n', c', c'2, n'2 #) = (case ds' of {[] -> n'; (:) arg0 arg1 -> ((c' arg0) (_0(# arg1, n'2, c'2, c', n' #)))})
