-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 48; Boxes: 21; Branches: 16
-- Apps: 30; Lams: 6; Unreduced Redexes: 2

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrderRec (rec7,rec7Test1,ds,rec0_0,rec7Test0,rec0) where

import GHC.Base
import GHC.Num

ds = 2

rec0 = (\f -> (f (_0(# {-A-}\(x) -> (_1(# f, (_2(# f #)) #)) #))))

_0(# f'2 #) = (\x -> ({-P-}(f'2(x)) x))

_1(# f'12, f'13 #) = ((_2(# f'12 #)) (_0(# {-A-}\(x) -> (_1(# f'13, (_2(# f'12 #)) #)) #)))

_2(# f'11 #) = f'11

rec0_0 = GHC.Base.id

rec7 = (\f' -> (_3(# {-A-}\(ds'3) -> (_4(# f', f' #)) #)))

_3(# f'5 #) = (\ds'3 -> (_6(# {-P-}(f'5(ds'3)), ds'3 #)))

_4(# f'7, f'8 #) = (f'8 (_10(# f'7 #)))

rec7Test0(# f' #) = (_3(# {-A-}\(ds'3) -> (_4(# f', f' #)) #))

rec7Test1 = (_3(# {-A-}\(ds'3) -> (_5(# (_6(# (_7(# (_8(# (\ds' -> (_5(# ds' #))) #)), (\ds' -> (_5(# ds' #))) #)), _9 #)) #)) #))

_5(# ds'4 #) = (((GHC.Num.*) ds'4) ds)

_6(# f'4, ds'2 #) = (case ds'2 of {() -> f'4})

_7(# f'9, f'10 #) = ((_8(# f'10 #)) (_10(# f'9 #)))

_8(# f'6 #) = f'6

_9 = ()

_10(# f'3 #) = (_6(# (_7(# (_8(# f'3 #)), f'3 #)), _9 #))
