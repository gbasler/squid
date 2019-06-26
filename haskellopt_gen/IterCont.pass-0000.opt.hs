-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 141; Boxes: 28; Branches: 20
-- Apps: 34; Lams: 7; Unreduced Redexes: 2

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

count = (_5(# (_4(# (_3(# (_2(# (_1(# _0 #)) #)) #)) #)), (_1(# _0 #)) #))

_5(# f, s #) = (case (((GHC.Classes.>) (_2(# s #))) 0) of {False -> (GHC.Types.I# 0#); True -> (((GHC.Num.+) f) (GHC.Types.I# 1#))})

_4(# s' #) = (_5(# (_4(# (_3(# (_2(# s' #)) #)) #)), s' #))

_3(# s'2 #) = (((GHC.Num.-) s'2) 1)

_2(# s'3 #) = s'3

_1(# state #) = state

_0 = 5

loop = (\f' -> (\state' -> ((_7(# (_6(# f' #)), f' #)) (_1(# state' #)))))

_7(# f'2, f'3 #) = (f'3 (\s'4 -> f'2))

_6(# f'4 #) = (_7(# (_6(# f'4 #)), f'4 #))

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) n3) (((:) 0) (((:) 1) (((:) 2) (((:) 3) (((:) 4) [])))))) of {False -> (((GHC.Base.>>) (System.IO.print n3)) System.Exit.exitFailure); True -> System.Exit.exitSuccess}))

n3 = ((GHC.List.take count) nats)

nats = (_12(# (_11(# (_10(# (_9(# (_1(# _8 #)) #)) #)) #)), (_1(# _8 #)) #))

_12(# f'5, s'5 #) = (((:) (_9(# s'5 #))) f'5)

_11(# s'6 #) = (_12(# (_11(# (_10(# (_9(# s'6 #)) #)) #)), s'6 #))

_10(# s'7 #) = (((GHC.Num.+) s'7) 1)

_9(# s'8 #) = s'8

_8 = 0
