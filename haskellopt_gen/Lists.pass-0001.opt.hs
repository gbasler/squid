-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Lists (lol,ls0,mutrec0,ls1,a,main,b,rec0,mutrec1) where

import Data.Foldable
import GHC.Base
import GHC.List
import GHC.Num
import GHC.Show
import GHC.Types
import System.IO

a = (((:) 1) b)

b = (((:) 2) a)

lol = (\x -> (\y -> (_0(# x, y #))))

_0(# x', y' #) = (((GHC.Num.+) x') y')

ls0 = (((:) 1) (((:) 2) (((:) 3) (((:) 4) []))))

ls1 = ((GHC.Base.map (\ds -> (((GHC.Num.+) ds) (_0(# 11, 22 #))))) ls0)

main = (((GHC.Base.$) System.IO.print) (Data.Foldable.sum ls1))

mutrec0 = ((GHC.List.take (GHC.Types.I# 20#)) a)

mutrec1 = ((GHC.List.take (GHC.Types.I# 30#)) _1)

_1 = (((:) (GHC.Num.fromInteger 1)) (((:) (GHC.Num.fromInteger 2)) _1))

rec0 = (((:) 1) rec0)
