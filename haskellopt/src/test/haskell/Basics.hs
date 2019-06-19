module Basics where


-- Local shared work

-- FIXME nesting
foo :: Int -> Int
foo x =
  let tmp = x * 2
  in tmp + tmp


-- Nested calls

f :: Int -> Int
f x = x * x

-- TODO test these with constant folding rewrite
fTest0 = f 11 * f 22
fTest1 = f (f 33)
fTest2 = f 44 + f (f 55)
fTest3 = f (f 66) * f (f 77)
fTest4 = f (f 66) + f (f 77)

g :: Int -> Int -> Int
g x y = x * y

gTest0 = g (g 2 3) 4
gTest1 = g 4 (g 2 3)
gTest2 = g (g 2 3)
gTest3 = g (g 2 3) 4
gTest4 = g (g 2 3) (g 4 5)

gTest5 =
  let k = \z -> g (g 11 z) (g z 22)
  in k(30) + k(40)

gTest6 =
  let k = \y -> \x -> g x y
  in k 11 (k 33 44)






