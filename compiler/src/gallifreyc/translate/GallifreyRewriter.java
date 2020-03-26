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


// move a-normalization to earlier pass, translations for transition and match
public class GallifreyRewriter extends ExtensionRewriter implements GRewriter {
	final String VALUE = "VALUE";
	final String RES = "RESTRICTION";
	final String TEMP = "TEMP";
    
    @Override
    public GallifreyLang lang() {
    	return (GallifreyLang) super.lang();
    }

	
	public GallifreyRewriter(Job job, ExtensionInfo from_ext, ExtensionInfo to_ext) {
		super(job, from_ext, to_ext);
	}
	
	// remove unique/shared annotations
	@Override 
	public TypeNode typeToJava(Type t, Position pos) {
		if (t instanceof RefQualifiedType) {
			return nf.CanonicalTypeNode(pos, ((RefQualifiedType) t).base());
		}
		return super.typeToJava(t, pos);
	}
	
	// wrap unique/shared refs with .value, AFTER rewriting
	private Node wrapExpr(Expr e) {
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
        return n;
	}
	
	private Node rewriteStmt(Node n) throws SemanticException {
        if (n instanceof LocalDecl) {
        	//rewrite RHS of decls
        	LocalDecl l = (LocalDecl) n;
        	Expr rhs = l.init();
        	if (l.type().type() instanceof RefQualifiedType) {
        		RefQualifiedType rt = (RefQualifiedType) l.type().type();
        		if (rt.refQualification() instanceof SharedRef) {
        			SharedRef s = (SharedRef) rt.refQualification();
        			RestrictionId rid = s.restriction();
        			Expr restriction = nf.StringLit(n.position(), rid.toString());
        			Expr new_rhs = qq().parseExpr("new Shared(%E, %E)", rhs, restriction);
        			l = l.type(nf.TypeNodeFromQualifiedName(l.position(), "Shared<"+rt.base().toString()+">"));
        			l = l.init(new_rhs);
        			return l;
        		}
        		if (rt.refQualification() instanceof UniqueRef) {
        			Expr new_rhs = qq().parseExpr("new Unique(%E)", rhs);
        			l = l.type(nf.TypeNodeFromQualifiedName(l.position(), "Unique<"+rt.base().toString()+">"));
        			l = l.init(new_rhs);
        			return l;
        		}
        	}
        	return l;
        }
        return n;
	}
	
	public Node rewrite(Node n) throws SemanticException {
		if (n instanceof Expr) {
			Expr e = (Expr) rewriteExpr(n);
			return wrapExpr(e);
		} 
		
		if (n instanceof Stmt && ! (n instanceof Block)) {
			Stmt s = (Stmt) rewriteStmt(n);
			return s;
		}
        
        // add Unique and Shared decls
        if (n instanceof SourceFile) {
        	NodeFactory nf = nodeFactory();
        	SourceFile sf = (SourceFile) n;
        	Import unique = nf.Import(n.position(), Import.SINGLE_TYPE, "gallifrey.Unique");
        	Import shared = nf.Import(n.position(), Import.SINGLE_TYPE, "gallifrey.Shared");
        	List<Import> imports = new ArrayList<>(sf.imports());
        	imports.add(0, unique);
        	imports.add(0, shared);
        	return sf.imports(imports);
        }
        
        return n;
	}
	
	public NodeVisitor rewriteEnter(Node n) throws SemanticException {
		return n.extRewriteEnter(this);
	}
}