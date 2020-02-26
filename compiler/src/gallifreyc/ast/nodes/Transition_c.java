package gallifreyc.ast.nodes;

import java.util.Collections;
import java.util.List;

import polyglot.ast.*;
import polyglot.types.SemanticException;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeChecker;

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
        v.visitCFG(expr, this, EXIT);
        return succs;
    }
    
    @Override
    public String toString() {
    	return "transition(" + expr.toString() + ", " + restriction.toString() + ")";
    }
    
    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        //TODO
    }

    @Override
    public void translate(CodeWriter w, Translator tr) {
        //TODO
    }
    
    @Override
    public Node visitChildren(NodeVisitor v) {
    	//TODO
        return null;
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        //TODO 
    	return null;
    }
}
