module Explosive where

-- a0 :: Int -> Int
-- a0 x = x + x + x
-- a1 y = a0 y *  a0 (y + 1) *  a0 (y + 2)
-- a2 z = a1 z ^  a1 (z * 2) ^  a1 (z * 3)


a0 :: (Int -> Int) -> Int
a0 f = f 0 + f 1 + f 2

a1 g = a0 g *  a0 g *  a0 g

-- a2 = a1 (*1) ^ a1 (*2) ^  a1 (* 3)
-- a2 = a1 (a3) ^ a1 (a3) ^  a1 (a3)
a2 = a1 (a0 a3 +) ^ a1 (a0 a3 *) ^  a1 (a0 a3^)

a3 x = 1 - x - 1



b0 :: (Int -> Int) -> Int -> Int
b0 f x = f (f (f x))

-- b1 = b0 (\y -> b0 (b0 (\z -> b0 (+1) z)) y) -- FIXME scheduler SOF
-- b1 = b0 (\y -> b0 (b0 (+1)) y)
-- b1 = b0 (\y -> b0 (b0 (+1)) (b0 (*2) y))
-- b1 = b0 (\y -> b0 (b0 (+1)) (b0 (*2) (b0 (^2) y)))

b1 g = b0 (\y -> b0 (b0 g) (b0 g (b0 g y)))

-- b2 = b1 (\z -> b1 id z) -- FIXME scheduler SOF
-- b2 = b1 (b0 (b0 (b0 id))) -- FIXME scheduler SOF
-- b2 = b1 (b0 (b0 id)) -- FIXME scheduler SOF
-- b2 = b1 (b0 id) -- FIXME scheduler SOF

