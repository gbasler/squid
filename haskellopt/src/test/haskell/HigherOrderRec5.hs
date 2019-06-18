-- module Main where
module HOR5 where

-- import System.Exit

-- main = return ()

-- rec0  f = f (\x -> rec0 f x)
-- rec0_0 = rec0 (\k -> id)
-- rec0_0_ = rec0 (\k s -> s)
-- rec0_1 = rec0 (\k s -> s + 1)


-- We used to get diverging scheduling as soon as rec0 was used more than once...
rec0 f = f (rec0 f)
rec0_0 = rec0 (\k0 -> id)
rec0_1 = rec0 (\k1 -> id)

-- FIXME scheduling diverges!
-- The graph used to duplicate (unroll) recursion with every new usage of the function!
rec1 f1 = f1 (rec1 f1)
rec1_0 = rec1 (\k0 -> id)
rec1_1 = rec1 (\k1 -> id)
rec1_2 = rec1 (\k2 -> id)
rec1_3 = rec1 (\k3 -> id)
-- rec1_4 = rec1 (\k4 -> id)

