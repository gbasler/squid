module Lists where

ls0 = [1,2,3,4]
ls1 = map (+ lol 11 22) ls0

{-# NOINLINE lol #-}
lol x y = x + y

main = do
    print $ sum ls1
