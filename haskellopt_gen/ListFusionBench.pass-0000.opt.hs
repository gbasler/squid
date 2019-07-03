-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 441; Boxes: 93; Branches: 47
-- Apps: 125; Lams: 28; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Main (bat,processLocalTupled,process,main,processLocal,values,foo) where

import Criterion.Main
import Criterion.Measurement.Types
import Data.Foldable
import GHC.Base
import GHC.CString
import GHC.Enum
import GHC.Num
import GHC.TopHandler
import GHC.Types

bat = (\sf -> (\arg -> (_0(# (_1(# arg, sf #)) #))))

_0(# sf' #) = (((GHC.Num.+) (((GHC.Num.*) sf') sf')) (GHC.Num.fromInteger 1))

_1(# arg', sf'2 #) = (sf'2 (GHC.Base.build (\c -> (\n -> (_2(# c, (_3(# arg' #)), n, values' #))))))

foo = (\sf'3 -> (\arg'2 -> (_4(# arg'2, sf'3 #))))

_4(# arg'3, sf'4 #) = (((GHC.Num.+) (_0(# (_1(# arg'3, sf'4 #)) #))) (_0(# (sf'4 (GHC.Base.build (\c' -> (\n' -> (_2(# (((GHC.Base..) c') (\x -> (((GHC.Num.*) x) (GHC.Types.I# 2#)))), (_3(# (((GHC.Num.+) arg'3) (GHC.Types.I# 1#)) #)), n', values' #)))))) #)))

main = (let sh = (GHC.Types.I# 42#) in (GHC.TopHandler.runMainIO (Criterion.Main.defaultMain (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "localTup"#))) ((Criterion.Measurement.Types.whnf processLocalTupled) sh))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "toplvl"#))) ((Criterion.Measurement.Types.whnf process) sh))) (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "local"#))) ((Criterion.Measurement.Types.whnf processLocal) sh))) []))))))

processLocalTupled = (\x' -> (((GHC.Num.+) (_5(# Data.Foldable.sum, x' #))) (_5(# (((GHC.Base..) Data.Foldable.sum) (\xs -> (GHC.Base.build (\c'2 -> (\n'2 -> (((GHC.Base.foldr (((GHC.Base..) c'2) (\x'2 -> (((GHC.Num.*) x'2) (GHC.Types.I# 2#))))) n'2) xs)))))), (((GHC.Num.+) x') (GHC.Types.I# 1#)) #))))

process = (\a -> (_4(# a, Data.Foldable.sum #)))

processLocal = (\arg'4 -> (((GHC.Num.+) (_6(# (Data.Foldable.sum (GHC.Base.build (\c'3 -> (\n'3 -> (_7(# c'3, (_8(# arg'4 #)), n'3, values' #)))))) #))) (_6(# (Data.Foldable.sum (GHC.Base.build (\c'4 -> (\n'4 -> (_7(# (((GHC.Base..) c'4) (\x'3 -> (((GHC.Num.*) x'3) (GHC.Types.I# 2#)))), (_8(# (((GHC.Num.+) arg'4) (GHC.Types.I# 1#)) #)), n'4, values' #)))))) #))))

_6(# sf'5 #) = (((GHC.Num.+) (((GHC.Num.*) sf'5) sf'5)) (GHC.Num.fromInteger 1))

_7(# c'5, f, n'5, xs' #) = (((GHC.Base.foldr (((GHC.Base..) c'5) f)) n'5) xs')

_8(# arg'5 #) = (\ds -> (((GHC.Num.+) ds) arg'5))

_5(# ds', ds'2 #) = (let sh' = (r(# ds', ds'2 #)) in (((GHC.Num.+) (((GHC.Num.*) sh') sh')) (GHC.Num.fromInteger 1)))

values = values'

values' = ((GHC.Enum.enumFromTo (GHC.Types.I# 0#)) (GHC.Types.I# 6660#))

_2(# c'6, f', n'6, xs'2 #) = (((GHC.Base.foldr (((GHC.Base..) c'6) f')) n'6) xs'2)

_3(# arg'6 #) = (\ds'3 -> (((GHC.Num.+) ds'3) arg'6))

r(# ds'4, ds'5 #) = (ds'4 (GHC.Base.build (\c'7 -> (\n'7 -> (((GHC.Base.foldr (((GHC.Base..) c'7) (\ds'6 -> (((GHC.Num.+) ds'6) ds'5)))) n'7) values')))))
