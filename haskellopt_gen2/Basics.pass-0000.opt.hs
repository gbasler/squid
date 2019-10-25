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

foo = (\x -> let sh = ((GHC.Num.* x) 2) in ((GHC.Num.+ sh) sh))

foo' = (\x' -> ((GHC.Num.* ((GHC.Num.+ x') 1)) (GHC.Num.fromInteger 2)))

foo'' = (\x'2 -> ((GHC.Num.* ((GHC.Num.+ x'2) 1)) x'2))

fTest4 = (let { sh' = ((GHC.Num.* 66) 66); sh'2 = ((GHC.Num.* 77) 77) } in ((GHC.Num.+ ((GHC.Num.* sh') sh')) ((GHC.Num.* sh'2) sh'2)))

fTest3 = (let { sh'3 = ((GHC.Num.* 66) 66); sh'4 = ((GHC.Num.* 77) 77) } in ((GHC.Num.* ((GHC.Num.* sh'3) sh'3)) ((GHC.Num.* sh'4) sh'4)))

fTest2 = (let sh'5 = ((GHC.Num.* 55) 55) in ((GHC.Num.+ ((GHC.Num.* 44) 44)) ((GHC.Num.* sh'5) sh'5)))

fTest1 = (let sh'6 = ((GHC.Num.* 33) 33) in ((GHC.Num.* sh'6) sh'6))

fTest0 = ((GHC.Num.* ((GHC.Num.* 11) 11)) ((GHC.Num.* 22) 22))

f = (\x'3 -> ((GHC.Num.* x'3) x'3))

gTest6 = ((GHC.Num.* ((GHC.Num.* 44) 33)) 11)

gTest5 = (let sh'7 = (GHC.Num.* 11) in ((GHC.Num.+ ((GHC.Num.* (sh'7 30)) ((GHC.Num.* 30) 22))) ((GHC.Num.* (sh'7 40)) ((GHC.Num.* 40) 22))))

gTest4 = ((GHC.Num.* ((GHC.Num.* 2) 3)) ((GHC.Num.* 4) 5))

gTest3 = ((GHC.Num.* ((GHC.Num.* 2) 3)) 4)

gTest2 = (\y -> ((GHC.Num.* ((GHC.Num.* 2) 3)) y))

gTest1 = ((GHC.Num.* 4) ((GHC.Num.* 2) 3))

gTest0 = ((GHC.Num.* ((GHC.Num.* 2) 3)) 4)

g = (\x'4 -> (\y -> ((GHC.Num.* x'4) y)))
