module IterEither where

loop k x = go x where
  go x = case k x of
    Left a -> go a
    Right b -> b

-- TODO
-- nats0 = loop (\ns -> Left ((length ns) : ns)) []
-- nats1 = loop (\ns -> Left ((len ns) : ns)) [] where
--   len [] = 0
--   len (x: xs) = len xs + 1

count :: Int -> Int
count start = loop (\(rem,n) -> if rem > 0 then Left (rem - 1, n + 1) else Right n) (start, 0)
