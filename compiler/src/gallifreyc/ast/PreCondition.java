package gallifreyc.ast;

import polyglot.ast.Expr;
import polyglot.ast.Node;

public interface PreCondition extends Node {
    Expr cond();
}
