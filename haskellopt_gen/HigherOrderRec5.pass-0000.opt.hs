-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 117; Boxes: 56; Branches: 71
-- Apps: 83; Lams: 7; Unreduced Redexes: 6

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (rec0_1,main,rec0_0,rec0_0_,rec0) where

import GHC.Base
import GHC.Num
import GHC.TopHandler

main = (GHC.TopHandler.runMainIO (GHC.Base.return ()))

rec0 = (\f -> let { _0 = (f _3) } in _0)

_3 = (\x -> (_0 x))

rec0_0 = GHC.Base.id

rec0_0_ = _1

_1 = (\s -> s)

rec0_1 = _2

_2 = (\s'2 -> (_4(# s'2 #)))

_4(# s' #) = (((GHC.Num.+) s') 1)
