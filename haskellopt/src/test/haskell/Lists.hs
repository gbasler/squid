module Lists where

ls0 = [1,2,3,4]
ls1 = map (+1) ls0

main = do
    print $ sum ls1
