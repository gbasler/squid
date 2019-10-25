-- Generated Haskell code from Graph optimizer
-- Core obtained from: The Glorious Glasgow Haskell Compilation System, version 8.6.3
-- Optimized after GHC phase:
--   desugar
-- Beta reductions:  58
-- Incl. one-shot:  0
-- Total nodes: 1274; Boxes: 384; Branches: 320
-- Apps: 257; Lams: 18

{-# LANGUAGE UnboxedTuples #-}
{-# LANGUAGE MagicHash #-}
{-# LANGUAGE NoMonomorphismRestriction  #-}

module HigherOrder (hTest5,hTest4,hTest3,h,gTest1,gTest0,g,m1,iTest2,iTest1,iTest0,i,f0,ls1,lol) where

import GHC.Num
import GHC.Types

hTest5 = (let { sh = (GHC.Num.- 3); sh' = (GHC.Num.- 2) } in ((GHC.Num.* ((GHC.Num.* 2) ((GHC.Num.* (sh' 2)) (sh' 3)))) ((GHC.Num.* 3) ((GHC.Num.* (sh 2)) (sh 3)))))

hTest4 = ((GHC.Num.- ((GHC.Num.* ((GHC.Num.+ 2) 1)) ((GHC.Num.+ 3) 1))) ((GHC.Num.* ((GHC.Num.* 2) 2)) ((GHC.Num.* 3) 2)))

hTest3 = (let { sh'2 = (GHC.Num.* 2); sh'3 = (GHC.Num.+ 1) } in ((GHC.Num.- ((GHC.Num.* (sh'3 2)) (sh'3 3))) ((GHC.Num.* (sh'2 2)) (sh'2 3))))

h = (\f -> ((GHC.Num.* (f 2)) (f 3)))

gTest1 = (let sh'4 = ((GHC.Num.- 3) 1) in ((GHC.Num.+ ((GHC.Num.- ((GHC.Num.+ ((GHC.Num.- 4) 1)) sh'4)) 1)) sh'4))

gTest0 = ((GHC.Num.+ ((GHC.Num.- 2) 1)) ((GHC.Num.- 3) 1))

g = (\f' -> (\x -> ((GHC.Num.+ (f' x)) (f' 3))))

m1 = (\x' -> ((GHC.Num.- x') 1))

iTest2 = ((GHC.Num.+ ((GHC.Num.+ ((GHC.Num.+ 11) 1)) ((GHC.Num.+ 22) 1))) ((GHC.Num.+ ((GHC.Num.* 11) 2)) ((GHC.Num.* 22) 2)))

iTest1 = ((GHC.Num.+ ((GHC.Num.+ ((GHC.Num.+ 11) 1)) ((GHC.Num.+ 22) 1))) ((GHC.Num.+ ((GHC.Num.* 11) 2)) ((GHC.Num.* 22) 2)))

iTest0 = (\x'2 -> ((GHC.Num.+ ((GHC.Num.+ ((GHC.Num.+ 11) 1)) ((GHC.Num.+ 22) 1))) ((GHC.Num.+ ((GHC.Num.* 11) 2)) ((GHC.Num.* 22) 2))))

i = (\f'2 -> (\x'2 -> ((GHC.Num.+ (f'2 (\ds -> ((GHC.Num.+ ds) 1)))) (f'2 (\ds' -> ((GHC.Num.* ds') 2))))))

f0 = (\f'3 -> ((GHC.Num.+ (f'3 11)) (f'3 22)))

ls1 = (GHC.Num.+ ((GHC.Num.+ 11) 22))

lol = (\x'3 -> (\y -> ((GHC.Num.+ x'3) y)))
