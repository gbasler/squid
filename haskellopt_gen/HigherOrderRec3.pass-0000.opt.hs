-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 61; Boxes: 19; Branches: 11
-- Apps: 8; Lams: 6; Unreduced Redexes: 2

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrderRec (rec7,rec7Test1,rec0_0,rec7Test0,rec0) where

import GHC.Base
import GHC.Num

rec0 = (\f -> (f (_0(# {-A-}\(x) -> f #))))

_0(# f'12 #) = (\x -> let { x' = (_10(# {-P-}(f'12(x)) #)) } in ((x' (_0(# x' #))) x))

rec0_0 = GHC.Base.id

rec7 = (\f' -> (_1(# {-A-}\(ds'2) -> (_2(# f', f' #)) #)))

_1(# f'5 #) = (\ds'2 -> (_5(# {-P-}(f'5(ds'2)), ds'2 #)))

_2(# f'7, f'8 #) = (f'7 (_9(# f'8, f'8 #)))

rec7Test0 = (_1(# {-A-}\(ds'2) -> (_2(# _3, _3 #)) #))

_3 = ((GHC.Num.+) 1)

rec7Test1_sub = (\ds -> (_4(# ds #)))
rec7Test1 = (_1(# {-A-}\(ds'2) -> (_4(# (_5(# (_6(# (_7(# rec7Test1_sub #)), rec7Test1_sub #)), _8 #)) #)) #))

_4(# ds'3 #) = (((GHC.Num.*) ds'3) 2)

_5(# f'4, ds' #) = (case ds' of {() -> f'4})

_6(# f'9, f'10 #) = (f'10 (_9(# f'9, f'9 #)))

_7(# f'6 #) = f'6

_8 = ()

_9(# f'2, f'3 #) = (_5(# (_6(# (_7(# f'2 #)), f'3 #)), _8 #))

_10(# f'11 #) = f'11
