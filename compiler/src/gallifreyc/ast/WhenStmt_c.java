package gallifreyc.ast;

import java.util.*;
import polyglot.ast.*;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.CFGBuilder;
import polyglot.visit.FlowGraph;
import polyglot.visit.NodeVisitor;

public class WhenStmt_c extends Stmt_c implements WhenStmt {
    private static final long serialVersionUID = SerialVersionUID.generate();
    
    Expr expr;
    Stmt body;

    public WhenStmt_c(Position pos, Expr e, Stmt b) {
        super(pos);
        this.expr = e;
        this.body = b;
    }

    @Override
    public Term firstChild() {
        if (expr != null)
            return expr;
        return null;
    }

    @Override
    public <T> List<T> acceptCFG(CFGBuilder<?> v, List<T> succs) {
        v.visitCFG(expr, FlowGraph.EDGE_KEY_OTHER, body, ENTRY);
        v = v.push(this);
        v.visitCFG(body, this, EXIT); 
        return succs;
    }
    
    public WhenStmt body(Stmt b) {
        this.body = b;
        return this;
    }
    
    public WhenStmt expr(Expr e) {
        this.expr = e;
        return this;
    }
    
    public Expr expr() {
        return expr;
    }
    
    public Stmt body() {
        return body;
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        this.expr = visitChild(this.expr, v);
        this.body = visitChild(this.body, v);
        return this;
    }
}
