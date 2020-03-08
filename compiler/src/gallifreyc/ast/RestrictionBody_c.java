package gallifreyc.ast;

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

import java.util.List;

public class RestrictionBody_c extends Node_c implements RestrictionBody {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected List<Node> members;

    public RestrictionBody_c(Position pos, List<Node> members) {
        super(pos);
        this.members = members;
    }

    @Override
    public String toString() {
        return "{...}";
    }


    public List<Node> members() {
        return members;
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
