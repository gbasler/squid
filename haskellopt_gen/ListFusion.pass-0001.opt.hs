-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 346; Boxes: 82; Branches: 32
-- Apps: 88; Lams: 31; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module ListFusion (bat,sumnats,f1,f0,f2,foo) where

import Data.Foldable
import GHC.Base
import GHC.Num
import GHC.Real

bat = (\sf -> (\arg -> (sf (GHC.Base.build (\c -> (\n -> (((GHC.Base.foldr (((GHC.Base..) c) (\c' -> (((GHC.Num.+) c') arg)))) n) [])))))))

f0 = (\xs -> (GHC.Base.build (\c'2 -> (\n' -> (((GHC.Base.foldr (((GHC.Base..) c'2) ((GHC.Num.+) 1))) n') xs)))))

f1 = (\ls -> (GHC.Base.build (\c'3 -> (\n'2 -> (((GHC.Base.foldr (((GHC.Base..) (((GHC.Base..) c'3) ((GHC.Num.+) (GHC.Num.fromInteger 1)))) ((GHC.Num.*) (GHC.Num.fromInteger 2)))) n'2) ls)))))

f2 = (\ls' -> (GHC.Base.build (\c'4 -> (\n'3 -> (((GHC.Base.foldr (((GHC.Base..) (((GHC.Base..) (((GHC.Base..) c'4) ((GHC.Num.+) (GHC.Num.fromInteger 1)))) ((GHC.Num.*) (GHC.Num.fromInteger 2)))) ((GHC.Real.^) (GHC.Num.fromInteger 3)))) n'3) ls')))))

foo = (\sf' -> (_0(# sf' #)))

_0(# sf'2 #) = (\arg' -> (((GHC.Num.+) (sf'2 (GHC.Base.build (\c'5 -> (\n'4 -> (((GHC.Base.foldr (((GHC.Base..) c'5) (\c'6 -> (((GHC.Num.+) c'6) arg')))) n'4) [])))))) (sf'2 (GHC.Base.build (\c'7 -> (\n'5 -> (((GHC.Base.foldr (((GHC.Base..) (((GHC.Base..) c'7) (\x -> (((GHC.Num.*) x) (GHC.Num.fromInteger 2))))) (\c'8 -> (((GHC.Num.+) c'8) (((GHC.Num.+) arg') (GHC.Num.fromInteger 1)))))) n'5) [])))))))

sumnats = (_0(# Data.Foldable.sum #))
