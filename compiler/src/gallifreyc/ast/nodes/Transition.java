package gallifreyc.ast.nodes;

import polyglot.ast.Node;
import polyglot.ast.Expr;

public interface Transition extends Node {
	public Expr expr();
	public RestrictionId restriction();
}
