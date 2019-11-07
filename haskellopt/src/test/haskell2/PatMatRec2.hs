module PatMatRec where

usum [] = 0
usum (x : xs) = x + usum xs
usum'0 = usum []
usum'1 = usum [1]
usum'2 = usum [1,2]

oops xs [] = oops xs xs
oops xs (y : ys) = y + oops xs ys
oops'0 = oops [] []

alternate (x, y) = x : alternate (y, x)
alternate'0 = take 5 (alternate (True, False))
