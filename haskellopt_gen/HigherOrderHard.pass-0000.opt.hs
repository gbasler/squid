-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Total nodes: 150; Boxes: 45; Branches: 27
-- Apps: 25; Lams: 17; Unreduced Redexes: 0

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module HigherOrderHard (gTest1,g''Test1,hTest0,g,g'',hTest1,g'Test0,h,gTest0,g') where

import GHC.Base
import GHC.Num

g = (\f -> (_0(# (f _1) #)))

_0(# f' #) = (\x -> (GHC.Base.id f'))

_1 = (GHC.Base.id x)

g' = (\f'2 -> (_2(# (f'2 _3) #)))

_2(# f'3 #) = (\x' -> (GHC.Base.id f'3))

_3 = (GHC.Base.id x')

g'' = (\f'4 -> (\x'2 -> (GHC.Base.id (f'4 (GHC.Base.id x'2)))))

g''Test1 = (\y -> (_0(# (((GHC.Num.+) _1) y) #)))

g'Test0 = (_2(# (((GHC.Num.+) _3) 1) #))

gTest0 = (_0(# (((GHC.Num.+) _1) 1) #))

gTest1 = (\y' -> (_0(# (((GHC.Num.+) _1) y') #)))

h = (\f'5 -> (_4(# (f'5 (GHC.Base.id (f'5 x'3))) #)))

_4(# f'6 #) = (\x'3 -> (GHC.Base.id f'6))

hTest0 = (_4(# (_5(# (GHC.Base.id (_5(# x'3 #))) #)) #))

_5(# ds #) = (((GHC.Num.+) ds) 1)

hTest1 = (\y'2 -> let _6(# ds' #) = (((GHC.Num.+) ds') y'2) in (_4(# (_6(# (GHC.Base.id (_6(# x'3 #))) #)) #)))
