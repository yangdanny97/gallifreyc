package gallifreyc.ast.nodes;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import java.util.List;

public interface MatchRestriction extends Node {
	public Expr expr();
	public List<MatchBranch> branches();
}
