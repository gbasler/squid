module Lists where

ls0 = [1,2,3,4]
ls1 = map (+ lol 11 22) ls0

{-# NOINLINE lol #-}
lol x y = x + y

mutrec0 = let
    a = 1 : b
    b = 2 : a
  in take 2 a

--TODO
--mutrec1 = let
--    a x = 1 : b (x + 1)
--    b y = 2 : a (y * 2)
--  in take 2 (a 0)

--TODO
--toprec0 x = x : toprec1 (x + 1)
--toprec1 y = y : toprec0 (y + 1)

main = do
    print $ sum ls1
