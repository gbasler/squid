-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 64; Boxes: 21; Branches: 12
-- Apps: 8; Lams: 6; Unreduced Redexes: 2

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrderRec (rec7,rec7Test1,rec0_0,rec7Test0,rec0) where

import GHC.Base
import GHC.Num

rec0 = (\f -> (f (let{-rec-} _1 = (_0(# {-A-}\(x) -> (f _1) #)) in _1)))

_0(# f'2 #) = (\x -> ({-P-}(f'2(x)) x))

rec0_0 = GHC.Base.id

rec7 = (\f' -> (_2(# {-A-}\(ds'2) -> (_3(# f', f' #)) #)))

_2(# f'6 #) = (\ds'2 -> (_6(# {-P-}(f'6(ds'2)), ds'2 #)))

_3(# f'8, f'9 #) = (f'8 (_10(# f'9, f'9 #)))

rec7Test0 = (_2(# {-A-}\(ds'2) -> (_3(# _4, _4 #)) #))

_4 = ((GHC.Num.+) 1)

rec7Test1_sub = (\ds -> (_5(# ds #)))
rec7Test1 = (_2(# {-A-}\(ds'2) -> (_5(# (_6(# (_7(# (_8(# rec7Test1_sub #)), rec7Test1_sub #)), _9 #)) #)) #))

_5(# ds'3 #) = (((GHC.Num.*) ds'3) 2)

_6(# f'5, ds' #) = (case ds' of {() -> f'5})

_7(# f'10, f'11 #) = (f'11 (_10(# f'10, f'10 #)))

_8(# f'7 #) = f'7

_9 = ()

_10(# f'3, f'4 #) = (_6(# (_7(# (_8(# f'3 #)), f'4 #)), _9 #))
