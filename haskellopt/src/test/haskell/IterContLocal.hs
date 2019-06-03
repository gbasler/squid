-- Like IterCont, but with a local definition of `loop`
module IterCont where

(count',nats') =
    ( loop (\k s -> k (s + 1)) 0
    , loop (\k s -> s : k (s + 1)) 0
    ) where
        loop f state =
            f (\new_state -> loop f new_state) state
