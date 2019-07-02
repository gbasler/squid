-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 183; Boxes: 51; Branches: 25
-- Apps: 37; Lams: 14; Unreduced Redexes: 1

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Main (rec7,rec3,rec2,rec8,rec7Test1,rec7Test2,main,ds,rec7Test0,rec0,rec1) where

import GHC.Base
import GHC.Classes
import GHC.List
import GHC.Num
import GHC.TopHandler
import GHC.Types
import System.Exit

ds = 2

main = (let sh = ((:) 1) in (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) rec7Test2) (sh (sh (sh [])))) of {False -> System.Exit.exitFailure; True -> System.Exit.exitSuccess})))

rec7Test2 = (((GHC.Base.$) (GHC.List.take (GHC.Types.I# 3#))) (_0(# ((:) 1) #)))

rec0 = (\f -> (_1(# f #)))

_1(# f' #) = (f' (\x -> ((_1(# f' #)) x)))

rec1 = (\f'2 -> (\x' -> (_2(# f'2 #))))

_2(# f'3 #) = (f'3 (_2(# f'3 #)))

rec2 = (\f'4 -> (\x'2 -> (_3(# f'4, x'2 #))))

_3(# f'5, x'3 #) = (((:) x'3) (f'5 (_3(# f'5, x'3 #))))

rec3 = (\f'6 -> (\x'4 -> (\y -> (_4(# f'6, x'4, y #)))))

_4(# f'7, x'5, y' #) = (((:) (f'7 x'5)) (_4(# f'7, (f'7 y'), x'5 #)))

rec7 = (\f'8 -> (_5(# (_0(# f'8 #)) #)))

_5(# f'9 #) = (\ds' -> (case ds' of {() -> f'9}))

_0(# f'10 #) = (f'10 (_0(# f'10 #)))

rec7Test0 = (_5(# (_0(# ((GHC.Num.+) 1) #)) #))

rec7Test1 = (_5(# (_6(# (_0(# (\ds'2 -> (_6(# ds'2 #))) #)) #)) #))

_6(# ds'3 #) = (((GHC.Num.*) ds'3) 2)

rec8 = (\eta -> (\eta' -> (_7(# eta, eta' #))))

_7(# f'11, x'6 #) = (let { sh' = (sx(# x'6 #)); sh'2 = (f'11 (_7(# f'11, sh' #))) } in (((GHC.Num.-) sh') (((GHC.Num.*) sh'2) sh'2)))

sx(# x'7 #) = (((GHC.Num.+) x'7) (GHC.Num.fromInteger 1))
