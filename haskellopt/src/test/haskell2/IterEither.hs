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

simple1 = loop (\(rem,n) -> Right n) (10, 20)
simple3 = loop (\rem -> Left (rem - 1)) 10
simple5 = loop (\rem -> if rem > 0 then Left (rem - 1) else Right rem) 5
simple2 = loop (\(rem,n) -> Left (rem, n)) (10, 0)
simple8 = loop (\(rem,n) -> if False then Left (1, 2) else Right 3) (4, 5)
simple0 = loop (\(rem,n) -> if rem > 0 then Left (rem - 1, 42) else Right n) (10, 20)
simple9 = loop (\(rem,n) -> if n then Left (n, rem) else Right n) (False, True)
