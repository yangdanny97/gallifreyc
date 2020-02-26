package gallifreyc.ast.nodes;

import java.util.Collections;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Stmt_c;
import polyglot.ast.Term;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;

public class MatchRestriction_c extends Stmt_c implements MatchRestriction {
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
	
    @Override
    public Term firstChild() {
        if (expr != null) return expr;
        return null;
    }
    
    @Override
    public <T> List<T> acceptCFG(CFGBuilder<?> v, List<T> succs) {
        if (expr != null) {
            v.visitCFG(expr, this, EXIT);
        }

        //TODO v.visitMatchRestriction(this);
        return Collections.<T> emptyList();
    }
    
    //TODO visitChildren
    //TODO typeCheck
    //TODO reconstruct and associated fns
    //TODO copy
}
