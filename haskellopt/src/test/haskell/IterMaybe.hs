module Main where

import System.Exit
import GHC.List

main =
  let n3 = nats (count 5) in
  if n3 == [0,1,2,3,4]
  then exitSuccess else do { print n3; exitFailure }

loop f state = rec state where
  rec st0 = case f st0 of { Just st1 -> rec st1; Nothing -> st0 }

count :: Integer -> Integer
count n = loop (\cs -> if cs < n then Just (cs + 1) else Nothing) 0

nats :: Integer -> [Integer]
nats m = loop (\ns -> if GHC.List.length ns < fromInteger m then Just ((head ns - 1) : ns) else Nothing) [m-1]
