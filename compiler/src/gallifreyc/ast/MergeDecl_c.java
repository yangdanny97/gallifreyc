package gallifreyc.ast;

import polyglot.ast.*;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;

import java.util.*;

public class MergeDecl_c extends Node_c implements MergeDecl {
    private static final long serialVersionUID = SerialVersionUID.generate();
    
    Id method1;
    List<Formal> method1Formals;
    
    Id method2;
    List<Formal> method2Formals;
    
    Block body;
    
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
}
