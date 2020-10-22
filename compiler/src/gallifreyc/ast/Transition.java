package gallifreyc.ast;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;

// allows transitioning restrictions for shared objects
public interface Transition extends Stmt {
    public Expr expr();

    public RestrictionId restriction();

    public Transition expr(Expr e);
}
