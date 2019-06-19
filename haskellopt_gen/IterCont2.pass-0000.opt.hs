-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 112; Boxes: 24; Branches: 25
-- Apps: 22; Lams: 6; Unreduced Redexes: 2

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (loop,count,nats,main) where

import GHC.Base
import GHC.Classes
import GHC.List
import GHC.Num
import GHC.Show
import GHC.TopHandler
import GHC.Types
import System.Exit
import System.IO

count = (_0(# GHC.Base.id, 0 #))

_0(# f, state #) = (f state)

loop = (\f' -> (\state' -> (_0(# (_1(# f' #)), state' #))))

_1(# f'2 #) = (f'2 (\new_state -> (_0(# (_1(# (_2(# f'2 #)) #)), (_3(# new_state #)) #))))

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) n3) (((:) 0) (((:) 1) (((:) 2) [])))) of {False -> (((GHC.Base.>>) (System.IO.print n3)) System.Exit.exitFailure); True -> System.Exit.exitSuccess}))

n3 = ((GHC.List.take (GHC.Types.I# 3#)) nats)

nats = (_5(# (_0(# (_1(# (_2(# (\k -> (\s -> (_5(# (k (_4(# s #))), s #)))) #)) #)), (_3(# (_4(# (_6(# 0 #)) #)) #)) #)), (_6(# 0 #)) #))

_5(# k', s' #) = (((:) s') k')

_6(# state'2 #) = state'2

_2(# f'3 #) = f'3

_3(# new_state' #) = new_state'

_4(# s'2 #) = (((GHC.Num.+) s'2) 1)
