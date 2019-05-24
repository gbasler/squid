-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrderRec (rec7,rec7Test1,rec0_0,rec7Test0,rec0) where

import GHC.Base
import GHC.Num

rec0 = (\f -> let { _1 = f } in (f (_0(# {-A-}\(x) -> (_1 (_0(# {-A-}\(x) -> (_2(# f, _1, (_3(# f #)) #)) #))) #))))

_0(# f'14 #) = (\x -> ({-P-}(f'14(x)) x))

_2(# f'11, f'12, f'13 #) = ((_3(# f'11 #)) (_0(# {-A-}\(x) -> (_2(# f'12, f'13, (_3(# f'11 #)) #)) #)))

_3(# f'10 #) = f'10

rec0_0 = GHC.Base.id

rec7 = (\f' -> (_4(# {-A-}\(ds') -> (_5(# f', f', f' #)) #)))

_4(# f'5 #) = (\ds' -> (_11(# {-P-}(f'5(ds')), ds' #)))

_5(# f'7, f'8, f'9 #) = (f'8 (_8(# f'9, f'7 #)))

rec7Test0 = (_4(# {-A-}\(ds') -> (_5(# _6, _6, _6 #)) #))

_6 = ((GHC.Num.+) 1)

rec7Test1 = (_4(# {-A-}\(ds') -> (_7(# (_8(# _9, _9 #)) #)) #))

_7(# ds'2 #) = (((GHC.Num.*) ds'2) 2)

_8(# f'2, f'3 #) = (_11(# ((_12(# f'2 #)) (_8(# f'3, (_12(# f'2 #)) #))), () #))

_9 = (\ds'3 -> (_7(# ds'3 #)))

_10 = (\k -> GHC.Base.id)

_11(# f'4, ds #) = (case ds of {() -> f'4})

_12(# f'6 #) = f'6
