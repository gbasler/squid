-- Generated Haskell code from SynthBench

module Main where

import Criterion.Main

prod_15 :: Num a => (a,a,a,a,a,a,a,a,a,a,a,a,a,a,a) -> a
prod_15 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14) = 1 * x0 * x1 * x2 * x3 * x4 * x5 * x6 * x7 * x8 * x9 * x10 * x11 * x12 * x13 * x14
test_15 n = sum (map (\i -> prod_15 (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11, i ^ 12, i ^ 13, i ^ 14)) [0..n])

prod_20 :: Num a => (a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a) -> a
prod_20 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14,x15,x16,x17,x18,x19) = 1 * x0 * x1 * x2 * x3 * x4 * x5 * x6 * x7 * x8 * x9 * x10 * x11 * x12 * x13 * x14 * x15 * x16 * x17 * x18 * x19
test_20 n = sum (map (\i -> prod_20 (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11, i ^ 12, i ^ 13, i ^ 14, i ^ 15, i ^ 16, i ^ 17, i ^ 18, i ^ 19)) [0..n])

prod_25 :: Num a => (a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a) -> a
prod_25 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14,x15,x16,x17,x18,x19,x20,x21,x22,x23,x24) = 1 * x0 * x1 * x2 * x3 * x4 * x5 * x6 * x7 * x8 * x9 * x10 * x11 * x12 * x13 * x14 * x15 * x16 * x17 * x18 * x19 * x20 * x21 * x22 * x23 * x24
test_25 n = sum (map (\i -> prod_25 (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11, i ^ 12, i ^ 13, i ^ 14, i ^ 15, i ^ 16, i ^ 17, i ^ 18, i ^ 19, i ^ 20, i ^ 21, i ^ 22, i ^ 23, i ^ 24)) [0..n])

prod_30 :: Num a => (a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a) -> a
prod_30 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14,x15,x16,x17,x18,x19,x20,x21,x22,x23,x24,x25,x26,x27,x28,x29) = 1 * x0 * x1 * x2 * x3 * x4 * x5 * x6 * x7 * x8 * x9 * x10 * x11 * x12 * x13 * x14 * x15 * x16 * x17 * x18 * x19 * x20 * x21 * x22 * x23 * x24 * x25 * x26 * x27 * x28 * x29
test_30 n = sum (map (\i -> prod_30 (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11, i ^ 12, i ^ 13, i ^ 14, i ^ 15, i ^ 16, i ^ 17, i ^ 18, i ^ 19, i ^ 20, i ^ 21, i ^ 22, i ^ 23, i ^ 24, i ^ 25, i ^ 26, i ^ 27, i ^ 28, i ^ 29)) [0..n])

prod_35 :: Num a => (a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a) -> a
prod_35 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14,x15,x16,x17,x18,x19,x20,x21,x22,x23,x24,x25,x26,x27,x28,x29,x30,x31,x32,x33,x34) = 1 * x0 * x1 * x2 * x3 * x4 * x5 * x6 * x7 * x8 * x9 * x10 * x11 * x12 * x13 * x14 * x15 * x16 * x17 * x18 * x19 * x20 * x21 * x22 * x23 * x24 * x25 * x26 * x27 * x28 * x29 * x30 * x31 * x32 * x33 * x34
test_35 n = sum (map (\i -> prod_35 (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11, i ^ 12, i ^ 13, i ^ 14, i ^ 15, i ^ 16, i ^ 17, i ^ 18, i ^ 19, i ^ 20, i ^ 21, i ^ 22, i ^ 23, i ^ 24, i ^ 25, i ^ 26, i ^ 27, i ^ 28, i ^ 29, i ^ 30, i ^ 31, i ^ 32, i ^ 33, i ^ 34)) [0..n])

prod_40 :: Num a => (a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a) -> a
prod_40 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14,x15,x16,x17,x18,x19,x20,x21,x22,x23,x24,x25,x26,x27,x28,x29,x30,x31,x32,x33,x34,x35,x36,x37,x38,x39) = 1 * x0 * x1 * x2 * x3 * x4 * x5 * x6 * x7 * x8 * x9 * x10 * x11 * x12 * x13 * x14 * x15 * x16 * x17 * x18 * x19 * x20 * x21 * x22 * x23 * x24 * x25 * x26 * x27 * x28 * x29 * x30 * x31 * x32 * x33 * x34 * x35 * x36 * x37 * x38 * x39
test_40 n = sum (map (\i -> prod_40 (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11, i ^ 12, i ^ 13, i ^ 14, i ^ 15, i ^ 16, i ^ 17, i ^ 18, i ^ 19, i ^ 20, i ^ 21, i ^ 22, i ^ 23, i ^ 24, i ^ 25, i ^ 26, i ^ 27, i ^ 28, i ^ 29, i ^ 30, i ^ 31, i ^ 32, i ^ 33, i ^ 34, i ^ 35, i ^ 36, i ^ 37, i ^ 38, i ^ 39)) [0..n])

main = defaultMain [
    bench "15" $ whnf test_15 1000,
    bench "20" $ whnf test_20 1000,
    bench "25" $ whnf test_25 1000,
    bench "30" $ whnf test_30 1000,
    bench "35" $ whnf test_35 1000,
    bench "40" $ whnf test_40 1000
  ]

