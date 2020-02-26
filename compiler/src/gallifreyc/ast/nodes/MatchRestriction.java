package gallifreyc.ast.nodes;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import java.util.List;

public interface MatchRestriction extends Stmt {
	public Expr expr();
	public List<MatchBranch> branches();
}
