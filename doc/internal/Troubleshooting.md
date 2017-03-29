# Troubleshooting

## Sources of subtle bugs

### MethodApp return types

Node `MethodApp` in `AST`/`SimpleANF` stores a return type
not displayed by default.

Also, transfo can leave methodapps weirdly typed  
For example, a redex like `(f->Int=>Int).apply(n->Int)->Int` (where `->` shows the return type of an expr)
may be (soundly) transformed later to `(f->Int=>Nothing).apply(n->Int)->Int`,
only now we have a repr that would not normally come up had we written the term directly
-- we would have got `(f->Int=>Nothing).apply(n->Int)->Nothing`  
Possible solution: insert ascriptions when type of term is refined by RwR...



## Speculative Rewriting Won't Apply

It helps to debug by printing something on abort:  
`x subs 'a -> { println("... $x ...") Abort() }`

typical causes for blocking rewritings include:
 * default arguments! a default argument creates a special value with a mangled name, 
 which can prevent a data structure from being inlined...
 * [TODO]



