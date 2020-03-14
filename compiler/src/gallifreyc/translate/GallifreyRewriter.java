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
import gallifreyc.types.*;
import java.util.*;

public class GallifreyRewriter extends ExtensionRewriter {

    // when Field appears on RHS, this makes it safe to null out
    public Block rewriteField(String fresh, Field f, ExtensionRewriter rw) {
    	// only rewrite if enclosing object is an expression (not a type)
        NodeFactory nf = rw.nodeFactory();
    	Receiver r = f.target();
    	if (r instanceof Expr) {
        	Expr o = (Expr) r;
        	Type oType = o.type();
    		Stmt stmt1 = rw.qq().parseStmt("%T %s = %E;", oType, fresh, o);
    		return nf.Block(f.position(), stmt1);
    	}
		return nf.Block(f.position(), new ArrayList<Stmt>());
    }
    
 // when ArrayAccess appears on RHS, this makes it safe to null out
    public Block rewriteArrayAccess(String fresh1, String fresh2, ArrayAccess a, ExtensionRewriter rw) {
        NodeFactory nf = rw.nodeFactory();
    	Expr array = a.array();
    	Expr index = a.index();
    	Type aType = array.type();
    	Type iType = index.type();
		Stmt stmt1 = rw.qq().parseStmt("%T %s = %E;", aType, fresh1, array);
		Stmt stmt2 = rw.qq().parseStmt("%T %s = %E;", iType, fresh2, index);
		return nf.Block(a.position(), stmt1, stmt2);
    }

	
	public GallifreyRewriter(Job job, ExtensionInfo from_ext, ExtensionInfo to_ext) {
		super(job, from_ext, to_ext);
		// TODO Auto-generated constructor stub
	}
	
	// remove unique/shared annotations
	@Override 
	public TypeNode typeToJava(Type t, Position pos) {
		if (t instanceof RefQualifiedType) {
			return nf.CanonicalTypeNode(pos, ((RefQualifiedType) t).base());
		}
		return super.typeToJava(t, pos);
	}
	
	public ClassDecl makeUniqueDecl(SourceFile sf) {
		NodeFactory nf = nodeFactory();
		Position p = sf.position();
		TypeNode t = nf.TypeNodeFromQualifiedName(p, "T");
		
    	List<ClassMember> uniqueMembers = new ArrayList<>(); 
    	FieldDecl f1 = nf.FieldDecl(p, Flags.PUBLIC, t, "value");
    	FieldDecl f2 = nf.FieldDecl(p, Flags.PUBLIC, t, "temp");
    	
    	List<Formal> constructorFormals = new ArrayList<>();
    	constructorFormals.add(nf.Formal(p, Flags.NONE, (TypeNode) t.copy(), "v"));
    	
    	List<Stmt> constructorStmts = new ArrayList<>();
    	constructorStmts.add(nf.Eval(p, nf.FieldAssign(p, nf.Field(p, nf.This(p), nf.Id(p, "value")), 
    			Assign.ASSIGN, nf.AmbExpr(p, "v"))));
    	
    	ConstructorDecl c = nf.ConstructorDecl(p, Flags.PUBLIC, nf.Id(p, "Unique"), 
    			constructorFormals,
    			new ArrayList<TypeNode>(),
    			nf.Block(p, constructorStmts));
    	
    	uniqueMembers.add(f1);
    	uniqueMembers.add(f2);
    	uniqueMembers.add(c);
    	
    	ClassBody uniqueBody = nf.ClassBody(p, uniqueMembers);
    	ClassDecl uniqueDecl = nf.ClassDecl(p, Flags.NONE, nf.Id(p, "Unique<T>"), null, new ArrayList<TypeNode>(), uniqueBody);
    	return uniqueDecl;
	}
	
	public ClassDecl makeSharedDecl(SourceFile sf) {
		NodeFactory nf = nodeFactory();
		Position p = sf.position();
		TypeNode t = nf.TypeNodeFromQualifiedName(p, "T");
		TypeNode str = nf.TypeNodeFromQualifiedName(p, "String");
		
    	List<ClassMember> sharedMembers = new ArrayList<>(); 
    	FieldDecl f1 = nf.FieldDecl(p, Flags.PUBLIC, t, "value");
    	FieldDecl f2 = nf.FieldDecl(p, Flags.PUBLIC, str, "restriction");
    	
    	List<Formal> constructorFormals = new ArrayList<>();
    	constructorFormals.add(nf.Formal(p, Flags.NONE, (TypeNode) t.copy(), "v"));
    	constructorFormals.add(nf.Formal(p, Flags.NONE, (TypeNode) str.copy(), "r"));
    	
    	List<Stmt> constructorStmts = new ArrayList<>();
    	constructorStmts.add(nf.Eval(p, nf.FieldAssign(p, nf.Field(p, nf.This(p), nf.Id(p, "value")), 
    			Assign.ASSIGN, nf.AmbExpr(p, "v"))));
    	constructorStmts.add(nf.Eval(p, nf.FieldAssign(p, nf.Field(p, nf.This(p), nf.Id(p, "restriction")), 
    			Assign.ASSIGN, nf.AmbExpr(p, "r"))));
    	
    	ConstructorDecl c = nf.ConstructorDecl(p, Flags.PUBLIC, nf.Id(p, "Shared"), 
    			constructorFormals,
    			new ArrayList<TypeNode>(),
    			nf.Block(p, constructorStmts));
    	
    	sharedMembers.add(f1);
    	sharedMembers.add(f2);
    	sharedMembers.add(c);
    	
    	ClassBody sharedBody = nf.ClassBody(p, sharedMembers);
    	ClassDecl sharedDecl = nf.ClassDecl(p, Flags.NONE, nf.Id(p, "Shared<T>"), null, new ArrayList<TypeNode>(), sharedBody);
    	return sharedDecl;
	}
	
	public Node rewrite(Node n) throws SemanticException {
        NodeFactory nf = nodeFactory();
        
        // unwrap Moves
        if (n instanceof Move) {
        	// move(a) ---> ((a.temp = a.value) == (a.value = null)) ? a.temp : a.temp
        	Move m = (Move) n;
        	Position p = n.position();
        	Expr e = m.expr();
        	
        	//HACK: re-wrap unique exprs inside of Moves
        	if (e instanceof Field) {
        		Field f = (Field) e;
        		if (f.name().toString().equals("value")) {
        			e = (Expr) f.target();
        		}
        	}
        	
        	Field tempField = nf.Field(p, e, nf.Id(p, "temp"));
        	Field valueField = nf.Field(p, e, nf.Id(p, "value"));
        	Expr cond = nf.Binary(p, 
    			nf.FieldAssign(p, (Field) tempField.copy(), Assign.ASSIGN, (Expr) valueField.copy()), 
    			Binary.EQ, 
    			nf.FieldAssign(p, (Field) valueField.copy(), Assign.ASSIGN, nf.NullLit(p))
        	);
        	return nf.Conditional(p, cond, (Expr) tempField.copy(), (Expr) tempField.copy());
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
        
        if (n instanceof Assign) {
        	Assign a = (Assign) n.copy();
        	//TODO 
        	return a;
        }
        
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
        			l = l.init(new_rhs);
        			return l;
        		}
        		if (rt.refQualification() instanceof UniqueRef) {
        			Expr new_rhs = qq().parseExpr("new Unique(%E)", rhs);
        			l = l.init(new_rhs);
        			return l;
        		}
        	}
        	//TODO rewrite LHS of decls
        	return l;
        }
        
        if (n instanceof Expr) {
        	Expr e = (Expr) n.copy();
        	Type t = e.type();
        	if (t instanceof RefQualifiedType) {
        		RefQualifiedType rt = (RefQualifiedType) t;
        		if (rt.refQualification() instanceof SharedRef || rt.refQualification() instanceof UniqueRef) {
        			Expr new_e = qq().parseExpr("(%E).value", e);
        			return new_e;
        		}
        	}
        	return e;
        }
        
        
        return n.extRewrite(this);
	}
	
	public NodeVisitor rewriteEnter(Node n) {
		if (n instanceof ClassDecl) {
			//TODO move field inits to constructor
		}
		if (n instanceof For) {
			//TODO hoist var decls outside
		}
		return this;
	}
//  if (n instanceof ArrayAccessAssign) {
//	ArrayAccessAssign an = (ArrayAccessAssign) n;
//	ArrayAccess left = an.left();
//	String fresh1 = lang().freshVar();
//	String fresh2 = lang().freshVar();
//	Stmt rewrittenAccessStmt = rewriteArrayAccess(fresh1, fresh2, left, rw);
//	left = (ArrayAccess) rw.qq().parseExpr("%s[%s]", fresh1, fresh2);
//	
//	Expr right = an.right();
//	Type lType = left.type();
//	Type rType = right.type();
//	
//	TypeNode lTypetn = crw.typeToJava(lType, lType.position());
//	TypeNode rTypetn = crw.typeToJava(rType, rType.position());
//	
//	if (rType instanceof RefQualifiedType) {
//		RefQualifiedType refRType = (RefQualifiedType) rType;
//		if (refRType.refQualification() instanceof UniqueRef) {
//			String fresh = lang().freshVar();
//			Stmt stmt1 = rw.qq().parseStmt("%T %s = %E;", rTypetn, fresh, right);
//			Stmt stmt2 = rw.qq().parseStmt("%E = %s;", left, fresh);
//			Stmt stmt3;
//			return nf.Block(node.position(), stmt1, stmt2, stmt3);
//		}
//	}
//else if (n instanceof Assign) {
//	Assign an = (Assign) n;
//	Expr left = an.left();
//	Expr right = an.right();
//	Type lType = left.type();
//	Type rType = right.type();
//	
//	TypeNode lTypetn = rw.typeToJava(lType, lType.position());
//	TypeNode rTypetn = rw.typeToJava(rType, rType.position());
//	
//	if (rType instanceof RefQualifiedType) {
//		RefQualifiedType refRType = (RefQualifiedType) rType;
//		/**
//		 * Nulling out references:
//		 * let local/shared x and unique y
//		 * From: x = y;
//		 * To: temp = y; x = temp; y = null;
//		 */
//		if (refRType.refQualification() instanceof UniqueRef) {
//			String fresh = lang().freshVar();
//			Stmt stmt1 = rw.qq().parseStmt("%T %s = %E;", rTypetn, fresh, right);
//			Stmt stmt2 = rw.qq().parseStmt("%E = %s;", left, fresh);
//			Stmt stmt3;
//			if (right instanceof ArrayAccess || right instanceof Variable || right instanceof Field) {
//				stmt3 = rw.qq().parseStmt("%E = %E", right, nf.NullLit(right.position()));
//			}
//			return nf.Block(node.position(), stmt1, stmt2, stmt3);
//		}
//	}
//} else if (n instanceof LocalDecl) {
//	LocalDecl ldn = (LocalDecl) n;
//	Id lName = ldn.id();
//	
//	Expr right = ldn.init();
//	Type rType = right.type();
//	
//	TypeNode lTypetn = ldn.type();
//	TypeNode rTypetn = rw.typeToJava(rType, rType.position());
//	
//	if (rType instanceof RefQualifiedType) {
//		RefQualifiedType refRType = (RefQualifiedType) rType;
//		/**
//		 * Nulling out references:
//		 * let local/shared x and unique y
//		 * From: x = y;
//		 * To: temp = y; x = temp; y = null;
//		 */
//		if (refRType.refQualification() instanceof UniqueRef) {
//			String fresh = lang().freshVar();
//			Stmt stmt1 = rw.qq().parseStmt("%T %s = %E;", rTypetn, fresh, right);
//			Stmt stmt2 = rw.qq().parseStmt("%T %s = %s;", , fresh);
//			Stmt stmt3 = rw.qq().parseStmt("%E = %E", right, nf.NullLit(right.position()));
//			return nf.Block(node.position(), stmt1, stmt2, stmt3);
//		}
//	}
//}
}
