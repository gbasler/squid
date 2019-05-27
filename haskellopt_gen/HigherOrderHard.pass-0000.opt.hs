-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 88; Boxes: 35; Branches: 15
-- Apps: 60; Lams: 12; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrderHard (gTest1,g''Test1,hTest0,g,g'',hTest1,g'Test0,h,gTest0,g') where

import GHC.Base
import GHC.Num

g = (\f'9 -> (_0(# {-A-}\(x'2) -> (_1(# f'9 #)) #)))

_0(# f'3 #) = (\x'2 -> (GHC.Base.id {-P-}(f'3(x'2))))

_1(# f'9 #) = (f'9 (_4(# x'2 #)))

g' = (\f -> (_2(# {-A-}\(x'3) -> (f (_3(# x'3 #))) #)))

_2(# f'4 #) = (\x'3 -> (GHC.Base.id {-P-}(f'4(x'3))))

_3(# x'3 #) = (GHC.Base.id x'3)

g'' = (\f' -> (\x -> (GHC.Base.id (f' (GHC.Base.id x)))))

g''Test1 = (\y'2 -> (_0(# {-A-}\(x'2) -> (_1(# f'9 #)) #)))

g'Test0 = (_2(# {-A-}\(x'3) -> (((GHC.Num.+) (_3(# x'3 #))) 1) #))

gTest0 = (_0(# {-A-}\(x'2) -> (_1(# f'9 #)) #))

gTest1 = (\y -> (_0(# {-A-}\(x'2) -> (((GHC.Num.+) (_4(# x'2 #))) y) #)))

_4(# x'2 #) = (GHC.Base.id x'2)

h = (\f'2 -> (_5(# {-A-}\(x') -> (_6(# (f'2 x'), f'2 #)) #)))

_5(# f'6 #) = (\x' -> (GHC.Base.id {-P-}(f'6(x'))))

_6(# f'7, f'8 #) = (f'8 (_8(# f'7 #)))

hTest0 = (_5(# {-A-}\(x') -> (_7(# (_8(# (_7(# x' #)) #)) #)) #))

_7(# ds #) = (((GHC.Num.+) ds) 1)

_8(# f'5 #) = (GHC.Base.id f'5)

hTest1(# f'2 #) = (\y' -> (_5(# {-A-}\(x') -> (_6(# (_9(# y', x' #)), f'2 #)) #)))

_9(# y', ds' #) = (((GHC.Num.+) ds') y')
