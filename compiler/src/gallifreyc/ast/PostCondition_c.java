package gallifreyc.ast;

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
        w.write(this.toString() + ";");
    }
    
    @Override
    public Node visitChildren(NodeVisitor v) {
        Expr expr = visitChild(this.cond, v);
        PostCondition_c n = copyIfNeeded(this);
        n.cond = expr;
        return n;
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        //TODO 
    	return this;
    }
}
