package gallifreyc.ast;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import polyglot.ast.Case;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt_c;
import polyglot.ast.SwitchElement;
import polyglot.ast.Term;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.CFGBuilder;
import polyglot.visit.FlowGraph;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeChecker;

public class MatchRestriction_c extends Stmt_c implements MatchRestriction {
	private static final long serialVersionUID = SerialVersionUID.generate();
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
        w.write("match_restriction (");
        printBlock(expr, w, tr);
        w.write(") {");
        w.unifiedBreak(4);
        w.begin(0);

        for (MatchBranch b : branches) {
            w.unifiedBreak(4);
            print(b, w, tr);
        }

        w.end();
        w.unifiedBreak(0);
        w.write("}");
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
