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

a_a_12  = (((:) 1) (((:) 2) a_a_12))

a_a_17  = (\x_a_20 -> (((:) (GHC.Num.fromInteger 1)) (b_a_18 (((GHC.Num.+) x_a_20) (GHC.Num.fromInteger 1)))))

b_a_18  = (\y_a_19 -> (((:) (GHC.Num.fromInteger 2)) (a_a_17 (((GHC.Num.*) y_a_19) (GHC.Num.fromInteger 2)))))

ds_d_14  = (((,) a_a_17) b_a_18)

lol  = (\x_a_8 -> (\y_a_9 -> (((GHC.Num.+) x_a_8) y_a_9)))

ls0  = (((:) 1) (((:) 2) (((:) 3) (((:) 4) []))))

ls1  = ((GHC.Base.map (\ds_d_11 -> (((GHC.Num.+) ds_d_11) ((lol 11) 22)))) ls0)

main  = (((GHC.Base.$) System.IO.print) (Data.Foldable.sum ls1))

mutrec0  = ((GHC.List.take (GHC.Types.I# 20#)) a_a_12)

mutrec1  = ((GHC.List.take (GHC.Types.I# 30#)) ((case ds_d_14 of {(,) arg0 arg1 -> arg0}) 0))
