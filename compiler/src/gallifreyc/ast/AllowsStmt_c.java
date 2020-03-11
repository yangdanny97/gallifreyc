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

public class AllowsStmt_c extends RestrictionMember_c implements AllowsStmt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected Id id;
    protected Id contingent_id;

    public AllowsStmt_c(Position pos, Id id, Id contingent_id) {
        super(pos);
        this.id = id;
        this.contingent_id = contingent_id;
    }

    @Override
    public String toString() {
        String s = "allows " + id.toString();
        if (contingent_id != null) {
        	s = s + " contingent " + contingent_id.toString(); 
        }
        return s;
    }

    public Id id() {
        return id;
    }

    public Id contingent_id() {
        return contingent_id;
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
        return this;
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        //TODO 
    	return this;
    }
}
