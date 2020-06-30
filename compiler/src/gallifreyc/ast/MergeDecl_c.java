package gallifreyc.ast;

import polyglot.ast.*;
import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.MemberInstance;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

import java.util.*;

import gallifreyc.types.GallifreyTypeSystem;
import gallifreyc.visit.GallifreyTypeBuilder;
import gallifreyc.visit.GallifreyTypeChecker;

public class MergeDecl_c extends Term_c implements MergeDecl {
    private static final long serialVersionUID = SerialVersionUID.generate();

    Id method1;
    List<Formal> method1Formals;

    Id method2;
    List<Formal> method2Formals;

    Block body;

    MethodInstance mi; // just to hold the return type

    protected ClassType currentRestrictionClass;
    String currentRestriction;

    public MergeDecl_c(Position pos, Id method1, List<Formal> method1Formals, Id method2, List<Formal> method2Formals,
            Block body) {
        super(pos);
        this.method1 = method1;
        this.method2 = method2;
        this.method1Formals = method1Formals;
        this.method2Formals = method2Formals;
        this.body = body;
    }

    public Id method1() {
        return method1;
    }

    public MergeDecl method1(Id method1) {
        this.method1 = method1;
        return this;
    }

    public List<Formal> method1Formals() {
        return method1Formals;
    }

    public MergeDecl method1Formals(List<Formal> method1Formals) {
        this.method1Formals = method1Formals;
        return this;
    }

    public Id method2() {
        return method2;
    }

    public MergeDecl method2(Id method2) {
        this.method2 = method2;
        return this;
    }

    public List<Formal> method2Formals() {
        return method2Formals;
    }

    public MergeDecl method2Formals(List<Formal> method2Formals) {
        this.method2Formals = method2Formals;
        return this;
    }

    public Block body() {
        return body;
    }

    public MergeDecl body(Block body) {
        this.body = body;
        return this;
    }

    public String name() {
        return method1.id() + " " + method2.id();
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        this.method1Formals = visitList(this.method1Formals, v);
        this.method2Formals = visitList(this.method2Formals, v);
        this.body = visitChild(this.body, v);
        return this;
    }

    @Override
    public int hashCode() {
        return name().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other.hashCode() == this.hashCode();
    }

    @Override
    public NodeVisitor typeCheckEnter(TypeChecker tc) throws SemanticException {
        GallifreyTypeChecker gtc = (GallifreyTypeChecker) tc;
        this.currentRestrictionClass = gtc.currentRestrictionClass;
        return super.typeCheckEnter(tc);
    }

    @Override
    public NodeVisitor buildTypesEnter(TypeBuilder tb) throws SemanticException {
        return tb.pushCode();
    }

    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        GallifreyTypeBuilder gtb = (GallifreyTypeBuilder) tb;
        GallifreyTypeSystem ts = (GallifreyTypeSystem) tb.typeSystem();

        String restriction = gtb.currentRestriction;
        this.currentRestriction = restriction;

        if (ts.getMergeDecls(restriction).contains(this)) {
            throw new SemanticException("Merge function for these 2 methods has already been defined", this.position);
        }
        ts.addMergeDecl(restriction, this);
        List<Type> params = new ArrayList<Type>();

        this.mi = ts.methodInstance(this.position, null, Flags.NONE, ts.Int(), name(), params, new ArrayList<Type>());

        return this;
    }

    @Override
    public Context enterScope(Context c) {
        return c.pushCode(mi);
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        ClassType ct = this.currentRestrictionClass;
        GallifreyTypeSystem ts = (GallifreyTypeSystem) tc.typeSystem();
        List<MethodInstance> mi1 = new ArrayList<>();
        mi1.addAll(ct.methodsNamed(method1.id()));
        List<MethodInstance> mi2 = new ArrayList<>();
        mi2.addAll(ct.methodsNamed(method2.id()));
        if (mi1.size() == 0) {
            throw new SemanticException(
                    "Unable to find method named " + method1.id() + " in " + this.currentRestrictionClass,
                    this.position);
        }
        if (mi2.size() == 0) {
            throw new SemanticException(
                    "Unable to find method named " + method2.id() + " in " + this.currentRestrictionClass,
                    this.position);
        }
        if (!ts.getAllowedMethods(this.currentRestriction).contains(method1.id())) {
            throw new SemanticException(method1.id() + " not allowed in " + this.currentRestriction, this.position);
        }
        if (!ts.getAllowedMethods(this.currentRestriction).contains(method2.id())) {
            throw new SemanticException(method2.id() + " not allowed in " + this.currentRestriction, this.position);
        }
        // TODO: check types
        return this;
    }

    @Override
    public Term firstChild() {
        return listChild(method1Formals, listChild(method2Formals, body));
    }

    @Override
    public <T> List<T> acceptCFG(CFGBuilder<?> v, List<T> succs) {
        List<Formal> formals = new ArrayList<>();
        formals.addAll(method1Formals);
        formals.addAll(method2Formals);
        v.visitCFGList(formals, body(), ENTRY);
        v.visitCFG(body(), this, EXIT);
        return succs;
    }

    // for dataflow analysis

    @Override
    public Term codeBody() {
        return body;
    }

    @Override
    public CodeInstance codeInstance() {
        return mi;
    }

    @Override
    public MemberInstance memberInstance() {
        return mi;
    }
}
