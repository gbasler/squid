-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 169; Boxes: 29; Branches: 20
-- Apps: 49; Lams: 5; Unreduced Redexes: 2

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

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

count = (_0(# (_1(# (_2(# 3 #)) #)), 3 #))

_0(# f, state #) = (case (((GHC.Classes.>) state) 0) of {False -> (GHC.Types.I# 0#); True -> (((GHC.Num.+) f) (GHC.Types.I# 1#))})

_1(# state' #) = (_0(# (_1(# (_2(# state' #)) #)), state' #))

_2(# s #) = (((GHC.Num.-) s) 1)

ds = (((,) (_0(# (_3(# (_2(# 3 #)) #)), 3 #))) (((:) _4) (_5(# (_6(# _4 #)) #))))

_3(# state'2 #) = (_0(# (_3(# (_2(# state'2 #)) #)), state'2 #))

_5(# state'3 #) = (((:) state'3) (_5(# (_6(# state'3 #)) #)))

_6(# s' #) = (((GHC.Num.+) s') (GHC.Types.I# 1#))

_4 = (GHC.Types.I# 0#)

main = (let sh = (GHC.Types.I# 3#) in (GHC.TopHandler.runMainIO (case (((GHC.Classes.&&) (((GHC.Classes.==) n3) (((:) (GHC.Types.I# 0#)) (((:) (GHC.Types.I# 1#)) (((:) (GHC.Types.I# 2#)) (((:) sh) (((:) (GHC.Types.I# 4#)) []))))))) (((GHC.Classes.==) count) sh)) of {False -> (((GHC.Base.>>) (System.IO.print n3)) System.Exit.exitFailure); True -> System.Exit.exitSuccess})))

n3 = ((GHC.List.take (GHC.Types.I# 5#)) nats)

nats = (((:) _4) (_7(# (_6(# _4 #)) #)))

_7(# state'4 #) = (((:) state'4) (_7(# (_6(# state'4 #)) #)))
