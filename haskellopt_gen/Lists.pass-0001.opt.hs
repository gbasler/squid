-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Lists (lol,ls0,mutrec0,ls1,a,main,b,mutrec1) where

import Data.Foldable
import GHC.Base
import GHC.List
import GHC.Num
import GHC.Show
import GHC.Types
import System.IO

_0(# _1, _2 #) = (((GHC.Num.+) _2) _1)

_3 = (((:) (GHC.Num.fromInteger 1)) (((:) (GHC.Num.fromInteger 2)) _3))

a = (((:) 1) b)

b = (((:) 2) a)

lol = (\x_a -> (\y_a -> (_0(# y_a, x_a #))))

ls0 = (((:) 1) (((:) 2) (((:) 3) (((:) 4) []))))

ls1 = ((GHC.Base.map (\ds_d -> (((GHC.Num.+) ds_d) (_0(# 22, 11 #))))) ls0)

main = (System.IO.print (Data.Foldable.sum ls1))

mutrec0 = ((GHC.List.take (GHC.Types.I# 20#)) a)

mutrec1 = ((GHC.List.take (GHC.Types.I# 30#)) _3)
