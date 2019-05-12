module Lists where

ls0 = [1,2,3,4]
ls1 = map (+1) ls0
lol x y = x + y

main = do
    print $ sum ls1
