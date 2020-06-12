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
    private Block body;

    public MatchBranch_c(Position pos, LocalDecl pattern, Block body) {
        super(pos, body.statements());
        assert pattern.init() == null;
        this.pattern = pattern;
        this.body = body;
    }

    // pushing scope and TC are handled by AbstractBlock_c

    public LocalDecl pattern() {
        return pattern;
    }

    public MatchBranch pattern(LocalDecl d) {
        pattern = d;
        return this;
    }

    public Block body() {
        return body;
    }

    @Override
    public String toString() {
        return "| " + pattern.toString() + " -> " + body.toString();
    }

    @Override
    public Term firstChild() {
        if (pattern != null)
            return pattern;
        return null;
    }

    @Override
    public <T> List<T> acceptCFG(CFGBuilder<?> v, List<T> succs) {
        v.visitCFG(pattern, body, ENTRY);
        v.visitCFG(body, this, EXIT);
        return succs;
    }

    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter pp) {
        w.write("|");
        print(pattern, w, pp);
        w.write(" -> ");
        print(body, w, pp);
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        LocalDecl pattern = visitChild(this.pattern, v);
        Block body = visitChild(this.body, v);
        this.pattern = pattern;
        this.body = body;
        return this;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
