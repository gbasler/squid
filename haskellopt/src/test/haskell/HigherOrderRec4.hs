module Main where

import System.Exit

main = return ()

rec0' = (rec0 id)
rec0 f = f (\x -> rec0 f x) -- FIXME seems like CSE does something unhygienic here
rec0_0 = rec0 (\k -> id)
