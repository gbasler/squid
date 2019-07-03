-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 446; Boxes: 96; Branches: 48
-- Apps: 126; Lams: 28; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Main (sumnatsLocalTupled,bat,sumnats,sf,main,loremipsum,sumnatsLocal,foo) where

import Criterion.Main
import Criterion.Measurement.Types
import Data.Foldable
import GHC.Base
import GHC.CString
import GHC.Enum
import GHC.Num
import GHC.TopHandler
import GHC.Types

bat = (\ds -> (case ds of {(,) arg0 arg1 -> (_0(# arg0, arg1 #))}))

_0(# ds', ds'2 #) = (let sh = (r(# ds', ds'2 #)) in (((GHC.Num.+) (((GHC.Num.*) sh) sh)) (GHC.Num.fromInteger 1)))

foo = (\sf' -> (\arg -> (_1(# arg, sf' #))))

_1(# arg', sf'2 #) = (((GHC.Num.+) (_2(# (sf'2 (GHC.Base.build (\c -> (\n -> (_3(# c, (_4(# arg' #)), n, loremipsum' #)))))) #))) (_2(# (sf'2 (GHC.Base.build (\c' -> (\n' -> (_3(# (((GHC.Base..) c') (\x -> (((GHC.Num.*) x) (GHC.Types.I# 2#)))), (_4(# (((GHC.Num.+) arg') (GHC.Types.I# 1#)) #)), n', loremipsum' #)))))) #)))

loremipsum = loremipsum'

loremipsum' = ((GHC.Enum.enumFromTo (GHC.Types.I# 0#)) (GHC.Types.I# 6660#))

main = (let sh' = (GHC.Types.I# 42#) in (GHC.TopHandler.runMainIO (Criterion.Main.defaultMain (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "localTup"#))) ((Criterion.Measurement.Types.whnf sumnatsLocalTupled) sh'))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "toplvl"#))) ((Criterion.Measurement.Types.whnf sumnats) sh'))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "local"#))) ((Criterion.Measurement.Types.whnf sumnatsLocal) sh'))) []))))))

sumnatsLocalTupled = (\x' -> (((GHC.Num.+) (_0(# Data.Foldable.sum, x' #))) (_0(# (((GHC.Base..) Data.Foldable.sum) (\xs -> (GHC.Base.build (\c'2 -> (\n'2 -> (((GHC.Base.foldr (((GHC.Base..) c'2) (\x'2 -> (((GHC.Num.*) x'2) (GHC.Types.I# 2#))))) n'2) xs)))))), (((GHC.Num.+) x') (GHC.Types.I# 1#)) #))))

sumnats = (\a -> (_1(# a, Data.Foldable.sum #)))

sumnatsLocal = (\arg'2 -> (((GHC.Num.+) (_5(# (Data.Foldable.sum (GHC.Base.build (\c'3 -> (\n'3 -> (_6(# c'3, (_7(# arg'2 #)), n'3, loremipsum' #)))))) #))) (_5(# (Data.Foldable.sum (GHC.Base.build (\c'4 -> (\n'4 -> (_6(# (((GHC.Base..) c'4) (\x'3 -> (((GHC.Num.*) x'3) (GHC.Types.I# 2#)))), (_7(# (((GHC.Num.+) arg'2) (GHC.Types.I# 1#)) #)), n'4, loremipsum' #)))))) #))))

sf = Data.Foldable.sum

_5(# sf'3 #) = (((GHC.Num.+) (((GHC.Num.*) sf'3) sf'3)) (GHC.Num.fromInteger 1))

_6(# c'5, f, n'5, xs' #) = (((GHC.Base.foldr (((GHC.Base..) c'5) f)) n'5) xs')

_7(# arg'3 #) = (\c'6 -> (((GHC.Num.+) c'6) arg'3))

_3(# c'7, f', n'6, xs'2 #) = (((GHC.Base.foldr (((GHC.Base..) c'7) f')) n'6) xs'2)

_4(# arg'4 #) = (\c'8 -> (((GHC.Num.+) c'8) arg'4))

_2(# sf'4 #) = (((GHC.Num.+) (((GHC.Num.*) sf'4) sf'4)) (GHC.Num.fromInteger 1))

r(# ds'3, ds'4 #) = (ds'3 (GHC.Base.build (\c'9 -> (\n'7 -> (((GHC.Base.foldr (((GHC.Base..) c'9) (\c'10 -> (((GHC.Num.+) c'10) ds'4)))) n'7) loremipsum')))))
