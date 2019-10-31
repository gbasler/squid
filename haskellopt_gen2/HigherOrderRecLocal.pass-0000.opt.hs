-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  21
-- Incl. one-shot:  0
-- Total nodes: 703; Boxes: 234; Branches: 222
-- Apps: 114; Lams: 19

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module HigherOrderRecLocal (foo_5,foo_4,foo_3,foo_1,foo_0,foo) where

import GHC.Base
import GHC.Num
import GHC.Types

foo_5 = \s_ε -> let
  rec β'2 = 
        let rec'3 β'3 = β'3 : ((rec (β'3 + 1)) ++ (rec'3 (β'3 * 2))) in
        β'2 : ((rec (β'2 + 1)) ++ (rec'3 (β'2 * 2)))
  rec' β = 
        let rec'2 β' = β' : ((rec'2 (β' + 1)) ++ (rec' (β' * 2))) in
        β : ((rec'2 (β + 1)) ++ (rec' (β * 2)))
  in s_ε : ((rec (s_ε + 1)) ++ (rec' (s_ε * 2)))

foo_4 = \s_ε' -> 
  let rec'4 β'4 = β'4 : (rec'4 (β'4 + 1)) in
  s_ε' : (rec'4 (s_ε' + 1))

foo_3 = \s_ε'2 -> 
  let rec'5 s = s : (rec'5 s) in
  s_ε'2 : (rec'5 s_ε'2)

foo_1 = id

foo_0 = 
  let _ε = id _ε in
  _ε

foo = \f_ε -> 
  let _ε' = f_ε _ε' in
  _ε'
