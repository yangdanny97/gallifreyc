package gallifreyc.ast.nodes;

import polyglot.ast.*;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class Transition_c extends Node_c implements Transition {
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
}
