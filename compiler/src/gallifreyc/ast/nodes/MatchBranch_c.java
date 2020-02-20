package gallifreyc.ast.nodes;

import polyglot.ast.Node_c;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.util.Position;
import polyglot.ast.LocalDecl;

public class MatchBranch_c extends Node_c implements MatchBranch {
	private LocalDecl pattern;
	private Stmt stmt;

	public MatchBranch_c(Position pos, LocalDecl pattern, Stmt stmt) {
		super(pos);
		this.pattern = pattern;
		this.stmt = stmt;
	}
	
	public LocalDecl pattern() {
		return pattern;
	}
	
	public Stmt stmt() {
		return stmt;
	}
	
}
