-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 263; Boxes: 77; Branches: 50
-- Apps: 50; Lams: 4; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module Basics (fTest4,gTest1,gTest6,gTest2,f,fTest3,gTest5,fTest0,fTest2,g,gTest4,fTest1,gTest0,foo,gTest3) where

import GHC.Num
import GHC.Types

f = (\x -> (_0(# x #)))

_0(# x'5 #) = (((GHC.Num.*) x'5) x'5)

fTest0 = (((GHC.Num.*) (_0(# (GHC.Types.I# 11#) #))) (_0(# (GHC.Types.I# 22#) #)))

fTest1 = (_0(# (_0(# (GHC.Types.I# 33#) #)) #))

fTest2 = (((GHC.Num.+) (_0(# (GHC.Types.I# 44#) #))) (_0(# (_0(# (GHC.Types.I# 55#) #)) #)))

fTest3 = (((GHC.Num.*) (_0(# (_0(# (GHC.Types.I# 66#) #)) #))) (_0(# (_0(# (GHC.Types.I# 77#) #)) #)))

fTest4 = (((GHC.Num.+) (_0(# (_0(# (GHC.Types.I# 66#) #)) #))) (_0(# (_0(# (GHC.Types.I# 77#) #)) #)))

foo = (\x'6 -> let { tmp = (((GHC.Num.*) x'6) (GHC.Types.I# 2#)) } in (((GHC.Num.+) tmp) tmp))

g = (\x' -> (_1(# {-A-}\(y'2) -> x' #)))

_1(# x'4 #) = (\y'2 -> (_2(# y'2, {-P-}(x'4(y'2)) #)))

gTest0 = (_2(# (GHC.Types.I# 4#), (_2(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)) #))

_2(# y', x'3 #) = (((GHC.Num.*) x'3) y')

gTest1 = (_2(# (_2(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)), (GHC.Types.I# 4#) #))

gTest2 = (_1(# {-A-}\(y'2) -> (_2(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)) #))

gTest3 = (_2(# (GHC.Types.I# 4#), (_2(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)) #))

gTest4 = (_2(# (_2(# (GHC.Types.I# 5#), (GHC.Types.I# 4#) #)), (_2(# (GHC.Types.I# 3#), (GHC.Types.I# 2#) #)) #))

gTest5_sub = (GHC.Types.I# 30#)
gTest5_sub' = (GHC.Types.I# 40#)
gTest5 = (((GHC.Num.+) (_2(# (_3(# gTest5_sub #)), (_4(# gTest5_sub #)) #))) (_2(# (_3(# gTest5_sub' #)), (_4(# gTest5_sub' #)) #)))

_3(# z' #) = (_2(# (GHC.Types.I# 22#), z' #))

_4(# z #) = (_2(# z, (GHC.Types.I# 11#) #))

gTest6 = (_2(# (_5(# (GHC.Types.I# 11#) #)), (_6(# (_2(# (_5(# (GHC.Types.I# 33#) #)), (_6(# (GHC.Types.I# 44#) #)) #)) #)) #))

_5(# y #) = y

_6(# x'2 #) = x'2
