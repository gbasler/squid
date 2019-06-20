-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 94; Boxes: 17; Branches: 10
-- Apps: 26; Lams: 3; Unreduced Redexes: 1

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (nats,main) where

import GHC.Base
import GHC.Classes
import GHC.List
import GHC.Num
import GHC.Show
import GHC.TopHandler
import GHC.Types
import System.Exit
import System.IO

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) n3) (((:) 0) (((:) 1) (((:) 2) (((:) 3) (((:) 4) [])))))) of {False -> (((GHC.Base.>>) (System.IO.print n3)) System.Exit.exitFailure); True -> System.Exit.exitSuccess}))

n3 = ((GHC.List.take (GHC.Types.I# 5#)) nats)

nats = (_6(# (_5(# (_4(# (_3(# (_2(# (_1(# _0 #)) #)) #)) #)) #)), (_1(# _0 #)) #))

_6(# f, st #) = (((:) (_2(# st #))) f)

_5(# st' #) = (_6(# (_5(# (_4(# (_3(# (_2(# st' #)) #)) #)) #)), st' #))

_4(# new_st #) = new_st

_3(# s #) = (((GHC.Num.+) s) 1)

_2(# st'2 #) = st'2

_1(# state #) = state

_0 = 0
