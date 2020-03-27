package gallifreyc.translate;

import java.util.ArrayList;
import java.util.List;

import gallifreyc.ast.MatchRestriction;
import gallifreyc.ast.Transition;
import gallifreyc.extension.GallifreyLang;
import polyglot.ast.ArrayAccess;
import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.NamedVariable;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.ProcedureCall;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.translate.ExtensionRewriter;
import polyglot.types.Flags;
import polyglot.types.LocalInstance_c;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;

public class ANormalizer extends ExtensionRewriter implements GRewriter {
	List<Stmt> hoisted;
	
	public ANormalizer(Job job, ExtensionInfo from_ext, ExtensionInfo to_ext) {
		super(job, from_ext, to_ext);
		hoisted = new ArrayList<>();
	}
	
    @Override
    public GallifreyLang lang() {
    	return (GallifreyLang) super.lang();
    }

	
	// hoist an expression e and replace it with a fresh temp
	private Expr hoist(Expr e) {
		// variables and literals are safe
		if (e instanceof NamedVariable || e instanceof Lit || e instanceof Special) return e;
		// hoist everything else
		NodeFactory nf = nodeFactory();
		Position p = e.position();
		String fresh = lang().freshVar();
		
		LocalDecl l = nf.LocalDecl(p, Flags.NONE, nf.CanonicalTypeNode(p, e.type()), nf.Id(p, fresh), e);
		l = l.localInstance(new LocalInstance_c(typeSystem(), e.position(), Flags.NONE, e.type(), fresh));
		hoisted.add(l);
		
		Local newLocal = nf.Local(p, nf.Id(p, fresh));
		newLocal = newLocal.localInstance(new LocalInstance_c(typeSystem(), e.position(), Flags.NONE, e.type(), fresh));
		
		return newLocal;
	}
	
	// replace statements with blocks of hoisted decls, if any
	private Stmt addHoistedDecls(Stmt s) {
		if (hoisted.size() > 0) {
	    	hoisted.add(s);
	    	List<Stmt> blockBody = hoisted;
	    	hoisted = new ArrayList<>();
	    	return nf.Block(s.position(), blockBody);
		}
		return s;
	}

	@Override
	public Node rewrite(Node n) throws SemanticException {
        if (n instanceof ArrayAccess) { 
        	ArrayAccess a = (ArrayAccess) n;
        	a = a.array(hoist(a.array()));
        	a = a.index(hoist(a.index()));
        	return a;
        }
        if (n instanceof Field) { 
        	Field f = (Field) n;
        	if (f.target() instanceof Expr) {
        		f = f.target(hoist((Expr) f.target()));
        		return f;
        	}
        	return n.extRewrite(this);
        }
        if (n instanceof ProcedureCall) {
        	ProcedureCall c = (ProcedureCall) n;
        	List<Expr> args = new ArrayList<>(c.arguments());
        	List<Expr> hoistedArgs = new ArrayList<>();
        	for (Expr arg : args) {
        		hoistedArgs.add(hoist(arg));
        	}
        	c = c.arguments(hoistedArgs);
        	return c;
        }
        if (n instanceof MatchRestriction) {
        	MatchRestriction m = (MatchRestriction) n;
        	m = m.expr(hoist(m.expr()));
        	Stmt s = addHoistedDecls(m);
        	return s;
        }
        
        if (n instanceof Transition) {
        	Transition t = (Transition) n;
        	t = t.expr(hoist(t.expr()));
        	Stmt s = addHoistedDecls(t);
        	return s;
        }
        
		if (n instanceof Stmt && ! (n instanceof Block)) {
			Stmt s = addHoistedDecls((Stmt) n);
			return s;
		}
		return n;
	}

	@Override
	public NodeVisitor rewriteEnter(Node n) throws SemanticException {
		return this;
	}
}
