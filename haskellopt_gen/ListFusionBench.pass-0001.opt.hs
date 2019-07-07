-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 240; Boxes: 33; Branches: 15
-- Apps: 92; Lams: 10; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Main (bat,sf,main,processLocal,values) where

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

bat = (\sf' -> (\arg -> (_0(# (_1(# arg, sf' #)) #))))

_0(# sf'2 #) = (((GHC.Num.+) (((GHC.Num.*) (((GHC.Num.*) (((GHC.Num.*) (((GHC.Num.*) (((GHC.Num.*) (((GHC.Num.*) (((GHC.Num.*) (((GHC.Num.*) (((GHC.Num.*) (((GHC.Num.*) sf'2) sf'2)) sf'2)) sf'2)) sf'2)) sf'2)) sf'2)) sf'2)) sf'2)) sf'2)) sf'2)) (GHC.Num.fromInteger 1))

_1(# arg', sf'3 #) = (sf'3 (GHC.Base.build (\c -> (\n -> (_2(# c, (_3(# arg' #)), n, values' #))))))

main = (GHC.TopHandler.runMainIO (Criterion.Main.defaultMain (((:) (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "local"#))) ((Criterion.Measurement.Types.whnf processLocal) (GHC.Types.I# 42#)))) [])))

processLocal = (\arg'2 -> (((GHC.Num.+) (_0(# (_1(# arg'2, Data.Foldable.sum #)) #))) (_0(# (Data.Foldable.sum (GHC.Base.build (\c' -> (\n' -> (_2(# (((GHC.Base..) c') (\x -> (((GHC.Num.*) x) (GHC.Types.I# 2#)))), (_3(# (((GHC.Num.+) arg'2) (GHC.Types.I# 1#)) #)), n', values' #)))))) #))))

sf = Data.Foldable.sum

_2(# c'2, f, n'2, xs #) = (((GHC.Base.foldr (((GHC.Base..) c'2) f)) n'2) xs)

_3(# arg'3 #) = (\x' -> let sh = (GHC.Types.I# 1#) in (((GHC.Num.+) x') (((GHC.Num.+) (((GHC.Real.^) (((GHC.Num.+) (((GHC.Real.^) (((GHC.Num.+) (((GHC.Real.^) (((GHC.Num.+) (((GHC.Real.^) (((GHC.Num.+) (((GHC.Real.^) (((GHC.Num.+) (((GHC.Real.^) arg'3) 2)) sh)) 2)) sh)) 2)) sh)) 2)) sh)) 2)) sh)) 2)) sh)))

values = values'

values' = ((GHC.Enum.enumFromTo (GHC.Types.I# 0#)) (GHC.Types.I# 6660#))
