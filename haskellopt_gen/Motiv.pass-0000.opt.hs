-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 120; Boxes: 32; Branches: 25
-- Apps: 9; Lams: 6; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Motiv (isJust,pgrm,e1,f,e0,e3,e2) where

import GHC.Maybe
import GHC.Num
import GHC.Types

e0 = (\a -> a)

e1 = (\ds -> (case ds of {(,) arg0 arg1 -> (((GHC.Num.*) arg0) arg1)}))

e2 = (\z -> (_0(# z #)))

_0(# z' #) = (((GHC.Num.+) z') 1)

e3 = (\c -> (_1(# c #)))

_1(# c' #) = (case c' of {False -> 0; True -> 1})

f = (\x -> (let x' = (_1(# (_2(# x #)) #)) in (case x of {Nothing -> (_0(# x' #)); Just arg0' -> (((GHC.Num.*) arg0') x')})))

_2(# ds' #) = (case ds' of {Nothing -> GHC.Types.False; Just arg0'2 -> GHC.Types.True})

isJust = (\ds'2 -> (_2(# ds'2 #)))

pgrm = (((GHC.Num.+) (((GHC.Num.*) 2) 1)) (_0(# 0 #)))
