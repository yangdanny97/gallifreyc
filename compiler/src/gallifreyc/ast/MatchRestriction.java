package gallifreyc.ast;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import java.util.List;

public interface MatchRestriction extends Stmt {
	public Expr expr();
	public List<MatchBranch> branches();
	
	public MatchRestriction expr(Expr e);
	public MatchRestriction branches(List<MatchBranch> b);
}
