package gallifreyc.ast;

import java.util.Collections;
import java.util.List;

import gallifreyc.types.GallifreyTypeSystem;
import gallifreyc.types.RefQualifiedType;
import gallifreyc.visit.GallifreyTypeChecker;

import java.util.ArrayList;

import polyglot.ast.Case;
import polyglot.ast.Expr;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt_c;
import polyglot.ast.SwitchElement;
import polyglot.ast.Term;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
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
    	TypeSystem ts = tc.typeSystem();
    	if (ts instanceof GallifreyTypeSystem) {
    		GallifreyTypeSystem gts = (GallifreyTypeSystem) ts;
        	Type t = expr.type();
        	if (!(t instanceof RefQualifiedType) || !(((RefQualifiedType) t).refQualification() instanceof SharedRef)) {
                throw new SemanticException("Can only match restrictions for Shared types", this.position);
        	}
        	RefQualifiedType rt = (RefQualifiedType) t;
        	String thisRV = ((SharedRef) rt.refQualification()).restriction().restriction().id();
        	if (!gts.isUnionRestriction(thisRV)) {
        		throw new SemanticException("Can only match on union restrictions", this.position);
        	}
        	for (MatchBranch b: this.branches) {
        		LocalDecl ld = b.pattern();
            	Type ldt = ld.declType();
            	if (!(ldt instanceof RefQualifiedType) || !(((RefQualifiedType) ldt).refQualification() instanceof SharedRef)) {
                    throw new SemanticException("Pattern in match branch must be shared type", b.position());
            	}
            	RefQualifiedType ldrt = (RefQualifiedType) ldt;
            	RestrictionId rid = ((SharedRef) rt.refQualification()).restriction();
            	if (!rid.isRvQualified()) {
            		throw new SemanticException("Match branch restriction must be qualified", b.position());
            	}
            	if (!rid.wildcardRv() && rid.rv().id() != thisRV) {
            		throw new SemanticException("Match branch restriction qualification does not match current restriction", b.position());
            	}
            	String variant = rid.restriction().id();
            	if (!gts.getVariantRestrictions(thisRV).contains(variant)) {
            		throw new SemanticException("Variant is not part of matched union restriction", b.position());
            	}
        	}
    	}
    	return this;
    }
    
}
