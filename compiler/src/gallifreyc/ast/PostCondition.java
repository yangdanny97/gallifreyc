package gallifreyc.ast;

import polyglot.ast.*;

// post-condition declaration to augment interface method declarations
// unused
public interface PostCondition extends Node {
    Expr cond();
}
