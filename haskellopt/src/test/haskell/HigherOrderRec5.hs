-- module Main where
module HOR5 where

-- import System.Exit

-- main = return ()

-- rec0  f = f (\x -> rec0 f x)
-- rec0_0 = rec0 (\k -> id)
-- rec0_0_ = rec0 (\k s -> s)
-- rec0_1 = rec0 (\k s -> s + 1)


-- FIXME diverging scheduling as soon as rec0 is used more than once
rec0 f = f (rec0 f) -- enough to raise bug
rec0_0 = rec0 (\k0 -> id)
rec0_1 = rec0 (\k1 -> id)
-- FIXME graph duplicates (unrolls) recursion with every new usage of the function!
-- rec0_2 = rec0 (\k2 -> id)
-- rec0_3 = rec0 (\k3 -> id)
-- rec0_4 = rec0 (\k4 -> id)

