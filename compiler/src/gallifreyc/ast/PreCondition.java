package gallifreyc.ast;

import polyglot.ast.Expr;
import polyglot.ast.Node;

//pre-condition declaration to augment interface method declarations
//unused
public interface PreCondition extends Node {
    Expr cond();
}
