module HigherOrderp where

only_q f x y = f x : (q f (f y) x)

q f x y = f x : (q f (f y) x)
-- q_0 = q (+1) -- FIXME graph diverges
q_1 = q (1+)
-- q_2 = q (\x -> q (1+)) -- FIXME graph diverges
-- q_3 = q (\x -> sum (q (1+) x x)) -- FIXME graph diverges
-- q_4 = q (\x -> sum (q (x+) x x)) -- FIXME graph diverges

-- TODO pattern matching: replace `unit` by `()`
r f unit = f (r f ())
r_0 = r (1+)
-- r_1 = r (*2)  -- FIXME graph diverges
r_2 = take 3 $ r (1:) ()

s f x = let sx = x + 1 in sx - f (s f sx) * f (s f sx) -- should let-bind sx in generated program
