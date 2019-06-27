module Main where

import Criterion.Main

main = defaultMain [
  bgroup "sumnats" [
      bench "10"  $ whnf sumnats 10
    , bench "500"  $ whnf sumnats 500
    , bench "10000" $ whnf sumnats 10000
  ]] where
    sumnats n = sum (take (count n) nats)
    count n = loop (\k s -> if s > 0 then k (s - 1) + 1 else 0) n
    nats = loop (\k s -> s : k (s + 1)) 0
    loop f state = rec state where
        rec state = f rec state

