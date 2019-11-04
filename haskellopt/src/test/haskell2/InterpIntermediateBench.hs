module Main where

import Criterion.Main

-- execute pgm x = run pgm x (-123) where
--   run [] x y = y
--   run (instr : rest) x y =
--     case instr of
--       0 -> run rest (x+1) y
--       -- 1 -> run rest x (y+1)
--       2 -> if x > 0 then run pgm x y else run rest x y
--       _ -> error "oops"

execute pgm x = run pgm x 0 where
  run [] x y = y
  run (instr : rest) x y =
    case instr of
      0 -> run rest (x+1) (y+1)
      -- 1 -> run rest x (y+1)
      2 -> if x > 0 then run pgm x y else run rest x y
      _ -> error "oops"

-- src = [(iAddX,-1),(iGoto,0)]
-- src = [(0,-1),(2,0)]
src = [0,2]

k = -1000*100

main = defaultMain [
  bgroup "interp" [
      bench "normal"  $ whnf (execute src) k
    ]
  ]
