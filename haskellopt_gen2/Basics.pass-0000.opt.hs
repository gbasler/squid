-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  52
-- Incl. one-shot:  2
-- Total nodes: 1613; Boxes: 410; Branches: 370
-- Apps: 284; Lams: 7

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module Basics (foo,foo',foo'',fTest4,fTest3,fTest2,fTest1,fTest0,f,gTest6,gTest5,gTest4,gTest3,gTest2,gTest1,gTest0,g) where

import GHC.Num
import GHC.Types

foo = (\x -> let sh = ((GHC.Num.* x) (GHC.Types.I# 2#)) in ((GHC.Num.+ sh) sh))

foo' = (\x' -> ((GHC.Num.* ((GHC.Num.+ x') (GHC.Types.I# 1#))) (GHC.Num.fromInteger 2)))

foo'' = (\x'2 -> ((GHC.Num.* ((GHC.Num.+ x'2) (GHC.Types.I# 1#))) x'2))

fTest4 = (let { sh' = (GHC.Types.I# 77#); sh'3 = ((GHC.Num.* sh') sh'); sh'2 = ((GHC.Num.* sh'4) sh'4); sh'4 = (GHC.Types.I# 66#) } in ((GHC.Num.+ ((GHC.Num.* sh'2) sh'2)) ((GHC.Num.* sh'3) sh'3)))

fTest3 = (let { sh'5 = (GHC.Types.I# 77#); sh'7 = ((GHC.Num.* sh'5) sh'5); sh'6 = ((GHC.Num.* sh'8) sh'8); sh'8 = (GHC.Types.I# 66#) } in ((GHC.Num.* ((GHC.Num.* sh'6) sh'6)) ((GHC.Num.* sh'7) sh'7)))

fTest2 = (let { sh'9 = (GHC.Types.I# 55#); sh'11 = ((GHC.Num.* sh'9) sh'9); sh'10 = (GHC.Types.I# 44#) } in ((GHC.Num.+ ((GHC.Num.* sh'10) sh'10)) ((GHC.Num.* sh'11) sh'11)))

fTest1 = (let { sh'12 = (GHC.Types.I# 33#); sh'13 = ((GHC.Num.* sh'12) sh'12) } in ((GHC.Num.* sh'13) sh'13))

fTest0 = (let { sh'14 = (GHC.Types.I# 22#); sh'15 = (GHC.Types.I# 11#) } in ((GHC.Num.* ((GHC.Num.* sh'15) sh'15)) ((GHC.Num.* sh'14) sh'14)))

f = (\x'3 -> ((GHC.Num.* x'3) x'3))

gTest6 = ((GHC.Num.* ((GHC.Num.* (GHC.Types.I# 44#)) (GHC.Types.I# 33#))) (GHC.Types.I# 11#))

gTest5 = (let { sh'16 = (GHC.Types.I# 30#); sh'17 = (GHC.Num.* (GHC.Types.I# 11#)); sh'19 = (GHC.Types.I# 40#); sh'18 = (GHC.Types.I# 22#) } in ((GHC.Num.+ ((GHC.Num.* (sh'17 sh'16)) ((GHC.Num.* sh'16) sh'18))) ((GHC.Num.* (sh'17 sh'19)) ((GHC.Num.* sh'19) sh'18))))

gTest4 = ((GHC.Num.* ((GHC.Num.* (GHC.Types.I# 2#)) (GHC.Types.I# 3#))) ((GHC.Num.* (GHC.Types.I# 4#)) (GHC.Types.I# 5#)))

gTest3 = ((GHC.Num.* ((GHC.Num.* (GHC.Types.I# 2#)) (GHC.Types.I# 3#))) (GHC.Types.I# 4#))

gTest2 = (\y -> ((GHC.Num.* ((GHC.Num.* (GHC.Types.I# 2#)) (GHC.Types.I# 3#))) y))

gTest1 = ((GHC.Num.* (GHC.Types.I# 4#)) ((GHC.Num.* (GHC.Types.I# 2#)) (GHC.Types.I# 3#)))

gTest0 = ((GHC.Num.* ((GHC.Num.* (GHC.Types.I# 2#)) (GHC.Types.I# 3#))) (GHC.Types.I# 4#))

g = (\x'4 -> (\y -> ((GHC.Num.* x'4) y)))
