module Main where

import Criterion.Main
import Data.Char

-- loremipsum :: [Char]
-- loremipsum = concatMap (replicate 10) ['a'..'z']
loremipsum :: [Int]
loremipsum = concatMap (replicate 10) [0..666]

-- Local version below is much faster (<1/3 of the time) in opt mode... is that due to more list fusion?

-- bat = (\sf arg -> 
--     -- let res = sf ((map (\c -> (ord c) + arg)) loremipsum) in (res * res + 1)
--     let res = sf ((map (\c -> c + arg)) loremipsum) in (res * res + 1)
--   )
-- foo = (\sf arg -> 
--     (((bat sf) arg), (bat ( sf . (map (\x -> x * 2)) ))(arg + 1))
--   )
-- sumnats = (foo sum)

sumnats = (foo sum) where
  bat = (\sf arg -> 
    -- let res = sf ((map (\c -> (ord c) + arg)) loremipsum) in (res * res + 1)
    let res = sf ((map (\c -> c + arg)) loremipsum) in (res * res + 1) )
  foo = (\sf arg -> 
    (((bat sf) arg), (bat ( sf . (map (\x -> x * 2)) ))(arg + 1)) )

main = defaultMain [bench "max" $ nf sumnats 42]
