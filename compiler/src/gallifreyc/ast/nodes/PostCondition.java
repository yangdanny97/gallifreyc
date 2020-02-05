package gallifreyc.ast.nodes;

import polyglot.ast.*;

public interface PostCondition extends Node {
    Expr cond();
}
