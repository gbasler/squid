module Main where
-- module Main(main) where  -- this makes the toplvl version as fast as the lcal one, as expected

import Criterion.Main

values :: [Int]
-- values :: [Integer]
values = [0..6660]

-- Local version below is much faster (<1/3 of the time) in opt mode... due to more list fusion
-- Very strangely, when I switched from `nf` to `whnf` and (,) to (+), the toplvl version became more than twice as slow!

-- bat sf arg = let res = sf (map (+arg) values) in (res * res + 1)
-- foo sf arg = (bat sf arg) + (bat (\ls -> sf (map (\x -> x * 2) ls) )) (arg + 1)
--   -- ^ NOTE: amazingly, changing (arg + 1) to arg made the program fuse like the local version!
-- -- process = foo sum -- Note: this contracted form seems to have a huge impact on perf
-- process a = foo sum a
-- process a = (foo sum a + foo maximum a)

-- -- The version below is as fast as the local one!
-- bat sf arg = let res = sf (map (+arg) values) in (res * res + 1)
-- process arg = ((bat sum arg), (bat ( sum . (map (\x -> x * 2)) )) (arg + 1))

processLocal = foo sum where
-- processLocal a = (foo sum a + foo maximum a) where
  -- bat sf arg = let res = sf (map (\x->x+(((((((arg^2+1)^2+1)^2+1)^2+1)^2+1)^2+1)^2+1)^2+1) values) in (res * res * res * res * res * res * res * res * res * res * res * res * res * res * res * res * res * res * res + 1)
  -- bat sf arg = let res = sf (map (\x->x+(((((((arg^2+1)^2+1)^2+1)^2+1)^2+1)^2+1)^2+1)^2+1) values) in (res * res * res * res * res * res * res * res * res * res * res + 1)
  -- bat sf arg = let res = sf (map (\x->x+(((((((arg^2+1)^2+1)^2+1)^2+1)^2+1)^2+1)^2+1)) values) in (res * res * res * res * res * res * res * res * res * res * res + 1) -- not inlined
  bat sf arg = let res = sf (map (\x->x+(((((((arg^2+1)^2+1)^2+1)^2+1)^2+1)^2+1))) values) in (res * res * res * res * res * res * res * res * res * res * res + 1) -- inlined!!!
  foo sf arg = (bat sf arg) + (bat (\ls -> sf (map (\x -> x * 2) ls))) (arg + 1)

-- Much slower due to the tuples
-- processLocalTupled = (\x -> (bat (sum, x) + bat (sum . (map (\x -> x * 2)), x + 1))) where
--   bat (sf, arg) = let r = sf (map (+arg) values) in r * r + 1

-- main = print (processLocal 42)
main = do
  -- print (processLocal 42)
  defaultMain [bench "local" $ whnf processLocal 42]

-- main = do
--   print (process 42, processLocal 42, processLocalTupled 42) -- making sure they're the same!
--   print (process 42 == processLocal 42, process 42 == processLocalTupled 42)

-- main = defaultMain
--   [ bench "localTup" $ whnf processLocalTupled 42
--   , bench "toplvl" $ whnf process 42
--   , bench "local" $ whnf processLocal 42
--   ]
  -- [ bench "toplvl" $ whnf process 42
  -- , bench "local" $ whnf processLocal 42
  -- ]
  -- [bench "localTup" $ whnf processLocalTupled 42]
  -- [bench "local" $ whnf processLocal 42]
  -- [bench "toplvl" $ whnf process 42]

