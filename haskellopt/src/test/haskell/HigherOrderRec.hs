-- module HigherOrderRec where
module Main where

import System.Exit

main =
  if rec7Test2 == [1,1,1]
  then exitSuccess else exitFailure


rec0 f = f (\x -> rec0 f x)

-- FIXME why two args in the worker?
rec1 f = (\x -> f (rec1 f x))

rec2 f = (\x -> x : f (rec2 f x))

rec3 f x y = f x : (rec3 f (f y) x)

-- FIXME breaks:
-- a = rec0 (\k s -> s + 1)
-- b = rec0 (\k s -> s * 2)


-- TODO direct unguarded recursion not supported yet
--rec4 x = case (rec4 x, rec4 x, rec4 x) of { (a,_,_) -> a }

-- rec4 x () = case (rec4 x (), rec4 x (), rec4 x ()) of { (a,_,_) -> a }
-- -- ^ in the Graph, equivalent to just `rec4 x y = case y of () -> rec4 x ()` because GHC removes the tuple intro/elim during desugaring and converts the recursive calls to let bindings (two of which are unused)

-- rec5 = rec5 + rec5
-- rec6 f = f (rec6 f) + f (rec6 f)
-- rec7 f x = let sx = x + 1 in f (rec7 f sx) * f (rec7 f sx) -- x not used!
-- rec7 f () = f (rec7 f ()) * f (rec7 f ())
-- rec7 f () = f (rec7 f ()) * 2
rec7 f () = f (rec7 f ()) -- simplest example of branch duplicity
-- rec7 f  = f (rec7 f ) -- no branch duplicity
-- rec7 f () = rec7 f () * rec7 f ()
-- rec8 f x = let sx = x + 1 in sx - f (rec8 f sx) * f (rec8 f sx) -- FIXME should let-bind sx in generated program

rec7Test0 = rec7 (1+)
rec7Test1 = rec7 (*2)
rec7Test2 = take 3 $ rec7 (1:) ()



-- FIXME
-- i f x = (f (i f x))
-- iTest0 = i (+1)
-- iTest1 y = i (+y)


