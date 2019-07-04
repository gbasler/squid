-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 304; Boxes: 77; Branches: 36
-- Apps: 71; Lams: 26; Unreduced Redexes: 1

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module ListFusion (bat,sumnats,f1,f0,f2,foo) where

import Data.Foldable
import GHC.Base
import GHC.Num
import GHC.Real

bat = (\sf -> (\arg -> (_0(# arg, sf #))))

_0(# arg', sf' #) = (sf' (GHC.Base.build (\c -> (\n -> (_1(# c, (_2(# arg' #)), n, [] #))))))

f0 = (\xs -> (GHC.Base.build (\c' -> (\n' -> (((GHC.Base.foldr (((GHC.Base..) c') ((GHC.Num.+) 1))) n') xs)))))

f1 = (\ls -> (GHC.Base.build (\c'2 -> (\n'2 -> (((GHC.Base.foldr (((GHC.Base..) (((GHC.Base..) c'2) ((GHC.Num.+) (GHC.Num.fromInteger 1)))) ((GHC.Num.*) (GHC.Num.fromInteger 2)))) n'2) ls)))))

f2 = (\ls' -> (GHC.Base.build (\c'3 -> (\n'3 -> (((GHC.Base.foldr (((GHC.Base..) (((GHC.Base..) (((GHC.Base..) c'3) ((GHC.Num.+) (GHC.Num.fromInteger 1)))) ((GHC.Num.*) (GHC.Num.fromInteger 2)))) ((GHC.Real.^) (GHC.Num.fromInteger 3)))) n'3) ls')))))

foo = (\sf'2 -> (_3(# sf'2 #)))

_3(# sf'3 #) = (\arg'2 -> (((GHC.Num.+) (_0(# arg'2, sf'3 #))) (sf'3 (GHC.Base.build (\c'4 -> (\n'4 -> (_1(# (((GHC.Base..) c'4) (\x -> (((GHC.Num.*) x) (GHC.Num.fromInteger 2)))), (_2(# (((GHC.Num.+) arg'2) (GHC.Num.fromInteger 1)) #)), n'4, [] #))))))))

sumnats = (_3(# Data.Foldable.sum #))

_2(# arg'3 #) = (\c'5 -> (((GHC.Num.+) c'5) arg'3))

_1(# c'6, f, n'5, xs' #) = (((GHC.Base.foldr (((GHC.Base..) c'6) f)) n'5) xs')
