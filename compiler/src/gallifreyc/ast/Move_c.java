package gallifreyc.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.types.SemanticException;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeChecker;

public class Move_c extends Expr_c implements Move {
	Expr expr;

	public Move_c(Position pos, Expr expr) {
		super(pos);
		this.expr = expr;
	}
	
	public Expr expr() {
		return expr;
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
    	return "move(" + expr.toString() + ")";
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
