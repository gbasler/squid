module Church where

-- trying to compare number of beta reductions with https://link.springer.com/content/pdf/10.1007%2F978-3-540-25979-4_11.pdf
-- also reproducing them in online demo of https://github.com/codedot/lambda
    -- I = x: x;
    -- _2 = s: z: s (s z);
    -- _2 two two I I

_2I = _2 _I where
  _I = \x -> x
  _2 = \x -> \y -> x (x y)

-- test = _2 _I _I where -- okay
test = _2 _2 _I _I where -- nonterm
-- test = _2 _2 _2 _I _I where -- nonterm
  _I = \x -> x
  _2 s z = s (s z)
