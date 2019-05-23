-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (main,rec0,rec0_0,rec0'') where

import GHC.Base
import GHC.TopHandler

main = (GHC.TopHandler.runMainIO (GHC.Base.return ()))

rec0 = (\f -> let { _1 = f } in (f (_0(# {-A-}\(x) -> (_1 (_0(# {-A-}\(x) -> (_2(# f, _1, (_3(# f #)) #)) #))) #))))

_0(# f' #) = (\x -> ({-P-}(f'(x)) x))

_2(# f'3, f'4, f'5 #) = ((_3(# f'3 #)) (_0(# {-A-}\(x) -> (_2(# f'4, f'5, (_3(# f'3 #)) #)) #)))

_3(# f'2 #) = f'2

rec0'' = rec0

rec0_0 = GHC.Base.id

_4 = (\k -> GHC.Base.id)
