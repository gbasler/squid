-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 181; Boxes: 29; Branches: 20
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

ds = (((,) (_5(# (_4(# (_3(# (_2(# (_1(# _0 #)) #)) #)) #)), (_1(# _0 #)) #))) (_10(# (_9(# (_8(# (_7(# (_1(# _6 #)) #)) #)) #)), (_1(# _6 #)) #)))

_5(# f, state #) = (case (((GHC.Classes.>) (_2(# state #))) 0) of {False -> (GHC.Types.I# 0#); True -> (((GHC.Num.+) f) (GHC.Types.I# 1#))})

_4(# state' #) = (_5(# (_4(# (_3(# (_2(# state' #)) #)) #)), state' #))

_3(# s #) = (((GHC.Num.-) s) 1)

_2(# state'2 #) = state'2

_1(# state'3 #) = state'3

_0 = 3

_10(# f', state'4 #) = (((:) (_7(# state'4 #))) f')

_9(# state'5 #) = (_10(# (_9(# (_8(# (_7(# state'5 #)) #)) #)), state'5 #))

_8(# s' #) = (((GHC.Num.+) s') (GHC.Types.I# 1#))

_7(# state'6 #) = state'6

_6 = (GHC.Types.I# 0#)

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.&&) (((GHC.Classes.==) n3) (((:) (GHC.Types.I# 0#)) (((:) (GHC.Types.I# 1#)) (((:) (GHC.Types.I# 2#)) (((:) (GHC.Types.I# 3#)) (((:) (GHC.Types.I# 4#)) []))))))) (((GHC.Classes.==) count) (GHC.Types.I# 3#))) of {False -> (((GHC.Base.>>) (System.IO.print n3)) System.Exit.exitFailure); True -> System.Exit.exitSuccess}))

n3 = ((GHC.List.take (GHC.Types.I# 5#)) nats)

nats = (case ds of {(,) arg0' arg1' -> arg1'})
