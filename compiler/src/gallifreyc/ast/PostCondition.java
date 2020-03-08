package gallifreyc.ast;

import polyglot.ast.*;

public interface PostCondition extends Node {
    Expr cond();
}
