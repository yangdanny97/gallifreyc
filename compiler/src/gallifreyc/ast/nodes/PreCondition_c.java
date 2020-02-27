package gallifreyc.ast.nodes;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Node_c;
import polyglot.types.SemanticException;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeChecker;

public class PreCondition_c extends Node_c implements PreCondition {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected Expr cond;

    public PreCondition_c(Position pos, Expr e) {
        super(pos);
        this.cond = e;
    }

    @Override
    public String toString() {
        return "requires " + cond.toString();
    }

    @Override
    public Expr cond() {
        return cond;
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
