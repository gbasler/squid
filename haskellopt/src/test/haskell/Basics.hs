module Basics where

-- FIXME
--foo :: Int -> Int
--foo x =
--  let tmp = x * 2
--  in tmp + tmp


-- Nested calls

ncF :: Int -> Int
ncF x = x * x

ncFTest0 = ncF 11 + ncF 22
ncFTest1 = ncF (ncF 33)

ncG :: Int -> Int -> Int
ncG x y = x * y

ncGTest0 = ncG (ncG 2 3) 4
ncGTest1 = ncG 4 (ncG 2 3)
ncGTest2 = ncG (ncG 2 3) (ncG 4 5)






