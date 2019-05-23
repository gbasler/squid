-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Lists (lol,ls0,mutrec0,ls1,main,rec0,mutrec1) where

import Data.Foldable
import GHC.Base
import GHC.List
import GHC.Num
import GHC.Show
import GHC.Types
import System.IO

_0(# x, y #) = (((GHC.Num.+) x) y)

_1 = ((:) 1)

_2 = (((:) (GHC.Num.fromInteger 2)) _3)

_3 = (((:) (GHC.Num.fromInteger 1)) _2)

a = (((:) 1) (((:) 2) a))

ds = (((,) (\x'2 -> _3)) (\y'2 -> _2))

lol = (\x' -> (\y' -> (_0(# x', y' #))))

ls0 = (((:) 1) (((:) 2) (((:) 3) (((:) 4) []))))

ls1 = ((GHC.Base.map (\ds' -> (((GHC.Num.+) ds') (_0(# 11, 22 #))))) ls0)

main = (((GHC.Base.$) System.IO.print) (Data.Foldable.sum ls1))

mutrec0 = ((GHC.List.take (GHC.Types.I# 20#)) a)

mutrec1 = ((GHC.List.take (GHC.Types.I# 30#)) ((case ds of {(,) arg0 arg1 -> arg0}) 0))

rec0 = (_1 rec0')

rec0' = (_1 rec0')
