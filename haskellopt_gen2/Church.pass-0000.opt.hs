-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  69
-- Incl. one-shot:  0
-- Total nodes: 2946; Boxes: 1232; Branches: 1330
-- Apps: 136; Lams: 74

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Church (zero_x_three,two_x_zero,two_x_three,mult,two_p_three,plus,three,three_id,two_id,two,one_id,one,zero,_I) where



zero_x_three = \s_ε -> \z_ε -> z_ε

two_x_zero = \s_ε' -> \z_ε' -> z_ε'

two_x_three = \s_ε'2 -> \z_ε'2 -> s_ε'2 (s_ε'2 (s_ε'2 (s_ε'2 (s_ε'2 (s_ε'2 z_ε'2)))))

mult = \m_ε -> \n_ε -> m_ε (\n_ε' -> \s_ε'3 -> \z_ε'3 -> n_ε s_ε'3 (n_ε' s_ε'3 z_ε'3)) (\s_ε'4 -> \z_ε'4 -> z_ε'4)

two_p_three = \s_ε'5 -> \z_ε'5 -> s_ε'5 (s_ε'5 (s_ε'5 (s_ε'5 (s_ε'5 z_ε'5))))

plus = \m_ε' -> \n_ε'2 -> \s_ε'6 -> \z_ε'6 -> m_ε' s_ε'6 (n_ε'2 s_ε'6 z_ε'6)

three = \s_ε'7 -> \z_ε'7 -> s_ε'7 (s_ε'7 (s_ε'7 z_ε'7))

three_id = \z_ε'8 -> z_ε'8

two_id = \z_ε'9 -> z_ε'9

two = \s_ε'8 -> \z_ε'10 -> s_ε'8 (s_ε'8 z_ε'10)

one_id = \z -> z

one = \s_ε'9 -> \z' -> s_ε'9 z'

zero = \s_ε'10 -> \z_ε'11 -> z_ε'11

_I = \x_ε -> x_ε
