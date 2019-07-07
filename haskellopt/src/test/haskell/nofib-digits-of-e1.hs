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

