module IterCont where

loop0 f = f (loop0 f)

nats0 =
    loop0 (\k s -> s : k (s + 1)) 0

nats0_5 = take 5 nats0

loop1 f state =
    rec state where
    -- rec st = f (\new_st -> rec new_st) st
    rec st = f rec st

nats1 =
    loop1 (\k s -> s : k (s + 1)) 0

-- TODO impl pattern matching
-- (count, nats) =
--     ( loop (\k s -> k s) 0
--     -- , loop (\k' s' -> k' s') 0
--     , loop (\k' -> id) 0 -- FIXME SOF
--     )

-- count n = loop (\k s -> let x = k (s - 1) in if s > 0 then x + x + 1 else 0) n
