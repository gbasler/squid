-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (main,rec0,rec0_0,rec0'') where

import GHC.Base
import GHC.TopHandler

main = (GHC.TopHandler.runMainIO (GHC.Base.return ()))

rec0 = rec0'

rec0' = (\f'2 -> let { _3 = f'2 } in (f'2 (_0(# {-A-}\(x) -> (_3 (_0(# {-A-}\(x) -> (_4(# f'2, _3, (_2(# f'2 #)) #)) #))) #))))

rec0'' = rec0'

rec0_0 = GHC.Base.id

_0(# f #) = (\x -> ({-P-}(f(x)) x))

_1 = (\k -> GHC.Base.id)

_2(# f' #) = f'

_4(# f'3, f'4, f'5 #) = ((_2(# f'3 #)) (_0(# {-A-}\(x) -> (_4(# f'4, f'5, (_2(# f'3 #)) #)) #)))
