-- Generated Haskell code from SynthBench

module Main (main) where

import Criterion.Main

prod_1 (x0) (y0) = 0 + x0 * y0
test_1 n = sum (map (\i -> prod_1 (i + 0) (i ^ 0)) [0..n])

prod_2 (x0,x1) (y0,y1) = 0 + x0 * y0 + x1 * y1
test_2 n = sum (map (\i -> prod_2 (i + 0, i + 1) (i ^ 0, i ^ 1)) [0..n])

prod_3 (x0,x1,x2) (y0,y1,y2) = 0 + x0 * y0 + x1 * y1 + x2 * y2
test_3 n = sum (map (\i -> prod_3 (i + 0, i + 1, i + 2) (i ^ 0, i ^ 1, i ^ 2)) [0..n])

prod_4 (x0,x1,x2,x3) (y0,y1,y2,y3) = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3
test_4 n = sum (map (\i -> prod_4 (i + 0, i + 1, i + 2, i + 3) (i ^ 0, i ^ 1, i ^ 2, i ^ 3)) [0..n])

prod_5 (x0,x1,x2,x3,x4) (y0,y1,y2,y3,y4) = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4
test_5 n = sum (map (\i -> prod_5 (i + 0, i + 1, i + 2, i + 3, i + 4) (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4)) [0..n])

prod_6 (x0,x1,x2,x3,x4,x5) (y0,y1,y2,y3,y4,y5) = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5
test_6 n = sum (map (\i -> prod_6 (i + 0, i + 1, i + 2, i + 3, i + 4, i + 5) (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5)) [0..n])

prod_7 (x0,x1,x2,x3,x4,x5,x6) (y0,y1,y2,y3,y4,y5,y6) = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6
test_7 n = sum (map (\i -> prod_7 (i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6) (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6)) [0..n])

prod_8 (x0,x1,x2,x3,x4,x5,x6,x7) (y0,y1,y2,y3,y4,y5,y6,y7) = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7
test_8 n = sum (map (\i -> prod_8 (i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7) (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7)) [0..n])

prod_9 (x0,x1,x2,x3,x4,x5,x6,x7,x8) (y0,y1,y2,y3,y4,y5,y6,y7,y8) = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8
test_9 n = sum (map (\i -> prod_9 (i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8) (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8)) [0..n])

prod_10 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9) (y0,y1,y2,y3,y4,y5,y6,y7,y8,y9) = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9
test_10 n = sum (map (\i -> prod_10 (i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9) (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9)) [0..n])

prod_11 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10) (y0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10) = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9 + x10 * y10
test_11 n = sum (map (\i -> prod_11 (i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10) (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10)) [0..n])

prod_12 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11) (y0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11) = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9 + x10 * y10 + x11 * y11
test_12 n = sum (map (\i -> prod_12 (i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11) (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11)) [0..n])

prod_13 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12) (y0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11,y12) = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9 + x10 * y10 + x11 * y11 + x12 * y12
test_13 n = sum (map (\i -> prod_13 (i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11, i + 12) (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11, i ^ 12)) [0..n])

prod_14 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13) (y0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11,y12,y13) = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9 + x10 * y10 + x11 * y11 + x12 * y12 + x13 * y13
test_14 n = sum (map (\i -> prod_14 (i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11, i + 12, i + 13) (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11, i ^ 12, i ^ 13)) [0..n])

prod_15 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14) (y0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11,y12,y13,y14) = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9 + x10 * y10 + x11 * y11 + x12 * y12 + x13 * y13 + x14 * y14
test_15 n = sum (map (\i -> prod_15 (i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11, i + 12, i + 13, i + 14) (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11, i ^ 12, i ^ 13, i ^ 14)) [0..n])

prod_16 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14,x15) (y0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11,y12,y13,y14,y15) = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9 + x10 * y10 + x11 * y11 + x12 * y12 + x13 * y13 + x14 * y14 + x15 * y15
test_16 n = sum (map (\i -> prod_16 (i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11, i + 12, i + 13, i + 14, i + 15) (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11, i ^ 12, i ^ 13, i ^ 14, i ^ 15)) [0..n])

prod_17 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14,x15,x16) (y0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11,y12,y13,y14,y15,y16) = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9 + x10 * y10 + x11 * y11 + x12 * y12 + x13 * y13 + x14 * y14 + x15 * y15 + x16 * y16
test_17 n = sum (map (\i -> prod_17 (i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11, i + 12, i + 13, i + 14, i + 15, i + 16) (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11, i ^ 12, i ^ 13, i ^ 14, i ^ 15, i ^ 16)) [0..n])

prod_18 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14,x15,x16,x17) (y0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11,y12,y13,y14,y15,y16,y17) = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9 + x10 * y10 + x11 * y11 + x12 * y12 + x13 * y13 + x14 * y14 + x15 * y15 + x16 * y16 + x17 * y17
test_18 n = sum (map (\i -> prod_18 (i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11, i + 12, i + 13, i + 14, i + 15, i + 16, i + 17) (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11, i ^ 12, i ^ 13, i ^ 14, i ^ 15, i ^ 16, i ^ 17)) [0..n])

prod_19 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14,x15,x16,x17,x18) (y0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11,y12,y13,y14,y15,y16,y17,y18) = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9 + x10 * y10 + x11 * y11 + x12 * y12 + x13 * y13 + x14 * y14 + x15 * y15 + x16 * y16 + x17 * y17 + x18 * y18
test_19 n = sum (map (\i -> prod_19 (i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11, i + 12, i + 13, i + 14, i + 15, i + 16, i + 17, i + 18) (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11, i ^ 12, i ^ 13, i ^ 14, i ^ 15, i ^ 16, i ^ 17, i ^ 18)) [0..n])

prod_20 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14,x15,x16,x17,x18,x19) (y0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11,y12,y13,y14,y15,y16,y17,y18,y19) = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9 + x10 * y10 + x11 * y11 + x12 * y12 + x13 * y13 + x14 * y14 + x15 * y15 + x16 * y16 + x17 * y17 + x18 * y18 + x19 * y19
test_20 n = sum (map (\i -> prod_20 (i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11, i + 12, i + 13, i + 14, i + 15, i + 16, i + 17, i + 18, i + 19) (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11, i ^ 12, i ^ 13, i ^ 14, i ^ 15, i ^ 16, i ^ 17, i ^ 18, i ^ 19)) [0..n])

prod_21 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14,x15,x16,x17,x18,x19,x20) (y0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11,y12,y13,y14,y15,y16,y17,y18,y19,y20) = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9 + x10 * y10 + x11 * y11 + x12 * y12 + x13 * y13 + x14 * y14 + x15 * y15 + x16 * y16 + x17 * y17 + x18 * y18 + x19 * y19 + x20 * y20
test_21 n = sum (map (\i -> prod_21 (i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11, i + 12, i + 13, i + 14, i + 15, i + 16, i + 17, i + 18, i + 19, i + 20) (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11, i ^ 12, i ^ 13, i ^ 14, i ^ 15, i ^ 16, i ^ 17, i ^ 18, i ^ 19, i ^ 20)) [0..n])

prod_22 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14,x15,x16,x17,x18,x19,x20,x21) (y0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11,y12,y13,y14,y15,y16,y17,y18,y19,y20,y21) = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9 + x10 * y10 + x11 * y11 + x12 * y12 + x13 * y13 + x14 * y14 + x15 * y15 + x16 * y16 + x17 * y17 + x18 * y18 + x19 * y19 + x20 * y20 + x21 * y21
test_22 n = sum (map (\i -> prod_22 (i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11, i + 12, i + 13, i + 14, i + 15, i + 16, i + 17, i + 18, i + 19, i + 20, i + 21) (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11, i ^ 12, i ^ 13, i ^ 14, i ^ 15, i ^ 16, i ^ 17, i ^ 18, i ^ 19, i ^ 20, i ^ 21)) [0..n])

prod_23 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14,x15,x16,x17,x18,x19,x20,x21,x22) (y0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11,y12,y13,y14,y15,y16,y17,y18,y19,y20,y21,y22) = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9 + x10 * y10 + x11 * y11 + x12 * y12 + x13 * y13 + x14 * y14 + x15 * y15 + x16 * y16 + x17 * y17 + x18 * y18 + x19 * y19 + x20 * y20 + x21 * y21 + x22 * y22
test_23 n = sum (map (\i -> prod_23 (i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11, i + 12, i + 13, i + 14, i + 15, i + 16, i + 17, i + 18, i + 19, i + 20, i + 21, i + 22) (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11, i ^ 12, i ^ 13, i ^ 14, i ^ 15, i ^ 16, i ^ 17, i ^ 18, i ^ 19, i ^ 20, i ^ 21, i ^ 22)) [0..n])

prod_24 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14,x15,x16,x17,x18,x19,x20,x21,x22,x23) (y0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11,y12,y13,y14,y15,y16,y17,y18,y19,y20,y21,y22,y23) = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9 + x10 * y10 + x11 * y11 + x12 * y12 + x13 * y13 + x14 * y14 + x15 * y15 + x16 * y16 + x17 * y17 + x18 * y18 + x19 * y19 + x20 * y20 + x21 * y21 + x22 * y22 + x23 * y23
test_24 n = sum (map (\i -> prod_24 (i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11, i + 12, i + 13, i + 14, i + 15, i + 16, i + 17, i + 18, i + 19, i + 20, i + 21, i + 22, i + 23) (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11, i ^ 12, i ^ 13, i ^ 14, i ^ 15, i ^ 16, i ^ 17, i ^ 18, i ^ 19, i ^ 20, i ^ 21, i ^ 22, i ^ 23)) [0..n])

prod_25 (x0,x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12,x13,x14,x15,x16,x17,x18,x19,x20,x21,x22,x23,x24) (y0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11,y12,y13,y14,y15,y16,y17,y18,y19,y20,y21,y22,y23,y24) = 0 + x0 * y0 + x1 * y1 + x2 * y2 + x3 * y3 + x4 * y4 + x5 * y5 + x6 * y6 + x7 * y7 + x8 * y8 + x9 * y9 + x10 * y10 + x11 * y11 + x12 * y12 + x13 * y13 + x14 * y14 + x15 * y15 + x16 * y16 + x17 * y17 + x18 * y18 + x19 * y19 + x20 * y20 + x21 * y21 + x22 * y22 + x23 * y23 + x24 * y24
test_25 n = sum (map (\i -> prod_25 (i + 0, i + 1, i + 2, i + 3, i + 4, i + 5, i + 6, i + 7, i + 8, i + 9, i + 10, i + 11, i + 12, i + 13, i + 14, i + 15, i + 16, i + 17, i + 18, i + 19, i + 20, i + 21, i + 22, i + 23, i + 24) (i ^ 0, i ^ 1, i ^ 2, i ^ 3, i ^ 4, i ^ 5, i ^ 6, i ^ 7, i ^ 8, i ^ 9, i ^ 10, i ^ 11, i ^ 12, i ^ 13, i ^ 14, i ^ 15, i ^ 16, i ^ 17, i ^ 18, i ^ 19, i ^ 20, i ^ 21, i ^ 22, i ^ 23, i ^ 24)) [0..n])

main = defaultMain [
    bench "prod_1" $ whnf test_1 1000,
    bench "prod_2" $ whnf test_2 1000,
    bench "prod_3" $ whnf test_3 1000,
    bench "prod_4" $ whnf test_4 1000,
    bench "prod_5" $ whnf test_5 1000,
    bench "prod_6" $ whnf test_6 1000,
    bench "prod_7" $ whnf test_7 1000,
    bench "prod_8" $ whnf test_8 1000,
    bench "prod_9" $ whnf test_9 1000,
    bench "prod_10" $ whnf test_10 1000,
    bench "prod_11" $ whnf test_11 1000,
    bench "prod_12" $ whnf test_12 1000,
    bench "prod_13" $ whnf test_13 1000,
    bench "prod_14" $ whnf test_14 1000,
    bench "prod_15" $ whnf test_15 1000,
    bench "prod_16" $ whnf test_16 1000,
    bench "prod_17" $ whnf test_17 1000,
    bench "prod_18" $ whnf test_18 1000,
    bench "prod_19" $ whnf test_19 1000,
    bench "prod_20" $ whnf test_20 1000,
    bench "prod_21" $ whnf test_21 1000,
    bench "prod_22" $ whnf test_22 1000,
    bench "prod_23" $ whnf test_23 1000,
    bench "prod_24" $ whnf test_24 1000,
    bench "prod_25" $ whnf test_25 1000
  ]

