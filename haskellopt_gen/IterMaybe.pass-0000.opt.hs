-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 150; Boxes: 25; Branches: 13
-- Apps: 36; Lams: 6; Unreduced Redexes: 2

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (loop,nats,count,main) where

import GHC.Base
import GHC.Classes
import GHC.List
import GHC.Maybe
import GHC.Num
import GHC.Show
import GHC.TopHandler
import System.Exit
import System.IO

count = (\n -> (_3(# (_2(# n, (_1(# _0 #)) #)), (_1(# _0 #)) #)))

_3(# f, st0 #) = (case f of {Nothing -> st0; Just arg0 -> (_3(# f, arg0 #))})

_2(# n', st0' #) = (case (((GHC.Classes.<) st0') n') of {False -> GHC.Maybe.Nothing; True -> (GHC.Maybe.Just (((GHC.Num.+) st0') 1))})

_1(# state #) = state

_0 = 0

loop = (\f' -> (\state' -> (_3(# (f' (_1(# state' #))), (_1(# state' #)) #))))

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) n3) (((:) 0) (((:) 1) (((:) 2) (((:) 3) (((:) 4) [])))))) of {False -> (((GHC.Base.>>) (System.IO.print n3)) System.Exit.exitFailure); True -> System.Exit.exitSuccess}))

n3 = (_3(# (_6(# _4, (_1(# (_5(# _4 #)) #)) #)), (_1(# (_5(# _4 #)) #)) #))

nats = (\m -> (_3(# (_6(# m, (_1(# (_5(# m #)) #)) #)), (_1(# (_5(# m #)) #)) #)))

_6(# m', st0'2 #) = (case (((GHC.Classes.<) (GHC.List.length st0'2)) (GHC.Num.fromInteger m')) of {False -> GHC.Maybe.Nothing; True -> (GHC.Maybe.Just (((:) (((GHC.Num.-) (GHC.List.head st0'2)) 1)) st0'2))})

_5(# m'2 #) = (((:) (((GHC.Num.-) m'2) 1)) [])

_4 = (_3(# (_2(# 5, (_1(# _0 #)) #)), (_1(# _0 #)) #))
