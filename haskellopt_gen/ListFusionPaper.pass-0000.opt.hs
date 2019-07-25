-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 127; Boxes: 34; Branches: 16
-- Apps: 29; Lams: 11; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module ListFusion (values,aux,analyze,process) where

import Data.Foldable
import GHC.Base
import GHC.Enum
import GHC.Num

analyze = (\sf' -> (_0(# sf' #)))

_0(# sf'2 #) = (\offs' -> (((GHC.Num.+) (_1(# (_2(# offs', sf'2 #)) #))) (_1(# (sf'2 (GHC.Base.build (\c' -> (\n' -> (_3(# (((GHC.Base..) c') (\ds' -> (((GHC.Num.*) ds') 2))), (_4(# (((GHC.Num.+) offs') 1) #)), n', values' #)))))) #))))

aux = (\sf -> (\offs -> (_1(# (_2(# offs, sf #)) #))))

_1(# sf'3 #) = (((GHC.Num.+) (((GHC.Num.*) sf'3) sf'3)) (GHC.Num.fromInteger 1))

_2(# offs'2, sf'4 #) = (sf'4 (GHC.Base.build (\c -> (\n -> (_3(# c, (_4(# offs'2 #)), n, values' #))))))

process = (_0(# Data.Foldable.sum #))

values = values'

values' = ((GHC.Enum.enumFromTo 0) 6660)

_3(# c'2, f, n'2, xs #) = (((GHC.Base.foldr (((GHC.Base..) c'2) f)) n'2) xs)

_4(# offs'3 #) = (\ds -> (((GHC.Num.+) ds) offs'3))
