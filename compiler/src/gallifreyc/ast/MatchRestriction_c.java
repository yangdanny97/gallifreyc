package gallifreyc.ast;

import java.util.List;

import gallifreyc.extension.GallifreyExprExt;
import gallifreyc.extension.GallifreyLang_c;
import gallifreyc.types.GallifreyTypeSystem;
import java.util.ArrayList;

import polyglot.ast.Expr;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.Stmt_c;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.CFGBuilder;
import polyglot.visit.FlowGraph;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
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
	public MatchRestriction expr(Expr e) {
		return new MatchRestriction_c(this.position(), e, this.branches());
	}

	@Override
	public List<MatchBranch> branches() {
		return branches;
	}
	
	@Override
	public MatchRestriction branches(List<MatchBranch> b) {
		return new MatchRestriction_c(this.position(), this.expr(), b);
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
            GallifreyExprExt ext = GallifreyExprExt.ext(this.expr);
        	RefQualification q = ext.gallifreyType.qualification();

        	if (q instanceof SharedRef) {
                throw new SemanticException("Can only match restrictions for Shared types", this.position);
        	}
        	String thisRV = ((SharedRef) q).restriction().restriction().id();
        	if (!gts.isUnionRestriction(thisRV)) {
        		throw new SemanticException("Can only match on union restrictions", this.position);
        	}
        	for (MatchBranch b: this.branches) {
        		LocalDecl ld = b.pattern();
            	TypeNode ldt = ld.type();
            	if (!(ldt instanceof RefQualifiedTypeNode) || 
            			!(((RefQualifiedTypeNode) ldt).qualification() instanceof SharedRef)) {
                    throw new SemanticException("Pattern in match branch must be shared type", b.position());
            	}
            	RefQualifiedTypeNode ldrt = (RefQualifiedTypeNode) ldt;
            	RestrictionId rid = ((SharedRef) ldrt.qualification()).restriction();
            	if (!rid.isRvQualified()) {
            		throw new SemanticException("Match branch restriction must be qualified", b.position());
            	}
            	if (!rid.wildcardRv() && rid.rv().id() != thisRV) {
            		throw new SemanticException(
            				"Match branch restriction qualification does not match current restriction", b.position());
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
