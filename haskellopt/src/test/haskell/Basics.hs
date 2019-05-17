module Basics where


-- Local shared work

foo :: Int -> Int
foo x =
  let tmp = x * 2
  in tmp + tmp


-- Nested calls

f :: Int -> Int
f x = x * x

fTest0 = f 11 + f 22
fTest1 = f (f 33)

g :: Int -> Int -> Int
g x y = x * y

gTest0 = g (g 2 3) 4
gTest1 = g 4 (g 2 3)
gTest2 = g (g 2 3) (g 4 5)






