-- ghc -O3 ListFusionBench.hs  && ./ListFusionBench --regress allocated:iters +RTS -T -RTS
module Main where

import Criterion.Main
import Data.Char


import Control.DeepSeq
import Criterion.Main
import Criterion.Measurement.Types
import Data.Foldable
import GHC.Base
import GHC.CString
import GHC.Enum
import GHC.List
import GHC.Num
import GHC.TopHandler
import GHC.Types

-- loremipsum :: [Char]
-- loremipsum = concatMap (replicate 10) ['a'..'z']
loremipsum :: [Int]
loremipsum = GHC.List.concatMap (replicate 10) [0..666]

-- Local version below is much faster (<1/3 of the time) in opt mode... is that due to more list fusion?

bat = (\sf arg -> 
    -- let res = sf ((map (\c -> (ord c) + arg)) loremipsum) in (res * res + 1)
    let res = sf ((map (\c -> c + arg)) loremipsum) in (res * res + 1)
  )
foo = (\sf arg -> 
    (((bat sf) arg), (bat ( sf . (map (\x -> x * 2)) ))(arg + 1))
  )
sumnats = (foo Data.Foldable.sum)

sumnatsLocal = (foo Data.Foldable.sum) where
  bat = (\sf arg -> 
    -- let res = sf ((map (\c -> (ord c) + arg)) loremipsum) in (res * res + 1)
    let res = sf ((map (\c -> c + arg)) loremipsum) in (res * res + 1) )
  foo = (\sf arg -> 
    (((bat sf) arg), (bat ( sf . (map (\x -> x * 2)) ))(arg + 1)) )

-- -- Much slower due to the tuples
-- sumnatsLocalTupled = (\x -> foo (Data.Foldable.sum, Data.Foldable.sum . (map (\x -> x * 2)), x )) where
--   bat = (\(sf, arg) -> 
--     let res(sf, arg) = sf ((map (\c -> c + arg)) loremipsum) in (let r = res(sf,arg) in r * r + 1) )
--   foo = (\(sf, sf2, arg) -> 
--     ((bat (sf, arg)), (bat ( sf2, arg + 1))) )

-- -- Much slower due to the tuples
sumnatsLocalTupled = (\x -> (bat (Data.Foldable.sum, x ), bat (Data.Foldable.sum . (map (\x -> x * 2)), x ))) where
  bat = (\(sf, arg) -> 
    (let r = res(sf,arg) in r * r + 1) )
    -- (let r = res(sf,arg) in r * r) + 1 )
  res(sf, arg) = sf ((map (\c -> c + arg)) loremipsum)
  -- foo = (\(sf, sf2, arg) -> 
  --   ((bat (sf, arg)), (bat ( sf2, arg + 1))) )

-- -- Weirdly, this one is just as fast, although it's the closest to the original regression I noted (in bench-doc)...
-- sumnatsLocalTupled =  (\arg -> (((,) (_0 arg Data.Foldable.sum )) (_0 (((GHC.Num.+) arg) 1) (((GHC.Base..) Data.Foldable.sum) (GHC.Base.map (\x -> (((GHC.Num.*) x) 2)))) ))) where
--   _0 arg' sf  = (((GHC.Num.+) (let r = (res arg' sf ) in ((GHC.Num.*) r) r)) 1)
--   res arg'2 sf'  = (sf' ((GHC.Base.map (\c -> (((GHC.Num.+) c) arg'2))) loremipsum))

main = defaultMain
  [ bench "toplvl" $ nf sumnats 42
  , bench "local" $ nf sumnatsLocal 42
  , bench "localTup" $ nf sumnatsLocalTupled 42
  ]
  -- [bench "localTup" $ nf sumnatsLocalTupled 42]
