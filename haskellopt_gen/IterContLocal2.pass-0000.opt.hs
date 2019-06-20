-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 186; Boxes: 31; Branches: 22
-- Apps: 49; Lams: 5; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (ds,count,nats,main) where

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

ds = (((,) (_1(# (_7(# (_3(# (_2(# (\k -> (\s -> (_1(# (k (_0(# s #))), s #)))) #)) #)), (_4(# (_6(# (_0(# (_5(# (_4(# 3 #)) #)) #)) #)) #)) #)), (_5(# (_4(# 3 #)) #)) #))) (_9(# (_7(# (_3(# (_2(# (\k' -> (\s' -> (_9(# (k' (_8(# s' #))), s' #)))) #)) #)), (_4(# (_6(# (_8(# (_10(# (_4(# (GHC.Types.I# 0#) #)) #)) #)) #)) #)) #)), (_10(# (_4(# (GHC.Types.I# 0#) #)) #)) #)))

_1(# k'2, s'2 #) = (case (((GHC.Classes.>) s'2) 0) of {False -> (GHC.Types.I# 0#); True -> (((GHC.Num.+) k'2) (GHC.Types.I# 1#))})

_7(# f, state #) = (f state)

_3(# f' #) = (f' (\new_state -> (_7(# (_3(# (_2(# f' #)) #)), (_4(# (_6(# new_state #)) #)) #))))

_2(# f'2 #) = f'2

_4(# state' #) = state'

_6(# new_state' #) = new_state'

_0(# s'3 #) = (((GHC.Num.-) s'3) 1)

_5(# state'2 #) = state'2

_9(# k'3, s'4 #) = (((:) s'4) k'3)

_8(# s'5 #) = (((GHC.Num.+) s'5) (GHC.Types.I# 1#))

_10(# state'3 #) = state'3

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.&&) (((GHC.Classes.==) n3) (((:) (GHC.Types.I# 0#)) (((:) (GHC.Types.I# 1#)) (((:) (GHC.Types.I# 2#)) (((:) (GHC.Types.I# 3#)) (((:) (GHC.Types.I# 4#)) []))))))) (((GHC.Classes.==) count) (GHC.Types.I# 3#))) of {False -> (((GHC.Base.>>) (System.IO.print n3)) System.Exit.exitFailure); True -> System.Exit.exitSuccess}))

n3 = ((GHC.List.take (GHC.Types.I# 5#)) nats)

nats = (case ds of {(,) arg0' arg1' -> arg1'})
