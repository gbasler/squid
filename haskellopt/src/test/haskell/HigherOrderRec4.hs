module Main where

import System.Exit

main = return ()

rec0' = (rec0 id)
rec0 f = f (\x -> rec0 f x)
rec0_0 = rec0 (\k -> id)
