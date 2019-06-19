-- Like IterCont, but with a local definition of `loop`
-- module IterCont where
module Main where

import System.Exit

main = exitSuccess
-- main =
--     if (take 3 nats) == [0,1,2]
--     -- then exitSuccess else exitFailure
--     then exitSuccess else do { print $ take 3 nats; exitFailure }

-- TODO:
-- (count, nats) =
--     ( loop (\k s -> k (s + 1)) 0
--     , loop (\k s -> s : k (s + 1)) 0
--     ) where
--         loop f state =
--             f (\new_state -> loop f new_state) state

-- TODO:
-- (count, nats) =
--     ( loop (\k s -> k (s + 1)) 0
--     , loop (\k s -> s : k (s + 1)) 0
--     ) where
--     -- loop f state =
--     --     f (\new_state -> loop f new_state) state
--     loop f state = rec state where
--         rec state = (\new_state -> loop f new_state) state

-- FIXME generates inf (+1) loop
-- nats =
--     loop (\k s -> s : k (s + 1)) 0 where
--         loop f state =
--             f (\new_state -> loop f new_state) state

-- Simpler version, to debug
nats =
    -- loop (\k s -> s : k s) 0 where
    loop (\k s -> s : k (s + 1)) 0 where
    loop f state =
        rec state where
        rec st = f (\new_st -> rec new_st) st

-- FIXME SOF
-- (count, nats) =
--     ( loop (\k s -> k s) 0
--     -- , loop (\k' s' -> k' s') 0
--     , loop (\k' -> id) 0 -- FIXME SOF
--     ) where
--         loop f state =
--             f (\new_state -> loop f new_state) state
