module HigherOrderRecLocal where

foo f = rec where
  -- rec = f (\x -> rec x) -- note that this version fixes the divergence of foo_2
  rec = f rec
foo_0 = foo id
foo_1 = foo (\k -> id)
-- foo_2 = foo (\k -> k) -- FIXME scheduling diverges
foo_3 = foo (\k s -> s : k s)
foo_4 = foo (\k s -> s : k (s + 1))
foo_5 = foo (\k s -> s : (k (s + 1) ++ k (s * 2)))
-- foo_6 = foo (\k s -> s : (if s `mod` 2 == 0 then k (s + 1) else k (s * 2))) -- TODO pattern matching
