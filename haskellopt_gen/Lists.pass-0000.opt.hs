{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Lists (lol,ls0,mutrec0,ls1,main) where

import Data.Foldable
import GHC.Base
import GHC.List
import GHC.Num
import GHC.Show
import GHC.Types
import System.IO

a_a_11  = (((:) 1) (((:) 2) a_a_11))

lol  = (\x_a_7 -> (\y_a_8 -> (((GHC.Num.+) x_a_7) y_a_8)))

ls0  = (((:) 1) (((:) 2) (((:) 3) (((:) 4) []))))

ls1  = ((GHC.Base.map (\ds_d_10 -> (((GHC.Num.+) ds_d_10) ((lol 11) 22)))) ls0)

main  = (((GHC.Base.$) System.IO.print) (Data.Foldable.sum ls1))

mutrec0  = ((GHC.List.take (GHC.Types.I# 2#)) a_a_11)
