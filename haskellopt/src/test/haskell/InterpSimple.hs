module InterpSimple where
-- module Main where
--   main = print test
-- import System.Exit
-- main = return ()


-- currently we don't partially evaluate eqString
-- test = run pgm pgm 123 where
--   pgm = ["X++","X**"]
--   run pgm pgmtail x = case pgmtail of
--     instr : rest ->
--       case instr of
--         "X++" -> run pgm rest (x+1)
--         "X**" -> run pgm rest (x*2)
--     [] -> x

test = run pgm pgm 123 where
  -- pgm = []
  -- pgm = [True]
  pgm = [True,False]
  run pgm pgmtail x = case pgmtail of
    instr : rest ->
      case instr of
        True -> run pgm rest (x+1)
        False -> run pgm rest (x*2)
    [] -> x

