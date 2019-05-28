-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 128; Boxes: 40; Branches: 16
-- Apps: 25; Lams: 11; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrderHard (gTest1,g''Test1,hTest0,g,g'',hTest1,g'Test0,h,gTest0,g') where

import GHC.Base
import GHC.Num

g = (\f -> (_0(# {-A-}\(x'2) -> (f (_1(# x'2 #))) #)))

_0(# f'4 #) = (\x'2 -> (GHC.Base.id {-P-}(f'4(x'2))))

_1(# x'2 #) = (GHC.Base.id x'2)

g' = (\f' -> (_2(# {-A-}\(x'3) -> (f' (_3(# x'3 #))) #)))

_2(# f'5 #) = (\x'3 -> (GHC.Base.id {-P-}(f'5(x'3))))

_3(# x'3 #) = (GHC.Base.id x'3)

g'' = (\f'2 -> (\x -> (GHC.Base.id (f'2 (GHC.Base.id x)))))

g''Test1 = (\y -> (_0(# {-A-}\(x'2) -> (((GHC.Num.+) (_1(# x'2 #))) y) #)))

g'Test0 = (_2(# {-A-}\(x'3) -> (((GHC.Num.+) (_3(# x'3 #))) 1) #))

gTest0 = (_0(# {-A-}\(x'2) -> (((GHC.Num.+) (_1(# x'2 #))) 1) #))

gTest1 = (\y' -> (_0(# {-A-}\(x'2) -> (((GHC.Num.+) (_1(# x'2 #))) y') #)))

h = (\f'3 -> (_4(# {-A-}\(x') -> (f'3 (_5(# (f'3 x') #))) #)))

_4(# f'7 #) = (\x' -> (GHC.Base.id {-P-}(f'7(x'))))

_5(# f'6 #) = (GHC.Base.id f'6)

hTest0 = (_4(# {-A-}\(x') -> (_6(# (_5(# (_6(# x' #)) #)) #)) #))

_6(# ds #) = (((GHC.Num.+) ds) 1)

hTest1 = (\y'2 -> (_4(# {-A-}\(x') -> (_7(# y'2, (_5(# (_7(# y'2, x' #)) #)) #)) #)))

_7(# y'2, ds' #) = (((GHC.Num.+) ds') y'2)
