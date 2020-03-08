package gallifreyc.ast;

import polyglot.ast.*;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeChecker;

public class RestrictionDecl_c extends Node_c implements RestrictionDecl {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected Id id;
    protected Id for_id;
    protected RestrictionBody body;

    public RestrictionDecl_c(Position pos, Id id, Id for_id, RestrictionBody body) {
        super(pos);
        this.id = id;
        this.for_id = for_id;
        this.body = body;
    }

    @Override
    public String toString() {
        return "restriction " + id.toString() + " for " + for_id.toString();
    }


    public Id id() {
        return id;
    }

    public Id for_id() {
        return for_id;
    }

    public RestrictionBody body() {
        return body;
    }
    
    /** From TopLevelDecl */
    public Flags flags() {
    	return null; //TODO
    }

    public String name() {
    	return id.id();
    }
    
    public Documentable javadoc(Javadoc javadoc) {
    	return null; //TODO
    }

    public Javadoc javadoc() {
    	return null; //TODO
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
