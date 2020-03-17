package gallifreyc.translate;

import polyglot.ast.*;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.translate.ExtensionRewriter;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import gallifreyc.ast.*;
import gallifreyc.extension.GallifreyLang;
import gallifreyc.types.*;
import java.util.*;

public class GallifreyRewriter extends ExtensionRewriter implements GRewriter {
	List<Stmt> hoisted;
	final String VALUE = "VALUE";
	final String RES = "RESTRICTION";
	final String TEMP = "TEMP";
    
    @Override
    public GallifreyLang lang() {
    	return (GallifreyLang) super.lang();
    }

	
	public GallifreyRewriter(Job job, ExtensionInfo from_ext, ExtensionInfo to_ext) {
		super(job, from_ext, to_ext);
		hoisted = new ArrayList<>();
	}
	
	// remove unique/shared annotations
	@Override 
	public TypeNode typeToJava(Type t, Position pos) {
		if (t instanceof RefQualifiedType) {
			return nf.CanonicalTypeNode(pos, ((RefQualifiedType) t).base());
		}
		return super.typeToJava(t, pos);
	}
	
	private ClassDecl makeUniqueDecl(SourceFile sf) {
		NodeFactory nf = nodeFactory();
		Position p = sf.position();
		TypeNode t = nf.TypeNodeFromQualifiedName(p, "T");
		
    	List<ClassMember> uniqueMembers = new ArrayList<>(); 
    	// public T value;
    	FieldDecl f1 = nf.FieldDecl(p, Flags.PUBLIC, t, nf.Id(p, VALUE));
    	// public T TEMP;
    	FieldDecl f2 = nf.FieldDecl(p, Flags.PUBLIC, t, nf.Id(p, TEMP));
    	
    	List<Formal> constructorFormals = new ArrayList<>();
    	// public Unique(T v)
    	constructorFormals.add(nf.Formal(p, Flags.NONE, (TypeNode) t.copy(),  nf.Id(p, VALUE)));
    	
    	List<Stmt> constructorStmts = new ArrayList<>();
    	// this.value = v;
    	constructorStmts.add(nf.Eval(p, nf.FieldAssign(p, nf.Field(p, nf.This(p), nf.Id(p, VALUE)), 
    			Assign.ASSIGN, nf.AmbExpr(p, nf.Id(p, VALUE)))));
    	
    	ConstructorDecl c = nf.ConstructorDecl(p, Flags.PUBLIC, nf.Id(p, "Unique"), 
    			constructorFormals,
    			new ArrayList<TypeNode>(),
    			nf.Block(p, constructorStmts),
    			nf.Javadoc(p, ""));
    	
    	uniqueMembers.add(f1);
    	uniqueMembers.add(f2);
    	uniqueMembers.add(c);
    	
    	ClassBody uniqueBody = nf.ClassBody(p, uniqueMembers);
    	ClassDecl uniqueDecl = nf.ClassDecl(p, Flags.NONE, 
    			nf.Id(p, "Unique<T>"), null, new ArrayList<TypeNode>(), 
    			uniqueBody, nf.Javadoc(p, ""));
    	return uniqueDecl;
	}
	
	private ClassDecl makeSharedDecl(SourceFile sf) {
		NodeFactory nf = nodeFactory();
		Position p = sf.position();
		TypeNode t = nf.TypeNodeFromQualifiedName(p, "T");
		TypeNode str = nf.TypeNodeFromQualifiedName(p, "String");
		
    	List<ClassMember> sharedMembers = new ArrayList<>(); 
    	// public T value
    	FieldDecl f1 = nf.FieldDecl(p, Flags.PUBLIC, t,  nf.Id(p, VALUE));
    	// public String restriction
    	FieldDecl f2 = nf.FieldDecl(p, Flags.PUBLIC, str,  nf.Id(p, RES));
    	
    	List<Formal> constructorFormals = new ArrayList<>();
    	// public Shared(T v, String r)
    	constructorFormals.add(nf.Formal(p, Flags.NONE, (TypeNode) t.copy(),  nf.Id(p, VALUE)));
    	constructorFormals.add(nf.Formal(p, Flags.NONE, (TypeNode) str.copy(),  nf.Id(p, RES)));
    	
    	List<Stmt> constructorStmts = new ArrayList<>();
    	// this.value = v;
    	constructorStmts.add(nf.Eval(p, nf.FieldAssign(p, nf.Field(p, nf.This(p), nf.Id(p, VALUE)), 
    			Assign.ASSIGN, nf.AmbExpr(p, nf.Id(p, VALUE)))));
    	// this.restriction = r;
    	constructorStmts.add(nf.Eval(p, nf.FieldAssign(p, nf.Field(p, nf.This(p), nf.Id(p, RES)), 
    			Assign.ASSIGN, nf.AmbExpr(p, nf.Id(p, RES)))));
    	
    	ConstructorDecl c = nf.ConstructorDecl(p, Flags.PUBLIC, nf.Id(p, "Shared"), 
    			constructorFormals,
    			new ArrayList<TypeNode>(),
    			nf.Block(p, constructorStmts),
    			nf.Javadoc(p, ""));
    	
    	sharedMembers.add(f1);
    	sharedMembers.add(f2);
    	sharedMembers.add(c);
    	
    	ClassBody sharedBody = nf.ClassBody(p, sharedMembers);
    	ClassDecl sharedDecl = nf.ClassDecl(p, Flags.NONE, 
    			nf.Id(p, "Shared<T>"), null, new ArrayList<TypeNode>(), 
    			sharedBody, nf.Javadoc(p, ""));
    	return sharedDecl;
	}
	
	// hoist an expression e and replace it with a fresh temp
	private Expr hoist(Expr e) {
		// variables and literals are safe
		if (e instanceof NamedVariable || e instanceof Lit || e instanceof Special) return e;
		// things we unwrapped in this pass are safe
		if (e instanceof Field) {
			Field f = (Field) e;
			if (f.name() == VALUE) {
				return e;
			}
		}
		// hoist everything else
		NodeFactory nf = nodeFactory();
		Position p = e.position();
		String fresh = lang().freshVar();
		LocalDecl l = nf.LocalDecl(p, Flags.NONE, nf.CanonicalTypeNode(p, e.type()), nf.Id(p, fresh), e);
		hoisted.add(l);
		return nf.Local(p, nf.Id(p, fresh));
	}
	
	// wrap unique/shared refs with .value, AFTER rewriting
	private Node wrapExpr(Expr n) {
    	Expr e = (Expr) n.copy();
    	Type t = e.type();
    	if (t instanceof RefQualifiedType) {
    		RefQualifiedType rt = (RefQualifiedType) t;
    		if (rt.refQualification() instanceof SharedRef || rt.refQualification() instanceof UniqueRef) {
    			Expr new_e = qq().parseExpr("(%E)." + VALUE, e);
    			return new_e;
    		}
    	}
    	return e;
	}
	
	private Node rewriteExpr(Node n) throws SemanticException {
        NodeFactory nf = nodeFactory();
        
        // unwrap Moves
        if (n instanceof Move) {
        	// move(a) ---> ((a.TEMP = a.value) == (a.value = null)) ? a.TEMP : a.TEMP
        	Move m = (Move) n;
        	Position p = n.position();
        	Expr e = m.expr();
        	
        	//HACK: re-wrap unique exprs inside of Moves
        	if (e instanceof Field) {
        		Field f = (Field) e;
        		if (f.name().toString().equals(VALUE)) {
        			e = (Expr) f.target();
        		}
        	}
        	
        	Field tempField = nf.Field(p, e, nf.Id(p, TEMP));
        	Field valueField = nf.Field(p, e, nf.Id(p, VALUE));
        	Expr cond = nf.Binary(p, 
    			nf.FieldAssign(p, (Field) tempField.copy(), Assign.ASSIGN, (Expr) valueField.copy()), 
    			Binary.EQ, 
    			nf.FieldAssign(p, (Field) valueField.copy(), Assign.ASSIGN, nf.NullLit(p))
        	);
        	return nf.Conditional(p, cond, (Expr) tempField.copy(), (Expr) tempField.copy());
        }
        
        // a-normalizing
        if (n instanceof ArrayAccess) { 
        	ArrayAccess a = (ArrayAccess) n.copy();
        	a = a.array(hoist(a.array()));
        	a = a.index(hoist(a.index()));
        	return a;
        }
        if (n instanceof Field) { 
        	Field f = (Field) n.copy();
        	if (f.target() instanceof Expr) {
        		f = f.target(hoist((Expr) f.target()));
        		return f;
        	}
        	return n.extRewrite(this);
        }
        if (n instanceof ProcedureCall) {
        	ProcedureCall c = (ProcedureCall) n.copy();
        	List<Expr> args = new ArrayList<>(c.arguments());
        	List<Expr> hoistedArgs = new ArrayList<>();
        	for (Expr arg : args) {
        		hoistedArgs.add(hoist(arg));
        	}
        	c = c.arguments(hoistedArgs);
        	return c;
        }
        
        return n.extRewrite(this);
	}
	
	// replace statements with blocks of hoisted decls, if any
	private Node hoistStmt(Stmt s) {
		if (hoisted.size() > 0) {
	    	hoisted.add(s);
	    	List<Stmt> blockBody = hoisted;
	    	hoisted = new ArrayList<>();
	    	return nf.Block(s.position(), blockBody);
		}
		return s;
	}
	
	private Node rewriteStmt(Node n) throws SemanticException {
        if (n instanceof LocalDecl) {
        	//rewrite RHS of decls
        	LocalDecl l = (LocalDecl) n.copy();
        	Expr rhs = l.init();
        	if (l.type().type() instanceof RefQualifiedType) {
        		RefQualifiedType rt = (RefQualifiedType) l.type().type();
        		if (rt.refQualification() instanceof SharedRef) {
        			SharedRef s = (SharedRef) rt.refQualification();
        			RestrictionId rid = s.restriction();
        			Expr restriction = nf.StringLit(n.position(), rid.toString());
        			Expr new_rhs = qq().parseExpr("new Shared(%E, %E)", rhs, restriction);
        			l = l.type(qq().parseType("Shared<%T>", rt.base()));
        			l = l.init(new_rhs);
        			return l;
        		}
        		if (rt.refQualification() instanceof UniqueRef) {
        			Expr new_rhs = qq().parseExpr("new Unique(%E)", rhs);
        			l = l.type(qq().parseType("Unique<%T>", rt.base()));
        			l = l.init(new_rhs);
        			return l;
        		}
        	}
        	return l;
        }
        return n.extRewrite(this);
	}
	
	public Node rewrite(Node n) throws SemanticException {
		if (n instanceof Expr) {
			Expr e = (Expr) rewriteExpr(n);
			return wrapExpr(e);
		} 
		
		if (n instanceof Stmt && ! (n instanceof Block)) {
			Stmt s = (Stmt) rewriteStmt(n);
			return hoistStmt(s);
		}
        
        // add Unique and Shared decls
        if (n instanceof SourceFile) {
        	SourceFile sf = (SourceFile) n.copy();
        	ClassDecl uniqueDecl = makeUniqueDecl(sf);
        	ClassDecl sharedDecl = makeSharedDecl(sf);
        	List<TopLevelDecl> decls = new ArrayList<>(sf.decls());
        	decls.add(0, uniqueDecl);
        	decls.add(0, sharedDecl);
        	return sf.decls(decls);
        }
        
        return n.extRewrite(this);
	}
	
	public NodeVisitor rewriteEnter(Node n) throws SemanticException {
		if (n instanceof For) {
			//TODO hoist var decls outside
		}
		return n.extRewriteEnter(this);
	}
}