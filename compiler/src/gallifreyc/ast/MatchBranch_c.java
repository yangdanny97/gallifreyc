package gallifreyc.ast;

import polyglot.ast.AbstractBlock_c;
import polyglot.ast.*;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import java.util.*;

import polyglot.ast.LocalDecl;

public class MatchBranch_c extends AbstractBlock_c implements MatchBranch {
    private static final long serialVersionUID = SerialVersionUID.generate();
    private LocalDecl pattern;
    private Stmt stmt;

    public MatchBranch_c(Position pos, LocalDecl pattern, Stmt stmt) {
        super(pos, Collections.singletonList(stmt));
        assert pattern.init() == null;
        this.pattern = pattern;
        this.stmt = stmt;
    }

    // pushing scope and TC are handled by AbstractBlock_c

    public LocalDecl pattern() {
        return pattern;
    }

    public Stmt stmt() {
        return stmt;
    }

    @Override
    public String toString() {
        return "| " + pattern.toString() + " -> { " + stmt.toString() + "}";
    }

    @Override
    public Term firstChild() {
        if (pattern != null)
            return pattern;
        return null;
    }

    @Override
    public <T> List<T> acceptCFG(CFGBuilder<?> v, List<T> succs) {
        v.visitCFG(pattern, stmt, ENTRY);
        v.visitCFG(stmt, this, EXIT);
        return succs;
    }

    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter pp) {
        w.write("|");
        print(pattern, w, pp);
        w.write(" -> {");
        print(stmt, w, pp);
        w.write("}");
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        LocalDecl pattern = visitChild(this.pattern, v);
        Stmt stmt = visitChild(this.stmt, v);
        MatchBranch_c mb = (MatchBranch_c) this.copy();
        mb.pattern = pattern;
        mb.stmt = stmt;
        return mb;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
