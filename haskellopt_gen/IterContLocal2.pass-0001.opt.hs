-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 186; Boxes: 31; Branches: 21
-- Apps: 49; Lams: 7; Unreduced Redexes: 2

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (count,loop,main,ds,nats,n3) where

import GHC.Base
import GHC.Classes
import GHC.List
import GHC.Num
import GHC.Show
import GHC.TopHandler
import GHC.Types
import System.Exit
import System.IO

count = (case ds of {(,) arg0 arg1 -> arg0})

ds = (((,) (_1(# (_6(# (_3(# (_2(# (\k -> (\s -> (_1(# (k (_0(# s #))), s #)))) #)) #)), (_5(# (_0(# (_4(# 3 #)) #)) #)) #)), (_4(# 3 #)) #))) (_8(# (_6(# (_3(# (_2(# (\k' -> (\s' -> (_8(# (k' (_7(# s' #))), s' #)))) #)) #)), (_5(# (_7(# (_9(# (GHC.Types.I# 0#) #)) #)) #)) #)), (_9(# (GHC.Types.I# 0#) #)) #)))

_1(# k'2, s'2 #) = (case (((GHC.Classes.>) s'2) 0) of {False -> (GHC.Types.I# 0#); True -> (((GHC.Num.+) k'2) (GHC.Types.I# 1#))})

_6(# f, state #) = (f state)

_3(# f' #) = (f' (\new_state -> (_6(# (_3(# (_2(# f' #)) #)), (_5(# new_state #)) #))))

_2(# f'2 #) = f'2

_5(# new_state' #) = new_state'

_0(# s'3 #) = (((GHC.Num.-) s'3) 1)

_4(# state' #) = state'

_8(# k'3, s'4 #) = (((:) s'4) k'3)

_7(# s'5 #) = (((GHC.Num.+) s'5) (GHC.Types.I# 1#))

_9(# state'2 #) = state'2

loop = (\f'3 -> (\state'3 -> (_6(# (_3(# f'3 #)), state'3 #))))

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.&&) (((GHC.Classes.==) n3) (((:) (GHC.Types.I# 0#)) (((:) (GHC.Types.I# 1#)) (((:) (GHC.Types.I# 2#)) (((:) (GHC.Types.I# 3#)) (((:) (GHC.Types.I# 4#)) []))))))) (((GHC.Classes.==) count) (GHC.Types.I# 3#))) of {False -> (((GHC.Base.>>) (System.IO.print n3)) System.Exit.exitFailure); True -> System.Exit.exitSuccess}))

n3 = ((GHC.List.take (GHC.Types.I# 5#)) nats)

nats = (case ds of {(,) arg0' arg1' -> arg1'})
