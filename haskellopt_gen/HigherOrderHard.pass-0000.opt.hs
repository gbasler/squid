-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrderHard (gTest1,g''Test1,hTest0,g,g'',hTest1,g'Test0,h,gTest0,g') where

import GHC.Base
import GHC.Num

g = (\f -> (_0(# {-A-}\(x'2) -> (f (_1(# x'2 #))) #)))

_0(# f'3 #) = (\x'2 -> (GHC.Base.id {-P-}(f'3(x'2))))

_1(# x'2 #) = (GHC.Base.id x'2)

g' = (\f' -> (_2(# {-A-}\(x'3) -> (f' (_3(# x'3 #))) #)))

_2(# f'4 #) = (\x'3 -> (GHC.Base.id {-P-}(f'4(x'3))))

_3(# x'3 #) = (GHC.Base.id x'3)

g'' = (\f'2 -> (\x -> (GHC.Base.id (f'2 (GHC.Base.id x)))))

g''Test1 = (\y -> (_0(# {-A-}\(x'2) -> (((GHC.Num.+) (_1(# x'2 #))) y) #)))

g'Test0 = (_2(# {-A-}\(x'3) -> (((GHC.Num.+) (_3(# x'3 #))) 1) #))

gTest0 = (_0(# {-A-}\(x'2) -> (((GHC.Num.+) (_1(# x'2 #))) 1) #))

gTest1 = (\y' -> (_0(# {-A-}\(x'2) -> (((GHC.Num.+) (_1(# x'2 #))) y') #)))

h = (\f'7 -> (_4(# {-A-}\(x') -> ((_5(# f'7 #)) (_6(# ((_5(# f'7 #)) x') #))) #)))

_4(# f'6 #) = (\x' -> (GHC.Base.id {-P-}(f'6(x'))))

_5(# f'7 #) = f'7

_6(# f'5 #) = (GHC.Base.id f'5)

hTest0 = (_4(# {-A-}\(x') -> (_7(# (_6(# (_7(# x' #)) #)) #)) #))

_7(# ds #) = (((GHC.Num.+) ds) 1)

hTest1 = (\y'2 -> (_4(# {-A-}\(x') -> (_8(# y'2, (_6(# (_8(# y'2, x' #)) #)) #)) #)))

_8(# y'2, ds' #) = (((GHC.Num.+) ds') y'2)
