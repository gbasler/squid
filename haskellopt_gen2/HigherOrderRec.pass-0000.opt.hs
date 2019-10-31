-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  16
-- Incl. one-shot:  0
-- Total nodes: 209; Boxes: 65; Branches: 34
-- Apps: 43; Lams: 13

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module HigherOrderp (s,r_2,r_0,r,only_q,q_1,q) where

import GHC.Base
import GHC.List
import GHC.Num
import GHC.Tuple
import GHC.Types

s = \f_ε -> \x_ε -> let
        β = x_ε + fromInteger 1
        rec β'5 = let
              β'6 = β'5 + fromInteger 1
              rec'3 β'7 = 
                    let β'8 = β'7 + fromInteger 1 in
                    β'8 - (f_ε (rec β'8) * f_ε (rec'3 β'8))
              in β'6 - (f_ε (rec β'6) * f_ε (rec'3 β'6))
        rec' β' = let
              β'2 = β' + fromInteger 1
              rec'2 β'3 = 
                    let β'4 = β'3 + fromInteger 1 in
                    β'4 - (f_ε (rec'2 β'4) * f_ε (rec' β'4))
              in β'2 - (f_ε (rec'2 β'2) * f_ε (rec' β'2))
        in β - (f_ε (rec β) * f_ε (rec' β))

r_2 = let
  β'9 = (:) 1
  rec'4 = β'9 rec'4
  in GHC.List.take 3 $ β'9 rec'4

r_0 = 
  let β'10 = (+) 1 in
  \unit_ε -> 
        let rec'5 = β'10 rec'5 in
        β'10 rec'5

r = \f_ε' -> \unit_ε' -> 
        let rec'6 = f_ε' rec'6 in
        f_ε' rec'6

only_q = \f -> \x -> \y -> 
              let rec'7 = f (f x) : rec'7 in
              f x : (f (f y) : rec'7)

q_1 = 
  let β'11 = (+) 1 in
  \x_ε' -> \y_ε -> 
              let rec'8 = β'11 (β'11 y_ε) : rec'8 in
              β'11 x_ε' : rec'8

q = \f_ε'2 -> \x_ε'2 -> \y_ε' -> 
              let rec'9 = f_ε'2 (f_ε'2 y_ε') : rec'9 in
              f_ε'2 x_ε'2 : rec'9
