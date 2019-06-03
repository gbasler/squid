module Main where

import System.Exit

main = return ()

rec0' = (rec0 id) -- note that this id will get duplicated, because we don't try to avoid the duplication of constants
rec0 f = f (\x -> rec0 f x)
rec0_0 = rec0 (\k -> id)
