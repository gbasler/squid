module HigherOrderHard where

-- FIXME scopes!

hoG f x = id (f (id x))
hoGTest0 = hoG (+1)
hoGTest1 y = hoG (+y)

hoG' f x = id (f (id x))
hoG'Test0 = hoG' (+1)

hoG'' f x = id (f (id x))
hoG''Test1 y = hoG (+y)

