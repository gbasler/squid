-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 128; Boxes: 20; Branches: 0
-- Apps: 41; Lams: 17; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module HigherOrderHard (gTest1,g''Test1,ds,hTest0,g,g'',hTest1,g'Test0,h,gTest0,g') where

import GHC.Base
import GHC.Num

ds = 1

g = (\f -> (\x -> (GHC.Base.id (f (GHC.Base.id x)))))

g' = (\f' -> (\x' -> (GHC.Base.id (f' (GHC.Base.id x')))))

g'' = (\f'2 -> (\x'2 -> (GHC.Base.id (f'2 (GHC.Base.id x'2)))))

g''Test1 = (\y -> (\x'3 -> (GHC.Base.id (((GHC.Num.+) (GHC.Base.id x'3)) y))))

g'Test0 = (\x'4 -> (GHC.Base.id (((GHC.Num.+) (GHC.Base.id x'4)) 1)))

gTest0 = (\x'5 -> (GHC.Base.id (((GHC.Num.+) (GHC.Base.id x'5)) 1)))

gTest1 = (\y' -> (\x'6 -> (GHC.Base.id (((GHC.Num.+) (GHC.Base.id x'6)) y'))))

h = (\f'3 -> (\x'7 -> (GHC.Base.id (f'3 (GHC.Base.id (f'3 x'7))))))

hTest0 = (\x'8 -> (GHC.Base.id (((GHC.Num.+) (GHC.Base.id (((GHC.Num.+) x'8) 1))) 1)))

hTest1 = (\y'2 -> (\eta -> (GHC.Base.id (((GHC.Num.+) (GHC.Base.id (((GHC.Num.+) eta) y'2))) y'2))))
