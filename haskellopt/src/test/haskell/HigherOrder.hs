module HigherOrder where


-- Higer-Order Functions

h :: (Int -> Int) -> Int
h f = f 2 * f 3

hTest3 = (h (1+)) + (h (2*))
hTest4 = (h (+1)) + (h (*2))
hTest5 = (h (\x -> x * h (\y -> x - y)))
-- hTest5 = (h (\x -> x * h (\_ -> x))) -- simpler, but exhibit the same general trickiness as above

---- TODO
----h' :: (forall ...) -> ...
----h' f = f f

