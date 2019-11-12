module BasicRec where

------ Top-level recursion ------

trec_0 :: Int -> [Int]
trec_0 y = y : (trec_0 (y + 1))

trec_0_0 = trec_0 27 ++ trec_0 32
trec_0_1 = trec_0 (head (trec_0 32))

trec_1 :: Int -> [Int]
trec_1 x =
  let f y = y : (f (y + 1))
  in f 0 ++ f x

-- Recursive top-level value
alternateTF = True : False : alternateTF
alternateTF'0 = take 5 alternateTF


------ Nested recursion ------

-- TODO usages

--- One-shot recursion

nrec_0 :: Int -> [Int]
nrec_0 x =
  let f y = y : (f (y + 1))
  -- let f y = y : (f y)
  in f x

--- Basic recursion

nrec_1 :: Int -> [Int]
nrec_1 x =
  let f y = y : (f (y + 1))
  in f 0 ++ f x

--- Recursion with capture

nrec_capt_0 :: Int -> [Int]
nrec_capt_0 x =
  let f y = y : (f (y + x))
  in f 0 ++ f x

--- Recursion with more arguments

alternateZO_0 x y = go where go = x : alternateZO_0 y x
alternateZO_0'0 = take 5 (alternateZO_0 0 1)

alternateZO_1 x y = go where go = x : y : go
alternateZO_1'0 = take 5 (alternateZO_1 0 1)

alternate123_0 x y z = x : alternate123_0 y z x
alternate123_0'0 = take 10 (alternate123_0 1 2 3)

alternate123_1 x y z = go where go = x : y : z : go
alternate123_1'0 = take 10 (alternate123_1 1 2 3)

alternate123_2 x y z = go x where
  go x2 = x2 : y : go' z
  go' z2 = z2 : go x
alternate123_2'0 = take 10 (alternate123_2 1 2 3)

alternate123_3 x y z = go z where go z' = x : y : z : go z'
alternate123_3'0 = take 10 (alternate123_3 1 2 3)
