module Main where

import System.Exit

main = return ()

rec0  f = f (\x -> rec0 f x)

rec0_0 = rec0 (\k -> id)
rec0_0_ = rec0 (\k s -> s)
rec0_1 = rec0 (\k s -> s + 1)
