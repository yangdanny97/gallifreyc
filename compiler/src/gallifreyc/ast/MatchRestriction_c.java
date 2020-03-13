package gallifreyc.ast;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt_c;
import polyglot.ast.Term;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.FlowGraph;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeChecker;

public class MatchRestriction_c extends Stmt_c implements MatchRestriction {
	private Expr expr;
	private List<MatchBranch> branches;

	public MatchRestriction_c(Position pos, Expr expr, List<MatchBranch> branches) {
		super(pos);
		this.expr = expr;
		this.branches = branches;
	}

	@Override
	public Expr expr() {
		return expr;
	}

	@Override
	public List<MatchBranch> branches() {
		return branches;
	}
	
    @Override
    public Term firstChild() {
        if (expr != null) return expr;
        return null;
    }
    
    @Override
    public <T> List<T> acceptCFG(CFGBuilder<?> v, List<T> succs) {
    	List<Term> t_branches = new ArrayList<>();
    	List<Integer> entry = new ArrayList<>();
    	for (MatchBranch b : branches()) {
    		t_branches.add(b);
    		entry.add(new Integer(ENTRY));
    	}
    	t_branches.add(this);
    	entry.add(EXIT);
        v.visitCFG(expr, FlowGraph.EDGE_KEY_OTHER, t_branches, entry);
        v.push(this).visitCFGList(branches, this, EXIT);
        return succs;
    }
    
    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        //TODO
    }

    @Override
    public void translate(CodeWriter w, Translator tr) {
        //TODO
    }
    
    @Override
    public Node visitChildren(NodeVisitor v) {
    	Expr e = visitChild(this.expr, v);
    	List<MatchBranch> brs = new ArrayList<>();
    	for (MatchBranch b: this.branches) {
    		brs.add(visitChild(b, v));
    	}
    	MatchRestriction_c mr = (MatchRestriction_c) this.copy();
    	mr.expr = e;
    	mr.branches = brs;
    	return mr;
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        //TODO 
    	return this;
    }
}
