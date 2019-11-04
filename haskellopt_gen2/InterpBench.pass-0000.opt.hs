-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  16
-- Incl. one-shot:  1
-- Case reductions:  5
-- Field reductions:  10
-- Total nodes: 187; Boxes: 37; Branches: 29
-- Apps: 52; Lams: 1

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Main (main,k,src) where

import Criterion.Main
import Criterion.Measurement.Types
import GHC.Base
import GHC.CString
import GHC.Classes
import GHC.List
import GHC.Num
import GHC.Tuple
import GHC.Types

main = 
  let _0 = negate (1::Int) in
  Criterion.Main.defaultMain (Criterion.Measurement.Types.bgroup (GHC.CString.unpackCString# "interp"#) ((Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "normal"#) $ Criterion.Measurement.Types.whnf (\x -> let
        _2 = GHC.List.drop _0 ((,) (0::Int) _0 : [])
        rec y = let
              rec' p = let
                    _1 = x + (let (,) _ arg = (let (:) arg _ = (let (:) _ arg = _2 in arg) in arg) in arg)
                    rec'2 y'3 = 
                          let rec'5 y'4 = case (let (:) _ arg = (let (:) _ arg = (let (:) _ arg = _2 in arg) in arg) in arg) of { (:) ρ'12 ρ'13 -> (case ρ'12 of { (,) ρ'14 ρ'15 -> (case ρ'14 == (0::Int) of { True -> (rec'2 y'4); False -> (case ρ'14 == (1::Int) of { True -> (rec' (y'4 + ρ'15)); False -> (case ρ'14 == (2::Int) of { True -> (case _1 > (0::Int) of { True -> (rec y'4); False -> (rec'5 y'4) }); False -> negate (1::Int) }) }) }) }); [] -> y'4 } in
                          case (let (:) _ arg = (let (:) _ arg = _2 in arg) in arg) of { (:) ρ'16 ρ'17 -> (case ρ'16 of { (,) ρ'18 ρ'19 -> (case ρ'18 == (0::Int) of { True -> (rec'2 y'3); False -> (case ρ'18 == (1::Int) of { True -> (rec' (y'3 + ρ'19)); False -> (case ρ'18 == (2::Int) of { True -> (case _1 > (0::Int) of { True -> (rec y'3); False -> (rec'5 y'3) }); False -> negate (1::Int) }) }) }) }); [] -> y'3 }
                    rec'3 y' = 
                          let rec'4 y'2 = case (let (:) _ arg = (let (:) _ arg = (let (:) _ arg = _2 in arg) in arg) in arg) of { (:) ρ'4 ρ'5 -> (case ρ'4 of { (,) ρ'6 ρ'7 -> (case ρ'6 == (0::Int) of { True -> (rec'4 y'2); False -> (case ρ'6 == (1::Int) of { True -> (rec' (y'2 + ρ'7)); False -> (case ρ'6 == (2::Int) of { True -> (case (x + (let (,) _ arg = (let (:) arg _ = (let (:) _ arg = (let (:) _ arg = _2 in arg) in arg) in arg) in arg)) > (0::Int) of { True -> (rec y'2); False -> (rec'3 y'2) }); False -> negate (1::Int) }) }) }) }); [] -> y'2 } in
                          case (let (:) _ arg = (let (:) _ arg = _2 in arg) in arg) of { (:) ρ'8 ρ'9 -> (case ρ'8 of { (,) ρ'10 ρ'11 -> (case ρ'10 == (0::Int) of { True -> (rec'4 y'); False -> (case ρ'10 == (1::Int) of { True -> (rec' (y' + ρ'11)); False -> (case ρ'10 == (2::Int) of { True -> (case x > (0::Int) of { True -> (rec y'); False -> (rec'3 y') }); False -> negate (1::Int) }) }) }) }); [] -> y' }
                    in case (let (:) _ arg = _2 in arg) of { (:) ρ ρ' -> (case ρ of { (,) ρ'2 ρ'3 -> (case ρ'2 == (0::Int) of { True -> (rec'2 p); False -> (case ρ'2 == (1::Int) of { True -> (rec' (p + ρ'3)); False -> (case ρ'2 == (2::Int) of { True -> (case x > (0::Int) of { True -> (rec p); False -> (rec'3 p) }); False -> negate (1::Int) }) }) }) }); [] -> p }
              rec'7 y'8 = let
                    _4 = x + (let (,) _ arg = (let (:) arg _ = (let (:) _ arg = _2 in arg) in arg) in arg)
                    rec'12 y'10 = 
                          let rec'15 p'4 = case (let (:) _ arg = (let (:) _ arg = (let (:) _ arg = _2 in arg) in arg) in arg) of { (:) ρ'56 ρ'57 -> (case ρ'56 of { (,) ρ'58 ρ'59 -> (case ρ'58 == (0::Int) of { True -> (rec'12 p'4); False -> (case ρ'58 == (1::Int) of { True -> (rec'15 (p'4 + ρ'59)); False -> (case ρ'58 == (2::Int) of { True -> (case _4 > (0::Int) of { True -> (rec p'4); False -> (rec'7 p'4) }); False -> negate (1::Int) }) }) }) }); [] -> p'4 } in
                          case (let (:) _ arg = (let (:) _ arg = _2 in arg) in arg) of { (:) ρ'60 ρ'61 -> (case ρ'60 of { (,) ρ'62 ρ'63 -> (case ρ'62 == (0::Int) of { True -> (rec'12 y'10); False -> (case ρ'62 == (1::Int) of { True -> (rec'15 (y'10 + ρ'63)); False -> (case ρ'62 == (2::Int) of { True -> (case _4 > (0::Int) of { True -> (rec y'10); False -> (rec'7 y'10) }); False -> negate (1::Int) }) }) }) }); [] -> y'10 }
                    rec'13 p'3 = 
                          let rec'14 y'9 = case (let (:) _ arg = (let (:) _ arg = (let (:) _ arg = _2 in arg) in arg) in arg) of { (:) ρ'48 ρ'49 -> (case ρ'48 of { (,) ρ'50 ρ'51 -> (case ρ'50 == (0::Int) of { True -> (rec'14 y'9); False -> (case ρ'50 == (1::Int) of { True -> (rec'13 (y'9 + ρ'51)); False -> (case ρ'50 == (2::Int) of { True -> (case (x + (let (,) _ arg = (let (:) arg _ = (let (:) _ arg = (let (:) _ arg = _2 in arg) in arg) in arg) in arg)) > (0::Int) of { True -> (rec y'9); False -> (rec'7 y'9) }); False -> negate (1::Int) }) }) }) }); [] -> y'9 } in
                          case (let (:) _ arg = (let (:) _ arg = _2 in arg) in arg) of { (:) ρ'52 ρ'53 -> (case ρ'52 of { (,) ρ'54 ρ'55 -> (case ρ'54 == (0::Int) of { True -> (rec'14 p'3); False -> (case ρ'54 == (1::Int) of { True -> (rec'13 (p'3 + ρ'55)); False -> (case ρ'54 == (2::Int) of { True -> (case x > (0::Int) of { True -> (rec p'3); False -> (rec'7 p'3) }); False -> negate (1::Int) }) }) }) }); [] -> p'3 }
                    in case (let (:) _ arg = _2 in arg) of { (:) ρ'44 ρ'45 -> (case ρ'44 of { (,) ρ'46 ρ'47 -> (case ρ'46 == (0::Int) of { True -> (rec'12 y'8); False -> (case ρ'46 == (1::Int) of { True -> (rec'13 (y'8 + ρ'47)); False -> (case ρ'46 == (2::Int) of { True -> (case x > (0::Int) of { True -> (rec y'8); False -> (rec'7 y'8) }); False -> negate (1::Int) }) }) }) }); [] -> y'8 }
              _3 = x + (let (,) _ arg = (let (:) arg _ = _2 in arg) in arg)
              rec'6 y'5 = let
                    rec'8 y'6 = 
                          let rec'9 p' = case (let (:) _ arg = (let (:) _ arg = (let (:) _ arg = _2 in arg) in arg) in arg) of { (:) ρ'24 ρ'25 -> (case ρ'24 of { (,) ρ'26 ρ'27 -> (case ρ'26 == (0::Int) of { True -> (rec'6 p'); False -> (case ρ'26 == (1::Int) of { True -> (rec'9 (p' + ρ'27)); False -> (case ρ'26 == (2::Int) of { True -> (case _3 > (0::Int) of { True -> (rec p'); False -> (rec'8 p') }); False -> negate (1::Int) }) }) }) }); [] -> p' } in
                          case (let (:) _ arg = (let (:) _ arg = _2 in arg) in arg) of { (:) ρ'28 ρ'29 -> (case ρ'28 of { (,) ρ'30 ρ'31 -> (case ρ'30 == (0::Int) of { True -> (rec'6 y'6); False -> (case ρ'30 == (1::Int) of { True -> (rec'9 (y'6 + ρ'31)); False -> (case ρ'30 == (2::Int) of { True -> (case _3 > (0::Int) of { True -> (rec y'6); False -> (rec'8 y'6) }); False -> negate (1::Int) }) }) }) }); [] -> y'6 }
                    rec'10 p'2 = 
                          let rec'11 y'7 = case (let (:) _ arg = (let (:) _ arg = (let (:) _ arg = _2 in arg) in arg) in arg) of { (:) ρ'36 ρ'37 -> (case ρ'36 of { (,) ρ'38 ρ'39 -> (case ρ'38 == (0::Int) of { True -> (rec'6 y'7); False -> (case ρ'38 == (1::Int) of { True -> (rec'10 (y'7 + ρ'39)); False -> (case ρ'38 == (2::Int) of { True -> (case _3 > (0::Int) of { True -> (rec y'7); False -> (rec'11 y'7) }); False -> negate (1::Int) }) }) }) }); [] -> y'7 } in
                          case (let (:) _ arg = (let (:) _ arg = _2 in arg) in arg) of { (:) ρ'40 ρ'41 -> (case ρ'40 of { (,) ρ'42 ρ'43 -> (case ρ'42 == (0::Int) of { True -> (rec'6 p'2); False -> (case ρ'42 == (1::Int) of { True -> (rec'10 (p'2 + ρ'43)); False -> (case ρ'42 == (2::Int) of { True -> (case _3 > (0::Int) of { True -> (rec p'2); False -> (rec'11 p'2) }); False -> negate (1::Int) }) }) }) }); [] -> p'2 }
                    in case (let (:) _ arg = _2 in arg) of { (:) ρ'32 ρ'33 -> (case ρ'32 of { (,) ρ'34 ρ'35 -> (case ρ'34 == (0::Int) of { True -> (rec'6 y'5); False -> (case ρ'34 == (1::Int) of { True -> (rec'10 (y'5 + ρ'35)); False -> (case ρ'34 == (2::Int) of { True -> (case _3 > (0::Int) of { True -> (rec y'5); False -> (rec'8 y'5) }); False -> negate (1::Int) }) }) }) }); [] -> y'5 }
              in case _2 of { (:) ρ'20 ρ'21 -> (case ρ'20 of { (,) ρ'22 ρ'23 -> (case ρ'22 == (0::Int) of { True -> (rec'6 y); False -> (case ρ'22 == (1::Int) of { True -> (rec' (y + ρ'23)); False -> (case ρ'22 == (2::Int) of { True -> (case x > (0::Int) of { True -> (rec y); False -> (rec'7 y) }); False -> negate (1::Int) }) }) }) }); [] -> y }
        in case (0::Int) == (0::Int) of { True -> (123::Int); False -> (case (0::Int) == (1::Int) of { True -> (123::Int) + _0; False -> (case (0::Int) == (2::Int) of { True -> (case x > (0::Int) of { True -> (rec (123::Int)); False -> (123::Int) }); False -> negate (1::Int) }) }) }) ((1000::Int) * (100::Int))) : []) : [])

k = (1000::Int) * (100::Int)

src = (,) (0::Int) (negate (1::Int)) : []
