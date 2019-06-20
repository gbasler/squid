module HOR2 where
-- module Main where

-- import System.Exit

-- main = return ()


rec0' f = f (\x -> rec0 f x)  -- used to break with SOF when enabled
rec0 f = f (\x -> rec0 f x)

rec0_0 = rec0 (\k -> id)  -- used to break with SOF when enabled
-- rec0_0_ = rec0 (\k s -> s)
rec0_1 = rec0 (\k s -> s + 1)
rec0_2 = rec0 (\k s -> s * 2)
-- rec0_3 = rec0 (\k s -> k (s + 1)) -- TODO try


-- Minimized example of lack of reduction:
rec1 f = f (\x -> rec1 f x)
-- rec1_1 = rec1 id
rec1_1 = rec1 (\k -> k) -- FIXME not reduced
-- rec1_1 = rec1 (\k s -> k s) -- FIXME not reduced
-- rec1_1 s0 = rec1 (\k s -> k s) s0 -- FIXME even less reduced!


-- I move the problematic example with local recursion to HigherOrderRecLocal.hs


-- -- This seems to reduce and work, but it currently suffers from scope extrusion:
-- rec3 f x = x : f (x + 1)
-- rec3_0 = rec3 rec3_0
-- -- take 3 (rec3_0 1) == [1,2,3]


