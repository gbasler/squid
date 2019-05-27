-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 48; Boxes: 21; Branches: 16
-- Apps: 30; Lams: 6; Unreduced Redexes: 2

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrderRec (rec7,rec7Test1,rec0_0,rec7Test0,rec0) where

import GHC.Base
import GHC.Num

rec0 = (\f -> (f (_0(# {-A-}\(x) -> (_1(# f, (_2(# f #)) #)) #))))

_0(# f'13 #) = (\x -> ({-P-}(f'13(x)) x))

_1(# f'11, f'12 #) = ((_2(# f'11 #)) (_0(# {-A-}\(x) -> (_1(# f'12, (_2(# f'11 #)) #)) #)))

_2(# f'10 #) = f'10

rec0_0 = GHC.Base.id

rec7 = (\f' -> (_3(# {-A-}\(ds'2) -> (_4(# f', f' #)) #)))

_3(# f'4 #) = (\ds'2 -> (_6(# {-P-}(f'4(ds'2)), ds'2 #)))

_4(# f'6, f'7 #) = (f'7 (_10(# f'6 #)))

rec7Test0(# f' #) = (_3(# {-A-}\(ds'2) -> (_4(# f', f' #)) #))

rec7Test1 = (_3(# {-A-}\(ds'2) -> (_5(# (_6(# (_7(# (_8(# (\ds -> (_5(# ds #))) #)), (\ds -> (_5(# ds #))) #)), _9 #)) #)) #))

_5(# ds'3 #) = (((GHC.Num.*) ds'3) 2)

_6(# f'3, ds' #) = (case ds' of {() -> f'3})

_7(# f'8, f'9 #) = ((_8(# f'9 #)) (_10(# f'8 #)))

_8(# f'5 #) = f'5

_9 = ()

_10(# f'2 #) = (_6(# (_7(# (_8(# f'2 #)), f'2 #)), _9 #))
