module HigherOrderRec where

import System.Exit

rec0  f = f (\x -> rec0 f x)
rec0_0 = rec0 (\k -> id) -- FIXME not fully reduced...

rec7 f () = f (rec7 f ()) -- simplest example of branch duplicity
rec7Test0 = rec7 (1+)
rec7Test1 = rec7 (*2)


-- Old minimized version:
-- rec0 f = f (\x -> rec0 f)
-- rec0_0 = rec0 (\k -> id) -- FIXME not fully reduced...
