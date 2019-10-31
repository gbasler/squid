module Motiv where

-- import Data.Maybe
isJust (Just _) = True
isJust Nothing = False

e0 :: Integer -> Integer
e0(a) = a

e1 :: (Integer,Integer) -> Integer
e1(a,z) = a * z

e2 :: Integer -> Integer
e2(z) = z + 1

e3 :: Bool -> Integer
e3(c) = if c then 1 else 0

f :: Maybe Integer -> Integer
f x = let z = e3(isJust x) in e0(case x of Just a -> e1(a, z)
                                           Nothing -> e2(z))

pgrm = f (Just 2) + f Nothing

