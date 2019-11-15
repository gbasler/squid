-- !!! Wentworth's version of a program to generate
-- !!! all the expansions of a generalised regular expression
-- !!!
--
-- RJE: Modified so it only outputs the number of characters in the output,
-- rather that the output itself, thus avoiding having to generate such a
-- huge output file to get a reasonable execution time.

module Main (main) where

import Criterion.Main

import Data.Char
import Control.Monad (replicateM_)
import System.Environment


-- import NofibUtils (hash)
-- | A very simple hash function so that we don't have to store and compare
-- huge output files.
import Data.List (foldl')
import Data.Char (ord)
hash :: String -> Int
hash = foldl' (\acc c -> ord c + acc*31) 0


-- main = replicateM_ 500 $ do
--   (regex:_) <- getArgs
--   print (hash (concat (expand regex)))

numchars :: [String] -> Int
numchars l = sum $ map length l

expand [] = [""]
-- expand ('<':x) = numericRule x
-- expand ('[':x) = alphabeticRule x
expand (c:x) | c == head "<" = numericRule x
expand (c:x) | c == head "[" = alphabeticRule x
expand x = constantRule x

constantRule (c:rest) = [ c:z | z <- expand rest ]

-- alphabeticRule (a:'-':b:']':rest)
-- | a <= b   = [c:z | c <- [a..b],       z <- expand rest]
alphabeticRule (a:c0:b:c1:rest)
  | c0 == '-' && c1 == ']' && a <= b   = [c:z | c <- [a..b],       z <- expand rest]
  | otherwise = [c:z | c <- reverse [b..a], z <- expand rest]

numericRule x
  = [ pad (show i) ++ z
 | i <- if u < v then [u..v] else [u,u-1..v]
 , z <- expand s ]
  where
    (p,_:q) = span (/= '-') x
    (r,_:s) = span (/= '>') q
    (u,v)   = (mknum p, mknum r)
    mknum s = foldl (\ u c -> u * 10 + (ord c - ord '0')) 0 s
    pad s   = [ '0' | i <- [1 .. (width-(length s))]] ++ s
    width   = max (length (show u)) (length (show v))


main = do
  defaultMain [bench "main" $ whnf (\regex -> hash (concat (expand regex))) "420"]
