-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  34
-- Incl. one-shot:  0
-- Case reductions:  0
-- Field reductions:  0
-- Total nodes: 985; Boxes: 335; Branches: 325
-- Apps: 155; Lams: 20

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module HigherOrderRecLocal (foo_6,foo_5,foo_4,foo_3,foo_1) where

import GHC.Base
import GHC.Classes
import GHC.Num
import GHC.Real
import GHC.Types

foo_6 = \s -> let
  rec' p'2 = 
        let rec'3 p'3 = p'3 : (case mod p'3 2 == 0 of { False -> (rec'3 (p'3 * 2)); True -> (rec' (p'3 + 1)) }) in
        p'2 : (case mod p'2 2 == 0 of { False -> (rec'3 (p'2 * 2)); True -> (rec' (p'2 + 1)) })
  rec p = 
        let rec'2 p' = p' : (case mod p' 2 == 0 of { False -> (rec (p' * 2)); True -> (rec'2 (p' + 1)) }) in
        p : (case mod p 2 == 0 of { False -> (rec (p * 2)); True -> (rec'2 (p + 1)) })
  in s : (case mod s 2 == 0 of { False -> (rec (s * 2)); True -> (rec' (s + 1)) })

foo_5 = \s -> let
  rec p'2 = 
        let rec'3 p'3 = p'3 : ((rec (p'3 + 1)) ++ (rec'3 (p'3 * 2))) in
        p'2 : ((rec (p'2 + 1)) ++ (rec'3 (p'2 * 2)))
  rec' p = 
        let rec'2 p' = p' : ((rec'2 (p' + 1)) ++ (rec' (p' * 2))) in
        p : ((rec'2 (p + 1)) ++ (rec' (p * 2)))
  in s : ((rec (s + 1)) ++ (rec' (s * 2)))

foo_4 = \s -> 
  let rec p = p : (rec (p + 1)) in
  s : (rec (s + 1))

foo_3 = \s -> 
  let rec s' = s' : (rec s') in
  s : (rec s)

foo_1 = id
