package gallifreyc.ast.nodes;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import java.util.List;

public interface MatchRestriction extends Stmt {
	public Expr expr();
	public List<MatchBranch> branches();
}
