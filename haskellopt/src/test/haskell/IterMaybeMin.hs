module Main where
-- module IM where

import System.Exit
-- import GHC.List

-- main = print count
-- main =
--   let n3 = nats (count 5) in
--   if n3 == [0,1,2,3,4]
--   then exitSuccess else do { print n3; exitFailure }

-- loop f state = rec state where
--   rec st0 = case f st0 of { Just st1 -> rec st1; Nothing -> st0 }

-- -- count :: Integer -> Integer
-- -- count n = loop (\cs -> if cs < n then Just (cs + 1) else Nothing) 0
-- -- count n = loop (\cs -> if cs < n then Nothing else Nothing) 0
-- count = loop (\cs -> Nothing) 0

-- count = loop (\cs -> Nothing) 0 where
-- count = loop (\cs -> if cs > 10 then Nothing else Just (cs+1)) 0 where
-- main = print (loop (\cs -> if cs then Just (not cs) else Nothing) True) where
main = if (loop (\cs -> if cs then Just (not cs) else Nothing) True) then exitFailure else exitSuccess where -- SIMPLEST VERSION; below, versions with two users of `loop`
-- main = if (loop (\cs -> if cs then Just (not cs) else Nothing) True) && (loop (\ds -> if ds>0 then Just (ds-1) else Nothing) 2 == 0) then exitFailure else exitSuccess where
-- main = if (loop (\ds -> if ds>0 then Just (ds-1) else Nothing) 2 == 0) && (loop (\cs -> if cs then Just (not cs) else Nothing) True) then exitFailure else exitSuccess where
    loop f state = rec state where
        rec st0 = case f st0 of { Just st1 -> rec st1; Nothing -> st0 }

-- nats :: Integer -> [Integer]
-- nats m = loop (\ns -> if GHC.List.length ns < fromInteger m then Just ((head ns - 1) : ns) else Nothing) [m-1]
