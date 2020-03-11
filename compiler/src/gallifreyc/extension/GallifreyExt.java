package gallifreyc.extension;

import gallifreyc.visit.SharedTypeWrapper;
import polyglot.ast.*;
import polyglot.util.Copy;
import polyglot.util.InternalCompilerError;
import polyglot.util.SerialVersionUID;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.translate.ExtensionRewriter;
import gallifreyc.translate.AssignmentRewriter;
import gallifreyc.types.*;
import gallifreyc.ast.UniqueRef;
import gallifreyc.ast.*;
import java.util.*;

public class GallifreyExt extends Ext_c implements GallifreyOps {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public static GallifreyExt ext(Node n) {
        Ext e = n.ext();
        while (e != null && !(e instanceof GallifreyExt)) {
            e = e.ext();
        }
        if (e == null) {
            throw new InternalCompilerError("No Gallifrey extension object for node "
                    + n + " (" + n.getClass() + ")", n.position());
        }
        return (GallifreyExt) e;
    }

    @Override
    public final GallifreyLang lang() {
        return GallifreyLang_c.instance;
    }

    @Override
    public SharedTypeWrapper wrapSharedTypeEnter(SharedTypeWrapper v) {
////        System.out.printf("Calling wrapSharedTypeEnter on this='%s' whose class is '%s'\n", node(), node().getClass().getName());
////        System.out.printf("v's hashcode = %d\n", v.hashCode());
////        System.out.printf("v.sourceFile() == null is %b\n", v.sourceFile() == null);
//        if (node() instanceof SourceFile) {
////            System.out.printf("Calling wrapSharedTypeEnter on this='%s' whose class is '%s'\n", node(), node().getClass().getName());
//            return v.sourceFile((SourceFile) node());
//        }
        return v;
    }

    @Override
    public Node wrapSharedType(SharedTypeWrapper v) {
//        System.out.printf("Calling wrapSharedType on this='%s' whose class is '%s'\n", node(), node().getClass().getName());
//        if (node() instanceof ClassDecl) {
//            System.out.printf("Calling wrapSharedType on this='%s' whose type is '%s' (class: %s)\n",
//                              node(),
//                              ((ClassDecl) node()).type(),
//                              ((ClassDecl) node()).type().getClass().getName());
//            System.out.printf("Visiting node %s with type %s");
//        }
//        System.out.printf("v's hashcode = %d\n", v.hashCode());
//        if (v.sourceFile() != null) {
//            System.out.printf("v.sourceFile().decls().size() = %d\n", v.sourceFile().decls().size());
//        } else {
//            System.out.printf("v.sourceFile() == null is %b\n", v.sourceFile() == null);
//        }
        return node();
    }
    
    
    // when Field appears on RHS, this makes it safe to null out
    public Block rewriteField(String fresh, Field f, ExtensionRewriter rw) {
    	// only rewrite if enclosing object is an expression (not a type)
        NodeFactory nf = rw.nodeFactory();
    	Receiver r = f.target();
    	if (r instanceof Expr) {
        	Expr o = (Expr) r;
        	Type oType = o.type();
    		Stmt stmt1 = rw.qq().parseStmt("%T %s = %E;", oType, fresh, o);
    		return nf.Block(node.position(), stmt1);
    	}
		return nf.Block(node.position(), new ArrayList<Stmt>());
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
		return nf.Block(node.position(), stmt1, stmt2);
    }

    
    @Override 
    public Node extRewrite(ExtensionRewriter rw) throws SemanticException {
        AssignmentRewriter crw = (AssignmentRewriter) rw;
        NodeFactory nf = rw.nodeFactory();
        Node n = node();
        if (n instanceof Move) {
        	Move m = (Move) n;
        	Expr e = m.expr();
        	return e;
        }
//        if (n instanceof ArrayAccessAssign) {
//        	ArrayAccessAssign an = (ArrayAccessAssign) n;
//        	ArrayAccess left = an.left();
//        	String fresh1 = lang().freshVar();
//        	String fresh2 = lang().freshVar();
//        	Stmt rewrittenAccessStmt = rewriteArrayAccess(fresh1, fresh2, left, rw);
//        	left = (ArrayAccess) rw.qq().parseExpr("%s[%s]", fresh1, fresh2);
//        	
//        	Expr right = an.right();
//        	Type lType = left.type();
//        	Type rType = right.type();
//        	
//        	TypeNode lTypetn = crw.typeToJava(lType, lType.position());
//        	TypeNode rTypetn = crw.typeToJava(rType, rType.position());
//        	
//        	if (rType instanceof RefQualifiedType) {
//        		RefQualifiedType refRType = (RefQualifiedType) rType;
//        		if (refRType.refQualification() instanceof UniqueRef) {
//        			String fresh = lang().freshVar();
//        			Stmt stmt1 = rw.qq().parseStmt("%T %s = %E;", rTypetn, fresh, right);
//        			Stmt stmt2 = rw.qq().parseStmt("%E = %s;", left, fresh);
//        			Stmt stmt3;
//        			return nf.Block(node.position(), stmt1, stmt2, stmt3);
//        		}
//        	}
//        else if (n instanceof Assign) {
//        	Assign an = (Assign) n;
//        	Expr left = an.left();
//        	Expr right = an.right();
//        	Type lType = left.type();
//        	Type rType = right.type();
//        	
//        	TypeNode lTypetn = rw.typeToJava(lType, lType.position());
//        	TypeNode rTypetn = rw.typeToJava(rType, rType.position());
//        	
//        	if (rType instanceof RefQualifiedType) {
//        		RefQualifiedType refRType = (RefQualifiedType) rType;
//				/**
//				 * Nulling out references:
//				 * let local/shared x and unique y
//				 * From: x = y;
//				 * To: temp = y; x = temp; y = null;
//				 */
//        		if (refRType.refQualification() instanceof UniqueRef) {
//        			String fresh = lang().freshVar();
//        			Stmt stmt1 = rw.qq().parseStmt("%T %s = %E;", rTypetn, fresh, right);
//        			Stmt stmt2 = rw.qq().parseStmt("%E = %s;", left, fresh);
//        			Stmt stmt3;
//        			if (right instanceof ArrayAccess || right instanceof Variable || right instanceof Field) {
//        				stmt3 = rw.qq().parseStmt("%E = %E", right, nf.NullLit(right.position()));
//        			}
//        			return nf.Block(node.position(), stmt1, stmt2, stmt3);
//        		}
//        	}
//        } else if (n instanceof LocalDecl) {
//        	LocalDecl ldn = (LocalDecl) n;
//        	Id lName = ldn.id();
//        	
//        	Expr right = ldn.init();
//        	Type rType = right.type();
//        	
//        	TypeNode lTypetn = ldn.type();
//        	TypeNode rTypetn = rw.typeToJava(rType, rType.position());
//        	
//        	if (rType instanceof RefQualifiedType) {
//        		RefQualifiedType refRType = (RefQualifiedType) rType;
//				/**
//				 * Nulling out references:
//				 * let local/shared x and unique y
//				 * From: x = y;
//				 * To: temp = y; x = temp; y = null;
//				 */
//        		if (refRType.refQualification() instanceof UniqueRef) {
//        			String fresh = lang().freshVar();
//        			Stmt stmt1 = rw.qq().parseStmt("%T %s = %E;", rTypetn, fresh, right);
//        			Stmt stmt2 = rw.qq().parseStmt("%T %s = %s;", , fresh);
//        			Stmt stmt3 = rw.qq().parseStmt("%E = %E", right, nf.NullLit(right.position()));
//        			return nf.Block(node.position(), stmt1, stmt2, stmt3);
//        		}
//        	}
//        }
        return super.extRewrite(rw);
    }
}
