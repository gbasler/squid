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

