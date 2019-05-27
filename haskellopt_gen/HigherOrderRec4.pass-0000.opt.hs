-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 22; Boxes: 10; Branches: 5
-- Apps: 15; Lams: 3; Unreduced Redexes: 1

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (main,rec0,rec0_0,rec0') where

import GHC.Base
import GHC.TopHandler

main = (GHC.TopHandler.runMainIO (GHC.Base.return ()))

rec0 = (\f -> (_0(# {-A-}\(x) -> f, f #)))

_0(# f'3, f'4 #) = (f'4 (_1(# f'3 #)))

rec0' = (_0(# {-A-}\(x) -> GHC.Base.id, GHC.Base.id #))

rec0_0 = GHC.Base.id

_1(# f' #) = (\x -> (((_2(# {-P-}(f'(x)) #)) (_1(# (_2(# {-P-}(f'(x)) #)) #))) x))

_2(# f'2 #) = f'2
