module Main where

import System.Exit

main =
  let n3 = take (count 3) nats in
  if n3 == [0,1,2,3,4,5,6]
  then exitSuccess else do { print n3; exitFailure }

loop f state = rec state where
  rec s = f rec s

-- count = loop (\k s -> if s > 0 then k (s - 1) + k (s `div` 2) else 0) 5 -- FIXME triggers unhandled sched case; makes interpreter loop!
count n = loop (\k s -> let x = k (s - 1) in if s > 0 then x + x + 1 else 0) n

nats = loop (\k s -> s : k (s + 1)) 0
