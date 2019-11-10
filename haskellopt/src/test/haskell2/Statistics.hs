module Statistics where

maxMaybe0 [] = Nothing
maxMaybe0 (x : xs) = case maxMaybe0 xs of
  Just m -> Just (if x > m then x else m)
  Nothing -> Just x

maxTest'0 = maxMaybe0 [1,2,3]

maxMaybe1 [] = Nothing
maxMaybe1 (x : xs) = case maxMaybe1 xs of
  old @ (Just m) -> if x > m then Just x else old
  Nothing -> Just x

-- penultimate [] = Nothing
-- penultimate (x : xs) = case penultimate xs of
--   old @ (Just m) -> if x > m then Just x else old
--   Nothing -> Just x

lastMaybe [] = Nothing
lastMaybe (x : []) = Just x
lastMaybe (x : xs) = lastMaybe xs

-- Another program that also exhibits wrong scheduling:
lastWeird [] = Nothing
lastWeird (x : []) = Just x
lastWeird (x : xs) = case lastWeird xs of { Just y -> Just y; Nothing -> Just 666 }
