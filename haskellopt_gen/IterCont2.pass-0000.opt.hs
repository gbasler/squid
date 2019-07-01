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

count = (_0(# ((_1(# (\k -> (\s -> (_0(# (k (_2(# s #))), s #)))) #)) (_2(# 5 #))), 5 #))

_0(# k', s' #) = (case (((GHC.Classes.>) s') 0) of {False -> (GHC.Types.I# 0#); True -> (((GHC.Num.+) k') (GHC.Types.I# 1#))})

_1(# f #) = (f (\new_state -> ((_1(# f #)) new_state)))

_2(# s'2 #) = (((GHC.Num.-) s'2) 1)

loop = (\f' -> (\state -> ((_1(# f' #)) state)))

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) n3) (((:) 0) (((:) 1) (((:) 2) (((:) 3) (((:) 4) [])))))) of {False -> (((GHC.Base.>>) (System.IO.print n3)) System.Exit.exitFailure); True -> System.Exit.exitSuccess}))

n3 = ((GHC.List.take count) nats)

nats = (((:) 0) ((_1(# (\k'2 -> (\s'3 -> (((:) s'3) (k'2 (_3(# s'3 #)))))) #)) (_3(# 0 #))))

_3(# s'4 #) = (((GHC.Num.+) s'4) 1)
