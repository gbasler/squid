-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 42; Boxes: 17; Branches: 11
-- Apps: 25; Lams: 6; Unreduced Redexes: 2

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrderRec (rec7,rec7Test1,rec0_0,rec7Test0,rec0) where

import GHC.Base
import GHC.Num

rec0 = (\f -> (f (_0(# {-A-}\(x) -> f #))))

_0(# f'11 #) = (\x -> (((_9(# {-P-}(f'11(x)) #)) (_0(# (_9(# {-P-}(f'11(x)) #)) #))) x))

rec0_0 = GHC.Base.id

rec7 = (\f' -> (_1(# {-A-}\(ds'2) -> (_2(# f', f' #)) #)))

_1(# f'4 #) = (\ds'2 -> (_4(# {-P-}(f'4(ds'2)), ds'2 #)))

_2(# f'6, f'7 #) = (f'7 (_8(# f'6 #)))

rec7Test0(# f' #) = (_1(# {-A-}\(ds'2) -> (_2(# f', f' #)) #))

rec7Test1 = (_1(# {-A-}\(ds'2) -> (_3(# (_4(# (_5(# (_6(# (\ds -> (_3(# ds #))) #)), (\ds -> (_3(# ds #))) #)), _7 #)) #)) #))

_3(# ds'3 #) = (((GHC.Num.*) ds'3) 2)

_4(# f'3, ds' #) = (case ds' of {() -> f'3})

_5(# f'8, f'9 #) = ((_6(# f'9 #)) (_8(# f'8 #)))

_6(# f'5 #) = f'5

_7 = ()

_8(# f'2 #) = (_4(# (_5(# (_6(# f'2 #)), f'2 #)), _7 #))

_9(# f'10 #) = f'10
