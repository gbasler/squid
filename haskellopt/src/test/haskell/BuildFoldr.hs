{-# LANGUAGE RankNTypes #-}

module BuildFoldr where

foldr' :: (a -> b -> b) -> b -> [a] -> b
foldr' c n []     = n
foldr' c n (x:xs) = c x (foldr' c n xs)

build :: (forall b. (a -> b -> b) -> b -> b) -> [a]
build g = g (:) []



