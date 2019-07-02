-- ghc -O3 bench-doc/ListFusionBench.pass-0000.opt.hs  && bench-doc/ListFusionBench.pass-0000.opt

-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 107; Boxes: 15; Branches: 5
-- Apps: 40; Lams: 3; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

-- module Main (loremipsum,sumnats,main) where
module Main (loremipsum,main) where

import Control.DeepSeq
import Criterion.Main
import Criterion.Measurement.Types
import Data.Foldable
import GHC.Base
import GHC.CString
import GHC.Enum
import GHC.List
import GHC.Num
import GHC.TopHandler
import GHC.Types

loremipsum = ((Data.Foldable.concatMap (GHC.List.replicate (GHC.Types.I# 10#))) ((GHC.Enum.enumFromTo (GHC.Types.I# 0#)) (GHC.Types.I# 666#)))

main = (GHC.TopHandler.runMainIO (Criterion.Main.defaultMain 
    [ (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "sumnatsTup"#))) ((Criterion.Measurement.Types.nf sumnatsTup) (GHC.Types.I# 42#)))
    , (((GHC.Base.$) (Criterion.Measurement.Types.bench (GHC.CString.unpackCString# "sumnats"#))) ((Criterion.Measurement.Types.nf sumnats) (GHC.Types.I# 42#)))
    ] ))

-- with tuples
sumnatsTup = (\arg -> (((,) (_0( arg, Data.Foldable.sum ))) (_0( (((GHC.Num.+) arg) (GHC.Types.I# 1#)), (((GHC.Base..) Data.Foldable.sum) (GHC.Base.map (\x -> (((GHC.Num.*) x) (GHC.Types.I# 2#))))) )))) where
    _0( arg', sf ) = (((GHC.Num.+) (let r = (res arg' sf) in ((GHC.Num.*) r) r)) (GHC.Types.I# 1#))
    res arg'2 sf'  = (sf' ((GHC.Base.map (\c -> (((GHC.Num.+) c) arg'2))) loremipsum))
    
-- without tuples: much faster!
sumnats = (\arg -> (((,) (_0 arg Data.Foldable.sum )) (_0 (((GHC.Num.+) arg) (GHC.Types.I# 1#)) (((GHC.Base..) Data.Foldable.sum) (GHC.Base.map (\x -> (((GHC.Num.*) x) (GHC.Types.I# 2#))))) ))) where
    _0 arg' sf  = (((GHC.Num.+) (let r = (res arg' sf ) in ((GHC.Num.*) r) r)) (GHC.Types.I# 1#))
    res arg'2 sf'  = (sf' ((GHC.Base.map (\c -> (((GHC.Num.+) c) arg'2))) loremipsum))
    




