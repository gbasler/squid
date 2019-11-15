module Main where

import Criterion.Main

import System.Environment (getArgs)
import Control.Monad (replicateM_)


-- import NofibUtils (hash)
-- | A very simple hash function so that we don't have to store and compare
-- huge output files.
import Data.List (foldl')
import Data.Char (ord)
hash :: String -> Int
hash = foldl' (\acc c -> ord c + acc*31) 0

-- err() = err()

type ContFrac = [Integer]

eContFrac :: ContFrac
eContFrac = 2:aux 2 where aux n = 1:n:1:aux (n+2)

-- ratTrans (a,b,c,d) x: compute (a + bx)/(c+dx) as a continued fraction
ratTrans :: (Integer,Integer,Integer,Integer) -> ContFrac -> ContFrac
-- Output a digit if we can
ratTrans (a,b,c,d) xs |
  ((signum c == signum d) || (abs c < abs d)) && -- No pole in range
  (c+d)*q <= a+b && (c+d)*q + (c+d) > a+b       -- Next digit is determined
     = q:ratTrans (c,d,a-q*c,b-q*d) xs
  where q = b `div` d
ratTrans (a,b,c,d) (x:xs) = ratTrans (b,a+x*b,d,c+x*d) xs
-- ratTrans _ _ = err()

takeDigits :: Int -> ContFrac -> [Integer]
-- takeDigits 0 _ = []
takeDigits n _ | n == 0 = []
takeDigits n (x:xs) = x:takeDigits (n-1) (ratTrans (10,0,0,1) xs)

e :: Int -> [Integer]
e n = takeDigits n eContFrac

-- main = replicateM_ 100 $ do
--   [digits] <- getArgs
--   print (hash (show (e (read digits))))
main = do
  defaultMain [bench "main" $ whnf (\d -> hash (show (e (d)))) 420]

{-
ghc -O2 nofib-digits-of-e1.hs && ./nofib-digits-of-e1
ghc -O2 nofib-digits-of-e1.pass-0000.opt.hs && ./nofib-digits-of-e1.pass-0000.opt

benchmarking main
time                 54.49 ms   (52.98 ms .. 55.50 ms)
                     0.999 R²   (0.997 R² .. 1.000 R²)
mean                 56.05 ms   (55.31 ms .. 56.83 ms)
std dev              1.394 ms   (1.101 ms .. 1.755 ms)

--- OPT ---

benchmarking main
time                 8.471 ms   (8.393 ms .. 8.574 ms)
                     0.999 R²   (0.999 R² .. 1.000 R²)
mean                 8.511 ms   (8.471 ms .. 8.556 ms)
std dev              117.6 μs   (92.69 μs .. 141.4 μs)

-}
