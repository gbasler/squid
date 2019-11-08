-- Lots of indirection by going through Fold... how much of that can we eliminate?

{-# LANGUAGE PatternSynonyms #-}

module Main where

import Criterion.Main
import Control.Foldl

-- data Fold a b = forall x. Fold ((x -> a -> x), x, (x -> b))

pure_F b = Fold (\() _ -> ()) () (\() -> b)

pattern Pair a b = (a, b) -- in the actual library, this is a strict Pair constructor with bangs

ap_F :: Fold x (a -> b) -> Fold x a -> Fold x b
(Fold stepL beginL doneL) `ap_F` (Fold stepR beginR doneR) =
    let step (Pair xL xR) a = Pair (stepL xL a) (stepR xR a)
        begin = Pair beginL beginR
        done (Pair xL xR) = doneL xL (doneR xR)
    in  Fold step begin done

fmap_F f (Fold step begin done) = Fold step begin (f . done)

-- fold :: Foldable f => Fold a b -> f a -> b
-- fold (Fold step begin done) as = F.foldr cons done as begin
--   where
--     cons a k x = k $! step x a
fold_F :: Fold a b -> [a] -> b
fold_F (Fold step begin done) xs = done (foldl' step begin xs)

foldl' :: (b -> a -> b) -> b -> [a] -> b
foldl' f z []     = z
foldl' f z (x:xs) = let z' = z `f` x 
                    -- in seq z' $ foldl' f z' xs
                    in foldl' f z' xs

-- sum :: Num a => Fold a a
sum_F :: Fold Int Int
sum_F = Fold (+) 0 id

-- genericLength :: Num b => Fold a b
length_F :: Fold a Int
length_F = Fold (\n _ -> n + 1) 0 id

avg_F xs = let
        f = (,) `fmap_F` sum_F `ap_F` length_F
        (s,l) = fold_F f xs
    in s + l

avg_manual_tr :: [Int] -> Int
avg_manual_tr xs = go 0 0 xs where
    go s l [] = s + l
    go s l (x : xs) = go (s + x) (l + 1) xs

avg_manual :: [Int] -> Int
avg_manual xs = su + le where
    (su, le) = go xs
    go [] = (0, 0)
    go (x : xs) =
        let (s, l) = go xs
        in (s + x, l + 1)

k = [0..1000*1000]

main = defaultMain [
    bgroup "folding" [
        bench "normal"  $ whnf avg_F k,
        bench "manual"  $ whnf avg_manual_tr k,
        bench "manual_tr"  $ whnf avg_manual_tr k
    ]
  ]

{-

> ghc -O2 -fforce-recomp -ddump-to-file -ddump-prep -dsuppress-module-prefixes -dsuppress-idinfo FoldingBench.hs && ./FoldingBench
[1 of 1] Compiling Main             ( FoldingBench.hs, FoldingBench.o )
Linking FoldingBench ...
benchmarking folding/normal
time                 239.7 ms   (211.6 ms .. 260.6 ms)
                     0.996 R²   (0.989 R² .. 1.000 R²)
mean                 241.3 ms   (235.8 ms .. 248.5 ms)
std dev              8.002 ms   (4.429 ms .. 11.41 ms)
variance introduced by outliers: 16% (moderately inflated)

benchmarking folding/manual
time                 3.089 ms   (3.004 ms .. 3.216 ms)
                     0.993 R²   (0.989 R² .. 0.996 R²)
mean                 3.161 ms   (3.094 ms .. 3.250 ms)
std dev              255.9 μs   (189.7 μs .. 368.3 μs)
variance introduced by outliers: 56% (severely inflated)

benchmarking folding/manual_tr
time                 3.009 ms   (2.904 ms .. 3.119 ms)
                     0.988 R²   (0.979 R² .. 0.994 R²)
mean                 3.160 ms   (3.074 ms .. 3.278 ms)
std dev              324.4 μs   (238.7 μs .. 418.4 μs)
variance introduced by outliers: 66% (severely inflated)

-}
