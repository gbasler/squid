module IterCont where

loop f state =
  f (\new_state -> loop f new_state) state

dont = loop (\k s -> s + 1) 0
