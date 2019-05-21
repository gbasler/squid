module Main where

import System.Exit

main = return ()

-- rec0' f = f (\x -> rec0 f x)  -- FIXME breaks when enabled
rec0  f = f (\x -> rec0 f x)

-- rec0_0 = rec0 (\k -> id)  -- FIXME breaks when enabled
-- rec0_0 = rec0 (\k s -> s)
rec0_1 = rec0 (\k s -> s + 1)
rec0_2 = rec0 (\k s -> s * 2)
