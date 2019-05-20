-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module BuildFoldr (foldr',build) where



_0(# _1, _2, _3, _4, _5 #) = (case _1 of {[] -> _2; (:) arg0 arg1 -> ((_3 arg0) (_0(# arg1, _5, _4, _3, _2 #)))})

build = (\g_a -> ((g_a (:)) []))

foldr' = (\c_a -> (\n_a -> (\ds_d -> (_0(# ds_d, n_a, c_a, c_a, n_a #)))))
