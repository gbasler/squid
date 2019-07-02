-- ghc -O3 ListFusionBench.hs  && ./ListFusionBench --regress allocated:iters +RTS -T -RTS
module Main where

import Criterion.Main

-- loremipsum :: [Char]
-- loremipsum = concatMap (replicate 10) ['a'..'z']
loremipsum :: [Int]
loremipsum = concatMap (replicate 10) [0..666]

-- Local version below is much faster (<1/3 of the time) in opt mode... is that due to more list fusion?
-- Very strangely, when I switched from `nf` to `whnf` and (,) to (+), the toplvl version became super slow!

bat sf arg = let res = sf ((map (\c -> c + arg)) loremipsum) in (res * res + 1)
foo sf arg =
  -- ((bat sf arg), (bat ( sf . (map (\x -> x * 2)) )) (arg + 1))
  ((bat sf arg) + (bat ( sf . (map (\x -> x * 2)) )) (arg + 1))
  -- ^ NOTE: amazingly, changing (arg + 1) to arg made the program fuse like the local version!
sumnats = foo sum
-- -- The version below is as fast as the local one!
-- bat sf arg = let res = sf ((map (\c -> c + arg)) loremipsum) in (res * res + 1)
-- sumnats arg = ((bat sum arg), (bat ( sum . (map (\x -> x * 2)) )) (arg + 1))

sumnatsLocal = foo sum where
  bat sf arg = let res = sf ((map (\c -> c + arg)) loremipsum) in (res * res + 1)
  foo sf arg =
    -- ((bat sf arg), (bat ( sf . (map (\x -> x * 2)) )) (arg + 1))
    ((bat sf arg) + (bat ( sf . (map (\x -> x * 2)) )) (arg + 1))

-- -- Much slower due to the tuples
-- sumnatsLocalTupled = (\x -> (bat (sum, x), bat (sum . (map (\x -> x * 2)), x + 1))) where
sumnatsLocalTupled = (\x -> (bat (sum, x) + bat (sum . (map (\x -> x * 2)), x + 1))) where
  bat (sf, arg) = let r = sf ((map (\c -> c + arg)) loremipsum) in r * r + 1

-- -- Weirdly, this one is just as fast, although it's the closest to the original regression I noted (in bench-doc)...
-- sumnatsLocalTupled =  (\arg -> (((,) (_0 arg sum )) (_0 (((GHC.Num.+) arg) 1) (((GHC.Base..) sum) (GHC.Base.map (\x -> (((GHC.Num.*) x) 2)))) ))) where
--   _0 arg' sf  = (((GHC.Num.+) (let r = (res arg' sf ) in ((GHC.Num.*) r) r)) 1)
--   res arg'2 sf'  = (sf' ((GHC.Base.map (\c -> (((GHC.Num.+) c) arg'2))) loremipsum))

-- main = do
--   print (sumnats 42, sumnatsLocal 42, sumnatsLocalTupled 42) -- making sure they're the same!
--   print (sumnats 42 == sumnatsLocal 42, sumnats 42 == sumnatsLocalTupled 42)

main = defaultMain
  [ bench "localTup" $ whnf sumnatsLocalTupled 42
  , bench "toplvl" $ whnf sumnats 42
  , bench "local" $ whnf sumnatsLocal 42
  ]
  -- [bench "localTup" $ nf sumnatsLocalTupled 42]




--- OLD ---


-- bat = (\sf arg -> 
--     -- let res = sf ((map (\c -> (ord c) + arg)) loremipsum) in (res * res + 1)
--     let res = sf ((map (\c -> c + arg)) loremipsum) in (res * res + 1)
--   )
-- foo = (\sf arg -> 
--     (((bat sf) arg), (bat ( sf . (map (\x -> x * 2)) )) (arg + 1)) -- NOTE: amazingly, changing (arg + 1) to arg makes the program fuse like the local version!
--   )


-- -- Also much slower due to the tuples:
-- sumnatsLocalTupled = (\x -> foo (sum, sum . (map (\x -> x * 2)), x )) where
--   bat = (\(sf, arg) -> 
--     let res(sf, arg) = sf ((map (\c -> c + arg)) loremipsum) in (let r = res(sf,arg) in r * r + 1) )
--   foo = (\(sf, sf2, arg) -> 
--     ((bat (sf, arg)), (bat ( sf2, arg + 1))) )


