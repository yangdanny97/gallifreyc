package gallifreyc.ast.nodes;

import polyglot.ast.Node;
import polyglot.ast.Expr;
import polyglot.ast.Stmt;

public interface Transition extends Stmt {
	public Expr expr();
	public RestrictionId restriction();
}
