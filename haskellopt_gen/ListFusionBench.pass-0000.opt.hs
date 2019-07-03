-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 449; Boxes: 96; Branches: 49
-- Apps: 126; Lams: 29; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Main (sumnatsLocalTupled,bat,sumnats,main,loremipsum,sumnatsLocal,foo) where

import Criterion.Main
import Criterion.Measurement.Types
import Data.Foldable
import GHC.Base
import GHC.CString
import GHC.Enum
import GHC.Num
import GHC.TopHandler
import GHC.Types

bat = (\sf -> (\arg -> (_0(# (sf (_1(# (_2(# arg #)), loremipsum' #))) #))))

_0(# sf' #) = (((GHC.Num.+) (((GHC.Num.*) sf') sf')) (GHC.Num.fromInteger 1))

_1(# f, xs #) = (GHC.Base.build (\c -> (\n -> (_3(# c, f, n, xs #)))))

_2(# arg' #) = (\c' -> (((GHC.Num.+) c') arg'))

foo = (\sf'2 -> (\arg'2 -> (_4(# arg'2, sf'2 #))))

_4(# arg'3, sf'3 #) = (((GHC.Num.+) (_0(# (sf'3 (_1(# (_2(# arg'3 #)), loremipsum' #))) #))) (_0(# (sf'3 (GHC.Base.build (\c'2 -> (\n' -> (_3(# (((GHC.Base..) c'2) (\x -> (((GHC.Num.*) x) (GHC.Types.I# 2#)))), (_2(# (((GHC.Num.+) arg'3) (GHC.Types.I# 1#)) #)), n', loremipsum' #)))))) #)))

loremipsum = loremipsum'

loremipsum' = ((GHC.Enum.enumFromTo (GHC.Types.I# 0#)) (GHC.Types.I# 6660#))

main = (let sh = (GHC.Types.I# 42#) in (GHC.TopHandler.runMainIO (Criterion.Main.defaultMain (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "localTup"#))) ((Criterion.Measurement.Types.whnf sumnatsLocalTupled) sh))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "toplvl"#))) ((Criterion.Measurement.Types.whnf sumnats) sh))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "local"#))) ((Criterion.Measurement.Types.whnf sumnatsLocal) sh))) []))))))

sumnatsLocalTupled = (\x' -> (((GHC.Num.+) (_5(# Data.Foldable.sum, x' #))) (_5(# (((GHC.Base..) Data.Foldable.sum) (\xs' -> (GHC.Base.build (\c'3 -> (\n'2 -> (((GHC.Base.foldr (((GHC.Base..) c'3) (\x'2 -> (((GHC.Num.*) x'2) (GHC.Types.I# 2#))))) n'2) xs')))))), (((GHC.Num.+) x') (GHC.Types.I# 1#)) #))))

sumnats = (\a -> (_4(# a, Data.Foldable.sum #)))

sumnatsLocal = (\arg'4 -> (((GHC.Num.+) (_6(# (Data.Foldable.sum (GHC.Base.build (\c'4 -> (\n'3 -> (_7(# c'4, (_8(# arg'4 #)), n'3, loremipsum' #)))))) #))) (_6(# (Data.Foldable.sum (GHC.Base.build (\c'5 -> (\n'4 -> (_7(# (((GHC.Base..) c'5) (\x'3 -> (((GHC.Num.*) x'3) (GHC.Types.I# 2#)))), (_8(# (((GHC.Num.+) arg'4) (GHC.Types.I# 1#)) #)), n'4, loremipsum' #)))))) #))))

_6(# sf'4 #) = (((GHC.Num.+) (((GHC.Num.*) sf'4) sf'4)) (GHC.Num.fromInteger 1))

_7(# c'6, f', n'5, xs'2 #) = (((GHC.Base.foldr (((GHC.Base..) c'6) f')) n'5) xs'2)

_8(# arg'5 #) = (\c'7 -> (((GHC.Num.+) c'7) arg'5))

_5(# ds, ds' #) = (let sh' = (r(# ds, ds' #)) in (((GHC.Num.+) (((GHC.Num.*) sh') sh')) (GHC.Num.fromInteger 1)))

r(# ds'2, ds'3 #) = (ds'2 (GHC.Base.build (\c'8 -> (\n'6 -> (((GHC.Base.foldr (((GHC.Base..) c'8) (\c'9 -> (((GHC.Num.+) c'9) ds'3)))) n'6) loremipsum')))))

_3(# c'10, f'2, n'7, xs'3 #) = (((GHC.Base.foldr (((GHC.Base..) c'10) f'2)) n'7) xs'3)
