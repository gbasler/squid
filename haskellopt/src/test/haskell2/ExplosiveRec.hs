module ExplosiveRec where

-- a0 x = x : a0 0 ++  a0 1
a0 x = x : a0 0 ++  a0 1 ++  a0 2
-- a0 x = x : a0 0 ++  a0 1 ++  a0 2 ++  a0 3
