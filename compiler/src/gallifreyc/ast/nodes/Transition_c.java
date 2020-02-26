package gallifreyc.ast.nodes;

import java.util.Collections;
import java.util.List;

import polyglot.ast.*;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;

public class Transition_c extends Stmt_c implements Transition {
	private static final long serialVersionUID = SerialVersionUID.generate();
	private Expr expr;
	private RestrictionId restriction;
	
	public Transition_c(Position pos, Expr expr, RestrictionId restriction) {
		super(pos);
		this.expr = expr;
		this.restriction = restriction;
	}
	
	public Expr expr() {
		return expr;
	}
	
	public RestrictionId restriction() {
		return restriction;
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

        //TODO v.visitTransition(this);
        return Collections.<T> emptyList();
    }
    
    //TODO visitChildren
    //TODO typeCheck
    //TODO reconstruct and associated fns
    //TODO copy
}
