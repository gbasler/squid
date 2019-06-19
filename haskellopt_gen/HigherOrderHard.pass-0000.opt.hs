-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 150; Boxes: 45; Branches: 27
-- Apps: 25; Lams: 17; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}

module HigherOrderHard (gTest1,g''Test1,hTest0,g,g'',hTest1,g'Test0,h,gTest0,g') where

import GHC.Base
import GHC.Num

g = (\f -> (_1(# (f _0) #)))

_1(# f' #) = (\x -> (GHC.Base.id f'))

_0 = (GHC.Base.id x)

g' = (\f'2 -> (_3(# (f'2 _2) #)))

_3(# f'3 #) = (\x' -> (GHC.Base.id f'3))

_2 = (GHC.Base.id x')

g'' = (\f'4 -> (\x'2 -> (GHC.Base.id (f'4 (GHC.Base.id x'2)))))

g''Test1 = (\y -> (_1(# (((GHC.Num.+) _0) y) #)))

g'Test0 = (_3(# (((GHC.Num.+) _2) 1) #))

gTest0 = (_1(# (((GHC.Num.+) _0) 1) #))

gTest1 = (\y' -> (_1(# (((GHC.Num.+) _0) y') #)))

h = (\f'5 -> (_6(# ((_4(# f'5 #)) (_5(# ((_4(# f'5 #)) x'3) #))) #)))

_6(# f'6 #) = (\x'3 -> (GHC.Base.id f'6))

_4(# f'7 #) = f'7

_5(# f'8 #) = (GHC.Base.id f'8)

hTest0 = (_6(# (_7(# (_5(# (_7(# x'3 #)) #)) #)) #))

_7(# ds #) = (((GHC.Num.+) ds) 1)

hTest1 = (\y'2 -> let { _8(# ds' #) = (((GHC.Num.+) ds') y'2) } in (_6(# (_8(# (_5(# (_8(# x'3 #)) #)) #)) #)))
