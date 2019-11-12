-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  37
-- Incl. one-shot:   0
-- Case reductions:  0
-- Field reductions: 0
-- Case commutings:  0
-- Total nodes: 1641; Boxes: 538; Branches: 544
-- Apps: 254; Lams: 34

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module HigherOrderRecLocal (foo_6_5,foo_6,foo_5_10,foo_5,foo_4,foo_3,foo_1,foo_0,foo) where

import GHC.Base
import GHC.Classes
import GHC.List
import GHC.Num
import GHC.Real
import GHC.Types

foo_6_5 = \x -> let
  _0 = x + (1::Int)
  rec p'10 = let
        _5 = p'10 * (2::Int)
        rec'11 p'11 = p'11 : (case mod p'11 (2::Int) == (0::Int) of { True -> (rec (p'11 + (1::Int))); False -> (rec'11 (p'11 * (2::Int))) })
        in p'10 : (case mod p'10 (2::Int) == (0::Int) of { True -> (rec (p'10 + (1::Int))); False -> _5 : (case mod _5 (2::Int) == (0::Int) of { True -> (rec (_5 + (1::Int))); False -> (rec'11 (_5 * (2::Int))) }) })
  _1 = _0 * (2::Int)
  rec'2 p'8 = 
        let rec'10 p'9 = p'9 : (case mod p'9 (2::Int) == (0::Int) of { True -> (rec'10 (p'9 + (1::Int))); False -> (rec'2 (p'9 * (2::Int))) }) in
        p'8 : (case mod p'8 (2::Int) == (0::Int) of { True -> (rec'10 (p'8 + (1::Int))); False -> (rec'2 (p'8 * (2::Int))) })
  rec' p'6 = 
        let rec'9 p'7 = p'7 : (case mod p'7 (2::Int) == (0::Int) of { True -> (rec' (p'7 + (1::Int))); False -> (rec'9 (p'7 * (2::Int))) }) in
        p'6 : (case mod p'6 (2::Int) == (0::Int) of { True -> (rec' (p'6 + (1::Int))); False -> (rec'9 (p'6 * (2::Int))) })
  _2 = x * (2::Int)
  rec'5 p'4 = let
        _4 = p'4 + (1::Int)
        rec'8 p'5 = p'5 : (case mod p'5 (2::Int) == (0::Int) of { True -> (rec'8 (p'5 + (1::Int))); False -> (rec'5 (p'5 * (2::Int))) })
        in p'4 : (case mod p'4 (2::Int) == (0::Int) of { True -> _4 : (case mod _4 (2::Int) == (0::Int) of { True -> (rec'8 (_4 + (1::Int))); False -> (rec'5 (_4 * (2::Int))) }); False -> (rec'5 (p'4 * (2::Int))) })
  rec'3 p'2 = 
        let rec'7 p'3 = p'3 : (case mod p'3 (2::Int) == (0::Int) of { True -> (rec'3 (p'3 + (1::Int))); False -> (rec'7 (p'3 * (2::Int))) }) in
        p'2 : (case mod p'2 (2::Int) == (0::Int) of { True -> (rec'3 (p'2 + (1::Int))); False -> (rec'7 (p'2 * (2::Int))) })
  _3 = _2 + (1::Int)
  rec'4 p = 
        let rec'6 p' = p' : (case mod p' (2::Int) == (0::Int) of { True -> (rec'6 (p' + (1::Int))); False -> (rec'4 (p' * (2::Int))) }) in
        p : (case mod p (2::Int) == (0::Int) of { True -> (rec'6 (p + (1::Int))); False -> (rec'4 (p * (2::Int))) })
  in GHC.List.take (5::Int) (x : (case mod x (2::Int) == (0::Int) of { True -> _0 : (case mod _0 (2::Int) == (0::Int) of { True -> (rec (_0 + (1::Int))); False -> _1 : (case mod _1 (2::Int) == (0::Int) of { True -> (rec' (_1 + (1::Int))); False -> (rec'2 (_1 * (2::Int))) }) }); False -> _2 : (case mod _2 (2::Int) == (0::Int) of { True -> _3 : (case mod _3 (2::Int) == (0::Int) of { True -> (rec'3 (_3 + (1::Int))); False -> (rec'4 (_3 * (2::Int))) }); False -> (rec'5 (_2 * (2::Int))) }) }))

foo_6 = \s -> let
  _0 = s + (1::Int)
  rec p'10 = let
        _5 = p'10 * (2::Int)
        rec'11 p'11 = p'11 : (case mod p'11 (2::Int) == (0::Int) of { True -> (rec (p'11 + (1::Int))); False -> (rec'11 (p'11 * (2::Int))) })
        in p'10 : (case mod p'10 (2::Int) == (0::Int) of { True -> (rec (p'10 + (1::Int))); False -> _5 : (case mod _5 (2::Int) == (0::Int) of { True -> (rec (_5 + (1::Int))); False -> (rec'11 (_5 * (2::Int))) }) })
  rec'2 p'8 = 
        let rec'10 p'9 = p'9 : (case mod p'9 (2::Int) == (0::Int) of { True -> (rec'10 (p'9 + (1::Int))); False -> (rec'2 (p'9 * (2::Int))) }) in
        p'8 : (case mod p'8 (2::Int) == (0::Int) of { True -> (rec'10 (p'8 + (1::Int))); False -> (rec'2 (p'8 * (2::Int))) })
  _1 = _0 * (2::Int)
  rec' p'6 = 
        let rec'9 p'7 = p'7 : (case mod p'7 (2::Int) == (0::Int) of { True -> (rec' (p'7 + (1::Int))); False -> (rec'9 (p'7 * (2::Int))) }) in
        p'6 : (case mod p'6 (2::Int) == (0::Int) of { True -> (rec' (p'6 + (1::Int))); False -> (rec'9 (p'6 * (2::Int))) })
  _2 = s * (2::Int)
  rec'5 p'4 = let
        _4 = p'4 + (1::Int)
        rec'8 p'5 = p'5 : (case mod p'5 (2::Int) == (0::Int) of { True -> (rec'8 (p'5 + (1::Int))); False -> (rec'5 (p'5 * (2::Int))) })
        in p'4 : (case mod p'4 (2::Int) == (0::Int) of { True -> _4 : (case mod _4 (2::Int) == (0::Int) of { True -> (rec'8 (_4 + (1::Int))); False -> (rec'5 (_4 * (2::Int))) }); False -> (rec'5 (p'4 * (2::Int))) })
  _3 = _2 + (1::Int)
  rec'4 p'2 = 
        let rec'7 p'3 = p'3 : (case mod p'3 (2::Int) == (0::Int) of { True -> (rec'7 (p'3 + (1::Int))); False -> (rec'4 (p'3 * (2::Int))) }) in
        p'2 : (case mod p'2 (2::Int) == (0::Int) of { True -> (rec'7 (p'2 + (1::Int))); False -> (rec'4 (p'2 * (2::Int))) })
  rec'3 p = 
        let rec'6 p' = p' : (case mod p' (2::Int) == (0::Int) of { True -> (rec'3 (p' + (1::Int))); False -> (rec'6 (p' * (2::Int))) }) in
        p : (case mod p (2::Int) == (0::Int) of { True -> (rec'3 (p + (1::Int))); False -> (rec'6 (p * (2::Int))) })
  in s : (case mod s (2::Int) == (0::Int) of { True -> _0 : (case mod _0 (2::Int) == (0::Int) of { True -> (rec (_0 + (1::Int))); False -> _1 : (case mod _1 (2::Int) == (0::Int) of { True -> (rec' (_1 + (1::Int))); False -> (rec'2 (_1 * (2::Int))) }) }); False -> _2 : (case mod _2 (2::Int) == (0::Int) of { True -> _3 : (case mod _3 (2::Int) == (0::Int) of { True -> (rec'3 (_3 + (1::Int))); False -> (rec'4 (_3 * (2::Int))) }); False -> (rec'5 (_2 * (2::Int))) }) })

foo_5_10 = let
  _0 = (23::Int) + (1::Int)
  _1 = _0 * (2::Int)
  rec' p'10 = 
        let rec'11 p'11 = p'11 : ((rec' (p'11 + (1::Int))) ++ (rec'11 (p'11 * (2::Int)))) in
        p'10 : ((rec' (p'10 + (1::Int))) ++ (rec'11 (p'10 * (2::Int))))
  rec'2 p'8 = 
        let rec'10 p'9 = p'9 : ((rec'10 (p'9 + (1::Int))) ++ (rec'2 (p'9 * (2::Int)))) in
        p'8 : ((rec'10 (p'8 + (1::Int))) ++ (rec'2 (p'8 * (2::Int))))
  rec p'6 = let
        _5 = p'6 * (2::Int)
        rec'9 p'7 = p'7 : ((rec (p'7 + (1::Int))) ++ (rec'9 (p'7 * (2::Int))))
        in p'6 : ((rec (p'6 + (1::Int))) ++ (_5 : ((rec (_5 + (1::Int))) ++ (rec'9 (_5 * (2::Int))))))
  rec'4 p'4 = 
        let rec'8 p'5 = p'5 : ((rec'8 (p'5 + (1::Int))) ++ (rec'4 (p'5 * (2::Int)))) in
        p'4 : ((rec'8 (p'4 + (1::Int))) ++ (rec'4 (p'4 * (2::Int))))
  _2 = (23::Int) * (2::Int)
  _3 = _2 + (1::Int)
  rec'3 p'2 = 
        let rec'7 p'3 = p'3 : ((rec'3 (p'3 + (1::Int))) ++ (rec'7 (p'3 * (2::Int)))) in
        p'2 : ((rec'3 (p'2 + (1::Int))) ++ (rec'7 (p'2 * (2::Int))))
  rec'5 p = let
        _4 = p + (1::Int)
        rec'6 p' = p' : ((rec'6 (p' + (1::Int))) ++ (rec'5 (p' * (2::Int))))
        in p : ((_4 : ((rec'6 (_4 + (1::Int))) ++ (rec'5 (_4 * (2::Int))))) ++ (rec'5 (p * (2::Int))))
  in GHC.List.take (10::Int) ((23::Int) : ((_0 : ((rec (_0 + (1::Int))) ++ (_1 : ((rec' (_1 + (1::Int))) ++ (rec'2 (_1 * (2::Int))))))) ++ (_2 : ((_3 : ((rec'3 (_3 + (1::Int))) ++ (rec'4 (_3 * (2::Int))))) ++ (rec'5 (_2 * (2::Int)))))))

foo_5 = \s -> let
  _0 = s + (1::Int)
  _1 = _0 * (2::Int)
  rec' p'10 = 
        let rec'11 p'11 = p'11 : ((rec' (p'11 + (1::Int))) ++ (rec'11 (p'11 * (2::Int)))) in
        p'10 : ((rec' (p'10 + (1::Int))) ++ (rec'11 (p'10 * (2::Int))))
  rec'2 p'8 = 
        let rec'10 p'9 = p'9 : ((rec'10 (p'9 + (1::Int))) ++ (rec'2 (p'9 * (2::Int)))) in
        p'8 : ((rec'10 (p'8 + (1::Int))) ++ (rec'2 (p'8 * (2::Int))))
  rec p'6 = let
        _5 = p'6 * (2::Int)
        rec'9 p'7 = p'7 : ((rec (p'7 + (1::Int))) ++ (rec'9 (p'7 * (2::Int))))
        in p'6 : ((rec (p'6 + (1::Int))) ++ (_5 : ((rec (_5 + (1::Int))) ++ (rec'9 (_5 * (2::Int))))))
  _2 = s * (2::Int)
  _3 = _2 + (1::Int)
  rec'4 p'4 = 
        let rec'8 p'5 = p'5 : ((rec'8 (p'5 + (1::Int))) ++ (rec'4 (p'5 * (2::Int)))) in
        p'4 : ((rec'8 (p'4 + (1::Int))) ++ (rec'4 (p'4 * (2::Int))))
  rec'3 p'2 = 
        let rec'7 p'3 = p'3 : ((rec'3 (p'3 + (1::Int))) ++ (rec'7 (p'3 * (2::Int)))) in
        p'2 : ((rec'3 (p'2 + (1::Int))) ++ (rec'7 (p'2 * (2::Int))))
  rec'5 p = let
        _4 = p + (1::Int)
        rec'6 p' = p' : ((rec'6 (p' + (1::Int))) ++ (rec'5 (p' * (2::Int))))
        in p : ((_4 : ((rec'6 (_4 + (1::Int))) ++ (rec'5 (_4 * (2::Int))))) ++ (rec'5 (p * (2::Int))))
  in s : ((_0 : ((rec (_0 + (1::Int))) ++ (_1 : ((rec' (_1 + (1::Int))) ++ (rec'2 (_1 * (2::Int))))))) ++ (_2 : ((_3 : ((rec'3 (_3 + (1::Int))) ++ (rec'4 (_3 * (2::Int))))) ++ (rec'5 (_2 * (2::Int))))))

foo_4 = \s -> let
  _0 = s + (1::Int)
  rec p = p : (rec (p + (1::Int)))
  in s : (_0 : (rec (_0 + (1::Int))))

foo_3 = \s -> 
  let rec s' = s' : (rec s') in
  s : (s : (rec s))

foo_1 = id

foo_0 = 
  let _0 = id _0 in
  _0

foo = \f -> 
  let _0 = f _0 in
  _0
