module HigherOrder where


-- Higer-Order Functions

h :: (Int -> Int) -> Int
h f = f 2 * f 3

hTest3 = (h (1+)) + (h (2*))
hTest4 = (h (+1)) + (h (*2))
hTest5 = (h (\x -> x * h (\y -> x - y)))
-- hTest5 = (h (\x -> x * h (\_ -> x))) -- simpler, but exhibit the same general trickiness as above


g f x = f x + f 3

p1 = (+1)

gTest0 = g p1 2
gTest1 = g p1 (g p1 2)


i :: ((Int -> Int) -> Int) -> Int -> Int
i f x = f (+1) + f (*2)

iTest0 = i (\f -> f 11 + f 22)
iTest1 = i (\f -> f 11 + f 22) 66
f0 = \f -> f 11 + f 22
iTest2 = i f0 (i f0 77)

-- minimized version of abve that exposes two sibling calls to the same def:
-- -- i f = f (+1) + f (*2)
-- i f = f (1+) + f (2*)
-- iTest0 = i (\f -> 0)


ls1 = (lol 11 22 +)

{-# NOINLINE lol #-}
lol x y = x + y


---- TODO
----h' :: (forall ...) -> ...
----h' f = f f

