# GPCE'17 Examples

## Modular Approach to the planets-simulation case

Where the 'geometry' lib defines its own opts, and we apply them to our own planets-simulation lib

Note the peculiar organization of the code – Planet cannot be defined in the same project as the one where we optimize code that uses it (such as `gravityForce`)

Several sub-approach: 
 * (currently) everything online; optimize sqrt(x)*sqrt(x) _after the fact_
 * (todo) as in the paper: use phases to optimize sqrt before pow;
 or at least let a full tree be built, rewriting sqrt on-the-fly, before doing the pow opt as its own phase...

EDIT: actually no, this problem is solved; optimizing pow(sqrt,2) just works online along with everything else!



 
