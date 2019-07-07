-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 240; Boxes: 33; Branches: 16
-- Apps: 92; Lams: 8; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Main (values,processLocal,main) where

import Criterion.Main
import Criterion.Measurement.Types
import Data.Foldable
import GHC.Base
import GHC.CString
import GHC.Enum
import GHC.Num
import GHC.Real
import GHC.TopHandler
import GHC.Types

main = (GHC.TopHandler.runMainIO (Criterion.Main.defaultMain (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "local"#))) ((Criterion.Measurement.Types.whnf processLocal) (GHC.Types.I# 42#)))) [])))

processLocal = (\arg -> (((GHC.Num.+) (_0(# (Data.Foldable.sum (GHC.Base.build (\c -> (\n -> (_1(# c, (_2(# arg #)), n, values' #)))))) #))) (_0(# (Data.Foldable.sum (GHC.Base.build (\c' -> (\n' -> (_1(# (((GHC.Base..) c') (\x -> (((GHC.Num.*) x) (GHC.Types.I# 2#)))), (_2(# (((GHC.Num.+) arg) (GHC.Types.I# 1#)) #)), n', values' #)))))) #))))

_0(# sf #) = (((GHC.Num.+) (((GHC.Num.*) (((GHC.Num.*) (((GHC.Num.*) (((GHC.Num.*) (((GHC.Num.*) (((GHC.Num.*) (((GHC.Num.*) (((GHC.Num.*) (((GHC.Num.*) (((GHC.Num.*) sf) sf)) sf)) sf)) sf)) sf)) sf)) sf)) sf)) sf)) sf)) (GHC.Num.fromInteger 1))

_1(# c'2, f, n'2, xs #) = (((GHC.Base.foldr (((GHC.Base..) c'2) f)) n'2) xs)

_2(# arg' #) = (\x' -> let sh = (GHC.Types.I# 1#) in (((GHC.Num.+) x') (((GHC.Num.+) (((GHC.Real.^) (((GHC.Num.+) (((GHC.Real.^) (((GHC.Num.+) (((GHC.Real.^) (((GHC.Num.+) (((GHC.Real.^) (((GHC.Num.+) (((GHC.Real.^) (((GHC.Num.+) (((GHC.Real.^) arg') 2)) sh)) 2)) sh)) 2)) sh)) 2)) sh)) 2)) sh)) 2)) sh)))

values = values'

values' = ((GHC.Enum.enumFromTo (GHC.Types.I# 0#)) (GHC.Types.I# 6660#))
