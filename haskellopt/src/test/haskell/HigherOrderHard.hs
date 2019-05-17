module HigherOrderHard where

-- FIXME scopes!

g f x = id (f (id x))
gTest0 = g (+1)
gTest1 y = g (+y)

g' f x = id (f (id x))
g'Test0 = g' (+1)

g'' f x = id (f (id x))
g''Test1 y = g (+y)

