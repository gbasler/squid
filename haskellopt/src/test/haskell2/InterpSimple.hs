module InterpSimple where

-- currently we don't partially evaluate eqString
-- test = run pgm pgm 123 where
--   pgm = ["X++","X**"]
--   run pgm pgmtail x = case pgmtail of
--     instr : rest ->
--       case instr of
--         "X++" -> run pgm rest (x+1)
--         "X**" -> run pgm rest (x*2)
--     [] -> x

-- FIXME propag div due to param cycle
-- test = run pgm pgm 123 where
--   -- pgm = []
--   -- pgm = [True]
--   pgm = [True,False]
--   run pgm pgmtail x = case pgmtail of
--     instr : rest ->
--       case instr of
--         True -> run pgm rest (x+1)
--         False -> run pgm rest (x*2)
--     [] -> x

test = run pgm 123 where
  -- pgm = []
  -- pgm = [True]
  -- pgm = [True,False]
  pgm = [True,False,False]
  -- pgm = [True,False,False,False] -- FIXME sch bug
  -- pgm = [True,False,False,False,True,False] -- FIXME sch bug
  run pgmtail x = case pgmtail of
    instr : rest ->
      case instr of
        True -> run rest (x+1)
        False -> run rest (x*2)
    [] -> x
