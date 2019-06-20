-- Like IterContLocal2, but with `loop` used twice to make two recursive defs
-- module IterCont where
module Main where

import System.Exit

-- main = exitSuccess
main =
    let n3 = take 5 nats in
    if n3 == [0,1,2,3,4] && count == 3
    then exitSuccess else do { print n3; exitFailure }

count :: Int -- -> Int -- TODO
nats :: [Int]
(count, nats) =
    -- ( loop (\k s -> k (s + 1)) 0
    ( loop (\k s -> if s > 0 then k (s - 1) + 1 else 0) 3 -- TODO move `3` to the call-site (currently causes scope extrusion)
    , loop (\k s -> s : k (s + 1)) 0
    ) where
    -- -- A version of loop that doesn't reduce as much because of the threading of `f`:
    -- loop f state =
    --     f (\new_state -> loop f new_state) state
    -- A version of loop that reduces fully:
    loop f state = rec state where
        rec state = f (\new_state -> loop f new_state) state  -- FIXME later divergence when we remove outer `f` call

