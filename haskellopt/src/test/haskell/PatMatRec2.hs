module PatMatRec where


usum [] = 0
usum (x : xs) = x + usum xs
usum'0 = usum []
-- usum'1 = usum [1]
usum'2 = usum [1,2] -- FIXME? generates: usum'2 = (((GHC.Num.+) 1) _2); _2 = _2
-- ^ FIXME: when all 3 are uncommented, it causes too many graph iterations (likely mostly on dead branches)


