-- Generated Haskell code from SynthBench

module Main(main) where

import Criterion.Main

prod_1 :: Num a => [a] -> a
prod_1 [x0] = 0 + x0
prod_1 [] = 0
test_1 n = sum (map (\i -> prod_1 [i + 0]) [0..n])

prod_2 :: Num a => [a] -> a
prod_2 [x0,x1] = 0 + x0 + x1
prod_2 [x0] = 0 + x0
prod_2 [] = 0
test_2 n = sum (map (\i -> prod_2 [i + 0, i + 1]) [0..n])

prod_3 :: Num a => [a] -> a
prod_3 [x0,x1,x2] = 0 + x0 + x1 + x2
prod_3 [x0,x1] = 0 + x0 + x1
prod_3 [x0] = 0 + x0
prod_3 [] = 0
test_3 n = sum (map (\i -> prod_3 [i + 0, i + 1, i + 2]) [0..n])

prod_4 :: Num a => [a] -> a
prod_4 [x0,x1,x2,x3] = 0 + x0 + x1 + x2 + x3
prod_4 [x0,x1,x2] = 0 + x0 + x1 + x2
prod_4 [x0,x1] = 0 + x0 + x1
prod_4 [x0] = 0 + x0
prod_4 [] = 0
test_4 n = sum (map (\i -> prod_4 [i + 0, i + 1, i + 2, i + 3]) [0..n])

prod_5 :: Num a => [a] -> a
prod_5 [x0,x1,x2,x3,x4] = 0 + x0 + x1 + x2 + x3 + x4
prod_5 [x0,x1,x2,x3] = 0 + x0 + x1 + x2 + x3
prod_5 [x0,x1,x2] = 0 + x0 + x1 + x2
prod_5 [x0,x1] = 0 + x0 + x1
prod_5 [x0] = 0 + x0
prod_5 [] = 0
test_5 n = sum (map (\i -> prod_5 [i + 0, i + 1, i + 2, i + 3, i + 4]) [0..n])

prod_6 :: Num a => [a] -> a
prod_6 [x0,x1,x2,x3,x4,x5] = 0 + x0 + x1 + x2 + x3 + x4 + x5
prod_6 [x0,x1,x2,x3,x4] = 0 + x0 + x1 + x2 + x3 + x4
prod_6 [x0,x1,x2,x3] = 0 + x0 + x1 + x2 + x3
prod_6 [x0,x1,x2] = 0 + x0 + x1 + x2
prod_6 [x0,x1] = 0 + x0 + x1
prod_6 [x0] = 0 + x0
prod_6 [] = 0
test_6 n = sum (map (\i -> prod_6 [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5]) [0..n])

prod_7 :: Num a => [a] -> a
prod_7 [x0,x1,x2,x3,x4,x5,x6] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6
prod_7 [x0,x1,x2,x3,x4,x5] = 0 + x0 + x1 + x2 + x3 + x4 + x5
prod_7 [x0,x1,x2,x3,x4] = 0 + x0 + x1 + x2 + x3 + x4
prod_7 [x0,x1,x2,x3] = 0 + x0 + x1 + x2 + x3
prod_7 [x0,x1,x2] = 0 + x0 + x1 + x2
prod_7 [x0,x1] = 0 + x0 + x1
prod_7 [x0] = 0 + x0
prod_7 [] = 0
test_7 n = sum (map (\i -> prod_7 [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6]) [0..n])

prod_8 :: Num a => [a] -> a
prod_8 [x0,x1,x2,x3,x4,x5,x6,x7] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6 + x7
prod_8 [x0,x1,x2,x3,x4,x5,x6] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6
prod_8 [x0,x1,x2,x3,x4,x5] = 0 + x0 + x1 + x2 + x3 + x4 + x5
prod_8 [x0,x1,x2,x3,x4] = 0 + x0 + x1 + x2 + x3 + x4
prod_8 [x0,x1,x2,x3] = 0 + x0 + x1 + x2 + x3
prod_8 [x0,x1,x2] = 0 + x0 + x1 + x2
prod_8 [x0,x1] = 0 + x0 + x1
prod_8 [x0] = 0 + x0
prod_8 [] = 0
test_8 n = sum (map (\i -> prod_8 [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7]) [0..n])

prod_9 :: Num a => [a] -> a
prod_9 [x0,x1,x2,x3,x4,x5,x6,x7,x8] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6 + x7 + x8
prod_9 [x0,x1,x2,x3,x4,x5,x6,x7] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6 + x7
prod_9 [x0,x1,x2,x3,x4,x5,x6] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6
prod_9 [x0,x1,x2,x3,x4,x5] = 0 + x0 + x1 + x2 + x3 + x4 + x5
prod_9 [x0,x1,x2,x3,x4] = 0 + x0 + x1 + x2 + x3 + x4
prod_9 [x0,x1,x2,x3] = 0 + x0 + x1 + x2 + x3
prod_9 [x0,x1,x2] = 0 + x0 + x1 + x2
prod_9 [x0,x1] = 0 + x0 + x1
prod_9 [x0] = 0 + x0
prod_9 [] = 0
test_9 n = sum (map (\i -> prod_9 [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8]) [0..n])

prod_10 :: Num a => [a] -> a
prod_10 [x0,x1,x2,x3,x4,x5,x6,x7,x8,x9] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6 + x7 + x8 + x9
prod_10 [x0,x1,x2,x3,x4,x5,x6,x7,x8] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6 + x7 + x8
prod_10 [x0,x1,x2,x3,x4,x5,x6,x7] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6 + x7
prod_10 [x0,x1,x2,x3,x4,x5,x6] = 0 + x0 + x1 + x2 + x3 + x4 + x5 + x6
prod_10 [x0,x1,x2,x3,x4,x5] = 0 + x0 + x1 + x2 + x3 + x4 + x5
prod_10 [x0,x1,x2,x3,x4] = 0 + x0 + x1 + x2 + x3 + x4
prod_10 [x0,x1,x2,x3] = 0 + x0 + x1 + x2 + x3
prod_10 [x0,x1,x2] = 0 + x0 + x1 + x2
prod_10 [x0,x1] = 0 + x0 + x1
prod_10 [x0] = 0 + x0
prod_10 [] = 0
test_10 n = sum (map (\i -> prod_10 [i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9]) [0..n])

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
    bench "10" $ whnf test_10 1000
  ]

