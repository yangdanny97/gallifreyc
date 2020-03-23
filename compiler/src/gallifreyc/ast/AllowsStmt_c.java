package gallifreyc.ast;

import gallifreyc.types.GallifreyTypeSystem;
import gallifreyc.visit.GallifreyTypeChecker;
import polyglot.ast.*;
import polyglot.types.ClassType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeChecker;

public class AllowsStmt_c extends Node_c implements AllowsStmt {
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
    public void prettyPrint(CodeWriter w, PrettyPrinter pp) {
    	w.write(this.toString());
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        return this;
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        TypeSystem ts = tc.typeSystem();
        if (tc instanceof GallifreyTypeChecker) {
        	GallifreyTypeChecker gtc = (GallifreyTypeChecker) tc;
        	Type t = ts.typeForName(gtc.currentRestrictionClass);
            if (!(t instanceof ClassType)) {
            	throw new SemanticException("Restriction "+ gtc.currentRestrictionClass +" must be for a valid class", this.position);
            }
            ClassType ct = (ClassType) t;
            if (ct.methodsNamed(id.id()).size() == 0) {
            	throw new SemanticException("Unable to find method named " + id.id() + " in " + gtc.currentRestrictionClass, this.position);
            }
        }
    	return this;
    }
}
