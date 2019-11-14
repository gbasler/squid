module Basics where


-- Local shared work

foo_0 :: Int -> Int
foo_0 x =
  let tmp = x * 2
  in tmp + tmp

-- Local function

foo_1 :: Int -> Int
foo_1 x =
  let f y = y * 2
  in f (x + 1)

-- Variable capture

foo_2 :: Int -> Int
foo_2 x =
  let f y = y * x
  in f (x + 1)

-- All of the above

foo_3 :: Int -> Int
foo_3 x =
  let f y = let tmp = y * x in tmp ^ tmp
  in f (x + 1) - f x

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


-- Strings and characters
-- hello0 = case "hello" of { 'h' : xs -> 'H' : xs } -- TODO Lit patterns
hello1 = case "hello" of { h : xs | h == 'h' -> 'H' : xs }
