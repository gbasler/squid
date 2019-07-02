-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 263; Boxes: 77; Branches: 50
-- Apps: 50; Lams: 4; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Basics (fTest4,gTest1,gTest6,gTest2,f,fTest3,gTest5,fTest0,fTest2,g,gTest4,fTest1,gTest0,foo,gTest3) where

import GHC.Num
import GHC.Types

f = (\x -> (_0(# x #)))

_0(# x' #) = (((GHC.Num.*) x') x')

fTest0 = (((GHC.Num.*) (_0(# (GHC.Types.I# 11#) #))) (_0(# (GHC.Types.I# 22#) #)))

fTest1 = (_0(# (_0(# (GHC.Types.I# 33#) #)) #))

fTest2 = (((GHC.Num.+) (_0(# (GHC.Types.I# 44#) #))) (_0(# (_0(# (GHC.Types.I# 55#) #)) #)))

fTest3 = (((GHC.Num.*) (_0(# (_0(# (GHC.Types.I# 66#) #)) #))) (_0(# (_0(# (GHC.Types.I# 77#) #)) #)))

fTest4 = (((GHC.Num.+) (_0(# (_0(# (GHC.Types.I# 66#) #)) #))) (_0(# (_0(# (GHC.Types.I# 77#) #)) #)))

foo = (\x'2 -> let tmp = (((GHC.Num.*) x'2) (GHC.Types.I# 2#)) in (((GHC.Num.+) tmp) tmp))

g = (\x'3 -> (_1(# x'3 #)))

_1(# x'4 #) = (\y -> (((GHC.Num.*) x'4) y))

gTest0 = (((GHC.Num.*) (((GHC.Num.*) (GHC.Types.I# 2#)) (GHC.Types.I# 3#))) (GHC.Types.I# 4#))

gTest1 = (((GHC.Num.*) (GHC.Types.I# 4#)) (((GHC.Num.*) (GHC.Types.I# 2#)) (GHC.Types.I# 3#)))

gTest2 = (_1(# (((GHC.Num.*) (GHC.Types.I# 2#)) (GHC.Types.I# 3#)) #))

gTest3 = (((GHC.Num.*) (((GHC.Num.*) (GHC.Types.I# 2#)) (GHC.Types.I# 3#))) (GHC.Types.I# 4#))

gTest4 = (((GHC.Num.*) (((GHC.Num.*) (GHC.Types.I# 2#)) (GHC.Types.I# 3#))) (((GHC.Num.*) (GHC.Types.I# 4#)) (GHC.Types.I# 5#)))

gTest5 = (((GHC.Num.+) (((GHC.Num.*) (_2(# _3 #))) (_4(# _3 #)))) (((GHC.Num.*) (_2(# _5 #))) (_4(# _5 #))))

_2(# z #) = (((GHC.Num.*) (GHC.Types.I# 11#)) z)

_3 = (GHC.Types.I# 30#)

_4(# z' #) = (((GHC.Num.*) z') (GHC.Types.I# 22#))

_5 = (GHC.Types.I# 40#)

gTest6 = (((GHC.Num.*) (((GHC.Num.*) (GHC.Types.I# 44#)) (GHC.Types.I# 33#))) (GHC.Types.I# 11#))
