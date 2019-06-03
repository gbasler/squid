-- Like IterCont, but with usages
module IterCont where

loop f state =
  f (\new_state -> loop f new_state) state  --(\res -> res)

-- dont = loop (\k s -> s + 1) 0

-- count = loop (\k s -> k (s + 1)) 0 -- FIXME SOF when enabled...

nats = loop (\k s -> s : k (s + 1)) 0

-- sum_down n = loop (\k s -> s + (if s > 0 then k (s - 1) else 0)) n

-- test0 = sum_down 10 * sum_down 20

