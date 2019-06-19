-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 103; Boxes: 22; Branches: 18
-- Apps: 22; Lams: 6; Unreduced Redexes: 1

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (count,loop,main,nats,n3) where

import GHC.Base
import GHC.Classes
import GHC.List
import GHC.Num
import GHC.Show
import GHC.TopHandler
import GHC.Types
import System.Exit
import System.IO

count = (_0(# 0, GHC.Base.id #))

_0(# state', f'3 #) = (f'3 state')

loop = (\f -> (\state -> (_0(# state, (_1(# f #)) #))))

_1(# f'2 #) = (f'2 (\new_state' -> (_0(# (_3(# new_state' #)), (_1(# (_6(# f'2 #)) #)) #))))

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) n3) (((:) 0) (((:) 1) (((:) 2) [])))) of {False -> (((GHC.Base.>>) (System.IO.print n3)) System.Exit.exitFailure); True -> System.Exit.exitSuccess}))

n3 = ((GHC.List.take (GHC.Types.I# 3#)) nats)

nats = (_2(# 0, (_0(# (_3(# (_4(# (let{-rec-} _5 = (_3(# (_4(# _5 #)) #)) in _5) #)) #)), (_1(# (let{-rec-} _7 = (_6(# _7 #)) in _7) #)) #)) #))

_2(# s', k #) = (((:) s') k)

_3(# new_state #) = new_state

_4(# s #) = (((GHC.Num.+) s) 1)

_6(# f' #) = f'
