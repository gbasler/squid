-- Generated Haskell code from Graph optimizer
-- Optimized after GHC phase:
--   desugar

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrderHard (gTest1,g''Test1,hTest0,g,g'',hTest1,g'Test0,h,gTest0,g') where

import GHC.Base
import GHC.Num

_0(# x #) = (GHC.Base.id x)

_1(# f #) = (\x -> (GHC.Base.id {-P-}(f(x))))

_2(# x' #) = (GHC.Base.id x')

_3(# f' #) = (\x' -> (GHC.Base.id {-P-}(f'(x'))))

_4(# f'2 #) = (GHC.Base.id f'2)

_5(# f'3 #) = (\x'2 -> (GHC.Base.id {-P-}(f'3(x'2))))

_6(# ds #) = (((GHC.Num.+) ds) 1)

_7(# y, ds' #) = (((GHC.Num.+) ds') y)

_8(# f'4 #) = f'4

g = (\f'5 -> (_1(# {-A-}\(x) -> (f'5 (_0(# x #))) #)))

g' = (\f'6 -> (_3(# {-A-}\(x') -> (f'6 (_2(# x' #))) #)))

g'' = (\f'7 -> (\x'3 -> (GHC.Base.id (f'7 (GHC.Base.id x'3)))))

g''Test1 = (\y' -> (_1(# {-A-}\(x) -> (((GHC.Num.+) (_0(# x #))) y') #)))

g'Test0 = (_3(# {-A-}\(x') -> (((GHC.Num.+) (_2(# x' #))) 1) #))

gTest0 = (_1(# {-A-}\(x) -> (((GHC.Num.+) (_0(# x #))) 1) #))

gTest1 = (\y'2 -> (_1(# {-A-}\(x) -> (((GHC.Num.+) (_0(# x #))) y'2) #)))

h(# x'2 #) = (\f'4 -> (_5(# {-A-}\(x'2) -> ((_8(# f'4 #)) (_4(# ((_8(# f'4 #)) x'2) #))) #)))

hTest0(# x'2 #) = (_5(# {-A-}\(x'2) -> (_6(# (_4(# (_6(# x'2 #)) #)) #)) #))

hTest1(# x'2 #) = (\y -> (_5(# {-A-}\(x'2) -> (_7(# y, (_4(# (_7(# y, x'2 #)) #)) #)) #)))
