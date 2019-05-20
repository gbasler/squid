-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module IterCont (loop,nats) where

import GHC.Num

_0(# _1 #) = (\new_state_a -> (_1(new_state_a)))

_2(# _3 #) = (((GHC.Num.+) _3) 1)

_4(# _5, _3 #) = (((:) _3) _5)

_6 = (\k_a -> (\s_a -> (_4(# (_7(# s_a, k_a #)), s_a #))))

_8(# f_a #) = f_a

_9(# _10 #) = _10

_11(# f_a #) = f_a

_12(# _1 #) = (_0(# (_1(new_state_a)) #))

_13(# _14 #) = _14

_15(# _1, _14 #) = (((_11(# f_a #)) (_0(# (_1(new_state_a)) #))) _14)

_16(# _10, _14, _1 #) = (((_9(# _10 #)) (_0(# (_1(new_state_a)) #))) _14)

_17(# _10, _18, _10, _19 #) = (_16(# _10, _18, \(new_state_a) -> (_17(# _10, (_20(# new_state_a #)), _19, (_9(# _10 #)) #)) #))

_7(# _3, _21 #) = (_21 (_2(# _3 #)))

_22(# _3 #) = (_2(# _3 #))

_20(# new_state_a #) = new_state_a

loop = (\f_a -> (\state_a -> (((_8(# f_a #)) (_0(# \(new_state_a) -> (_15(# \(new_state_a) -> (_17(# (_8(# f_a #)), (_20(# new_state_a #)), (_11(# f_a #)), (_9(# (_8(# f_a #)) #)) #)), (_20(# new_state_a #)) #)) #))) state_a)))

nats = (_4(# (_4(# (_7(# (_13(# (_22(# 0 #)) #)), (_12(# \(new_state_a) -> (_17(# _6, (_20(# new_state_a #)), _6, (_9(# _6 #)) #)) #)) #)), (_13(# (_22(# 0 #)) #)) #)), 0 #))
