-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  69
-- Incl. one-shot:   0
-- Case reductions:  0
-- Field reductions: 0
-- Case commutings:  0
-- Total nodes: 2946; Boxes: 1232; Branches: 1330
-- Apps: 136; Lams: 74

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Church (zero_x_three,two_x_zero,two_x_three,mult,two_p_three,plus,three,three_id,two_id,two,one_id,one,zero,_I) where



zero_x_three = \s -> \z -> z

two_x_zero = \s -> \z -> z

two_x_three = \s -> \z -> s (s (s (s (s (s z)))))

mult = \m -> \n -> m (\n' -> \s -> \z -> n s (n' s z)) (\s' -> \z' -> z')

two_p_three = \s -> \z -> s (s (s (s (s z))))

plus = \m -> \n -> \s -> \z -> m s (n s z)

three = \s -> \z -> s (s (s z))

three_id = \z -> z

two_id = \z -> z

two = \s -> \z -> s (s z)

one_id = \z -> z

one = \s -> \z -> s z

zero = \s -> \z -> z

_I = \x -> x
