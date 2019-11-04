module Main where

import Criterion.Main

-- instructions:
-- iAddX = 0
-- iAddY = 1
-- iGoto = 2

-- src = [(iAddX,-1),(iGoto,0)]
-- src = [(0,-1),(2,0)]
src = [(0,-1)]

k = 1000*100

main = defaultMain [
  bgroup "interp" [
      bench "normal"  $ whnf (execute src) k
      -- bench "normal"  $ whnf test k
    -- , bench "fully_lifted"  $ whnf test_fully_lifted k
    -- , bench "partial"  $ whnf test_partial k
    ]
  ] where
  execute pgm x = run pgm x 123 where
    run [] x y = y
    run (instr : rest) x y =
      case instr of
        (0, n) -> run rest (x+n) y
        (1, n) -> run rest x (y+n)
        (2, n) -> if x > 0 then run (drop n pgm) x y else run rest x y
        _ -> -1
