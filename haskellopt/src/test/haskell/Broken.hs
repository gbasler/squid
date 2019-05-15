-- TODO fail more gracefully on these

data A = A

instance Show A where
  show _ = "OOOPS"

bla = show A
