module ListFusion where

import GHC.Base

-- f0 :: [Int] -> [Int]
-- f0 ls = map (1+) (map (1+) (map (2*) ls))
-- f0 ls = map (1+) (map (2*) ls)
f0 = map (1+)
-- f0 g = map g

-- f1 = map (1+) . map (2*)
f1 ls = map (1+) (map (2*) ls)

f2 ls = map (1+) (map (2*) (map (3^) ls))

-- bat sf arg = let res = sf ((map (\c -> c + arg)) []) in (res * res + 1)
bat sf arg = sf ((map (\c -> c + arg)) [])
foo sf arg = ((bat sf arg) + (bat (\ls -> sf (map (\x -> x * 2) ls) )) (arg + 1))
-- foo sf arg = bat ( \ls -> sf ((map (\x -> x * 2) ls)) ) (arg + 1)
sumnats = foo sum


-- -- d_0 arg' sf' = (sf' (build (\c -> (\n -> (_1( c, (_2( arg' )), n, [] ))))))
-- d_0 arg' sf' = (sf' (build (\c -> (\n -> GHC.Base.foldr c n [] ))))

-- foo = (\sf'2 -> (d_3( sf'2 )))

-- d_3( sf'3 ) = (\arg'2 -> (((d_0 arg'2 sf'3 )) ))

-- -- sumnats = (d_3( ((GHC.Base.foldr (+)) 0) ))
-- sumnats = (d_3(\eta -> ((GHC.Base.foldr (+)) 0) eta ))

-- -- _1( c'2, f, n'2, xs ) = (((GHC.Base.foldr (((.) c'2) f)) n'2) xs)

-- -- _2( arg'3 ) = (\c'3 -> (((+) c'3) arg'3))

