-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 121; Boxes: 26; Branches: 18
-- Apps: 32; Lams: 6; Unreduced Redexes: 0

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

count = (GHC.Base.id 0)

loop = (\f -> (\state -> ((f (let{-rec-} _1 = (_0(# {-A-}\(new_state) -> ((f _1) state) #)) in _1)) state)))

_0(# f' #) = (\new_state -> {-P-}(f'(new_state)))

main_sub' = ((GHC.List.take (GHC.Types.I# 3#)) nats)
main_sub'2 = (GHC.List.take (GHC.Types.I# 3#))
main_sub = (GHC.Types.I# 3#)
main = (GHC.TopHandler.runMainIO (case (((GHC.Classes.==) ((GHC.List.take main_sub) nats)) (((:) 0) (((:) 1) (((:) 2) [])))) of {False -> (((GHC.Base.>>) (((GHC.Base.$) System.IO.print) ((GHC.List.take main_sub) nats))) System.Exit.exitFailure); True -> System.Exit.exitSuccess}))

nats = (_2(# 0, (_2(# 0, (let{-rec-} _4 = (_3(# {-A-}\(new_state) -> _4, (\k -> (\s -> (_2(# s, (k (((GHC.Num.+) s) 1)) #)))), 0 #)) in _4) #)) #))

_2(# s', k' #) = (((:) s') k')

_3(# f'2, f'3, state' #) = ((f'3 (_0(# f'2 #))) state')
