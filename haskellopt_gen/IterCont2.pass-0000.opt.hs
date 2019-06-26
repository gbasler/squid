-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 154; Boxes: 30; Branches: 31
-- Apps: 34; Lams: 7; Unreduced Redexes: 3

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

count = (_1(# (_6(# (_3(# (_2(# (\k -> (\s -> (_1(# (k (_0(# s #))), s #)))) #)) #)), (_5(# (_0(# (_4(# 5 #)) #)) #)) #)), (_4(# 5 #)) #))

_1(# k', s' #) = (case (((GHC.Classes.>) s') 0) of {False -> (GHC.Types.I# 0#); True -> (((GHC.Num.+) k') (GHC.Types.I# 1#))})

_6(# f, state #) = (f state)

_3(# f' #) = (f' (\new_state -> (_6(# (_3(# (_2(# f' #)) #)), (_5(# new_state #)) #))))

_2(# f'2 #) = f'2

_5(# new_state' #) = new_state'

_0(# s'2 #) = (((GHC.Num.-) s'2) 1)

_4(# state' #) = state'

loop = (\f'3 -> (\state'2 -> (_6(# (_3(# f'3 #)), state'2 #))))

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) n3) (((:) 0) (((:) 1) (((:) 2) (((:) 3) (((:) 4) [])))))) of {False -> (((GHC.Base.>>) (System.IO.print n3)) System.Exit.exitFailure); True -> System.Exit.exitSuccess}))

n3 = ((GHC.List.take count) nats)

nats = (_8(# (_6(# (_3(# (_2(# (\k'2 -> (\s'3 -> (_8(# (k'2 (_7(# s'3 #))), s'3 #)))) #)) #)), (_5(# (_7(# (_9(# 0 #)) #)) #)) #)), (_9(# 0 #)) #))

_8(# k'3, s'4 #) = (((:) s'4) k'3)

_7(# s'5 #) = (((GHC.Num.+) s'5) 1)

_9(# state'3 #) = state'3
