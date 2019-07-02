-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 163; Boxes: 32; Branches: 21
-- Apps: 42; Lams: 8; Unreduced Redexes: 2

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Main (loop,count,nats,main) where

import GHC.Base
import GHC.Classes
import GHC.List
import GHC.Num
import GHC.Show
import GHC.TopHandler
import System.Exit
import System.IO

count = (\n -> (_0(# (_1(# (_2(# n #)) #)), n #)))

_0(# f, s #) = (case (((GHC.Classes.>) s) (GHC.Num.fromInteger 0)) of {False -> (GHC.Num.fromInteger 0); True -> (((GHC.Num.+) (((GHC.Num.+) f) f)) (GHC.Num.fromInteger 1))})

_1(# s' #) = (_0(# (_1(# (_2(# s' #)) #)), s' #))

_2(# s'2 #) = (((GHC.Num.-) s'2) (GHC.Num.fromInteger 1))

loop = (\f' -> (\state -> ((f' (\s'3 -> (_3(# f' #)))) state)))

_3(# f'2 #) = (f'2 (\s'3 -> (_3(# f'2 #))))

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) n3) (((:) 0) (((:) 1) (((:) 2) (((:) 3) (((:) 4) (((:) 5) (((:) 6) [])))))))) of {False -> (((GHC.Base.>>) (System.IO.print n3)) System.Exit.exitFailure); True -> System.Exit.exitSuccess}))

n3 = ((GHC.List.take (_0(# (_4(# (_2(# 3 #)) #)), 3 #))) nats)

nats = (((:) 0) (_5(# (_6(# 0 #)) #)))

_5(# s'4 #) = (((:) s'4) (_5(# (_6(# s'4 #)) #)))

_6(# s'5 #) = (((GHC.Num.+) s'5) 1)

_4(# s'6 #) = (_0(# (_4(# (_2(# s'6 #)) #)), s'6 #))
