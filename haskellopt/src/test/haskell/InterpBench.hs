module Main where

-- import System.Exit
-- main = return ()

import Criterion.Main

k = 1000*100

main = defaultMain [
  bgroup "interp" [
      bench "normal"  $ whnf test k
    -- , bench "fully_lifted"  $ whnf test_fully_lifted k
    , bench "partial"  $ whnf test_partial k
    ]
  ]

execute pgm x = run pgm pgm x 123

-- run pgm pgmtail x y =
--   let instr : rest = pgmtail in
--     case instr of
--       ("X+=", n) -> run pgm rest (x+n) y
--       -- ("Y+=", n) -> run pgm rest x (y+n)
--       -- ("GOTO", l) -> if y == 0 then run pgm (drop l pgm) x y else run pgm rest x y
--       ("GOTO", l) -> if x > 0 then run pgm (drop l pgm) x y else run pgm rest x y

run pgm pgmtail x y = case pgmtail of
  instr : rest ->
    case instr of
      ("X+=", n) -> run pgm rest (x+n) y
      -- ("Y+=", n) -> run pgm rest x (y+n)
      -- ("GOTO", l) -> if y == 0 then run pgm (drop l pgm) x y else run pgm rest x y
      ("GOTO", l) -> if x > 0 then run pgm (drop l pgm) x y else run pgm rest x y
  [] -> y

src = [("X+=",-1),("GOTO",0)]

test x = execute src x

-- test_fully_lifted x = execute src x where
--   execute pgm x = run pgm pgm x 123
--   run pgm pgmtail x y = case pgmtail of
--     instr : rest ->
--       case instr of
--         ("X+=", n) -> run pgm rest (x+n) y
--         -- ("Y+=", n) -> run pgm rest x (y+n)
--         -- ("GOTO", l) -> if y == 0 then run pgm (drop l pgm) x y else run pgm rest x y
--         ("GOTO", l) -> if x > 0 then run pgm (drop l pgm) x y else run pgm rest x y
--     [] -> y
--   src = [("X+=",-1),("GOTO",0)]

test_partial x = run_partial x 123

run_partial x y = if x > 0 then run_partial (x-1) y else y

