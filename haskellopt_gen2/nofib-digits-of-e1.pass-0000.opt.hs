-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  13
-- Incl. one-shot:   0
-- Case reductions:  3
-- Field reductions: 22
-- Case commutings:  0
-- Total nodes: 936; Boxes: 152; Branches: 149
-- Apps: 312; Lams: 10

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}
{-# LANGUAGE FlexibleContexts  #-}

module Main (main,e,takeDigits,ratTrans,eContFrac,hash) where

import Data.Tuple.Select
import Control.Exception.Base
import Criterion.Main
import Criterion.Measurement.Types
import Data.Foldable
import GHC.Base
import GHC.CString
import GHC.Classes
import GHC.Num
import GHC.Real
import GHC.Show
import GHC.Tuple
import GHC.Types

main = Criterion.Main.defaultMain ((Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "main"#) $ Criterion.Measurement.Types.whnf (\d -> let
  rec ψ n = let
        rec'21 _fε'5 π'25 π'26 _89 _90 π'27 = let
              _91 = div _89 _90
              rec'22 xs'5 π'28 π'29 _92 _93 = 
                    let _94 = div π'29 _92 in
                    case ((signum _93 == signum _92) || (abs _93 < abs _92)) && ((((_93 + _92) * _94) <= (π'28 + π'29)) && ((((_93 + _92) * _94) + (_93 + _92)) > (π'28 + π'29))) of { True -> _94 : (rec'22 xs'5 _93 _92 (π'29 - (_94 * _92)) (π'28 - (_94 * _93))); False -> (rec'21 (let (:) _ arg = xs'5 in arg) (let (:) _ arg = xs'5 in arg) π'29 (π'28 + ((let (:) arg _ = xs'5 in arg) * π'29)) (_93 + ((let (:) arg _ = xs'5 in arg) * _92)) _92) }
              in case ((signum π'27 == signum _90) || (abs π'27 < abs _90)) && ((((π'27 + _90) * _91) <= (π'26 + _89)) && ((((π'27 + _90) * _91) + (π'27 + _90)) > (π'26 + _89))) of { True -> _91 : (rec'22 π'25 π'27 _90 (_89 - (_91 * _90)) (π'26 - (_91 * π'27))); False -> (rec'21 (let (:) _ arg = _fε'5 in arg) (let (:) _ arg = _fε'5 in arg) _89 (π'26 + ((let (:) arg _ = _fε'5 in arg) * _89)) (π'27 + ((let (:) arg _ = _fε'5 in arg) * _90)) _90) }
        _95 = div (0::Int) (1::Int)
        rec'23 xs'6 π'30 π'31 _96 _97 = let
              _98 = div π'31 _96
              rec'24 _fε'6 π'32 π'33 _99 _100 π'34 = 
                    let _101 = div _99 _100 in
                    case ((signum π'34 == signum _100) || (abs π'34 < abs _100)) && ((((π'34 + _100) * _101) <= (π'33 + _99)) && ((((π'34 + _100) * _101) + (π'34 + _100)) > (π'33 + _99))) of { True -> _101 : (rec'23 π'32 π'34 _100 (_99 - (_101 * _100)) (π'33 - (_101 * π'34))); False -> (rec'24 (let (:) _ arg = _fε'6 in arg) (let (:) _ arg = _fε'6 in arg) _99 (π'33 + ((let (:) arg _ = _fε'6 in arg) * _99)) (π'34 + ((let (:) arg _ = _fε'6 in arg) * _100)) _100) }
              in case ((signum _97 == signum _96) || (abs _97 < abs _96)) && ((((_97 + _96) * _98) <= (π'30 + π'31)) && ((((_97 + _96) * _98) + (_97 + _96)) > (π'30 + π'31))) of { True -> _98 : (rec'23 xs'6 _97 _96 (π'31 - (_98 * _96)) (π'30 - (_98 * _97))); False -> (rec'24 (let (:) _ arg = xs'6 in arg) (let (:) _ arg = xs'6 in arg) π'31 (π'30 + ((let (:) arg _ = xs'6 in arg) * π'31)) (_97 + ((let (:) arg _ = xs'6 in arg) * _96)) _96) }
        in case n == (0::Int) of { True -> []; False -> (let (:) arg _ = ψ in arg) : (rec (case ((signum (0::Int) == signum (1::Int)) || (abs (0::Int) < abs (1::Int))) && (((((0::Int) + (1::Int)) * _95) <= ((10::Int) + (0::Int))) && (((((0::Int) + (1::Int)) * _95) + ((0::Int) + (1::Int))) > ((10::Int) + (0::Int)))) of { True -> _95 : (rec'23 (let (:) _ arg = ψ in arg) (0::Int) (1::Int) ((0::Int) - (_95 * (1::Int))) ((10::Int) - (_95 * (0::Int)))); False -> (rec'21 (let (:) _ arg = (let (:) _ arg = ψ in arg) in arg) (let (:) _ arg = (let (:) _ arg = ψ in arg) in arg) (0::Int) ((10::Int) + ((let (:) arg _ = (let (:) _ arg = ψ in arg) in arg) * (0::Int))) ((0::Int) + ((let (:) arg _ = (let (:) _ arg = ψ in arg) in arg) * (1::Int))) (1::Int)) }) (n - (1::Int))) }
  _4 = (0::Int) + ((1::Int) * (1::Int))
  rec'20 _87 = 
        let _88 = _87 : ((1::Int) : sel2 (rec'20 (_87 + (2::Int)))) in
        (,) _88 ((1::Int) : _88)
  rec_call'6 = (rec'20 ((2::Int) + (2::Int)))
  _17 = sel1 rec_call'6
  _3 = (10::Int) + ((1::Int) * (0::Int))
  _16 = sel2 rec_call'6
  _11 = (1::Int) : _16
  _1 = (2::Int) : _11
  rec' xs'4 π'20 π'21 _81 _82 = let
        _83 = div π'21 _81
        rec'19 _fε'4 π'22 π'23 _84 _85 π'24 = 
              let _86 = div _84 _85 in
              case ((signum π'24 == signum _85) || (abs π'24 < abs _85)) && ((((π'24 + _85) * _86) <= (π'23 + _84)) && ((((π'24 + _85) * _86) + (π'24 + _85)) > (π'23 + _84))) of { True -> _86 : (rec' π'22 π'24 _85 (_84 - (_86 * _85)) (π'23 - (_86 * π'24))); False -> (rec'19 (let (:) _ arg = _fε'4 in arg) (let (:) _ arg = _fε'4 in arg) _84 (π'23 + ((let (:) arg _ = _fε'4 in arg) * _84)) (π'24 + ((let (:) arg _ = _fε'4 in arg) * _85)) _85) }
        in case ((signum _82 == signum _81) || (abs _82 < abs _81)) && ((((_82 + _81) * _83) <= (π'20 + π'21)) && ((((_82 + _81) * _83) + (_82 + _81)) > (π'20 + π'21))) of { True -> _83 : (rec' xs'4 _82 _81 (π'21 - (_83 * _81)) (π'20 - (_83 * _82))); False -> (rec'19 (let (:) _ arg = xs'4 in arg) (let (:) _ arg = xs'4 in arg) π'21 (π'20 + ((let (:) arg _ = xs'4 in arg) * π'21)) (_82 + ((let (:) arg _ = xs'4 in arg) * _81)) _81) }
  _0 = div (0::Int) (1::Int)
  in Data.Foldable.foldl' (\acc -> \c -> ord c + (acc * (31::Int))) (0::Int) (GHC.Show.show (case d == (0::Int) of { True -> []; False -> (2::Int) : (rec (case ((signum (0::Int) == signum (1::Int)) || (abs (0::Int) < abs (1::Int))) && (((((0::Int) + (1::Int)) * _0) <= ((10::Int) + (0::Int))) && (((((0::Int) + (1::Int)) * _0) + ((0::Int) + (1::Int))) > ((10::Int) + (0::Int)))) of { True -> _0 : (rec' ((1::Int) : _1) (0::Int) (1::Int) ((0::Int) - (_0 * (1::Int))) ((10::Int) - (_0 * (0::Int)))); False -> (let
        _2 = div _3 _4
        rec'18 _79 = 
              let _80 = _79 : ((1::Int) : sel1 (rec'18 (_79 + (2::Int)))) in
              (,) ((1::Int) : _80) _80
        rec_call'5 = (rec'18 ((2::Int) + (2::Int)))
        _5 = sel1 rec_call'5
        _6 = sel2 rec_call'5
        rec'2 _64 _65 _66 xs'3 π'15 π'16 _67 _68 = let
              _69 = div π'16 _67
              rec'17 _77 = 
                    let _78 = _77 : ((1::Int) : sel2 (rec'17 (_77 + (2::Int)))) in
                    (,) _78 ((1::Int) : _78)
              rec_call'3 = (rec'17 ((2::Int) + (2::Int)))
              _70 = sel2 rec_call'3
              rec'15 π'17 _fε'3 π'18 _71 _72 π'19 = let
                    _73 = div _71 _72
                    rec'16 _75 = 
                          let _76 = _75 : ((1::Int) : sel2 (rec'16 (_75 + (2::Int)))) in
                          (,) _76 ((1::Int) : _76)
                    rec_call'4 = (rec'16 ((2::Int) + (2::Int)))
                    _74 = sel2 rec_call'4
                    in case ((signum π'19 == signum _72) || (abs π'19 < abs _72)) && ((((π'19 + _72) * _73) <= (π'18 + _71)) && ((((π'19 + _72) * _73) + (π'19 + _72)) > (π'18 + _71))) of { True -> _73 : (rec'2 ((1::Int) : _74) _74 (sel1 rec_call'4) π'17 π'19 _72 (_71 - (_73 * _72)) (π'18 - (_73 * π'19))); False -> (rec'15 (let (:) _ arg = _fε'3 in arg) (let (:) _ arg = _fε'3 in arg) _71 (π'18 + ((let (:) arg _ = _fε'3 in arg) * _71)) (π'19 + ((let (:) arg _ = _fε'3 in arg) * _72)) _72) }
              in case ((signum _68 == signum _67) || (abs _68 < abs _67)) && ((((_68 + _67) * _69) <= (π'15 + π'16)) && ((((_68 + _67) * _69) + (_68 + _67)) > (π'15 + π'16))) of { True -> _69 : (rec'2 ((1::Int) : _70) _70 (sel1 rec_call'3) xs'3 _68 _67 (π'16 - (_69 * _67)) (π'15 - (_69 * _68))); False -> (rec'15 (let (:) _ arg = xs'3 in arg) (let (:) _ arg = xs'3 in arg) π'16 (π'15 + ((let (:) arg _ = xs'3 in arg) * π'16)) (_68 + ((let (:) arg _ = xs'3 in arg) * _67)) _67) }
        in case ((signum (1::Int) == signum _4) || (abs (1::Int) < abs _4)) && (((((1::Int) + _4) * _2) <= ((0::Int) + _3)) && (((((1::Int) + _4) * _2) + ((1::Int) + _4)) > ((0::Int) + _3))) of { True -> _2 : (rec'2 ((1::Int) : _5) _5 _6 _1 (1::Int) _4 (_3 - (_2 * _4)) ((0::Int) - (_2 * (1::Int)))); False -> (let
              _7 = (0::Int) + ((2::Int) * _3)
              _8 = (1::Int) + ((2::Int) * _4)
              rec'14 _62 = 
                    let _63 = _62 : ((1::Int) : sel1 (rec'14 (_62 + (2::Int)))) in
                    (,) ((1::Int) : _63) _63
              rec_call = (rec'14 ((2::Int) + (2::Int)))
              _10 = sel2 rec_call
              _9 = div _7 _8
              rec'3 _42 _43 _44 _45 xs'2 π'10 π'11 _46 _47 = let
                    _48 = div π'11 _46
                    rec'13 _60 = 
                          let _61 = _60 : ((1::Int) : sel2 (rec'13 (_60 + (2::Int)))) in
                          (,) _61 ((1::Int) : _61)
                    rec_call'2 = (rec'13 ((2::Int) + (2::Int)))
                    _49 = sel1 rec_call'2
                    _50 = sel2 rec_call'2
                    rec'11 _51 _52 π'12 _fε'2 π'13 _53 _54 π'14 = let
                          _55 = div _53 _54
                          rec'12 _58 = 
                                let _59 = _58 : ((1::Int) : sel2 (rec'12 (_58 + (2::Int)))) in
                                (,) _59 ((1::Int) : _59)
                          rec_call' = (rec'12 ((2::Int) + (2::Int)))
                          _56 = sel1 rec_call'
                          _57 = sel2 rec_call'
                          in case ((signum π'14 == signum _54) || (abs π'14 < abs _54)) && ((((π'14 + _54) * _55) <= (π'13 + _53)) && ((((π'14 + _54) * _55) + (π'14 + _54)) > (π'13 + _53))) of { True -> _55 : (rec'3 _56 _57 _52 _51 π'12 π'14 _54 (_53 - (_55 * _54)) (π'13 - (_55 * π'14))); False -> (rec'11 _56 _57 (let (:) _ arg = _fε'2 in arg) (let (:) _ arg = _fε'2 in arg) _53 (π'13 + ((let (:) arg _ = _fε'2 in arg) * _53)) (π'14 + ((let (:) arg _ = _fε'2 in arg) * _54)) _54) }
                    in case ((signum _47 == signum _46) || (abs _47 < abs _46)) && ((((_47 + _46) * _48) <= (π'10 + π'11)) && ((((_47 + _46) * _48) + (_47 + _46)) > (π'10 + π'11))) of { True -> _48 : (rec'3 _49 _50 _43 _42 xs'2 _47 _46 (π'11 - (_48 * _46)) (π'10 - (_48 * _47))); False -> (rec'11 _49 _50 (let (:) _ arg = xs'2 in arg) (let (:) _ arg = xs'2 in arg) π'11 (π'10 + ((let (:) arg _ = xs'2 in arg) * π'11)) (_47 + ((let (:) arg _ = xs'2 in arg) * _46)) _46) }
              in case ((signum _4 == signum _8) || (abs _4 < abs _8)) && ((((_4 + _8) * _9) <= (_3 + _7)) && ((((_4 + _8) * _9) + (_4 + _8)) > (_3 + _7))) of { True -> _9 : (rec'3 _10 (sel1 rec_call) _5 _6 _11 _4 _8 (_7 - (_9 * _8)) (_3 - (_9 * _4))); False -> (let
                    _12 = _3 + ((1::Int) * _7)
                    _13 = _4 + ((1::Int) * _8)
                    _14 = div _12 _13
                    rec'4 _25 _26 _27 xs' π'5 π'6 _28 _29 = let
                          rec'8 _30 _31 π'7 _fε' π'8 _32 _33 π'9 = let
                                _34 = (2::Int) + (2::Int)
                                rec'9 _37 = (1::Int) : (_37 : ((1::Int) : (rec'9 (_37 + (2::Int)))))
                                _36 = _34 : ((1::Int) : (rec'9 (_34 + (2::Int))))
                                _35 = div _32 _33
                                in case ((signum π'9 == signum _33) || (abs π'9 < abs _33)) && ((((π'9 + _33) * _35) <= (π'8 + _32)) && ((((π'9 + _33) * _35) + (π'9 + _33)) > (π'8 + _32))) of { True -> _35 : (rec'4 _36 _31 _30 π'7 π'9 _33 (_32 - (_35 * _33)) (π'8 - (_35 * π'9))); False -> (rec'8 _31 _36 (let (:) _ arg = _fε' in arg) (let (:) _ arg = _fε' in arg) _32 (π'8 + ((let (:) arg _ = _fε' in arg) * _32)) (π'9 + ((let (:) arg _ = _fε' in arg) * _33)) _33) }
                          _40 = (2::Int) + (2::Int)
                          rec'10 _41 = (1::Int) : (_41 : ((1::Int) : (rec'10 (_41 + (2::Int)))))
                          _39 = _40 : ((1::Int) : (rec'10 (_40 + (2::Int))))
                          _38 = div π'6 _28
                          in case ((signum _29 == signum _28) || (abs _29 < abs _28)) && ((((_29 + _28) * _38) <= (π'5 + π'6)) && ((((_29 + _28) * _38) + (_29 + _28)) > (π'5 + π'6))) of { True -> _38 : (rec'4 _39 _25 _26 xs' _29 _28 (π'6 - (_38 * _28)) (π'5 - (_38 * _29))); False -> (rec'8 _25 _39 (let (:) _ arg = xs' in arg) (let (:) _ arg = xs' in arg) π'6 (π'5 + ((let (:) arg _ = xs' in arg) * π'6)) (_29 + ((let (:) arg _ = xs' in arg) * _28)) _28) }
                    _15 = (2::Int) + (2::Int)
                    rec'5 _24 = (1::Int) : (_24 : ((1::Int) : (rec'5 (_24 + (2::Int)))))
                    rec'6 π _fε π' _18 _19 π'2 = let
                          _20 = div _18 _19
                          rec'7 xs π'3 π'4 _21 _22 = 
                                let _23 = div π'4 _21 in
                                case ((signum _22 == signum _21) || (abs _22 < abs _21)) && ((((_22 + _21) * _23) <= (π'3 + π'4)) && ((((_22 + _21) * _23) + (_22 + _21)) > (π'3 + π'4))) of { True -> _23 : (rec'7 xs _22 _21 (π'4 - (_23 * _21)) (π'3 - (_23 * _22))); False -> (rec'6 (let (:) _ arg = xs in arg) (let (:) _ arg = xs in arg) π'4 (π'3 + ((let (:) arg _ = xs in arg) * π'4)) (_22 + ((let (:) arg _ = xs in arg) * _21)) _21) }
                          in case ((signum π'2 == signum _19) || (abs π'2 < abs _19)) && ((((π'2 + _19) * _20) <= (π' + _18)) && ((((π'2 + _19) * _20) + (π'2 + _19)) > (π' + _18))) of { True -> _20 : (rec'7 π π'2 _19 (_18 - (_20 * _19)) (π' - (_20 * π'2))); False -> (rec'6 (let (:) _ arg = _fε in arg) (let (:) _ arg = _fε in arg) _18 (π' + ((let (:) arg _ = _fε in arg) * _18)) (π'2 + ((let (:) arg _ = _fε in arg) * _19)) _19) }
                    in case ((signum _8 == signum _13) || (abs _8 < abs _13)) && ((((_8 + _13) * _14) <= (_7 + _12)) && ((((_8 + _13) * _14) + (_8 + _13)) > (_7 + _12))) of { True -> _14 : (rec'4 (_15 : ((1::Int) : (rec'5 (_15 + (2::Int))))) _10 _6 _16 _8 _13 (_12 - (_14 * _13)) (_7 - (_14 * _8))); False -> (rec'6 _17 _17 _12 (_7 + ((1::Int) * _12)) (_8 + ((1::Int) * _13)) _13) }) }) }) }) (d - (1::Int))) }))) (420::Int)) : [])

e = \n -> let
  rec'24 _100 = 
        let _101 = _100 : ((1::Int) : sel2 (rec'24 (_100 + (2::Int)))) in
        (,) _101 ((1::Int) : _101)
  rec_call = (rec'24 ((2::Int) + (2::Int)))
  _17 = sel2 rec_call
  _12 = (1::Int) : _17
  _3 = (0::Int) + ((1::Int) * (1::Int))
  _1 = (2::Int) : _12
  _5 = (10::Int) + ((1::Int) * (0::Int))
  _0 = div (0::Int) (1::Int)
  rec' xs'6 π'30 π'31 _94 _95 = let
        _96 = div π'31 _94
        rec'23 _fε'6 π'32 π'33 _97 _98 π'34 = 
              let _99 = div _97 _98 in
              case ((signum π'34 == signum _98) || (abs π'34 < abs _98)) && ((((π'34 + _98) * _99) <= (π'33 + _97)) && ((((π'34 + _98) * _99) + (π'34 + _98)) > (π'33 + _97))) of { True -> _99 : (rec' π'32 π'34 _98 (_97 - (_99 * _98)) (π'33 - (_99 * π'34))); False -> (rec'23 (let (:) _ arg = _fε'6 in arg) (let (:) _ arg = _fε'6 in arg) _97 (π'33 + ((let (:) arg _ = _fε'6 in arg) * _97)) (π'34 + ((let (:) arg _ = _fε'6 in arg) * _98)) _98) }
        in case ((signum _95 == signum _94) || (abs _95 < abs _94)) && ((((_95 + _94) * _96) <= (π'30 + π'31)) && ((((_95 + _94) * _96) + (_95 + _94)) > (π'30 + π'31))) of { True -> _96 : (rec' xs'6 _95 _94 (π'31 - (_96 * _94)) (π'30 - (_96 * _95))); False -> (rec'23 (let (:) _ arg = xs'6 in arg) (let (:) _ arg = xs'6 in arg) π'31 (π'30 + ((let (:) arg _ = xs'6 in arg) * π'31)) (_95 + ((let (:) arg _ = xs'6 in arg) * _94)) _94) }
  rec ψ n' = let
        _81 = div (0::Int) (1::Int)
        rec'19 xs'5 π'25 π'26 _88 _89 = let
              _90 = div π'26 _88
              rec'22 _fε'5 π'27 π'28 _91 _92 π'29 = 
                    let _93 = div _91 _92 in
                    case ((signum π'29 == signum _92) || (abs π'29 < abs _92)) && ((((π'29 + _92) * _93) <= (π'28 + _91)) && ((((π'29 + _92) * _93) + (π'29 + _92)) > (π'28 + _91))) of { True -> _93 : (rec'19 π'27 π'29 _92 (_91 - (_93 * _92)) (π'28 - (_93 * π'29))); False -> (rec'22 (let (:) _ arg = _fε'5 in arg) (let (:) _ arg = _fε'5 in arg) _91 (π'28 + ((let (:) arg _ = _fε'5 in arg) * _91)) (π'29 + ((let (:) arg _ = _fε'5 in arg) * _92)) _92) }
              in case ((signum _89 == signum _88) || (abs _89 < abs _88)) && ((((_89 + _88) * _90) <= (π'25 + π'26)) && ((((_89 + _88) * _90) + (_89 + _88)) > (π'25 + π'26))) of { True -> _90 : (rec'19 xs'5 _89 _88 (π'26 - (_90 * _88)) (π'25 - (_90 * _89))); False -> (rec'22 (let (:) _ arg = xs'5 in arg) (let (:) _ arg = xs'5 in arg) π'26 (π'25 + ((let (:) arg _ = xs'5 in arg) * π'26)) (_89 + ((let (:) arg _ = xs'5 in arg) * _88)) _88) }
        rec'20 _fε'4 π'20 π'21 _82 _83 π'22 = let
              _84 = div _82 _83
              rec'21 xs'4 π'23 π'24 _85 _86 = 
                    let _87 = div π'24 _85 in
                    case ((signum _86 == signum _85) || (abs _86 < abs _85)) && ((((_86 + _85) * _87) <= (π'23 + π'24)) && ((((_86 + _85) * _87) + (_86 + _85)) > (π'23 + π'24))) of { True -> _87 : (rec'21 xs'4 _86 _85 (π'24 - (_87 * _85)) (π'23 - (_87 * _86))); False -> (rec'20 (let (:) _ arg = xs'4 in arg) (let (:) _ arg = xs'4 in arg) π'24 (π'23 + ((let (:) arg _ = xs'4 in arg) * π'24)) (_86 + ((let (:) arg _ = xs'4 in arg) * _85)) _85) }
              in case ((signum π'22 == signum _83) || (abs π'22 < abs _83)) && ((((π'22 + _83) * _84) <= (π'21 + _82)) && ((((π'22 + _83) * _84) + (π'22 + _83)) > (π'21 + _82))) of { True -> _84 : (rec'21 π'20 π'22 _83 (_82 - (_84 * _83)) (π'21 - (_84 * π'22))); False -> (rec'20 (let (:) _ arg = _fε'4 in arg) (let (:) _ arg = _fε'4 in arg) _82 (π'21 + ((let (:) arg _ = _fε'4 in arg) * _82)) (π'22 + ((let (:) arg _ = _fε'4 in arg) * _83)) _83) }
        in case n' == (0::Int) of { True -> []; False -> (let (:) arg _ = ψ in arg) : (rec (case ((signum (0::Int) == signum (1::Int)) || (abs (0::Int) < abs (1::Int))) && (((((0::Int) + (1::Int)) * _81) <= ((10::Int) + (0::Int))) && (((((0::Int) + (1::Int)) * _81) + ((0::Int) + (1::Int))) > ((10::Int) + (0::Int)))) of { True -> _81 : (rec'19 (let (:) _ arg = ψ in arg) (0::Int) (1::Int) ((0::Int) - (_81 * (1::Int))) ((10::Int) - (_81 * (0::Int)))); False -> (rec'20 (let (:) _ arg = (let (:) _ arg = ψ in arg) in arg) (let (:) _ arg = (let (:) _ arg = ψ in arg) in arg) (0::Int) ((10::Int) + ((let (:) arg _ = (let (:) _ arg = ψ in arg) in arg) * (0::Int))) ((0::Int) + ((let (:) arg _ = (let (:) _ arg = ψ in arg) in arg) * (1::Int))) (1::Int)) }) (n' - (1::Int))) }
  in case n == (0::Int) of { True -> []; False -> (2::Int) : (rec (case ((signum (0::Int) == signum (1::Int)) || (abs (0::Int) < abs (1::Int))) && (((((0::Int) + (1::Int)) * _0) <= ((10::Int) + (0::Int))) && (((((0::Int) + (1::Int)) * _0) + ((0::Int) + (1::Int))) > ((10::Int) + (0::Int)))) of { True -> _0 : (rec' ((1::Int) : _1) (0::Int) (1::Int) ((0::Int) - (_0 * (1::Int))) ((10::Int) - (_0 * (0::Int)))); False -> (let
        _2 = sel1 rec_call
        _4 = div _5 _3
        rec'18 _79 = 
              let _80 = _79 : ((1::Int) : sel1 (rec'18 (_79 + (2::Int)))) in
              (,) ((1::Int) : _80) _80
        rec_call'4 = (rec'18 ((2::Int) + (2::Int)))
        _6 = sel1 rec_call'4
        rec'2 _64 _65 _66 xs'3 π'15 π'16 _67 _68 = let
              _69 = div π'16 _67
              rec'17 _77 = 
                    let _78 = _77 : ((1::Int) : sel2 (rec'17 (_77 + (2::Int)))) in
                    (,) _78 ((1::Int) : _78)
              rec_call'5 = (rec'17 ((2::Int) + (2::Int)))
              _70 = sel2 rec_call'5
              rec'15 π'17 _fε'3 π'18 _71 _72 π'19 = let
                    _73 = div _71 _72
                    rec'16 _75 = 
                          let _76 = _75 : ((1::Int) : sel2 (rec'16 (_75 + (2::Int)))) in
                          (,) _76 ((1::Int) : _76)
                    rec_call'6 = (rec'16 ((2::Int) + (2::Int)))
                    _74 = sel2 rec_call'6
                    in case ((signum π'19 == signum _72) || (abs π'19 < abs _72)) && ((((π'19 + _72) * _73) <= (π'18 + _71)) && ((((π'19 + _72) * _73) + (π'19 + _72)) > (π'18 + _71))) of { True -> _73 : (rec'2 ((1::Int) : _74) _74 (sel1 rec_call'6) π'17 π'19 _72 (_71 - (_73 * _72)) (π'18 - (_73 * π'19))); False -> (rec'15 (let (:) _ arg = _fε'3 in arg) (let (:) _ arg = _fε'3 in arg) _71 (π'18 + ((let (:) arg _ = _fε'3 in arg) * _71)) (π'19 + ((let (:) arg _ = _fε'3 in arg) * _72)) _72) }
              in case ((signum _68 == signum _67) || (abs _68 < abs _67)) && ((((_68 + _67) * _69) <= (π'15 + π'16)) && ((((_68 + _67) * _69) + (_68 + _67)) > (π'15 + π'16))) of { True -> _69 : (rec'2 ((1::Int) : _70) _70 (sel1 rec_call'5) xs'3 _68 _67 (π'16 - (_69 * _67)) (π'15 - (_69 * _68))); False -> (rec'15 (let (:) _ arg = xs'3 in arg) (let (:) _ arg = xs'3 in arg) π'16 (π'15 + ((let (:) arg _ = xs'3 in arg) * π'16)) (_68 + ((let (:) arg _ = xs'3 in arg) * _67)) _67) }
        _7 = sel2 rec_call'4
        in case ((signum (1::Int) == signum _3) || (abs (1::Int) < abs _3)) && (((((1::Int) + _3) * _4) <= ((0::Int) + _5)) && (((((1::Int) + _3) * _4) + ((1::Int) + _3)) > ((0::Int) + _5))) of { True -> _4 : (rec'2 ((1::Int) : _6) _6 _7 _1 (1::Int) _3 (_5 - (_4 * _3)) ((0::Int) - (_4 * (1::Int)))); False -> (let
              _8 = (0::Int) + ((2::Int) * _5)
              _9 = (1::Int) + ((2::Int) * _3)
              _10 = div _8 _9
              rec'14 _62 = 
                    let _63 = _62 : ((1::Int) : sel1 (rec'14 (_62 + (2::Int)))) in
                    (,) ((1::Int) : _63) _63
              rec_call' = (rec'14 ((2::Int) + (2::Int)))
              _11 = sel2 rec_call'
              rec'3 _42 _43 _44 _45 xs'2 π'10 π'11 _46 _47 = let
                    _48 = div π'11 _46
                    rec'13 _60 = 
                          let _61 = _60 : ((1::Int) : sel2 (rec'13 (_60 + (2::Int)))) in
                          (,) _61 ((1::Int) : _61)
                    rec_call'3 = (rec'13 ((2::Int) + (2::Int)))
                    _50 = sel2 rec_call'3
                    _49 = sel1 rec_call'3
                    rec'11 _51 _52 π'12 _fε'2 π'13 _53 _54 π'14 = let
                          rec'12 _55 = 
                                let _56 = _55 : ((1::Int) : sel2 (rec'12 (_55 + (2::Int)))) in
                                (,) _56 ((1::Int) : _56)
                          rec_call'2 = (rec'12 ((2::Int) + (2::Int)))
                          _58 = sel1 rec_call'2
                          _59 = sel2 rec_call'2
                          _57 = div _53 _54
                          in case ((signum π'14 == signum _54) || (abs π'14 < abs _54)) && ((((π'14 + _54) * _57) <= (π'13 + _53)) && ((((π'14 + _54) * _57) + (π'14 + _54)) > (π'13 + _53))) of { True -> _57 : (rec'3 _58 _59 _52 _51 π'12 π'14 _54 (_53 - (_57 * _54)) (π'13 - (_57 * π'14))); False -> (rec'11 _58 _59 (let (:) _ arg = _fε'2 in arg) (let (:) _ arg = _fε'2 in arg) _53 (π'13 + ((let (:) arg _ = _fε'2 in arg) * _53)) (π'14 + ((let (:) arg _ = _fε'2 in arg) * _54)) _54) }
                    in case ((signum _47 == signum _46) || (abs _47 < abs _46)) && ((((_47 + _46) * _48) <= (π'10 + π'11)) && ((((_47 + _46) * _48) + (_47 + _46)) > (π'10 + π'11))) of { True -> _48 : (rec'3 _49 _50 _43 _42 xs'2 _47 _46 (π'11 - (_48 * _46)) (π'10 - (_48 * _47))); False -> (rec'11 _49 _50 (let (:) _ arg = xs'2 in arg) (let (:) _ arg = xs'2 in arg) π'11 (π'10 + ((let (:) arg _ = xs'2 in arg) * π'11)) (_47 + ((let (:) arg _ = xs'2 in arg) * _46)) _46) }
              _14 = _3 + ((1::Int) * _9)
              in case ((signum _3 == signum _9) || (abs _3 < abs _9)) && ((((_3 + _9) * _10) <= (_5 + _8)) && ((((_3 + _9) * _10) + (_3 + _9)) > (_5 + _8))) of { True -> _10 : (rec'3 _11 (sel1 rec_call') _6 _7 _12 _3 _9 (_8 - (_10 * _9)) (_5 - (_10 * _3))); False -> (let
                    _13 = _5 + ((1::Int) * _8)
                    _15 = div _13 _14
                    rec'4 _25 _26 _27 xs' π'5 π'6 _28 _29 = let
                          _30 = div π'6 _28
                          _40 = (2::Int) + (2::Int)
                          rec'10 _41 = (1::Int) : (_41 : ((1::Int) : (rec'10 (_41 + (2::Int)))))
                          _31 = _40 : ((1::Int) : (rec'10 (_40 + (2::Int))))
                          rec'8 _32 _33 π'7 _fε' π'8 _34 _35 π'9 = let
                                _36 = (2::Int) + (2::Int)
                                rec'9 _39 = (1::Int) : (_39 : ((1::Int) : (rec'9 (_39 + (2::Int)))))
                                _38 = _36 : ((1::Int) : (rec'9 (_36 + (2::Int))))
                                _37 = div _34 _35
                                in case ((signum π'9 == signum _35) || (abs π'9 < abs _35)) && ((((π'9 + _35) * _37) <= (π'8 + _34)) && ((((π'9 + _35) * _37) + (π'9 + _35)) > (π'8 + _34))) of { True -> _37 : (rec'4 _38 _33 _32 π'7 π'9 _35 (_34 - (_37 * _35)) (π'8 - (_37 * π'9))); False -> (rec'8 _33 _38 (let (:) _ arg = _fε' in arg) (let (:) _ arg = _fε' in arg) _34 (π'8 + ((let (:) arg _ = _fε' in arg) * _34)) (π'9 + ((let (:) arg _ = _fε' in arg) * _35)) _35) }
                          in case ((signum _29 == signum _28) || (abs _29 < abs _28)) && ((((_29 + _28) * _30) <= (π'5 + π'6)) && ((((_29 + _28) * _30) + (_29 + _28)) > (π'5 + π'6))) of { True -> _30 : (rec'4 _31 _25 _26 xs' _29 _28 (π'6 - (_30 * _28)) (π'5 - (_30 * _29))); False -> (rec'8 _25 _31 (let (:) _ arg = xs' in arg) (let (:) _ arg = xs' in arg) π'6 (π'5 + ((let (:) arg _ = xs' in arg) * π'6)) (_29 + ((let (:) arg _ = xs' in arg) * _28)) _28) }
                    _16 = (2::Int) + (2::Int)
                    rec'5 _24 = (1::Int) : (_24 : ((1::Int) : (rec'5 (_24 + (2::Int)))))
                    rec'6 π _fε π' _18 _19 π'2 = let
                          _20 = div _18 _19
                          rec'7 xs π'3 π'4 _21 _22 = 
                                let _23 = div π'4 _21 in
                                case ((signum _22 == signum _21) || (abs _22 < abs _21)) && ((((_22 + _21) * _23) <= (π'3 + π'4)) && ((((_22 + _21) * _23) + (_22 + _21)) > (π'3 + π'4))) of { True -> _23 : (rec'7 xs _22 _21 (π'4 - (_23 * _21)) (π'3 - (_23 * _22))); False -> (rec'6 (let (:) _ arg = xs in arg) (let (:) _ arg = xs in arg) π'4 (π'3 + ((let (:) arg _ = xs in arg) * π'4)) (_22 + ((let (:) arg _ = xs in arg) * _21)) _21) }
                          in case ((signum π'2 == signum _19) || (abs π'2 < abs _19)) && ((((π'2 + _19) * _20) <= (π' + _18)) && ((((π'2 + _19) * _20) + (π'2 + _19)) > (π' + _18))) of { True -> _20 : (rec'7 π π'2 _19 (_18 - (_20 * _19)) (π' - (_20 * π'2))); False -> (rec'6 (let (:) _ arg = _fε in arg) (let (:) _ arg = _fε in arg) _18 (π' + ((let (:) arg _ = _fε in arg) * _18)) (π'2 + ((let (:) arg _ = _fε in arg) * _19)) _19) }
                    in case ((signum _9 == signum _14) || (abs _9 < abs _14)) && ((((_9 + _14) * _15) <= (_8 + _13)) && ((((_9 + _14) * _15) + (_9 + _14)) > (_8 + _13))) of { True -> _15 : (rec'4 (_16 : ((1::Int) : (rec'5 (_16 + (2::Int))))) _11 _7 _17 _9 _14 (_13 - (_15 * _14)) (_8 - (_15 * _9))); False -> (rec'6 _2 _2 _13 (_8 + ((1::Int) * _13)) (_9 + ((1::Int) * _14)) _14) }) }) }) }) (n - (1::Int))) }

takeDigits = \n -> \ds -> let
        rec ψ n' = let
              rec'5 _fε'2 π'10 π'11 _13 _14 π'12 = let
                    _15 = div _13 _14
                    rec'6 xs'2 π'13 π'14 _16 _17 = 
                          let _18 = div π'14 _16 in
                          case ((signum _17 == signum _16) || (abs _17 < abs _16)) && ((((_17 + _16) * _18) <= (π'13 + π'14)) && ((((_17 + _16) * _18) + (_17 + _16)) > (π'13 + π'14))) of { True -> _18 : (rec'6 xs'2 _17 _16 (π'14 - (_18 * _16)) (π'13 - (_18 * _17))); False -> (rec'5 (let (:) _ arg = xs'2 in arg) (let (:) _ arg = xs'2 in arg) π'14 (π'13 + ((let (:) arg _ = xs'2 in arg) * π'14)) (_17 + ((let (:) arg _ = xs'2 in arg) * _16)) _16) }
                    in case ((signum π'12 == signum _14) || (abs π'12 < abs _14)) && ((((π'12 + _14) * _15) <= (π'11 + _13)) && ((((π'12 + _14) * _15) + (π'12 + _14)) > (π'11 + _13))) of { True -> _15 : (rec'6 π'10 π'12 _14 (_13 - (_15 * _14)) (π'11 - (_15 * π'12))); False -> (rec'5 (let (:) _ arg = _fε'2 in arg) (let (:) _ arg = _fε'2 in arg) _13 (π'11 + ((let (:) arg _ = _fε'2 in arg) * _13)) (π'12 + ((let (:) arg _ = _fε'2 in arg) * _14)) _14) }
              _19 = div (0::Int) (1::Int)
              rec'7 xs'3 π'15 π'16 _20 _21 = let
                    rec'8 _fε'3 π'17 π'18 _22 _23 π'19 = 
                          let _24 = div _22 _23 in
                          case ((signum π'19 == signum _23) || (abs π'19 < abs _23)) && ((((π'19 + _23) * _24) <= (π'18 + _22)) && ((((π'19 + _23) * _24) + (π'19 + _23)) > (π'18 + _22))) of { True -> _24 : (rec'7 π'17 π'19 _23 (_22 - (_24 * _23)) (π'18 - (_24 * π'19))); False -> (rec'8 (let (:) _ arg = _fε'3 in arg) (let (:) _ arg = _fε'3 in arg) _22 (π'18 + ((let (:) arg _ = _fε'3 in arg) * _22)) (π'19 + ((let (:) arg _ = _fε'3 in arg) * _23)) _23) }
                    _25 = div π'16 _20
                    in case ((signum _21 == signum _20) || (abs _21 < abs _20)) && ((((_21 + _20) * _25) <= (π'15 + π'16)) && ((((_21 + _20) * _25) + (_21 + _20)) > (π'15 + π'16))) of { True -> _25 : (rec'7 xs'3 _21 _20 (π'16 - (_25 * _20)) (π'15 - (_25 * _21))); False -> (rec'8 (let (:) _ arg = xs'3 in arg) (let (:) _ arg = xs'3 in arg) π'16 (π'15 + ((let (:) arg _ = xs'3 in arg) * π'16)) (_21 + ((let (:) arg _ = xs'3 in arg) * _20)) _20) }
              in case n' == (0::Int) of { True -> []; False -> (let (:) arg _ = ψ in arg) : (rec (case ((signum (0::Int) == signum (1::Int)) || (abs (0::Int) < abs (1::Int))) && (((((0::Int) + (1::Int)) * _19) <= ((10::Int) + (0::Int))) && (((((0::Int) + (1::Int)) * _19) + ((0::Int) + (1::Int))) > ((10::Int) + (0::Int)))) of { True -> _19 : (rec'7 (let (:) _ arg = ψ in arg) (0::Int) (1::Int) ((0::Int) - (_19 * (1::Int))) ((10::Int) - (_19 * (0::Int)))); False -> (rec'5 (let (:) _ arg = (let (:) _ arg = ψ in arg) in arg) (let (:) _ arg = (let (:) _ arg = ψ in arg) in arg) (0::Int) ((10::Int) + ((let (:) arg _ = (let (:) _ arg = ψ in arg) in arg) * (0::Int))) ((0::Int) + ((let (:) arg _ = (let (:) _ arg = ψ in arg) in arg) * (1::Int))) (1::Int)) }) (n' - (1::Int))) }
        _0 = div (0::Int) (1::Int)
        rec'2 _fε' π'5 π'6 _7 _8 π'7 = let
              _9 = div _7 _8
              rec'4 xs' π'8 π'9 _10 _11 = 
                    let _12 = div π'9 _10 in
                    case ((signum _11 == signum _10) || (abs _11 < abs _10)) && ((((_11 + _10) * _12) <= (π'8 + π'9)) && ((((_11 + _10) * _12) + (_11 + _10)) > (π'8 + π'9))) of { True -> _12 : (rec'4 xs' _11 _10 (π'9 - (_12 * _10)) (π'8 - (_12 * _11))); False -> (rec'2 (let (:) _ arg = xs' in arg) (let (:) _ arg = xs' in arg) π'9 (π'8 + ((let (:) arg _ = xs' in arg) * π'9)) (_11 + ((let (:) arg _ = xs' in arg) * _10)) _10) }
              in case ((signum π'7 == signum _8) || (abs π'7 < abs _8)) && ((((π'7 + _8) * _9) <= (π'6 + _7)) && ((((π'7 + _8) * _9) + (π'7 + _8)) > (π'6 + _7))) of { True -> _9 : (rec'4 π'5 π'7 _8 (_7 - (_9 * _8)) (π'6 - (_9 * π'7))); False -> (rec'2 (let (:) _ arg = _fε' in arg) (let (:) _ arg = _fε' in arg) _7 (π'6 + ((let (:) arg _ = _fε' in arg) * _7)) (π'7 + ((let (:) arg _ = _fε' in arg) * _8)) _8) }
        rec' xs π π' _1 _2 = let
              _3 = div π' _1
              rec'3 _fε π'2 π'3 _4 _5 π'4 = 
                    let _6 = div _4 _5 in
                    case ((signum π'4 == signum _5) || (abs π'4 < abs _5)) && ((((π'4 + _5) * _6) <= (π'3 + _4)) && ((((π'4 + _5) * _6) + (π'4 + _5)) > (π'3 + _4))) of { True -> _6 : (rec' π'2 π'4 _5 (_4 - (_6 * _5)) (π'3 - (_6 * π'4))); False -> (rec'3 (let (:) _ arg = _fε in arg) (let (:) _ arg = _fε in arg) _4 (π'3 + ((let (:) arg _ = _fε in arg) * _4)) (π'4 + ((let (:) arg _ = _fε in arg) * _5)) _5) }
              in case ((signum _2 == signum _1) || (abs _2 < abs _1)) && ((((_2 + _1) * _3) <= (π + π')) && ((((_2 + _1) * _3) + (_2 + _1)) > (π + π'))) of { True -> _3 : (rec' xs _2 _1 (π' - (_3 * _1)) (π - (_3 * _2))); False -> (rec'3 (let (:) _ arg = xs in arg) (let (:) _ arg = xs in arg) π' (π + ((let (:) arg _ = xs in arg) * π')) (_2 + ((let (:) arg _ = xs in arg) * _1)) _1) }
        in case n == (0::Int) of { True -> []; False -> (let (:) arg _ = ds in arg) : (rec (case ((signum (0::Int) == signum (1::Int)) || (abs (0::Int) < abs (1::Int))) && (((((0::Int) + (1::Int)) * _0) <= ((10::Int) + (0::Int))) && (((((0::Int) + (1::Int)) * _0) + ((0::Int) + (1::Int))) > ((10::Int) + (0::Int)))) of { True -> _0 : (rec' (let (:) _ arg = ds in arg) (0::Int) (1::Int) ((0::Int) - (_0 * (1::Int))) ((10::Int) - (_0 * (0::Int)))); False -> (rec'2 (let (:) _ arg = (let (:) _ arg = ds in arg) in arg) (let (:) _ arg = (let (:) _ arg = ds in arg) in arg) (0::Int) ((10::Int) + ((let (:) arg _ = (let (:) _ arg = ds in arg) in arg) * (0::Int))) ((0::Int) + ((let (:) arg _ = (let (:) _ arg = ds in arg) in arg) * (1::Int))) (1::Int)) }) (n - (1::Int))) }

ratTrans = \ds -> \xs -> let
        _0 = div (let (,,,) _ arg _ _ = ds in arg) (let (,,,) _ _ _ arg = ds in arg)
        rec xs'2 π'5 π'6 _7 _8 = let
              _9 = div π'6 _7
              rec'3 _fε' π'7 π'8 _10 _11 π'9 = 
                    let _12 = div _10 _11 in
                    case ((signum π'9 == signum _11) || (abs π'9 < abs _11)) && ((((π'9 + _11) * _12) <= (π'8 + _10)) && ((((π'9 + _11) * _12) + (π'9 + _11)) > (π'8 + _10))) of { True -> _12 : (rec π'7 π'9 _11 (_10 - (_12 * _11)) (π'8 - (_12 * π'9))); False -> (rec'3 (let (:) _ arg = _fε' in arg) (let (:) _ arg = _fε' in arg) _10 (π'8 + ((let (:) arg _ = _fε' in arg) * _10)) (π'9 + ((let (:) arg _ = _fε' in arg) * _11)) _11) }
              in case ((signum _8 == signum _7) || (abs _8 < abs _7)) && ((((_8 + _7) * _9) <= (π'5 + π'6)) && ((((_8 + _7) * _9) + (_8 + _7)) > (π'5 + π'6))) of { True -> _9 : (rec xs'2 _8 _7 (π'6 - (_9 * _7)) (π'5 - (_9 * _8))); False -> (rec'3 (let (:) _ arg = xs'2 in arg) (let (:) _ arg = xs'2 in arg) π'6 (π'5 + ((let (:) arg _ = xs'2 in arg) * π'6)) (_8 + ((let (:) arg _ = xs'2 in arg) * _7)) _7) }
        rec' _fε π π' _1 _2 π'2 = let
              _3 = div _1 _2
              rec'2 xs' π'3 π'4 _4 _5 = 
                    let _6 = div π'4 _4 in
                    case ((signum _5 == signum _4) || (abs _5 < abs _4)) && ((((_5 + _4) * _6) <= (π'3 + π'4)) && ((((_5 + _4) * _6) + (_5 + _4)) > (π'3 + π'4))) of { True -> _6 : (rec'2 xs' _5 _4 (π'4 - (_6 * _4)) (π'3 - (_6 * _5))); False -> (rec' (let (:) _ arg = xs' in arg) (let (:) _ arg = xs' in arg) π'4 (π'3 + ((let (:) arg _ = xs' in arg) * π'4)) (_5 + ((let (:) arg _ = xs' in arg) * _4)) _4) }
              in case ((signum π'2 == signum _2) || (abs π'2 < abs _2)) && ((((π'2 + _2) * _3) <= (π' + _1)) && ((((π'2 + _2) * _3) + (π'2 + _2)) > (π' + _1))) of { True -> _3 : (rec'2 π π'2 _2 (_1 - (_3 * _2)) (π' - (_3 * π'2))); False -> (rec' (let (:) _ arg = _fε in arg) (let (:) _ arg = _fε in arg) _1 (π' + ((let (:) arg _ = _fε in arg) * _1)) (π'2 + ((let (:) arg _ = _fε in arg) * _2)) _2) }
        in case ds of { (,,,) ρ ρ' ρ'2 ρ'3 -> (case ((signum ρ'2 == signum ρ'3) || (abs ρ'2 < abs ρ'3)) && ((((ρ'2 + ρ'3) * _0) <= (ρ + ρ')) && ((((ρ'2 + ρ'3) * _0) + (ρ'2 + ρ'3)) > (ρ + ρ'))) of { True -> _0 : (rec xs ρ'2 ρ'3 (ρ' - (_0 * ρ'3)) (ρ - (_0 * ρ'2))); False -> (rec' (let (:) _ arg = xs in arg) (let (:) _ arg = xs in arg) ρ' (ρ + ((let (:) arg _ = xs in arg) * ρ')) (ρ'2 + ((let (:) arg _ = xs in arg) * ρ'3)) ρ'3) }) }

eContFrac = 
  let rec _0 = (1::Int) : (_0 : ((1::Int) : (rec (_0 + (2::Int))))) in
  (2::Int) : ((1::Int) : ((2::Int) : ((1::Int) : (rec ((2::Int) + (2::Int))))))

hash = Data.Foldable.foldl' (\acc -> \c -> ord c + (acc * (31::Int))) (0::Int)
