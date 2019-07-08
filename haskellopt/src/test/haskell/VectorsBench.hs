-- Generated Haskell code from SynthBench

module Main where

import Criterion.Main

prod_1 :: (Int) -> Int
prod_1 (x0) = 1 * x0
test_1 n = sum (map (\i -> prod_1 (i ^ 0)) [0..n])

prod_2 :: (Int,Int) -> Int
prod_2 (x0,x1) = 1 * x0 * x1
test_2 n = sum (map (\i -> prod_2 (i ^ 0, i ^ 1)) [0..n])

prod_3 :: (Int,Int,Int) -> Int
prod_3 (x0,x1,x2) = 1 * x0 * x1 * x2
test_3 n = sum (map (\i -> prod_3 (i ^ 0, i ^ 1, i ^ 2)) [0..n])

prod_4 :: (Int,Int,Int,Int) -> Int
prod_4 (x0,x1,x2,x3) = 1 * x0 * x1 * x2 * x3
test_4 n = sum (map (\i -> prod_4 (i ^ 0, i ^ 1, i ^ 2, i ^ 3)) [0..n])

prod_5 :: (Int,Int,Int,Int,Int) -> Int
prod_5 (x0,x1,x2,x3,x4) = 1 * x0 * x1 * x2 * x3 * x4
test_5 n = sum (map (\i -> prod_5 (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4)) [0..n])

prod_6 :: (Int,Int,Int,Int,Int,Int) -> Int
prod_6 (x0,x1,x2,x3,x4,x5) = 1 * x0 * x1 * x2 * x3 * x4 * x5
test_6 n = sum (map (\i -> prod_6 (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5)) [0..n])

prod_7 :: (Int,Int,Int,Int,Int,Int,Int) -> Int
prod_7 (x0,x1,x2,x3,x4,x5,x6) = 1 * x0 * x1 * x2 * x3 * x4 * x5 * x6
test_7 n = sum (map (\i -> prod_7 (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6)) [0..n])

prod_8 :: (Int,Int,Int,Int,Int,Int,Int,Int) -> Int
prod_8 (x0,x1,x2,x3,x4,x5,x6,x7) = 1 * x0 * x1 * x2 * x3 * x4 * x5 * x6 * x7
test_8 n = sum (map (\i -> prod_8 (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7)) [0..n])

prod_9 :: (Int,Int,Int,Int,Int,Int,Int,Int,Int) -> Int
prod_9 (x0,x1,x2,x3,x4,x5,x6,x7,x8) = 1 * x0 * x1 * x2 * x3 * x4 * x5 * x6 * x7 * x8
test_9 n = sum (map (\i -> prod_9 (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8)) [0..n])

prod_10 :: (Int,Int,Int,Int,Int,Int,Int,Int,Int,Int) -> Int
prod_10 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9) = 1 * x0 * x1 * x2 * x3 * x4 * x5 * x6 * x7 * x8 * x9
test_10 n = sum (map (\i -> prod_10 (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9)) [0..n])

prod_11 :: (Int,Int,Int,Int,Int,Int,Int,Int,Int,Int,Int) -> Int
prod_11 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10) = 1 * x0 * x1 * x2 * x3 * x4 * x5 * x6 * x7 * x8 * x9 * x10
test_11 n = sum (map (\i -> prod_11 (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10)) [0..n])

prod_12 :: (Int,Int,Int,Int,Int,Int,Int,Int,Int,Int,Int,Int) -> Int
prod_12 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11) = 1 * x0 * x1 * x2 * x3 * x4 * x5 * x6 * x7 * x8 * x9 * x10 * x11
test_12 n = sum (map (\i -> prod_12 (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11)) [0..n])

prod_13 :: (Int,Int,Int,Int,Int,Int,Int,Int,Int,Int,Int,Int,Int) -> Int
prod_13 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12) = 1 * x0 * x1 * x2 * x3 * x4 * x5 * x6 * x7 * x8 * x9 * x10 * x11 * x12
test_13 n = sum (map (\i -> prod_13 (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11, i ^ 12)) [0..n])

prod_14 :: (Int,Int,Int,Int,Int,Int,Int,Int,Int,Int,Int,Int,Int,Int) -> Int
prod_14 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13) = 1 * x0 * x1 * x2 * x3 * x4 * x5 * x6 * x7 * x8 * x9 * x10 * x11 * x12 * x13
test_14 n = sum (map (\i -> prod_14 (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11, i ^ 12, i ^ 13)) [0..n])

prod_15 :: (Int,Int,Int,Int,Int,Int,Int,Int,Int,Int,Int,Int,Int,Int,Int) -> Int
prod_15 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14) = 1 * x0 * x1 * x2 * x3 * x4 * x5 * x6 * x7 * x8 * x9 * x10 * x11 * x12 * x13 * x14
test_15 n = sum (map (\i -> prod_15 (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11, i ^ 12, i ^ 13, i ^ 14)) [0..n])

main = defaultMain [
    bench "1" $ whnf test_1 1000,
    bench "2" $ whnf test_2 1000,
    bench "3" $ whnf test_3 1000,
    bench "4" $ whnf test_4 1000,
    bench "5" $ whnf test_5 1000,
    bench "6" $ whnf test_6 1000,
    bench "7" $ whnf test_7 1000,
    bench "8" $ whnf test_8 1000,
    bench "9" $ whnf test_9 1000,
    bench "10" $ whnf test_10 1000,
    bench "11" $ whnf test_11 1000,
    bench "12" $ whnf test_12 1000,
    bench "13" $ whnf test_13 1000,
    bench "14" $ whnf test_14 1000,
    bench "15" $ whnf test_15 1000
  ]

