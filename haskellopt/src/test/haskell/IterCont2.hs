-- Like IterCont, but with a slightly different loop (pass `f` in each recursive call)
module Main where

import System.Exit

main =
  -- let n3 = take 5 nats in
  let n3 = take count nats in
  if n3 == [0,1,2,3,4]
  -- then exitSuccess else exitFailure
  then exitSuccess else do { print n3; exitFailure }

loop f state =
  f (\new_state -> loop f new_state) state
-- loop f state = rec state where
--   rec st = f (rec st)

-- dont = loop (\k s -> s + 1) 0  -- TODO put it back

count = loop (\k s -> if s > 0 then k (s - 1) + 1 else 0) 5
-- count = loop (\k s -> k (s + 1)) 0 -- FIXME stack overflow!
-- count = loop (\k -> id) 0

-- FIXME wrong, (take 3 nats) == [0,0,0]
nats = loop (\k s -> s : k (s + 1)) 0

-- sum_down n = loop (\k s -> s + (if s > 0 then k (s - 1) else 0)) n

-- test0 = sum_down 10 * sum_down 20

