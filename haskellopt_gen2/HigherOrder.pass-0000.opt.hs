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

hTest5 = (let { sh = (GHC.Types.I# 3#); sh'3 = (GHC.Num.- sh); sh'2 = (GHC.Num.- sh'); sh' = (GHC.Types.I# 2#) } in ((GHC.Num.* ((GHC.Num.* sh') ((GHC.Num.* (sh'2 sh')) (sh'2 sh)))) ((GHC.Num.* sh) ((GHC.Num.* (sh'3 sh')) (sh'3 sh)))))

hTest4 = (let { sh'4 = (GHC.Types.I# 3#); sh'5 = (GHC.Types.I# 2#); sh'6 = (GHC.Types.I# 1#) } in ((GHC.Num.- ((GHC.Num.* ((GHC.Num.+ sh'5) sh'6)) ((GHC.Num.+ sh'4) sh'6))) ((GHC.Num.* ((GHC.Num.* sh'5) sh'5)) ((GHC.Num.* sh'4) sh'5))))

hTest3 = (let { sh'7 = (GHC.Types.I# 3#); sh'10 = (GHC.Num.* sh'9); sh'8 = (GHC.Num.+ (GHC.Types.I# 1#)); sh'9 = (GHC.Types.I# 2#) } in ((GHC.Num.- ((GHC.Num.* (sh'8 sh'9)) (sh'8 sh'7))) ((GHC.Num.* (sh'10 sh'9)) (sh'10 sh'7))))

h = (\f -> ((GHC.Num.* (f (GHC.Types.I# 2#))) (f (GHC.Types.I# 3#))))

gTest1 = (let { sh'11 = (GHC.Types.I# 1#); sh'12 = ((GHC.Num.- (GHC.Types.I# 3#)) sh'11) } in ((GHC.Num.+ ((GHC.Num.- ((GHC.Num.+ ((GHC.Num.- (GHC.Types.I# 4#)) sh'11)) sh'12)) sh'11)) sh'12))

gTest0 = (let sh'13 = (GHC.Types.I# 1#) in ((GHC.Num.+ ((GHC.Num.- (GHC.Types.I# 2#)) sh'13)) ((GHC.Num.- (GHC.Types.I# 3#)) sh'13)))

g = (\f' -> (\x -> ((GHC.Num.+ (f' x)) (f' (GHC.Types.I# 3#)))))

m1 = (\x' -> ((GHC.Num.- x') (GHC.Types.I# 1#)))

iTest2 = (let { sh'14 = (GHC.Types.I# 1#); sh'15 = (GHC.Types.I# 11#); sh'16 = (GHC.Types.I# 22#); sh'17 = (GHC.Types.I# 2#) } in ((GHC.Num.+ ((GHC.Num.+ ((GHC.Num.+ sh'15) sh'14)) ((GHC.Num.+ sh'16) sh'14))) ((GHC.Num.+ ((GHC.Num.* sh'15) sh'17)) ((GHC.Num.* sh'16) sh'17))))

iTest1 = (let { sh'18 = (GHC.Types.I# 1#); sh'19 = (GHC.Types.I# 11#); sh'20 = (GHC.Types.I# 22#); sh'21 = (GHC.Types.I# 2#) } in ((GHC.Num.+ ((GHC.Num.+ ((GHC.Num.+ sh'19) sh'18)) ((GHC.Num.+ sh'20) sh'18))) ((GHC.Num.+ ((GHC.Num.* sh'19) sh'21)) ((GHC.Num.* sh'20) sh'21))))

iTest0 = (\x'2 -> let { sh'22 = (GHC.Types.I# 11#); sh'24 = (GHC.Types.I# 22#); sh'25 = (GHC.Types.I# 2#); sh'23 = (GHC.Types.I# 1#) } in ((GHC.Num.+ ((GHC.Num.+ ((GHC.Num.+ sh'22) sh'23)) ((GHC.Num.+ sh'24) sh'23))) ((GHC.Num.+ ((GHC.Num.* sh'22) sh'25)) ((GHC.Num.* sh'24) sh'25))))

i = (\f'2 -> (\x'2 -> ((GHC.Num.+ (f'2 (\ds -> ((GHC.Num.+ ds) (GHC.Types.I# 1#))))) (f'2 (\ds' -> ((GHC.Num.* ds') (GHC.Types.I# 2#)))))))

f0 = (\f'3 -> ((GHC.Num.+ (f'3 (GHC.Types.I# 11#))) (f'3 (GHC.Types.I# 22#))))

ls1 = (GHC.Num.+ ((GHC.Num.+ 11) 22))

lol = (\x'3 -> (\y -> ((GHC.Num.+ x'3) y)))
