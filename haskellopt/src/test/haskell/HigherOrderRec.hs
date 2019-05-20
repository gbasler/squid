module HigherOrderRec where

rec0 f = f (\x -> rec0 f x)

-- FIXME why two args in the worker?
rec1 f = (\x -> f (rec1 f x))

rec2 f = (\x -> x : f (rec2 f x))

rec3 f x y = f x : (rec3 f (f y) x)

-- FIXME breaks:
-- a = rec0 (\k s -> s + 1)
-- b = rec0 (\k s -> s * 2)


-- FIXME
-- i f x = (f (i f x))
-- iTest0 = i (+1)
-- iTest1 y = i (+y)


