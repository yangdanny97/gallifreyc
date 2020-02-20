package gallifreyc.ast.nodes;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node_c;
import polyglot.util.Position;

public class MatchRestriction_c extends Node_c implements MatchRestriction {
	private Expr expr;
	private List<MatchBranch> branches;

	public MatchRestriction_c(Position pos, Expr expr, List<MatchBranch> branches) {
		super(pos);
		this.expr = expr;
		this.branches = branches;
	}

	@Override
	public Expr expr() {
		return expr;
	}

	@Override
	public List<MatchBranch> branches() {
		return branches;
	}
}
