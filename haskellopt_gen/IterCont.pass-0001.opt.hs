-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 118; Boxes: 20; Branches: 6
-- Apps: 34; Lams: 5; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (count,loop,n5,main,rec,nats) where

import GHC.Base
import GHC.Classes
import GHC.List
import GHC.Num
import GHC.Show
import GHC.TopHandler
import System.Exit
import System.IO

count = (\n -> (_0(# n #)))

_0(# s #) = (case (((GHC.Classes.>) s) (GHC.Num.fromInteger 0)) of {False -> (GHC.Num.fromInteger 0); True -> (((GHC.Num.+) (_0(# (((GHC.Num.-) s) (GHC.Num.fromInteger 1)) #))) (GHC.Num.fromInteger 1))})

loop = (\f -> let { _1(# s' #) = ((f (\s'2 -> (_1(# s'2 #)))) s') } in (\state -> (_1(# state #))))

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) n5) (((:) 0) (((:) 1) (((:) 2) (((:) 3) (((:) 4) [])))))) of {False -> (((GHC.Base.>>) (System.IO.print n5)) System.Exit.exitFailure); True -> System.Exit.exitSuccess}))

n5 = ((GHC.List.take (_0(# 5 #))) nats)

nats = (_2(# 0 #))

_2(# s'3 #) = (((:) s'3) (_2(# (((GHC.Num.+) s'3) 1) #)))

rec = (\s'4 -> (_2(# s'4 #)))
