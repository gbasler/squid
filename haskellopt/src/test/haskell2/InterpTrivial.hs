module InterpTrivial where

test0 = run pgm where
  -- pgm = [(),()]
  -- pgm = [(),(),()]
  -- pgm = [(),(),(),()]
  -- pgm = [(),(),(),(),()]
  pgm = [(),(),(),(),(),(),(),(),()]
  run [] = 0
  -- run (() : rest) = run rest + 1
  run (unit : rest) = run rest + 1

test1 = run pgm 123 where
  pgm = [(),()]
  -- pgm = [(),(),()] -- FIXME sch: not in scope
  -- pgm = [(),(),(),()]
  -- pgm = [(),(),(),(),()]
  run [] x = x
  run (unit : rest) x = run rest x + 1

test2 = run pgm 123 where
  pgm = [(),()]
  -- pgm = [(),(),()] -- FIXME sch: comparison method
  -- pgm = [(),(),(),()]
  -- pgm = [(),(),(),(),()]
  run [] x = x
  run (unit : rest) x = run rest (x + 1)

test3 = run pgm 123 where
  -- pgm = [(),()]
  pgm = [(),(),()]
  -- pgm = [(),(),(),()] -- FIXME sch bug
  run pgmtail x = case pgmtail of
    instr : rest ->
      case instr of
        () -> run rest (x+1)
    [] -> x
