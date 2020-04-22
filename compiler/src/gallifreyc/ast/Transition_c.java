package gallifreyc.ast;

import java.util.Collections;
import java.util.List;

import gallifreyc.extension.GallifreyExprExt;
import gallifreyc.extension.GallifreyLang;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.*;
import polyglot.types.ClassType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeChecker;

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
		return new Transition_c(this.position, e, this.restriction);
	}
	
	public RestrictionId restriction() {
		return restriction;
	}
	
    @Override
    public Term firstChild() {
        if (expr != null) return expr;
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
        Transition_c n = copyIfNeeded(this);
        n.expr = e;
        return n;
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
    	TypeSystem ts = tc.typeSystem();
    	Type t = expr.type();
        GallifreyExprExt ext = ((GallifreyLang) lang()).exprExt(this.expr);
        GallifreyNodeFactory nf = (GallifreyNodeFactory) tc.nodeFactory();
        
    	RefQualification q = ext.gallifreyType.qualification();
    	
    	if (q instanceof SharedRef) {
            throw new SemanticException("Can only transition restrictions for Shared types", this.position);
    	}
    	if (ts instanceof GallifreyTypeSystem) {
    		GallifreyTypeSystem gts = (GallifreyTypeSystem) ts;
    		String restrictionClass = gts.getClassNameForRestriction(restriction.restriction().id());
    		if (restrictionClass == null) {
    			throw new SemanticException("Unknown Restriction "+restriction.restriction().id(), this.position());
    		}
    		// requires equality between expr type and restriction's "for" type
    		if (!ts.typeEquals(t, ts.typeForName(restrictionClass))) {
    			throw new SemanticException("Invalid restriction for class "+restrictionClass, this.position());
    		}
    	}
    	return this;
    }
}
