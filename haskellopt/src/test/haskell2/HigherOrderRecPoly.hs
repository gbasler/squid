module HigherOrderp where

-- Most of the defs below generate code that needs polymorphic recursion (occurs-check fails)

only_p0 f = f (\x -> only_p0 f x)
only_p1 f = f (\x -> only_p1 (id f) x)
only_p2 f = f (only_p2 (id f))

p1 f = f (\x -> p1 (id f) (x + 1))
p1_1 = p1 id
p1_2 = p1 (\h -> h)
p1_3 = p1 (\h x -> x)
p1_4 = p1 (\h x -> h x)
p1_5 = p1 (\h x -> h (x + 1) * h (x - 1))
