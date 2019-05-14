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

a  = (((:) 1) b)

a_a_27  = (\x_a_28 -> (((:) (GHC.Num.fromInteger 1)) (((:) (GHC.Num.fromInteger 2)) (a_a_27 (((GHC.Num.*) (((GHC.Num.+) x_a_28) (GHC.Num.fromInteger 1))) (GHC.Num.fromInteger 2))))))

b  = (((:) 2) a)

lol  = (\x_a_23 -> (\y_a_24 -> (((GHC.Num.+) x_a_23) y_a_24)))

ls0  = (((:) 1) (((:) 2) (((:) 3) (((:) 4) []))))

ls1  = ((GHC.Base.map (\ds_d_26 -> (((GHC.Num.+) ds_d_26) ((lol 11) 22)))) ls0)

main  = (System.IO.print (Data.Foldable.sum ls1))

mutrec0  = ((GHC.List.take (GHC.Types.I# 20#)) a)

mutrec1  = ((GHC.List.take (GHC.Types.I# 30#)) (a_a_27 0))
