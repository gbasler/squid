-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 141; Boxes: 41; Branches: 22
-- Apps: 25; Lams: 17; Unreduced Redexes: 0

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

h = (\f'3 -> (_4(# {-A-}\(x') -> (_5(# (_6(# x', f'3 #)), f'3 #)) #)))

_4(# f'8 #) = (\x' -> (GHC.Base.id {-P-}(f'8(x'))))

_5(# f'9, f'10 #) = ((_10(# f'10 #)) (_8(# f'9 #)))

_6(# x', f'11 #) = ((_10(# f'11 #)) x')

hTest0 = (_4(# {-A-}\(x') -> (_7(# (_8(# (_6(# x', (\ds -> (_7(# ds #))) #)) #)) #)) #))

_7(# ds'2 #) = (((GHC.Num.+) ds'2) 1)

_8(# f'7 #) = (GHC.Base.id f'7)

hTest1 = (\y'2 -> (_4(# {-A-}\(x') -> (_5(# (_9(# y'2, x' #)), (\ds' -> (_9(# y'2, ds' #))) #)) #)))

_9(# y'2, ds'3 #) = (((GHC.Num.+) ds'3) y'2)

_10(# f'6 #) = f'6
