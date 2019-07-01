module Main where

import System.Exit

main =
  let n5 = take (count 5) nats in
  if n5 == [0,1,2,3,4]
  -- then exitSuccess else exitFailure
  then exitSuccess else do { print n5; exitFailure }

-- loop f state =
--   f (\new_state -> loop f new_state) state
loop f state = rec state where
  rec s = f rec s

-- loop f state = rec state where
--   -- rec state = f (\new_state -> loop f new_state) state
--   rec state = f (rec) state

-- dont = loop (\k s -> s + 1) 0

count n = loop (\k s -> if s > 0 then k (s - 1) + 1 else 0) n
-- count = loop (\k s -> k (s + 1)) 0
-- count = loop (\k s -> k s) 0
-- count = loop (\k -> k) 0
-- count = loop id True

-- nats :: [Int]
nats = loop (\k s -> s : k (s + 1)) 0
-- nats = loop (\k s -> s : k s) 0
-- nats = loop (\k -> k) 0 -- infinite type error
-- nats = loop (\k -> k) -- scope extrusion + infinite type error
-- nats = loop (\k -> id) 0
-- nats = loop (\k -> True) False
-- nats = loop id 0



-- -- this module used to be:

-- module IterCont where

-- loop f state =
--   f (\new_state -> loop f new_state) state

-- dont = loop (\k s -> s + 1) 0
