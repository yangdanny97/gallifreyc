package gallifreyc.translate;

import polyglot.ast.*;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.translate.ExtensionRewriter;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import gallifreyc.ast.*;
import gallifreyc.types.*;
import java.util.*;

public class GallifreyRewriter extends ExtensionRewriter {
	
	private static String uniqueDecl = "class Unique<T> {\n" + 
			"	public T value;\n" + 
			"	public Unique(T value) {\n" + 
			"		this.value = value;\n" + 
			"	}\n" + 
			"}\n";
	
	private static String sharedDecl = "class Shared<T> {\n" + 
			"	public T value;\n" + 
			"	public String restriction;\n" + 
			"	public Shared(T value, String restriction) {\n" + 
			"		this.value = value;\n" + 
			"		this.restriction = restriction;\n" + 
			"	}\n" + 
			"}\n";
	

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
	
	public Node rewrite(Node n) {
        NodeFactory nf = nodeFactory();
        
        // unwrap Moves
        if (n instanceof Move) {
        	Move m = (Move) n;
        	Expr e = m.expr();
        	//TODO maybe unbox here?
        	return e;
        }
        
        // add Unique and Shared decls
        if (n instanceof SourceFile) {
        	SourceFile sf = (SourceFile) n.copy();
//        	ClassDecl uniqueDecl = qq().parseDecl(this.uniqueDecl, new ArrayList<>());
//        	ClassDecl sharedDecl = qq().parseDecl(this.sharedDecl, new ArrayList<>());
//        	List<TopLevelDecl> decls = sf.decls();
//        	decls.add(0, uniqueDecl);
//        	decls.add(0, sharedDecl);
        	return sf;
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
        
        
        return n;
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
