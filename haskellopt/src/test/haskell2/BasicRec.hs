module BasicRec where

------ Top-level recursion ------

-- TODO try

-- rec_0 :: Int -> [Int]
-- rec_0 y = y : (rec_0 (y + 1))

-- rec_0_0 = rec_0 27 ++ rec_0 32
-- rec_0_1 = rec_0 (head (rec_0 32))

-- rec_1 :: Int -> [Int]
-- rec_1 x =
--   let f y = y : (f (y + 1))
--   in f 0 ++ f x


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

