-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (main,rec0,rec0_0,rec0') where

import GHC.Base
import GHC.TopHandler

main = (GHC.TopHandler.runMainIO (GHC.Base.return ()))

rec0 = (\f -> (_0(# {-A-}\(x) -> (_1(# (_2(# f #)), f, (_2(# f #)), f #)), f #)))

_0(# f'2, f'3 #) = (f'3 (_4(# f'2 #)))

_1(# f'8, f'9, f'10, f'11 #) = (f'10 (_4(# {-A-}\(x) -> (_7(# f'9, f'8, (_6(# f'11 #)) #)) #)))

_2(# f #) = f

rec0' = (_0(# {-A-}\(x) -> (_1(# _3, GHC.Base.id, _3, GHC.Base.id #)), GHC.Base.id #))

_3 = GHC.Base.id

rec0_0 = GHC.Base.id

_4(# f' #) = (\x -> ({-P-}(f'(x)) x))

_5 = (\k -> GHC.Base.id)

_6(# f'4 #) = f'4

_7(# f'5, f'6, f'7 #) = ((_6(# f'5 #)) (_4(# {-A-}\(x) -> (_7(# f'6, f'7, (_6(# f'5 #)) #)) #)))
