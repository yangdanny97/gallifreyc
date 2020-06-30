package gallifreyc.ast;

import gallifreyc.types.GallifreyTypeSystem;
import gallifreyc.visit.GallifreyTypeBuilder;
import gallifreyc.visit.GallifreyTypeChecker;
import polyglot.ast.*;
import polyglot.types.ClassType;
import polyglot.types.MemberInstance;
import polyglot.types.SemanticException;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.util.SubtypeSet;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

public class AllowsStmt_c extends Node_c implements AllowsStmt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected Id id;
    protected Id contingent_id;
    protected ClassType currentRestrictionClass;
    protected boolean testOnly = false; // whether the allow is an "allow as test"

    public AllowsStmt_c(Position pos, Id id, Id contingent_id, boolean testOnly) {
        super(pos);
        this.id = id;
        this.contingent_id = contingent_id;
        this.testOnly = testOnly;
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

    public boolean testOnly() {
        return testOnly;
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
        if (testOnly) {
            ts.addAllowedTestMethod(gtb.currentRestriction, id.id());
        } else {
            ts.addAllowedMethod(gtb.currentRestriction, id.id());
        }
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
            throw new SemanticException(
                    "Unable to find method named " + id.id() + " in " + this.currentRestrictionClass, this.position);
        }
        return this;
    }

    // classmember methods

    @Override
    public MemberInstance memberInstance() {
        return null;
    }

    @Override
    public boolean reachable() {
        return true;
    }

    @Override
    public Term reachable(boolean reachable) {
        return this;
    }

    @Override
    public SubtypeSet exceptions() {
        return null;
    }

    @Override
    public Term exceptions(SubtypeSet exceptions) {
        return this;
    }
}
