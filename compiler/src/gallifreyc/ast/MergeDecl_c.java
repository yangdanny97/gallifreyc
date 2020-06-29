package gallifreyc.ast;

import polyglot.ast.*;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.Flags;
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
    
    public MergeDecl_c(Position pos, Id method1, List<Formal> method1Formals, 
            Id method2, List<Formal> method2Formals, Block body) {
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
        if (ts.getMergeDecls(restriction) != null && ts.getMergeDecls(restriction).contains(this)) {
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
        if (ct.methodsNamed(method1.id()).size() == 0) {
            throw new SemanticException(
                    "Unable to find method named " + method1.id() + " in " + this.currentRestrictionClass, this.position);
        }
        if (ct.methodsNamed(method2.id()).size() == 0) {
            throw new SemanticException(
                    "Unable to find method named " + method2.id() + " in " + this.currentRestrictionClass, this.position);
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
}
