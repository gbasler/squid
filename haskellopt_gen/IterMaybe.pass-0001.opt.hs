-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 135; Boxes: 23; Branches: 8
-- Apps: 34; Lams: 4; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (count,loop,main,nats,n3) where

import GHC.Base
import GHC.Classes
import GHC.List
import GHC.Num
import GHC.Show
import GHC.TopHandler
import System.Exit
import System.IO

count = (\n -> (_1(# n, _0 #)))

_1(# n', st0 #) = (case (((GHC.Classes.<) st0) n') of {False -> st0; True -> (_1(# n', (((GHC.Num.+) st0) 1) #))})

_0 = 0

loop = (\f -> let { _2(# st0' #) = (case (f st0') of {Nothing -> st0'; Just arg0 -> (_2(# arg0 #))}) } in (\state -> (_2(# state #))))

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) n3) (((:) 0) (((:) 1) (((:) 2) (((:) 3) (((:) 4) [])))))) of {False -> (((GHC.Base.>>) (System.IO.print n3)) System.Exit.exitFailure); True -> System.Exit.exitSuccess}))

n3 = (_5(# _3, (_4(# _3 #)) #))

_5(# m, st0'2 #) = (case (((GHC.Classes.<) (GHC.List.length st0'2)) (GHC.Num.fromInteger m)) of {False -> st0'2; True -> (_5(# m, (((:) (((GHC.Num.-) (GHC.List.head st0'2)) 1)) st0'2) #))})

_3 = (_1(# 5, _0 #))

_4(# m' #) = (((:) (((GHC.Num.-) m') 1)) [])

nats = (\m'2 -> (_5(# m'2, (_4(# m'2 #)) #)))
