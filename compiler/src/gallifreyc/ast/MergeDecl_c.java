package gallifreyc.ast;

import polyglot.ast.*;
import polyglot.main.Report;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

import java.util.*;

import gallifreyc.types.GallifreyTypeSystem;
import gallifreyc.visit.GallifreyTypeBuilder;
import gallifreyc.visit.GallifreyTypeChecker;

public class MergeDecl_c extends Node_c implements MergeDecl {
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
        List<Formal> f1 = new ArrayList<>();
        for (Formal f : this.method1Formals) {
            f1.add(visitChild(f, v));
        }
        List<Formal> f2 = new ArrayList<>();
        for (Formal f : this.method2Formals) {
            f2.add(visitChild(f, v));
        }
        this.method1Formals = f1;
        this.method2Formals = f2;
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
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        GallifreyTypeBuilder gtb = (GallifreyTypeBuilder) tb;
        GallifreyTypeSystem ts = (GallifreyTypeSystem) tb.typeSystem();
        String restriction = gtb.currentRestriction;
        if (ts.getMergeDecls(restriction) != null && ts.getMergeDecls(restriction).contains(this)) {
            throw new SemanticException("Merge function for these 2 methods has already been defined", this.position);
        }
        ts.addMergeDecl(restriction, this);
        
        this.mi = ts.methodInstance(this.position, null, null, ts.Int(), name(), null, null);
        
        return this;
    }
    
    @Override
    public Context enterScope(Context c) {
        return c.pushClass(null, this.currentRestrictionClass).pushCode(mi);
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
        return this;
    }
}
