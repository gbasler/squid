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

_33(# _2d, _2e #) = (((GHC.Num.+) _2e) _2d)

_92 = (((:) (GHC.Num.fromInteger 1)) (((:) (GHC.Num.fromInteger 2)) _92))

a = (((:) 1) b)

b = (((:) 2) a)

lol = (\x_a_23 -> (\y_a_24 -> _33))

ls0 = (((:) 1) (((:) 2) (((:) 3) (((:) 4) []))))

ls1 = ((GHC.Base.map (\ds_d_26 -> (((GHC.Num.+) ds_d_26) (_33(# 22, 11 #))))) ls0)

main = (System.IO.print (Data.Foldable.sum ls1))

mutrec0 = ((GHC.List.take (GHC.Types.I# 20#)) a)

mutrec1 = ((GHC.List.take (GHC.Types.I# 30#)) _92)
