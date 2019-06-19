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

count = (_0(# 0, GHC.Base.id #))

_0(# state', f'3 #) = (f'3 state')

loop = (\f -> (\state -> (_0(# state, (_1(# f #)) #))))

_1(# f'2 #) = (f'2 (\new_state' -> (_0(# (_3(# new_state' #)), (_1(# (_5(# f'2 #)) #)) #))))

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) n3) (((:) 0) (((:) 1) (((:) 2) [])))) of {False -> (((GHC.Base.>>) (System.IO.print n3)) System.Exit.exitFailure); True -> System.Exit.exitSuccess}))

n3 = ((GHC.List.take (GHC.Types.I# 3#)) nats)

nats = (_2(# 0, (_0(# (_3(# (_4(# 0 #)) #)), (_1(# (_5(# (\k -> (\s -> (_2(# s, (k (_4(# s #))) #)))) #)) #)) #)) #))

_2(# s'2, k' #) = (((:) s'2) k')

_3(# new_state #) = new_state

_4(# s' #) = (((GHC.Num.+) s') 1)

_5(# f' #) = f'
