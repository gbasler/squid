-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 108; Boxes: 17; Branches: 5
-- Apps: 32; Lams: 4; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (count,loop,main,rec,nats,n3) where

import GHC.Base
import GHC.Classes
import GHC.List
import GHC.Num
import GHC.Show
import GHC.TopHandler
import GHC.Types
import System.Exit
import System.IO

count = (_0(# 5 #))

_0(# s #) = (case (((GHC.Classes.>) s) 0) of {False -> (GHC.Types.I# 0#); True -> (((GHC.Num.+) (_0(# (((GHC.Num.-) s) 1) #))) (GHC.Types.I# 1#))})

loop = (\f -> let { _1(# s' #) = ((f (\s'2 -> (_1(# s'2 #)))) s') } in (\state -> (_1(# state #))))

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) n3) (((:) 0) (((:) 1) (((:) 2) (((:) 3) (((:) 4) [])))))) of {False -> (((GHC.Base.>>) (System.IO.print n3)) System.Exit.exitFailure); True -> System.Exit.exitSuccess}))

n3 = ((GHC.List.take count) nats)

nats = (_2(# 0 #))

_2(# s'3 #) = (((:) s'3) (_2(# (((GHC.Num.+) s'3) 1) #)))

rec = (\s'4 -> (_2(# s'4 #)))
