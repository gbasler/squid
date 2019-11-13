module Main where

-- ghc -O2 InterpIntermediateBench.hs && ./InterpIntermediateBench
-- ghc -O2 InterpIntermediateBench.pass-0000.opt.hs && ./InterpIntermediateBench.pass-0000.opt

import Criterion.Main

-- execute pgm x = run pgm x (-123) where
--   run [] x y = y
--   run (instr : rest) x y =
--     case instr of
--       0 -> run rest (x+1) y
--       -- 1 -> run rest x (y+1)
--       2 -> if x > 0 then run pgm x y else run rest x y
--       _ -> error "oops"

-- src = [(iAddX,-1),(iGoto,0)]
-- src = [(0,-1),(2,0)]
src = [0,2]

k = -1000*100

main = do
  -- print (execute src k)
  defaultMain [
    bgroup "interp" [
        bench "normal"  $ whnf (execute src) k
      ]
    ] where
  
  execute pgm x = run pgm x 0 where
    run [] x y = y
    run (instr : rest) x y =
      case instr of
        0 -> run rest (x+1) (y+1)
        -- 1 -> run rest x (y+1)
        2 -> if x > 0 then run pgm x y else run rest x y
        -- _ -> error "oops"
        _ -> err()
    -- err = err -- FIXME diverges (in graph loader?)
    err() = err()
