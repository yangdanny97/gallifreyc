package gallifreyc.ast;

import java.util.List;

import polyglot.ast.*;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;

public class Transition_c extends Stmt_c implements Transition {
    private static final long serialVersionUID = SerialVersionUID.generate();
    private Expr expr;
    private RestrictionId restriction;

    public Transition_c(Position pos, Expr expr, RestrictionId restriction) {
        super(pos);
        this.expr = expr;
        this.restriction = restriction;
    }

    public Expr expr() {
        return expr;
    }

    public Transition expr(Expr e) {
        this.expr = e;
        return this;
    }

    public RestrictionId restriction() {
        return restriction;
    }

    @Override
    public Term firstChild() {
        if (expr != null)
            return expr;
        return null;
    }

    @Override
    public <T> List<T> acceptCFG(CFGBuilder<?> v, List<T> succs) {
        v.visitCFG(expr, this, EXIT);
        return succs;
    }

    @Override
    public String toString() {
        return "transition(" + expr.toString() + ", " + restriction.toString() + ");";
    }

    // by default, the translator relies on the pretty-printer
    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter pp) {
        w.write("transition(");
        expr.prettyPrint(w, pp);
        w.write(", " + restriction.toString() + ");");
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        Expr e = visitChild(this.expr, v);
        this.expr = e;
        return this;
    }
}
