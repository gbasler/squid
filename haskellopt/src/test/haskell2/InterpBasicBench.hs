module Main where

-- ghc -O2 -fforce-recomp -ddump-to-file -ddump-prep -dsuppress-module-prefixes -dsuppress-idinfo InterpBasic.hs && ./InterpBasic
-- ghc -O2 -fforce-recomp -ddump-to-file -ddump-prep -dsuppress-module-prefixes -dsuppress-idinfo InterpBasic.pass-0000.opt.hs && ./InterpBasic.pass-0000.opt 

import Criterion.Main

-- src = [Just (Just 3), Nothing, Just (Just 5), Nothing, Just Nothing]
-- src = [Nothing, Just Nothing]
src = [Nothing, Just (Just 2), Just Nothing]

k = -1000*100

main = do
  -- print (test (-15))
  defaultMain [
    bgroup "interp" [
        bench "normal"  $ whnf test k
      -- , bench "execute"  $ whnf test2 src
      ]
    ]
  where
  test x = execute src x
  test2 s = execute s k
  execute :: [Maybe (Maybe Int)] -> Int -> Int
  execute pgm x = run pgm x where
    run [] x = run pgm x
    run (instr : rest) x =
      case instr of
            Nothing -> run rest (x+1)
            Just Nothing -> if x < 0 then run rest x else 0
            Just (Just n) -> (run rest x) + n



---- Older prototypes:


-- execute :: [Maybe (Maybe ())] -> Int -> [Int]
-- execute pgm x = run pgm x where
--   run [] x = []
--   run (instr : rest) x =
--     let x' = case instr of
--           Nothing -> (x+1)
--           Just Nothing -> x
--           -- _ -> err()
--           _ -> -1
--     in x' : run rest x

-- execute :: [Maybe (Maybe ())] -> Int -> [Int]
-- execute pgm x = run pgm x where
--   run [] x = []
--   run (instr : rest) x =
--     case instr of
--           Nothing -> (x+1) : run rest x
--           Just Nothing -> x : run rest x
--           -- _ -> -1
--           _ -> run pgm x

-- src = [(iAddX,-1),(iGoto,0)]

-- -- src = [(0,-1),(2,0)]
-- src = [Just Nothing]
-- -- src = [Nothing, Just Nothing]
-- -- src = [Nothing, Just Nothing, Nothing, Nothing, Nothing]
-- -- src = [Nothing, Just Nothing, Nothing, Nothing, Nothing, Just Nothing, Nothing, Nothing]

-- k = -1000*100

-- main = do
--   print (test 15)
--   defaultMain [
--     bgroup "interp" [
--         bench "normal"  $ whnf test k
--       , bench "execute"  $ whnf test2 src
--       ]
--     ]
--   where
--   test x = sum $ take (-k) $ execute src x
--   test2 s = sum $ take (-k) $ execute s k

