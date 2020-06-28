package gallifreyc.ast;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;

public interface WhenStmt extends Stmt {
    public WhenStmt body(Stmt b);
    
    public WhenStmt expr(Expr e);
    
    public Expr expr();
    
    public Stmt body();
}
