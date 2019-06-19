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


-- FIXME diverges cf {-rec-}
-- rec2 f = rec where
--   rec = f (\x -> rec x)
-- rec2_0 = rec2 (\k -> k)

