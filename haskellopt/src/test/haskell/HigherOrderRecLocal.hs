module HOR6 where


-- Minimized example of diverging scheduling
-- FIXME diverges cf {-rec-}
rec2 f = rec where
--   rec = f (\x -> rec x)
  rec = f rec
-- rec2_0 = rec2 id -- always worked; also creates an interesting program
rec2_0 = rec2 (\k -> id) -- used to diverge
-- rec2_1 = rec2 (\k -> k) -- diverges when one above also uncommented!
-- rec2_2 = rec2 (\k s -> s:k s)


