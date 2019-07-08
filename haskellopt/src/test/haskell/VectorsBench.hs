-- Generated Haskell code from SynthBench

module Main (main) where

import Criterion.Main

prod_1 :: [Int] -> [Int] -> Int
prod_1 [x0] [y0] = 0 + x0 * y0
test_1 n = sum (map (\i -> prod_1 [i + 0] [i + 0]) [0..n])

prod_2 :: [Int] -> [Int] -> Int
prod_2 [x0,x1] [y0,y1] = 0 + x0 * y0 + x1 * y1
test_2 n = sum (map (\i -> prod_2 [i + 0, i + 1] [i + 0, i + 1]) [0..n])

prod_3 :: [Int] -> [Int] -> Int
prod_3 [x0,x1,x2] [y0,y1,y2] = 0 + x0 * y0 + x1 * y1 + x2 * y2
test_3 n = sum (map (\i -> prod_3 [i + 0, i + 1, i + 2] [i + 0, i + 1, i + 2]) [0..n])

prod_4 :: [Int] -> [Int] -> Int
prod_4 [x0,x1,x2,x3] [y0,y1,y2,y3] = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3
test_4 n = sum (map (\i -> prod_4 [i + 0, i + 1, i + 2, i + 3] [i + 0, i + 1, i + 2, i + 3]) [0..n])

prod_5 :: [Int] -> [Int] -> Int
prod_5 [x0,x1,x2,x3,x4] [y0,y1,y2,y3,y4] = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4
test_5 n = sum (map (\i -> prod_5 [i + 0, i + 1, i + 2, i + 3, i + 4] [i + 0, i + 1, i + 2, i + 3, i + 4]) [0..n])

prod_6 :: [Int] -> [Int] -> Int
prod_6 [x0,x1,x2,x3,x4,x5] [y0,y1,y2,y3,y4,y5] = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5
test_6 n = sum (map (\i -> prod_6 [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5] [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5]) [0..n])

prod_7 :: [Int] -> [Int] -> Int
prod_7 [x0,x1,x2,x3,x4,x5,x6] [y0,y1,y2,y3,y4,y5,y6] = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6
test_7 n = sum (map (\i -> prod_7 [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6] [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6]) [0..n])

prod_8 :: [Int] -> [Int] -> Int
prod_8 [x0,x1,x2,x3,x4,x5,x6,x7] [y0,y1,y2,y3,y4,y5,y6,y7] = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7
test_8 n = sum (map (\i -> prod_8 [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7] [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7]) [0..n])

prod_9 :: [Int] -> [Int] -> Int
prod_9 [x0,x1,x2,x3,x4,x5,x6,x7,x8] [y0,y1,y2,y3,y4,y5,y6,y7,y8] = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8
test_9 n = sum (map (\i -> prod_9 [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8] [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8]) [0..n])

prod_10 :: [Int] -> [Int] -> Int
prod_10 [x0,x1,x2,x3,x4,x5,x6,x7,x8,x9] [y0,y1,y2,y3,y4,y5,y6,y7,y8,y9] = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9
test_10 n = sum (map (\i -> prod_10 [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9] [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9]) [0..n])

prod_12 :: [Int] -> [Int] -> Int
prod_12 [x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11] [y0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11] = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9 + x10 * y10 + x11 * y11
test_12 n = sum (map (\i -> prod_12 [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11] [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11]) [0..n])

prod_14 :: [Int] -> [Int] -> Int
prod_14 [x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13] [y0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11,y12,y13] = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9 + x10 * y10 + x11 * y11 + x12 * y12 + x13 * y13
test_14 n = sum (map (\i -> prod_14 [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11, i + 12, i + 13] [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11, i + 12, i + 13]) [0..n])

prod_16 :: [Int] -> [Int] -> Int
prod_16 [x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14,x15] [y0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11,y12,y13,y14,y15] = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9 + x10 * y10 + x11 * y11 + x12 * y12 + x13 * y13 + x14 * y14 + x15 * y15
test_16 n = sum (map (\i -> prod_16 [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11, i + 12, i + 13, i + 14, i + 15] [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11, i + 12, i + 13, i + 14, i + 15]) [0..n])

prod_18 :: [Int] -> [Int] -> Int
prod_18 [x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14,x15,x16,x17] [y0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11,y12,y13,y14,y15,y16,y17] = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9 + x10 * y10 + x11 * y11 + x12 * y12 + x13 * y13 + x14 * y14 + x15 * y15 + x16 * y16 + x17 * y17
test_18 n = sum (map (\i -> prod_18 [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11, i + 12, i + 13, i + 14, i + 15, i + 16, i + 17] [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11, i + 12, i + 13, i + 14, i + 15, i + 16, i + 17]) [0..n])

prod_20 :: [Int] -> [Int] -> Int
prod_20 [x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14,x15,x16,x17,x18,x19] [y0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11,y12,y13,y14,y15,y16,y17,y18,y19] = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9 + x10 * y10 + x11 * y11 + x12 * y12 + x13 * y13 + x14 * y14 + x15 * y15 + x16 * y16 + x17 * y17 + x18 * y18 + x19 * y19
test_20 n = sum (map (\i -> prod_20 [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11, i + 12, i + 13, i + 14, i + 15, i + 16, i + 17, i + 18, i + 19] [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11, i + 12, i + 13, i + 14, i + 15, i + 16, i + 17, i + 18, i + 19]) [0..n])

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
    bench "12" $ whnf test_12 1000,
    bench "14" $ whnf test_14 1000,
    bench "16" $ whnf test_16 1000,
    bench "18" $ whnf test_18 1000,
    bench "20" $ whnf test_20 1000
  ]

