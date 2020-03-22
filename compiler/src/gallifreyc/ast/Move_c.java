package gallifreyc.ast;

import java.util.List;

import polyglot.ast.*;
import polyglot.types.*;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeChecker;
import gallifreyc.types.*;

public class Move_c extends Expr_c implements Move {
	private static final long serialVersionUID = SerialVersionUID.generate();
	Expr expr;

	public Move_c(Position pos, Expr expr) {
		super(pos);
		this.expr = expr;
	}
	
	public Expr expr() {
		return expr;
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
    	return "move(" + expr.toString() + ")";
    }
    
    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        w.write(this.toString());
    }
    
    @Override
    public Node visitChildren(NodeVisitor v) {
        Expr expr = visitChild(this.expr, v);
        Move_c n = copyIfNeeded(this);
        n.expr = expr;
        return n;
    }
    
    @Override
    public Node copy(NodeFactory nf) {
    	return ((GallifreyNodeFactory) nf).Move(this.position, (Expr) this.expr.copy(nf));
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        Type t = this.expr.type();
        GallifreyNodeFactory nf = (GallifreyNodeFactory) tc.nodeFactory();
        assert t != null;
        if (t instanceof RefQualifiedType) {
        	RefQualifiedType rt = (RefQualifiedType) t;
        	RefQualification q = rt.refQualification();
        	if (q instanceof UniqueRef) {
        		q = nf.MoveRef(q.position());
                RefQualifiedType new_type = (RefQualifiedType) t.copy();
                new_type.refQualification(q);
                return type(new_type);
        	}
        }
        throw new SemanticException("cannot move non-unique!");
    }
}
