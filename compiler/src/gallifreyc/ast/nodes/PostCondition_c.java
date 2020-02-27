package gallifreyc.ast.nodes;

import polyglot.ast.*;
import polyglot.types.SemanticException;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeChecker;

public class PostCondition_c extends Node_c implements PostCondition {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected Expr cond;

    public PostCondition_c(Position pos, Expr e) {
        super(pos);
        this.cond = e;
    }

    @Override
    public String toString() {
        return "ensures " + cond.toString();
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
