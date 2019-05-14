-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Lists (lol,ls0,mutrec0,ls1,main,mutrec1) where

import Data.Foldable
import GHC.Base
import GHC.List
import GHC.Num
import GHC.Show
import GHC.Types
import System.IO

_24(# _1e, _1f #) = (((GHC.Num.+) _1f) _1e)

_73 = (((:) (GHC.Num.fromInteger 2)) _89)

_89 = (((:) (GHC.Num.fromInteger 1)) _73)

a_a_12 = (((:) 1) (((:) 2) a_a_12))

ds_d_14 = (((,) (\x_a_20 -> _89)) (\y_a_19 -> _73))

lol = (\x_a_8 -> (\y_a_9 -> _24))

ls0 = (((:) 1) (((:) 2) (((:) 3) (((:) 4) []))))

ls1 = ((GHC.Base.map (\ds_d_11 -> (((GHC.Num.+) ds_d_11) (_24(# 22, 11 #))))) ls0)

main = (((GHC.Base.$) System.IO.print) (Data.Foldable.sum ls1))

mutrec0 = ((GHC.List.take (GHC.Types.I# 20#)) a_a_12)

mutrec1 = ((GHC.List.take (GHC.Types.I# 30#)) ((case ds_d_14 of {(,) arg0 arg1 -> arg0}) 0))
