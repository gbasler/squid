-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 56; Boxes: 25; Branches: 25
-- Apps: 37; Lams: 7; Unreduced Redexes: 4

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (rec0_1,main,rec0_0,rec0_0_,rec0) where

import GHC.Base
import GHC.Num
import GHC.TopHandler

main = (GHC.TopHandler.runMainIO (GHC.Base.return ()))

rec0(# f #) = (\f' -> (f' (_0(# {-P-}(f(f')) #))))

_0(# f'2 #) = (\x -> {-P-}(f'2(x)))

rec0_0 = GHC.Base.id

rec0_0_ = _1

_1 = (\s -> s)

rec0_1 = _2

_2 = (\s'2 -> (_3(# s'2 #)))

_3(# s' #) = (((GHC.Num.+) s') 1)
