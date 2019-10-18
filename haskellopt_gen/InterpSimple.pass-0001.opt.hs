-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   Simplifier: Max iterations = 4
--               SimplMode {Phase = 0 [Non-opt simplification],
--                          inline,
--                          no rules,
--                          eta-expand,
--                          case-of-case}
-- Total nodes: 683; Boxes: 82; Branches: 555
-- Apps: 10; Lams: 3; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module InterpSimple (pgm,run,test) where

import GHC.Num
import GHC.Types

pgm = (((:) GHC.Types.True) (((:) GHC.Types.False) []))

run = (\pgm' -> (\pgmtail -> (\x -> let { sh = (_8(# (_13(# sh' #)) #)); sh' = (_13(# x #)) } in (_0(# (_1(# (_2(# (case (case pgmtail of (:) _ arg -> arg) of (:) arg _ -> arg), _3, (_0(# (_1(# _4, (case (case (case _5 of (:) _ arg -> arg) of (:) _ arg -> arg) of (:) arg _ -> arg), (_0(# _6, (case _7 of (:) _ arg -> arg), (_8(# sh #)) #)) #)), (case (case _9 of (:) _ arg -> arg) of (:) _ arg -> arg), sh #)), (case (case _10 of (:) _ arg -> arg) of (:) _ arg -> arg), (case (case _11 of (:) _ arg -> arg) of (:) arg _ -> arg), sh' #)), (case pgmtail of (:) arg _ -> arg), _12 #)), pgmtail, x #)))))

_0(# pgmtail', pgmtail'2, x' #) = (case pgmtail'2 of {[] -> x'; (:) arg0 arg1 -> pgmtail'})

_1(# pgmtail'3, pgmtail'4, pgmtail'5 #) = (case pgmtail'4 of {False -> pgmtail'5; True -> pgmtail'3})

_2(# pgmtail'6, pgmtail'7, pgmtail'8, pgmtail'9, pgmtail'10, x'2 #) = (let { sh'2 = (case pgmtail'9 of (:) _ arg -> arg); sh'3 = (_8(# (_13(# sh'4 #)) #)); sh'4 = (_13(# x'2 #)) } in (_1(# (_2(# pgmtail'10, pgmtail'8, (_0(# (_1(# _14, (case sh'2 of (:) arg _ -> arg), (_0(# _15, (case _16 of (:) _ arg -> arg), (_8(# sh'3 #)) #)) #)), sh'2, sh'3 #)), sh'2, (case pgmtail'9 of (:) arg _ -> arg), sh'4 #)), pgmtail'6, pgmtail'7 #)))

_3 = _3

_4 = _4

_5 = _5

_6 = _6

_7 = _7

_8(# x'3 #) = (((GHC.Num.*) x'3) (GHC.Num.fromInteger 2))

_13(# x'4 #) = (((GHC.Num.+) x'4) (GHC.Num.fromInteger 1))

_9 = _9

_10 = _10

_11 = _11

_12 = _12

test = _17

_17 = _17

_14 = _14

_15 = _15

_16 = _16
