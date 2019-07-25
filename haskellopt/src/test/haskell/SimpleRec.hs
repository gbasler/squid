module SimpleRec where

nats21 = nats 21 where
  nats n = n : nats (n + 1)

