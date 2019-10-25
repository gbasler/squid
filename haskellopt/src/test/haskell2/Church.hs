module Church where

_I = \x -> x

zero s z = z
one s z = s z
two s z = s (s z)
three s z = s (s (s z))

two_id = two _I

three_id = two _I

plus m n s z = m s (n s z)

mult m n = m (plus n) zero

two_x_three = mult two three
two_x_zero = mult two zero

two_p_three = plus two three

zero_x_three = mult zero three

