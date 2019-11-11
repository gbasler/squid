-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  34
-- Incl. one-shot:   0
-- Case reductions:  0
-- Field reductions: 0
-- Case commutings:  0
-- Total nodes: 785; Boxes: 264; Branches: 260
-- Apps: 124; Lams: 16

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
  rec' p'3 = let
        _6 = p'3 * (2::Int)
        rec'5 p'5 = 
              let _11 = p'5 + (1::Int) in
              p'5 : (case mod p'5 (2::Int) == (0::Int) of { True -> _11 : (case mod _11 (2::Int) == (0::Int) of { True -> (rec'5 (_11 + (1::Int))); False -> (rec' (_11 * (2::Int))) }); False -> (rec' (p'5 * (2::Int))) })
        rec'4 p'4 = let
              _7 = p'4 * (2::Int)
              _10 = _7 + (1::Int)
              _8 = p'4 + (1::Int)
              _9 = _8 * (2::Int)
              in p'4 : (case mod p'4 (2::Int) == (0::Int) of { True -> _8 : (case mod _8 (2::Int) == (0::Int) of { True -> (rec'4 (_8 + (1::Int))); False -> _9 : (case mod _9 (2::Int) == (0::Int) of { True -> (rec'4 (_9 + (1::Int))); False -> (rec' (_9 * (2::Int))) }) }); False -> _7 : (case mod _7 (2::Int) == (0::Int) of { True -> _10 : (case mod _10 (2::Int) == (0::Int) of { True -> (rec'4 (_10 + (1::Int))); False -> (rec' (_10 * (2::Int))) }); False -> (rec' (_7 * (2::Int))) }) })
        in p'3 : (case mod p'3 (2::Int) == (0::Int) of { True -> (rec'4 (p'3 + (1::Int))); False -> _6 : (case mod _6 (2::Int) == (0::Int) of { True -> (rec'5 (_6 + (1::Int))); False -> (rec' (_6 * (2::Int))) }) })
  rec p = let
        _0 = p + (1::Int)
        rec'2 p'2 = 
              let _5 = p'2 * (2::Int) in
              p'2 : (case mod p'2 (2::Int) == (0::Int) of { True -> (rec (p'2 + (1::Int))); False -> _5 : (case mod _5 (2::Int) == (0::Int) of { True -> (rec (_5 + (1::Int))); False -> (rec'2 (_5 * (2::Int))) }) })
        rec'3 p' = let
              _1 = p' * (2::Int)
              _4 = _1 + (1::Int)
              _2 = p' + (1::Int)
              _3 = _2 * (2::Int)
              in p' : (case mod p' (2::Int) == (0::Int) of { True -> _2 : (case mod _2 (2::Int) == (0::Int) of { True -> (rec (_2 + (1::Int))); False -> _3 : (case mod _3 (2::Int) == (0::Int) of { True -> (rec (_3 + (1::Int))); False -> (rec'3 (_3 * (2::Int))) }) }); False -> _1 : (case mod _1 (2::Int) == (0::Int) of { True -> _4 : (case mod _4 (2::Int) == (0::Int) of { True -> (rec (_4 + (1::Int))); False -> (rec'3 (_4 * (2::Int))) }); False -> (rec'3 (_1 * (2::Int))) }) })
        in p : (case mod p (2::Int) == (0::Int) of { True -> _0 : (case mod _0 (2::Int) == (0::Int) of { True -> (rec (_0 + (1::Int))); False -> (rec'2 (_0 * (2::Int))) }); False -> (rec'3 (p * (2::Int))) })
  in s : (case mod s (2::Int) == (0::Int) of { True -> (rec (s + (1::Int))); False -> (rec' (s * (2::Int))) })

foo_5 = \s -> let
  rec p'3 = let
        rec'4 p'4 = let
              _6 = p'4 + (1::Int)
              _7 = _6 * (2::Int)
              _8 = p'4 * (2::Int)
              _9 = _8 + (1::Int)
              in p'4 : ((_6 : ((rec (_6 + (1::Int))) ++ (_7 : ((rec (_7 + (1::Int))) ++ (rec'4 (_7 * (2::Int))))))) ++ (_8 : ((_9 : ((rec (_9 + (1::Int))) ++ (rec'4 (_9 * (2::Int))))) ++ (rec'4 (_8 * (2::Int))))))
        _10 = p'3 + (1::Int)
        rec'5 p'5 = 
              let _11 = p'5 * (2::Int) in
              p'5 : ((rec (p'5 + (1::Int))) ++ (_11 : ((rec (_11 + (1::Int))) ++ (rec'5 (_11 * (2::Int))))))
        in p'3 : ((_10 : ((rec (_10 + (1::Int))) ++ (rec'5 (_10 * (2::Int))))) ++ (rec'4 (p'3 * (2::Int))))
  rec' p = let
        rec'2 p' = let
              _0 = p' + (1::Int)
              _1 = _0 * (2::Int)
              _2 = p' * (2::Int)
              _3 = _2 + (1::Int)
              in p' : ((_0 : ((rec'2 (_0 + (1::Int))) ++ (_1 : ((rec'2 (_1 + (1::Int))) ++ (rec' (_1 * (2::Int))))))) ++ (_2 : ((_3 : ((rec'2 (_3 + (1::Int))) ++ (rec' (_3 * (2::Int))))) ++ (rec' (_2 * (2::Int))))))
        _4 = p * (2::Int)
        rec'3 p'2 = 
              let _5 = p'2 + (1::Int) in
              p'2 : ((_5 : ((rec'3 (_5 + (1::Int))) ++ (rec' (_5 * (2::Int))))) ++ (rec' (p'2 * (2::Int))))
        in p : ((rec'2 (p + (1::Int))) ++ (_4 : ((rec'3 (_4 + (1::Int))) ++ (rec' (_4 * (2::Int))))))
  in s : ((rec (s + (1::Int))) ++ (rec' (s * (2::Int))))

foo_4 = \s -> 
  let rec p = 
        let _0 = p + (1::Int) in
        p : (_0 : (rec (_0 + (1::Int)))) in
  s : (rec (s + (1::Int)))

foo_3 = \s -> 
  let rec s' = s' : (s' : (rec s')) in
  s : (rec s)

foo_1 = id
