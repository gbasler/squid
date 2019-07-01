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

count = (\n -> (_5(# (_4(# (_3(# (_2(# (_1(# (_0(# n #)) #)) #)) #)) #)), (_1(# (_0(# n #)) #)) #)))

_5(# f, s #) = (case (((GHC.Classes.>) (_2(# s #))) (GHC.Num.fromInteger 0)) of {False -> (GHC.Num.fromInteger 0); True -> (((GHC.Num.+) (((GHC.Num.+) (x(# f #))) (x(# f #)))) (GHC.Num.fromInteger 1))})

_4(# s' #) = (_5(# (_4(# (_3(# (_2(# s' #)) #)) #)), s' #))

_3(# s'2 #) = (((GHC.Num.-) s'2) (GHC.Num.fromInteger 1))

_2(# s'3 #) = s'3

_1(# state #) = state

_0(# n' #) = n'

loop = (\f' -> (\state' -> ((_7(# (_6(# f' #)), f' #)) (_1(# state' #)))))

_7(# f'2, f'3 #) = (f'3 (\s'4 -> f'2))

_6(# f'4 #) = (_7(# (_6(# f'4 #)), f'4 #))

main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) n3) (((:) 0) (((:) 1) (((:) 2) (((:) 3) (((:) 4) (((:) 5) (((:) 6) [])))))))) of {False -> (((GHC.Base.>>) (System.IO.print n3)) System.Exit.exitFailure); True -> System.Exit.exitSuccess}))

n3 = ((GHC.List.take (_5(# (_9(# (_3(# (_2(# (_1(# (_0(# _8 #)) #)) #)) #)) #)), (_1(# (_0(# _8 #)) #)) #))) nats)

nats = (_14(# (_13(# (_12(# (_11(# (_1(# _10 #)) #)) #)) #)), (_1(# _10 #)) #))

_14(# f'5, s'5 #) = (((:) (_11(# s'5 #))) f'5)

_13(# s'6 #) = (_14(# (_13(# (_12(# (_11(# s'6 #)) #)) #)), s'6 #))

_12(# s'7 #) = (((GHC.Num.+) s'7) 1)

_11(# s'8 #) = s'8

_10 = 0

_9(# s'9 #) = (_5(# (_9(# (_3(# (_2(# s'9 #)) #)) #)), s'9 #))

x(# k #) = k

_8 = 3
