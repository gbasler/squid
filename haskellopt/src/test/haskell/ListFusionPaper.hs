module ListFusion where

values = [0..6660]
process = analyze sum
analyze sf offs = aux sf offs + aux (\ls -> sf (map (*2) ls)) (offs + 1)
aux sf offs = let res = sf (map (+offs) values) in res * res + 1
