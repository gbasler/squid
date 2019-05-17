module HigherOrder where


-- Higer-Order Functions

hoH :: (Int -> Int) -> Int
hoH f = f 2 * f 3

hoHTest3 = (hoH (1+)) + (hoH (2*))
hoHTest4 = (hoH (+1)) + (hoH (*2))
hoHTest5 = (hoH (\x -> x * hoH (\y -> x - y)))
-- hoHTest5 = (hoH (\x -> x * hoH (\_ -> x))) -- simpler, but exhibit the same general trickiness as above

---- TODO
----h :: (forall )
----h f = f f

