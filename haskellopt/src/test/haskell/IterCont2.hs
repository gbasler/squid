-- Like IterCont, but with more usages
module Main where

import System.Exit

main =
  if (take 3 nats) == [0,1,2]
  -- then exitSuccess else exitFailure
  then exitSuccess else do { print $ take 3 nats; exitFailure }

loop f state =
  f (\new_state -> loop f new_state) state

-- dont = loop (\k s -> s + 1) 0  -- TODO put it back

-- count = loop (\k s -> k (s + 1)) 0  -- TODO put it back
count = loop (\k -> id) 0

-- FIXME wrong, (take 3 nats) == [0,0,0]
nats = loop (\k s -> s : k (s + 1)) 0

-- sum_down n = loop (\k s -> s + (if s > 0 then k (s - 1) else 0)) n

-- test0 = sum_down 10 * sum_down 20

