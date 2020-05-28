package gallifreyc.ast;

import gallifreyc.types.GallifreyTypeSystem;
import gallifreyc.visit.GallifreyTypeBuilder;
import gallifreyc.visit.GallifreyTypeChecker;
import polyglot.ast.*;
import polyglot.types.ClassType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

public class AllowsStmt_c extends Node_c implements AllowsStmt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected Id id;
    protected Id contingent_id;
    protected Type currentRestrictionClass;

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
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        GallifreyTypeBuilder gtb = (GallifreyTypeBuilder) tb;
        GallifreyTypeSystem ts = (GallifreyTypeSystem) tb.typeSystem();
        ts.addAllowedMethod(gtb.currentRestriction, id.id());
        return this;
    }
    
    

    @Override
    public NodeVisitor typeCheckEnter(TypeChecker tc) throws SemanticException {
        GallifreyTypeChecker gtc = (GallifreyTypeChecker) tc;
        this.currentRestrictionClass = gtc.currentRestrictionClass;
        return super.typeCheckEnter(tc);
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        ClassType ct = (ClassType) this.currentRestrictionClass;
        if (ct.methodsNamed(id.id()).size() == 0) {
            throw new SemanticException("Unable to find method named " + id.id() + " in " + this.currentRestrictionClass,
                    this.position);
        }
        return this;
    }
}
