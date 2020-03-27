package gallifreyc.ast;

import polyglot.ast.Node;
import polyglot.ast.Expr;
import polyglot.ast.Stmt;

public interface Transition extends Stmt {
	public Expr expr();
	public RestrictionId restriction();
	
	public Transition expr(Expr e);
}
