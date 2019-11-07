module Statistics where

maxMaybe0 [] = Nothing
maxMaybe0 (x : xs) = case maxMaybe0 xs of
  Just m -> Just (if x > m then x else m)
  Nothing -> Just x

maxMaybe1 [] = Nothing
maxMaybe1 (x : xs) = case maxMaybe1 xs of
  old @ (Just m) -> if x > m then Just x else old
  Nothing -> Just x

-- penultimate [] = Nothing
-- penultimate (x : xs) = case penultimate xs of
--   old @ (Just m) -> if x > m then Just x else old
--   Nothing -> Just x
