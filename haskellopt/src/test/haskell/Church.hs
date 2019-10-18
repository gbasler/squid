module Church where

-- _I = \x -> x

twoI = two _I --where
two = \x y -> x (x y)
_I = \x -> x



