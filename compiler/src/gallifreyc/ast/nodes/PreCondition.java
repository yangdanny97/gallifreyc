package gallifreyc.ast.nodes;

import polyglot.ast.Expr;
import polyglot.ast.Node;

public interface PreCondition extends Node {
    Expr cond();
}
